package com.imps.activities;

//好友列表界面

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ExpandableListActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CursorTreeAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.imps.R;
import com.imps.util.ContactsManagerDbAdater;

public class FriendTab extends ExpandableListActivity {
	private ExpandableListView friendList;
	//private static ArrayList<HashMap<String, Object>> listItem;
	private String mUsername = null;
	private String selectedFri = null;
	private String selectedMsg = null;
	private Bundle bundle;
	private static final int VIEW_INTO = Menu.FIRST+3;
	private static final int SYS_MSG = Menu.FIRST;
	private static final int ADD_NEW_FRI = Menu.FIRST + 1;
	private static final int EXIT_APP = Menu.FIRST+2;
	/**
	 * three image buttons
	 */
	private static ImageButton curSessionsTab, friendListTab, tab3;
	
	private View popViewItem;
	private PopupWindow listPopupWindow;
	private MyCursrTreeAdapter myCursorTreeAdapter;
	private int groupNameIndex;
	private ContactsManagerDbAdater contactsManagerDbAdapter;
	//组上groupName字段索引
	private static final int groupName_index=1;
	private Cursor groupCursor;
	//联系人各个字段索引
	private static final int icon_index=2;  //icon
	private static final int name_index=1;   //friname
	private static final int description_index=8;  //not used
	private static final int status_index=4;    //status
	
