package com.imps.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.imps.R;
import com.imps.base.User;
import com.imps.base.userStatus;
import com.imps.handler.UserManager;

public class CurrentSessions extends Activity {
	private ListView concurSessionsList;
	private ArrayList<HashMap<String, Object>> sessionsList;
	private static final int SYS_MSG = Menu.FIRST;
	
	/**
	 * three image buttons
	 */
	private static ImageButton curSessionsTab, friendListTab, tab3;
	
	@Override
	public void onCreate(Bundle savedInstanceState	)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.currentsessions);
		sessionsList = new ArrayList<HashMap<String,Object>>();
		concurSessionsList = (ListView)findViewById(R.id.concurSessions);
		getList();
		SimpleAdapter listItemAdapter = new SimpleAdapter(this, sessionsList, R.layout.friend, 
				new String[]{"avatar", "name"}, new int[]{R.id.avatar, R.id.name});
		concurSessionsList.setAdapter(listItemAdapter);
		concurSessionsList.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				ComponentName cn=new ComponentName(CurrentSessions.this,ChatView.class);
				Intent intent=new Intent();
				intent.setComponent(cn);
				String fUsername = (String)sessionsList.get(arg2).get("name");
				intent.putExtra("fUsername", fUsername);
				startActivity(intent);
			}
			
		});
		processTabClick();
	}
	@Override
	public void onResume()
	{
		super.onResume();
		IntentFilter ifilter = new IntentFilter();
		ifilter.addAction("exit");
		ifilter.addAction("status_notify");
		registerReceiver(exitReceiver,ifilter);
		
	}
	@Override
	public void onStop()
	{
		super.onStop();
		unregisterReceiver(exitReceiver);
	}
	
	private void getList()
	{
		//add items to the list
		Iterator<String> iter = UserManager.CurSessionFriList.keySet().iterator();
		int len = UserManager.AllFriList.size();
		while(iter.hasNext()){
			HashMap<String,Object> map = new HashMap<String,Object>();
			String nm =(String)iter.next();
			for(int i=0;i<len;i++)
			{			
				if(UserManager.AllFriList.get(i).getUsername().equals(nm))
				{
					User usr = UserManager.AllFriList.get(i);
					map.put("avatar",usr.getStatus()==userStatus.ONLINE?R.drawable.list_online:R.drawable.list_offline);
					map.put("name", nm);
					break;
				}
			}
			if(map!=null)
			   sessionsList.add(map);
		}
	}
	
	/**
	 * option tab event handle
	 */
	private void processTabClick() {
		// TODO Auto-generated method stub
		
		curSessionsTab = (ImageButton)findViewById(R.id.current_talk_menu);
		friendListTab = (ImageButton)findViewById(R.id.friend_list_menu);
		tab3 = (ImageButton)findViewById(R.id.main_menu);
		
		curSessionsTab.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
/*				System.out.println("1 clicked");
	       		ComponentName cn=new ComponentName(CurrentSessions.this,"com.imps.pack.CurrentSessions");				
				Intent intent=new Intent();
				intent.setComponent(cn);
				startActivity(intent);*/
				// TODO Auto-generated method stub
			}
		});
		friendListTab.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
	       		ComponentName cn=new ComponentName(CurrentSessions.this,FriendTab.class);			
				Intent intent=new Intent();
				intent.setComponent(cn);
				startActivity(intent);
				finish();
			}
			
		});
		tab3.setOnClickListener(new View.OnClickListener(){
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
			else if("status_notify".equals(intent.getAction()))
			{
				String fUsername = intent.getStringExtra("fUsername");
				if(fUsername==null)
					return;
				if(UserManager.CurSessionFriList.containsKey(fUsername))
				{
					updateList();
				}
				
			}
		}		
	}
	private ExitReceiver exitReceiver = new ExitReceiver();
	public void updateList()
	{
		sessionsList = new ArrayList<HashMap<String, Object>>();
		getList();
		SimpleAdapter listItemAdapter = (SimpleAdapter)concurSessionsList.getAdapter();
		listItemAdapter.notifyDataSetChanged();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add(0, SYS_MSG, 0, R.string.sysMsg);
		return result;
	}
	@Override
	public boolean onMenuItemSelected(int id,MenuItem item)
	{
		switch(item.getItemId())
		{
		case SYS_MSG:
			ComponentName cn=new ComponentName(CurrentSessions.this,SystemMsg.class);
			
			Intent intent=new Intent();
			intent.setComponent(cn);
			startActivity(intent);
			break;
		}
		return super.onMenuItemSelected(id,item);
		
	}
}
