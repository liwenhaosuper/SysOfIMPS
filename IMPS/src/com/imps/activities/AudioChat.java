package com.imps.activities;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.imps.R;
import com.imps.audioEngine.codecs.ulaw;
import com.imps.audioEngine.net.ImpsSocket;
import com.imps.audioEngine.net.RtpStreamReceiver;
import com.imps.audioEngine.net.RtpStreamSender;
import com.imps.handler.UserManager;
import com.imps.util.CommonHelper;

public class AudioChat extends Activity implements View.OnTouchListener{

	private final static byte READY = 0;
	private final static byte CANCEL = 1;
	private final static byte START = 2;
	private final static byte EXIT = 3;
	private final static byte REJECT = 4;
	private final static int ALERT = 1000;
	private byte status = READY;
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
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.walkietalkie);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		pushToTalkButton = (ToggleButton) findViewById(R.id.pushToTalk);
		mContext = this;
		Intent fi = this.getIntent();
		friname = fi.getStringExtra("fUsername");
		ip = fi.getStringExtra("ip");
		if(ip==null)
			ip = "";
		else Log.d("AudioChat", "oncreate: IP is "+ip);
		port = fi.getIntExtra("port", 1300);
		updateStatus(status);
		if(friname!=null&&!"".equals(friname))
		{
			String title = this.getResources().getString(R.string.audio_chat_title, friname);
			setTitle(title);
		}
		if(!"".equals(ip))
		    showDialog(ALERT);
		
	}
	@Override
	public void onResume()
	{
		super.onResume();
		IntentFilter ifilter = new IntentFilter();
		ifilter.addAction("exit");
		ifilter.addAction("ptpaudio_rsp");
		registerReceiver(exitReceiver,ifilter);
	}
	@Override
	public void onStop()
	{
		super.onStop();
		unregisterReceiver(exitReceiver);
		updateStatus(CANCEL);
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
    public void updateStatus(byte status)
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
    	try {
			socket = new ImpsSocket (2110);			
			sender = new RtpStreamSender (socket,ip,2110);
			receiver = new RtpStreamReceiver (socket);			
			new Thread (sender).start();
			new Thread (receiver).start();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
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
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public class ExitReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if("exit".equals(intent.getAction()))
			{
				stopAudio();
				finish();
			}
			else if("ptpaudio_rsp".equals(intent.getAction()))
			{
				Log.d("AudioChat", "audio rsp broadcast received...");
				Bundle bundle = intent.getExtras();
				String fri = bundle.getString("fUsername");
				if(fri!=null&&!"".equals(fri))
				{
					String title = getResources().getString(R.string.audio_chat_title, friname);
					setTitle(title);
				}
				int res = bundle.getInt("result");
				//res = 3;
				if(res==1)
				{
					ip = bundle.getString("ip");
					Log.d("AudioChat", "rsp: IP received is "+ip);
					port = bundle.getInt("port", 1300);
					updateStatus(START);
				}
				else {
					updateStatus(REJECT);
				}
			}
		}		
	}
	private ExitReceiver exitReceiver = new ExitReceiver();
	
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
					String myip = CommonHelper.getLocalIpAddress();
					int cnt = 0;
					while("".equals(myip))
					{
						Toast.makeText(AudioChat.mContext, getResources().getString(R.string.net_problem_and_retrying), Toast.LENGTH_LONG);
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						cnt++;
						myip = CommonHelper.getLocalIpAddress();
						if(cnt>10){
							return;
						}
					}
					Log.d("AudioChat", "IP sent is "+ myip+" but IP received is "+ip);
					UserManager.getInstance().SendPTPAudioRsp(friname,myip,1300, true);
					updateStatus(START);
				}
			});
    		b.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					String myip = CommonHelper.getLocalIpAddress();
					int cnt = 0;
					while("".equals(myip))
					{
						Toast.makeText(AudioChat.mContext,getResources().getString(R.string.net_problem_and_retrying), Toast.LENGTH_LONG);
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						cnt++;
						myip = CommonHelper.getLocalIpAddress();
						if(cnt>10){
							return;
						}
					}
					Log.d("AudioChat", "IP sent is "+ myip+ " but IP received is "+ip);
					UserManager.getInstance().SendPTPAudioRsp(friname,myip,1300, false);
					updateStatus(CANCEL);
				}
			});
    		dialog = b.create();
    		break;
    	} 
    	return dialog;
	}
	
}
