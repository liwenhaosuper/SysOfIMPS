package com.imps.services.impl;

import java.util.Timer;
import java.util.TimerTask;

import org.jboss.netty.buffer.ChannelBuffers;

import android.util.Log;

import com.imps.IMPSDev;
import com.imps.basetypes.User;
import com.imps.basetypes.UserStatus;
import com.imps.events.IConnEvent;
import com.imps.events.ILoginEvent;
import com.imps.net.handler.MessageFactory;

public class AccountService implements IConnEvent{
	private static boolean DEBUG =IMPSDev.isDEBUG();
	private static String TAG = AccountService.class.getCanonicalName();
	public boolean isConnected = false;
	public boolean isLogined = false;
	public static boolean autoAuth = false;
	public static String userName;
	public static String userPwd;
	public AccountService(){
	}
	public boolean start(){
		ServiceManager.getmNetLogic().addConnEventHandler(this);
		return true;
	}
	public boolean isConnected(){
		return isConnected;
	}
	public boolean isLogined(){
		return isLogined;
	}
	public void login(String username,String password){
		if(!ServiceManager.getmNet().isAvailable()){
			return;
		}
		userName = username;
		userPwd = password;
		if(isConnected==false){
			if(DEBUG)Log.d(TAG,"Login():not connected...fireConnect...");
			ConnectionService.fireConnect();
			autoAuth = true;
			Timer timer = new Timer();
			timer.schedule(new TimerTask(){
				@Override
				public void run(){
					if(!isLogined)
						login(userName,userPwd);
				}
			},3000);
		}
		if(ConnectionService.getChannel().isConnected()){
			ConnectionService.getChannel().write(ChannelBuffers.wrappedBuffer(
					MessageFactory.createCLoginReq(userName, userPwd).build()));
		}
	}
	public void logout(){
		if(isConnected&&isLogined&&ConnectionService.getChannel()!=null&&ConnectionService.getChannel().isConnected()){
			ConnectionService.getChannel().write(ChannelBuffers.wrappedBuffer(
					MessageFactory.createCStatusNotify(userName, UserStatus.OFFLINE).build()));
		}
		isLogined = false;
	}
	public void register(User user){
		if(isConnected){
			if(DEBUG)Log.d(TAG,"Reg:sent...");
			ConnectionService.getChannel().write(ChannelBuffers.wrappedBuffer(
				MessageFactory.createCRegisterReq(user.getUsername(), user.getPassword(), user.getGender(), user.getEmail()).build()));
		}
	}
	@Override
	public void onConnected() {
		// TODO Auto-generated method stub
		isConnected = true;
		if(autoAuth){
			if(DEBUG)Log.d(TAG,"loginning...");
			login(userName,userPwd);
		}
		if(DEBUG)Log.d(TAG,"onConnected...");
	}
	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		isConnected = false;
		isLogined = false;
		if(DEBUG)Log.d(TAG,"onDisconnected...");
	}
	public void onLoginError() {
		isConnected = true;
		isLogined = false;
		autoAuth = false;
	}
	public void onLoginSuccess() {
		if(DEBUG)Log.d(TAG,"Log succ...");
		isLogined = true;
		autoAuth = true;
	}
}
