package com.imps.server.handler.baseLogic;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

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


public class AddFriendReq extends MessageProcessTask{

	private 		User user=null;
	private			UserManager manager=null;
	public AddFriendReq(Channel session, ChannelBuffer message)
		 {
		super(session, message);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void parse() {
		// TODO Auto-generated method stub
		int len = inMsg.readInt();
		byte nm[] = new byte[len];
		inMsg.readBytes(nm);
		String userName="";
		try {
			userName = new String(nm,"gb2312");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			manager = UserManager.getInstance();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			user = UserManager.getInstance().getUser(userName);
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
	}

	@Override
	public void execute() {
		try {
			int len;
			len = inMsg.readInt();
			byte[] fribyte = new byte[(int)len];
			inMsg.readBytes(fribyte);
			String friendname = new String(fribyte,"gb2312");
			User curuser = manager.getUser(user.getUsername());
			if(curuser==null)
			{
				try {
				    UserManager manager = UserManager.getInstance();
				
					curuser = manager.getUserFromDB(user.getUsername());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(curuser==null)
				{
					System.out.println(" user is not exist!");
					return;
				}
				curuser.setSessionId(session.getId());
				manager.getUserMap().putIfAbsent(user.getUsername(), curuser);
			}
			if(curuser.getSessionId().intValue()==-1)
				curuser.setSessionId(session.getId());
	        User fri = manager.getUser(friendname);
	        if(fri==null)
	        {
	        	System.out.println(" friend is now offline");
	        	return;
	        }
	        if(fri.getSessionId().intValue()==-1)
	        {
	        	System.out.println(" friend is now offline");
	        	return;
	        }
	        Channel mysession = IMPSTcpServer.getAllGroups().find(fri.getSessionId());
	        OutputMessage outMsg = MessageFactory.createSAddFriReq(user.getUsername());
	        mysession.write(ChannelBuffers.wrappedBuffer(outMsg.build()));
	        System.out.println("req sent...");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
