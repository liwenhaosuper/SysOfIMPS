



package com.imps.server.main;

import java.io.DataInputStream;
import java.io.IOException;
import java.sql.SQLException;

import com.imps.server.base.InputMessage;
import com.imps.server.base.MessageFactory;
import com.imps.server.base.MessageProcessTask;
import com.imps.server.base.NetAddress;
import com.imps.server.base.OutputMessage;
import com.imps.server.base.User;
import com.imps.server.base.userStatus;
import com.imps.server.handler.RegularUserCheck;
import com.imps.server.net.IoService;
import com.imps.server.net.IoSession;
import com.imps.server.net.impl.IoSessionImpl;



public class Login extends MessageProcessTask{


	public Login(IoSession session,InputMessage message) throws SQLException
	{
         super(session,message);
	}
	
	@Override
	public void parse() throws IOException {
		// TODO Auto-generated method stub
}

	@Override
	public void execute()  {
		// TODO Auto-generated method stub
		OutputMessage outMsg = null;
		try {
			if(!validate())
			{
				outMsg = MessageFactory.createErrorMsg();
				outMsg.getOutputStream().writeInt(6);
				session.write(outMsg);
			}
			else
			{
				User user = manager.getUser(message.getUserName());
				if(true)
				{
					try {
						if(user==null)
						{
							user = manager.getUserFromDB(message.getUserName());
							if(user==null)
								return;
						}
						else{
							long sid = user.getSessionId();
							if(sid!=-1)
							{
								IoService myserver = ServerBoot.server;
								IoSession mysession = myserver.getIoSession(sid);
								OutputMessage remsg = MessageFactory.createErrorMsg();
								remsg.getOutputStream().writeInt(7);
								mysession.write(remsg);
								mysession.close();
								System.out.println("disconnect the older connection: "+user.getUsername());
							}
							
						}
						outMsg = MessageFactory.createSLoginRsp();
						//username
						String str = user.getUsername();
						long len = (long)str.getBytes("gb2312").length;
						outMsg.getOutputStream().writeLong(len);
						outMsg.getOutputStream().write(str.getBytes("gb2312"));
						
						//gender
						outMsg.getOutputStream().writeInt(user.getGender());
						
						//email
						str = user.getEmail();
						len =(long)str.getBytes("gb2312").length;
						outMsg.getOutputStream().writeLong(len);
						outMsg.getOutputStream().write(str.getBytes("gb2312"));
						
						//add the user to the userlist
						user.setStatus(userStatus.ONLINE);
						//set session id
						user.setSessionId(session.getId());
						if(manager.getUser(user.getUsername())==null)
						      manager.addUser(user);
						else manager.getUser(user.getUsername()).setSessionId(session.getId());
						NetAddress netaddr = new NetAddress();
						netaddr.addPublicAddress(((IoSessionImpl)session).getChannel().socket().getInetAddress().getAddress().toString(),
								((IoSessionImpl)session).getChannel().socket().getPort());
						manager.addUserAddress(user.getUsername(), netaddr);
						//TODO: notify friends
						manager.updateUserStatus(user);						
						session.write(outMsg);
						RegularUserCheck check = new RegularUserCheck(user);
						manager.getTimer().schedule(check, 1000*1,1000*10);
						
						System.out.println("check starts:"+user.getUsername());
						
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		//set regular check
		
	}
	
	/**
	 * 0锟斤拷not valid username or password
	 * 1: valid information 
	 * @return
	 * @throws IOException
	 */
	public boolean validate() throws IOException
	{
		boolean res = false;
		
		DataInputStream body = message.getInputStream();
        long pwdsize = body.readLong();
		
		byte[] pwd = new byte[(int) pwdsize];
		body.read(pwd);   //
		String password = new String(pwd);
		
		try {
			if(manager.checkUser(message.getUserName(),password))
				res = true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(res == false)
	     	System.out.println("用户名或密码错误");
		return res;
	}

}
