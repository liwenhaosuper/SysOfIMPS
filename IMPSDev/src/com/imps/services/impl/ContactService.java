package com.imps.services.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffers;

import android.util.Log;
import android.widget.Toast;

import com.imps.IMPSDev;
import com.imps.R;
import com.imps.activities.IMPSContainer;
import com.imps.basetypes.SystemMsgType;
import com.imps.events.IContactEvent;
import com.imps.net.handler.MessageFactory;
import com.imps.net.handler.UserManager;
import com.imps.services.IContactService;

public class ContactService implements IContactService,IContactEvent{
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
		ServiceManager.getmNetLogic().addContactEventHandler(this);
		return true;
	}

	@Override
	public boolean stop() {
		isStarted = false;
		ServiceManager.getmNetLogic().removeContactEventHandler(this);
		return true;
	}

	@Override
	public void sendFriListReq() {
		// TODO Auto-generated method stub
		if(ServiceManager.getmTcpConn().getChannel().isConnected()){
			ServiceManager.getmTcpConn().getChannel().write(ChannelBuffers.wrappedBuffer(
					MessageFactory.createCFriendListReq(UserManager.getGlobaluser().getUsername()).build()));
		}
	}

	@Override
	public void sendUpdateMyStatusReq(byte status) {
		// TODO Auto-generated method stub
		if(ServiceManager.getmTcpConn().getChannel().isConnected()){
			ServiceManager.getmTcpConn().getChannel().write(ChannelBuffers.wrappedBuffer(
					MessageFactory.createCStatusNotify(UserManager.getGlobaluser().getUsername(), status).build()
					));
		}
	}

	@Override
	public void onUpdateFriStatus() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpdateFriList() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRecvFriendReq(String friName) {
		// TODO Auto-generated method stub
		SystemMsgType item = new SystemMsgType();
		item.name = friName;
		item.status = SystemMsgType.NONE;
		item.text = IMPSDev.getContext().getResources().getString(R.string.add_friend_req_notify,friName);
		item.time = getTime();
		item.type = SystemMsgType.FROM;
		UserManager.mSysMsgs.add(item);
		if(DEBUG)Log.d(TAG,"req ADD...");
		ServiceManager.showNotification(R.drawable.new_contact_notification, R.drawable.new_contact_notification,
				IMPSDev.getContext().getResources().getString(R.string.add_friend_req_notify,friName), IMPSContainer.class,2);
		//SystemMsg.refresh();
	}

	@Override
	public void onRecvFriendRsp(String friName, boolean rel) {
		// TODO Auto-generated method stub
		SystemMsgType item = new SystemMsgType();
		item.name = friName;
		if(rel){
			item.status = SystemMsgType.ACCEPTED;
			item.text = IMPSDev.getContext().getResources().getString(R.string.add_friend_rsp_accepted,friName);
		}else{
			item.status = SystemMsgType.DENIED;
			item.text = IMPSDev.getContext().getResources().getString(R.string.add_friend_rsp_denied,friName);
		}
		if(DEBUG) Log.d(TAG,"RSP...");
		item.time = getTime();
		item.type = SystemMsgType.FROM;
		UserManager.mSysMsgs.add(item);
		if(DEBUG)Log.d(TAG,"rsp ADD...");
		ServiceManager.showNotification(R.drawable.new_contact_notification, R.drawable.new_contact_notification,
				IMPSDev.getContext().getResources().getString(rel==false?R.string.add_friend_rsp_denied:R.string.add_friend_rsp_accepted,friName), IMPSContainer.class,2);
		//SystemMsg.refresh();
		if(rel){
			sendFriListReq();
		}
	}

	@Override
	public void sendSearchFriendReq(String keyword) {
		if(ServiceManager.getmTcpConn().getChannel().isConnected()){
			ServiceManager.getmTcpConn().getChannel().write(ChannelBuffers.wrappedBuffer(
					MessageFactory.createCSearchFriendReq(UserManager.getGlobaluser().getUsername(), keyword).build())
					);
		}
	}

	@Override
	public void onRecvSearchFriendRsq(List<String> friends) {}

	@Override
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

	@Override
	public void onRecvAddFriendReq(String friname) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRecvAddFriendRsp(String friname, boolean res) {
		// TODO Auto-generated method stub
		
	}

	@Override
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

}
