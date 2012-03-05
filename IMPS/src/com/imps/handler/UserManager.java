
/*
 * Author: liwenhaosuper
 * Date: 2011/5/19
 * Description:
 *     user manager class method
 */




package com.imps.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Timer;


import android.util.Log;

import com.imps.base.MessageFactory;
import com.imps.base.OutputMessage;
import com.imps.base.User;
import com.imps.base.location;
import com.imps.base.userStatus;
import com.imps.main.Client;
import com.yz.net.IoSession;


public class UserManager {

	private static UserManager instance =  new UserManager();
	/**
	 * 全局user,当前用户
	 */
	private static User globaluser = null;
	
	private static Timer timer = new Timer(true);
	/**
	 * 所有好友列表
	 */
	public static List<User> AllFriList; 
    /**
     * 当前会话好友列表
     * User: 好友
     * List<String>: 会话消息
     */
	public static HashMap<String,List<String> > CurSessionFriList = new HashMap<String, List<String>>();
    /**
     * 未读消息
     * friendName:message
     */
	public static HashMap<String,String> UnReadMessages = new HashMap<String,String>();
	/**
	 * active friend
	 */
	public static String activeFriend = new String();
	public UserManager() 
	{	
	}
	
	public static Timer getTimer() {
		return timer;
	}
	//向服务器发送信息
	public boolean SendMsg(String friname,String msg)
	{
		IoSession session = Client.session;
		OutputMessage outMsg = null;
		//添加协议头
		outMsg = MessageFactory.createCSendMsgReq();
		//发送用户名
		String username = globaluser.getUsername();
		
		try {
			long len = username.getBytes("gb2312").length;
			outMsg.getOutputStream().writeLong(len);
			byte[] usernm = new byte[(int)len];
			usernm = username.getBytes("gb2312");
			outMsg.getOutputStream().write(usernm);
			//发送好友名
			len = friname.getBytes("gb2312").length;
			outMsg.getOutputStream().writeLong(len);
			byte[] frinm = new byte[(int)len];
			frinm = friname.getBytes("gb2312");
			outMsg.getOutputStream().write(frinm);
			//发送信息
			len = msg.getBytes("gb2312").length;
			outMsg.getOutputStream().writeLong(len);
			byte[] msgcontent = new byte[(int)len];
			msgcontent = msg.getBytes("gb2312");
			outMsg.getOutputStream().write(msgcontent);
			System.out.println("message has been sent to "+friname);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		session.write(outMsg);
		
	   return true;
	}
	/**
	 * 发送音频
	 * @param friName
	 * @param data
	 * @return
	 */
	public boolean SendAudio(String friName,LinkedList<byte[]> data)
	{
		Log.d("UserManager", "record size is "+data.size());
		 for(int i=0;i<data.size();i++)
		 {
			 System.out.println("data "+i+" is "+ data.get(i)+" and size is "+data.get(i).length);
		 }
		IoSession session = Client.session;
		OutputMessage outMsg = null;
		//添加协议头
		int size = data.size();
		for(int i=0;i<size;i++)
		{
			byte[] dataItem = data.removeFirst();
			int cnt = dataItem.length/200;
			for(int j=0;j<cnt;j++)
			{
				byte[] one = new byte[200]; 
				if(200*(j+1)<=dataItem.length)
				{
				    System.arraycopy(dataItem,j*200,one,0,200);
				}
				else{
					System.arraycopy(dataItem,j*200,one,0,dataItem.length-200*j);
				}
				outMsg = MessageFactory.createCAudioReq(globaluser.getUsername(), friName,one );
				session.write(outMsg);
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
		outMsg = MessageFactory.createCAudioReq(globaluser.getUsername(), friName, last);
		session.write(outMsg);
		return true;
	}
	/**
	 * 发送音频聊天请求
	 * @param user
	 * @return
	 * @throws IOException
	 */
	public boolean SendPTPAudioReq(String friName,String ip,int port)
	{
		IoSession session = Client.session;
		OutputMessage outMsg = null;
		outMsg = MessageFactory.createCPTPAudioReq(globaluser.getUsername(), friName, ip, port);
		session.write(outMsg);
		return true;
	}
	/**
	 * 发送视频聊天请求
	 * @param user
	 * @return
	 * @throws IOException
	 */
	public boolean SendPTPVideoReq(String friName,String ip,int port)
	{
		IoSession session = Client.session;
		OutputMessage outMsg = null;
		outMsg = MessageFactory.createCPTPVideoReq(globaluser.getUsername(), friName, ip, port);
		session.write(outMsg);
		return true;
	}
	/**
	 * 发送音频聊天响应
	 * @param user
	 * @return
	 * @throws IOException
	 */
	public boolean SendPTPAudioRsp(String friName,String ip,int port,boolean res)
	{
		IoSession session = Client.session;
		OutputMessage outMsg = null;
		if(ip==null||"".equals(ip))
			return false;
		outMsg = MessageFactory.createCPTPAudioRsp(globaluser.getUsername(), friName, ip,port,res);
		session.write(outMsg);
		return true;
	}
	/**
	 * 发送视频聊天响应
	 * @param user
	 * @return
	 * @throws IOException
	 */
	public boolean SendPTPVideoRsp(String friName,String ip,int port,boolean res)
	{
		IoSession session = Client.session;
		OutputMessage outMsg = null;
		if(ip==null||"".equals(ip))
			return false;
		outMsg = MessageFactory.createCPTPVideoRsp(globaluser.getUsername(), friName, ip,port,res);
		session.write(outMsg);
		return true;
	}

	//登录系统用户信息存在User中，
	public boolean Login(User user) throws IOException
	{
        IoSession session = Client.session;
        OutputMessage outMsg = null;
        //添加协议头
        outMsg = MessageFactory.createCLoginReq();
        //发送用户名
        String usernm = user.getUsername();
        long len = (long)usernm.getBytes("gb2312").length;
        outMsg.getOutputStream().writeLong(len);
        outMsg.getOutputStream().write(usernm.getBytes("gb2312"));
        //发送密码
        String pwd = user.getPassword();
        len = (long)pwd.getBytes("gb2312").length;
        outMsg.getOutputStream().writeLong(len);
        outMsg.getOutputStream().write(pwd.getBytes("gb2312"));
        session.write(outMsg);
        
/*        //等待服务器响应
        int cnt = 0;
        while(true)
        {
        	cnt++;
        	if(cnt>300)
        	{
        		System.out.println("登录等待超时");
        		return false;
        	}
        	try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(globaluser!=null&&globaluser.getUsername()!="")
			{
				return true;
			}
        }*/
        return true;
	}
	
	//用户注册，注册信息存放在user参数中，
	public void register(User user ) throws IOException
	{
		IoSession session = Client.session;
		OutputMessage outMsg = null;
		//添加协议头
		outMsg = MessageFactory.createCRegisterReq();
		//发送用户名
        String usernm = user.getUsername();
        long len = (long)usernm.getBytes("gb2312").length;
        outMsg.getOutputStream().writeLong(len);
        outMsg.getOutputStream().write(usernm.getBytes("gb2312"));
        //发送密码
        String pwd = user.getPassword();
        len = (long)pwd.getBytes("gb2312").length;
        outMsg.getOutputStream().writeLong(len);
        outMsg.getOutputStream().write(pwd.getBytes("gb2312"));
        //发送性别
        int gender = user.getGender();
        outMsg.getOutputStream().writeInt(gender);
        //发送email
        String email = user.getEmail();
        len = (long)email.getBytes("gb2312").length;
        outMsg.getOutputStream().writeLong(len);
        outMsg.getOutputStream().write(email.getBytes("gb2312"));
        //发送
        session.write(outMsg);
        
        
	}
	//发送好友列表请求
	public void SendFriListReq()
	{
		if(globaluser==null)
		{
			System.out.println("golbal user is null");
			return ;
		}
		IoSession session = Client.session;
		OutputMessage outMsg = MessageFactory.createCFriendListReq(globaluser.getUsername());
		session.write(outMsg);
		System.out.println("friend list request hase been sent");
		
	}
	
	
	//更改用户状态
	public void setStatus(byte status)
	{
		globaluser.setStatus(status);
		IoSession session = Client.session;
		OutputMessage outMsg = MessageFactory.createCStatusNotify(globaluser.getUsername(), status);
		session.write(outMsg);
	}
	/*
	 * 添加好友请求
	 */
	public void AddFriendRequest(String friName)
	{
		IoSession session = Client.session;
		OutputMessage outMsg = MessageFactory.createCAddFriReq(globaluser.getUsername(), friName);
		session.write(outMsg);
	}
	/**
	 * 添加好友响应
	 */
	public void AddFriRsp(String friName,boolean res)
	{
		if(friName==null||friName.equals(""))
		{
			return;
		}
		IoSession session = Client.session;
		OutputMessage outMsg = MessageFactory.createCAddFriRsq(globaluser.getUsername(), friName, res);
		session.write(outMsg);
	}
	
	
	/**
	 * @param instance the instance to set
	 */
	public static void setInstance(UserManager instance) {
		UserManager.instance = instance;
	}


	/**
	 * @return the instance
	 * 
	 */
	public static UserManager getInstance()  {
		if(instance==null)
			instance = new UserManager();
		return instance;
	}

	/**
	 * delete friend from the cursessionlist
	 */
	public void deleteFriFromSession(String friName)
	{
		Iterator iter = CurSessionFriList.entrySet().iterator();
		while(iter.hasNext())
		{
			Map.Entry entity = (Map.Entry)iter.next();
			User fri = (User)entity.getKey();
			if(fri.getUsername().equals(friName))
			{
				CurSessionFriList.remove(entity.getKey());
				break;
			}
		}
	}
	
    /**
     * 刷新好友状态
     * @param user
     */
	public void updateUserStatus(String friName,byte status)
	{
        for(int i=0;i<AllFriList.size();i++)
        {
        	if(AllFriList.get(i).getUsername().equals(friName))
        	{
        		User fri = AllFriList.get(i);
        		fri.setStatus(status);
        		break;
        	}
        }
	}

	
	/**
	 * 获取在线好友列表
	 * @param username
	 * @return
	 */
	public User[] getOnlineFriendlist()
	{
		int frilen = 0;
        for(int i=0;i<AllFriList.size();i++)
        {
        	if(AllFriList.get(i).getStatus()==userStatus.ONLINE)
        	{
        		frilen++;
        	}
        }
        User[] res = new User[frilen];
        frilen = 0;
        for(int i=0;i<AllFriList.size();i++){
        	if(AllFriList.get(i).getStatus()==userStatus.ONLINE)
        	{
        		res[frilen] = AllFriList.get(i);
        		frilen++;
        	}
        }
        return res;
	}
	/**
	 * get the user's friend's location
	 * @param friendname
	 */
	public location getFriendLocation(String friendname)
	{
		location loc = new location();
	    for(int i=0;i<AllFriList.size();i++)
	    {
	    	if(AllFriList.get(i).getUsername()==friendname)
	    	{
	    		User tusr =AllFriList.get(i);
	    		loc.x = tusr.getLocX();
	    		loc.y = tusr.getLocY();
	    		loc.ptime = tusr.getLoctime();
	    		break;
	    	}
		}
		return loc;
	}

	public static void setGlobaluser(User globaluser) {
		UserManager.globaluser = globaluser;
	}

	public static User getGlobaluser() {
		return globaluser;
	}
	
}
