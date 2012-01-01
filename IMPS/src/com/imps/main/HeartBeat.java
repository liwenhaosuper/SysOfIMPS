package com.imps.main;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import android.content.Intent;
import android.util.Log;

import com.imps.activities.Login;
import com.imps.activities.My_Map;
import com.imps.base.InputMessage;
import com.imps.base.MessageFactory;
import com.imps.base.MessageProcessTask;
import com.imps.base.OutputMessage;
import com.imps.base.User;
import com.imps.base.userStatus;
import com.imps.handler.IMService;
import com.imps.handler.LogicHandler;
import com.imps.handler.NetProtocolHandler;
import com.imps.handler.UserManager;
import com.yz.net.Configure;
import com.yz.net.IoSession;
import com.yz.net.expand.IoConnector;


public class HeartBeat extends MessageProcessTask{

	public HeartBeat(IoSession session, InputMessage message)
			throws SQLException {
		super(session, message);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void parse() throws IOException {
		// TODO Auto-generated method stub
		
	} 

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		OutputMessage outMsg = null;
		try {
			//休息一下 20s
			Thread.sleep(20000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(false/*Client.session==null||Client.session.isClose()*/)
		{
			try {
				initialConfig();
				UserManager.getInstance().Login(UserManager.getGlobaluser());
				Log.d("HeartBeat", "trying to reconnect to server");
				UserManager.getInstance().SendFriListReq();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		outMsg = MessageFactory.createCHeartbeatReq();
		User user = UserManager.getGlobaluser();
		if(user==null)
			return;
		
		try {
			long len = user.getUsername().getBytes("gb2312").length;
			//返回用户名
			outMsg.getOutputStream().writeLong(len);
			outMsg.getOutputStream().write(user.getUsername().getBytes("gb2312"));
			//位置个数,设置为1
			outMsg.getOutputStream().writeLong(1);
			//设置时间
			SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String datetime = tempDate.format(new java.util.Date());
			//发送时间
			outMsg.getOutputStream().writeLong(datetime.getBytes("gb2312").length);
			outMsg.getOutputStream().write(datetime.getBytes("gb2312"));
			
			//获取地理数据信息(x,y)    
			double x = 0,y = 0;
			if(My_Map.currentGeoPoint!=null)
			{
				x = My_Map.currentGeoPoint.getLatitudeE6();
				y = My_Map.currentGeoPoint.getLongitudeE6();
			}
			outMsg.getOutputStream().writeDouble(x);
			outMsg.getOutputStream().writeDouble(y);
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//反馈给服务端
		Log.d("HeartBeat","session id:"+session.getId()+user.getUsername()+": heart beat sent~");
		Client.session.write(outMsg);
	}
	
	 public void initialConfig() throws Exception
	    {
	    	Configure config = new Configure();
	        /**
	         * 127.0.0.1换成响应的服务器IP
	         * 1200为对应的端口号
	         */
	        config.setAddress(new InetSocketAddress("59.78.23.73",1200));
	        config.setProtocolHandler(new NetProtocolHandler());
	        config.setIoHandler(new LogicHandler());
	        Client.connector = new IoConnector();
	        config.start(Client.connector);
	        Client.session = IoConnector.newSession(Client.connector);
	        Client.future  = Client.session.connect();
	        Client.future.await();
	        Client.usermanager = UserManager.getInstance();
			//start service
			//startService(new Intent(Login.this,IMService.class));
			Log.d("HeartBeat","net config has finished");
	    }
}
