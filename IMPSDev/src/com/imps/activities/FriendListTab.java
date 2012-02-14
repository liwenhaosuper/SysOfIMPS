package com.imps.activities;

import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.imps.R;
import com.imps.basetypes.Constant;
import com.imps.basetypes.User;
import com.imps.basetypes.UserStatus;
import com.imps.events.IContactEvent;
import com.imps.net.handler.UserManager;
import com.imps.receivers.IMPSBroadcastReceiver;
import com.imps.services.impl.ServiceManager;

public class FriendListTab extends ExpandableListActivity implements IContactEvent{

	private static final int SYS_MSG = Menu.FIRST;
	private static final int ADD_NEW_FRI = Menu.FIRST + 1;
	private static final int EXIT_APP = Menu.FIRST+2;
	private static final int VIEW_INTO = Menu.FIRST+3;
	private static final int FRESH = Menu.FIRST+4;
	private FriendListAdapter mAdapter;
	private ExpandableListView friendList;
	private View popViewItem;
	private PopupWindow listPopupWindow;
	private Button btnDraw;
	private Button btnEmail;
	private Button btnCall;
	private String mUsername = UserManager.getGlobaluser().getUsername();
	private IMPSBroadcastReceiver bReceiver = new IMPSBroadcastReceiver();
	private Handler receiver = new Handler(){
		@Override
		public void handleMessage(Message msg){
			switch(msg.what){
			case FRESH:
				updateFriendList();
				break;
			}
		}
	};
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.friendtab);		
		registerForContextMenu(getExpandableListView());
		registerReceiver(bReceiver,bReceiver.getFilter());
		friendList = getExpandableListView();
		initAdapter();
		initPopupWindow();
		friendList.setCacheColorHint(0);
		friendList.setDivider(null);
		friendList.setGroupIndicator(getResources().getDrawable(R.drawable.expander_ic_folder));
		friendList.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(listPopupWindow!=null&&listPopupWindow.isShowing())
				{
					listPopupWindow.dismiss();
				}
				return false;
			}
			
		});
		friendList.setOnChildClickListener(new OnChildClickListener(){

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				if(v==findViewById(R.id.myCursor))
				{
					return false;
				}
				ComponentName cn = new ComponentName(FriendListTab.this,ChatView.class);
				Intent intent = new Intent();
				intent.setComponent(cn);
				String fUsername =((User)(mAdapter.getChild(groupPosition, childPosition))).getUsername();//=myCursorTreeAdapter.getChild(groupPosition, childPosition).getString(name_index);
				intent.putExtra("fUsername",fUsername);
				intent.putExtra("mUsername", mUsername);
				startActivity(intent);
				return false;
			}
		});
		ServiceManager.getmNetLogic().addContactEventHandler(this);
	}
	private void initAdapter(){
		mAdapter = new FriendListAdapter();
		setListAdapter(mAdapter);
	}
	private void initPopupWindow(){
		popViewItem = this.getLayoutInflater().inflate(R.layout.friendtab_popup, null);
		listPopupWindow = new PopupWindow(popViewItem, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	    listPopupWindow.setTouchable(true);
	    listPopupWindow.setFocusable(false);
		btnDraw=(Button)popViewItem.findViewById(R.id.btnDraw);
		btnEmail=(Button)popViewItem.findViewById(R.id.btnEmail);
		btnCall=(Button)popViewItem.findViewById(R.id.btnCall);
	}

	private void updateFriendList(){
		mAdapter.refresh();
		//ServiceManager.getmContact().sendFriListReq();
	}
	@Override
	public void onResume()
	{
		super.onResume();
		registerReceiver(bReceiver,bReceiver.getFilter());
		updateFriendList();
	}
	@Override
	public void onStop()
	{
		super.onStop();
		unregisterReceiver(bReceiver);
	}
	@Override
	public boolean onMenuItemSelected(int id,MenuItem item)
	{
		switch(item.getItemId())
		{
		case IMPSContainer.SETTING:
			ComponentName cn=new ComponentName(FriendListTab.this,FindFriend.class);
			
			Intent intent=new Intent();
			intent.setComponent(cn);
			startActivity(intent);
			break;
		case IMPSContainer.ABOUT:
		{
			Intent i = new Intent(FriendListTab.this,About.class);
			startActivity(i);
			return true;
		}
		case IMPSContainer.EXIT:
		{
			new AlertDialog.Builder(this.getParent())
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
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if(listPopupWindow!=null&&listPopupWindow.isShowing())
		{
			listPopupWindow.dismiss();
		}
		return super.onTouchEvent(event);
	}


	
	private class FriendListAdapter extends BaseExpandableListAdapter
	{
		private String[] mGroups = {getResources().getString(R.string.unsort_group),
				getResources().getString(R.string.friend_group),
				getResources().getString(R.string.family_group),
				getResources().getString(R.string.colleague_group),
				getResources().getString(R.string.classmate_group)};
		private HashMap<String,List<User>> mData = new HashMap<String,List<User>>();
		public FriendListAdapter(){
			mData.put(mGroups[0], UserManager.AllFriList);
			mData.put(mGroups[1],null);
			mData.put(mGroups[2],null);
			mData.put(mGroups[3],null);
			mData.put(mGroups[4],null);
		}
		@Override
		public int getGroupCount() {
			// TODO Auto-generated method stub
			return mGroups.length;
		}
		public void refresh(){
			mData.clear();
			mData.put(mGroups[0], UserManager.AllFriList);
			mData.put(mGroups[1],null);
			mData.put(mGroups[2],null);
			mData.put(mGroups[3],null);
			mData.put(mGroups[4],null);
			this.notifyDataSetChanged();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			// TODO Auto-generated method stub
			String key = mGroups[groupPosition];
			return mData.get(key)==null?0:mData.get(key).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			// TODO Auto-generated method stub
			String key = mGroups[groupPosition];
			return mData.get(key);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			String key = mGroups[groupPosition];
			return mData.get(key)==null?null:mData.get(key).get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			// TODO Auto-generated method stub
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
	        View v;
	        if (convertView == null) {
	            v = newGroupView(groupPosition,isExpanded, parent);
	        } else {
	            v = convertView;
	        }
	        bindGroupView(v, groupPosition, isExpanded);
	        return v;
		}
		public void bindGroupView(View view,int groupPosition,
				boolean isExpanded)
		{
			TextView groupName=(TextView)view.findViewById(R.id.groupName);
			String group=mGroups[groupPosition];
			groupName.setText(group);		
			TextView groupCount=(TextView)view.findViewById(R.id.groupCount);
			String key = mGroups[groupPosition];
			List<User> users = mData.get(key);
			User[] allUsers = UserManager.getInstance().getOnlineFriendlist();
			int onlineCount = 0;
			for(int i=0;users!=null&&i<users.size();i++){
				for(int j=0;j<allUsers.length;j++){
					if(allUsers[j].getUsername().equals(users.get(i).getUsername())){
						onlineCount++;
						break;
					}
				}
			}
			int count=(users==null?0:users.size());
			groupCount.setText("["+onlineCount+"/"+count+"]");
		}
		public View newGroupView(int groupPosition,
				boolean isExpanded, ViewGroup parent) {
			LayoutInflater inflate=LayoutInflater.from(FriendListTab.this);
			View view=inflate.inflate(R.layout.grouplayout, null);
			bindGroupView(view, groupPosition, isExpanded);
			return view;
		}
		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
	        View v;
	        if (convertView == null) {
	            v = newChildView(groupPosition, childPosition, isLastChild, parent);
	        } else {
	            v = convertView;
	        }
	        bindChildView(v, groupPosition,childPosition, isLastChild);
	        return v;
		}
	    public void bindChildView(View view, int groupPosition,int childPosition,
	            boolean isLastChild){
			ImageView contactIcon=(ImageView)view.findViewById(R.id.contactIcon);
			//contactIcon.setImageBitmap(getBitmapFromByte(cursor.getBlob(icon_index)));
			contactIcon.setBackgroundDrawable(getResources().getDrawable(R.drawable.user));
			TextView name=(TextView)view.findViewById(R.id.name);
			String key = mGroups[groupPosition];
			name.setText(mData.get(key).get(childPosition).getUsername());
			
			TextView description=(TextView)view.findViewById(R.id.description);
			description.setTextKeepState(mData.get(key).get(childPosition).getDescription());
			
			TextView statusView = (TextView)view.findViewById(R.id.status);
			statusView.setText(mData.get(key).get(childPosition).getStatus()==UserStatus.ONLINE?getResources().getString(R.string.online):getResources().getString(R.string.offline));
			//final String email=cursor.getString(email_index);
			
			ImageView  mycursor=(ImageView)view.findViewById(R.id.myCursor);
			mycursor.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(listPopupWindow.isShowing())
					{
						listPopupWindow.dismiss();
					}
					else
					{ 
						listPopupWindow.showAsDropDown(v); 
						
						btnDraw.setOnClickListener(new View.OnClickListener() {
							
							@Override
							public void onClick(View v) {
								listPopupWindow.dismiss();
								
								//ComponentName cn=new ComponentName(FriendListTab.this,Graffiti.class);
								//Intent intent=new Intent();
								//intent.setComponent(cn);
								//startActivity(intent);

							}
						});
						
						btnEmail.setOnClickListener(new View.OnClickListener() {
							
							@Override
							public void onClick(View v) {
								listPopupWindow.dismiss();
							}
						});
						
						btnCall.setOnClickListener(new View.OnClickListener() {
							
							@Override
							public void onClick(View v) {
								listPopupWindow.dismiss();
								//Uri uri = Uri.parse("tel:"+phoneNumber);
								//Intent it = new Intent(Intent.ACTION_DIAL, uri);  
								//startActivity(it);
							}
						});
					}
				}
			});
	    }
	    public View newChildView(int groupPosition,int childPosition, boolean isLastChild,
	            ViewGroup parent)
	    {
			LayoutInflater inflate=LayoutInflater.from(FriendListTab.this);
			View view=inflate.inflate(R.layout.childlayout, null);
			
			bindChildView(view, groupPosition, childPosition, isLastChild);
			
			return view;
	    }
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return true;
		}
		

		
	}

	@Override
	public void sendFriListReq() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void sendUpdateMyStatusReq(byte status) {
		// TODO Auto-generated method stub
	}
	@Override
	public void onUpdateFriStatus() {
		// TODO Auto-generated method stub
		Message msg = new Message();
		msg.what = FRESH;
		receiver.sendMessage(msg);
	}
	@Override
	public void onUpdateFriList() {
		// TODO Auto-generated method stub
		Message msg = new Message();
		msg.what = FRESH;
		receiver.sendMessage(msg);
	}
	@Override
	public void onRecvFriendReq(String friName) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onRecvFriendRsp(String friName, boolean rel) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void sendSearchFriendReq(String keyword) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onRecvSearchFriendRsq(List<String> friends) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void sendAddFriendReq(String friname) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onRecvAddFriendReq(String friname) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onRecvAddFriendRsp(String friname, boolean res) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void sendAddFriendRsp(String friname, boolean res) {
		// TODO Auto-generated method stub
		
	}
}
