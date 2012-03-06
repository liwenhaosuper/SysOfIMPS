package com.imps.server.manager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import com.imps.server.main.DoodleTcpServer;
import com.imps.server.main.basetype.User;

public class DoodleManager {
	
	private static DoodleManager instance;
	
	public static DoodleManager getInstance(){
		if(instance==null){
			instance = new DoodleManager();
		}
		return instance;
	}
   public List<User> getOnlineFriendList(String userName){
    	List<User> res = new ArrayList<User>();
    	try {
			User []users = UserManager.getInstance().getFriendlistFromDB(userName);
			for(int i=0;i<DoodleTcpServer.doodleUsers.size();i++){
				for(int j=0;j<users.length;j++){
					if(DoodleTcpServer.doodleUsers.get(i).getUsername().equals(users[j].getUsername())){
						res.add(DoodleTcpServer.doodleUsers.get(i));
						break;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
    	return res;
    }
   public void notifyStatus(String userName,boolean online){
	   List<User> res = getOnlineFriendList(userName);
	   for(int i=0;i<res.size();i++){
		   Channel channel = DoodleTcpServer.allGroups.find(res.get(i).getSessionId());
		   if(channel!=null){
			   if(channel.isConnected()){
				   channel.write(ChannelBuffers.wrappedBuffer(MessageFactory.createDoodleStatusNotify(userName,online).build()));
			   }
		   }
	   }
   }
}
