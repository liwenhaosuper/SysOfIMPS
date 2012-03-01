package com.imps.server.handler.doodleLogic;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;

import com.imps.server.main.DoodleTcpServer;
import com.imps.server.main.basetype.MessageProcessTask;
import com.imps.server.main.basetype.OutputMessage;
import com.imps.server.manager.MessageFactory;

public class DoodleData extends MessageProcessTask{

	private String userName="";
	private String roomMaster="";
	public DoodleData(Channel session, ChannelBuffer inMsg) {
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
			roomMaster = new String(nm,"gb2312");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		}
		
	}

	@Override
	public void execute() {
		OutputMessage outMsg = MessageFactory.createDoodleData(userName);
		try {
			outMsg.getOutputStream().writeInt(inMsg.readInt());
			outMsg.getOutputStream().writeFloat(inMsg.readFloat());
			outMsg.getOutputStream().writeFloat(inMsg.readFloat());
		} catch (IOException e) {
			e.printStackTrace();
		}
		ChannelGroup group = DoodleTcpServer.roomGroups.get(roomMaster);
		if(group!=null){
			group.write(ChannelBuffers.wrappedBuffer(outMsg.build()));
		}
	}

}
