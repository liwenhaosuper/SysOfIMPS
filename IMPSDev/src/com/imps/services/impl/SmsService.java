
package com.imps.services.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.jboss.netty.buffer.ChannelBuffers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.imps.IMPSDev;
import com.imps.basetypes.MediaType;
import com.imps.basetypes.OutputMessage;
import com.imps.net.handler.MessageFactory;
import com.imps.net.handler.UserManager;
import com.imps.services.ISmsService;
import com.imps.util.CommonHelper;

public class SmsService implements ISmsService{

	private static String TAG = SmsService.class.getCanonicalName();
	private static boolean DEBUG = IMPSDev.isDEBUG();
	
	private List<MediaType> mUnsendList;
	private List<MediaType> mSentList;
		
	private static boolean isStarted = false;
	private static Timer schedule;
	public static int DEFAULTPACKETSIZE = 800;
	
	private class ImageIdentify{
		public int sid;
		public String friName;
		@Override
		public boolean equals(Object obj){
			if(sid==((ImageIdentify)obj).sid&&friName.equals(((ImageIdentify)obj).friName)){
				return true;
			}
			return false;
		}
	}
	private class AudioIdentify{
		public int sid;
		public String friName;
	}

	public SmsService(){
		mUnsendList = new ArrayList<MediaType>();
		mSentList = new ArrayList<MediaType>();
	}
	
	@Override
	public int sendSms(MediaType item) {
		// TODO Auto-generated method stub
/*		if(schedule==null){
			schedule = new Timer();
			schedule.schedule(new Scheduler(), Scheduler.DELAY,Scheduler.INTERRUPT);
		}*/
		mUnsendList.add(item);
		if(ServiceManager.getmTcpConn().getChannel().isConnected()){
			ServiceManager.getmTcpConn().getChannel().write(ChannelBuffers.wrappedBuffer(
					MessageFactory.createCSendMsgReq(UserManager.getGlobaluser().getUsername(),
				item.getFriend(), item.getMsgContant(),item.getId()).build()));
		}
		return 0;
	}

