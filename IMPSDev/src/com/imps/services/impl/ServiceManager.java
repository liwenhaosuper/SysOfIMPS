package com.imps.services.impl;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.imps.IMPSDev;
import com.imps.IMPSMain;
import com.imps.R;
import com.imps.events.IConnEvent;
import com.imps.net.handler.NetMsgLogicHandler;

public class ServiceManager extends Service implements IConnEvent{
    //services
	private static BaseStationService mBsstion;
	private static GPSService mGPS;
	private static SoundService mSound;
	private static ConfigurationService mConfig;
	private static ContactService mContact;
	private static SmsService mSms;
	private static P2PService mMedia;
	private static P2PAudioService mAudio;
	private static NetworkService mNet;
	private static NetMsgLogicHandler mNetLogic;
	private static ConnectionService mTcpConn;
	private static AccountService mAccount;
	//TAG
	public static String TAG = ServiceManager.class.getCanonicalName();
	public static boolean DEBUG = IMPSDev.isDEBUG();
	
	public static boolean isStarted = false;
	public static NotificationManager notifManager;
	
	public static final int NOTIF_OFFLINE_ID = R.drawable.status_offline;
	public static final int NOTIF_ONLINE_ID = R.drawable.status_online;
	public static final int NOTIF_HIDE_ID = R.drawable.status_hide;
	public static final int NOTIF_LOGGINGIN_ID = R.drawable.status_login;
	
	public static final int NOTIF_SMS_ID = R.drawable.new_msg_notification;
	public static final int NOTIF_VIDEO_ID = R.drawable.new_video_notification;
	public static final int NOTIF_AUDIO_ID = R.drawable.new_audio_notification;
	public static final int NOTIF_CONTACT_ID = R.drawable.new_contact_notification;

	
	public static void init(){
		if(isStarted){
			return;
		}
		setmTcpConn(new ConnectionService("59.78.23.15",1200));
		setmNetLogic(new NetMsgLogicHandler());
		setmBsstion(new BaseStationService());
		setmGPS(new GPSService());
		setmSound(new SoundService());
		setmConfig(new ConfigurationService(IMPSDev.getPreferences()));
		setmContact(new ContactService());
		setmSms(new SmsService());
		P2PService.init("59.78.23.73",1300);
		setmMedia(P2PService.getInstance());
		setmAudio(new P2PAudioService());
		setmNet(new NetworkService());
		setmAccount(new AccountService());
	}
	

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onCreate(){
		super.onCreate();
		if(ServiceManager.notifManager == null){
			ServiceManager.notifManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			if(DEBUG) Log.d(TAG,"Notify manager is created");
		}
	}
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Bundle bundle = intent.getExtras();
		if(bundle != null && bundle.getBoolean("autostarted")){
			if(DEBUG) Log.d(TAG,"AutoStart...");
			ServiceManager.start();
		}else{
			init();
		}
		if(DEBUG) Log.d(TAG,"onStart is called...");
		if(ServiceManager.notifManager == null){
			ServiceManager.notifManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			if(DEBUG) Log.d(TAG,"Notify manager is created");
		}
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	public static boolean start(){
		if(ServiceManager.isStarted){
			return true;
		}
		init();
		// Start Android service
		IMPSDev.getContext().startService(
				new Intent(IMPSDev.getContext(), ServiceManager.class));
		boolean success = true;
		success&=ServiceManager.getmTcpConn().startTcp();
		success&= ServiceManager.getmAccount().start();
		//success&= ServiceManager.getmBsstion().start();
		success&= ServiceManager.getmConfig().start();
		success&= ServiceManager.getmNet().start();
		success&= ServiceManager.getmContact().start();
		//success&= ServiceManager.getmGPS().start();
		success&= ServiceManager.getmMedia().start();		
		success&= ServiceManager.getmSms().start();
		success&= ServiceManager.getmSound().start();
		if(!success&&DEBUG){
			Log.d(TAG,"fail to start all services.");
		}else if(DEBUG){
			Log.d(TAG,"services have been started.");
		}
		ServiceManager.isStarted = true;
		return true;
	}
	public static boolean stop(){
		if(!ServiceManager.isStarted){
			return true;
		}
		// stops Android service
		IMPSDev.getContext().stopService(
				new Intent(IMPSDev.getContext(), ServiceManager.class));
		isStarted = false;
		boolean success = true;
		success&= ServiceManager.getmTcpConn().stopTcp();
		ServiceManager.getmAccount().logout();
		success&= ServiceManager.getmBsstion().stop();
		success&= ServiceManager.getmConfig().stop();
		success&= ServiceManager.getmNet().stop();
		success&= ServiceManager.getmContact().stop();
		success&= ServiceManager.getmGPS().stop();
		success&= ServiceManager.getmMedia().stop();
		success&= ServiceManager.getmSms().stop();
		success&= ServiceManager.getmSound().stop();
		if(!success&&DEBUG){
			Log.d(TAG,"fail to stop all services.");
		}else if(DEBUG){
			Log.d(TAG,"services have been stopped.");
		}
		return true;
	}
	
