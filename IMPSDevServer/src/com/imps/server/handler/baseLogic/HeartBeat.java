package com.imps.server.handler.baseLogic;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;

import com.imps.server.main.basetype.MessageProcessTask;
import com.imps.server.main.basetype.User;
import com.imps.server.main.basetype.userStatus;
import com.imps.server.manager.UserManager;

public class HeartBeat extends MessageProcessTask{

	private 		User user=null;
	private			UserManager manager=null;
	
	public HeartBeat(Channel session, ChannelBuffer inMsg) {
		super(session, inMsg);
		
	}
	@Override
	public void parse() {
		int len = inMsg.readInt();
		byte nm[] = new byte[len];
		inMsg.readBytes(nm);
		String userName="";
		try {
			userName = new String(nm,"gb2312");
		} catch (UnsupportedEncodingException e1) {
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
			e.printStackTrace();
		}
	    len = inMsg.readInt();
		//time
		byte[] l_time = new byte[(int)len];
		inMsg.readBytes(l_time);
		try {
			String p_time = new String(l_time,"gb2312");
			int LatitudeE6 = inMsg.readInt();
			int LongitudeE6 = inMsg.readInt();
			if(LatitudeE6<=0||LongitudeE6<=0){
				return;
			}
			try {
				manager.updateLocation(userName, p_time, LatitudeE6/1E6, LongitudeE6/1E6);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		
	}

}
