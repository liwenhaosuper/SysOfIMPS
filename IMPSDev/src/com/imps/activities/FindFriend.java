package com.imps.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.imps.IMPSActivity;
import com.imps.IMPSDev;
import com.imps.R;
import com.imps.basetypes.Constant;
import com.imps.basetypes.FindFriendItem;
import com.imps.receivers.IMPSBroadcastReceiver;
import com.imps.services.impl.ServiceManager;

public class FindFriend extends IMPSActivity{
	private static String TAG = FindFriend.class.getCanonicalName();
	private static boolean DEBUG = IMPSDev.isDEBUG();
	private DefaultAdapter defaultAdapter;
	private ListView defaultList;
	private IMPSBroadcastReceiver receiver = new IMPSBroadcastReceiver();

	private List<FindFriendItem> mItems = new ArrayList<FindFriendItem>();

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.findfriend);
		registerReceiver(receiver,receiver.getFilter());
		//mList = (ListView)findViewById(R.id.find_list);
		defaultList = (ListView)findViewById(android.R.id.list);
		initAdapter();
		defaultList.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(FindFriend.this,mItems.get(position).cls);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				startActivity(intent);
				
			}});
	}
	public void onStop(){
		super.onStop();
		unregisterReceiver(receiver);
	}
	public void onResume(){
		super.onResume();
		registerReceiver(receiver,receiver.getFilter());
	}
	public void initAdapter(){
		defaultAdapter = new DefaultAdapter(this,mItems);
		defaultList.setAdapter(defaultAdapter);
		initData();
	}
	public void initData(){
		FindFriendItem item = new FindFriendItem();
		//nearby friend
		item.image = R.drawable.icon_lbs;
		item.text = getResources().getString(R.string.find_nearby_friend);
		item.cls = SearchFriend.class;
		mItems.add(item);
		//introduce friend
		FindFriendItem item2 = new FindFriendItem();
		item2.image = R.drawable.icon_recommend_friend;
		item2.text = getResources().getString(R.string.recommend_friend);
		item2.cls = SearchFriend.class;
		mItems.add(item2);
		//search friend
		FindFriendItem item3 = new FindFriendItem();
		item3.image = R.drawable.icon_search;
		item3.text = getResources().getString(R.string.search_friend);
		item3.cls = SearchFriend.class;
		mItems.add(item3);
		defaultAdapter.notifyDataSetChanged();
	}

 
	@Override
	public boolean onMenuItemSelected(int id,MenuItem item)
	{
		switch(item.getItemId())
		{
		case IMPSContainer.SETTING:
			ComponentName cn=new ComponentName(FindFriend.this,FindFriend.class);
			
			Intent intent=new Intent();
			intent.setComponent(cn);
			startActivity(intent);
			break;
		case IMPSContainer.ABOUT:
		{
			Intent i = new Intent(FindFriend.this,About.class);
			startActivity(i);
			return true;
		}
		case IMPSContainer.EXIT:
		{
			new AlertDialog.Builder(this.getParent())
			.setMessage(getResources().getString(R.string.exit_warning))
			.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener(){
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
	private class DefaultAdapter extends BaseAdapter{

		private Context context;
		private List<FindFriendItem> items;
		public DefaultAdapter(Context context,List<FindFriendItem> items){
			this.context = context;
			this.items = items;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return items.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return items.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			FindFriendItem item = items.get(position);
			ViewHolder holder = null;
			if(convertView==null){
				holder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.findfrienditem,null);			
				holder.image = (ImageView)convertView.findViewById(R.id.find_icon);
				holder.btn = (Button)convertView.findViewById(R.id.btn_share);
				holder.text = (TextView)convertView.findViewById(R.id.find_content);
				holder.image.setImageResource(item.image);
				holder.text.setText(item.text);
				convertView.setTag(holder);
			}
			return convertView;
		}
		
		class ViewHolder{
			ImageView image;
			TextView text;
			Button btn;
			 
		}
		
	}
}
