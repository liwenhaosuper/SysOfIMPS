package com.imps.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.imps.R;
import com.imps.handler.UserManager;

public class AddFriend extends Activity{
	private EditText friendText;
	
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
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addfriend);
		setTitle(this.getResources().getString(R.string.add_new_fri));
		Button addFriButton = (Button)findViewById(R.id.addFriend);
		Button cancelButton = (Button)findViewById(R.id.cancel);
		friendText = (EditText)findViewById(R.id.newFriendUsername);
		
		addFriButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(friendText.length()>0)
				{
					UserManager.getInstance().AddFriendRequest(friendText.getText().toString());
					Toast.makeText(AddFriend.this, R.string.request_sent, Toast.LENGTH_SHORT).show();
					finish();
				}
			}
			
		});
		cancelButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			    finish();	
			}
			
		});		
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
