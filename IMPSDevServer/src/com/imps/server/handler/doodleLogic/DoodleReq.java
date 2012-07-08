package com.imps.server.handler.doodleLogic;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;

import com.imps.server.main.DoodleTcpServer;
import com.imps.server.main.basetype.MessageProcessTask;
import com.imps.server.main.basetype.User;
import com.imps.server.manager.MessageFactory;
import com.imps.server.manager.UserManager;

public class DoodleReq/* extends MessageProcessTask*/{
	
/*	private String userName = "";
	private List<String> friendList = new ArrayList<String>();
	public DoodleReq(Channel session, ChannelBuffer inMsg) {
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
		if(!DoodleTcpServer.roomGroups.containsKey(userName)){
			//create a doodle room
			ChannelGroup group = new DefaultChannelGroup();
			group.add(session);
			if(DoodleTcpServer.allGroups.find(session.getId())==null){
				DoodleTcpServer.allGroups.add(session);
			}
			DoodleTcpServer.roomGroups.put(userName, group);
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
					System.out.println("Server Doodle add..."+user.getUsername());
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
				System.out.println("Server Doodle add..."+user.getUsername());
			}
		}
		int sz = inMsg.readInt();
		for(int i=0;i<sz;i++){
			len = inMsg.readInt();
			nm = new byte[len];
			inMsg.readBytes(nm);
			try {
				friendList.add(new String(nm,"gb2312"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return;
			}
		}
	}

	@Override
	public void execute() {
		for(int i=0;i<friendList.size();i++){
			String friNm = friendList.get(i);
			System.out.println("check:"+friNm);
			for(int j=0;j<DoodleTcpServer.doodleUsers.size();j++){
				if(DoodleTcpServer.doodleUsers.get(j).getUsername().equals(friNm)){
					Channel channel = DoodleTcpServer.allGroups.find(DoodleTcpServer.doodleUsers.get(j).getSessionId());
					if(channel!=null&&channel.isConnected()){
						channel.write(ChannelBuffers.wrappedBuffer(MessageFactory.createSDoodleReq(userName).build()));
						System.out.println("Server Doodle req sent..."+friNm);
					}else{
						System.out.println("Server Doodle req sent fail..."+friNm);
					}
					break;
				}
			}
		}
	}
*/
}
