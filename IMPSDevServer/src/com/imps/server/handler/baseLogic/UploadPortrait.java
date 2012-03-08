package com.imps.server.handler.baseLogic;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import com.imps.server.main.basetype.MessageProcessTask;
import com.imps.server.main.basetype.OutputMessage;
import com.imps.server.main.basetype.User;
import com.imps.server.manager.MessageFactory;

public class UploadPortrait extends MessageProcessTask{
	private User user = new User();
	private byte[] b;
	public UploadPortrait(Channel session, ChannelBuffer inMsg) {
		super(session, inMsg);
	}

	@Override
	public void parse() {
		try{
			int len = inMsg.readInt();
			byte nm[] = new byte[len];
			inMsg.readBytes(nm);
			user.setUsername(new String(nm,"gb2312"));
			
			len = inMsg.readInt();
			nm = new byte[len];
			inMsg.readBytes(nm);
			b = nm;
			
			System.out.println(user.getUsername() + " upload a portrait(len is " + len + " )");
		}catch (UnsupportedEncodingException e){
			e.printStackTrace();
		}
	}

	@Override
	public void execute() {
		File fpath = new File("portrait/");
        if (!fpath.exists())
        	fpath.mkdirs();
		
		try {
			File f = new File("portrait/" + user.getUsername() + ".portrait");
			FileOutputStream fstream = new FileOutputStream(f);
			BufferedOutputStream stream = new BufferedOutputStream(fstream);
			stream.write(b);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		OutputMessage outMsg = MessageFactory.createSUploadPortraitRsp();
//		OutputMessage outMsg = null;
//		try{
//		    UserManager manager = UserManager.getInstance();
//		    if(manager.updateUserEmail(user))
//		    {
//		    	outMsg = MessageFactory.createSUploadPortraitRsp();
//		    	//更新成功
//		    	System.out.println("更新成功!");
//		    } else{
//		    	System.out.println("更新失败!");
//		    }
//		} catch(Exception e) {
//			System.out.println("更新失败!");
//			e.printStackTrace();
//		}
		//反馈给客户端
		session.write(ChannelBuffers.wrappedBuffer(outMsg.build()));
	}
}
