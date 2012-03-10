package com.imps.server.handler.baseLogic;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;

import com.imps.server.main.basetype.MessageProcessTask;
import com.imps.server.main.basetype.User;
import com.imps.server.main.basetype.userStatus;
import com.imps.server.manager.UserManager;

public class NotifyStatus extends MessageProcessTask{
	private String userName = "";
	private byte status = 0;
	public NotifyStatus(Channel session, ChannelBuffer inMsg) {
		super(session, inMsg);
	}

	@Override
	public void parse() {
		int len = inMsg.readInt();
		byte nm[] = new byte[len];
		inMsg.readBytes(nm);
		try {
			userName = new String(nm,"gb2312");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		status = inMsg.readByte();
	}

	@Override
	public void execute() {
		try {
			UserManager manager = UserManager.getInstance();
			User user = manager.getUser(userName);
			if(user==null&&status==userStatus.ONLINE) 
			{
				user = manager.getUserFromDB(userName);
				user.setStatus(status);
				user.setSessionId(session.getId());
				manager.addUser(user);
			}else if(user!=null&&status==userStatus.OFFLINE){
				manager.deleteUser(user);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

}
