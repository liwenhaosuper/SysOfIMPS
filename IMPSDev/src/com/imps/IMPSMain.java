package com.imps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;

import com.ant.liao.GifView;
import com.imps.activities.IMPSContainer;
import com.imps.activities.Login;
import com.imps.activities.PreLaunch;
import com.imps.basetypes.User;
import com.imps.net.handler.UserManager;
import com.imps.services.impl.ConfigurationService;
import com.imps.services.impl.ServiceManager;
/**
 * Copyright (C) 2010-2020 IMPS Development Team
 * @author liwenhaosuper
 *
 */
public class IMPSMain extends Activity {

	private static String TAG = IMPSMain.class.getCanonicalName();
	private static boolean DEBUG = IMPSDev.isDEBUG();
	private GifView gv;
	@Override 
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
        gv = (GifView)findViewById(R.id.earth);
        gv.setGifImage(R.drawable.earth);
        if(DEBUG) Log.d(TAG,"onCreate");
		if(ServiceManager.isStarted&&ServiceManager.getmAccount().isLogined()){
			startMainActivity();	
		}
		if(!ServiceManager.isStarted){
			if(DEBUG) Log.d(TAG,"ServiceManager.isStarted false");
		}
		if(!ServiceManager.getmAccount().isLogined()){
			if(DEBUG) Log.d(TAG,"ServiceManager.getmAccount().isLogined() false");
		}
        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
			@Override
			public void run() {
				if(ServiceManager.isStarted&&ServiceManager.getmAccount().isLogined()){
					return;
				}
				ServiceManager.start();
				ServiceManager.getmConfig().setupDefault();
				UserManager.setGlobaluser(new User());
				UserManager.getGlobaluser().setUsername("test");
				if(ServiceManager.getmConfig().getPreferences().getBoolean(
						ConfigurationService.PREFERENCE_SHOW_PRELAUNCH_ACTIVITY,true))
				{
					startPreLaunchActivity();
				}else{
					startLoginActivity();
				}
			}},1000);
	}
	
	public void startLoginActivity(){
        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
	}
	public void startMainActivity(){
        Intent intent = new Intent(this, IMPSContainer.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
	}
	public void startPreLaunchActivity(){
        Intent intent = new Intent(this, PreLaunch.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
	}
}
