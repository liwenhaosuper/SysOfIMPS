package com.imps.activities;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.imps.R;
import com.imps.handler.UserManager;
import com.imps.media.video.core.VideoInterface;
import com.imps.media.video.core.VideoReceiver;
import com.imps.media.video.core.VideoSender;
import com.imps.media.video.core.SocketImpl.VideoParameter;
import com.imps.util.CommonHelper;


public class VideoContact extends Activity implements Callback {

	private final static byte READY = 0;
	private final static byte CANCEL = 1;
	private final static byte START = 2;
	private final static byte EXIT = 3;
	private final static byte REJECT = 4;
	private final static int ALERT = 1000;
	private byte status = READY;
	private static final String TAG = "VideoContact";
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private SurfaceView mySurfaceView;
	private SurfaceHolder mySurfaceHolder;
	public static Camera mCamera;
	private Bitmap bitmap;
	private VideoInterface socketVideo;
	private VideoParameter videoParameter;
	public static String ip = "127.0.0.1";
	public static int port = 2011;
	private String friName;
	private boolean isRunning = false;
	private VideoReceiver receiver; 
	private VideoSender sender;
	private boolean isDebug = true;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState); 
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.video_chat);
		mSurfaceView = (SurfaceView) findViewById(R.id.videoView); 
		mSurfaceHolder = mSurfaceView.getHolder(); 
		mySurfaceView = (SurfaceView) findViewById(R.id.myVideoView);
		mySurfaceHolder = mySurfaceView.getHolder();
		mySurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); 
		mSurfaceHolder.addCallback(this);
		mySurfaceHolder.addCallback(this);
		
		DisplayMetrics dm=new DisplayMetrics();  
        getWindowManager().getDefaultDisplay().getMetrics(dm); 
        int height = (int)(dm.heightPixels * 0.6);
        mSurfaceView.setLayoutParams(new LinearLayout.LayoutParams(
        		LinearLayout.LayoutParams.FILL_PARENT, height));
		
		Intent fi = this.getIntent();
		if(fi!=null)
		{
			friName = fi.getStringExtra("fUsername");
			ip = fi.getStringExtra("ip");
			if(ip==null)
				ip = "127.0.0.1";
			else Log.d("VideoContact", "oncreate: IP is "+ip);
			port = fi.getIntExtra("port", 2011);
			updateStatus(status);
			if(friName!=null&&!"".equals(friName))
			{
				setTitle("与"+friName+"视频通话");
			}
			if(!"127.0.0.1".equals(ip))
			    showDialog(ALERT);
		}		
		if("127.0.0.1".equals(ip))
		{
			sender = new VideoSender(2011);
			//port = 2012;
			port = 2011;
		}
		else{
			//sender = new VideoSender(2012);
			sender = new VideoSender(2011);
			port = 2011;
		}
		sender.start();
		if(isDebug)
		{
			updateStatus(START);
		}
		
	}
	@Override
	public void onResume()
	{
		super.onResume();
		IntentFilter ifilter = new IntentFilter();
		ifilter.addAction("exit");
		ifilter.addAction("ptpvideo_rsp");
		registerReceiver(exitReceiver,ifilter);
	}
	@Override
	public void onStop()
	{
		super.onStop();
		unregisterReceiver(exitReceiver);
		updateStatus(CANCEL);
		stopVideo();
	}
	public class ExitReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if("exit".equals(intent.getAction()))
			{
				stopVideo();
				finish();
			}
			else if("ptpvideo_rsp".equals(intent.getAction()))
			{
				Log.d("VideoChat", "video rsp broadcast received...");
				Bundle bundle = intent.getExtras();
				String fri = bundle.getString("fUsername");
				if(fri!=null&&!"".equals(fri))
				{
					setTitle("与"+friName+"视频通话");
				}
				int res = bundle.getInt("result");
				//res = 3;
				if(res==1)
				{
					ip = bundle.getString("ip");
					Log.d("VideoContact", "rsp: IP received is "+ip);
					if(ip==null||"".equals(ip))
					{
						ip = "127.0.0.1";
					}
					//port = bundle.getInt("port", 2011);
					//port = 2012;
					updateStatus(START);
				}
				else {
					updateStatus(REJECT);
				}
			}
		}		
	}
	private ExitReceiver exitReceiver = new ExitReceiver();
	
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int format, int width, int height) {
		// TODO Auto-generated method stub		
		if(arg0.equals(mSurfaceHolder))
		{
			if(isRunning)
			{
				receiver.setSurfaceSize(width, height);
			}
			Log.d("VideoContact", "mSurface changed...");
			return;
		}
		mCamera.setDisplayOrientation(90);
		Camera.Parameters parameters = mCamera.getParameters();
		parameters.set("ORIENTATION", "PORTRAIT");
		parameters.setSceneMode("portrait");
		List<Camera.Size> reviewSizes = parameters.getSupportedPreviewSizes();
		for(int i=0;i<reviewSizes.size();i++)
		{
			Log.d("VideoContact", ""+reviewSizes.get(i).width+":"+reviewSizes.get(i).height);
		}
		//parameters.setPreviewSize(width,height);
		mCamera.setParameters(parameters);
		mCamera.setPreviewCallback(new VideoData(VideoContact.this));
		try {
			mCamera.setPreviewDisplay(mySurfaceView.getHolder());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			mCamera.release();
			mCamera = null;
			e.printStackTrace();
		}
		mCamera.startPreview();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			Log.e("", "Stop Recorder");
			stopVideo();
		}
		return super.onKeyDown(keyCode, event);
	}
    

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		if(arg0.equals(mySurfaceHolder))
		{
			mCamera = Camera.open();
			if(mCamera!=null)
			    Log.d("log", "camera is not null");
		}
		else{
			updateStatus(START);
		}
		
		
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		if(arg0.equals(mySurfaceHolder))
		{
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
		else{
			if(isRunning)
			  receiver.closeCameraSource();
		}

	}
	
	class VideoData implements Camera.PreviewCallback 
	{
        private Context context;
        public VideoData(Context context){
        	this.context = context;
        }
		@Override
		public void onPreviewFrame(byte[] data, Camera arg1) {
			// TODO Auto-generated method stub
			Log.d("VideoContact", "onPreviewFrame is called...");
			if(data!=null&&isRunning==true)
			{
			   //send data to the other side
				sender.update(data);
			}
		}
		
	}
    public void updateStatus(final String status) {
        // Be a good citizen.  Make sure UI changes fire on the UI thread.
        this.runOnUiThread(new Runnable() {
            public void run() {
                TextView labelView = (TextView) findViewById(R.id.videoStatus);
                labelView.setText(status);
            }
        });
    }
    public void startVideo(){
    	if(isRunning)
    		return;
    	isRunning = true;
    	receiver = new VideoReceiver(this,mSurfaceHolder/*,ip,2011*/);
    	receiver.start();
    	Log.d("VideoContact", "startVideo is called");
    }
    public void stopVideo(){
    	isRunning = false;
    	Log.d("VideoContact", "stopVideo is called");
    	sender.stopThread();
    	receiver.closeCameraSource();
    }
    
    
    public void updateStatus(byte status)
    {
    	this.status = status;
    	switch(status){
    	case READY:
    		updateStatus("准备中..."); 
    		break;
    	case CANCEL:
    		updateStatus("通话已取消...");
    		stopVideo();
    		break;
    	case START:
    		updateStatus("正在通话中...");
    		startVideo();
    		break;
    	case EXIT:
    		updateStatus("正在关闭...");
    		stopVideo();
    		finish();
    		break;
    	case REJECT:
    		updateStatus("好友已拒绝您的视频通话请求");
    		stopVideo();
    		break;
    	}
    		
    }
	
	protected Dialog onCreateDialog(int id){
    	Dialog dialog = null;
    	Builder b = new AlertDialog.Builder(this);
    	switch (id){
    	case ALERT:
    		b.setTitle("视频通讯请求");
    		b.setMessage("是否接受"+friName+"的视频通话请求？");
    		b.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub		
					String myip = CommonHelper.getLocalIpAddress();
					int cnt = 0;
					while("".equals(myip)||myip==null)
					{
						Toast.makeText(AudioChat.mContext, " 当前网络不可用，正在重试...", Toast.LENGTH_LONG);
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
					Log.d("VideoChat", "IP sent is "+ myip+" but IP received is "+ip);
					UserManager.getInstance().SendPTPVideoRsp(friName,myip,1300, true);
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
						Toast.makeText(AudioChat.mContext, " 当前网络不可用，正在重试...", Toast.LENGTH_LONG);
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
					Log.d("VideoContact", "IP sent is "+ myip+ " but IP received is "+ip);
					UserManager.getInstance().SendPTPVideoRsp(friName,myip,1300, false);
					updateStatus(CANCEL);
					finish();
				}
			});
    		dialog = b.create();
    		break;
    	} 
    	return dialog;
	}
	

}
