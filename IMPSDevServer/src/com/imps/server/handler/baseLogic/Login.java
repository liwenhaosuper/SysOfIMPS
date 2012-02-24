package com.imps.server.handler.baseLogic;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import com.imps.server.main.IMPSTcpServer;
import com.imps.server.main.basetype.CommandId;
import com.imps.server.main.basetype.MessageProcessTask;
import com.imps.server.main.basetype.OutputMessage;
import com.imps.server.main.basetype.User;
import com.imps.server.main.basetype.userStatus;
import com.imps.server.manager.MessageFactory;
import com.imps.server.manager.UserManager;

public class Login extends MessageProcessTask{
	private String userName;
	private String pwd;

	public Login(Channel session, ChannelBuffer inMsg) {
		super(session, inMsg);
		// TODO Auto-generated constructor stub
		IMPSTcpServer.getAllGroups().add(session);
	}

	@Override
	public void parse() {
	}

	@Override
	public void execute() {
		OutputMessage outMsg = null;
		UserManager manager = null;
		try {
			manager = UserManager.getInstance();
			if(!validate()){
				// [ERROR] login failure
			    outMsg = MessageFactory.createErrorMsg();
			    try {
					outMsg.getOutputStream().writeInt(CommandId.S_LOGIN_ERROR);
					outMsg.getOutputStream().writeInt(CommandId.S_LOGIN_ERROR_UNVALID);
			    } catch (IOException e) {
					e.printStackTrace();
			    }
			    session.write(ChannelBuffers.wrappedBuffer(outMsg.build()));
			}else{
				// [SUCCE] login ok
				User user = manager.getUser(userName);
				if(user==null)
				{
					user = manager.getUserFromDB(userName);
					if(user==null){
						// TODO E....
						outMsg = MessageFactory.createErrorMsg();
						try {
							outMsg.getOutputStream().writeInt(CommandId.S_LOGIN_ERROR);
							outMsg.getOutputStream().writeInt(CommandId.S_LOGIN_UNKNOWN);
						} catch (IOException e) {
							e.printStackTrace();
						}
						System.out.println("Login error...");
						session.write(ChannelBuffers.wrappedBuffer(outMsg.build()));
						return;
					}
					Integer sid = user.getSessionId();
					if(sid!=-1)
					{
						Channel mysession = IMPSTcpServer.getAllGroups().find(sid);
						if(mysession!=null)
						{
							OutputMessage remsg = MessageFactory.createErrorMsg();
							remsg.getOutputStream().writeInt(CommandId.S_LOGIN_ERROR);
							remsg.getOutputStream().writeInt(CommandId.S_LOGIN_ERROR_OTHER_PLACE);
							mysession.write(ChannelBuffers.wrappedBuffer(remsg.build()));
							mysession.close();
							System.out.println("disconnect the older connection: "+user.getUsername());
						}
					}
					outMsg = MessageFactory.createSLoginRsp();
					//username
					outMsg.getOutputStream().writeInt(user.getUsername().getBytes("gb2312").length);
					outMsg.getOutputStream().write(user.getUsername().getBytes("gb2312"));
					
					//gender
					outMsg.getOutputStream().writeInt(user.getGender());
					
					//email
					outMsg.getOutputStream().writeInt(user.getEmail().getBytes("gb2312").length);
					outMsg.getOutputStream().write(user.getEmail().getBytes("gb2312"));
					
					//add the user to the userlist
					user.setStatus(userStatus.ONLINE);
					//set session id
					user.setSessionId(session.getId());
					manager.updateUserStatus(user);					
					session.write(ChannelBuffers.wrappedBuffer(outMsg.build()));
					System.out.println("Login ok...");

					// check OFFLINE msg
					manager.getOfflineMsg(userName);
				}else{
					
				}
			} // END of login [SUCCE]
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}

	}
	public boolean validate(){
		try {
			boolean res = false;
			int len = inMsg.readInt();
			byte []nm = new byte[len];
			inMsg.readBytes(nm);
			userName = new String(nm,"gb2312");
			len = inMsg.readInt();
			nm = new byte[len];
			inMsg.readBytes(nm);
			pwd = new String(nm,"gb2312");
			try {
				if(UserManager.getInstance().checkUser(userName,pwd))
					res = true;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return res;
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}				
	}
}
