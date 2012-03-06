package com.imps.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;

import com.imps.IMPSDev;
import com.imps.R;
import com.imps.basetypes.Constant;
import com.imps.net.handler.UserManager;
import com.imps.receivers.IMPSBroadcastReceiver;
import com.imps.ui.widget.SystemMsgAdapter;
/**
 * SystemMsg contains add friend req&rsp msg,system notice and so on
 * @author liwenhaosuper
 *
 */
public class SystemMsg extends Activity{
	private static String TAG = SystemMsg.class.getCanonicalName();
	private static boolean DEBUG = IMPSDev.isDEBUG();
	public static final int REFRESH = 1;
	public static SystemMsgAdapter mAdapter;
	private ListView mList;
    private SystemMsgReceiver receiver = new SystemMsgReceiver();
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.systemmsg);
		mList = (ListView)findViewById(R.id.sys_msg_list);
		initAdapter();
	}
	public void onResume(){
		super.onResume();
		registerReceiver(receiver,receiver.getFilter());
		if(mAdapter!=null){
			mAdapter.notifyDataSetChanged();
		}
	}
	public void onStop(){
		super.onStop();
		unregisterReceiver(receiver);
	}
	public void initAdapter(){
		mAdapter = new SystemMsgAdapter(this,UserManager.mSysMsgs);
		mList.setAdapter(mAdapter);
		initData();
	}
	public void initData(){
		
	}
	
	public class SystemMsgReceiver extends IMPSBroadcastReceiver{
		@Override
		public void onReceive(Context context,Intent intent){
			super.onReceive(context, intent);
			if(intent.getAction().equals(Constant.ADDFRIENDREQ)){
				mAdapter.notifyDataSetChanged();
			}else if(intent.getAction().equals(Constant.ADDFRIENDRSP)){
				mAdapter.notifyDataSetChanged();
			}
		}
		@Override
		public IntentFilter getFilter(){
			IntentFilter filter = super.getFilter();
			filter.addAction(Constant.ADDFRIENDREQ);
			filter.addAction(Constant.ADDFRIENDRSP);
			return filter;
		}
	}
}
