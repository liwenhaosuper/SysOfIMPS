package com.imps.server.handler.baseLogic;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import com.imps.server.main.basetype.MessageProcessTask;
import com.imps.server.model.CommandId;
import com.imps.server.model.CommandType;
import com.imps.server.model.IMPSType;

public class UploadPortrait extends MessageProcessTask{
	private byte[] b;
	public UploadPortrait(Channel session, IMPSType inMsg) {
		super(session, inMsg);
	}
	@Override
	public void execute() {
		String userName = inMsg.getmHeader().get("UserName");
		b = inMsg.getContent();
		if(b==null||userName==null){
			if(DEBUG)System.out.println("illegal request of uploading portrait");
			return;
		}
		File fpath = new File("portrait/");
        if (!fpath.exists())
        	fpath.mkdirs();
		try {
			File f = new File("portrait/" + userName + ".portrait");
			FileOutputStream fstream = new FileOutputStream(f);
			BufferedOutputStream stream = new BufferedOutputStream(fstream);
			stream.write(b);
			stream.close();
			fstream.close();
		} catch (Exception e1) {
			if(DEBUG) e1.printStackTrace();
		}
		IMPSType result = new CommandType();
		HashMap<String,String> header = new HashMap<String,String>();
		header.put("Command", CommandId.S_UPLOAD_PORTRAIT_RSP);
		header.put("Result","OK");
		result.setmHeader(header);
		session.write(ChannelBuffers.wrappedBuffer(result.MediaWrapper()));
		updateList(userName,true);
		
	}
}
