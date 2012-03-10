package com.imps.activities;

import java.util.ArrayList;

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
import com.imps.IMPSMain;
import com.imps.R;
import com.imps.basetypes.MediaType;
import com.imps.net.handler.UserManager;
import com.imps.receivers.IMPSBroadcastReceiver;
import com.imps.services.impl.ServiceManager;
import com.imps.util.LocalDBHelper;

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
	public static final String TAB_SNS = "sns";
	
	public static final int SETTING = Menu.FIRST + 1;
	public static final int MYCARD = Menu.FIRST+2;
	public static final int ABOUT = Menu.FIRST+3;
	public static final int EXIT = Menu.FIRST+4;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(DEBUG) Log.d(TAG,"Container create");
		ServiceManager.getmScreen().addScreen(this.getClass());
		if(!ServiceManager.isStarted||ServiceManager.getmAccount()==null||!ServiceManager.getmAccount().isLogined()){	
			startActivity(new Intent(IMPSContainer.this,IMPSMain.class));
			finish();
			return;
		}
		/* get recent contact from local database */
		UserManager.buildLocalDB(IMPSDev.getContext());
		LocalDBHelper localDB = UserManager.localDB;
		ArrayList<String> recentFriends = localDB.fetchRecentContacts();
		if (recentFriends == null) {
			Log.d(TAG, "fail to restore recent contact from local db");
		} else {
			Log.d(TAG, recentFriends.toString());
			for (String s : recentFriends) {
				UserManager.CurSessionFriList.put(s, new ArrayList<MediaType>());
			}
		}

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
		tabHost.addTab(tabHost.newTabSpec(TAB_SNS)
	    		.setIndicator(TAB_SNS)
	    		.setContent(new Intent(this,SnsMain.class)));
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
				case R.id.radio_sns:
					tabHost.setCurrentTabByTag(TAB_SNS);
					break;
				default:
					break;
				}
			}
		});
		//start heart beat
		ServiceManager.getmHeartbeat().start();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add(0,SETTING, 0, R.string.setting).setIcon(R.drawable.menu_setting);
		menu.add(0,MYCARD, 0, R.string.card).setIcon(R.drawable.menu_setting);
        menu.add(0,ABOUT,0,R.string.about).setIcon(R.drawable.menu_about);
        menu.add(0,EXIT,0,R.string.exit).setIcon(R.drawable.menu_exit);
		return result;
	}
	@Override 
	public void onResume(){
		super.onResume();
		if(DEBUG) Log.d(TAG,"Container resume");
		registerReceiver(receiver,receiver.getFilter());
	}
	@Override
	public void onStop(){
		super.onStop();
		if(DEBUG) Log.d(TAG,"Container stop");
		unregisterReceiver(receiver);
		ServiceManager.getmScreen().removeScreen(this.getClass());
	}
	@Override
	public void onPause(){
		super.onPause();
		if(DEBUG) Log.d(TAG,"Container pause");
	}
}
