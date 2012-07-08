package com.imps.server.handler.baseLogic;

import java.sql.SQLException;
import java.util.HashMap;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import com.imps.server.main.basetype.MessageProcessTask;
import com.imps.server.model.CommandId;
import com.imps.server.model.CommandType;
import com.imps.server.model.IMPSType;

public class HeartBeat extends MessageProcessTask{
	public HeartBeat(Channel session, IMPSType inMsg) {
		super(session, inMsg);		
	}
	@Override
	public void execute() {					
		String userName = inMsg.getmHeader().get("UserName");
		String stime = inMsg.getmHeader().get("TimeStamp");
		String LatitudeE6s = inMsg.getmHeader().get("Latitude");
		String LongitudeE6s = inMsg.getmHeader().get("Longitude");
		if(userName==null||stime==null){
			if(DEBUG) System.out.println("illegal heart beat request");
			return;
		}
		int LatitideE6=0, LongitudeE6=0;
		if(LatitudeE6s!=null){
			LatitideE6 = Integer.valueOf(LatitudeE6s);
		}
		if(LongitudeE6s!=null){
			LongitudeE6 = Integer.valueOf(LongitudeE6s);
		}
		updateList(userName,true);
		try {
			manager.updateLocation(userName, stime, LatitideE6/1E6, LongitudeE6/1E6);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(DEBUG) System.out.println("heart beat request done");
	}
}
