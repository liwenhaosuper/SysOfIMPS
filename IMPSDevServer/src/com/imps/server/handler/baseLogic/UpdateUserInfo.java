package com.imps.server.handler.baseLogic;

import java.io.UnsupportedEncodingException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import com.imps.server.main.basetype.MessageProcessTask;
import com.imps.server.main.basetype.OutputMessage;
import com.imps.server.main.basetype.User;
import com.imps.server.manager.MessageFactory;
import com.imps.server.manager.UserManager;

public class UpdateUserInfo extends MessageProcessTask{
	private User user = new User();
	public UpdateUserInfo(Channel session, ChannelBuffer inMsg) {
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
			user.setGender(len);
			len = inMsg.readInt();
			nm = new byte[len];
			inMsg.readBytes(nm);
			user.setEmail(new String(nm,"gb2312"));
			
			System.out.println(user.getUsername()+","+user.getGender()+","+user.getEmail());
		}catch (UnsupportedEncodingException e){
			e.printStackTrace();
		}
	}

	@Override
	public void execute() {
		OutputMessage outMsg = null;
		try{
		    UserManager manager = UserManager.getInstance();
		    if(manager.updateUserEmail(user))
		    {
		    	outMsg = MessageFactory.createSUpdateUserInfoRsp();
		    	//更新成功
		    	System.out.println("更新成功!");
		    } else{
		    	System.out.println("更新失败!");
		    }
		} catch(Exception e) {
			System.out.println("更新失败!");
			e.printStackTrace();
		}
		//反馈给客户端
		session.write(ChannelBuffers.wrappedBuffer(outMsg.build()));
	}
}
