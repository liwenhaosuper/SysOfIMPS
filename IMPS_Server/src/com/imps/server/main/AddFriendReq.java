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
import com.imps.server.net.IoService;
import com.imps.server.net.IoSession;


public class AddFriendReq extends MessageProcessTask{

	public AddFriendReq(IoSession session, InputMessage message)
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
		DataInputStream body = message.getInputStream();
		//获取好友名
		
		try {
			long len;
			len = body.readLong();
			byte[] fribyte = new byte[(int)len];
			body.read(fribyte);
			String friendname = new String(fribyte,"gb2312");
			
			//将username添加入usermap之中
			User curuser = manager.getUser(message.getUserName());
			if(curuser==null)
			{
				//从数据库中添加进来
				try {
				    UserManager manager = UserManager.getInstance();
				
					curuser = manager.getUserFromDB(message.getUserName());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(curuser==null)
				{
					System.out.println(" user is not exist!");
					return;
				}
				curuser.setSessionId(session.getId());
				manager.getUserMap().putIfAbsent(message.getUserName(), curuser);
			}
			//设置session
			if(curuser.getSessionId()==-1)
				curuser.setSessionId(session.getId());
			
			//获取好友的session
	        IoService myserver = ServerBoot.server;
	        User fri = manager.getUser(friendname);
	        if(fri==null)
	        {
	        	System.out.println(" friend is now offline");
	        	return;
	        }
	        if(fri.getSessionId()==-1)
	        {
	        	System.out.println(" friend is now offline");
	        	return;
	        }
	        IoSession mysession = myserver.getIoSession(fri.getSessionId());
	        OutputMessage outMsg = MessageFactory.createSAddFriReq(message.getUserName());
	        mysession.write(outMsg);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
