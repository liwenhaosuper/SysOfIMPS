package com.imps;

import com.imps.services.impl.ServiceManager;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Build;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
/**
 * Copyright (C) 2010-2020 IMPS Development Team
 * @author liwenhaosuper
 *
 */
public class IMPSDev extends Application {
    /** Called when the activity is first created. */
	private static IMPSDev instance;
	private static PackageManager packageManager;
    private static String packageName;
    private static String deviceURN;
    private static int sdkVersion;
    private static boolean DEBUG = true;
    private static String TAG = IMPSDev.class.getCanonicalName();
    
    public IMPSDev(){
        IMPSDev.instance = this;
    }
    public static Context getContext() {
        return IMPSDev.instance;
    }
	@Override 
	public void onCreate(){
		super.onCreate();
		IMPSDev.packageManager = IMPSDev.instance.getPackageManager();    		
		IMPSDev.packageName = IMPSDev.instance.getPackageName();
		if(DEBUG){Log.d(TAG, "OnCreate...");}
		java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
		if(ServiceManager.notifManager == null){
			ServiceManager.notifManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			if(DEBUG) Log.d(TAG,"Notify manager is created");
		}
		if(DEBUG) Log.d(TAG,"TEL:"+getDeviceURN());
	}
	@Override
	public void onTerminate(){
		if(DEBUG){Log.d(TAG, "onTerminate...");}
		ServiceManager.stop();
	}
    public static int getSDKVersion(){
    	if(IMPSDev.sdkVersion == 0){
    		IMPSDev.sdkVersion = Integer.parseInt(Build.VERSION.SDK);
    	}
    	return IMPSDev.sdkVersion;
    }
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
    }
    public static String getVersionName(){
    	if(IMPSDev.packageManager != null){
    		try {
				return IMPSDev.packageManager.getPackageInfo(IMPSDev.packageName, 0).versionName;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
    	}
    	return "0.0";
    }
    public static String getDeviceURN(){
    	if(IMPSDev.deviceURN==null||IMPSDev.deviceURN.equals("")){
	    	try{
		    	TelephonyManager telephonyMgr = (TelephonyManager) IMPSDev.getContext().getSystemService(Context.TELEPHONY_SERVICE);
		        String msisdn = telephonyMgr.getLine1Number();
		        if(msisdn == null){
		        	IMPSDev.deviceURN = String.format("urn:imei:%s", telephonyMgr.getDeviceId());
		        }
		        else{
		        	IMPSDev.deviceURN = String.format("urn:tel:%s", msisdn);
		        }
	    	}
	    	catch(Exception e){
	    		Log.d("com.imps", e.toString());
	    		IMPSDev.deviceURN = "null";
	    	}
    	}
    	return IMPSDev.deviceURN;
    }
	public static void setDEBUG(boolean dEBUG) {
		DEBUG = dEBUG;
	}
	public static boolean isDEBUG() {
		return DEBUG;
	}
	public static SharedPreferences getPreferences(){
		return PreferenceManager.getDefaultSharedPreferences(instance);
	}
}