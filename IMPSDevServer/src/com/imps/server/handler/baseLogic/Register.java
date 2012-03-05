package com.imps.server.handler.baseLogic;

import java.io.UnsupportedEncodingException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import com.imps.server.main.basetype.CommandId;
import com.imps.server.main.basetype.MessageProcessTask;
import com.imps.server.main.basetype.OutputMessage;
import com.imps.server.main.basetype.User;
import com.imps.server.manager.MessageFactory;
import com.imps.server.manager.UserManager;

public class Register extends MessageProcessTask{
	private User user = new User();
	public Register(Channel session, ChannelBuffer inMsg) {
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
			user.setPassword(new String(nm,"gb2312"));
			user.setGender(inMsg.readInt());
			len = inMsg.readInt();
			nm = new byte[len];
			inMsg.readBytes(nm);
			user.setEmail(new String(nm,"gb2312"));
		}catch (UnsupportedEncodingException e){
			e.printStackTrace();
		}

	}
	@Override
	public void execute() {
		OutputMessage outMsg = null;
		try{
		    UserManager manager = UserManager.getInstance();
		    if(manager.registerUser(user))
		    {
		    	outMsg = MessageFactory.createSRegisterRsp();
		    	//注册成功
		    	System.out.println("注册成功!");
		    	outMsg.getOutputStream().writeInt(1);
		    }
		    else{
		    	outMsg = MessageFactory.createErrorMsg();
		    	outMsg.getOutputStream().writeInt(CommandId.S_REG_ERROR);
		    	outMsg.getOutputStream().writeInt(CommandId.S_REG_ERROR_USER_EXIST);//用户已经存在
		    	System.out.println("注册失败:用户已经存在");
		    }
		}catch(Exception e)
		{
			System.out.println("注册失败!");
			e.printStackTrace();
		}
		//反馈给客户端
		session.write(ChannelBuffers.wrappedBuffer(outMsg.build()));
	}
}