	private Button btnDraw;
	private Button btnEmail;
	private Button btnCall;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.friendtab);		
		registerForContextMenu(getExpandableListView());
		bundle = this.getIntent().getBundleExtra("bundle");
		if (bundle != null){
			mUsername = bundle.getString("mUsername");
		}
		else {
			bundle = new Bundle();
		}
        contactsManagerDbAdapter=new ContactsManagerDbAdater(this);
        contactsManagerDbAdapter.open();
		friendList = getExpandableListView();
        initMyAdapter();
		initPopupWindow();
        friendList.setCacheColorHint(0);//拖动时避免出现黑色
        friendList.setDivider(null);//去掉每项下面的黑线(分割线)
        //自定义下拉图标
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
        //listItem = new ArrayList<HashMap<String, Object>>();
		//getFriend();
		
		//SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItem, R.layout.friend, 
			//	new String[]{"avatar", "name"}, new int[]{R.id.avatar, R.id.name});
		//friendList.setAdapter(listItemAdapter);  
		//长按事件监听
		/*friendList.setOnItemLongClickListener(new OnItemLongClickListener(){
		    public boolean onItemLongClick(AdapterView<?> arg0,View arg1,int arg2,long arg3){
		    	AlertDialog.Builder dialog=new AlertDialog.Builder(FriendTab.this);
		    	String fUsername=(String)listItem.get(arg2).get("name");
		    	List<User> flist=UserManager.AllFriList;
		    	User friend=null;
		    	for(int i=0;i<flist.size();i++){
		           if(flist.get(i).getUsername()==fUsername){
		        	   friend=flist.get(i);
		        	   break;
		           }
		    	}
		    	if(friend!=null){
		    		selectedFri = friend.getUsername();
		    	    dialog.setTitle(friend.getUsername());
		    	    String gender=friend.getGender()==0?"男":"女";
		    	    String status=friend.getStatus()==userStatus.OFFLINE?"离线":"在线";
		    	    selectedMsg = "性别: "+gender+"\n邮箱:"+friend.getEmail()+"\n状态:"+status;
		    	    System.out.println(selectedFri);
		    	    dialog.setItems(R.array.friTab_choice, new DialogInterface.OnClickListener() {				
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						System.out.println("您点击了 "+getResources().getStringArray(R.array.friTab_choice)[which]);
					    switch(which)
					    {
					    case 0://个人资料
					    	showDialog(VIEW_INTO);
					    	break;
					    case 1://查看位置
					    	if(selectedFri==null)
					    		return ;				    	
					    	ComponentName cn=new ComponentName(FriendTab.this,My_Map.class);
							Intent ti=new Intent();
							ti.setComponent(cn);
							Bundle bundle = new Bundle();
							bundle.putString("fUsername", selectedFri);
							ti.putExtra("fUsername",selectedFri);
							startActivity(ti);
					    	finish();
					    	break;
					    case 2://音频通话
					    	break;
					    case 3://视频通话
					    	break;
					    default:
					    	break;
					    	
					    }
					}
				});
		    	dialog.show();
		    	}
		    	return true;
		    }
		});
		*/
		/*
		 * 			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				ComponentName cn=new ComponentName(FriendTab.this,ChatView.class);
				Intent intent=new Intent();
				intent.setComponent(cn);
				String fUsername = (String)listItem.get(arg2).get("name");
				intent.putExtra("fUsername", fUsername);
				intent.putExtra("mUsername", mUsername);
				startActivity(intent);
			}
		 */
		friendList.setOnChildClickListener(new OnChildClickListener(){

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				if(v==findViewById(R.id.myCursor))
				{
					return false;
				}
				ComponentName cn = new ComponentName(FriendTab.this,ChatView.class);
				Intent intent = new Intent();
				intent.setComponent(cn);
				String fUsername =myCursorTreeAdapter.getChild(groupPosition, childPosition).getString(name_index);
				intent.putExtra("fUsername",fUsername);
				intent.putExtra("mUsername", mUsername);
				startActivity(intent);
				finish();
				return false;
			}
		});
		processTabClick();
	}
	
	private void initPopupWindow()
	{
		popViewItem = this.getLayoutInflater().inflate(R.layout.friendtab_popup, null);
		listPopupWindow = new PopupWindow(popViewItem, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	    listPopupWindow.setTouchable(true);
	    listPopupWindow.setFocusable(false);
		btnDraw=(Button)popViewItem.findViewById(R.id.btnDraw);
		btnEmail=(Button)popViewItem.findViewById(R.id.btnEmail);
		btnCall=(Button)popViewItem.findViewById(R.id.btnCall);
	}
    //给适配器赋值，刷新界面的时候也会用到
    public void initMyAdapter(){
    	groupCursor=contactsManagerDbAdapter.getAllGroups();
        startManagingCursor(groupCursor);
        //get the groupName column index
        groupNameIndex=groupCursor.getColumnIndexOrThrow("groupName");
        
        //set my adapter
        myCursorTreeAdapter=new MyCursrTreeAdapter(
        		groupCursor,
        		this,
        		true
        		);
        setListAdapter(myCursorTreeAdapter);
    }

/*	private void getFriend() {
		// TODO Auto-generated method stub
		List<User> friends = UserManager.AllFriList;
		if (friends != null){
			for (int i = 0; i < friends.size(); i++){
				HashMap<String, Object> map = new HashMap<String, Object>();
				if(friends.get(i).getStatus()==userStatus.ONLINE)
				{
					map.put("avatar",R.drawable.list_online);
				}
				else 
				{
					map.put("avatar", R.drawable.list_offline);
				}				
				map.put("name", friends.get(i).getUsername());
				listItem.add(map);
			}
		}
	}
	*/
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
				System.out.println("1 clicked");
	       		ComponentName cn=new ComponentName(FriendTab.this,CurrentSessions.class);
				Intent intent=new Intent();
				intent.setComponent(cn);
				startActivity(intent); 
				finish();
				// TODO Auto-generated method stub
			}
		});
		friendListTab.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				updateList();
			}
		});
		tab3.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("3 clicked");
				finish();
			}
			
		});
	}
	
	public void updateList()
	{
		//listItem = new ArrayList<HashMap<String, Object>>();
		//getFriend();
		//SimpleAdapter listItemAdapter = (SimpleAdapter)friendList.getAdapter();
		//listItemAdapter.notifyDataSetChanged();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add(0, SYS_MSG, 0, R.string.sysMsg);
        menu.add(0,ADD_NEW_FRI,0,R.string.add_new_fri);
//        menu.add(0,EXIT_APP,0,R.string.exit);
		return result;
	}
	@Override
	public boolean onMenuItemSelected(int id,MenuItem item)
	{
		switch(item.getItemId())
		{
		case SYS_MSG:
			ComponentName cn=new ComponentName(FriendTab.this,SystemMsg.class);
			
			Intent intent=new Intent();
			intent.setComponent(cn);
			startActivity(intent);
			break;
		case ADD_NEW_FRI:
		{
			Intent i = new Intent(FriendTab.this,AddFriend.class);
			startActivity(i);
			return true;
		}
		case EXIT_APP:
		{
			Intent i = new Intent();
			i.setAction("exit");
			sendBroadcast(i);
			return true;
		}
		}
		return super.onMenuItemSelected(id,item);
		
	}

	@Override
	public void onResume()
	{
		super.onResume();
		IntentFilter ifilter = new IntentFilter();
		ifilter.addAction("exit");
		ifilter.addAction("fri_list");
		ifilter.addAction("status_notify");
		registerReceiver(exitReceiver,ifilter);
		contactsManagerDbAdapter.open();
	}
	@Override
	public void onStop()
	{
		super.onStop();
		unregisterReceiver(exitReceiver);
		contactsManagerDbAdapter.close();
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
			else if("fri_list".equals(intent.getAction()))
			{
				updateList();
			}
			else if("status_notify".equals(intent.getAction()))
			{
				updateList();
			}
		}		
	}
	private ExitReceiver exitReceiver = new ExitReceiver();
	
	@Override
	public Dialog onCreateDialog(int id,Bundle bundle)
	{
		Dialog dialog = null;
		switch(id)
		{
		case VIEW_INTO:
			Builder b = new AlertDialog.Builder(this);
			b.setTitle(selectedFri);
			b.setMessage("selectedMsg");
			dialog = b.create();
		}
		return dialog;		
	}
	@Override
	public void onPrepareDialog(int id,Dialog dialog)
	{
		switch(id)
		{
		case VIEW_INTO:
			AlertDialog ad = (AlertDialog)dialog;
			ad.setTitle(selectedFri);
			ad.setMessage(selectedMsg);
			break;
			default:break;
			
		}
	}
	
	public class MyCursrTreeAdapter extends CursorTreeAdapter {

		public MyCursrTreeAdapter(Cursor cursor, Context context,
				boolean autoRequery) {
			super(cursor, context, autoRequery);
		}		
		@Override
		protected void bindGroupView(View view, Context context, Cursor cursor,
				boolean isExpanded) {
			// TODO Auto-generated method stub
			TextView groupName=(TextView)view.findViewById(R.id.groupName);
			String group=cursor.getString(groupName_index);
			groupName.setText(group);
			
			TextView groupCount=(TextView)view.findViewById(R.id.groupCount);
			int onlineCount = contactsManagerDbAdapter.getOnlineCountContactByGroupName(group);
			int count=contactsManagerDbAdapter.getCountContactByGroupName(group);
			groupCount.setText("["+onlineCount+"/"+count+"]");
		}
		
		@Override
		protected View newGroupView(Context context, Cursor cursor,
				boolean isExpanded, ViewGroup parent) {
			LayoutInflater inflate=LayoutInflater.from(FriendTab.this);
			View view=inflate.inflate(R.layout.grouplayout, null);
			
			bindGroupView(view, context, cursor, isExpanded);
			
			return view;
		}

		@Override
		protected Cursor getChildrenCursor(Cursor groupCursor) {
			String groupName=groupCursor.getString(groupName_index);//得到当前的组名
			Cursor childCursor=contactsManagerDbAdapter.getContactsByGroupName(groupName);
			startManagingCursor(childCursor);
			return childCursor;
		}

		@Override
		protected View newChildView(Context context, Cursor cursor,
				boolean isLastChild, ViewGroup parent) {
			LayoutInflater inflate=LayoutInflater.from(FriendTab.this);
			View view=inflate.inflate(R.layout.childlayout, null);
			
			bindChildView(view, context, cursor, isLastChild);
			
			return view;
		}
		
		@Override
		protected void bindChildView(View view, Context context, Cursor cursor,
				boolean isLastChild) {
			// TODO Auto-generated method stub
			ImageView contactIcon=(ImageView)view.findViewById(R.id.contactIcon);
			//contactIcon.setImageBitmap(getBitmapFromByte(cursor.getBlob(icon_index)));
			contactIcon.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.user));
			TextView name=(TextView)view.findViewById(R.id.name);
			name.setText(cursor.getString(name_index));
			
			TextView description=(TextView)view.findViewById(R.id.description);
			description.setTextKeepState(cursor.getString(description_index));
			
			TextView statusView = (TextView)view.findViewById(R.id.status);
			statusView.setText(cursor.getInt(status_index)==1?"在线":"离线");
			//final String email=cursor.getString(email_index);
			
			ImageView  mycursor=(ImageView)view.findViewById(R.id.myCursor);
			mycursor.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//showToast("点击了图片");
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
								
								ComponentName cn=new ComponentName(FriendTab.this,Graffiti.class);
								Intent intent=new Intent();
								intent.setComponent(cn);
								startActivity(intent);
								//Uri uri=Uri.parse("smsto:"+phoneNumber);
								//Intent it = new Intent(Intent.ACTION_SENDTO, uri);   
								//it.putExtra("sms_body", "呵呵！好久不见");   
								//startActivity(it);  
							}
						});
						
						btnEmail.setOnClickListener(new View.OnClickListener() {
							
							@Override
							public void onClick(View v) {
								listPopupWindow.dismiss();
								//Uri uri = Uri.parse("mailto:"+email);
								//Intent it = new Intent(Intent.ACTION_SENDTO, uri);
								//startActivity(it);
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
    }
	
	  //得到存储在数据库中的头像
	public Bitmap getBitmapFromByte(byte[] temp){
		if(temp!=null){
			Bitmap bitmap=BitmapFactory.decodeByteArray(temp, 0, temp.length);
			return bitmap;
		}else{
            return null;
		}
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
}
