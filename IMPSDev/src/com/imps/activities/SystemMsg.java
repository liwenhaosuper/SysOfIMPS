package com.imps.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.ListView;

import com.imps.IMPSDev;
import com.imps.R;
import com.imps.net.handler.UserManager;
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
	private Handler receiver = new Handler(){
		@Override
		public void handleMessage(Message msg){
			switch(msg.what){
			case REFRESH:
				if(mAdapter!=null){
					mAdapter.notifyDataSetChanged();
				}
				break;
			}
		}
	};
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
		if(mAdapter!=null){
			mAdapter.notifyDataSetChanged();
		}
	}
	public void initAdapter(){
		mAdapter = new SystemMsgAdapter(this,UserManager.mSysMsgs);
		mList.setAdapter(mAdapter);
		initData();
	}
	public void initData(){
		
	}
}
