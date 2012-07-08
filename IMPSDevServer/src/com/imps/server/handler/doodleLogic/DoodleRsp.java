package com.imps.server.handler.doodleLogic;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;

import com.imps.server.main.DoodleTcpServer;
import com.imps.server.main.basetype.MessageProcessTask;
import com.imps.server.main.basetype.User;
import com.imps.server.manager.MessageFactory;
import com.imps.server.manager.UserManager;

public class DoodleRsp /*extends MessageProcessTask*/{

/*	private String userName;
	private String friName;
	private boolean res;
	public DoodleRsp(Channel session, ChannelBuffer inMsg) {
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
		len = inMsg.readInt();
		nm = new byte[len];
		inMsg.readBytes(nm);
		try {
			friName = new String(nm,"gb2312");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		}
		res = (inMsg.readInt()==0?false:true);
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
		if(res==true){
			ChannelGroup group = DoodleTcpServer.roomGroups.get(friName);
			if(group!=null){
				group.add(session);
			}
		}
	}
	@Override
	public void execute() {
		if(DoodleTcpServer.allGroups.find(session.getId())==null){
			DoodleTcpServer.allGroups.add(session);
		}
		for(int i=0;i<DoodleTcpServer.doodleUsers.size();i++){
			if(friName.equals(DoodleTcpServer.doodleUsers.get(i).getUsername())){
				Channel channel = DoodleTcpServer.allGroups.find(DoodleTcpServer.doodleUsers.get(i).getSessionId());
				if(channel!=null&&channel.isConnected()){
					channel.write(ChannelBuffers.wrappedBuffer(MessageFactory.createSDoodleRsp(userName, res).build()));
				}
			}
		}
	}*/

}
