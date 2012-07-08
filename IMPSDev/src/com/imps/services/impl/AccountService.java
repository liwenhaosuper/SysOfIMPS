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
import com.imps.net.tcp.ConnectionService;

public class AccountService {
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
		}
		if(ConnectionService.getChannel().isConnected()){
			ConnectionService.getChannel().write(ChannelBuffers.wrappedBuffer(
					MessageFactory.createCLoginReq(userName, userPwd).build()));
		}else{
			if(DEBUG)Log.d(TAG,"Login():not connected...");
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
	public void updateUserInfo(User user){
		if(isConnected){
			if(DEBUG)Log.d(TAG,"Update user info: sent...");
			ConnectionService.getChannel().write(ChannelBuffers.wrappedBuffer(
				MessageFactory.createCUpdateUserInfoReq(user.getUsername(), user.getGender(), user.getEmail()).build()));
		}
	}

	public void onConnected() {
		isConnected = true;
		if(DEBUG)Log.d(TAG,"onConnected...");
	}

	public void onDisconnected() {
		isConnected = false;
		if(DEBUG)Log.d(TAG,"onDisconnected...");
	}
	public void onLoginError() {
		isConnected = true;
		autoAuth = false;
	}
	public void onLoginSuccess() {
		if(DEBUG)Log.d(TAG,"Log succ...");
		isLogined = true;
		autoAuth = true;
	}
}
