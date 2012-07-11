package com.imps.server.main.basetype;


import java.sql.SQLException;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import com.imps.server.main.IMPSTcpServer;
import com.imps.server.manager.MessageFactory;
import com.imps.server.manager.UserManager;
import com.imps.server.model.IMPSType;

public abstract class MessageProcessTask implements Runnable{
	protected static boolean DEBUG = true;
	protected Channel session;
	protected IMPSType inMsg;
	protected UserManager manager;
	public MessageProcessTask(Channel session,IMPSType inMsg){
		this.session = session;
		this.inMsg = inMsg;
		try {
			manager = UserManager.getInstance();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public abstract void execute();
	public  void updateList(String userName,boolean status){
		User user = manager.getUser(userName);
		if ((user == null&&status)) 
		{
			try {
				user = manager.getUserFromDB(userName);
			} catch (SQLException e) {
				e.printStackTrace();
				return;
			}
			user.setStatus(userStatus.ONLINE);
			user.setSessionId(session.getId());
			manager.addUser(user);
		}else if(user!=null&&!status){
			user.setStatus(userStatus.OFFLINE);
			manager.deleteUser(user);
		}else if(status&&user!=null&&!session.getId().equals(user.getSessionId())){
			user.setSessionId(session.getId());
		}
		Channel mysession = IMPSTcpServer.getAllGroups().find(session.getId());
		if(mysession==null){
			IMPSTcpServer.getAllGroups().add(session);
		}
	}

	@Override
	public void run() {
		try {
			execute();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
