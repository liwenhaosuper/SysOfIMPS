package com.imps.activities;



import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.imps.IMPSActivity;
import com.imps.IMPSDev;
import com.imps.R;
import com.imps.basetypes.Constant;
import com.imps.basetypes.ListContentEntity;
import com.imps.basetypes.MediaType;
import com.imps.basetypes.SystemMsgType;
import com.imps.basetypes.UserMessage;
import com.imps.media.audio.Record;
import com.imps.media.audio.Track;
import com.imps.net.handler.UserManager;
import com.imps.receivers.IMPSBroadcastReceiver;
import com.imps.services.impl.ServiceManager;
import com.imps.ui.widget.ChattingAdapter;
import com.imps.util.LocalDBHelper;

public class ChatView extends IMPSActivity{
	protected static final String TAG = ChatView.class.getCanonicalName();
	private static boolean DEBUG = IMPSDev.isDEBUG();
	private ListView mListView;
	private Button send;
	public static ArrayList<ListContentEntity> list = new ArrayList<ListContentEntity>();
	private ChattingAdapter listAdapter;
	private String fUsername = "";
	private EditText textEditor;
	private View recording;
	private ImageView faceButton;
	private ImageView audioImageIv;
	private ImageView captureImageIv;
	private ImageView graffitiImageIv;
	private PopupWindow menuWindow = null;
	private Record record = null;
	private ChatViewReceiver receiver = new ChatViewReceiver();
	private LocalDBHelper localDB = new LocalDBHelper(this);
	private static boolean resume = false;
	
	private static Map<Integer, String> faces = new HashMap<Integer, String>();
	
