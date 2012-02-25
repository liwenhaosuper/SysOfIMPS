package com.imps.server.handler.baseLogic;

import java.sql.SQLException;
import java.util.ArrayList;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import com.imps.server.main.basetype.MessageProcessTask;
import com.imps.server.main.basetype.OutputMessage;
import com.imps.server.main.basetype.User;
import com.imps.server.main.basetype.UserMessage;
import com.imps.server.main.basetype.userStatus;
import com.imps.server.manager.MessageFactory;
import com.imps.server.manager.UserManager;

/**
 * This is a message processing thread
 * to handle request from client of geting offline message
 * @author Styx
 *
 */
public class OfflineMsg extends MessageProcessTask {

	private UserManager manager;
	private User user;
	public OfflineMsg(Channel session, ChannelBuffer inMsg) {
		super(session, inMsg);
	}

	@Override
	public void parse() {
		int len = inMsg.readInt();
		byte nm[] = new byte[len];
		inMsg.readBytes(nm);
		try {
			// user's name of the receiver of offline message
			// also the sender of the request
			String userName = new String(nm,"gb2312");
			manager = UserManager.getInstance();
			user = manager.getUser(userName);
			if (user == null) // If the user is offline or heartbeats are not received
			{
				user = manager.getUserFromDB(userName);
				user.setStatus(userStatus.ONLINE);
				user.setSessionId(session.getId());
				manager.addUser(user);
			}
			user.setSessionId(session.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void execute() {
		try {
			// get off-line messages from database
			ArrayList< UserMessage > msgs = manager.getOfflineMsg(user.getUsername());

			// use a loop to send the messages
			for (int i = 0; i < msgs.size(); i++) {
				UserMessage msg = msgs.get(i);
				OutputMessage outMsg = MessageFactory.createSSendMsg(manager.getUserFromDB(msg.getFrom()).getUsername(), msg.getMsg(), msg.getTime());
		        session.write(ChannelBuffers.wrappedBuffer(outMsg.build()));
		        manager.markRead(msg.getM_id());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public UserManager getManager() {
		return manager;
	}

	public void setManager(UserManager manager) {
		this.manager = manager;
	}

}
