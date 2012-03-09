package com.imps.server.handler.baseLogic;

import java.io.IOException;
import java.sql.SQLException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import com.imps.server.main.IMPSTcpServer;
import com.imps.server.main.basetype.CommandId;
import com.imps.server.main.basetype.MessageProcessTask;
import com.imps.server.main.basetype.NetAddress;
import com.imps.server.main.basetype.OutputMessage;
import com.imps.server.main.basetype.User;
import com.imps.server.main.basetype.location;
import com.imps.server.main.basetype.userStatus;
import com.imps.server.manager.UserManager;


public class FriendListRequest extends MessageProcessTask{
	
	private String userName;
	private UserManager manager;
	public FriendListRequest(Channel session, ChannelBuffer message)
	{
		super(session, message);
	}

	@Override
	public void parse() {
		// TODO Auto-generated method stub
		int len = inMsg.readInt();
		byte nm[] = new byte[len];
		inMsg.readBytes(nm);
		try {
			manager = UserManager.getInstance();
			userName = new String(nm,"gb2312");
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		}
		User user;
		try {
			user = manager.getUser(userName);
			if(user==null)
			{
				user = manager.getUserFromDB(userName);
				if(user==null){
					return;
				}
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
			//check session
			if(IMPSTcpServer.getAllGroups().find(user.getSessionId())==null){
				IMPSTcpServer.getAllGroups().add(session);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		OutputMessage outMsg = new OutputMessage(CommandId.S_FRIENDLIST_REFURBISH_RSP);
		try {
			UserManager manager = UserManager.getInstance();
			User[] fri = manager.getFriendlistFromDB(userName);
			int lon = 0;
			if(fri==null)
				lon = 0;
			else{
				lon = fri.length;
				for(int i=0;i<lon;i++)
				{
					if(UserManager.getInstance().userMap.containsKey(fri[i].getUsername()))
					{
						User usr = UserManager.getInstance().userMap.get(fri[i].getUsername());
						fri[i].setStatus(usr.getStatus());
					}
				}
			}
			outMsg.getOutputStream().writeInt(lon);
			location loc = new location();
			int len = 0;
			for(int i=0;i<lon;i++)
			{
				//user name
				len = fri[i].getUsername().getBytes("gb2312").length;
				outMsg.getOutputStream().writeInt(len);
				outMsg.getOutputStream().write(fri[i].getUsername().getBytes("gb2312"));//name
				//status 
				byte status = manager.getUserStatus(fri[i].getUsername());
				outMsg.getOutputStream().write(status);
				//email
				len = fri[i].getEmail().getBytes("gb2312").length;
				outMsg.getOutputStream().writeInt(len);
				outMsg.getOutputStream().write(fri[i].getEmail().getBytes("gb2312"));
				//time
				loc = manager.getFriendLocation(fri[i].getUsername());
				len = loc.ptime.getBytes("gb2312").length;
				outMsg.getOutputStream().writeInt(len);
				outMsg.getOutputStream().write(loc.ptime.getBytes("gb2312"));
				//location
				outMsg.getOutputStream().writeDouble(loc.x);
				outMsg.getOutputStream().writeDouble(loc.y);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e)
		{
			e.printStackTrace();
		}
		
		System.out.println("friend list back to client!");
		session.write(ChannelBuffers.wrappedBuffer(outMsg.build()));
		
	}
	
	private User getAssistor(){
		User user = new User();
		user.setEmail("impsweb@126.com");
		user.setGender(0);
		user.setStatus(userStatus.ONLINE);
		user.setUsername("IMPSAssistor");
		return user;
	}

}
