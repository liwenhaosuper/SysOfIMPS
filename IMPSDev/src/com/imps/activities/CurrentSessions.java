package com.imps.activities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.imps.IMPSActivity;
import com.imps.R;
import com.imps.basetypes.Constant;
import com.imps.basetypes.MediaType;
import com.imps.basetypes.SystemMsgType;
import com.imps.basetypes.User;
import com.imps.basetypes.UserStatus;
import com.imps.net.handler.UserManager;
import com.imps.receivers.IMPSBroadcastReceiver;
import com.imps.services.impl.ServiceManager;
import com.imps.ui.widget.FaceTextView;
import com.imps.ui.widget.ScrollTabHostActivity;
import com.imps.ui.widget.ScrollTabHostActivity.TabOnGestureListener;

public class CurrentSessions extends IMPSActivity{
	protected static final String TAG = CurrentSessions.class.getCanonicalName();
	private ListView concurSessionsList;
	private List<User> sessionsList;
	private CurrentSessionAdapter mAdapter;
	private CurrentSessionsReceiver receiver = new CurrentSessionsReceiver();
	private SystemMsgType sysmsg ;
	@Override
	public void onCreate(Bundle savedInstanceState	)
	{
		Log.d(TAG, "onCreate() started");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.currentsessions);
		setTitle(getResources().getString(R.string.currentsession));
		sessionsList = new ArrayList<User>();
		concurSessionsList = (ListView)findViewById(R.id.concurSessions);
		initAdapter();
	}
	@Override
	public void onStart(){
		super.onStart();
		final GestureDetector detector = new GestureDetector(this, new ScrollTabHostActivity.TabOnGestureListener((ScrollTabHostActivity)this.getParent()));
        this.getWindow().getDecorView().setOnTouchListener(new View.OnTouchListener(){
          public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
          {		     
              return detector.onTouchEvent(paramMotionEvent);
          }
        });
	}
	@Override
	public void onResume(){
		super.onResume();
		
		Log.d(TAG, "onResume() started");
		if(sessionsList!=null){
		sessionsList.clear();
		Iterator<String> iter = UserManager.CurSessionFriList.keySet().iterator();
		int len = UserManager.AllFriList.size();
		while(iter.hasNext()){
			String nm =(String)iter.next();
			for(int i=0;i<len;i++)
			{			
				if(UserManager.AllFriList.get(i).getUsername().equals(nm))
				{
					User usr = UserManager.AllFriList.get(i);
					sessionsList.add(usr);
					break;
				}
			}
		}
		if(UserManager.mSysMsgs.size()!=0){
		User SysAdmin=new User();
		sysmsg = UserManager.mSysMsgs.get(UserManager.mSysMsgs.size()-1);
		SysAdmin.setUsername("SysAdmin");
		SysAdmin.setStatus(UserStatus.ONLINE);
		if(sysmsg.type==SystemMsgType.FROM){
			if(sysmsg.status==SystemMsgType.ACCEPTED){
				SysAdmin.setDescription(sysmsg.name+getResources().getString(R.string.accept_friend_sysmsg));
			}
			else if(sysmsg.status==SystemMsgType.DENIED){
				SysAdmin.setDescription(sysmsg.name+getResources().getString(R.string.deny_friend_sysmsg));
			}
			else{
				SysAdmin.setDescription(sysmsg.name+getResources().getString(R.string.receive_friend_sysmsg));
			}
		}
		Log.d(TAG, "4444");
		sessionsList.add(SysAdmin);
		}
		}
		registerReceiver(receiver,receiver.getFilter());
		

		mAdapter.notifyDataSetChanged();
	}
	@Override
	public void onStop(){
		super.onStop();
		unregisterReceiver(receiver);
	}
	public void initAdapter(){
		mAdapter = new CurrentSessionAdapter(sessionsList);
		concurSessionsList.setAdapter(mAdapter);
		concurSessionsList.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ComponentName cn=new ComponentName(CurrentSessions.this,ChatView.class);
				Intent intent=new Intent();
				intent.setComponent(cn);
				String fUsername = sessionsList.get(position).getUsername();
				intent.putExtra("fUsername", fUsername);
				startActivity(intent);
			}
		});
		initData();
	}
	public void initData(){
		//add items to the list
		sessionsList.clear();
		Iterator<String> iter = UserManager.CurSessionFriList.keySet().iterator();
		int len = UserManager.AllFriList.size();
		while(iter.hasNext()){
			String nm =(String)iter.next();
			for(int i=0;i<len;i++)
			{			
				if(UserManager.AllFriList.get(i).getUsername().equals(nm))
				{
					User usr = UserManager.AllFriList.get(i);
					sessionsList.add(usr);
					break;
				}
			}
		}
		if(UserManager.mSysMsgs.size()!=0){
			User SysAdmin=new User();
			sysmsg = UserManager.mSysMsgs.get(UserManager.mSysMsgs.size()-1);
			SysAdmin.setUsername("SysAdmin");
			SysAdmin.setStatus(UserStatus.ONLINE);
			if(sysmsg.type==SystemMsgType.FROM){
				if(sysmsg.status==SystemMsgType.ACCEPTED){
					SysAdmin.setDescription(sysmsg.name+getResources().getString(R.string.accept_friend_sysmsg));
				}
				else if(sysmsg.status==SystemMsgType.DENIED){
					SysAdmin.setDescription(sysmsg.name+getResources().getString(R.string.deny_friend_sysmsg));
				}
				else{
					SysAdmin.setDescription(sysmsg.name+getResources().getString(R.string.receive_friend_sysmsg));
				}
			}
			sessionsList.add(SysAdmin);
		}
		mAdapter.notifyDataSetChanged();
	}
	@Override
	public boolean onMenuItemSelected(int id,MenuItem item)
	{
		switch(item.getItemId())
		{
		case IMPSContainer.SETTING:
			ComponentName cn=new ComponentName(CurrentSessions.this,FindFriend.class);
			Intent intent=new Intent();
			intent.setComponent(cn);
			startActivity(intent);
			break;
		case IMPSContainer.ABOUT:
		{
			Intent i = new Intent(CurrentSessions.this,About.class);
			startActivity(i);
			return true;
		}
		case IMPSContainer.EXIT:
		{
			new AlertDialog.Builder(this)
			.setMessage(getResources().getString(R.string.exit_warning))
			.setPositiveButton(getResources().getString(R.string.ok), new OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					ServiceManager.getmAccount().logout();
					ServiceManager.stop();
					Intent intent = new Intent(Constant.EXIT);
					sendBroadcast(intent);
					finish();
				}		
			})
			.setNegativeButton(getResources().getString(R.string.cancel), null)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.show();
			return true;
		}
		}
		return super.onMenuItemSelected(id,item);
		
	}
	private class CurrentSessionAdapter extends BaseAdapter{
		private List<User> sessionsList;
		public CurrentSessionAdapter(List<User> list){
			sessionsList = list;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return sessionsList.size();
		}
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return sessionsList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.d(TAG,"getView:"+sessionsList.get(position).getUsername());

				if(sessionsList==null||sessionsList.get(position)==null){
					return null;
				}
				LayoutInflater inflate=LayoutInflater.from(CurrentSessions.this);
				convertView = inflate.inflate(R.layout.currsession_item, null);
				//convertView.setBackgroundResource(R.drawable.list_bg);     //添加listitem的焦点事件
				ImageView contactIcon=(ImageView)convertView.findViewById(R.id.contactIcon);
				TextView name=(TextView)convertView.findViewById(R.id.name);
				TextView date=(TextView)convertView.findViewById(R.id.date);
				if(name!=null)name.setText(sessionsList.get(position).getUsername());
				FaceTextView description=(FaceTextView)convertView.findViewById(R.id.description);
				TextView statusView = (TextView)convertView.findViewById(R.id.status);
				if(sessionsList.get(position).getUsername().equals("SysAdmin")){
					if(description!=null)description.setText(sessionsList.get(position).getDescription());					
					date.setText(sysmsg.time.substring(5));
					statusView.setText(getResources().getString(R.string.online));
					return convertView;
				}
				
				
				if(UserManager.CurSessionFriList.containsKey(sessionsList.get(position).getUsername())){
					List<MediaType> items = UserManager.CurSessionFriList.get(sessionsList.get(position).getUsername());
					for(int i=items.size()-1;i>=0;i--){
						if(items.get(i).getType()==MediaType.SMS){	
							if(description!=null)description.setText(items.get(i).getMsgContant());							
							date.setText(items.get(i).getTime().substring(5));
							break;
						}
					}
				
				}

				if(statusView!=null)statusView.setText(sessionsList.get(position).getStatus()==UserStatus.ONLINE?getResources().getString(R.string.online):getResources().getString(R.string.offline));
			
			return convertView;
		}
		
	}
	
	public class CurrentSessionsReceiver extends IMPSBroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			super.onReceive(context, intent);
			if(intent.getAction().equals(Constant.SMS)){
				//TODO:
				int len = UserManager.AllFriList.size();
				String nm = intent.getStringExtra(Constant.USERNAME);
				for(int i=0;i<len;i++)
				{			
					if(UserManager.AllFriList.get(i).getUsername().equals(nm))
					{
						User usr = UserManager.AllFriList.get(i);
						sessionsList.add(usr);
						mAdapter.notifyDataSetChanged();
						break;
					}
				}
			}
		}
		@Override
		public IntentFilter getFilter(){
			// TODO Auto-generated method stub
			IntentFilter filter = super.getFilter();
			filter.addAction(Constant.SMS);
			return filter;
		}
		
	}
}
