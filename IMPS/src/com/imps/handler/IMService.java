package com.imps.handler;


import java.io.DataInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.imps.activities.AudioChat;
import com.imps.activities.ChatView;
import com.imps.activities.SystemMsg;
import com.imps.activities.VideoContact2;
import com.imps.audio.Track;
import com.imps.base.CommandId;
import com.imps.base.InputMessage;
import com.imps.base.MessageProcessTask;
import com.imps.base.User;
import com.imps.base.userStatus;
import com.imps.main.Client;
import com.imps.util.ContactsManagerDbAdater;
import com.yz.net.IoSession;

public class IMService extends Service {
	
	public NotificationManager mNM;
	public static Thread thread;
	public static ContactsManagerDbAdater contactsManagerDbAdapter;
	@Override
	public void onCreate()
	{
		System.out.println(" service has been started!");
        contactsManagerDbAdapter=new ContactsManagerDbAdater(this);
		mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		thread = new Thread(){
			@Override
			public void run(){
				while(true){
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				while(!intentMessage.empty())
				{
					Intent topIntent = intentMessage.pop();
					sendBroadcast(topIntent);
					System.out.println("!!!!!message broadcast has been sent!!!!!");
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				while(!notifyMessage.empty())
				{
					notify tnty = notifyMessage.pop();
					showNotification(tnty.friname,tnty.msg,tnty.stime);
					System.out.println("!!!!!message broadcast has been sent!!!!!");
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				while(!addFriNotify.empty())
				{
					AddFriNotify nty = addFriNotify.pop();
					showAddFriNotification(nty.friname,nty.type,nty.res);
					System.out.println("!!!!!add friend broadcast has been sent!!!!!");
				}
				while(!errorMsg.empty())
				{
					String message = errorMsg.pop();
					Intent i = new Intent();
					i.setAction("error_msg");
					i.putExtra("message", message);
					sendBroadcast(i);
					System.out.print("!!!!!error message broadcast has been sent!!!!!");
				}
				while(!audioMessage.empty())
				{
					audiomsg msg = audioMessage.pop();
					Intent i = new Intent();
					i.setAction("ptpaudio_req");
					i.putExtra("fUsername", msg.friname);
					i.putExtra("ip", msg.ip);
					i.putExtra("port", msg.port);
					Log.d("IMService","audioMessage: msg request ip is "+ msg.ip);
					showAudioNotification(msg.friname,msg.ip,msg.port);
					System.out.println("!!!!!audio req broadcast has been sent!!!!!");
				}
				while(!videoMessage.empty())
				{
					audiomsg msg = videoMessage.pop();
					Intent i = new Intent();
					i.setAction("ptpvideo_req");
					i.putExtra("fUsername", msg.friname);
					i.putExtra("ip", msg.ip);
					i.putExtra("port", msg.port);
					Log.d("IMService","videoMessage: msg request ip is "+ msg.ip);
					showVideoNotification(msg.friname,msg.ip,msg.port);
					System.out.println("!!!!!video req broadcast has been sent!!!!!");
				}
				}
			}
			
		};
		thread.start();
	}
	@Override
	public int onStartCommand(Intent intent,int flags,int startId)
	{
		return START_STICKY;
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}
	
	public  void exit(){
		if(Client.session==null||Client.session.isClose())
		{
			this.stopSelf();
			return;
		}
		Client.session.close();
		this.stopSelf();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 *  I have to use this method because it conflicts with IO I have implemented.
	 *  Having tried for a long time, this method may be not the best, but it works.
	 */
	public static Stack<Intent> intentMessage = new Stack<Intent>();
	public class notify{
		public String friname;
		public String msg;
		public String stime;
	}
	public static Stack<audiomsg> audioMessage = new Stack<audiomsg>();
	public static Stack<audiomsg> videoMessage = new Stack<audiomsg>();
	public class audiomsg
	{
		public String friname;
		public String ip;
		public int port;
	}
	public static Stack<notify> notifyMessage = new Stack<notify>();
	public class AddFriNotify{
		public String friname;
		public int type; //0:req;1:rsq
		public int res;
	}
	public static Stack<AddFriNotify> addFriNotify = new Stack<AddFriNotify>();
	public static Stack<String> errorMsg = new Stack<String>();
    /**
     * this function will be toggled when new message is received
     * @param cmdType: the message type,heart beat type is not in consideration
     * @param friName: the friend name.If message type is error type, the parameter is set to NULL
     * @param Msg: the body message. If message type is friend request type or error type, the content will be integer
     */
	public void messageReceived(byte cmdType,String friName,String Msg,String stime)
	{
		//TO BE CONTINUED
		if(cmdType==CommandId.S_ADDFRIEND_REQ)
		{
			//Add friend request
			Intent i = new Intent("add_fri_req");
			i.putExtra("fUsername",friName);
			intentMessage.add(i);
            AddFriNotify anty = new AddFriNotify();
            anty.friname = friName;
            anty.type = 0;
            anty.res = 0;
            addFriNotify.add(anty);
		}
		else if(cmdType == CommandId.S_ADDFRIEND_RSP)
		{
			//Add friend response
			Intent i = new Intent("add_fri_rsp");
			i.putExtra("fUsername", friName);
			i.putExtra("result", Integer.parseInt(Msg));
			AddFriNotify anty = new AddFriNotify();
			anty.friname = friName;
			anty.res = Integer.parseInt(Msg);
			anty.type = 1;
			addFriNotify.add(anty);
		}
		else if(cmdType == CommandId.S_ERROR)
		{
			//Error message
			int res = Integer.parseInt(Msg);
			Intent i = new Intent();
			i.setAction("error_msg");
			String message = "";
			if(res==1)
			{
				System.out.println("the username is already exists~");
				message = "���û����Ѿ�����";
			}
			else if(res==2)
			{
				System.out.println("the username is not exists~"); 
				message ="���û������";
			}
			else if(res==3)
			{
				System.out.println("the user is not in state~");
				message = "���û�������";
			}
			else if(res==4)
			{
				System.out.println("you have already logged on~");
				message = "���Ѿ���¼";
			}
			else if(res==5)
			{
				System.out.println("user is now offline~");
				message = "���û���ǰ��������״̬";
			}
			else if(res ==6)
			{
				System.out.println(" username or password is not valid!");
				message = "�û�������������";
			}
			i.putExtra("message",message);
			intentMessage.add(i);
			
		}
		else if(cmdType == CommandId.S_FRIENDLIST_REFURBISH_RSP)
		{
			//full friends list
			UserManager.AllFriList = friendList;
			int flen = friendList.size();
			for(int tmp=0;tmp<flen;tmp++)
			{
				long rid = contactsManagerDbAdapter.insertDataToContacts(friendList.get(tmp));
			    Log.d("IMService", "db result is "+rid+" and group is "+friendList.get(tmp).getGroupName());
			}
			//TODO:Update the map
			Intent i = new Intent();
			i.setAction("fri_list");
			intentMessage.add(i);
		}
		else if(cmdType == CommandId.S_SEND_MSG)
		{
			System.out.println("message received:"+ friName+" says: "+ Msg);
			//friend message has been received
			UserManager.UnReadMessages.put(friName, Msg);
			Intent tintent = new Intent("new_message");
			tintent.putExtra("fUsername", friName);
			String rmsg =  friName + "|" + stime + "|" + Msg;
			tintent.putExtra("message",rmsg);
			tintent.putExtra("time", stime);
			//sent broadcast
			//bug here!!!
			//sendBroadcast(tintent);
			intentMessage.add(tintent);
			contactsManagerDbAdapter.insertDataToMessage(friName, rmsg, stime);
			
			
			if((UserManager.activeFriend==null||!UserManager.activeFriend.equals(friName))&&UserManager.CurSessionFriList.containsKey(friName))
			{
				UserManager.getInstance();
				List<String> msgbox = UserManager.CurSessionFriList.get(friName);
				msgbox.add(friName + "|" + stime + "|" + Msg);
			}
			else if(UserManager.activeFriend==null||!UserManager.activeFriend.equals(friName)){
				List<String> newmsgbox = new ArrayList<String>();
				newmsgbox.add(friName + "|" + stime + "|" + Msg);
				UserManager.getInstance();
				UserManager.CurSessionFriList.put(friName, newmsgbox);
			}
			if(UserManager.activeFriend==null||!UserManager.activeFriend.equals(friName))
			{
				notify tnty = new notify();
				tnty.friname = friName;
				tnty.msg = Msg;
				tnty.stime = stime;
				notifyMessage.add(tnty);
			}
			//TODO: add GUI service notify here
		}
		else if(cmdType ==CommandId.S_AUDIO_RSP)
		{
			Track track = new Track();
			int cnt = audioData.size();
            for(int i=0;i<cnt;i++)
            {
            	track.data.add(audioData.getFirst());
            }
            track.run();
			System.out.println("Audio message received");
			Intent tintent = new Intent("new_audio");
			tintent.putExtra("fUsername", friName);
			tintent.putExtra("data", audioData);
			tintent.putExtra("time", stime);
			intentMessage.add(tintent);
			if((UserManager.activeFriend==null||!UserManager.activeFriend.equals(friName))&&UserManager.CurSessionFriList.containsKey(friName))
			{
				UserManager.getInstance();
				List<String> msgbox = UserManager.CurSessionFriList.get(friName);
				msgbox.add("��Ƶ" + "|" + stime + "|" + friName+" ��������һ����Ƶ");
			}
			else if(UserManager.activeFriend==null||!UserManager.activeFriend.equals(friName)){
				List<String> newmsgbox = new ArrayList<String>();
				newmsgbox.add("��Ƶ" + "|" + stime + "|" + friName+" ��������һ����Ƶ");
				UserManager.getInstance();
				UserManager.CurSessionFriList.put(friName, newmsgbox);
			}
			if(UserManager.activeFriend==null||!UserManager.activeFriend.equals(friName))
			{
				notify tnty = new notify();
				tnty.friname = "��Ƶ";
				tnty.msg = friName+" ��������һ����Ƶ.";
				tnty.stime = stime;
				notifyMessage.add(tnty);
			}
		}
		else if(cmdType == CommandId.S_STATUS_NOTIFY)
		{
			//friend status notified
			UserManager.getInstance().updateUserStatus(friName, status);
			Intent i = new Intent();
			i.setAction("status_notify");
			i.putExtra("fUsername", friName);
			i.putExtra("status", status);
			intentMessage.add(i);
			System.out.println(" status broadcast has been sent!");
			//TODO: you could add more operations here
		}
		else if(cmdType == CommandId.S_REGISTER)
		{
			Intent i = new Intent();
			i.setAction("register_success");
			intentMessage.add(i);
		}
		else if(cmdType == CommandId.S_PTP_AUDIO_REQ)
		{
			Intent i = new Intent();
			i.setAction("ptpaudio_req");
			i.putExtra("fUsername", friName);
			i.putExtra("ip", Msg);
			i.putExtra("port", Integer.parseInt(stime));
			intentMessage.add(i);
			Log.d("IMService", "request handler- intentmessage added: ip is "+Msg);
			if((UserManager.activeFriend==null||!UserManager.activeFriend.equals(friName))&&UserManager.CurSessionFriList.containsKey(friName))
			{
				UserManager.getInstance();
				List<String> msgbox = UserManager.CurSessionFriList.get(friName);
				msgbox.add("��Ƶͨ��" + "|" + " " + "|" + friName+"�����������������");
			}
			else if(UserManager.activeFriend==null||!UserManager.activeFriend.equals(friName)){
				List<String> newmsgbox = new ArrayList<String>();
				newmsgbox.add("��Ƶͨ��" + "|" + " " + "|" + friName+"�����������������");
				UserManager.getInstance();
				UserManager.CurSessionFriList.put(friName, newmsgbox);
			}
			if(UserManager.activeFriend==null||!UserManager.activeFriend.equals(friName))
			{
/*				notify tnty = new notify();
				tnty.friname = "��Ƶͨ��";
				tnty.msg = friName+" �����������������.";
				tnty.stime = stime;
				notifyMessage.add(tnty);*/
			}
			audiomsg msg = new audiomsg();
			msg.friname = friName;
			msg.ip = Msg;
			msg.port = Integer.parseInt(stime);
			audioMessage.add(msg);
			Log.d("IMService", "request handler- audiomessage added: ip is "+Msg);
		}
		else if(cmdType == CommandId.S_PTP_AUDIO_RSP)
		{
			Intent i = new Intent();
			i.setAction("ptpaudio_rsp");
			i.putExtra("fUsername", friName);
			if("".equals(Msg))
			{
				i.putExtra("result", 0);
				i.putExtra("ip", "");
				i.putExtra("port", 1300);
			}
			else
			{
				i.putExtra("result", 1);
				i.putExtra("ip", Msg);
				i.putExtra("port", Integer.parseInt(stime));
			}
			
			
			intentMessage.add(i);
			if((UserManager.activeFriend==null||!UserManager.activeFriend.equals(friName))&&UserManager.CurSessionFriList.containsKey(friName))
			{
				UserManager.getInstance();
				List<String> msgbox = UserManager.CurSessionFriList.get(friName);
				if("".equals(Msg))
				{
					msgbox.add("��Ƶͨ��" + "|" + " " + "|" + friName+"�ܾ���������������");
				}
				else{
					msgbox.add("��Ƶͨ��" + "|" + " " + "|" + friName+"������������������");
				}
				
			}
			else if(UserManager.activeFriend==null||!UserManager.activeFriend.equals(friName)){
				List<String> newmsgbox = new ArrayList<String>();
				if("".equals(Msg))
				{
					newmsgbox.add("��Ƶͨ��" + "|" + " " + "|" + friName+"�ܾ���������������");
				}
				else{
					newmsgbox.add("��Ƶͨ��" + "|" + " " + "|" + friName+"������������������");
				}
				UserManager.getInstance();
				UserManager.CurSessionFriList.put(friName, newmsgbox);
			}
			if(UserManager.activeFriend==null||!UserManager.activeFriend.equals(friName))
			{
/*				notify tnty = new notify();
				tnty.friname = "��Ƶͨ��";
				tnty.msg = friName+" �ܾ���������������.";
				tnty.stime = stime;
				notifyMessage.add(tnty);*/
			}
		}
		else if(cmdType == CommandId.S_PTP_VIDEO_REQ)
		{
			Intent i = new Intent();
			i.setAction("ptpvideo_req");
			i.putExtra("fUsername", friName);
			i.putExtra("ip", Msg);
			i.putExtra("port", Integer.parseInt(stime));
			intentMessage.add(i);
			Log.d("IMService", "request handler- intentmessage added: ip is "+Msg);
			if((UserManager.activeFriend==null||!UserManager.activeFriend.equals(friName))&&UserManager.CurSessionFriList.containsKey(friName))
			{
				UserManager.getInstance();
				List<String> msgbox = UserManager.CurSessionFriList.get(friName);
				msgbox.add("��Ƶͨ��" + "|" + " " + "|" + friName+"�������Ƶ��������");
			}
			else if(UserManager.activeFriend==null||!UserManager.activeFriend.equals(friName)){
				List<String> newmsgbox = new ArrayList<String>();
				newmsgbox.add("��Ƶͨ��" + "|" + " " + "|" + friName+"�������Ƶ��������");
				UserManager.getInstance();
				UserManager.CurSessionFriList.put(friName, newmsgbox);
			}
			if(UserManager.activeFriend==null||!UserManager.activeFriend.equals(friName))
			{
/*				notify tnty = new notify();
				tnty.friname = "��Ƶͨ��";
				tnty.msg = friName+" �����������������.";
				tnty.stime = stime;
				notifyMessage.add(tnty);*/
			}
			audiomsg msg = new audiomsg();
			msg.friname = friName;
			msg.ip = Msg;
			msg.port = Integer.parseInt(stime);
			videoMessage.add(msg);
			Log.d("IMService", "request handler- audiomessage added: ip is "+Msg);
		}
		else if(cmdType == CommandId.S_PTP_VIDEO_RSP)
		{
			Intent i = new Intent();
			i.setAction("ptpvideo_rsp");
			i.putExtra("fUsername", friName);
			if("".equals(Msg))
			{
				i.putExtra("result", 0);
				i.putExtra("ip", "");
				i.putExtra("port", 1300);
			}
			else
			{
				i.putExtra("result", 1);
				i.putExtra("ip", Msg);
				i.putExtra("port", Integer.parseInt(stime));
			}			
			intentMessage.add(i);
			if((UserManager.activeFriend==null||!UserManager.activeFriend.equals(friName))&&UserManager.CurSessionFriList.containsKey(friName))
			{
				UserManager.getInstance();
				List<String> msgbox = UserManager.CurSessionFriList.get(friName);
				if("".equals(Msg))
				{
					msgbox.add("��Ƶͨ��" + "|" + " " + "|" + friName+"�ܾ�����Ƶ��������");
				}
				else{
					msgbox.add("��Ƶͨ��" + "|" + " " + "|" + friName+"��������Ƶ��������");
				}				
			}
			else if(UserManager.activeFriend==null||!UserManager.activeFriend.equals(friName)){
				List<String> newmsgbox = new ArrayList<String>();
				if("".equals(Msg))
				{
					newmsgbox.add("��Ƶͨ��" + "|" + " " + "|" + friName+"�ܾ�����Ƶ��������");
				}
				else{
					newmsgbox.add("��Ƶͨ��" + "|" + " " + "|" + friName+"��������Ƶ��������");
				}
				UserManager.getInstance();
				UserManager.CurSessionFriList.put(friName, newmsgbox);
			}
			if(UserManager.activeFriend==null||!UserManager.activeFriend.equals(friName))
			{
/*				notify tnty = new notify();
				tnty.friname = "��Ƶͨ��";
				tnty.msg = friName+" �ܾ���������������.";
				tnty.stime = stime;
				notifyMessage.add(tnty);*/
			}
		}
		
	}	
	/**
	 * show a notification while this service is running.
	 * bug exists
	 */
	public void showNotification(String friName,String msg,String stime)
	{
		String title = friName + " :"+((msg.length()<5)?msg:msg.substring(0,5))+"...";
		Notification notification = new Notification(com.imps.R.drawable.stat,title,System.currentTimeMillis());
		Intent i = new Intent(this,ChatView.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.putExtra("fUsername", friName);
		i.putExtra("message", friName + "|" + stime + "|" + msg);
		i.putExtra("time", stime);
		//the pendingIntent to launch our activity if the user selects this notification
		PendingIntent contentIntent = PendingIntent.getActivity(this,0,i,0);
		//set the info for the views that show in the notification panel.
		notification.setLatestEventInfo(this, "����"+friName+"������Ϣ", msg, contentIntent);
		notification.flags|=Notification.FLAG_AUTO_CANCEL;
		mNM.notify((friName+msg+stime).hashCode(),notification);
	}
	public void showAudioNotification(String friName,String ip,int port)
	{
		String title = friName+" ������������ͨ��";
		Notification notification = new Notification(com.imps.R.drawable.stat,title,System.currentTimeMillis());
		Intent i = new Intent(this,AudioChat.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.setAction("ptpaudio_req");
		i.putExtra("fUsername", friName);
		i.putExtra("ip", ip);
		i.putExtra("port", port);
		Log.d("IMService","showaudionotification: ip is "+ ip );
		PendingIntent contentIntent = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(this, "������������", title, contentIntent);
		notification.flags|=Notification.FLAG_AUTO_CANCEL;
		mNM.notify(("������������"+title+System.currentTimeMillis()).hashCode(),notification);
	}
	public void showVideoNotification(String friName,String ip,int port)
	{
		String title = friName+" ����������Ƶͨ��";
		Notification notification = new Notification(com.imps.R.drawable.stat,title,System.currentTimeMillis());
		Intent i = new Intent(this,VideoContact2.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.setAction("ptpvideo_req");
		i.putExtra("fUsername", friName);
		i.putExtra("ip", ip);
		i.putExtra("port", port);
		Log.d("IMService","showaudionotification: ip is "+ ip );
		PendingIntent contentIntent = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(this, "��Ƶ��������", title, contentIntent);
		notification.flags|=Notification.FLAG_AUTO_CANCEL;
		mNM.notify(("��Ƶ��������"+title+System.currentTimeMillis()).hashCode(),notification);
	}
	public void showAddFriNotification(String friName,int type,int res)
	{
		String title = friName ;
		Intent i = new Intent(this,SystemMsg.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.putExtra("fUsername", friName);
		i.putExtra("type", type);
		i.putExtra("result", res);
		String brief = "";
		if(type==0)
		{
			title+="���������Ϊ����";
			brief = "�µĺ�������";
			i.setAction("add_fri_req");
		}
		else if(type==1)
		{
			title+="�ظ��������Ӻ�������";
			i.putExtra("result", res);
			brief = " ��������ظ�";
			i.setAction("add_fri_rsq");
			if(res ==1)
			{
				//��������б�
				UserManager.getInstance().SendFriListReq();
			}
		}
		Notification notification = new Notification(com.imps.R.drawable.stat,title,System.currentTimeMillis());
		PendingIntent contentIntent = PendingIntent.getActivity(this,0,i,0);
		notification.setLatestEventInfo(this, brief, title, contentIntent);
		notification.flags|=Notification.FLAG_AUTO_CANCEL;
		mNM.notify((friName+type).hashCode(),notification);
	}
	/**
	 * instant message handler
	 */
	
	/**
	 * add friend request received
	 */
	public class AddFriReq extends MessageProcessTask{

		public AddFriReq(IoSession session, InputMessage message)
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
			DataInputStream body = message.getInputStream();
			long len;
			try {
				len = body.readLong();
				byte[] nm = new byte[(int)len];
				body.read(nm);
				//friend name
				String friname = new String(nm,"gb2312");
				System.out.println(friname+ " wants to add your as his/her friend");
				messageReceived(message.getCmdType(), friname, "1","");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}			
	}
	/**
	 * add friend response
	 */
	public class AddFriRsp extends MessageProcessTask{
		public AddFriRsp(IoSession session, InputMessage message)
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
			DataInputStream body = message.getInputStream();
			long len;
			try {
				len = body.readLong();
				byte[] nm = new byte[(int)len];
				body.read(nm);
				String friname = new String(nm,"gb2312");
				int res = body.readInt();
				System.out.println(friname+ "'s response to your request");
				messageReceived(message.getCmdType(), friname, new Integer(res).toString(),"");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * error message handler
	 */
	public class ErrorMsgHandler extends MessageProcessTask {

		public ErrorMsgHandler(IoSession session, InputMessage message)
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
			DataInputStream body = message.getInputStream();
			//��ȡ��Ϣ����
			try {
				int res = body.readInt();
				if(res==1)
				{
					System.out.println("the username is already exists~");
				}
				else if(res==2)
				{
					System.out.println("the username is not exists~"); 
				}
				else if(res==3)
				{
					System.out.println("the user is not in state~");
				}
				else if(res==4)
				{
					System.out.println("you have already logged on~");
				}
				else if(res==5)
				{
					System.out.println("user is now offline~");
				}
				else if(res ==6)
				{
					System.out.println(" username or password is not valid!");
				}
				messageReceived(message.getCmdType(), "", String.valueOf(res),"");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}

	}
   /**
    * friend list request received
    * 
    */
	public static List<User> friendList;
	public class FriendListRequest extends MessageProcessTask{
		
		
		public FriendListRequest(IoSession session, InputMessage message)
				throws SQLException {
			super(session, message);
			// TODO Auto-generated constructor stub
		}
		@Override
		public void parse() throws IOException {
			// TODO Auto-generated method stub
			DataInputStream body = message.getInputStream();
			friendList = new ArrayList<User>();
			long count = body.readLong();
			for(int i=0;i<count;i++)
			{
				//name
				long len = body.readLong();
				byte[] nm = new byte[(int)len];
				body.read(nm);
				String username = new String(nm);
				//status
				byte status = body.readByte();
				//email
				len = body.readLong();
				byte[] em = new byte[(int)len];
				body.read(em);
				String email = new String(em);
				//time
			    len = body.readLong();
			    byte[] tm = new byte[(int)len];
			    body.read(tm);
			    String strtm = new String(tm);
			    //location
			    double x = 0,y=0;
			    x = body.readDouble();
			    y = body.readDouble();
			    User user = new User();
			    user.setLoctime(strtm);
			    user.setUsername(username);
			    user.setLocX(x);
			    user.setLocY(y);
			    user.setStatus(status);
			    user.setEmail(email);
			    friendList.add(user);
			}
		}
		@Override
		public void execute() {
			// TODO Auto-generated method stub
			messageReceived(message.getCmdType(), "", "","");			
		}
	}
	/**
	 * login response
	 */
	public class MyLogin extends MessageProcessTask{

		public MyLogin(IoSession session,InputMessage message) throws SQLException
		{
	         super(session,message);
		}
		
		@Override
		public void parse() throws IOException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void execute()  {
			// TODO Auto-generated method stub
			try {
				DataInputStream body = message.getInputStream();
				//��ȡ�û���
				long len = body.readLong();
				byte[] nm = new byte[(int) len];			
				body.read(nm);
				String username = new String(nm);
				System.out.println("username:"+username);
				//��ȡ�Ա�
				int gender=body.readInt();
				//��ȡemail
				len = body.readLong();
				byte[] mail = new byte[(int)len];
				body.read(mail);
				String email = new String(mail);
				//�����û�
				User myuser = new User();
				myuser.setEmail(email);
				myuser.setGender(gender);
				myuser.setUsername(username);
				myuser.setStatus(userStatus.ONLINE);
				//�����û�
				UserManager.setGlobaluser(myuser);
				//open the db
				contactsManagerDbAdapter.open();
				//sent broadcast
				Intent i=  new Intent();
				i.setAction("login_success");
				intentMessage.add(i);
				/**
				 * ��½�ɹ�
				 * TODO:�ڴ˴���ӵ�½�ɹ���Ĵ�����Ϣ
				 */
	/*			try {
					new HeartBeat(session,message).run();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/			
			   } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   //	
		}
	}
	/**
	 * register response
	 */
	public class Register extends MessageProcessTask{
		
		private User user = new User();

		public Register(IoSession session, InputMessage message) throws SQLException {
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
	       DataInputStream body = message.getInputStream();
	       int res;
		try {
			res = body.readInt();
		    if(res!=0)
		    {
		        System.out.println("succeed to register!");
		        messageReceived(message.getCmdType(),"","1","");
		    }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		}
	}
	/**
	 * sent message received
	 */
	public class SendMessage extends MessageProcessTask{
		public SendMessage(IoSession session, InputMessage message)
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
			DataInputStream body = message.getInputStream();
			long len;
			try {
				//������
				len = body.readLong();
				byte[] nm = new byte[(int)len];
				body.read(nm);
				String friname = new String(nm,"gb2312");
				//��Ϣ����
				len = body.readLong();
				byte[] msgcnt = new byte[(int)len];
				body.read(msgcnt);
				String msg = new String(msgcnt,"gb2312");
				//����ʱ��
				len = body.readLong();
				byte[] sendtime = new byte[(int)len];
				body.read(sendtime);
				String stime= new String(sendtime,"gb2312");
				System.out.println(friname+"  "+stime+ "  says to you: "+msg);
				messageReceived(message.getCmdType(), friname, msg,stime);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
	}
	
	public static LinkedList<byte[]> audioData;
	/**
	 * send audio data received
	 */
	public class SendAudio extends MessageProcessTask{

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
			DataInputStream body = message.getInputStream();
			long len;
			try {
				//������
				len = body.readLong();
				byte[] nm = new byte[(int)len];
				body.read(nm);
				String friname = new String(nm,"gb2312");
				//��Ϣ����
				len = body.readLong();
				//���ԭ�������
				//audioData.clear();
				byte[]taudioData = new byte[(int)len];
				body.read(taudioData);
				if(taudioData.length!=2||taudioData[0]!='K'||taudioData[1]!='O')
				{
					audioData.add(taudioData);
					body.readLong();
					body.read(new byte[(int)len]);
					return;
				}			
				//����ʱ��
				len = body.readLong();
				byte[] sendtime = new byte[(int)len];
				body.read(sendtime);
				String stime= new String(sendtime,"gb2312");
				messageReceived(message.getCmdType(), friname,"",stime);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		
		}
		
	}
	/**
	 * ������������
	 */
	public class SendPTPAudioReq extends MessageProcessTask{

		public SendPTPAudioReq(IoSession session, InputMessage message)
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
			DataInputStream body = message.getInputStream();
			long len;
			try {
				//������
				len = body.readLong();
				byte[] nm = new byte[(int)len];
				body.read(nm);
				String friname = new String(nm,"gb2312");
				//ip
				len = body.readLong();
				byte[] ipbyte = new byte[(int)len];
				body.read(ipbyte);
				String myip = new String(ipbyte,"gb2312");
				Log.d("IMService", "request received:ip received is "+myip);
				//port 
				int port = body.readInt();
				messageReceived(message.getCmdType(),friname,myip,String.valueOf(port));
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		
	}
	/**
	 * ��Ƶ��������
	 */
	public class SendPTPVideoReq extends MessageProcessTask{

		public SendPTPVideoReq(IoSession session, InputMessage message)
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
			DataInputStream body = message.getInputStream();
			long len;
			try {
				//������
				len = body.readLong();
				byte[] nm = new byte[(int)len];
				body.read(nm);
				String friname = new String(nm,"gb2312");
				//ip
				len = body.readLong();
				byte[] ipbyte = new byte[(int)len];
				body.read(ipbyte);
				String myip = new String(ipbyte,"gb2312");
				Log.d("IMService", "request received:ip received is "+myip);
				//port 
				int port = body.readInt();
				//public ip
				len = body.readLong();
				ipbyte = new byte[(int)len];
				body.read(ipbyte);
				myip = new String(ipbyte,"gb2312");
				Log.d("IMService", "request received:public ip received is "+myip);
				//public port 
				port = body.readInt();
				messageReceived(message.getCmdType(),friname,myip,String.valueOf(port));
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		
	}
	
	
	/**
	 * ����������Ӧ
	 */
	public class SendPTPAudioRsp extends MessageProcessTask
	{

		public SendPTPAudioRsp(IoSession session, InputMessage message)
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
			//��ȡ�������
			Log.d("IMService", "audio rsp message received...");
			DataInputStream body = message.getInputStream();
			long len;
			try {
				len = body.readLong();
				byte[] nm = new byte[(int)len];
				body.read(nm);
				String friname = new String(nm,"gb2312");
				//res
				int res = body.readInt();
				//ip
				len = body.readLong();
				byte[] ipbyte = new byte[(int)len];
				body.read(ipbyte);
				String ip = new String(ipbyte,"gb2312");
				int port = body.readInt();
				Log.d("IMService", "res is "+res+" and ip is "+ip+" and port is "+port);
				if(res==0)
				{
					messageReceived(message.getCmdType(), friname, "","");
				}
				else{
					messageReceived(message.getCmdType(), friname, ip,String.valueOf(port));
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		
	}
	/**
	 * ��Ƶ������Ӧ
	 */
	public class SendPTPVideoRsp extends MessageProcessTask
	{

		public SendPTPVideoRsp(IoSession session, InputMessage message)
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
			//��ȡ�������
			Log.d("IMService", "video rsp message received...");
			DataInputStream body = message.getInputStream();
			long len;
			try {
				len = body.readLong();
				byte[] nm = new byte[(int)len];
				body.read(nm);
				String friname = new String(nm,"gb2312");
				//res
				int res = body.readInt();
				//ip
				len = body.readLong();
				byte[] ipbyte = new byte[(int)len];
				body.read(ipbyte);
				String ip = new String(ipbyte,"gb2312");
				int port = body.readInt();
				//public ip
				len = body.readLong();
				ipbyte = new byte[(int)len];
				body.read(ipbyte);
				ip = new String(ipbyte,"gb2312");
				port = body.readInt();
				Log.d("IMService", "res is "+res+" and ip is "+ip+" and port is "+port);
				if(res==0)
				{
					messageReceived(message.getCmdType(), friname, "","");
				}
				else{
					messageReceived(message.getCmdType(), friname, ip,String.valueOf(port));
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}		
	}
	/**
	 * status notify
	 */
	public static byte status;
	
	public class StatusNotify extends MessageProcessTask{
		
		public StatusNotify(IoSession session, InputMessage message)
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
			//��ȡ�������
			DataInputStream body = message.getInputStream();
			long len;
			try {
				len = body.readLong();
				byte[] nm = new byte[(int)len];
				body.read(nm);
				String friname = new String(nm,"gb2312");
				status = body.readByte();
				messageReceived(message.getCmdType(), friname, String.valueOf(status),"");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
	}
}
