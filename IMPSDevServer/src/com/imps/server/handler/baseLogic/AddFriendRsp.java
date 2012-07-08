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

/** 
 * Processing add friend response
 * request header should includes: UserName,FriendName,Result
 * response header should includes: FriendName,Result
 * @author liwenhaosuper
 *
 */
public class AddFriendRsp extends MessageProcessTask{
	public AddFriendRsp(Channel session, IMPSType message){
		super(session, message);
	}
	@Override
	public void execute() {
		String userName = inMsg.getmHeader().get("UserName");
		String friendName = inMsg.getmHeader().get("FriendName");
		String rspInt = inMsg.getmHeader().get("Result");
		if(userName==null||friendName==null||rspInt==null){
			if(DEBUG) System.out.println("illegal add friend response.");
			IMPSType result = new CommandType();
			HashMap<String,String> header = new HashMap<String,String>();
			header.put("Command", CommandId.S_ADDFRIEND_RSP_FAIL);
			header.put("Message", "Illegal add friend response");
			header.put("Result","Fail");
			result.setmHeader(header);
			session.write(ChannelBuffers.wrappedBuffer(result.MediaWrapper()));
			return;
		}
		updateList(userName,true);
		User fri = manager.getUser(friendName);
		if(fri==null||fri.getSessionId().intValue()==-1){
			if(DEBUG) System.out.println("friend is now offline");
			IMPSType result = new CommandType();
			HashMap<String,String> header = new HashMap<String,String>();
			header.put("Command", CommandId.S_ADDFRIEND_RSP_FAIL);
			header.put("Message", "User is now offline.");
			header.put("Result","Fail");
			result.setmHeader(header);
			session.write(ChannelBuffers.wrappedBuffer(result.MediaWrapper()));
			return;
		}
		if(Integer.valueOf(rspInt).intValue()!=0){
        	try {
				UserManager.getInstance().addFriendToDB(userName,friendName);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	    Channel frisession = IMPSTcpServer.getAllGroups().find(fri.getSessionId());
	    if(frisession!=null){
			try {
				IMPSType result = new CommandType();
				HashMap<String,String> header = new HashMap<String,String>();
				header.put("Command", CommandId.S_ADDFRIEND_RSP);
				header.put("FriendName",userName);
				header.put("Result", rspInt);
				result.setmHeader(header);
				frisession.write(ChannelBuffers.wrappedBuffer(result.MediaWrapper()));
		        if(DEBUG) System.out.println("rsq sent...");
			} catch (Exception e) {
				if(DEBUG)e.printStackTrace();
			}
		}else{
			//TODO: add to database
			if(DEBUG) System.out.println("usr offline...");
		}
	   
	}

}

