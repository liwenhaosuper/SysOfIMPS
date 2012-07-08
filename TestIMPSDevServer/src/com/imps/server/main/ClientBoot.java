package com.imps.server.main;

import java.util.HashMap;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.group.ChannelGroup;

import com.imps.server.model.CommandId;
import com.imps.server.model.CommandType;
import com.imps.server.model.IMPSType;


public class ClientBoot {
	private static String ip = "127.0.0.1";
	private static int tcpPort = 1200;
	private static int udpPort = 1300;
	private static int doodlePort = 1400;
	private static int httpPort = 1500;
	private static ChannelGroup allGroups;
    public static void main(String argv[]) {
    	ConnectionService connservice = new ConnectionService(ip,tcpPort);
    	connservice.startTcp();
    	loginTest();
    }
    public static void loginTest(){
		if(ConnectionService.getChannel()!=null&&ConnectionService.getChannel().isConnected()){
			HashMap<String,String> header = new HashMap<String,String>();
			ChannelBuffer buff = null;
			/*while(true){*/
				header = new HashMap<String,String>();
				header.put("Command", CommandId.C_FRIENDLIST_REFURBISH_REQ);
				header.put("UserName","liwenhaosuper");
				header.put("FriendName","liwenhao");
				IMPSType result2 = new CommandType();
				result2.setmHeader(header);
				buff = ChannelBuffers.wrappedBuffer(result2.MediaWrapper());
				ConnectionService.getChannel().write(buff);
				System.out.println("req sent...");
			/*}*/
			
		}
    }
    
}
