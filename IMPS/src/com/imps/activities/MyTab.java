package com.imps.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

import com.imps.R;
import com.imps.activities.FriendTab.ExitReceiver;
import com.imps.base.User;
import com.imps.handler.UserManager;
import com.imps.main.Client;
public class MyTab extends Activity{
	public static Client client;
	private Context context;
	private TextView tv;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mytab);
		User me=UserManager.getGlobaluser();
		tv=(TextView)findViewById(R.id.textView000);
		String gender=me.getGender()==0?"男":"女";
		tv.setText("个人资料：/n"+"姓名: "+me.getUsername()+"\n 性别"+gender+"\n 邮件"+me.getEmail());
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
