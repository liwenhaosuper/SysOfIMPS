package com.imps.activities;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;

import com.imps.IMPSDev;
import com.imps.R;
import com.imps.receivers.IMPSBroadcastReceiver;

public class IMPSContainer extends TabActivity{
	private static String TAG = IMPSContainer.class.getCanonicalName();
	private static boolean DEBUG = IMPSDev.isDEBUG();
	private RadioGroup navigator;
	private TabHost tabHost;
	private IMPSBroadcastReceiver receiver = new IMPSBroadcastReceiver();
	public static final String TAB_FRIENDLIST="friendlist";
	public static final String TAB_CURRENTSESSIONS="currentsession";
	public static final String TAB_SYSMSG="sysmsg";
	public static final String TAB_MAP = "map";
	public static final String TAB_CARD = "card";
	
	public static final int SETTING = Menu.FIRST + 1;
	public static final int ABOUT = Menu.FIRST+2;
	public static final int EXIT = Menu.FIRST+3;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.maincontainer);
		registerReceiver(receiver,receiver.getFilter());
		navigator = (RadioGroup)findViewById(R.id.tab_navigator);
		tabHost = getTabHost();
		tabHost.addTab(tabHost.newTabSpec(TAB_FRIENDLIST)
                .setIndicator(TAB_FRIENDLIST)
                .setContent(new Intent(this,FriendContainer.class)));
		tabHost.addTab(tabHost.newTabSpec(TAB_CURRENTSESSIONS)
                .setIndicator(TAB_CURRENTSESSIONS)
                .setContent(new Intent(this,CurrentSessions.class)));
		tabHost.addTab(tabHost.newTabSpec(TAB_SYSMSG)
    		.setIndicator(TAB_SYSMSG)
    		.setContent(new Intent(this,SystemMsg.class)));
		tabHost.addTab(tabHost.newTabSpec(TAB_MAP)
	    		.setIndicator(TAB_MAP)
	    		.setContent(new Intent(this,MapContainer.class)));
		tabHost.addTab(tabHost.newTabSpec(TAB_CARD)
	    		.setIndicator(TAB_CARD)
	    		.setContent(new Intent(this,MyCard.class)));
		Intent intent = getIntent();
		int tag = 0;
		if(intent!=null){
			tag = intent.getIntExtra("TAG", 0);
		}
		if(DEBUG) Log.d(TAG,"tag:"+tag);
		tabHost.setCurrentTab(tag);
		navigator.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.radio_friendlist:
					tabHost.setCurrentTabByTag(TAB_FRIENDLIST);
					break;
				case R.id.radio_currentsession:
					tabHost.setCurrentTabByTag(TAB_CURRENTSESSIONS);
					break;
				case R.id.radio_sysmsg:
					tabHost.setCurrentTabByTag(TAB_SYSMSG);
					break;
				case R.id.radio_map:
					tabHost.setCurrentTabByTag(TAB_MAP);
					break;
				case R.id.radio_card:
					tabHost.setCurrentTabByTag(TAB_CARD);
					break;
				default:
					break;
				}
			}
		});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add(0,SETTING, 0, R.string.setting).setIcon(R.drawable.menu_setting);
        menu.add(0,ABOUT,0,R.string.about).setIcon(R.drawable.menu_about);
        menu.add(0,EXIT,0,R.string.exit).setIcon(R.drawable.menu_exit);
		return result;
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
