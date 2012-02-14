package com.imps.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.imps.IMPSDev;
import com.imps.IMPSMain;
import com.imps.R;
import com.imps.basetypes.MediaType;
import com.imps.services.impl.ServiceManager;

public class PopupMsg extends Activity {
	private static String TAG = PopupMsg.class.getCanonicalName();
	private static boolean DEBUG =IMPSDev.isDEBUG();
	
	private Button mBtnClose;
	private Button mBtnMain;
	private Button mBtnReplay;
	private TextView mTitle;
	private TextView mMsgContent;
	private EditText mReplay;
	private String friName ="";
	private String msg = "";
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.popupmsg);
		mBtnClose = (Button)findViewById(R.id.popup_closePopupmsg);
		mBtnMain = (Button)findViewById(R.id.popup_launchMain);
		mBtnReplay = (Button)findViewById(R.id.popup_msgReplay);
		mTitle = (TextView)findViewById(R.id.popup_title);
		mMsgContent = (TextView)findViewById(R.id.popup_content);
		mReplay = (EditText)findViewById(R.id.popup_replaymsg);
		Intent intent = this.getIntent();
		if(intent!=null){
			friName = intent.getStringExtra("friName");
			msg = intent.getStringExtra("msg");
			mTitle.setText(getResources().getString(R.string.newSmsFrom,friName));
			mMsgContent.setText(msg);
		}
		mBtnClose.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
		mBtnMain.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(PopupMsg.this,IMPSMain.class);
				startActivity(in);
				finish();
			}});
		mBtnReplay.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(DEBUG) Log.d(TAG,"replay click...");
				String replay = mReplay.getText().toString();
				if(replay==null||replay.equals("")){
					Toast.makeText(PopupMsg.this, getResources().getString(R.string.empty_sms_warning), Toast.LENGTH_SHORT);
					return;
				}
				MediaType item = new MediaType(MediaType.SMS,friName,MediaType.to);
				item.setMsgContant(replay);
				//ServiceManager.getmSms().sendSms(item);
				Toast.makeText(PopupMsg.this, getResources().getString(R.string.sending), Toast.LENGTH_LONG);
				mReplay.setText("");
			}});
	}
}
