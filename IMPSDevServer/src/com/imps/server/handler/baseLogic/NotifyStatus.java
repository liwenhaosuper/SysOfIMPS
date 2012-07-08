package com.imps.server.handler.baseLogic;

import java.sql.SQLException;

import org.jboss.netty.channel.Channel;

import com.imps.server.main.IMPSTcpServer;
import com.imps.server.main.basetype.MessageProcessTask;
import com.imps.server.main.basetype.User;
import com.imps.server.main.basetype.userStatus;
import com.imps.server.model.IMPSType;

public class NotifyStatus extends MessageProcessTask{
	public NotifyStatus(Channel session, IMPSType inMsg) {
		super(session, inMsg);
	}
	@Override
	public void execute() {
		String userName = inMsg.getmHeader().get("UserName");
		String status = inMsg.getmHeader().get("Status");	 
		if(userName==null||status==null){
			if(DEBUG) System.out.println("illegal request of notify status");
			return;
		}
		try {
			User user = manager.getUser(userName);
			if(user==null&&status.equals("ONLINE")) 
			{
				user = manager.getUserFromDB(userName);
				user.setStatus(userStatus.ONLINE);
				user.setSessionId(session.getId());
				manager.addUser(user);
			}else if(user!=null&&status.equals("OFFLINE")){
				manager.deleteUser(user);
				session.close();
				IMPSTcpServer.getAllGroups().remove(session);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		}
		if(DEBUG) System.out.println("notify status done");
	}

}