	public static void showNotification(int notifId, int drawableId, String tickerText,Class<?> cls,String... param)
	{
		Notification notification = new Notification(drawableId,"",System.currentTimeMillis());
        Intent intent = new Intent(IMPSDev.getContext(), cls==null?IMPSMain.class:cls);
        if(param!=null){
            intent.putExtra("fUsername",param[0]);
            if(param.length>=3){
            	intent.putExtra("ip", param[1]);
            	intent.putExtra("port", Integer.parseInt(param[2]));
            }
        }
    	intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP  | Intent.FLAG_ACTIVITY_NEW_TASK);
        switch(notifId){
    	case NOTIF_OFFLINE_ID:
    		ServiceManager.notifManager.cancel(ServiceManager.NOTIF_ONLINE_ID);
    		ServiceManager.notifManager.cancel(ServiceManager.NOTIF_HIDE_ID);
    		ServiceManager.notifManager.cancel(ServiceManager.NOTIF_LOGGINGIN_ID);
    		notification.flags |= Notification.FLAG_NO_CLEAR /*| Notification.FLAG_SHOW_LIGHTS*/;
    		break;
    	case NOTIF_HIDE_ID:
    		ServiceManager.notifManager.cancel(ServiceManager.NOTIF_ONLINE_ID);
    		ServiceManager.notifManager.cancel(ServiceManager.NOTIF_OFFLINE_ID);
    		ServiceManager.notifManager.cancel(ServiceManager.NOTIF_LOGGINGIN_ID);
    		notification.flags |= Notification.FLAG_NO_CLEAR;
    		break;
    	case NOTIF_ONLINE_ID:
    		ServiceManager.notifManager.cancel(ServiceManager.NOTIF_OFFLINE_ID);
    		ServiceManager.notifManager.cancel(ServiceManager.NOTIF_HIDE_ID);
    		ServiceManager.notifManager.cancel(ServiceManager.NOTIF_LOGGINGIN_ID);
    		notification.flags |= Notification.FLAG_NO_CLEAR;
    		break;
    	case NOTIF_LOGGINGIN_ID:
    		ServiceManager.notifManager.cancel(ServiceManager.NOTIF_ONLINE_ID);
    		ServiceManager.notifManager.cancel(ServiceManager.NOTIF_HIDE_ID);
    		ServiceManager.notifManager.cancel(ServiceManager.NOTIF_OFFLINE_ID);
    		notification.flags |= Notification.FLAG_NO_CLEAR;
    		break;
    	case NOTIF_SMS_ID:
    		notification.flags |= Notification.FLAG_AUTO_CANCEL;
    		break;
    	case NOTIF_VIDEO_ID:
    		notification.flags |= Notification.FLAG_AUTO_CANCEL;
    		break;
    	case NOTIF_AUDIO_ID:
    		notification.flags |= Notification.FLAG_AUTO_CANCEL;
    		break;
    	case NOTIF_CONTACT_ID:
    		notification.flags |=Notification.FLAG_AUTO_CANCEL;
    		break;
   		default:
   			break;
        }
        PendingIntent contentIntent = PendingIntent.getActivity(IMPSDev.getContext(), notifId/*requestCode*/, intent, PendingIntent.FLAG_UPDATE_CURRENT);     
        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(IMPSDev.getContext(),"IMPS", tickerText, contentIntent);
        // Send the notification.
        // We use a layout id because it is a unique number.  We use it later to cancel.
        if(ServiceManager.notifManager==null){
        	if(DEBUG) Log.d(TAG,"Notify manager is null");
        }else
        	ServiceManager.notifManager.notify(notifId, notification);
	}
	public static void showNotification(int notifId, int drawableId, String tickerText,Class<?> cls,int tag)
	{
		Notification notification = new Notification(drawableId,"",System.currentTimeMillis());
        Intent intent = new Intent(IMPSDev.getContext(), cls==null?IMPSMain.class:cls);
        intent.putExtra("TAG", tag);
        switch(notifId){
    	case NOTIF_OFFLINE_ID:
    		ServiceManager.notifManager.cancel(ServiceManager.NOTIF_ONLINE_ID);
    		ServiceManager.notifManager.cancel(ServiceManager.NOTIF_HIDE_ID);
    		ServiceManager.notifManager.cancel(ServiceManager.NOTIF_LOGGINGIN_ID);
    		notification.flags |= Notification.FLAG_NO_CLEAR /*| Notification.FLAG_SHOW_LIGHTS*/;
    		break;
    	case NOTIF_HIDE_ID:
    		ServiceManager.notifManager.cancel(ServiceManager.NOTIF_ONLINE_ID);
    		ServiceManager.notifManager.cancel(ServiceManager.NOTIF_OFFLINE_ID);
    		ServiceManager.notifManager.cancel(ServiceManager.NOTIF_LOGGINGIN_ID);
    		notification.flags |= Notification.FLAG_NO_CLEAR;
    		break;
    	case NOTIF_ONLINE_ID:
    		ServiceManager.notifManager.cancel(ServiceManager.NOTIF_OFFLINE_ID);
    		ServiceManager.notifManager.cancel(ServiceManager.NOTIF_HIDE_ID);
    		ServiceManager.notifManager.cancel(ServiceManager.NOTIF_LOGGINGIN_ID);
    		notification.flags |= Notification.FLAG_NO_CLEAR;
    		break;
    	case NOTIF_LOGGINGIN_ID:
    		ServiceManager.notifManager.cancel(ServiceManager.NOTIF_ONLINE_ID);
    		ServiceManager.notifManager.cancel(ServiceManager.NOTIF_HIDE_ID);
    		ServiceManager.notifManager.cancel(ServiceManager.NOTIF_OFFLINE_ID);
    		notification.flags |= Notification.FLAG_NO_CLEAR;
    		break;
    	case NOTIF_SMS_ID:
    		notification.flags |= Notification.FLAG_AUTO_CANCEL;
    		break;
    	case NOTIF_VIDEO_ID:
    		notification.flags |= Notification.FLAG_AUTO_CANCEL;
    		break;
    	case NOTIF_AUDIO_ID:
    		notification.flags |= Notification.FLAG_AUTO_CANCEL;
    		break;
    	case NOTIF_CONTACT_ID:
    		notification.flags |=Notification.FLAG_AUTO_CANCEL;
    		break;
   		default:
   			break;
        }
        PendingIntent contentIntent = PendingIntent.getActivity(IMPSDev.getContext(), notifId/*requestCode*/, intent, PendingIntent.FLAG_UPDATE_CURRENT);     
        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(IMPSDev.getContext(),"IMPS", tickerText, contentIntent);
        // Send the notification.
        // We use a layout id because it is a unique number.  We use it later to cancel.
        if(ServiceManager.notifManager==null){
        	if(DEBUG) Log.d(TAG,"Notify manager is null");
        }else
        	ServiceManager.notifManager.notify(notifId, notification);
	}
	public static void broadcast(Intent intent){
		if(DEBUG) Log.d(TAG, "New broadcast sent...");
		IMPSDev.getContext().sendBroadcast(intent);
	}
	
	
	public NotificationManager getNotifyManager(){
		return ServiceManager.notifManager;
	}
	
	public static void setmGPS(GPSService mGPS) {
		ServiceManager.mGPS = mGPS;
	}

	public static GPSService getmGPS() {
		if(!mGPS.isStarted()){
			mGPS.start();
		}
		return mGPS;
	}

	public static void setmSound(SoundService mSound) {
		ServiceManager.mSound = mSound;
	}

	public static SoundService getmSound() {
		if(!mSound.isStarted()){
			mSound.start();
		}
		return mSound;
	}

	public static void setmConfig(ConfigurationService mConfig) {
		ServiceManager.mConfig = mConfig;
	}

	public static ConfigurationService getmConfig() {
		if(!mConfig.isStarted()){
			mConfig.start();
		}
		return mConfig;
	}

	public static void setmContact(ContactService mContact) {
		ServiceManager.mContact = mContact;
	}

	public static ContactService getmContact() {
		if(!mContact.isStarted()){
			mContact.start();
		}
		return mContact;
	}

	public static void setmSms(SmsService mSms) {
		ServiceManager.mSms = mSms;
	}

	public static SmsService getmSms() {
		if(!mSms.isStarted()){
			mSms.start();
		}
		return mSms;
	}

	public static void setmMedia(P2PService mMedia) {
		ServiceManager.mMedia = mMedia;
	}

	public static P2PService getmMedia() {
		if(!mMedia.isStarted()){
			mMedia.start();
		}
		return mMedia;
	}
	public static void setmBsstion(BaseStationService mBsstion) {
		ServiceManager.mBsstion = mBsstion;
	}

	public static BaseStationService getmBsstion() {
		if(!mBsstion.isStarted()){
			mBsstion.start();
		}
		return mBsstion;
	}


	public static void setmNet(NetworkService mNet) {
		ServiceManager.mNet = mNet;
	}


	public static NetworkService getmNet() {
		if(!mNet.isStarted()){
			mNet.start();
		}
		return mNet;
	}


	public static void setmNetLogic(NetMsgLogicHandler mNetLogic) {
		ServiceManager.mNetLogic = mNetLogic;
	}


	public static NetMsgLogicHandler getmNetLogic() {
		return mNetLogic;
	}


	public static void setmTcpConn(ConnectionService mTcpConn) {
		ServiceManager.mTcpConn = mTcpConn;
	}


	public static ConnectionService getmTcpConn() {
		return mTcpConn;
	}


	@Override
	public void onConnected() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onDisconnected() {
		showNotification(R.drawable.offline, R.drawable.offline,IMPSDev.getContext().getString(R.string.offline),
				null, null);
	}


	public static void setmAccount(AccountService mAccount) {
		ServiceManager.mAccount = mAccount;
	}


	public static AccountService getmAccount() {
		return mAccount;
	}


	public static void setmAudio(P2PAudioService mAudio) {
		ServiceManager.mAudio = mAudio;
	}


	public static P2PAudioService getmAudio() {
		return mAudio;
	}

}
