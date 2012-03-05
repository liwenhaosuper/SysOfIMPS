package com.imps.main;


import java.net.InetSocketAddress;


import com.imps.base.User;
import com.imps.handler.LogicHandler;
import com.imps.handler.NetProtocolHandler;
import com.imps.handler.UserManager;
import com.yz.net.Configure;
import com.yz.net.IoFuture;
import com.yz.net.IoSession;
import com.yz.net.expand.IoConnector;


public class Client {
	
	public static IoConnector connector;
	public static IoSession session;
	public static IoFuture future;
	public static UserManager usermanager ;
	public static void main(String[] args){
		
		try {
	        Configure config = new Configure();
	        /**
	         * 127.0.0.1������Ӧ�ķ�����IP
	         * 1200Ϊ��Ӧ�Ķ˿ں�
	         */
	        config.setAddress(new InetSocketAddress("127.0.0.1",1200));
	        config.setProtocolHandler(new NetProtocolHandler());
	        config.setIoHandler(new LogicHandler());
	        connector = new IoConnector();
	        config.start(connector);
	        session = IoConnector.newSession(connector);
	        future  = session.connect();
	        future.await();
	        //ѭ������
	        usermanager = UserManager.getInstance();
	        User user = new User();
	        user.setEmail("liwenhaosuper@126.com");
	        user.setGender(0);
	        user.setPassword("li");
	        user.setUsername("li");
	        usermanager.Login(user);
	        usermanager.SendMsg("l111i", "���쿴��Ӱ������~");
	        
	        while(true)
	        {
	        	//new HeartBeat(session,null).execute();
	        	Thread.sleep(5000);
	        }
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}