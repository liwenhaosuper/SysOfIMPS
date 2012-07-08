package com.imps.server.handler.baseLogic;

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

public class SendPTPAudioRsp extends MessageProcessTask{
	private SocketAddress addr;
	public SendPTPAudioRsp(Channel session, IMPSType message,SocketAddress addr)
	{
		super(session, message);
		this.addr = addr;
	}
	@Override
	public void execute() {

		String userName = inMsg.getmHeader().get("UserName");
		String friendname = inMsg.getmHeader().get("FriendName");
		String results = inMsg.getmHeader().get("Result");
		if(userName==null||friendname==null||results==null){
			if(DEBUG) System.out.println("illegal ptp audio rsp.");
			return;
		}
		updateList(userName,true);
		if(manager.getUserMap().containsKey(friendname)){
	        User fri = manager.getUser(friendname);
	        Channel mysession = IMPSTcpServer.getAllGroups().find(fri.getSessionId());
	        if(mysession==null){
	        	if(DEBUG) System.out.println("illegal audio rsp..");
				return;
	        }
			IMPSType result = new CommandType();
			HashMap<String,String> header = new HashMap<String,String>();
			header.put("Command", CommandId.S_PTP_AUDIO_RSP);
			header.put("FriendName",userName);
			header.put("Result",results);
			result.setmHeader(header);
			mysession.write(ChannelBuffers.wrappedBuffer(result.MediaWrapper()));
	        if(DEBUG) System.out.println("ptp audio rsp sent...");
		}else{
			IMPSType result = new CommandType();
			HashMap<String,String> header = new HashMap<String,String>();
			//TODO: Bug
			header.put("Command", CommandId.S_PTP_AUDIO_RSP);
			header.put("FriendName",friendname);
			header.put("Result","Fail");
			result.setmHeader(header);
			session.write(ChannelBuffers.wrappedBuffer(result.MediaWrapper()));
			if(DEBUG) System.out.println("ptp audio rsp send failed: user offline...");
		}
	
	}
}
