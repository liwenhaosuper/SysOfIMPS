package com.imps.server.handler.baseLogic;

import java.sql.SQLException;
import java.util.HashMap;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import com.imps.server.main.basetype.MessageProcessTask;
import com.imps.server.main.basetype.User;
import com.imps.server.main.basetype.location;
import com.imps.server.main.basetype.userStatus;
import com.imps.server.model.CommandId;
import com.imps.server.model.CommandType;
import com.imps.server.model.IMPSType;


public class FriendListRequest extends MessageProcessTask{
	
	public FriendListRequest(Channel session, IMPSType message)
	{
		super(session, message);
	}
	@Override
	public void execute() {
		String userName = inMsg.getmHeader().get("UserName");
		String resultList = "";
		User[] fri = null;
		try {
			fri = manager.getFriendlistFromDB(userName);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		int lon = 0;
		if(fri==null)
			lon = 0;
		else{
			lon = fri.length;
			for(int i=0;i<lon;i++)
			{
				if(manager.userMap.containsKey(fri[i].getUsername()))
				{
					User usr = manager.userMap.get(fri[i].getUsername());
					fri[i].setStatus(usr.getStatus());
				}
			}
		}
		for(int i=0;i<lon;i++){
			resultList+="{";
			resultList+="UserName:"+fri[i].getUsername();
			resultList+=",Status:"+(fri[i].getStatus()==userStatus.OFFLINE?"OFFLINE":"ONLINE");
			resultList+=",Email:"+fri[i].getEmail();
			location loc = new location();
			loc = manager.getFriendLocation(fri[i].getUsername());
			resultList+=",Time:"+loc.ptime;
			resultList+=",Latitide:"+String.valueOf(loc.x);
			resultList+=",Longitude:"+String.valueOf(loc.y);
			resultList+="},";
		}
		HashMap<String,String> header = new HashMap<String,String>();
		header.put("Command", CommandId.S_FRIENDLIST_REFURBISH_RSP);
		header.put("Result",resultList);
		IMPSType result = new CommandType();
		result.setmHeader(header);
		System.out.println(result.toString());
		session.write(ChannelBuffers.wrappedBuffer(result.MediaWrapper()));
		if(DEBUG) System.out.println("friend list back to client!");
		updateList(userName,true);
	}
	
	public User getAssistor(){
		User user = new User();
		user.setEmail("impsweb@126.com");
		user.setGender(0);
		user.setStatus(userStatus.ONLINE);
		user.setUsername("IMPSAssistor");
		return user;
	}

}
