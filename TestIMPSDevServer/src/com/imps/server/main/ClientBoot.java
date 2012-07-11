package com.imps.server.main;

import java.util.HashMap;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.group.ChannelGroup;

import com.imps.server.model.CommandId;
import com.imps.server.model.CommandType;
import com.imps.server.model.IMPSType;
import com.imps.server.model.TextMedia;


public class ClientBoot {
	private static String ip = "127.0.0.1";
	private static int tcpPort = 1200;
	private static int udpPort = 1300;
	private static int doodlePort = 1400;
	private static int httpPort = 1500;
	private static ChannelGroup allGroups;
	private static boolean DEBUG = false;
    public static void main(String argv[]) {
    	if(DEBUG){
    		String res = "{s,ss},,{a,aaa},{ddd},";
    		String[] r = res.split("\\{[^}]*\\}");
    		for(int i=0;i<r.length;i++){
    			if(r[i].equals("")){
    				continue;
    			}
    			System.out.println(r[i]);
    		}
    		return;
    	}
    	ConnectionService connservice = new ConnectionService(ip,tcpPort);
    	connservice.startTcp();
    	loginTest();
    	getFriendListTest();
    	try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	sendMessageTest();
    }
    public static void sendMessageTest(){
		if(ConnectionService.getChannel()!=null&&ConnectionService.getChannel().isConnected()){
			HashMap<String,String> header = new HashMap<String,String>();
			ChannelBuffer buff = null;	
			header = new HashMap<String,String>();
			header.put("Command", CommandId.C_SEND_MSG);
			header.put("UserName","lili");
			header.put("FriendName","li");
			byte[] content = "hello world".getBytes();
			IMPSType result2 = new TextMedia();
			result2.setmHeader(header);
			result2.setContent(content);
			buff = ChannelBuffers.wrappedBuffer(result2.MediaWrapper());
			ConnectionService.getChannel().write(buff);
			System.out.println("sendMessageTest: req sent...");		
		}
    }
    public static void loginTest(){
		if(ConnectionService.getChannel()!=null&&ConnectionService.getChannel().isConnected()){
			HashMap<String,String> header = new HashMap<String,String>();
			ChannelBuffer buff = null;	
			header = new HashMap<String,String>();
			header.put("Command", CommandId.C_LOGIN_REQ);
			header.put("UserName","lili");
			header.put("Password","lili");
			IMPSType result2 = new CommandType();
			result2.setmHeader(header);
			buff = ChannelBuffers.wrappedBuffer(result2.MediaWrapper());
			ConnectionService.getChannel().write(buff);
			System.out.println("loginTest :req sent...");		
		}
    }
    public static void getFriendListTest(){
		if(ConnectionService.getChannel()!=null&&ConnectionService.getChannel().isConnected()){
			HashMap<String,String> header = new HashMap<String,String>();
			ChannelBuffer buff = null;	
			header = new HashMap<String,String>();
			header.put("Command", CommandId.C_FRIENDLIST_REFURBISH_REQ);
			header.put("UserName","liwenhaosuper");
			header.put("FriendName","liwenhao");
			IMPSType result2 = new CommandType();
			result2.setmHeader(header);
			buff = ChannelBuffers.wrappedBuffer(result2.MediaWrapper());
			ConnectionService.getChannel().write(buff);
			System.out.println("getFriendListTest:req sent...");		
		}
    }
    
}
