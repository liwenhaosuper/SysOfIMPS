package com.imps.server.main;

import java.io.DataInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import com.imps.server.base.InputMessage;
import com.imps.server.base.MessageFactory;
import com.imps.server.base.MessageProcessTask;
import com.imps.server.base.NetAddress;
import com.imps.server.base.OutputMessage;
import com.imps.server.base.User;
import com.imps.server.base.userStatus;
import com.imps.server.handler.UserManager;
import com.imps.server.net.IoFuture;
import com.imps.server.net.IoService;
import com.imps.server.net.IoSession;

public class SendPTPVideoRsp extends MessageProcessTask{

	public SendPTPVideoRsp(IoSession session, InputMessage message)
			throws SQLException {
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
		UserManager manager;
		try {
			manager = UserManager.getInstance();
			User user = manager.getUser(message.getUserName());
			if(user==null)
			{
				user = manager.getUserFromDB(message.getUserName());
				user.setStatus(userStatus.ONLINE);
				manager.addUser(user);
			}
			DataInputStream inMsg = message.getInputStream();
			//get the friend name
			long len = inMsg.readLong();
			byte[] fribyte = new byte[(int)len];
			inMsg.read(fribyte);
			String friendname = new String(fribyte,"gb2312");
			//get the result
			int res = inMsg.readInt();
			System.out.println(" rsp result is "+res);
			//get the ip
			len = inMsg.readLong();
			byte[] ipbyte = new byte[(int)len];
			inMsg.read(ipbyte);
			String ip = new String(ipbyte,"gb2312");
			System.out.println("video rsp ip is "+ ip+" !!!");
			//get the port
			int port = inMsg.readInt();
			NetAddress netAddr = UserManager.getInstance().userAddress.get(message.getUserName());
			netAddr.addNATAddress(ip, port);
			UserManager.getInstance().addUserAddress(message.getUserName(), netAddr);
			
			SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String datetime = tempDate.format(new java.util.Date());
			
			User curuser = manager.getUser(message.getUserName());
			if(curuser==null)
			{
			
				curuser = manager.getUserFromDB(message.getUserName());
				if(curuser==null)
				{
					System.out.println(" user is not exist!");
					return;
				}
				curuser.setSessionId(session.getId());
				manager.getUserMap().putIfAbsent(message.getUserName(), curuser);
			}
			
			if(curuser.getSessionId()==-1)
				curuser.setSessionId(session.getId());
			
			if(manager.getUserMap().containsKey(friendname))
			{
				
		        IoService myserver = ServerBoot.server;
		        User fri = manager.getUser(friendname);
		        IoSession mysession = myserver.getIoSession(fri.getSessionId());
		       // OutputMessage;
		        OutputMessage remsg = MessageFactory.createSPTPVideoRsp(message.getUserName(), ip,port,res==1?true:false,netAddr.getPubIp(),netAddr.getPubPort());
		        IoFuture iof = mysession.write(remsg);
		        if(iof.isCannel()||iof.isComplete()||iof.isComplete())
		        {
		        	System.out.println(" send msg to "+friendname+" failed!");
		        } 
		        System.out.println(" send msg to "+friendname+" successfully!");
			}
			else{
				
			    System.out.println(" user is now offline and could not sent video response to him~");
				OutputMessage remsg = MessageFactory.createErrorMsg();
				remsg.getOutputStream().writeInt(5);
				session.write(remsg);
			}
		}catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}catch (IOException e2)
		{
			e2.printStackTrace();
		}
	}

}
