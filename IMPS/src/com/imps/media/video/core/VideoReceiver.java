package com.imps.media.video.core;

import com.imps.activities.VideoContact;
import com.imps.media.video.codec.H263Config;
import com.imps.media.video.codec.NativeH263Decoder;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toast;

public class VideoReceiver extends Thread{

	/** Handle to the surface manager object we interact with */
    private SurfaceHolder mSurfaceHolder;
    private Context context;
    /**
     * Current height of the surface/canvas.
     * 
     * @see #setSurfaceSize
     */
    private int mCanvasHeight = 1;
    /**
     * Current width of the surface/canvas.
     * 
     * @see #setSurfaceSize
     */
    private int mCanvasWidth = 1;
    
    /** Indicate whether the surface has been created & is ready to draw */
    private boolean mRun = true;
    private CameraSource cs;
  //  private String ip;
  //  private int port;
    private Canvas c = null;
    
    public VideoReceiver(Context context,SurfaceHolder surfaceHolder/*,String ip,int port*/)
    {
    	super();
    	this.context = context;
    	this.mSurfaceHolder = surfaceHolder	;
    	//this.ip = ip;
    	//this.port = port;
    }
    public void setRunning(boolean b) {
        mRun = b;
    }
	@Override
	public void run(){
		// while running do stuff in this loop...bzzz!
        while (true) {
            
            try {
            	if(mRun==false)
            		break;
            	Log.d("VideoReceiver", "VideoReceiver:run() method is called...");
                c = mSurfaceHolder.lockCanvas(null);
                // synchronized (mSurfaceHolder) {
//                captureImage(cmPara.getIp(), cmPara.getPort());
                captureImage(VideoContact.ip, VideoContact.port, mCanvasWidth, mCanvasHeight);
                // }
            } finally {
                // do this in a finally so that if an exception is thrown
                // during the above, we don't leave the Surface in an
                // inconsistent state
                if (c != null) {
                    mSurfaceHolder.unlockCanvasAndPost(c);
                    c = null;
                }
            }// end finally block
            
        }
	}
	
	public void setSurfaceSize(int width, int height) {
		// synchronized to make sure these all change atomically
        synchronized (mSurfaceHolder) {
            mCanvasWidth = width;
            mCanvasHeight = height;

            
        }
	}
	private boolean captureImage(String ip, int port, int width, int height){
		
		cs =new SocketCamera(ip, port, width, height, true);
        if (!cs.open()) { /* deal with failure to obtain camera */ 
        	Toast.makeText(context, "无法建立连接...", Toast.LENGTH_LONG);
        	return false;
        }
        cs.capture(c); //capture the frame onto the canvas
        
        return true;
	}
	
	public void closeCameraSource(){
		mRun = false;
		cs.close();
    }
	
	public CameraSource getCameraSource() {
		return cs;
	}
	
	
	
}
