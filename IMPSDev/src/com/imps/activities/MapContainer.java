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

public class MapContainer extends TabActivity{
	private TabHost tabHost;
	private RadioGroup navigator;
	private IMPSBroadcastReceiver receiver = new IMPSBroadcastReceiver();
	public static final String TAB_FRIENDLOCATION="friendlocation";
	public static final String TAB_MYLOCATION="mylocation";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mapcontainer);
		registerReceiver(receiver,receiver.getFilter());
		navigator = (RadioGroup)findViewById(R.id.tab_navigator);
		tabHost = getTabHost();
		tabHost.addTab(tabHost.newTabSpec(TAB_FRIENDLOCATION)
                .setIndicator(TAB_FRIENDLOCATION)
                .setContent(new Intent(this,FriendLocation.class)));
		tabHost.addTab(tabHost.newTabSpec(TAB_MYLOCATION)
                .setIndicator(TAB_MYLOCATION)
                .setContent(new Intent(this,FindFriend.class)));
		tabHost.setCurrentTab(0);
		navigator.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.radio_friendlist:
					tabHost.setCurrentTabByTag(TAB_FRIENDLOCATION);
					break;
				case R.id.radio_findfriend:
					tabHost.setCurrentTabByTag(TAB_MYLOCATION);
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
	}
}
