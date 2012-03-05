package com.imps.server.net.impl;


import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.imps.server.net.IoSession;
import com.imps.server.net.NetMessage;
import com.imps.server.net.ProtocolHandler;


class ProtocolGroup implements ProtocolHandler {

	
	private int prefixByteNum;
	
	private ArrayList<ProtocolHandler> handlerList = new ArrayList<ProtocolHandler>();

	

	ProtocolGroup(int prefixByteNum) {
		this.prefixByteNum = prefixByteNum;
	}
	

	
	
	public void addHandler(ProtocolHandler handler) {
		if(handler == null) {
			return;
		}
		
		this.handlerList.add(handler);
	}
	

	@Override
	public List<NetMessage> onData(ByteBuffer data, IoSession session) {
		//ByteBuffer readdata = data.asReadOnlyBuffer();
		
		if(data.remaining() < prefixByteNum) {
			return null;
		}
		
	
		int size = handlerList.size();
		for(int i=0; i<size; i++) {
			ProtocolHandler handler = handlerList.get(i);
			if(handler == null) {
				continue;
			}
			
			List<NetMessage> list = handler.onData(data, session);
			if(list != null && list.size() > 0) {
				return list;
			}
		}
		
	/*	ArrayList<NetMessage> list = new ArrayList<NetMessage>();
		list.add(NetMessage.ERROR_MSG);*/
		
		return null;
		
	}
}
