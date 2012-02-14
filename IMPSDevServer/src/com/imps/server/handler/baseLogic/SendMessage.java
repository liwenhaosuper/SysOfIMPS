

/*
 * Author: liwenhaosuper
 * Date: 2011/5/21
 */


package com.imps.server.handler.baseLogic;

import java.io.DataInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import com.imps.server.main.IMPSTcpServer;
import com.imps.server.main.ServerBoot;
import com.imps.server.main.basetype.CommandId;
import com.imps.server.main.basetype.MessageProcessTask;
import com.imps.server.main.basetype.OutputMessage;
import com.imps.server.main.basetype.User;
import com.imps.server.main.basetype.userStatus;
import com.imps.server.manager.MessageFactory;
import com.imps.server.manager.UserManager;

public class SendMessage extends MessageProcessTask{

	private UserManager manager;
	private User user;
	public SendMessage(Channel session, ChannelBuffer message)
			 {
		super(session, message);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void parse(){
		// TODO Auto-generated method stub
		int len = inMsg.readInt();
		byte nm[] = new byte[len];
		inMsg.readBytes(nm);
		try {
			String userName = new String(nm,"gb2312");
			manager = UserManager.getInstance();
			user = manager.getUser(userName);
			if(user==null)
			{
				user = manager.getUserFromDB(userName);
				user.setStatus(userStatus.ONLINE);
				user.setSessionId(session.getId());
				manager.addUser(user);
				User[] friends = user.getOnlineFriendList();
				//通知所有朋友
				if(friends==null)
					return ;
				for(int i=0;i<friends.length;i++)
				{
				     manager.updateUserStatus(user);
				}
			}
			user.setSessionId(session.getId());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void execute() {
		try {
			//get the friend name
			int len = inMsg.readInt();
			byte[] fribyte = new byte[len];
			inMsg.readBytes(fribyte);
			String friendname = new String(fribyte,"gb2312");
			//get sid
			int sid = inMsg.readInt();
			//get the message
			len = inMsg.readInt();
			byte[] bmsg = new byte[len];
			inMsg.readBytes(bmsg);
			String msg = new String(bmsg,"gb2312");		
			System.out.println("friend:"+friendname+" msg:"+msg);
			//获取时间
			SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String datetime = tempDate.format(new java.util.Date());
			
			//存入数据库中
			manager.addMessage(user.getUsername(), friendname, datetime, msg);


			if(manager.getUserMap().containsKey(friendname))
			{
				//向好友发送信息
				//获取好友的session
		        User fri = manager.getUser(friendname);
		        Channel mysession = IMPSTcpServer.getAllGroups().find(fri.getSessionId());
		       // OutputMessage;
		        OutputMessage remsg = MessageFactory.createSSendMsg(user.getUsername(), msg, datetime);
		        mysession.write(ChannelBuffers.wrappedBuffer(remsg.build()));
		        System.out.println(" send msg to "+friendname+" successfully!");
		        //反馈
		        session.write(ChannelBuffers.wrappedBuffer(MessageFactory.createSSmsSuccess(friendname, sid).build()));
			}
			else{
				//该好友不在线
			    System.out.println(" user is now offline and could not sent msg to him~");
				OutputMessage remsg = MessageFactory.createErrorMsg();
				remsg.getOutputStream().writeInt(CommandId.S_SMS_ERROR);
				remsg.getOutputStream().writeInt(CommandId.S_SMS_ERROR_OFFLINE);
				remsg.getOutputStream().writeInt(sid);
				session.write(ChannelBuffers.wrappedBuffer(remsg.build()));
			}
			
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}catch (IOException e2)
		{
			e2.printStackTrace();
		}


		
		
	}
	
}
