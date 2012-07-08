package com.imps.server.handler.baseLogic;

import java.sql.SQLException;
import java.util.HashMap;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import com.imps.server.main.basetype.MessageProcessTask;
import com.imps.server.main.basetype.User;
import com.imps.server.model.CommandId;
import com.imps.server.model.CommandType;
import com.imps.server.model.IMPSType;

public class Register extends MessageProcessTask{
	public Register(Channel session,IMPSType inMsg) {
		super(session, inMsg);
	}
	@Override
	public void execute() {
		String userName = inMsg.getmHeader().get("UserName");
		String pwd = inMsg.getmHeader().get("Password");
		String email = inMsg.getmHeader().get("Email");
		String gender = inMsg.getmHeader().get("Gender");
		if(userName==null||pwd==null||userName.equals("")||pwd.equals("")){
			return;
		}
		User user = new User();
		user.setEmail(email);
		user.setGender(gender=="M"?0:1);
		user.setPassword(pwd);
		user.setUsername(userName);
		try {
			if(manager.registerUser(user)){
				HashMap<String,String> header = new HashMap<String,String>();
				header.put("Command", CommandId.S_REGISTER);
				header.put("Result","OK");
				header.put("UserName", userName);
				header.put("Email", email);
				header.put("Gener", gender);
				IMPSType result = new CommandType();
				result.setmHeader(header);
				session.write(ChannelBuffers.wrappedBuffer(result.MediaWrapper()));
				if(DEBUG)System.out.println("user reg succ...");
			}else{
				HashMap<String,String> header = new HashMap<String,String>();
				header.put("Command", CommandId.S_REG_ERROR_USER_EXIST);
				header.put("Result","NO");
				header.put("Description","username already exists.");
				IMPSType result = new CommandType();
				result.setmHeader(header);
				session.write(ChannelBuffers.wrappedBuffer(result.MediaWrapper()));
				if(DEBUG)System.out.println("user reg fail:username already exists...");
			}
		} catch (SQLException e) {
			HashMap<String,String> header = new HashMap<String,String>();
			header.put("Command", CommandId.S_REG_ERROR_UNKNOWN);
			header.put("Result","NO");
			header.put("Description","server internal error.");
			IMPSType result = new CommandType();
			result.setmHeader(header);
			session.write(ChannelBuffers.wrappedBuffer(result.MediaWrapper()));
			if(DEBUG) e.printStackTrace();
			if(DEBUG)System.out.println("user reg:server internal error...");
		}
		
	}
}
