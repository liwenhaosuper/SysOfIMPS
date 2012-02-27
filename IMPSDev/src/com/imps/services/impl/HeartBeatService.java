package com.imps.services.impl;

import java.util.Timer;
import java.util.TimerTask;

import org.jboss.netty.buffer.ChannelBuffers;

import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.imps.IMPSDev;
import com.imps.basetypes.GeoLocation;
import com.imps.net.handler.MessageFactory;
import com.imps.net.handler.UserManager;
import com.imps.services.IHeartBeatService;

public class HeartBeatService extends TimerTask implements IHeartBeatService{
	private static boolean DEBUG = IMPSDev.isDEBUG();
	private static String TAG = HeartBeatService.class.getCanonicalName();
	private boolean isStarted = false;
	public static int INTERNAL = 1000*60*10;
	public static int DELAY = 1000*60*1;
	private Timer timer;
	@Override
	public boolean isStarted() {
		return isStarted;
	}
	@Override
	public boolean start() {
		if(isStarted){
			return true;
		}
		isStarted = true;
		if(timer!=null){
			 timer.cancel();
		}
		timer= new Timer();
		timer.schedule(this,DELAY,INTERNAL);
		return true;
	}
	@Override
	public boolean stop() {
		isStarted = false;
		timer.cancel();
		return false;
	}
	@Override
	public void fireBeat(GeoPoint location) {
		String username = UserManager.getGlobaluser().getUsername();
		if(location==null||username==null||username.equals("")||!ServiceManager.getmNet().isAvailable()){
			return;
		}
		if(ServiceManager.getmTcpConn().getChannel()!=null&&ServiceManager.getmTcpConn().getChannel().isConnected()){
			ServiceManager.getmTcpConn().getChannel().write(ChannelBuffers.wrappedBuffer(
					MessageFactory.createCHeartbeatReq(username, location.getLatitudeE6(), location.getLongitudeE6()).build())
					);
			if(DEBUG) Log.d(TAG,"Heartbeat sent...");
		}
	}
	@Override
	public void run() {
		if(isStarted){
			String username = UserManager.getGlobaluser().getUsername();
			if(username==null||username.equals("")||!ServiceManager.getmNet().isAvailable()){
				return;
			}
			GeoLocation location = null;
			if(ServiceManager.getmGPS().isStarted()){
				location = ServiceManager.getmGPS().getGeoLocation();
			}
			if(location==null){
				location = ServiceManager.getmBsstion().getGeoLocation();
			}
			if(location==null){
				return;
			}
			if(ServiceManager.getmTcpConn().getChannel()!=null&&ServiceManager.getmTcpConn().getChannel().isConnected()){
				ServiceManager.getmTcpConn().getChannel().write(ChannelBuffers.wrappedBuffer(
						MessageFactory.createCHeartbeatReq(username, location.getLatitudeE6(), location.getLongitudeE6()).build())
						);
				if(DEBUG) Log.d(TAG,"Heartbeat sent...");
			}
		}
	}
	public void setTimer(Timer timer) {
		this.timer = timer;
	}
	public Timer getTimer() {
		return timer;
	}
}
