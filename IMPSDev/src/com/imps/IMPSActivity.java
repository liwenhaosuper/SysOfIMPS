package com.imps;

import android.app.Activity;
import android.os.Bundle;

import com.imps.services.impl.ServiceManager;

public class IMPSActivity extends Activity{
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		ServiceManager.getmScreen().addScreen(this.getClass());
	}
	@Override
	public void onStop(){
		super.onStop();
		ServiceManager.getmScreen().removeScreen(this.getClass());
	}
}
