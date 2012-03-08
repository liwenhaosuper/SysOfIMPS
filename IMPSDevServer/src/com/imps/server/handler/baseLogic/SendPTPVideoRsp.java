package com.imps.server.handler.baseLogic;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.sql.SQLException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import com.imps.server.main.IMPSTcpServer;
import com.imps.server.main.basetype.MessageProcessTask;
import com.imps.server.main.basetype.User;
import com.imps.server.main.basetype.userStatus;
import com.imps.server.manager.MessageFactory;
import com.imps.server.manager.UserManager;

public class SendPTPVideoRsp extends MessageProcessTask{

	private String userName;
	private String friName;
	private String ip;
	private int port;
	private SocketAddress addr;
	private boolean res;
	public SendPTPVideoRsp(Channel session, ChannelBuffer inMsg,SocketAddress addr) {
		super(session, inMsg);
		this.addr = addr;
	}

	@Override
	public void parse() {
		int len = inMsg.readInt();
		byte []nm = new byte[len];
		inMsg.readBytes(nm);
		try {
			userName = new String(nm,"gb2312");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		}
		len = inMsg.readInt();
		nm = new byte[len];
		inMsg.readBytes(nm);
		try {
			friName = new String(nm,"gb2312");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		}
		//get ip
		len = inMsg.readInt();
		byte[] IPbyte = new byte[(int)len];
		inMsg.readBytes(IPbyte);
		try {
			ip = new String(IPbyte,"gb2312");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		}
		//get port
		port = inMsg.readInt();
		//get result
		res = inMsg.readInt()==0?false:true;
		
	}

	@Override
	public void execute() {
		UserManager manager = null;
		//update user status
		try {
			manager = UserManager.getInstance();
			User user = manager.getUser(userName);
			if(user==null)
			{
				user = manager.getUserFromDB(userName);
				user.setStatus(userStatus.ONLINE);
				user.setSessionId(session.getId());
				manager.addUser(user);
				User[] friends = user.getOnlineFriendList();
				//notify all friends
				if(friends==null)
					return ;
				for(int i=0;i<friends.length;i++)
				{
				     manager.updateUserStatus(user);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
		//dispatch user request
		if(manager.userMap.contains(friName)){
			User fri = manager.getUser(friName);
	        Channel mysession = IMPSTcpServer.getAllGroups().find(fri.getSessionId());
	        if(mysession.isConnected()){
	        	mysession.write(ChannelBuffers.wrappedBuffer(
	        			MessageFactory.createSPTPVideoRsp(userName, ip, port,res,((InetSocketAddress)addr).getAddress().getHostAddress()
	        					,((InetSocketAddress)addr).getPort()).build()
	        			));
	        	System.out.println("dispatch video rsp succ.");
	        }
		}else{
			System.out.println(" user is now offline and could not sent video rsp to him~");
		}
	}

}
