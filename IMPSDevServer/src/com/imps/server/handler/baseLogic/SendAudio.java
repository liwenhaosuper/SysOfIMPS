package com.imps.server.handler.baseLogic;

import java.util.HashMap;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import com.imps.server.main.IMPSTcpServer;
import com.imps.server.main.basetype.MessageProcessTask;
import com.imps.server.main.basetype.User;
import com.imps.server.model.CommandId;
import com.imps.server.model.CommandType;
import com.imps.server.model.IMPSType;

public class SendAudio extends MessageProcessTask{
	public SendAudio(Channel session, IMPSType message){
		super(session, message);
	}
	@Override
	public void execute() {
		String userName = inMsg.getmHeader().get("UserName");
		String friName = inMsg.getmHeader().get("FriendName");
		byte imagebuff[] = inMsg.getContent();
		if(imagebuff==null||userName==null||friName==null){
			if(DEBUG) System.out.println("illegal send audio request");
			return;
		}
		updateList(userName,true);
		User fri = manager.getUser(friName);
		if(fri==null||fri.getSessionId().intValue()==-1){
			if(DEBUG) System.out.println("friend is now offline");
			//TODO: Add to db
			return;
		}
		Channel frisession = IMPSTcpServer.getAllGroups().find(fri.getSessionId());
		if(frisession!=null){
			IMPSType result = new CommandType();
			HashMap<String,String> header = new HashMap<String,String>();
			header.put("Command", CommandId.S_AUDIO_REQ);
			header.put("FriendName", userName);
			header.put("Result","OK");
			result.setmHeader(header);
			result.setContent(imagebuff);
			session.write(ChannelBuffers.wrappedBuffer(result.MediaWrapper()));
		}else{
			if(DEBUG)System.out.println("friend offline");
			//TODO: Add to db
			return;
		}
	}

}
