package com.imps.services.impl;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.util.Log;

import com.imps.IMPSDev;
import com.imps.services.IConfigurationService;

/**
 * Copyright (C) 2010-2020 IMPS Development Team
 * @author liwenhaosuper
 *
 */
public class ConfigurationService implements IConfigurationService{

	public static final String TAG = ConfigurationService.class.getCanonicalName();
	public static final boolean DEBUG = IMPSDev.isDEBUG();
	
	public static final String PREFERENCE_SHOW_PRELAUNCH_ACTIVITY = "show_prelaunch_activity";
	public static final String PREFERENCE_DUMPCATCHER_CLIENT = "dumpcatcher_client";
	public static final String GENERAL_AUTOSTART = "auto_start";
	
	public static final String SERVERADDRESS = "server_ip";
	public static final String SERVERPORT = "server_port";
	
	public static final String LoginUserName = "";
	public static final String LoginPassword = "";
	
	public static final String REMEMBERPASSWORD = "remember_password";
	public static final String AUTOLOGIN = "auto_login";
	
	private boolean started = true;
	private SharedPreferences preferences = null;
	
	public ConfigurationService(SharedPreferences pre){
		this.preferences = pre;
	}
	
	public void setPreferences(SharedPreferences preferences) {
		this.preferences = preferences;
	}
	@Override
	public SharedPreferences getPreferences() {
		return preferences;
	}

	public boolean isShowPrelaunchAct(){
		if(preferences==null){
			return true;
		}
		if(preferences.contains(PREFERENCE_SHOW_PRELAUNCH_ACTIVITY)){
			if(!preferences.getBoolean(PREFERENCE_SHOW_PRELAUNCH_ACTIVITY, true))
				return false;
		}
		return true;
	}
	
	public void setupDefault(){
		Editor editor = preferences.edit();
		if(!preferences.contains(PREFERENCE_SHOW_PRELAUNCH_ACTIVITY)){
			editor.putBoolean(PREFERENCE_SHOW_PRELAUNCH_ACTIVITY, true);
		}
		if(!preferences.contains(GENERAL_AUTOSTART)){
			editor.putBoolean(GENERAL_AUTOSTART, true);
		}
		if(!preferences.contains(SERVERADDRESS)){
			editor.putString(SERVERADDRESS, "169.254.95.183");
		}
		if(!preferences.contains(SERVERPORT)){
			editor.putInt(SERVERPORT, 1200);
		}
		if(!preferences.contains(REMEMBERPASSWORD)){
			editor.putBoolean(REMEMBERPASSWORD, false);
		}
		if(!preferences.contains(AUTOLOGIN)){
			editor.putBoolean(AUTOLOGIN, false);
		}
		editor.commit();
	}
    public void logIn(String username,String password){
    	if(DEBUG){
    		Log.d(TAG, "Trying to log in");
    	}
    	Editor editor = preferences.edit();
		editor.putString(LoginUserName, username);
		editor.putString(LoginPassword, password);
		editor.commit();
    }
    
    public void logOut(){
    	if(DEBUG){
    		Log.d(TAG, "Trying to log out");
    	}
    	Editor editor = preferences.edit();
    	editor.clear().commit();
    }
	@Override
	public boolean isStarted() {
		// TODO Auto-generated method stub
		return preferences==null?false:(started?true:false);
	}
	@Override
	public boolean start() {
		// TODO Auto-generated method stub
		if(preferences==null)
			started = false;
		else started = true;
		return started;
	}
	@Override
	public boolean stop() {
		// TODO Auto-generated method stub
		started = false;
		return true;
	}




}
