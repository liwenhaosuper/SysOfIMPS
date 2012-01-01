package com.imps.server.main;

import java.io.DataInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;

import com.imps.server.base.InputMessage;
import com.imps.server.base.MessageFactory;
import com.imps.server.base.MessageProcessTask;
import com.imps.server.base.OutputMessage;
import com.imps.server.base.User;
import com.imps.server.base.userStatus;
import com.imps.server.handler.UserManager;
import com.imps.server.net.IoFuture;
import com.imps.server.net.IoService;
import com.imps.server.net.IoSession;

public class SendAudio extends MessageProcessTask{
	
	//username, data
	public static HashMap<String,HashMap<String,LinkedList<byte[]> > > userMapData = 
		      new HashMap<String,HashMap<String,LinkedList<byte[]> > >();
	public SendAudio(IoSession session, InputMessage message)
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
		UserManager manager;
		try {
			manager = UserManager.getInstance();
			User user = manager.getUser(message.getUserName());
			if(user==null)
			{
				user = manager.getUserFromDB(message.getUserName());
				user.setStatus(userStatus.ONLINE);
				manager.addUser(user);
			}
			DataInputStream inMsg = message.getInputStream();
			//get the friend name
			long len = inMsg.readLong();
			byte[] fribyte = new byte[(int)len];
			inMsg.read(fribyte);
			String friendname = new String(fribyte,"gb2312");
			//get the message
			len = inMsg.readLong();
			if(len<=0)
				return;
			byte[] audioData = new byte[(int)len];
			inMsg.read(audioData);
			if(audioData.length==2&&audioData[0]=='K'&&audioData[1]=='O')
			{
				HashMap<String,LinkedList<byte[]> > userdatas = userMapData.get(message.getUserName());
				LinkedList<byte[]> datas = userdatas.get(friendname);
				if(datas==null)
					return;
				//获取时间
				SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String datetime = tempDate.format(new java.util.Date());
				
				//将username添加入usermap之中
				User curuser = manager.getUser(message.getUserName());
				if(curuser==null)
				{
					//从数据库中添加进来
					curuser = manager.getUserFromDB(message.getUserName());
					if(curuser==null)
					{
						System.out.println(" user is not exist!");
						return;
					}
					curuser.setSessionId(session.getId());
					manager.getUserMap().putIfAbsent(message.getUserName(), curuser);
				}
				//设置session
				if(curuser.getSessionId()==-1)
					curuser.setSessionId(session.getId());
				
				if(manager.getUserMap().containsKey(friendname))
				{
					//向好友发送信息
					//获取好友的session
			        IoService myserver = ServerBoot.server;
			        User fri = manager.getUser(friendname);
			        IoSession mysession = myserver.getIoSession(fri.getSessionId());
			       // OutputMessage;
			        int size = datas.size();
			        for(int i=0;i<size;i++)
			        {
			        	OutputMessage remsg = MessageFactory.createSAudioRsp(message.getUserName(), datas.removeFirst(), datetime);
				        IoFuture iof = mysession.write(remsg);
				        if(iof.isCannel()||iof.isComplete()||iof.isComplete())
				        {
				        	System.out.println(" send msg to "+friendname+" failed!");
				        }
				        try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			        }
					byte[] last = new byte[2];
					last[0]  = 'K';
					last[1]  = 'O';
					OutputMessage remsg = MessageFactory.createSAudioRsp(message.getUserName(), last, datetime);
			        mysession.write(remsg);
			        System.out.println(" send msg to "+friendname+" successfully!");
				}
				else{
					//该好友不在线
				    System.out.println(" user is now offline and could not sent msg to him~");
					OutputMessage remsg = MessageFactory.createErrorMsg();
					remsg.getOutputStream().writeInt(5);
					session.write(remsg);
				}
				
			}
			else{
				HashMap<String,LinkedList<byte[]> > userdatas = userMapData.get(message.getUserName());
				if(userdatas==null)
				{
					userdatas = new HashMap<String,LinkedList<byte[]> >();
					userdatas.put(friendname, new LinkedList<byte[]>());
					userMapData.put(message.getUserName(), userdatas);
					userdatas = userMapData.get(message.getUserName());
				}				
				LinkedList<byte[]> datas = userdatas.get(friendname);
				if(datas!=null)
				{
					datas.add(audioData);
				}
				else{
					datas = new LinkedList<byte[]>();
					datas.add(audioData);
					userdatas.put(friendname, datas);
				}				
			}

			
			
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}catch (IOException e2)
		{
			e2.printStackTrace();
		}


	}

}
