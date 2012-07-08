package com.imps.server.handler.baseLogic;

import java.util.HashMap;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import com.imps.server.main.basetype.MessageProcessTask;
import com.imps.server.main.basetype.User;
import com.imps.server.model.CommandId;
import com.imps.server.model.CommandType;
import com.imps.server.model.IMPSType;

public class UpdateUserInfo extends MessageProcessTask{
	public UpdateUserInfo(Channel session, IMPSType inMsg) {
		super(session, inMsg);
	}
	@Override
	public void execute() {
		String userName = inMsg.getmHeader().get("UserName");
		String gender = inMsg.getmHeader().get("Gender");
		String email = inMsg.getmHeader().get("Email");
		if(userName==null||gender==null||email==null){
			if(DEBUG) System.out.println("illegal update user info request");
			return;
		}
		updateList(userName,true);
		User user = manager.getUser(userName);
		if(user==null){
			if(DEBUG) System.out.println("illegal update user info request...");
			return;
		}
		user.setEmail(email);
		user.setGender(Integer.valueOf(gender));
		boolean res = false;
		try{
			if(manager.updateUserEmail(user))
		    {	    	
				if(DEBUG)System.out.println("update user info ok");
				res = true;
		    } else{
		    	if(DEBUG) System.out.println("update user info fail");
		    	res = false;
		    }
		}catch(Exception e){
			if(DEBUG) e.printStackTrace();
			res = false;
		}
		HashMap<String,String> header = new HashMap<String,String>();
		header.put("Command", CommandId.S_UPDATE_USER_INFO_RSP);
		header.put("Result",res?"OK":"FAIL");
		IMPSType result = new CommandType();
		result.setmHeader(header);
		session.write(ChannelBuffers.wrappedBuffer(result.MediaWrapper()));
	}
}
