package com.imps.activities;

//好友列表界面


import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.imps.R;
import com.imps.audio.Record;
import com.imps.audio.Track;
import com.imps.handler.UserManager;
import com.imps.util.ChattingAdapter;
import com.imps.util.CommonHelper;
import com.imps.util.ListContentEntity;

public class ChatView extends Activity {
	protected static final String TAG = "ChatView";
	private ListView mListView;
	private Button send;
	private EditText messageText;
	private ArrayList<ListContentEntity> list = new ArrayList<ListContentEntity>();
	//private ListContentAdapter listAdapter;
	private ChattingAdapter listAdapter;
	private String fUsername = null;
	private boolean existSession = false;
	private EditText textEditor;
	private View recording;
	private ImageView faceButton;
	private ImageView audioImageIv;
	private ImageView captureImageIv;
	private ImageView graffitiImageIv;
	private PopupWindow menuWindow = null;
	private Record record = null;
	
	@Override
	public void onResume()
	{
		//设置当前活跃用户
		UserManager.activeFriend = fUsername;
		//broadcast filter
		IntentFilter i = new IntentFilter();
		i.addAction("new_message");
		i.addAction("exit");
		i.addAction("ptpaudio_rsp");
		i.addAction("ptpaudio_req");
		registerReceiver(messagereceiver,i);
		super.onResume();
	}
	@Override
	public void onPause()
	{   //取消当前活跃用户
		super.onPause();
		UserManager.activeFriend = null;
		//remove broadcast filter
		unregisterReceiver(messagereceiver);
	}
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	//	setContentView(R.layout.chat);
		setContentView(R.layout.chatting);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.chatting_title_bar);
		//listAdapter = new ListContentAdapter(ChatView.this, list);
		mListView = (ListView) findViewById(R.id.chatting_history_lv);
		Intent fi = this.getIntent();
		String message = fi.getStringExtra("message");
		fUsername = fi.getStringExtra("fUsername");
		setAdapterForThis();
		send = (Button) findViewById(R.id.send_button);
		send.setOnClickListener(l);
		textEditor = (EditText) findViewById(R.id.text_editor);
		audioImageIv = (ImageView) findViewById(R.id.send_image);
		captureImageIv = (ImageView) findViewById(R.id.capture_image);
		graffitiImageIv = (ImageView)findViewById(R.id.start_graffiti);
		graffitiImageIv.setOnClickListener(l);
		captureImageIv.setOnClickListener(l);
		((TextView)findViewById(R.id.chatting_contact_name)).setText("与"+fUsername+"聊天中");
		recording = findViewById(R.id.recording);
		recording.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();

				switch (action) {
				case MotionEvent.ACTION_UP:
					v.setBackgroundResource(R.drawable.hold_to_talk_normal);
					if (menuWindow != null)
						menuWindow.dismiss();
					Log.d(TAG, "---onTouchEvent action:ACTION_UP");
					//record.stop();
					record.stop = true;
					Date now = new Date(); 
					SimpleDateFormat d1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				    String date = d1.format(now);
                	ListContentEntity listval = new ListContentEntity("系统提示",date,"音频发送成功！",ListContentEntity.MESSAGE_TO);
                	list.add(listval);
                	record.stopThread();
                    Track track = new Track();
                    for(int i=0;i<record.dataList.size();i++)
                    {
                    	track.data.add(record.dataList.get(i));
                    }
                    track.run();
					//发送
					UserManager.getInstance().SendAudio(fUsername, record.dataList);
                    
					break;
				case MotionEvent.ACTION_DOWN:
					v.setBackgroundResource(R.drawable.hold_to_talk_pressed);
					ViewGroup root = (ViewGroup) findViewById(R.id.chat_root);
					View view = LayoutInflater.from(ChatView.this).inflate(R.layout.audio_recorder_ring, null);
					menuWindow = new PopupWindow(view, 180, 180);
					// @+id/recorder_ring
					view.findViewById(R.id.recorder_ring).setVisibility(View.VISIBLE);
					view.setBackgroundResource(R.drawable.pls_talk);
					menuWindow.showAtLocation(root, Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
					Log.d(TAG, "---onTouchEvent action:ACTION_DOWN");
					// AudioRecorder recorder=new AudioRecorder();
					//just for a test!!!
					record = new Record();
					record.start();
					break;

				}

				return true;
			}
		});
		audioImageIv.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String ip = CommonHelper.getLocalIpAddress();
				if(ip==null||"".equals(ip))
				{
					Toast.makeText(ChatView.this, "当前网络状态不佳，请稍后重试", Toast.LENGTH_LONG);
					return;
				}
				Log.d("ChatView", "IP sent is "+ ip);
				UserManager.getInstance().SendPTPAudioReq(fUsername, ip, 1300);
				Log.d("ChatView", "ptp audio request sent");
				ComponentName cn=new ComponentName(ChatView.this,AudioChat.class);
				Intent intent=new Intent();
				intent.putExtra("fUsername", fUsername);
				intent.setComponent(cn);
				startActivity(intent);
				//do what?
			}			
		});
		faceButton = (ImageView)findViewById(R.id.sms_button_insert);
		faceButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ComponentName cn=new ComponentName(ChatView.this,FaceDialog.class);
				Intent intent=new Intent();
				intent.setComponent(cn);
				startActivityForResult(intent, 0);
			}
			
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		case 0:{   //face
			switch (resultCode){
			case 0:{
				if(data==null)
					return;
				int faceId = data.getIntExtra("selectedFace", 0);
				String id = changeIdToStr(faceId);
		        Drawable drawable = getResources().getDrawable(faceId);   
		        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());   
		        SpannableString spannable = new SpannableString(id);   
		        //要让图片替代指定的文字就要用ImageSpan   
		        ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);   
		        //开始替换，注意第2和第3个参数表示从哪里开始替换到哪里替换结束（start和end）   
		        //最后一个参数类似数学中的集合,[5,12)表示从5到12，包括5但不包括12 
		        spannable.setSpan(span, 0, id.length(), 
		        		Spannable.SPAN_INCLUSIVE_EXCLUSIVE);     
		        textEditor.append(spannable);
		        break;
			    }
			default:
			    break;
			}			
		}
		case 1:{  //graffiti
			switch(resultCode){
			case 0:  //false ,do nothing
				break;
			case 1:  //true, get bitmap path
				String path = data.getStringExtra("path");
				if(path!=null){
					
					//TODO:send out...
					sendImageMsg(path);
				}
				break;
			}
			
		}
			break;
		}
		
	}
	
	private String changeIdToStr(int faceId) {
		// TODO Auto-generated method stub
		switch (faceId){
		case R.drawable.exp_01:
			return "[exp_01]";
		case R.drawable.exp_02:
			return "[exp_02]";
		case R.drawable.exp_03:
			return "[exp_03]";
		case R.drawable.exp_04:
			return "[exp_04]";
		case R.drawable.exp_05:
			return "[exp_05]";
		case R.drawable.exp_06:
			return "[exp_06]";
		case R.drawable.exp_07:
			return "[exp_07]";
		case R.drawable.exp_08:
			return "[exp_08]";
		case R.drawable.exp_09:
			return "[exp_09]";
		case R.drawable.exp_10:
			return "[exp_10]";
		case R.drawable.exp_11:
			return "[exp_11]";
		case R.drawable.exp_12:
			return "[exp_12]";
		case R.drawable.exp_13:
			return "[exp_13]";
		case R.drawable.exp_14:
			return "[exp_14]";
		case R.drawable.exp_15:
			return "[exp_15]";
		case R.drawable.exp_16:
			return "[exp_16]";
		case R.drawable.exp_17:
			return "[exp_17]";
		case R.drawable.exp_18:
			return "[exp_18]";
		case R.drawable.exp_19:
			return "[exp_19]";
		case R.drawable.exp_20:
			return "[exp_20]";
		case R.drawable.exp_21:
			return "[exp_21]";
		case R.drawable.exp_22:
			return "[exp_22]";
		case R.drawable.exp_23:
			return "[exp_23]";
		case R.drawable.exp_24:
			return "[exp_24]";
		case R.drawable.exp_25:
			return "[exp_25]";
		case R.drawable.exp_26:
			return "[exp_26]";
		}
		return null;
	}
	private void setAdapterForThis() {
		// TODO Auto-generated method stub
		listAdapter = new ChattingAdapter(this,list);
		mListView.setAdapter(listAdapter);
		initMessages();
		
	}
	
	private void initMessages() {
		// TODO Auto-generated method stub
		Log.d("ChatView", "ChatView:listview count is "+mListView.getCount());
		if(mListView.getCount()==0&&UserManager.CurSessionFriList.containsKey(fUsername))
		{
			Log.d("ChatView", "ChatView:initialing the chat view with old msg");
			List<String> mbox = UserManager.CurSessionFriList.get(fUsername);
			for(int i=0;i<mbox.size();i++)
			{
				Log.d("ChatView", ""+i+" "+ mbox.get(i));
				String msg = mbox.get(i);
                String fname = msg.substring(0, msg.indexOf("|"));
                msg = msg.substring(msg.indexOf("|") + 1);
                String date = msg.substring(0, msg.indexOf("|"));
                msg = msg.substring(msg.indexOf("|") + 1);
                if(fname.equals(UserManager.getGlobaluser().getUsername()))
                {
                	ListContentEntity d1 = new ListContentEntity(fname,date,msg,ListContentEntity.MESSAGE_TO);
                	list.add(d1);
                }
                else{
                	ListContentEntity d1 = new ListContentEntity(fname,date,msg,ListContentEntity.MESSAGE_FROM);
                	list.add(d1);
                }
				System.out.println("add the existing messages");
			}
			existSession = true;
		}
	}
	
	private View.OnClickListener l = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			if (v.getId() == send.getId()) {
				String str = textEditor.getText().toString();
				String sendStr;
				if (str != null
						&& (sendStr = str.trim().replaceAll("\r", "").replaceAll("\t", "").replaceAll("\n", "")
								.replaceAll("\f", "")) != "") {
					sendMessage(sendStr);

				}
				textEditor.setText("");

			} else if (v.getId() == captureImageIv.getId()) {
				String ip = CommonHelper.getLocalIpAddress();
				if(ip==null||"".equals(ip))
				{
					Toast.makeText(ChatView.this, "当前网络状态不佳，请稍后重试", Toast.LENGTH_LONG);
					return;
				}
				Log.d("ChatView", "IP sent is "+ ip);
				UserManager.getInstance().SendPTPVideoReq(fUsername, ip, 1300);
				Log.d("ChatView", "ptp video request sent");
				ComponentName cn=new ComponentName(ChatView.this,VideoContact2.class);
				Intent intent=new Intent();
				intent.putExtra("fUsername", fUsername);
				intent.setComponent(cn);
				startActivity(intent);
			}else if(v.getId()==graffitiImageIv.getId()){
				Log.d("ChatView", "graffiti clicked...");
				ComponentName cn=new ComponentName(ChatView.this,Graffiti.class);
				Intent intent=new Intent();
				intent.setComponent(cn);
				startActivityForResult(intent, 1);
			}
		}

		// 发送消息
		private void sendMessage(String sendStr) {
			UserManager.getInstance().SendMsg(fUsername, sendStr);
			Date now = new Date(); 
			SimpleDateFormat d1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    String date = d1.format(now);
			list.add(new ListContentEntity(UserManager.getGlobaluser().getUsername(),date,sendStr,ListContentEntity.MESSAGE_TO));
			listAdapter.notifyDataSetChanged();
			String text = UserManager.getGlobaluser().getUsername()+"|"+date+"|"+sendStr;
			//加入当前会话
			if(UserManager.CurSessionFriList.containsKey(fUsername))
			{
				 UserManager.CurSessionFriList.get(fUsername).add(text);
				 Log.d("ChatView","adding to the list");
			}
			   
			else{
				List<String> newmsgbox = new ArrayList<String>();
				newmsgbox.add(text);
				UserManager.CurSessionFriList.put(fUsername, newmsgbox);
				Log.d("ChatView","adding to the list with new msg");
			}
		}
	};
	public void sendImageMsg(String path){
		Date now = new Date(); 
		SimpleDateFormat d1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    String date = d1.format(now);
		list.add(new ListContentEntity(UserManager.getGlobaluser().getUsername(),date,"",ListContentEntity.MESSAGE_TO_PICTURE,path));
		listAdapter.notifyDataSetChanged();
	}
	
	
	/**
	 * append message to the list view and update it
	 * @param name
	 * @param msg
	 * @param stime
	 */
	public void appendToList(String name,String msg,String stime)
	{
		String text = name +"|"+stime+"|"+msg;
	    ListContentEntity d4 = new ListContentEntity(name,stime,msg,ListContentEntity.MESSAGE_FROM);
		list.add(d4);
		listAdapter.notifyDataSetChanged();
		UserManager.CurSessionFriList.get(name).add(text);
		System.out.println("apend to list! ");
	}
	
	public class MessageReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			String action = arg1.getAction();
			if("exit".equals(action))
			{
				finish();
			}
			else if("new_message".equals(action))
			{
				System.out.println("!!!!!broadcast message has been received!!!!!");
				Bundle extra = arg1.getExtras();
				String friname = extra.getString("fUsername");
				String message = extra.getString("message");
				message = message.substring(message.lastIndexOf("|") + 1);
				String rtime = extra.getString("time");
				System.out.println("message broadcast received!");
				if(friname!=null&&message!=null)
				{
					if(friname!=null&&message!=null&&rtime!=null)
					{
						if(fUsername.equals(friname))
						{
							appendToList(friname,message,rtime);
						}
						else{
							if(message.length()>15){
								message = message.substring(0,15);
							}
							Toast.makeText(ChatView.this, friname+" : "+message, Toast.LENGTH_SHORT).show();
						}
					}
				}
				else return;
			}
			else if("ptpaudio_req".equals(action))
			{
				System.out.println("!!!!!audio req broadcast message has been received!!!!!");
				Bundle extra = arg1.getExtras();
				String friname = extra.getString("fUsername");
				String ip = extra.getString("ip");
				int port = extra.getInt("port");
			}
			else if("ptpaudio_rsp".equals(action))
			{
				System.out.println("!!!!!audio rsp broadcast message has been received!!!!!");
				Bundle extra = arg1.getExtras();
				String friname = extra.getString("fUsername");
				int result = extra.getInt("result");
			}
			
		}
	}
	private MessageReceiver messagereceiver = new MessageReceiver();

}
