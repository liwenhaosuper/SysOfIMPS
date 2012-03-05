package com.imps.media.video.core.SocketImpl;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Stack;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.imps.media.video.core.VideoInterface;
import com.imps.media.video.core.SocketImpl.SocketVideo.InData;
import com.yz.net.Configure;
import com.yz.net.IoFuture;
import com.yz.net.IoService;
import com.yz.net.IoSession;
import com.yz.net.expand.IoConnector;
import com.yz.net.impl.IoServerImpl;
/**
 * An easy implement of the SocketCamera class, using the simple socket to transfer data 
 * @author Administrator
 *
 */
public class SocketVideo implements VideoInterface{

	private static final int SOCKET_TIMEOUT = 1000;	
	private  String address;
	private  int port;
	private  Rect bounds;
	private  boolean preserveAspectRatio;
	private  Paint paint = new Paint();
	public   static boolean isServer;
	public   static boolean isConnected;
	private  IoConnector connector;
	public   static  IoSession session; //common used for server and client
	private  IoFuture future;
	private  IoService server;
	private  Configure config ;
	public static Thread thread;
	
	public SocketVideo(String address, int port, int width, int height, boolean preserveAspectRatio,boolean isServer)
	{
		this.address = address;
		this.port = port;
		bounds = new Rect(0, 0, width, height);
		this.preserveAspectRatio = preserveAspectRatio;		
		paint.setFilterBitmap(true);
		paint.setAntiAlias(true);
		this.isServer = isServer;
		isConnected = false;
		thread = new Thread()
		{
			@Override
			public void run()
			{
				
			}
		};
	}
	public class InData{
		public long len;
		public byte[] data;
	}
	public static Stack<InData> dataStack;
	
	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return bounds.right;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return  bounds.bottom;
	}

	@Override
	public boolean connect() {
		// TODO Auto-generated method stub
		if(session!=null)
		{
			if(session.isClose()==false)
			    session.close();
		}
		if(!isServer)
		{
			config = new Configure();
			config.setAddress(new InetSocketAddress(address,port));
			config.setProtocolHandler(new SocketProtocolHandler());
			config.setIoHandler(new DataHandler());
	        try {
				connector = new IoConnector();
		        config.start(connector);
		        session = IoConnector.newSession(connector);
		        future  = session.connect();
		        future.await();
		        isConnected = true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
		}
		else{
	    	try{
		        config = new Configure();
		        config.setAddress(new InetSocketAddress("192.168.168.45",1200));
		        config.setProtocolHandler(new SocketProtocolHandler());	
		        config.setIoHandler(new DataHandler());
		        server = new IoServerImpl();
		        config.start(server);
		        
	     }catch(Exception e)
	     {
		    e.printStackTrace();
	     }
		}
		return true;
	}
	@Override
	public boolean isConnect() {
		// TODO Auto-generated method stub
		return isConnected;
	}
	@Override
	public boolean disConnect() {
		// TODO Auto-generated method stub
		if(isServer)
		{
			try {
				server.stop();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			session.close();
		}
		return true;
	}
	@Override
	public boolean getCapture(Canvas canvas) {
		// TODO Auto-generated method stub
		
		return false;
	}
	static public void onDataReceived(byte[] data)
	{
		if(dataStack==null)
			dataStack = new Stack<InData>();
/*		InData inData = new InData();
		inData.len = data.length;
		inData.data = data;
		dataStack.push(inData);*/
	}
	static public void onDisconnect()
	{
		
	}
	@Override
	public boolean setCapture(byte[] data) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean open() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}
	
	public static String getLocalIpAddress() { 
		try { 
		    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
		    { 
		        NetworkInterface intf = en.nextElement(); 
		        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) { 
		            InetAddress inetAddress = enumIpAddr.nextElement(); 
		            if (!inetAddress.isLoopbackAddress()) { 
		                 return inetAddress.getHostAddress().toString(); 
		                 } 
		         } 
		      } 
		 } catch (SocketException ex) { 
		    Log.e(LOG_TAG, ex.toString()); 
		} 
		return null; 
		 
	}

}
