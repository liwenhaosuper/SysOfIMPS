package com.imps.server.main;

import java.io.DataInputStream;
import java.io.IOException;
import java.sql.SQLException;

import com.imps.server.base.InputMessage;
import com.imps.server.base.MessageFactory;
import com.imps.server.base.MessageProcessTask;
import com.imps.server.base.OutputMessage;
import com.imps.server.base.User;
import com.imps.server.base.userStatus;
import com.imps.server.handler.UserManager;
import com.imps.server.net.IoSession;


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
		//OutputMessage outMsg = null;
		//处理地理数据信息
		try {
			try {
				initialize();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//outMsg = MessageFactory.createSHeartbeatRsp();
		//反馈给客户端
		//session.write(outMsg);
	}
	
	public void initialize() throws IOException, SQLException
	{
		UserManager manager = UserManager.getInstance();
		User user = manager.getUser(message.getUserName());
		if(user==null)
		{
			//add the user to the userlist
			user = manager.getUserFromDB(message.getUserName());
			user.setStatus(userStatus.ONLINE);
			//set session id
			user.setSessionId(session.getId());
			if(manager.getUser(user.getUsername())==null)
			      manager.addUser(user);
			else manager.getUser(user.getUsername()).setSessionId(session.getId());			
			//TODO: notify friends
			manager.updateUserStatus(user);
			return;
		}
		//更新信息
		user.lastAccessTime = System.currentTimeMillis();
		
		DataInputStream body = message.getInputStream();
		long datasize = body.readLong();
		for(int i=0;i<datasize;i++)
		{
			long len = body.readLong();
			//时间
			byte[] l_time = new byte[(int)len];
			body.read(l_time);
			String p_time = new String(l_time,"gb2312");
			//坐标
			double x_location = body.readDouble();
			double y_location = body.readDouble();
			if(x_location<=0||y_location<=0)
				continue;
			manager.updateLocation(message.getUserName(), p_time, x_location, y_location);
		}
	}
	
    
}
