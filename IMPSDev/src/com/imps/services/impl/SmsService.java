
package com.imps.services.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.jboss.netty.buffer.ChannelBuffers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.imps.IMPSDev;
import com.imps.R;
import com.imps.activities.ChatView;
import com.imps.basetypes.ListContentEntity;
import com.imps.basetypes.MediaType;
import com.imps.basetypes.OutputMessage;
import com.imps.events.ISmsEvent;
import com.imps.media.audio.Track;
import com.imps.net.handler.MessageFactory;
import com.imps.net.handler.UserManager;
import com.imps.services.ISmsService;

public class SmsService implements ISmsService,ISmsEvent{

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
	//username, data
	public static HashMap<ImageIdentify,byte[] >  usersImageData = 
		      new HashMap<ImageIdentify,byte[] >();
    public static HashMap<AudioIdentify,LinkedList<byte[]> >  usersAudioData = 
			      new HashMap<AudioIdentify,LinkedList<byte[]> >();
	
	public SmsService(){
		mUnsendList = new ArrayList<MediaType>();
		mSentList = new ArrayList<MediaType>();
		ServiceManager.getmNetLogic().addSmsEventHandler(this);
	}
	
	@Override
	public int sendSms(MediaType item) {
		// TODO Auto-generated method stub
		if(schedule==null){
			schedule = new Timer();
			schedule.schedule(new Scheduler(), Scheduler.DELAY,Scheduler.INTERRUPT);
		}
		mUnsendList.add(item);
		if(ServiceManager.getmTcpConn().getChannel().isConnected()){
			ServiceManager.getmTcpConn().getChannel().write(ChannelBuffers.wrappedBuffer(
					MessageFactory.createCSendMsgReq(UserManager.getGlobaluser().getUsername(),
				item.getFriend(), item.getMsgContent(),item.getId()).build()));
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
		if(schedule==null){
			schedule = new Timer();
			schedule.schedule(new Scheduler(), Scheduler.DELAY,Scheduler.INTERRUPT);
		}
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
		if(schedule==null){
			schedule = new Timer();
			schedule.schedule(new Scheduler(), Scheduler.DELAY,Scheduler.INTERRUPT);
		}
		mUnsendList.add(item);
		if(item.getType()==MediaType.IMAGE&&item.getMsgContent()!=null&&!item.getMsgContent().equals("")){
			Bitmap tmap = BitmapFactory.decodeFile(item.getMsgContent());
			if(DEBUG) {Log.d(TAG,"Path is:"+saveImage(tmap));}
			if(tmap!=null){
				byte[] container = bitmapToBytes(tmap);
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
	@Override
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

	@Override
	public void onSmsSendFail(String errorCode, int smsId) {
		// TODO Auto-generated method stub
		for(int i=0;i<mUnsendList.size();i++){
			if(mUnsendList.get(i).getId()==smsId){
				MediaType item = mUnsendList.remove(i);
				mSentList.add(item);
				break;
			}
		}
	}

	@Override
	public void onSmsSendTimeOut(String errorCode, int smsId) {
		// TODO Auto-generated method stub
		
	}

	@Override
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

	@Override
	public void onAudioSendFail(String errorCode, int smsId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAudioSendTimeOut(String errorCode, int smsId) {
		// TODO Auto-generated method stub
		
	}

	@Override
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

	@Override
	public void onImageSendFail(String errorCode, int smsId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onImageSendTimeOut(String errorCode, int smsId) {
		// TODO Auto-generated method stub
		
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
							mUnsendList.get(i).getFriend(), mUnsendList.get(i).getMsgContent(),mUnsendList.get(i).getId()).build()));
					}
				}
			}
		}
	}
	@Override
	public void onSmsRecv(MediaType media) {
		// TODO Auto-generated method stub
		if(UserManager.activeFriend!=null&&UserManager.activeFriend.equals(media.getFriend())){
			return;
		}
		if(UserManager.CurSessionFriList.containsKey(media.getFriend())){
			UserManager.CurSessionFriList.get(media.getFriend()).add(media);
		}else{
			List<MediaType> item = new ArrayList<MediaType>();
			item.add(media);
			UserManager.CurSessionFriList.put(media.getFriend(), item);
		}
		String snip = media.getMsgContent();
		if(snip.length()>10){
			snip = snip.substring(0,9)+"...";
		}

		ServiceManager.showNotification(R.drawable.new_msg_notification, R.drawable.new_msg_notification,
				media.getFriend()+":"+snip, ChatView.class,media.getFriend());
		
	}

	@Override
	public void onAudioRecv(String friName,int sid,boolean isEOF,byte[] data) {
		// TODO Auto-generated method stub
		if(DEBUG) Log.d(TAG,"friName is:"+friName+" sid is:"+sid+" eof:"+isEOF);
		boolean flag = false;
		LinkedList<byte[]> packet = null;
		Iterator iter = usersAudioData.entrySet().iterator();
		AudioIdentify key = null;
		while (iter.hasNext()) { 
		    Map.Entry entry = (Map.Entry) iter.next(); 
		    key = (AudioIdentify)entry.getKey();
		    if(key.sid==sid&&key.friName.equals(friName)){
		    	((LinkedList<byte[]>)entry.getValue()).add(data);
		    	 flag = true;
		    	 if(isEOF){
		    		 packet = (LinkedList<byte[]>)entry.getValue();
		    	 }
		    	 break;
		    }
		}
		if(!flag){
			AudioIdentify newitem = new AudioIdentify();
			newitem.sid = sid;
			newitem.friName = friName;
			LinkedList<byte[]> nitem = new LinkedList<byte[]>();
			nitem.add(data);
			usersAudioData.put(newitem, nitem);
			if(isEOF){
				packet = nitem;
			}
		}
		if(isEOF){
			//TODO:Audio data changed...
			if(DEBUG) Log.d(TAG,"Start playing audio");
/*			Track track = new Track();
			track.data = packet;
			track.run();*/
			MediaType media = new MediaType(MediaType.AUDIO,packet,MediaType.from);
			media.setTime(getTime());
			media.setFriend(friName);
			if(UserManager.CurSessionFriList.containsKey(friName))
			{
				 UserManager.CurSessionFriList.get(friName).add(media);
				 if(DEBUG) Log.d(TAG,"adding to the list");
			}
			else{
				List<MediaType> newmsgbox = new ArrayList<MediaType>();
				newmsgbox.add(media);
				UserManager.CurSessionFriList.put(friName, newmsgbox);
				if(DEBUG) Log.d(TAG,"adding to the list with new image");
			}
			usersAudioData.remove(key);
			if(UserManager.activeFriend!=null&&UserManager.activeFriend.equals(friName)){
				ListContentEntity entity = new ListContentEntity(friName,media.getTime(),"",ListContentEntity.MESSAGE_FROM_AUDIO,media.getContant());
				ChatView.list.add(entity);
				//TODO:Notify the user
			}else{
				ServiceManager.showNotification(R.drawable.new_msg_notification, R.drawable.new_audio_notification,
						IMPSDev.getContext().getResources().getString(R.string.new_audio_received_from_friend,media.getFriend()),
						ChatView.class,media.getFriend());
			}
		}
	}

	@Override
	public void onImageRecv(String userName,int sid,boolean isEOF,byte[] data) {
		// TODO Auto-generated method stub
		boolean flag = false;
		byte packet[] = null;
		if(DEBUG) Log.d(TAG,"friName is:"+userName+" sid is:"+sid);
		Iterator iter = usersImageData.entrySet().iterator();
		ImageIdentify key = null;
		while (iter.hasNext()) { 
		    Map.Entry entry = (Map.Entry) iter.next(); 
		    key = (ImageIdentify)entry.getKey();
		    if(key.sid==sid&&key.friName.equals(userName)){
				byte[] val = (byte[])entry.getValue();
				byte[] newval =new byte[data.length+val.length];
		    	System.arraycopy(val, 0, newval, 0, val.length);
		    	System.arraycopy(data, 0, newval, val.length, data.length);
		    	usersImageData.put(key, newval);
		    	if(isEOF) packet = newval;
		    	if(DEBUG) Log.d(TAG,"Put image snipper.");
		    	flag = true;
		    	break;
		    }
		    
		}
		if(!flag){
			key = new ImageIdentify();
			key.friName = userName;
			key.sid = sid;
			usersImageData.put(key, data);
			if(isEOF) packet = data;
			if(DEBUG) Log.d(TAG,"Add image item.");
		}

		if(isEOF){
			Bitmap bMap = bytesToBitmap(packet);
			if(bMap==null){
				if(DEBUG) Log.d(TAG,"Bitmap decode to null...byte buffer size is:"+packet.length);
			}
			if(DEBUG) Log.d(TAG,"Bitmap bytes size is :"+packet.length);
			MediaType media = new MediaType(MediaType.IMAGE,MediaType.from);
			media.setMsgContent(saveImage(bMap));
			media.setFriend(userName);
			media.setTime(getTime());
			if(UserManager.CurSessionFriList.containsKey(userName))
			{
				 UserManager.CurSessionFriList.get(userName).add(media);
				 if(DEBUG) Log.d(TAG,"adding to the list");
			}
			else{
				List<MediaType> newmsgbox = new ArrayList<MediaType>();
				newmsgbox.add(media);
				UserManager.CurSessionFriList.put(userName, newmsgbox);
				if(DEBUG) Log.d(TAG,"adding to the list with new image");
			}
			if(UserManager.activeFriend!=null&&UserManager.activeFriend.equals(userName)){
				ListContentEntity entity = new ListContentEntity(userName,media.getTime(),"",ListContentEntity.MESSAGE_FROM_PICTURE,media.getMsgContent());
				ChatView.list.add(entity);
				//TODO:Notify the user
			}else{
				ServiceManager.showNotification(R.drawable.new_msg_notification, R.drawable.new_msg_notification,
						IMPSDev.getContext().getResources().getString(R.string.new_image_received_from_friend,media.getFriend()),
						ChatView.class,media.getFriend());
			}
			usersImageData.remove(key);
		}
	}
	
	private byte[] bitmapToBytes(Bitmap map){
		//map.
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		map.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		return baos.toByteArray();
	}
	private Bitmap bytesToBitmap(byte[] bytes){
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
	}
	private String getTime(){
		Date now = new Date();
		SimpleDateFormat d1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    String date = d1.format(now);
	    return date;
	}
    public String saveImage(Bitmap finalMap){
    	String res = null;
    	Long timestamp = System.currentTimeMillis();
    	
    	res ="/sdcard/imps/handwriting/recv/image/" + timestamp.toString()+".jpeg";
    	File destDir = new File("/sdcard/imps/handwriting/recv/image");
    	if(!destDir.exists())
    	{
    		destDir.mkdirs();
    	}
    	FileOutputStream fos;
    	try {
			fos = new FileOutputStream(res);
			if(finalMap!=null&&finalMap.compress(Bitmap.CompressFormat.JPEG, 100, fos)){
				//Toast.makeText(getApplicationContext(), "save successfully...", Toast.LENGTH_LONG);
				Log.d(TAG, "succ..."+res);
                fos.close();			
			}
			else{
				//Toast.makeText(getApplicationContext(), "save failed...", Toast.LENGTH_LONG);
				Log.d(TAG, "failed..."+res);
				 fos.close();
			}   			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	return res;
    }
}

