
/*
 * Author: liwenhaosuper
 * Date: 2011/5/18
 * Description:
 *     main class for the server project
 */

package com.imps.server.main;

import java.net.InetSocketAddress;


import com.imps.server.handler.LogicHandler;
import com.imps.server.handler.NetProtocolHandler;
import com.imps.server.net.Configure;
import com.imps.server.net.IoService;
import com.imps.server.net.impl.IoServerImpl;

public class ServerBoot {
 
	   public static IoService server;
	   public static void main(String[] args) {
			
	    	try{
			        Configure config = new Configure();
			        config.setAddress(new InetSocketAddress("192.168.120.106",1200));
			        config.setProtocolHandler(new NetProtocolHandler());
			        config.setIoHandler(new LogicHandler());
			
			        server = new IoServerImpl();
			        config.start(server);
			        
			        while(true)
			        {
			        	Thread.sleep(1000);
			        }
			        
		     }catch(Exception e)
		    {
			    e.printStackTrace();
		    }
	  
	    }
	}