	@Override
	public void onResume()
	{
		UserManager.activeFriend = fUsername;
		super.onResume();
		registerReceiver(receiver,receiver.getFilter());
		listAdapter.notifyDataSetChanged();
	}
	@Override
	public void onPause()
	{   
		super.onPause();
		UserManager.activeFriend = null;
	}
	@Override
	public void onStop(){
		super.onStop();
		unregisterReceiver(receiver);

	}
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.chatview);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.chatting_title_bar);
		list = new ArrayList<ListContentEntity>();
		mListView = (ListView) findViewById(R.id.chatting_history_lv);
		Intent fi = this.getIntent();
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
		((TextView)findViewById(R.id.chatting_contact_name)).setText(this.getResources().getString(R.string.chat_title, fUsername));
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
					if(DEBUG) Log.d(TAG, "---onTouchEvent action:ACTION_UP");
					record.stop = true;
                	record.stopThread();
                    Track track = new Track();
                    for(int i=0;i<record.dataList.size();i++)
                    {
                    	track.data.add(record.dataList.get(i));
                    }
                    track.run();
                    MediaType media = new MediaType(MediaType.AUDIO,record.dataList,MediaType.to);
                    media.setFriend(fUsername);media.setTime(getTime());
                    if(DEBUG){
                    	Log.d(TAG,"Audio size:"+media.getContant().size());
                    }
                    if(media.getContant().size()<3){
                    	if(DEBUG)Log.d(TAG,"Audio data too small...");
                    	break;
                    }
        			if(UserManager.CurSessionFriList.containsKey(fUsername))
        			{
        				 UserManager.CurSessionFriList.get(fUsername).add(media);
        				 if(DEBUG) Log.d(TAG,"adding to the list");
        			}
        			else{
        				List<MediaType> newmsgbox = new ArrayList<MediaType>();
        				newmsgbox.add(media);
        				UserManager.CurSessionFriList.put(fUsername, newmsgbox);
        				if(DEBUG) Log.d(TAG,"adding to the list with new msg");
        			}
                    ServiceManager.getmSms().SendAudio(media);
            	    ListContentEntity d4 = new ListContentEntity(UserManager.getGlobaluser().getUsername(),getTime(),
            	    		"",ListContentEntity.MESSAGE_TO_AUDIO,media.getContant());
            		list.add(d4);
            		listAdapter.notifyDataSetChanged();
					break;
				case MotionEvent.ACTION_DOWN:
					v.setBackgroundResource(R.drawable.hold_to_talk_pressed);
					ViewGroup root = (ViewGroup) findViewById(R.id.chat_root);
					View view = LayoutInflater.from(ChatView.this).inflate(R.layout.audio_recorder_ring, null);
					menuWindow = new PopupWindow(view, 180, 180);
					view.findViewById(R.id.recorder_ring).setVisibility(View.VISIBLE);
					view.setBackgroundResource(R.drawable.pls_talk);
					menuWindow.showAtLocation(root, Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
					if(DEBUG) Log.d(TAG, "---onTouchEvent action:ACTION_DOWN");
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
				if(!ServiceManager.getmNet().isAvailable()){
					Toast.makeText(ChatView.this, getResources().getString(R.string.net_problem), Toast.LENGTH_LONG);
					return;
				}
				String ip = ServiceManager.getmNet().getLocalIP(false);
				if(DEBUG) Log.d(TAG, "IP sent is "+ ip);
				ServiceManager.getmAudio().SendPTPAudioReq(fUsername);
				if(DEBUG) Log.d(TAG, "ptp audio request sent");
				ServiceManager.getmSound().playRingTone();
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
		initFaceMap();
	}
	
	private void initFaceMap() {
		// TODO Auto-generated method stub
		faces.put(R.drawable.exp_01, "[exp_01]");
		faces.put(R.drawable.exp_02, "[exp_02]");
		faces.put(R.drawable.exp_03, "[exp_03]");
		faces.put(R.drawable.exp_04, "[exp_04]");
		faces.put(R.drawable.exp_05, "[exp_05]");
		faces.put(R.drawable.exp_06, "[exp_06]");
		faces.put(R.drawable.exp_07, "[exp_07]");
		faces.put(R.drawable.exp_08, "[exp_08]");
		faces.put(R.drawable.exp_09, "[exp_09]");
		faces.put(R.drawable.exp_10, "[exp_10]");
		faces.put(R.drawable.exp_11, "[exp_11]");
		faces.put(R.drawable.exp_12, "[exp_12]");
		faces.put(R.drawable.exp_13, "[exp_13]");
		faces.put(R.drawable.exp_14, "[exp_14]");
		faces.put(R.drawable.exp_15, "[exp_15]");
		faces.put(R.drawable.exp_16, "[exp_16]");
		faces.put(R.drawable.exp_17, "[exp_17]");
		faces.put(R.drawable.exp_18, "[exp_18]");
		faces.put(R.drawable.exp_19, "[exp_19]");
		faces.put(R.drawable.exp_20, "[exp_20]");
		faces.put(R.drawable.exp_21, "[exp_21]");
		faces.put(R.drawable.exp_22, "[exp_22]");
		faces.put(R.drawable.exp_23, "[exp_23]");
		faces.put(R.drawable.exp_24, "[exp_24]");
		faces.put(R.drawable.exp_25, "[exp_25]");
		faces.put(R.drawable.exp_26, "[exp_26]");
		faces.put(R.drawable.e001, "[e001]");
		faces.put(R.drawable.e002, "[e002]");
		faces.put(R.drawable.e003, "[e003]");
		faces.put(R.drawable.e004, "[e004]");
		faces.put(R.drawable.e005, "[e005]");
		faces.put(R.drawable.e00d, "[e00d]");
		faces.put(R.drawable.e00e, "[e00e]");
		faces.put(R.drawable.e00f, "[e00f]");
		faces.put(R.drawable.e010, "[e010]");
		faces.put(R.drawable.e011, "[e011]");
		faces.put(R.drawable.e012, "[e012]");
		faces.put(R.drawable.e020, "[e020]");
		faces.put(R.drawable.e021, "[e021]");
		faces.put(R.drawable.e022, "[e022]");
		faces.put(R.drawable.e023, "[e023]");
		faces.put(R.drawable.e036, "[e036]");
		faces.put(R.drawable.e038, "[e038]");
		faces.put(R.drawable.e03e, "[e03e]");
		faces.put(R.drawable.e048, "[e048]");
		faces.put(R.drawable.e049, "[e049]");
		faces.put(R.drawable.e04a, "[e04a]");
		faces.put(R.drawable.e04b, "[e04b]");
		faces.put(R.drawable.e04c, "[e04c]");
		faces.put(R.drawable.e04e, "[e04e]");
		faces.put(R.drawable.e056, "[e056]");
		faces.put(R.drawable.e057, "[e057]");
		faces.put(R.drawable.e058, "[e058]");
		faces.put(R.drawable.e059, "[e059]");
		faces.put(R.drawable.e05a, "[e05a]");
		faces.put(R.drawable.e105, "[e105]");
		faces.put(R.drawable.e106, "[e106]");
		faces.put(R.drawable.e107, "[e107]");
		faces.put(R.drawable.e108, "[e108]");
		faces.put(R.drawable.e10c, "[e10c]");
		faces.put(R.drawable.e111, "[e111]");
		faces.put(R.drawable.e115, "[e115]");
		faces.put(R.drawable.e117, "[e117]");
		faces.put(R.drawable.e11a, "[e11a]");
		faces.put(R.drawable.e11c, "[e11c]");
		faces.put(R.drawable.e11d, "[e11d]");
		faces.put(R.drawable.e13c, "[e13c]");
		faces.put(R.drawable.e13d, "[e13d]");
		faces.put(R.drawable.e14c, "[e14c]");
		faces.put(R.drawable.e14d, "[e14d]");
		faces.put(R.drawable.e152, "[e152]");
		faces.put(R.drawable.e153, "[e153]");
		faces.put(R.drawable.e155, "[e155]");
		faces.put(R.drawable.e156, "[e156]");
		faces.put(R.drawable.e157, "[e157]");
		faces.put(R.drawable.e201, "[e201]");
		faces.put(R.drawable.e21c, "[e21c]");
		faces.put(R.drawable.e21d, "[e21d]");
		faces.put(R.drawable.e21e, "[e21e]");
		faces.put(R.drawable.e21f, "[e21f]");
		faces.put(R.drawable.e220, "[e220]");
		faces.put(R.drawable.e221, "[e221]");
		faces.put(R.drawable.e222, "[e222]");
		faces.put(R.drawable.e22e, "[e22e]");
		faces.put(R.drawable.e22f, "[e22f]");
		faces.put(R.drawable.e230, "[e230]");
		faces.put(R.drawable.e231, "[e231]");
		faces.put(R.drawable.e253, "[e253]");
		faces.put(R.drawable.e31d, "[e31d]");
		faces.put(R.drawable.e31e, "[e31e]");
		faces.put(R.drawable.e31f, "[e31f]");
		faces.put(R.drawable.e326, "[e326]");
		faces.put(R.drawable.e327, "[e327]");
		faces.put(R.drawable.e328, "[e328]");
		faces.put(R.drawable.e329, "[e329]");
		faces.put(R.drawable.e32a, "[e32a]");
		faces.put(R.drawable.e32b, "[e32b]");
		faces.put(R.drawable.e32c, "[e32c]");
		faces.put(R.drawable.e32d, "[e32d]");
		faces.put(R.drawable.e32e, "[e32e]");
		faces.put(R.drawable.e32f, "[e32f]");
		faces.put(R.drawable.e330, "[e330]");
		faces.put(R.drawable.e331, "[e331]");
		faces.put(R.drawable.e334, "[e334]");
		faces.put(R.drawable.e401, "[e401]");
		faces.put(R.drawable.e402, "[e402]");
		faces.put(R.drawable.e403, "[e403]");
		faces.put(R.drawable.e404, "[e404]");
		faces.put(R.drawable.e405, "[e405]");
		faces.put(R.drawable.e406, "[e406]");
		faces.put(R.drawable.e407, "[e407]");
		faces.put(R.drawable.e408, "[e408]");
		faces.put(R.drawable.e409, "[e409]");
		faces.put(R.drawable.e40a, "[e40a]");
		faces.put(R.drawable.e40b, "[e40b]");
		faces.put(R.drawable.e40c, "[e40c]");
		faces.put(R.drawable.e40d, "[e40d]");
		faces.put(R.drawable.e40e, "[e40e]");
		faces.put(R.drawable.e40f, "[e40f]");
		faces.put(R.drawable.e410, "[e410]");
		faces.put(R.drawable.e411, "[e411]");
		faces.put(R.drawable.e412, "[e412]");
		faces.put(R.drawable.e413, "[e413]");
		faces.put(R.drawable.e414, "[e414]");
		faces.put(R.drawable.e415, "[e415]");
		faces.put(R.drawable.e416, "[e416]");
		faces.put(R.drawable.e417, "[e417]");
		faces.put(R.drawable.e418, "[e418]");
		faces.put(R.drawable.e419, "[e419]");
		faces.put(R.drawable.e41a, "[e41a]");
		faces.put(R.drawable.e41b, "[e41b]");
		faces.put(R.drawable.e41c, "[e41c]");
		faces.put(R.drawable.e41d, "[e41d]");
		faces.put(R.drawable.e41e, "[e41e]");
		faces.put(R.drawable.e41f, "[e41f]");
		faces.put(R.drawable.e420, "[e420]");
		faces.put(R.drawable.e421, "[e421]");
		faces.put(R.drawable.e422, "[e422]");
		faces.put(R.drawable.e423, "[e423]");
		faces.put(R.drawable.e424, "[e424]");
		faces.put(R.drawable.e425, "[e425]");
		faces.put(R.drawable.e426, "[e426]");
		faces.put(R.drawable.e427, "[e427]");
		faces.put(R.drawable.e428, "[e428]");
		faces.put(R.drawable.e429, "[e429]");
		faces.put(R.drawable.e436, "[e436]");
		faces.put(R.drawable.e437, "[e437]");
		faces.put(R.drawable.e438, "[e438]");
		faces.put(R.drawable.e439, "[e439]");
		faces.put(R.drawable.e43a, "[e43a]");
		faces.put(R.drawable.e43b, "[e43b]");
		faces.put(R.drawable.e443, "[e443]");
		faces.put(R.drawable.e515, "[e515]");
		faces.put(R.drawable.e516, "[e516]");
		faces.put(R.drawable.e517, "[e517]");
		faces.put(R.drawable.e518, "[e518]");
		faces.put(R.drawable.e519, "[e519]");
		faces.put(R.drawable.e51a, "[e51a]");
		faces.put(R.drawable.e51b, "[e51b]");
		faces.put(R.drawable.e51c, "[e51c]");
		faces.put(R.drawable.e51e, "[e51e]");
		faces.put(R.drawable.e51f, "[e51f]");
		faces.put(R.drawable.e536, "[e536]");
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		case 0:{   //face
			switch (resultCode){
			case 0:{
				if(data==null){
					if(DEBUG) Log.d(TAG,"Face data is null");
					return;
				}
				int faceId = data.getIntExtra("selectedFace", 0);
				if(DEBUG) Log.d(TAG,"FaceId:"+faceId);
				String id = faces.get(faceId);
				if(DEBUG) Log.d(TAG,"Id:"+id);
		        Drawable drawable = getResources().getDrawable(faceId);   
		        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());   
		        SpannableString spannable = new SpannableString(id);   
		       
		        ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);   
		      
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
					sendImageMsg(path);
				}
				break;
			}		
		}
			break;
		}
		
	}

	private void setAdapterForThis() {
		// TODO Auto-generated method stub
		listAdapter = new ChattingAdapter(this,list);
		mListView.setAdapter(listAdapter);
		initMessages();
		
	}
	
	private void initMessages() {
		// TODO Auto-generated method stub
		if(DEBUG) Log.d(TAG, "ChatView:listview count is "+mListView.getCount());
		
		if(fUsername.equals("SysAdmin")){
			for(int i=0;i<UserManager.mSysMsgs.size();i++){
				SystemMsgType sysmsg=UserManager.mSysMsgs.get(i);
				String content=sysmsg.text;
				content+="\n";
				content+=getResources().getString(R.string.goto_sysmsg);
				list.add(new ListContentEntity(fUsername,

						sysmsg.time,content,ListContentEntity.MESSAGE_FROM));
			}
			return;
		}
		
		
		if(UserManager.CurSessionFriList.containsKey(fUsername))
		{
			Log.d(TAG, "ChatView:initialing the chat view with old msg");
			
			// Add local history message to current session's message list
			if (!resume && UserManager.CurSessionFriList.get(fUsername).size() == 0) {
				// Add local history message to current session's message list
				ArrayList<UserMessage> history = localDB.fetchMsg(fUsername);
				List<MediaType> mbox = UserManager.CurSessionFriList.get(fUsername);
				if (history != null) {
					for (UserMessage m : history) {
						if (m.getDir() == 1)
//							list.add(new ListContentEntity(m.getFriend(), m
//									.getTime(), m.getContent(),
//									ListContentEntity.MESSAGE_FROM));
							mbox.add(new MediaType(MediaType.SMS,m.getContent(),MediaType.from));
						else
//							list.add(new ListContentEntity(UserManager.globaluser.getUsername(), m
//									.getTime(), m.getContent(),
//									ListContentEntity.MESSAGE_TO));
							mbox.add(new MediaType(MediaType.SMS,m.getContent(),MediaType.to));
					}
				}
				resume = true;
			}
			
			// Add received messages in the active chat session
			List<MediaType> mbox = UserManager.CurSessionFriList.get(fUsername);
			MediaType media;
			String msg;
			for(int i=0;i<mbox.size();i++)
			{
				if(DEBUG) Log.d(TAG, ""+i+" "+ mbox.get(i));
				media = mbox.get(i);
                String fname = media.getDirecet()==MediaType.from?media.getFriend():UserManager.globaluser.getUsername();
                msg = media.getMsgContant();
                String date = media.getTime();
                if(fname.equals(UserManager.getGlobaluser().getUsername()))
                {
                	if(media.getType()==MediaType.SMS){
                    	ListContentEntity d1 = new ListContentEntity(fname,date,msg,ListContentEntity.MESSAGE_TO);
                    	list.add(d1);
                	}else if(media.getType()==MediaType.IMAGE){
                		list.add(new ListContentEntity(fname,date,"",ListContentEntity.MESSAGE_TO_PICTURE,media.getMsgContant()));
                	}else if(media.getType()==MediaType.AUDIO){
                		list.add(new ListContentEntity(fname,date,"",ListContentEntity.MESSAGE_TO_AUDIO,media.getContant()));
                	}
                }
                else{
                	if(media.getType()==MediaType.SMS){
                    	ListContentEntity d1 = new ListContentEntity(fname,date,msg,ListContentEntity.MESSAGE_FROM);
                    	list.add(d1);
                	}
                	else if(media.getType()==MediaType.IMAGE){
                		list.add(new ListContentEntity(fname,date,"",ListContentEntity.MESSAGE_FROM_PICTURE,media.getMsgContant()));
                	}else if(media.getType()==MediaType.AUDIO){
                		list.add(new ListContentEntity(fname,date,"",ListContentEntity.MESSAGE_FROM_AUDIO,media.getContant()));
                	}
                }
                if(DEBUG) Log.d(TAG,"add the existing messages");
			}
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
					
					/**
					 * Store messages which have been sent
					 * into local message database
					 * @author Styx
					 */
					localDB.storeMsg(textEditor.getText().toString(),
							new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()),
							fUsername, 0);

				}
				textEditor.setText("");

			} else if (v.getId() == captureImageIv.getId()) {
				if(!ServiceManager.getmNet().isAvailable()){
					Toast.makeText(ChatView.this,getResources().getString(R.string.net_problem), Toast.LENGTH_LONG);
					return;
				}
				String ip = ServiceManager.getmNet().getLocalIP(false);
				if(ip==null){
					Toast.makeText(ChatView.this, getResources().getString(R.string.net_problem),Toast.LENGTH_SHORT).show();
					return;
				}
				if(DEBUG) Log.d(TAG, "IP sent is "+ ip);
				ServiceManager.getmVideo().SendPTPVideoReq(fUsername, ip, 1300);
				if(DEBUG) Log.d(TAG, "ptp video request sent");
				ComponentName cn=new ComponentName(ChatView.this,VideoContact.class);
				Intent intent=new Intent();
				intent.putExtra("fUsername", fUsername);
				intent.setComponent(cn);
				startActivity(intent);
			}else if(v.getId()==graffitiImageIv.getId()){
				if(DEBUG) Log.d(TAG, "graffiti clicked...");
				ComponentName cn=new ComponentName(ChatView.this,Graffiti.class);
				Intent intent=new Intent();
				intent.setComponent(cn);
				startActivityForResult(intent, 1);
			}
		}

		private void sendMessage(String sendStr) {
			String date = getTime();
			list.add(new ListContentEntity(UserManager.globaluser.getUsername(),date,sendStr,ListContentEntity.MESSAGE_TO));
			listAdapter.notifyDataSetChanged();
			MediaType item = new MediaType(MediaType.SMS,sendStr,MediaType.to);
			item.setTime(date);item.setFriend(fUsername);
			if(UserManager.CurSessionFriList.containsKey(fUsername))
			{
				 UserManager.CurSessionFriList.get(fUsername).add(item);
				 if(DEBUG) Log.d(TAG,"adding to the list jyh");
			}
			else{
				List<MediaType> newmsgbox = new ArrayList<MediaType>();
				newmsgbox.add(item);
				UserManager.CurSessionFriList.put(fUsername, newmsgbox);
				if(DEBUG) Log.d(TAG,"adding to the list with new msg jyh");
			}
			ServiceManager.getmSms().sendSms(item);
			
		}
	};
	public void sendImageMsg(String path){
	    String date = getTime();
		list.add(new ListContentEntity(UserManager.getGlobaluser().getUsername(),date,"",ListContentEntity.MESSAGE_TO_PICTURE,path));
		listAdapter.notifyDataSetChanged();
		MediaType item = new MediaType(MediaType.IMAGE,MediaType.to);
		item.setMsgContant(path);
		item.setFriend(fUsername);
		if(UserManager.CurSessionFriList.containsKey(fUsername))
		{
			 UserManager.CurSessionFriList.get(fUsername).add(item);
			 if(DEBUG) Log.d(TAG,"adding to the list");
		}
		else{
			List<MediaType> newmsgbox = new ArrayList<MediaType>();
			newmsgbox.add(item);
			UserManager.CurSessionFriList.put(fUsername, newmsgbox);
			if(DEBUG) Log.d(TAG,"adding to the list with new msg");
		}
		ServiceManager.getmSms().SendImage(item);
	}
	
	
	/**
	 * append message to the list view and update it
	 * @param name
	 * @param msg
	 * @param stime
	 */
	public void appendToList(String name,String msg,String stime)
	{
	    ListContentEntity d4 = new ListContentEntity(name,stime,msg,ListContentEntity.MESSAGE_FROM);
		list.add(d4);
		if(DEBUG) Log.d(TAG,"apend to list! ");
	}
	
	public class ChatViewReceiver extends IMPSBroadcastReceiver{
		@Override
		public void onReceive(Context context,Intent intent){
			super.onReceive(context, intent);
			if(intent.getAction().equals(Constant.SMS)){
				String friend = intent.getStringExtra(Constant.USERNAME);
				if(DEBUG) Log.d(TAG,"Sms recv from:"+friend+":"+fUsername);
				if(friend.equals(fUsername)){
					String name = intent.getStringExtra(Constant.USERNAME);
					String stime = intent.getStringExtra(Constant.TIME);
					String msg = intent.getStringExtra(Constant.SMSCONTENT);
					ListContentEntity d4 = new ListContentEntity(name,stime,msg,ListContentEntity.MESSAGE_FROM);
					list.add(d4);
					listAdapter.notifyDataSetChanged();
				}

				// Store received message into local database
				// including displayed on the current chat session GUI
				// and those from other friends
				localDB.storeMsg(intent.getStringExtra(Constant.SMSCONTENT),
						intent.getStringExtra(Constant.TIME),
						intent.getStringExtra(Constant.USERNAME),
						1/* From friend */);

			}else if(intent.getAction().equals(Constant.SMSRSP)){
				
			}else if(intent.getAction().equals(Constant.IMAGE)){
				String friend = intent.getStringExtra(Constant.USERNAME); 
				if(DEBUG) Log.d(TAG,"Image recv from:"+friend+":"+fUsername);
				if(friend.equals(fUsername)){
					listAdapter.notifyDataSetChanged();
				}
			}else if(intent.getAction().equals(Constant.AUDIO)){
				String friend = intent.getStringExtra(Constant.USERNAME); 
				if(DEBUG) Log.d(TAG,"Audio recv from:"+friend+":"+fUsername);
				if(friend.equals(fUsername)){
					listAdapter.notifyDataSetChanged();
				}
			}
		}
		@Override
		public IntentFilter getFilter(){
			IntentFilter filter= super.getFilter();
			filter.addAction(Constant.SMS);
			filter.addAction(Constant.SMSRSP);
			filter.addAction(Constant.IMAGE);
			filter.addAction(Constant.AUDIO);
			return filter;
		}
	}


	
	private String getTime(){
		Date now = new Date();
		SimpleDateFormat d1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    String date = d1.format(now);
	    return date;
	}
}
