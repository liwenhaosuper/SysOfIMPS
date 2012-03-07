package com.imps.services.impl;

import org.jboss.netty.buffer.ChannelBuffers;

import com.imps.IMPSDev;
import com.imps.net.handler.MessageFactory;
import com.imps.net.handler.UserManager;

public class P2PVideoService{
	private static boolean DEBUG =IMPSDev.isDEBUG();
	private static String TAG = P2PAudioService.class.getCanonicalName();
	public P2PVideoService(){
		
	}
	public void SendPTPVideoReq(String friName,String ip,int port) {
		if(ServiceManager.getmTcpConn().getChannel().isConnected()){
			ServiceManager.getmTcpConn().getChannel().write(ChannelBuffers.wrappedBuffer(
					MessageFactory.createCPTPVideoReq(UserManager.getGlobaluser().getUsername(), friName, ip, port).build()
					));
		}
	}
	public void SendPTPVideoRsp(String friName,String ip,int port,boolean res) {
		if(ServiceManager.getmTcpConn().getChannel().isConnected()){
			ServiceManager.getmTcpConn().getChannel().write(ChannelBuffers.wrappedBuffer(
					MessageFactory.createCPTPVideoRsp(UserManager.getGlobaluser().getUsername(), friName, ip, port,res).build()
					));
		}
	}
}
