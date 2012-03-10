package com.imps.activities;

import java.lang.reflect.Method;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.imps.IMPSActivity;
import com.imps.R;
import com.imps.basetypes.Constant;
import com.imps.media.rtp.core.NetworkFactory;
import com.imps.media.video.LiveVideoPlayer;
import com.imps.media.video.VideoRenderer;
import com.imps.media.video.VideoSurfaceView;
import com.imps.media.video.codec.H263Config;
import com.imps.receivers.IMPSBroadcastReceiver;
import com.imps.services.impl.ServiceManager;
import com.imps.util.CommonHelper;

public class VideoContact extends IMPSActivity implements SurfaceHolder.Callback{

	private final static byte READY = 0;
	private final static byte CANCEL = 1;
	private final static byte START = 2;
	private final static byte EXIT = 3;
	private final static byte REJECT = 4;
	private final static int ALERT = 1000;
	private byte status = READY;
	private static final String TAG = "VideoContact2";
	/**
	 * Video player
	 */
	private LiveVideoPlayer outgoingPlayer;
	/**
	 * Video preview
	 */
	private VideoSurfaceView outgoingVideoView = null;
	/**
	 * Video surface holder
	 */
	private SurfaceHolder mSurfaceHolder;
	/**
	 * Camera
	 */
	private Camera camera = null;
	/**
	 * Video renderer
	 */
	private VideoRenderer incomingRenderer = null;
	/**
	 * Opened camera id
	 */
	private int openedCameraId = 0;

	/**
	 * Video preview
	 */
	private VideoSurfaceView incomingVideoView = null;
	/**
	 * Surface holder for video preview
	 */
	private SurfaceHolder video_holder = null;
	/**
	 * Number of cameras
	 */
	private int cam_num = 1;
	/**
	 * Video format
	 */
    private String incomingVideoFormat;

    private String outgoingVideoFormat;

	/**
	 * Video width
	 */
	private int videoWidth;

