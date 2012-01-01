package com.imps.media.video.core.SocketImpl;

import com.yz.net.IoHandlerAdapter;
import com.yz.net.IoSession;
import com.yz.net.NetMessage;

public class DataHandler extends IoHandlerAdapter{
	@Override
	public void messageReceived(IoSession session, NetMessage msg) {
		if(SocketVideo.isServer)
		{
			SocketVideo.isConnected = true;
			SocketVideo.session = session;
		}
		//deal with the data
		InVideoMessage in = (InVideoMessage)msg;
		switch(in.getType())
		{
		case VideoMsgHeader.OK:
		{
			SocketVideo.onDataReceived(in.getContent());
			break;
		}
			
		case VideoMsgHeader.BYE:
		{
			SocketVideo.onDisconnect();
			break;
		}			
		}
	}
}
