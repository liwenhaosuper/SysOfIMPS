

/*
 * Author: liwenhaosuper
 * Date: 2011/5/21
 */


package com.imps.server.handler.baseLogic;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import com.imps.server.main.IMPSTcpServer;
import com.imps.server.main.basetype.MessageProcessTask;
import com.imps.server.main.basetype.User;
import com.imps.server.main.basetype.userStatus;
import com.imps.server.model.CommandId;
import com.imps.server.model.CommandType;
import com.imps.server.model.IMPSType;

public class SendMessage extends MessageProcessTask{

	public SendMessage(Channel session, IMPSType message){
		super(session, message);
	}
	@Override
	public void execute() {
		String userName = inMsg.getmHeader().get("UserName");
		String friendname = inMsg.getmHeader().get("FriendName");
		byte[] msg = inMsg.getContent();
		if(msg==null||userName==null||friendname==null){
			if(DEBUG) System.out.println("illegal send message request");
			return;
		}
		updateList(userName,true);
		SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String datetime = tempDate.format(new java.util.Date());
		int sent = 1;
		if (manager.getUser(friendname) == null
		    || manager.getUser(friendname).getStatus() == userStatus.OFFLINE)
		    sent = 0;
		try{
			manager.addMessage(userName, friendname, datetime, new String(msg,"gb2312"), sent);
		}catch(Exception e){
			if(DEBUG)e.printStackTrace();
		}
		if(sent==1){
			User fri = manager.getUser(friendname);
			if(fri==null||fri.getSessionId().intValue()==-1){
				if(DEBUG) System.out.println("sth. go wrong");
				//TODO: Add to db
				return;
			}
			Channel frisession = IMPSTcpServer.getAllGroups().find(fri.getSessionId());
			if(frisession!=null){
				IMPSType result = new CommandType();
				HashMap<String,String> header = new HashMap<String,String>();
				header.put("Command", CommandId.S_SEND_MSG);
				header.put("FriendName", userName);
				header.put("Time", datetime);
				result.setmHeader(header);
				result.setContent(msg);
				frisession.write(ChannelBuffers.wrappedBuffer(result.MediaWrapper()));
			}else{
				if(DEBUG) System.out.println("sth. go wrong,really");
			}
			
		}
		else{
		    if(DEBUG) System.out.println("==   W: " + userName+" -> " + friendname + "[0] ==");
		    //TODO: notify user
		}
		
			
	}
	
}