	/**
	 * Video height
	 */
	private int videoHeight;
	private boolean cameraPreviewRunning = false;
	public static String ip = "127.0.0.1";
	public static int port = 1300;
	private String friName;
	private VideoContactReceiver receiver = new VideoContactReceiver();
	private boolean isDebug = false;
	private boolean isStarted = false;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// Set layout
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.video_chat);

		try {
			NetworkFactory.loadFactory("com.imps.media.rtp.core.AndroidNetworkFactory");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        incomingVideoFormat = H263Config.CODEC_NAME;
        outgoingVideoFormat = H263Config.CODEC_NAME;
        videoWidth = 176;
        videoHeight = 144;
        // Set the video preview
        if (outgoingVideoView == null) {
            outgoingVideoView = (VideoSurfaceView)findViewById(R.id.myVideoView);
        }
        outgoingPlayer = new LiveVideoPlayer(outgoingVideoFormat);
        outgoingVideoView.setAspectRatio(videoWidth, videoHeight);
        mSurfaceHolder = outgoingVideoView.getHolder();
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.addCallback(this);
        // Set incoming video preview
        
        incomingVideoView = (VideoSurfaceView)findViewById(R.id.videoView);
        incomingVideoView.setAspectRatio(videoWidth, videoHeight);
        incomingRenderer = new VideoRenderer(incomingVideoFormat);
        incomingRenderer.setVideoSurface(incomingVideoView);
        Intent fi = this.getIntent();
		if(fi!=null)
		{
			friName = fi.getStringExtra("fUsername");
			ip = fi.getStringExtra("ip");
			if(ip==null){
				ip = "127.0.0.1";
				incomingRenderer.notifyPlayerEventError(getResources().getString(R.string.unknow_request));
				return;
			}
			else Log.d("VideoContact2", "oncreate: IP is "+ip);
			port = fi.getIntExtra("port", 1300);
			updateStatus(status);
			if(friName!=null&&!"".equals(friName))
			{
				setTitle(getResources().getString(R.string.video_chat_title,friName));
			}
			if(!"127.0.0.1".equals(ip))
			    showDialog(ALERT);
		}
	}
	@Override
	public void onResume()
	{
		super.onResume();
		registerReceiver(receiver,receiver.getFilter());
	}
	@Override 
	public void onStop()
	{
		super.onStop();
		unregisterReceiver(receiver);
		updateStatus(CANCEL);
		stopVideo();
	}
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		 // Release the camera
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
        }
	}
	@Override
	public void onPause()
	{
		super.onPause();
	}
	 public void updateStatus(byte status)
	    {
	    	this.status = status;
	    	switch(status){
	    	case READY:
	    		updateStatus(getResources().getString(R.string.audio_status_ready)); 
	    		break;
	    	case CANCEL:
	    		updateStatus(getResources().getString(R.string.audio_status_cancel));
	    		stopVideo();
	    		break;
	    	case START:
	    		updateStatus(getResources().getString(R.string.audio_status_start));
	    		startVideo();
	    		break;
	    	case EXIT:
	    		updateStatus(getResources().getString(R.string.audio_status_exit));
	    		stopVideo();
	    		finish();
	    		break;
	    	case REJECT:
	    		updateStatus(getResources().getString(R.string.video_request_reject));
	    		stopVideo();
	    		break;
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
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		  if (camera == null) {
	            // Start camera preview
	            if (cam_num > 1) {
	                Method method = getCameraOpenMethod();
	                if (method != null) {
	                    try {
	                        camera = (Camera)method.invoke(camera, new Object[] {
	                            1
	                        });
	                        openedCameraId = 1;
	                    } catch (Exception e) {
	                        camera = Camera.open();
	                        openedCameraId = 0;
	                    }
	                } else {
	                    camera = Camera.open();
	                    openedCameraId = 0;
	                }
	            } else {
	                camera = Camera.open();
	                openedCameraId = 0;
	            }
	            camera.setPreviewCallback(outgoingPlayer);
	        }
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		video_holder = holder;
        if (camera != null) {
            if (cameraPreviewRunning ) {
                cameraPreviewRunning = false;
                camera.stopPreview();
            }
        }
        Log.d("VideoContact2", "width is:"+width+" and height is:"+height);
        startCameraPreview(width,height);
        
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		 // Release the camera
        /*if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
        }*/
	}

	  /**
     * Start the camera preview
     */
    private void startCameraPreview(int width,int height) {
        if (camera != null) {
        	camera.setDisplayOrientation(90);
            Camera.Parameters p = camera.getParameters();
            // Init Camera
            //p.setPreviewSize(videoWidth, videoWidth);
            p.setPreviewSize(176, 144);
          //  p.setPreviewFormat(PixelFormat.YCbCr_420_SP);
            p.set("ORIENTATION", "PORTRAIT");
            p.setSceneMode("portrait");
             p.setPreviewFormat(ImageFormat.NV21);
            // Try to set front camera if back camera doesn't support size
            List<Camera.Size> sizes = p.getSupportedPreviewSizes();
            for(int i=0;i<sizes.size();i++)
            	System.out.println("width:"+sizes.get(i).width+" height:"+sizes.get(i).height);
            if (!sizes.contains(camera.new Size(videoWidth, videoHeight))) {
                String cam_id = p.get("camera-id");
                if (cam_id != null) {
                    p.set("camera-id", 2);
                    p.setRotation(270);
                    p.setPreviewSize(videoHeight, videoWidth);
                }
            }
            camera.setParameters(p);
            try {
                camera.setPreviewDisplay(video_holder);
                camera.startPreview();
                cameraPreviewRunning = true;
            } catch (Exception e) {
                camera = null;
            }
        }
    }
    /**
     * Get Camera "open" Method
     *
     * @return Method
     */
    private Method getCameraOpenMethod() {
        ClassLoader classLoader = VideoContact.class.getClassLoader();
        Class cameraClass = null;
        try {
            cameraClass = classLoader.loadClass("android.hardware.Camera");
            try {
                return cameraClass.getMethod("open", new Class[] {
                    int.class
                });
            } catch (NoSuchMethodException e) {
                return null;
            }
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			Log.e("", "Stop Recorder");
			//stopVideo();
		}
		return super.onKeyDown(keyCode, event);
	}
    
    public void startVideo(){
    	/*if(isRunning)
    		return;
    	isRunning = true;
    	receiver = new VideoReceiver(this,mSurfaceHolder,ip,2011);
    	receiver.start();*/
    	if(isStarted)
    		return;
    	Log.d("VideoContact2", "startVideo is called");
    	if(isDebug)
    	{
    		incomingRenderer.open("127.0.0.1", 1300);   	
        	incomingRenderer.start();
        	outgoingPlayer.open("127.0.0.1", port);//sender
        	outgoingPlayer.start();
    	}
    	else{
    		incomingRenderer.open(ip, port);   	
        	incomingRenderer.start();
        	outgoingPlayer.open(ip, port);//sender
        	outgoingPlayer.start();
    	}
    	isStarted = true;
    }
    public void stopVideo(){
/*    	isRunning = false;
    	Log.d("VideoContact", "stopVideo is called");
    	sender.stopThread();
    	receiver.closeCameraSource();*/
    	if(!isStarted)
    		return;
    	incomingRenderer.stop();
    	incomingRenderer.close();
    	outgoingPlayer.close();   	
    	outgoingPlayer.stop();
    	isStarted = false;
    }
    
    protected Dialog onCreateDialog(int id){
    	Dialog dialog = null;
    	Builder b = new AlertDialog.Builder(this);
    	switch (id){
    	case ALERT:
    		b.setTitle(getResources().getString(R.string.video_chat_request_title));
    		b.setMessage(getResources().getString(R.string.video_chat_request_message,friName));
    		b.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub		
					String myip = CommonHelper.getLocalIpAddress();
					int cnt = 0;
					while("".equals(myip)||myip==null)
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
					Log.d("VideoChat", "IP sent is "+ myip+" but IP received is "+ip);
					ServiceManager.getmVideo().SendPTPVideoRsp(friName,myip,1300,true);
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
					Log.d("VideoContact", "IP sent is "+ myip+ " but IP received is "+ip);
					ServiceManager.getmVideo().SendPTPVideoRsp(friName,myip,1300,false);
					updateStatus(CANCEL);
					finish();
				}
			});
    		dialog = b.create();
    		break;
    	} 
    	return dialog;
	}

    public class VideoContactReceiver extends IMPSBroadcastReceiver{
    	@Override
    	public void onReceive(Context context,Intent intent){
    		super.onReceive(context, intent);
    		if(intent.getAction().equals(Constant.P2PVIDEOREQ)){
    			
    		}else if(intent.getAction().equals(Constant.P2PVIDEORSP)){

				String fip = intent.getStringExtra(Constant.IP);
				String fname = intent.getStringExtra(Constant.USERNAME);
				int fport = intent.getIntExtra(Constant.PORT, 0);
				boolean result = intent.getBooleanExtra(Constant.RESULT, true);
				if(fname.equals(friName)){
					if(result){
						ip = fip;
						port = fport;
						updateStatus(getResources().getString(R.string.audio_status_start));
						startVideo();
					}else{
						updateStatus(getResources().getString(R.string.video_request_reject));
						stopVideo();
					}
					ServiceManager.getmSound().stopRingTone();
				}
			
    		}
    	}
    	@Override
    	public IntentFilter getFilter(){
    		IntentFilter filter = super.getFilter();
    		filter.addAction(Constant.P2PVIDEOREQ);
    		filter.addAction(Constant.P2PVIDEORSP);
    		return filter;
    	}
    }
}
