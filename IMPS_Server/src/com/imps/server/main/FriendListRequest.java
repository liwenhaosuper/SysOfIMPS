package com.imps.server.main;

import java.io.IOException;
import java.sql.SQLException;

import com.imps.server.base.CommandId;
import com.imps.server.base.InputMessage;
import com.imps.server.base.MessageProcessTask;
import com.imps.server.base.OutputMessage;
import com.imps.server.base.User;
import com.imps.server.base.location;
import com.imps.server.handler.UserManager;
import com.imps.server.net.IoSession;


public class FriendListRequest extends MessageProcessTask{

	public FriendListRequest(IoSession session, InputMessage message)
			throws SQLException {
		super(session, message);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void parse() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		OutputMessage outMsg = new OutputMessage(CommandId.S_FRIENDLIST_REFURBISH_RSP);
		try {
			UserManager manager = UserManager.getInstance();
			User[] fri = manager.getFriendlistFromDB(message.getUserName());
			long lon = 0;
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
			outMsg.getOutputStream().writeLong(lon);
			location loc = new location();
			long len = 0;
			for(int i=0;i<lon;i++)
			{
				//user name
				len = fri[i].getUsername().getBytes("gb2312").length;
				outMsg.getOutputStream().writeLong(len);
				outMsg.getOutputStream().write(fri[i].getUsername().getBytes("gb2312"));//name
				//status 
				byte status = manager.getUserStatus(fri[i].getUsername());
				outMsg.getOutputStream().write(status);
				//email
				len = fri[i].getEmail().getBytes("gb2312").length;
				outMsg.getOutputStream().writeLong(len);
				outMsg.getOutputStream().write(fri[i].getEmail().getBytes("gb2312"));
				//time
				loc = manager.getFriendLocation(fri[i].getUsername());
				len = loc.ptime.getBytes("gb2312").length;
				outMsg.getOutputStream().writeLong(len);
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
		session.write(outMsg);
		
	}

}
