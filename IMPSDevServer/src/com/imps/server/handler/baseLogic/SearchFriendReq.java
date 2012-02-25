package com.imps.server.handler.baseLogic;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import com.imps.server.main.basetype.MessageProcessTask;
import com.imps.server.main.basetype.OutputMessage;
import com.imps.server.main.basetype.User;
import com.imps.server.main.basetype.userStatus;
import com.imps.server.manager.MessageFactory;
import com.imps.server.manager.UserManager;

public class SearchFriendReq extends MessageProcessTask{

	private User user;
	private UserManager manager;
	public SearchFriendReq(Channel session, ChannelBuffer message)
		 {
		super(session, message);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void parse(){
		// TODO Auto-generated method stub
		int len = inMsg.readInt();
		byte []nm=new byte[len];
		inMsg.readBytes(nm);
		String userName="";
		try {
			userName = new String(nm,"gb2312");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
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
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		try {
			manager = UserManager.getInstance();

			//get the friend name
			int len = inMsg.readInt();
			byte[] fribyte = new byte[len];
			inMsg.readBytes(fribyte);
			String keyword = new String(fribyte,"gb2312");
			List <String> res =  manager.getSearchFriendResult(keyword);
			OutputMessage msg = MessageFactory.createSSearchFriendRsp(res);
			session.write(ChannelBuffers.wrappedBuffer(msg.build()));
			System.out.println("search result back...");
		}catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}catch (IOException e2)
		{
			e2.printStackTrace();
		}
	}

}
