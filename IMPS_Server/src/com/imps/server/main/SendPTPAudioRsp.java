package com.imps.server.main;

import java.io.DataInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import com.imps.server.base.InputMessage;
import com.imps.server.base.MessageFactory;
import com.imps.server.base.MessageProcessTask;
import com.imps.server.base.OutputMessage;
import com.imps.server.base.User;
import com.imps.server.base.userStatus;
import com.imps.server.handler.UserManager;
import com.imps.server.net.IoFuture;
import com.imps.server.net.IoService;
import com.imps.server.net.IoSession;

public class SendPTPAudioRsp extends MessageProcessTask{

	public SendPTPAudioRsp(IoSession session, InputMessage message)
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
			System.out.println("audio rsp ip is "+ ip+" !!!");
			//get the port
			int port = inMsg.readInt();
			//获取时间
			SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String datetime = tempDate.format(new java.util.Date());
			//将username添加入usermap之中
			User curuser = manager.getUser(message.getUserName());
			if(curuser==null)
			{
				//从数据库中添加进来
				curuser = manager.getUserFromDB(message.getUserName());
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
			
			if(manager.getUserMap().containsKey(friendname))
			{
				//向好友发送信息
				//获取好友的session
		        IoService myserver = ServerBoot.server;
		        User fri = manager.getUser(friendname);
		        IoSession mysession = myserver.getIoSession(fri.getSessionId());
		       // OutputMessage;
		        OutputMessage remsg = MessageFactory.createSPTPAudioRsp(message.getUserName(), ip,port,res==1?true:false);
		        IoFuture iof = mysession.write(remsg);
		        if(iof.isCannel()||iof.isComplete()||iof.isComplete())
		        {
		        	System.out.println(" send msg to "+friendname+" failed!");
		        } 
		        System.out.println(" send msg to "+friendname+" successfully!");
			}
			else{
				//该好友不在线
			    System.out.println(" user is now offline and could not sent audio response to him~");
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
