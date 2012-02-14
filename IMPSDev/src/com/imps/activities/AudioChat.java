package com.imps.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.imps.IMPSDev;
import com.imps.R;
import com.imps.events.IAudioEvent;
import com.imps.media.audioEngine.net.ImpsSocket;
import com.imps.media.audioEngine.net.RtpStreamReceiver;
import com.imps.media.audioEngine.net.RtpStreamSender;
import com.imps.services.impl.ServiceManager;

public class AudioChat extends Activity implements View.OnTouchListener,IAudioEvent{

	private static String TAG = AudioChat.class.getCanonicalName();
	private static boolean DEBUG = IMPSDev.isDEBUG();
	private final static int READY = 0;
	private final static int CANCEL = 1;
	private final static int START = 2;
	private final static int EXIT = 3;
	private final static int REJECT = 4;
	private final static int ALERT = 1000;
	private int status = READY;
	private ToggleButton pushToTalkButton;
	private String friname = new String();
	private String ip;
	private int port;
	private RtpStreamSender sender ;
	private RtpStreamReceiver receiver ;
	private ImpsSocket socket=null;
	public static Context mContext;
	private Thread audioRecord;
	private Thread audioPlayThread;
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			updateStatus(msg.what);
		}
	};
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.audiochat);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		pushToTalkButton = (ToggleButton) findViewById(R.id.pushToTalk);
		mContext = this;
		Intent fi = this.getIntent();
		friname = fi.getStringExtra("fUsername");
		ip = fi.getStringExtra("ip");
		if(ip==null)
			ip = "";
		else if(DEBUG) Log.d("AudioChat", "oncreate: IP is "+ip);
		port = fi.getIntExtra("port", 1300);
		updateStatus(status);
		if(friname!=null&&!"".equals(friname))
		{
			String title = this.getResources().getString(R.string.audio_chat_title, friname);
			setTitle(title);
		}
		if(!"".equals(ip))
		    showDialog(ALERT);
		ServiceManager.getmNetLogic().addAudioEventHandler(this);
		
	}
	@Override
	public void onResume()
	{
		super.onResume();
	}
	@Override
	public void onStop()
	{
		super.onStop();
	}
    /**
     * Updates the status box at the top of the UI with a messege of your choice.
     * @param status The String to display in the status box.
     */
    public void updateStatus(final String status) {
        // Be a good citizen.  Make sure UI changes fire on the UI thread.
        this.runOnUiThread(new Runnable() {
            public void run() {
                TextView labelView = (TextView) findViewById(R.id.sipLabel);
                labelView.setText(status);
            }
        });
    }
    public void updateStatus(int status)
    {
    	this.status = status;
    	switch(status){
    	case READY:
    		updateStatus(this.getResources().getString(R.string.audio_status_ready));
    		pushToTalkButton.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.btn_speak_normal));
    		break;
    	case CANCEL:
    		updateStatus(this.getResources().getString(R.string.audio_status_cancel));
    		pushToTalkButton.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.btn_speak_normal));
    		stopAudio();
    		break;
    	case START:
    		updateStatus(this.getResources().getString(R.string.audio_status_start));
    		pushToTalkButton.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.btn_speak_pressed));
    		startAudio();
    		break;
    	case EXIT:
    		updateStatus(this.getResources().getString(R.string.audio_status_exit));
    		pushToTalkButton.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.btn_speak_normal));
    		stopAudio();
    		finish();
    		break;
    	case REJECT:
    		updateStatus(this.getResources().getString(R.string.audio_status_reject));
    		pushToTalkButton.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.btn_speak_normal));
    		stopAudio();
    		break;
    	}
    		
    }
    public void startAudio()
    {
    	//int localport = 0;//ServiceManager.getmMedia().unlock();
		socket = ServiceManager.getmMedia().getSocket();		
		sender = new RtpStreamSender (socket,ip,port);
		receiver = new RtpStreamReceiver (socket);	
		if(DEBUG) Log.d(TAG,"ip:"+ip+" port:"+port);
		new Thread (receiver).start();
		new Thread (sender).start();
    }
    public void stopAudio(){
    	if(sender != null)
    	{
    		sender.stopPcmu();
    	}   	
    	if(receiver != null)
    	{
    		receiver.stopPcmu();
    	}
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
    	switch (keyCode){
		case KeyEvent.KEYCODE_BACK:
			new AlertDialog.Builder(this)
				.setMessage(getResources().getString(R.string.audio_exit_warning))
				.setPositiveButton(getResources().getString(R.string.ok), new OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						updateStatus(CANCEL);
						ServiceManager.getmSound().stopRingTone();
						finish();
					}					
				})
				.setNegativeButton(getResources().getString(R.string.cancel), null)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.show();
			return true;
		}
		return super.onKeyDown(keyCode, event);
    }

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	protected Dialog onCreateDialog(int id){
    	Dialog dialog = null;
    	Builder b = new AlertDialog.Builder(this);
    	switch (id){
    	case ALERT:
    		b.setTitle(getResources().getString(R.string.audio_chat_request_title));
    		b.setMessage(getResources().getString(R.string.audio_chat_request_message,friname));
    		b.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub	
					if(!ServiceManager.getmNet().isAvailable()){
						Toast.makeText(AudioChat.this,getResources().getString(R.string.net_problem), Toast.LENGTH_LONG);
						return;
					}
					String myip = ServiceManager.getmNet().getLocalIP(false);
					if(DEBUG) Log.d("AudioChat", "IP sent is "+ myip+" but IP received is "+ip);
					ServiceManager.getmAudio().SendPTPAudioRsp(friname,true);
					updateStatus(START);
					ServiceManager.getmSound().stopRingTone();
				}
			});
    		b.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					if(!ServiceManager.getmNet().isAvailable()){
						Toast.makeText(AudioChat.this,getResources().getString(R.string.net_problem), Toast.LENGTH_LONG);
						return;
					}
					String myip = ServiceManager.getmNet().getLocalIP(false);
					Log.d("AudioChat", "IP sent is "+ myip+ " but IP received is "+ip);
					//ServiceManager.getmMedia().SendPTPAudioRsp(friname,false,myip,1300);
					updateStatus(CANCEL);
					ServiceManager.getmSound().stopRingTone();
				}
			});
    		dialog = b.create();
    		break;
    	} 
    	return dialog;
	}
	@Override
	public void onP2PAudioReq(String friName, String ip, int port) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onP2PAudioRsp(String friName, boolean result, String ip,
			int port) {
		// TODO Auto-generated method stub
		if(friName.equals(friname)){
			Message msg = new Message();
			if(result){
				this.ip = ip;
				this.port = port;
				msg.what = START;
			}else{
				msg.what = REJECT;
			}
			handler.sendMessage(msg);
			ServiceManager.getmSound().stopRingTone();
		}
	}

	@Override
	public void onP2PAudioReqSuccess() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onP2PAudioError(String msg, int errorCode) {
		// TODO Auto-generated method stub
		
	}
	
}
