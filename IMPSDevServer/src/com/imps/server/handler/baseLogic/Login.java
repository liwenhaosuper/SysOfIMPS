package com.imps.server.handler.baseLogic;

import java.sql.SQLException;
import java.util.HashMap;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import com.imps.server.main.IMPSTcpServer;
import com.imps.server.main.basetype.MessageProcessTask;
import com.imps.server.main.basetype.User;
import com.imps.server.manager.UserManager;
import com.imps.server.model.CommandId;
import com.imps.server.model.CommandType;
import com.imps.server.model.IMPSType;

public class Login extends MessageProcessTask{
	
	public Login(Channel session, IMPSType inMsg) {
		super(session, inMsg);
	}
	@Override
	public void execute() {
		String userName = inMsg.getmHeader().get("UserName");
		String pwd = inMsg.getmHeader().get("Password");
		if(userName==null||pwd==null){
			HashMap<String,String> header = new HashMap<String,String>();
			header.put("Command", CommandId.S_LOGIN_UNKNOWN);
			header.put("Result","NO");
			IMPSType result = new CommandType();
			result.setmHeader(header);
			session.write(ChannelBuffers.wrappedBuffer(result.MediaWrapper()));
			if(DEBUG)System.out.println("illegal login request");
			return;
		}
		if(validate(userName,pwd)){
			//success
			updateList(userName,true);
			User user = manager.getUser(userName);
			if(user==null){
				updateList(userName,true);
				user = manager.getUser(userName);
			}else{
				Integer sid = user.getSessionId();
				if(sid.intValue()!=-1){
					Channel mysession = IMPSTcpServer.getAllGroups().find(sid);
					if(mysession!=null&&!mysession.getId().equals(session.getId())){
						if(DEBUG) System.out.println("disconnect the older connection: "+user.getUsername());
						HashMap<String,String> header = new HashMap<String,String>();
						header.put("Command", CommandId.S_LOGIN_ERROR_OTHER_PLACE);
						header.put("Result","OK");
						IMPSType result = new CommandType();
						result.setmHeader(header);
						mysession.write(ChannelBuffers.wrappedBuffer(result.MediaWrapper()));
						IMPSTcpServer.getAllGroups().remove(mysession);
					}
				}
			}
			HashMap<String,String> header = new HashMap<String,String>();
			header.put("Command", CommandId.S_LOGIN_RSP);
			header.put("Result","OK");
			header.put("Email", user.getEmail());
			header.put("Gender", user.getGender()==0?"F":"M");
			IMPSType result = new CommandType();
			result.setmHeader(header);
			session.write(ChannelBuffers.wrappedBuffer(result.MediaWrapper()));
			updateList(userName,true);
			if(DEBUG)System.out.println("Login rsp sent...");
		}else{
			//fail
			HashMap<String,String> header = new HashMap<String,String>();
			header.put("Command", CommandId.S_LOGIN_ERROR_UNVALID);
			header.put("Result", "NO");
			header.put("Description","User Name or Password not valid.");
			IMPSType result = new CommandType();
			result.setmHeader(header);
			session.write(ChannelBuffers.wrappedBuffer(result.MediaWrapper()));
			if(DEBUG)System.out.println("login fail");
		}
	}
	public boolean validate(String userName,String pwd){
		boolean res = false;
		try {
			if(UserManager.getInstance().checkUser(userName,pwd))
				res = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;			
	}
}
