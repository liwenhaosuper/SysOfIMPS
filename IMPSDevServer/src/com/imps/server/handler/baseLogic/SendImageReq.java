package com.imps.server.handler.baseLogic;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import com.imps.server.main.IMPSTcpServer;
import com.imps.server.main.ServerBoot;
import com.imps.server.main.basetype.MessageProcessTask;
import com.imps.server.main.basetype.NetAddress;
import com.imps.server.main.basetype.OutputMessage;
import com.imps.server.main.basetype.User;
import com.imps.server.main.basetype.userStatus;
import com.imps.server.manager.MessageFactory;
import com.imps.server.manager.UserManager;

public class SendImageReq extends MessageProcessTask {
	private class ImageIdentify{
		public int sid;
		public String friName;
	}
	public static final int DEFAULTPACKETSIZE = 800;
	//username, data
	public static HashMap<String,HashMap<ImageIdentify,byte[] > > usersImageData = 
		      new HashMap<String,HashMap<ImageIdentify,byte[] > >();
	private String userName;
	private String friName;
	private int sid;
	private boolean isEOF;
	private byte[] packet;
	private byte[] totalPackets;
	private UserManager manager;
	
	public SendImageReq(Channel session, ChannelBuffer message)
			{
		super(session, message);
	}

	@Override
	public void parse() {
		// TODO Auto-generated method stub
		try{
		int len = inMsg.readInt();
		byte []nm = new byte[len];
		inMsg.readBytes(nm);
		userName = new String(nm,"gb2312");
		User user;
		manager = UserManager.getInstance();
		try {
			user = manager.getUser(userName);
			if(user==null)
			{
				user = manager.getUserFromDB(userName);
				user.setStatus(userStatus.ONLINE);
				user.setSessionId(session.getId());
				manager.addUser(user);
				User[] friends = user.getOnlineFriendList();
				//通知所有朋友
				if(friends==null)
					return ;
				for(int i=0;i<friends.length;i++)
				{
				     manager.updateUserStatus(user);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		len = inMsg.readInt();
		nm = new byte[len];
		inMsg.readBytes(nm);
		friName = new String(nm,"gb2312");
		sid =inMsg.readInt();
		int res = inMsg.readInt();
		if(res==0){
			isEOF = false;
		}else{
			isEOF = true;
		}
		System.out.println("username:"+userName+" friName:"+friName+" sid:"+sid+" res:"+res);
		len = inMsg.readInt();
		packet = new byte[len];
		inMsg.readBytes(packet);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		if(usersImageData.containsKey(userName)){
			HashMap<ImageIdentify,byte[]> items = usersImageData.get(userName);
			Iterator iter = items.entrySet().iterator();
			boolean flag = false;
			while (iter.hasNext()) { 
			    Map.Entry entry = (Map.Entry) iter.next(); 
			    ImageIdentify key = (ImageIdentify)entry.getKey();
			    if(key.sid==sid&&key.friName.equals(friName)){
			    	 byte[] val = (byte[])entry.getValue();
			    	 byte[] newval =new byte[packet.length+val.length];
			    	 System.arraycopy(val, 0, newval, 0, val.length);
			    	 System.arraycopy(packet, 0, newval, val.length, packet.length);
			    	 items.put(key, newval);
			    	 flag = true;
			    	 if(isEOF){
			    		 totalPackets = newval;
			    		 items.remove(key);
			    	 }
			    	 break;
			    }			   
			} 
			if(!flag){
				ImageIdentify newitem = new ImageIdentify();
				newitem.sid = sid;
				newitem.friName = friName;
				items.put(newitem, packet);
				if(isEOF){
					totalPackets = packet;
					items.remove(newitem);
				}
			}
		}else{
			HashMap<ImageIdentify,byte[]> item = new HashMap<ImageIdentify,byte[]>();
			ImageIdentify idItem = new ImageIdentify();
			idItem.sid = sid;
			idItem.friName = friName;
			item.put(idItem, packet);
			usersImageData.put(userName, item);
			if(isEOF){
				totalPackets = packet;
				item.remove(idItem);
			}
		}
		if(isEOF){
			//send to friend
			System.out.println("Recv image from " +userName+" to "+friName+" success...");
			try {
				manager = UserManager.getInstance();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(manager!=null&&manager.getUserMap().containsKey(friName))
			{
				//向好友发送信息
				//获取好友的session
		        User fri = manager.getUser(friName);
		        Channel mysession = IMPSTcpServer.getAllGroups().find(fri.getSessionId());
		        int cnt = totalPackets.length/DEFAULTPACKETSIZE+1;
		        OutputMessage outMsg = null;
		        packet = new byte[DEFAULTPACKETSIZE];
		        for(int i=0;i<cnt;i++){
					if(((i+1)*DEFAULTPACKETSIZE)>=totalPackets.length){
						System.arraycopy(totalPackets, i*DEFAULTPACKETSIZE, packet, 0,totalPackets.length-i*DEFAULTPACKETSIZE);
						outMsg = MessageFactory.createSSendImageReq(userName, sid, true);
						try {
							outMsg.getOutputStream().writeInt(totalPackets.length-i*DEFAULTPACKETSIZE);
							outMsg.getOutputStream().write(packet);
							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						mysession.write(ChannelBuffers.wrappedBuffer(outMsg.build()));
						break;						
					}else{
						System.arraycopy(totalPackets, i*DEFAULTPACKETSIZE, packet, 0, DEFAULTPACKETSIZE);
						outMsg = MessageFactory.createSSendImageReq(userName, sid, false);
						try {
							outMsg.getOutputStream().writeInt(DEFAULTPACKETSIZE);
							outMsg.getOutputStream().write(packet);
							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						mysession.write(ChannelBuffers.wrappedBuffer(outMsg.build()));
					}
		        }
		        System.out.println("send image from " +userName+" to "+friName+" success...image size is:"+totalPackets.length);
			}else{
				System.out.println("friend "+friName+" is offline and could not send image to hime");
			}
			
		}
	}

}
