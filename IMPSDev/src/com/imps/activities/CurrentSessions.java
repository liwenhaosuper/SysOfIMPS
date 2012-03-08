package com.imps.activities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.imps.R;
import com.imps.basetypes.Constant;
import com.imps.basetypes.MediaType;
import com.imps.basetypes.SystemMsgType;
import com.imps.basetypes.User;
import com.imps.basetypes.UserStatus;
import com.imps.net.handler.UserManager;
import com.imps.receivers.IMPSBroadcastReceiver;
import com.imps.services.impl.ServiceManager;

public class CurrentSessions extends Activity{
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
		private int changeStrToId(String str){
			if ("exp_01".equals(str)){
				return R.drawable.exp_01;
			}
			else if ("exp_02".equals(str)){
				return R.drawable.exp_02;
			}
			else if ("exp_03".equals(str)){
				return R.drawable.exp_03;
			}
			else if ("exp_04".equals(str)){
				return R.drawable.exp_04;
			}
			else if ("exp_05".equals(str)){
				return R.drawable.exp_05;
			}
			else if ("exp_06".equals(str)){
				return R.drawable.exp_06;
			}
			else if ("exp_07".equals(str)){
				return R.drawable.exp_07;
			}
			else if ("exp_08".equals(str)){
				return R.drawable.exp_08;
			}
			else if ("exp_09".equals(str)){
				return R.drawable.exp_09;
			}
			else if ("exp_10".equals(str)){
				return R.drawable.exp_10;
			}
			else if ("exp_11".equals(str)){
				return R.drawable.exp_11;
			}
			else if ("exp_12".equals(str)){
				return R.drawable.exp_12;
			}
			else if ("exp_13".equals(str)){
				return R.drawable.exp_13;
			}
			else if ("exp_14".equals(str)){
				return R.drawable.exp_14;
			}
			else if ("exp_15".equals(str)){
				return R.drawable.exp_15;
			}
			else if ("exp_16".equals(str)){
				return R.drawable.exp_16;
			}
			else if ("exp_17".equals(str)){
				return R.drawable.exp_17;
			}
			else if ("exp_18".equals(str)){
				return R.drawable.exp_18;
			}
			else if ("exp_19".equals(str)){
				return R.drawable.exp_19;
			}
			else if ("exp_20".equals(str)){
				return R.drawable.exp_20;
			}
			else if ("exp_21".equals(str)){
				return R.drawable.exp_21;
			}
			else if ("exp_22".equals(str)){
				return R.drawable.exp_22;
			}
			else if ("exp_23".equals(str)){
				return R.drawable.exp_23;
			}
			else if ("exp_24".equals(str)){
				return R.drawable.exp_24;
			}
			else if ("exp_25".equals(str)){
				return R.drawable.exp_25;
			}
			else if ("exp_26".equals(str)){
				return R.drawable.exp_26;
			}
			return -1;
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
				TextView description=(TextView)convertView.findViewById(R.id.description);
				if(description!=null)description.setText(sessionsList.get(position).getDescription());
				TextView statusView = (TextView)convertView.findViewById(R.id.status);
				if(sessionsList.get(position).getUsername().equals("SysAdmin")){
					String t=(sessionsList.get(position).getDescription());
					int pos = 0;
					while ((pos = t.indexOf("[exp_")) != -1){
						description.append(t.substring(0, pos));
						t = t.substring(pos);
						if (t.length() < 8){
							description.append(t);
							break;
						}
						String flag = t.substring(0, 8);
						t = t.substring(8);
						if (!flag.endsWith("]")){
							description.append(flag);
							continue;
						}
						int id = changeStrToId(flag.substring(1, 7));
						if (id != -1){
							Drawable drawable = getResources().getDrawable(id);   
					        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());   
					        SpannableString spannable = new SpannableString(flag);   
					        ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);   
					        spannable.setSpan(span, 0, flag.length(), 
					        		Spannable.SPAN_INCLUSIVE_EXCLUSIVE);     
					        description.append(spannable);	
						}
						else {
							description.append(flag);
						}
					}
					description.append(t);
					date.setText(sysmsg.time.substring(5));
					statusView.setText(getResources().getString(R.string.online));
					return convertView;
				}
				
				
				if(UserManager.CurSessionFriList.containsKey(sessionsList.get(position).getUsername())){
					List<MediaType> items = UserManager.CurSessionFriList.get(sessionsList.get(position).getUsername());
					for(int i=items.size()-1;i>=0;i--){
						if(items.get(i).getType()==MediaType.SMS){
							String t=(items.get(i).getMsgContant());

							int pos = 0;
							while ((pos = t.indexOf("[exp_")) != -1){
								description.append(t.substring(0, pos));
								t = t.substring(pos);
								if (t.length() < 8){
									description.append(t);
									break;
								}
								String flag = t.substring(0, 8);
								t = t.substring(8);
								if (!flag.endsWith("]")){
									description.append(flag);
									continue;
								}
								int id = changeStrToId(flag.substring(1, 7));
								if (id != -1){
									Drawable drawable = getResources().getDrawable(id);   
							        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());   
							        SpannableString spannable = new SpannableString(flag);   
							        ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);   
							        spannable.setSpan(span, 0, flag.length(), 
							        		Spannable.SPAN_INCLUSIVE_EXCLUSIVE);     
							        description.append(spannable);	
								}
								else {
									description.append(flag);
								}
							}
							description.append(t);
							
							
							
							
							
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
