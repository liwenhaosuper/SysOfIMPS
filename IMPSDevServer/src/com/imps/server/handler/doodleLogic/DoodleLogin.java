package com.imps.server.handler.doodleLogic;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import com.imps.server.main.DoodleTcpServer;
import com.imps.server.main.basetype.MessageProcessTask;
import com.imps.server.main.basetype.User;
import com.imps.server.manager.DoodleManager;
import com.imps.server.manager.MessageFactory;
import com.imps.server.manager.UserManager;

public class DoodleLogin /*extends MessageProcessTask*/{
/*
	private String userName = "";
	public DoodleLogin(Channel session, ChannelBuffer inMsg) {
		super(session, inMsg);
	}

	@Override
	public void parse() {
		int len = inMsg.readInt();
		byte []nm = new byte[len];
		inMsg.readBytes(nm);
		try {
			userName = new String(nm,"gb2312");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		}
		
	}

	@Override
	public void execute() {
		if(DoodleTcpServer.allGroups.find(session.getId())==null){
			DoodleTcpServer.allGroups.add(session);
		}
		
		for(int i=0;i<DoodleTcpServer.doodleUsers.size();i++){
			if(DoodleTcpServer.doodleUsers.get(i).getUsername().equals(userName)){
				DoodleTcpServer.doodleUsers.get(i).setSessionId(session.getId());
				break;
			}
			if(i==(DoodleTcpServer.doodleUsers.size()-1)){
				User user = null;
				try {
					user = UserManager.getInstance().getUserFromDB(userName);
				} catch (SQLException e) {
					e.printStackTrace();
					return;
				}
				if(user==null){
					return;
				}else{
					user.setSessionId(session.getId());
					DoodleTcpServer.doodleUsers.add(user);
				}
			}
		}
		if(DoodleTcpServer.doodleUsers.size()==0){
			User user = null;
			try {
				user = UserManager.getInstance().getUserFromDB(userName);
			} catch (SQLException e) {
				e.printStackTrace();
				return;
			}
			if(user==null){
				return;
			}else{
				user.setSessionId(session.getId());
				DoodleTcpServer.doodleUsers.add(user);
			}
		}
		//TODO: Return online friendlist
		List<User> friends = DoodleManager.getInstance().getOnlineFriendList(userName);
		if(friends==null){
			return;
		}
		String []friparams = new String[friends.size()];
		for(int i=0;i<friends.size();i++){
			friparams[i] = friends.get(i).getUsername();
		}
		session.write(ChannelBuffers.wrappedBuffer(MessageFactory.createDoodleOnlineFriendList(friparams).build()));
		DoodleManager.getInstance().notifyStatus(userName, true);
	}
*/
}
