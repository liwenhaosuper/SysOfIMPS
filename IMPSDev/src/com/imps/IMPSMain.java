package com.imps;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
public class IMPSMain extends IMPSActivity {

	private static String TAG = IMPSMain.class.getCanonicalName();
	private static boolean DEBUG = IMPSDev.isDEBUG();
	private GifView gv;
	private IMPSMainTask task = new IMPSMainTask();
	@Override 
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
        gv = (GifView)findViewById(R.id.earth);
        if(DEBUG) Log.d(TAG,"onCreate");
        
		if(ServiceManager.isStarted&&ServiceManager.getmAccount()!=null&&ServiceManager.getmAccount().isLogined()){
			startMainActivity();
			finish();
		}else{
			gv.setGifImage(R.drawable.earth);
			if(task!=null&&task.getStatus()==AsyncTask.Status.RUNNING){
				task.cancel(true);
			}
			task = new IMPSMainTask();
			task.execute();
		}

	}
	@Override
	public void onStop(){
		super.onStop();
		if(task!=null&&task.getStatus()==AsyncTask.Status.RUNNING){
			task.cancel(true);
		}
	}
	public class IMPSMainTask extends AsyncTask<Integer,Integer,Integer>{

		@Override
		protected Integer doInBackground(Integer... params) {
			publishProgress();
			ServiceManager.start();
			ServiceManager.getmConfig().setupDefault();
			UserManager.setGlobaluser(new User());
			UserManager.getGlobaluser().setUsername("test");
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		public void onProgressUpdate(Integer... params){
		}
		@Override
		protected void onPostExecute(Integer params){
			//startLoginActivity();
			if(ServiceManager.getmConfig().getPreferences().getBoolean(
					ConfigurationService.PREFERENCE_SHOW_PRELAUNCH_ACTIVITY,true))
			{
				//startPreLaunchActivity();
				startLoginActivity();
			}else{
				startLoginActivity();
			}
		}
		@Override
		protected void onPreExecute(){
			gv.showAnimation();
			ServiceManager.start();
		}
		
	}
	
	public void startLoginActivity(){
        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
	}
	public void startMainActivity(){
        Intent intent = new Intent(this, IMPSContainer.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
