package com.imps.server.handler.baseLogic;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.sql.SQLException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import com.imps.server.main.IMPSTcpServer;
import com.imps.server.main.basetype.CommandId;
import com.imps.server.main.basetype.MessageProcessTask;
import com.imps.server.main.basetype.OutputMessage;
import com.imps.server.main.basetype.User;
import com.imps.server.main.basetype.userStatus;
import com.imps.server.manager.MessageFactory;
import com.imps.server.manager.UserManager;

public class SendPTPAudioRsp extends MessageProcessTask{
	private UserManager manager;
	private User user;
	private SocketAddress addr;
	public SendPTPAudioRsp(Channel session, ChannelBuffer message,SocketAddress addr)
	{
		super(session, message);
		this.addr = addr;
	}

	@Override
	public void parse() {
		int len = inMsg.readInt();
		byte []nm = new byte[len];
		inMsg.readBytes(nm);
		try {
			String userName = new String(nm,"gb2312");
			manager =  UserManager.getInstance();
			user =manager.getUser(userName);
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
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void execute() {
		try{
			//get the friend name
			int len = inMsg.readInt();
			byte[] fribyte = new byte[(int)len];
			inMsg.readBytes(fribyte);
			String friendname = new String(fribyte,"gb2312");
			//get the result
			int res = inMsg.readInt();
			System.out.println(" rsp result is "+res);
			
			String ip = ((InetSocketAddress)addr).getAddress().getHostAddress();
			int port = ((InetSocketAddress)addr).getPort();
			System.out.println("public ip:"+ip+" port:"+port);

			if(manager.getUserMap().containsKey(friendname))
			{
				//向好友发送信息
				//获取好友的session
		        User fri = manager.getUser(friendname);
		        Channel mysession = IMPSTcpServer.getAllGroups().find(fri.getSessionId());
		        if(!mysession.isConnected()){
		        	 System.out.println("Session not connected...");
		        }
		       // OutputMessage;
		        OutputMessage remsg = MessageFactory.createSPTPAudioRsp(user.getUsername(), ip,port,res==1?true:false);
		        mysession.write(ChannelBuffers.wrappedBuffer(remsg.build()));
		        System.out.println(" send audio rsp to "+friendname+" successfully!");
			}
			else{
				//该好友不在线
			    System.out.println(" user is now offline and could not sent audio response to him~");
				OutputMessage remsg = MessageFactory.createErrorMsg();
				remsg.getOutputStream().writeInt(CommandId.S_P2PAUDIO_ERROR);
				remsg.getOutputStream().writeInt(2);
				session.write(ChannelBuffers.wrappedBuffer(remsg.build()));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