	@Override
	public boolean isSent(int msgId) {
		// TODO Auto-generated method stub
		for(int i=0;i<mSentList.size();i++){
			if(mSentList.get(i).getId()==msgId)
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public int SendAudio(MediaType item) {
		// TODO Auto-generated method stub
/*		if(schedule==null){
			schedule = new Timer();
			schedule.schedule(new Scheduler(), Scheduler.DELAY,Scheduler.INTERRUPT);
		}*/
		if(DEBUG)Log.d(TAG,"Sending audio...");
		mUnsendList.add(item);
		if(item.getType()==MediaType.AUDIO&&item.getContant()!=null){
			for(int i=0;i<item.getContant().size();i++){
				if(ServiceManager.getmTcpConn().getChannel().isConnected()){
					ServiceManager.getmTcpConn().getChannel().write(ChannelBuffers.wrappedBuffer(
							MessageFactory.createCAudioReq(UserManager.getGlobaluser().getUsername(), 
									item.getFriend(), item.getContant().get(i),item.getId(),
									i==(item.getContant().size()-1)?true:false).build()));
				}
			}
		}
		return 0;
	}

	@Override
	public int SendImage(MediaType item) {
		// TODO Auto-generated method stub
/*		if(schedule==null){
			schedule = new Timer();
			schedule.schedule(new Scheduler(), Scheduler.DELAY,Scheduler.INTERRUPT);
		}*/
		mUnsendList.add(item);
		if(item.getType()==MediaType.IMAGE&&item.getMsgContant()!=null&&!item.getMsgContant().equals("")){
			Bitmap tmap = BitmapFactory.decodeFile(item.getMsgContant());
			if(DEBUG) {Log.d(TAG,"Path is:"+CommonHelper.saveImage(tmap));}
			if(tmap!=null){
				byte[] container = CommonHelper.bitmapToBytes(tmap);
				byte[] packet = new byte[DEFAULTPACKETSIZE];
				int cnt = container.length/DEFAULTPACKETSIZE;
				OutputMessage outMsg = null;
				if(DEBUG) Log.d(TAG,"Packets is "+cnt);
				for(int i=0;i<cnt+1;i++){
					if(((i+1)*DEFAULTPACKETSIZE)>=container.length){
						System.arraycopy(container, i*DEFAULTPACKETSIZE, packet, 0,container.length-i*DEFAULTPACKETSIZE);
						outMsg = MessageFactory.createCImageReq(UserManager.getGlobaluser().getUsername(), item.getFriend(),
								item.getId(), true);
						try {
							outMsg.getOutputStream().writeInt(container.length-i*DEFAULTPACKETSIZE);
							outMsg.getOutputStream().write(packet);
							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if(ServiceManager.getmTcpConn().getChannel().isConnected()){
							ServiceManager.getmTcpConn().getChannel().write(ChannelBuffers.wrappedBuffer(
									outMsg.build()));
						}
						if(DEBUG) Log.d(TAG,"Last packet sent...image size is:"+container.length);
						break;
						
					}else{
						System.arraycopy(container, i*DEFAULTPACKETSIZE, packet, 0, DEFAULTPACKETSIZE);
						outMsg = MessageFactory.createCImageReq(UserManager.getGlobaluser().getUsername(), item.getFriend(),
								item.getId(), false);
						try {
							outMsg.getOutputStream().writeInt(DEFAULTPACKETSIZE);
							outMsg.getOutputStream().write(packet);
							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if(ServiceManager.getmTcpConn().getChannel().isConnected()){
							ServiceManager.getmTcpConn().getChannel().write(ChannelBuffers.wrappedBuffer(
									outMsg.build()));
						}
					}
				}
			}
		}
		return 0;
	}

	@Override
	public boolean isSmsServiceAvailable() {
		// TODO Auto-generated method stub
		if(!isStarted){
			return false;
		}
		return ServiceManager.getmTcpConn().getChannel().isConnected();
	}

	@Override
	public boolean isStarted() {
		// TODO Auto-generated method stub
		return isStarted;
	}

	@Override
	public boolean start() {
		// TODO Auto-generated method stub
		if(isStarted){
			return true;
		}
/*		if(schedule!=null){
			schedule.cancel();
		}*/
		isStarted = true;
		return true;
	}

	@Override
	public boolean stop() {
		// TODO Auto-generated method stub
		if(!isStarted){
			return true;
		}
		isStarted = false;
		if(schedule!=null){
			schedule.cancel();
		}
		isStarted = false;
		return isStarted;
	}

	public void onSmsSendSuccess(String ret, int smsId) {
		// TODO Auto-generated method stub	
		for(int i=0;i<mUnsendList.size();i++){
			if(mUnsendList.get(i).getId()==smsId){
				MediaType item = mUnsendList.remove(i);
				mSentList.add(item);
				break;
			}
		}
	}

	public void onAudioSendSuccess(String ret, int smsId) {
		// TODO Auto-generated method stub
		for(int i=0;i<mUnsendList.size();i++){
			if(mUnsendList.get(i).getId()==smsId){
				MediaType item = mUnsendList.remove(i);
				mSentList.add(item);
				break;
			}
		}
	}

	public void onImageSendSuccess(String ret, int smsId) {
		// TODO Auto-generated method stub
		for(int i=0;i<mUnsendList.size();i++){
			if(mUnsendList.get(i).getId()==smsId){
				MediaType item = mUnsendList.remove(i);
				mSentList.add(item);
				if(DEBUG) Log.d(TAG,"Send sms to "+ret+" success...");
				break;
			}
		}
	}

	private class Scheduler extends TimerTask{
		public static final long INTERRUPT = 60*1000;
		public static final long DELAY = 10*1000;
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(DEBUG) Log.d(TAG,"Scheduler...");
			if(!isSmsServiceAvailable()){
				return;
			}
			for(int i=0;i<mUnsendList.size();i++){
				if(mUnsendList.get(i).getType()==MediaType.AUDIO){
					
				}else if(mUnsendList.get(i).getType()==MediaType.IMAGE){
					
				}else if(mUnsendList.get(i).getType()==MediaType.SMS){
					if(ServiceManager.getmTcpConn().getChannel().isConnected()){
						ServiceManager.getmTcpConn().getChannel().write(ChannelBuffers.wrappedBuffer(
								MessageFactory.createCSendMsgReq(UserManager.getGlobaluser().getUsername(),
							mUnsendList.get(i).getFriend(), mUnsendList.get(i).getMsgContant(),mUnsendList.get(i).getId()).build()));
					}
				}
			}
		}
	}



}

