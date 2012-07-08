package com.imps.server.handler.baseLogic;

import java.sql.SQLException;
import java.util.HashMap;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import com.imps.server.main.IMPSTcpServer;
import com.imps.server.main.basetype.MessageProcessTask;
import com.imps.server.main.basetype.User;
import com.imps.server.model.CommandId;
import com.imps.server.model.CommandType;
import com.imps.server.model.IMPSType;

/**
 * processing add friend request
 * @author liwenhaosuper
 *
 */
public class AddFriendReq extends MessageProcessTask{
	public AddFriendReq(Channel session, IMPSType message){
		super(session, message);
	}
	@Override
	public void execute() {
		String userName = inMsg.getmHeader().get("UserName");
		String friendName = inMsg.getmHeader().get("FriendName");
		String reqMsg = inMsg.getmHeader().get("Message");
		if(userName==null||friendName==null){
			if(DEBUG) System.out.println("illegal add friend request.");
			IMPSType result = new CommandType();
			HashMap<String,String> header = new HashMap<String,String>();
			header.put("Command", CommandId.S_ADDFRIEND_REQ_FAIL);
			header.put("Message", "Illegal add friend request");
			header.put("Result","Fail");
			result.setmHeader(header);
			session.write(ChannelBuffers.wrappedBuffer(result.MediaWrapper()));
			return;
		}
		if(reqMsg==null){
			reqMsg = "";
		}
		updateList(userName,true);
		User fri = manager.getUser(friendName);
		if(fri==null||fri.getSessionId().intValue()==-1){
			try {
				if(manager.getFriendlistFromDB(friendName)==null){	
					if(DEBUG) System.out.println("user not exists");
					IMPSType result = new CommandType();
					HashMap<String,String> header = new HashMap<String,String>();
					header.put("Command", CommandId.S_ADDFRIEND_REQ_FAIL);
					header.put("Message", "User not exist");
					header.put("Result","Fail");
					result.setmHeader(header);
					session.write(ChannelBuffers.wrappedBuffer(result.MediaWrapper()));
					return;
				}
			} catch (SQLException e) {
				if(DEBUG) e.printStackTrace();
			}
			if(DEBUG) System.out.println("friend is now offline");
			IMPSType result = new CommandType();
			HashMap<String,String> header = new HashMap<String,String>();
			header.put("Command", CommandId.S_ADDFRIEND_REQ_FAIL);
			header.put("Message", "User is Offline");
			header.put("Result","Fail");
			result.setmHeader(header);
			session.write(ChannelBuffers.wrappedBuffer(result.MediaWrapper()));
			return;
		}
		Channel frisession = IMPSTcpServer.getAllGroups().find(fri.getSessionId());
		if(frisession!=null){
			try {
				IMPSType result = new CommandType();
				HashMap<String,String> header = new HashMap<String,String>();
				header.put("Command", CommandId.S_ADDFRIEND_REQ);
				header.put("FriendName",userName);
				header.put("Message", reqMsg);
				result.setmHeader(header);
				frisession.write(ChannelBuffers.wrappedBuffer(result.MediaWrapper()));
		        if(DEBUG) System.out.println("req sent...");
			} catch (Exception e) {
				if(DEBUG)e.printStackTrace();
			}
		}else{
			//TODO: add to database
			if(DEBUG)System.out.println("add friend req: user is offline.");
			IMPSType result = new CommandType();
			HashMap<String,String> header = new HashMap<String,String>();
			header.put("Command", CommandId.S_ADDFRIEND_REQ_FAIL);
			header.put("Message", "User is Offline");
			header.put("Result","Fail");
			result.setmHeader(header);
			session.write(ChannelBuffers.wrappedBuffer(result.MediaWrapper()));
		}
	}

}
