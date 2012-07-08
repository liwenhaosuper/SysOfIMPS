package com.imps.server.handler.baseLogic;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import com.imps.server.main.basetype.MessageProcessTask;
import com.imps.server.main.basetype.OutputMessage;
import com.imps.server.main.basetype.User;
import com.imps.server.main.basetype.UserMessage;
import com.imps.server.main.basetype.userStatus;
import com.imps.server.manager.MessageFactory;
import com.imps.server.manager.UserManager;
import com.imps.server.model.CommandId;
import com.imps.server.model.CommandType;
import com.imps.server.model.IMPSType;

/**
 * This is a message processing thread
 * to handle request from client of geting offline message
 * @author Styx
 *
 */
public class OfflineMsg extends MessageProcessTask {
	public OfflineMsg(Channel session, IMPSType inMsg) {
		super(session, inMsg);
	}
	@Override
	public void execute() {
		String userName = inMsg.getmHeader().get("UserName");
		if(userName==null){
			if(DEBUG) System.out.println("illegal offlinemsg request");
			return;
		}
		try {
			// get off-line messages from database
			ArrayList< UserMessage > msgs = manager.getOfflineMsg(userName);
			// use a loop to send the messages
			for (int i = 0; i < msgs.size(); i++) {
				UserMessage msg = msgs.get(i);
				manager.markRead(msg.getM_id());
				IMPSType result = new CommandType();
				HashMap<String,String> header = new HashMap<String,String>();
				header.put("Command", CommandId.S_SEND_MSG);
				header.put("FriendName",manager.getUserFromDB(msg.getFrom()).getUsername());
				header.put("Time", msg.getTime());
				result.setmHeader(header);
				result.setContent(msg.getMsg().getBytes());
				session.write(ChannelBuffers.wrappedBuffer(result.MediaWrapper()));		        
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
