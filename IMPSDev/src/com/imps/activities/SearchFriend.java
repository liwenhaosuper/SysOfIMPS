package com.imps.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.imps.IMPSDev;
import com.imps.R;
import com.imps.basetypes.FindFriendItem;
import com.imps.events.IContactEvent;
import com.imps.services.impl.ServiceManager;
import com.imps.ui.widget.FindFriendAdapter;

public class SearchFriend extends Activity implements IContactEvent{
	private static String TAG = SearchFriend.class.getCanonicalName();
	private static boolean DEBUG = IMPSDev.isDEBUG();
	private static final int FRIENDLIST = Menu.FIRST+1;
	private static final int ADDFRIEND = Menu.FIRST+2;
	private ListView defaultList;
	private EditText mSearchContent;
	private ImageView mSearchBtn;
	private FindFriendAdapter adapter;
	private String friend;
	private List<FindFriendItem> mItems = new ArrayList<FindFriendItem>();
	private Handler receiver = new Handler(){
		@Override
		public void handleMessage(Message msg){
			switch(msg.what){
			case FRIENDLIST:
				List< String> users = (ArrayList)msg.obj;
				mItems.clear();
				initData();
				for(int i=0;i<users.size();i++){
					FindFriendItem item = new FindFriendItem();
					item.image = R.drawable.user;
					item.text = users.get(i);
					mItems.add(item);
				}
				adapter.notifyDataSetChanged();
				break;
			}
		}
	};
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.searchfriend);
		defaultList = (ListView)findViewById(android.R.id.list);
		mSearchContent = (EditText)findViewById(R.id.please_input_keywords);
		mSearchBtn = (ImageView)findViewById(R.id.search_friend);
		initAdapter();
		mSearchBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				String search = mSearchContent.getText().toString();
				if("".equals(search)){
					return;
				}
			    ServiceManager.getmContact().sendSearchFriendReq(search);
			}
			
		});
		defaultList.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				friend = mItems.get(position).text;
				showDialog(ADDFRIEND);
				
			}});
		ServiceManager.getmNetLogic().addContactEventHandler(this);
	}
	public void initAdapter(){
		adapter = new FindFriendAdapter(this,mItems);
		defaultList.setAdapter(adapter);
		initData();
	}
	public void initData(){

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
	public void onRecvFriendReq(String friName) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onRecvFriendRsp(String friName, boolean rel) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onUpdateFriStatus() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onUpdateFriList() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void sendSearchFriendReq(String keyword) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onRecvSearchFriendRsq(List<String> friends) {
		Message msg = new Message();
		msg.what = FRIENDLIST;
		msg.obj = friends;
		receiver.sendMessage(msg);
		
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
	public void sendAddFriendRsp(String friname, boolean res) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onRecvAddFriendRsp(String friname, boolean res) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Dialog onCreateDialog(int id){
		Dialog dialog = null;
		Builder builder = new AlertDialog.Builder(this);
		switch(id){
		case ADDFRIEND:
			builder.setMessage(getResources().getString(R.string.whether_add_new_friend,friend));
			builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					ServiceManager.getmContact().sendAddFriendReq(friend);
					if(DEBUG) Log.d(TAG,"add friend "+friend+" sent");
					Toast.makeText(IMPSDev.getContext(), getResources().getString(R.string.add_friend_req_sent), Toast.LENGTH_LONG);
				}
			});
			dialog = builder.create();
			break;
		}
		return dialog;
	}
}
