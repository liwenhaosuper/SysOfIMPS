package com.imps.activities;



import com.imps.R;
import com.imps.activities.FriendTab.ExitReceiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

public class Other extends Activity {
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friendtab);
	}
	@Override
	public void onResume()
	{
		super.onResume();
		IntentFilter ifilter = new IntentFilter();
		ifilter.addAction("exit");
		registerReceiver(exitReceiver,ifilter);
	}
	@Override
	public void onStop()
	{
		super.onStop();
		unregisterReceiver(exitReceiver);
	}
	
	public class ExitReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if("exit".equals(intent.getAction()))
			{
				finish();
			}
		}		
	}
	private ExitReceiver exitReceiver = new ExitReceiver();
}
