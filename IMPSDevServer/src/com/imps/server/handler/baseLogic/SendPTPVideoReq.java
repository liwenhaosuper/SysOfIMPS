package com.imps.server.handler.baseLogic;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import com.imps.server.main.IMPSTcpServer;
import com.imps.server.main.basetype.MessageProcessTask;
import com.imps.server.main.basetype.User;
import com.imps.server.model.CommandId;
import com.imps.server.model.CommandType;
import com.imps.server.model.IMPSType;

public class SendPTPVideoReq extends MessageProcessTask{
	private SocketAddress addr;
	public SendPTPVideoReq(Channel session, IMPSType inMsg,SocketAddress addr) {
		super(session, inMsg);
		this.addr = addr;
	}
	@Override
	public void execute() {

		String userName = inMsg.getmHeader().get("UserName");
		String friendname = inMsg.getmHeader().get("FriendName");
		String privateIp = inMsg.getmHeader().get("PrivateIp");
		String privatePort = inMsg.getmHeader().get("PrivatePort");
		if(userName==null||friendname==null||privateIp==null||privatePort==null){
			if(DEBUG) System.out.println("illegal ptp video request.");
			return;
		}
		updateList(userName,true);
		if(manager.getUserMap().containsKey(friendname)){
	        User fri = manager.getUser(friendname);
	        Channel mysession = IMPSTcpServer.getAllGroups().find(fri.getSessionId());
	        if(mysession==null){
	        	if(DEBUG) System.out.println("illegal video audio request..");
				return;
	        }
			IMPSType result = new CommandType();
			HashMap<String,String> header = new HashMap<String,String>();
			header.put("Command", CommandId.S_PTP_VIDEO_REQ);
			header.put("FriendName",userName);
			header.put("PrivateIp",privateIp);
			header.put("PrivatePort",privatePort);
			header.put("PublicIp",((InetSocketAddress)addr).getAddress().getHostAddress());
			header.put("PublicPort",String.valueOf(((InetSocketAddress)addr).getPort()));
			result.setmHeader(header);
			mysession.write(ChannelBuffers.wrappedBuffer(result.MediaWrapper()));
	        if(DEBUG) System.out.println("ptp video req sent...");
		}else{
			IMPSType result = new CommandType();
			HashMap<String,String> header = new HashMap<String,String>();
			//TODO: Bug
			header.put("Command", CommandId.S_PTP_VIDEO_REQ);
			header.put("FriendName",friendname);
			result.setmHeader(header);
			session.write(ChannelBuffers.wrappedBuffer(result.MediaWrapper()));
			if(DEBUG) System.out.println("ptp video req send failed: user offline...");
		}
	
	}

}
