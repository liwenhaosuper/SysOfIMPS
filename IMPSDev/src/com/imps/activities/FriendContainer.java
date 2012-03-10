package com.imps.activities;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;

import com.imps.R;
import com.imps.receivers.IMPSBroadcastReceiver;
import com.imps.services.impl.ServiceManager;

public class FriendContainer extends TabActivity{
	private TabHost tabHost;
	private RadioGroup navigator;
	private IMPSBroadcastReceiver receiver = new IMPSBroadcastReceiver();
	public static final String TAB_FRIENDLIST="friendlist";
	public static final String TAB_FINDFRIEND="findfriend";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.friendcontainer);
		ServiceManager.getmScreen().addScreen(this.getClass());
		registerReceiver(receiver,receiver.getFilter());
		navigator = (RadioGroup)findViewById(R.id.tab_navigator);
		tabHost = getTabHost();
		tabHost.addTab(tabHost.newTabSpec(TAB_FRIENDLIST)
                .setIndicator(TAB_FRIENDLIST)
                .setContent(new Intent(this,FriendListTab.class)));
		tabHost.addTab(tabHost.newTabSpec(TAB_FINDFRIEND)
                .setIndicator(TAB_FINDFRIEND)
                .setContent(new Intent(this,FindFriend.class)));
		tabHost.setCurrentTab(0);
		navigator.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.radio_friendlist:
					tabHost.setCurrentTabByTag(TAB_FRIENDLIST);
					break;
				case R.id.radio_findfriend:
					tabHost.setCurrentTabByTag(TAB_FINDFRIEND);
					break;
				default:
					break;
				}
			}
		});
	}
	@Override 
	public void onResume(){
		super.onResume();
		registerReceiver(receiver,receiver.getFilter());
	}
	@Override
	public void onStop(){
		super.onStop();
		unregisterReceiver(receiver);
		ServiceManager.getmScreen().removeScreen(this.getClass());
	}
}
