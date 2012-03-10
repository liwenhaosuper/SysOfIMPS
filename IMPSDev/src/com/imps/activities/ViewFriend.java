package com.imps.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.imps.IMPSActivity;
import com.imps.R;
import com.imps.basetypes.User;
import com.imps.net.handler.UserManager;
import com.imps.ui.widget.ChattingAdapter;

public class ViewFriend extends IMPSActivity{
	protected static final String TAG = ChattingAdapter.class.getCanonicalName();
	private String fUsername;
	private User user;
	@Override
	public void onResume()
	{
		super.onResume();
	}
	@Override
	public void onPause()
	{   
		super.onPause();
	}
	@Override
	public void onStop(){
		super.onStop();

	}
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.friend_info);
		Intent fi = this.getIntent();
		fUsername = fi.getStringExtra("fUsername");
		Log.d(TAG,"view friend fUsername is :"+fUsername);
		for(int i=0;i<UserManager.AllFriList.size();i++){
			Log.d(TAG,"AllFriList["+i+"] is:"+UserManager.AllFriList.get(i).getUsername());
			if(fUsername.equals(UserManager.AllFriList.get(i).getUsername()))
			{
				user=UserManager.AllFriList.get(i);
				break;
			}
		}
		ImageView iv = (ImageView) findViewById(R.id.portrait1);
		
		TextView tv = (TextView) findViewById(R.id.portrait_name1);
		tv.setText(user.getUsername());
		tv = (TextView) findViewById(R.id.portrait_sex1);
		if (user.getGender() == 1)
			tv.setText(R.string.male);
		else
			tv.setText(R.string.female);
		tv = (TextView) findViewById(R.id.email_user1);
		tv.setText(user.getEmail());
	}
}
