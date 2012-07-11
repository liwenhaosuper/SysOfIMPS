package com.imps.services.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffers;

import android.util.Log;
import android.widget.Toast;

import com.imps.IMPSDev;
import com.imps.R;
import com.imps.activities.IMPSContainer;
import com.imps.basetypes.SystemMsgType;
import com.imps.events.IContactEvent;
import com.imps.model.CommandId;
import com.imps.model.CommandType;
import com.imps.model.IMPSType;
import com.imps.net.handler.MessageFactory;
import com.imps.net.handler.UserManager;
import com.imps.net.tcp.ConnectionService;
import com.imps.services.IContactService;

public class ContactService implements IContactService{
	protected static final String TAG = ContactService.class.getCanonicalName();
	private static boolean DEBUG = IMPSDev.isDEBUG();
	private boolean isStarted = false;
	public ContactService(){
		
	}
	@Override
	public boolean isStarted() {
		return isStarted;
	}

	@Override
	public boolean start() {
		if(isStarted)
			return true;
		isStarted = true;
		return true;
	}

	@Override
	public boolean stop() {
		isStarted = false;
		return true;
	}

	public void sendFriListReq() {
		if(ServiceManager.getmTcpConn().getChannel().isConnected()){
			HashMap<String,String> header = new HashMap<String,String>();
			header.put("Command", CommandId.C_FRIENDLIST_REFURBISH_REQ);
			header.put("UserName",UserManager.globaluser.getUsername());
			IMPSType result = new CommandType();
			result.setmHeader(header);
			ServiceManager.getmTcpConn().getChannel().write(ChannelBuffers.wrappedBuffer(result.MediaWrapper()));
		}
	}
	public void sendUpdateMyStatusReq(byte status) {
		// TODO Auto-generated method stub
		if(ServiceManager.getmTcpConn().getChannel().isConnected()){
			ServiceManager.getmTcpConn().getChannel().write(ChannelBuffers.wrappedBuffer(
					MessageFactory.createCStatusNotify(UserManager.getGlobaluser().getUsername(), status).build()
					));
		}
	}

	public void sendSearchFriendReq(String keyword) {
		if(ServiceManager.getmTcpConn().getChannel().isConnected()){
			ServiceManager.getmTcpConn().getChannel().write(ChannelBuffers.wrappedBuffer(
					MessageFactory.createCSearchFriendReq(UserManager.getGlobaluser().getUsername(), keyword).build())
					);
		}
	}


	public void sendAddFriendReq(String friName) {
		SystemMsgType item = new SystemMsgType();
		item.name = friName;
		item.status = SystemMsgType.NONE;
		item.time = getTime();
		item.type = SystemMsgType.TO;
		item.text = IMPSDev.getContext().getResources().getString(R.string.add_friend_req_sent);
		UserManager.mSysMsgs.add(item);
		
		if(ServiceManager.getmTcpConn().getChannel().isConnected()){
			ServiceManager.getmTcpConn().getChannel().write(ChannelBuffers.wrappedBuffer(
					MessageFactory.createCAddFriReq(UserManager.getGlobaluser().getUsername(), friName).build()
					));
			Toast.makeText(IMPSDev.getContext(), IMPSDev.getContext().getString(R.string.add_friend_req_sent),
					Toast.LENGTH_LONG).show();
		}
	}


	public void sendAddFriendRsp(String friName, boolean res) {
		if(ServiceManager.getmTcpConn().getChannel().isConnected()){
			ServiceManager.getmTcpConn().getChannel().write(ChannelBuffers.wrappedBuffer(
					MessageFactory.createCAddFriRsp(UserManager.getGlobaluser().getUsername(), friName,res).build()
					));
			Toast.makeText(IMPSDev.getContext(), IMPSDev.getContext().getString(R.string.add_friend_rsp_sent),
					Toast.LENGTH_LONG).show();
		}
	}
	private String getTime(){
		Date now = new Date();
		SimpleDateFormat d1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    String date = d1.format(now);
	    return date;
	}

	public void sendOfflineMsgReq() {
		if(ServiceManager.getmTcpConn().getChannel().isConnected()){		
			HashMap<String,String> header = new HashMap<String,String>();
			header.put("Command", CommandId.C_OFFLINE_MSG_REQ);
			header.put("UserName",UserManager.globaluser.getUsername());
			IMPSType result = new CommandType();
			result.setmHeader(header);
			ServiceManager.getmTcpConn().getChannel().write(ChannelBuffers.wrappedBuffer(result.MediaWrapper()));
		}
	}
}
