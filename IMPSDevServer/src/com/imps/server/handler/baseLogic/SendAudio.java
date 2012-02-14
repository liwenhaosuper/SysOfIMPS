package com.imps.server.handler.baseLogic;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import com.imps.server.main.IMPSTcpServer;
import com.imps.server.main.basetype.MessageProcessTask;
import com.imps.server.main.basetype.User;
import com.imps.server.main.basetype.userStatus;
import com.imps.server.manager.MessageFactory;
import com.imps.server.manager.UserManager;

public class SendAudio extends MessageProcessTask{

	//username, data
	public static HashMap<String,HashMap<AudioIdentify,LinkedList<byte[]> > > userAudioData = 
		      new HashMap<String,HashMap<AudioIdentify,LinkedList<byte[]> > >();
	
	private class AudioIdentify{
		public int sid;
		public String friName;
	}
	private String userName;
	private String friName;
	private int sid;
	private boolean isEOF;
	private byte[] packet;
	private LinkedList<byte[]> totalPackets;
	private UserManager manager;
	
	public SendAudio(Channel session, ChannelBuffer message)
	{
		super(session, message);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void parse(){
		// TODO Auto-generated method stub
		int len = inMsg.readInt();
		byte []nm = new byte[len];
		inMsg.readBytes(nm);
		try {
			manager = UserManager.getInstance();
			userName = new String(nm,"gb2312");
			len = inMsg.readInt();
			nm = new byte[len];
			inMsg.readBytes(nm);
			friName = new String(nm,"gb2312");
			sid = inMsg.readInt();
			isEOF = inMsg.readInt()==1?true:false;
			len = inMsg.readInt();
			packet = new byte[len];
			inMsg.readBytes(packet);
			User user = manager.getUser(userName);
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
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void execute() {
		if(userAudioData.containsKey(userName)){
			HashMap<AudioIdentify,LinkedList<byte[]>> items = userAudioData.get(userName);
			Iterator iter = items.entrySet().iterator();
			boolean flag = false;
			while (iter.hasNext()) { 
			    Map.Entry entry = (Map.Entry) iter.next(); 
			    AudioIdentify key = (AudioIdentify)entry.getKey();
			    if(key.sid==sid&&key.friName.equals(friName)){
			    	((LinkedList<byte[]>)entry.getValue()).add(packet);
			    	 flag = true;
			    	 if(isEOF){
			    		 totalPackets = (LinkedList<byte[]>)entry.getValue();
			    		 items.remove(key);
			    	 }
			    	 break;
			    }			   
			} 
			if(!flag){
				AudioIdentify newitem = new AudioIdentify();
				newitem.sid = sid;
				newitem.friName = friName;
				LinkedList<byte[]> nitem = new LinkedList<byte[]>();
				nitem.add(packet);
				items.put(newitem, nitem);
				if(isEOF){
					totalPackets = nitem;
					items.remove(newitem);
				}
			}
		}else{
			HashMap<AudioIdentify,LinkedList<byte[]>> item = new HashMap<AudioIdentify,LinkedList<byte[]>>();
			AudioIdentify idItem = new AudioIdentify();
			idItem.sid = sid;
			idItem.friName = friName;
			LinkedList<byte[]> nitem = new LinkedList<byte[]>();
			nitem.add(packet);
			item.put(idItem, nitem);
			userAudioData.put(userName, item);
			if(isEOF){
				totalPackets = nitem;
				userAudioData.remove(userName);
			}
		}
		if(isEOF){
			//send to friend
			System.out.println("Recv audio from " +userName+" to "+friName+" success...");
			if(manager!=null&&manager.getUserMap().containsKey(friName)){
				//向好友发送信息
				//获取好友的session
		        User fri = manager.getUser(friName);
		        Channel mysession = IMPSTcpServer.getAllGroups().find(fri.getSessionId());
		        if(mysession.isConnected()){
		        	for(int i=0;i<totalPackets.size();i++){
		        		mysession.write(ChannelBuffers.wrappedBuffer(
				        		MessageFactory.createSAudioReq(userName, 
				        				totalPackets.get(i), sid, i==(totalPackets.size()-1)?true:false).build()));
			        }
		        	System.out.println("Send audio from " +userName+" to "+friName+" success...");
		        }
		        
			}else{
				System.out.println("Send audio from " +userName+" to "+friName+" failed:offline...");
			}
		}
	}

}
