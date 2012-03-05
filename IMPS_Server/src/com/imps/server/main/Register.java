package com.imps.server.main;

import java.io.DataInputStream;
import java.io.IOException;
import java.sql.SQLException;

import com.imps.server.base.InputMessage;
import com.imps.server.base.MessageFactory;
import com.imps.server.base.MessageProcessTask;
import com.imps.server.base.OutputMessage;
import com.imps.server.base.User;
import com.imps.server.handler.UserManager;
import com.imps.server.net.IoSession;


public class Register extends MessageProcessTask{
	
	private User user = new User();

	public Register(IoSession session, InputMessage message) throws SQLException {
		super(session, message);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void parse() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		OutputMessage outMsg = null;
		try{
		    initial();
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
		    	outMsg.getOutputStream().writeInt(1);//用户已经存在
		    	System.out.println("注册失败!");
		    	outMsg.getOutputStream().writeInt(0);
		    }
		}catch(Exception e)
		{
			System.out.println("注册失败!");
			e.printStackTrace();
		}
		
		//反馈给客户端
		session.write(outMsg);
		
	}
	
	public void initial() throws IOException
	{
		DataInputStream body = message.getInputStream();
		long pwdsize = body.readLong();
		
		byte[] pwd = new byte[(int) pwdsize];
		int tt = body.read(pwd);   //
		String password = new String(pwd,"gb2312");	
		int gender = body.readInt();		
		long emailsize = body.readLong();
			
		byte[] mail = new byte[(int) emailsize];
		body.read(mail);
		String email = new String(mail,"gb2312");
		user.setEmail(email);
		user.setGender(gender);
		user.setPassword(password);
		user.setUsername(message.getUserName());
	}
	
	
	

}
