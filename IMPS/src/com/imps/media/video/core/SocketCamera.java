package com.imps.media.video.core;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.imps.activities.VideoContact;
import com.imps.media.video.codec.H263Config;
import com.imps.media.video.codec.NativeH263Decoder;
import com.imps.media.video.codec.NativeH264Decoder;

/**
 * A CameraSource implementation that obtains its bitmaps via a TCP connection
 * to a remote host on a specified address/port.
 * 
 * @author Tom Gibara
 *
 */

public class SocketCamera implements CameraSource {

	
	private static final int SOCKET_TIMEOUT = 1000;
	
	private final String address;
	private final int port;
	private final Rect bounds;
	private final boolean preserveAspectRatio;
	private final Paint paint = new Paint();

	private Socket socket = null;
	
	public SocketCamera(String address, int port, int width, int height, boolean preserveAspectRatio) {
		this.address = address;
		this.port = port;
		bounds = new Rect(0, 0, width, height);
		this.preserveAspectRatio = preserveAspectRatio;
		
		paint.setFilterBitmap(true);
		paint.setAntiAlias(true);
	}
	

	private int[] decodedFrame = new int[H263Config.VIDEO_WIDTH*H263Config.VIDEO_HEIGHT];
	
	public int getWidth() {
		return bounds.right;
	}
	
	public int getHeight() {
		return bounds.bottom;
	}
	
	public boolean open() {
		/* nothing to do */
		return true;
	}


	public boolean capture(Canvas canvas) {
		if (canvas == null) {
			Log.d("SocketCamera", "VideoReceiver: canvas is null...");
			return false;
		}
		
		try {
			Log.d("SocketCamera", "VideoReceiver:remote ip:"+address+" and port is "+port);
			socket = new Socket();
			socket.bind(null);
			socket.setSoTimeout(1000);
			socket.connect(new InetSocketAddress(address, port), SOCKET_TIMEOUT);
			Log.d("SocketCamera", "VideoReceiver:connected to host...and fetching data...");
			//obtain the bitmap
			InputStream in =socket.getInputStream();
	        
			int count=1450054;//40000;//31000;
			int index =0;
			byte[] buffer=new byte[count];
			byte[] buffer1=new byte[453600];

	        int readlen=in.read(buffer1);
	        System.arraycopy(buffer1, 0, buffer, index, readlen);
	        index+=readlen;
	        while(readlen>0){ 
	        	readlen=in.read(buffer1);
	        	if (readlen>0)
	        	{
		          System.arraycopy(buffer1, 0, buffer, index, readlen);
		          index+=readlen;
	        	}
		    }
			Log.d("SocketCamera", "VideoReceiver:datas have been received...and size of data is "+buffer.length);
			//Bitmap bitmap = BitmapFactory.decodeStream(in);
			if(VideoContact.mCamera==null)
			{
				Log.d("SocketCamera", "VideoReceiver: camera is null");
				return false;
			}			
			int previewWidth = VideoContact.mCamera.getParameters().getPreviewSize().width;  
	        int previewHeight = VideoContact.mCamera.getParameters().getPreviewSize().height;  
	      //  byte[] bitmapData = new byte[previewWidth * previewHeight];  
	        int[] rgbBuffer = new int[previewWidth * previewHeight * 3];
	        decodeYUV420SP(rgbBuffer, buffer, previewWidth, previewHeight); 
	        Bitmap myBitmap = Bitmap.createBitmap(rgbBuffer,previewWidth, previewHeight, Bitmap.Config.RGB_565) ;
	        Matrix matrix= new Matrix();
	        matrix.postRotate(90);
	        Bitmap bitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(),myBitmap.getHeight(),matrix, true);

	        // Bitmap mBitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(),myBitmap.getHeight(),matrix, true);
	       // ByteArrayOutputStream baos = new ByteArrayOutputStream();
	       // mBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
	       // byte[] result = baos.toByteArray();
	        
	        
			//Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0, count);
			Log.d("SocketCamera", "drawing the bitmap...");
			if(bitmap==null)
			{
				Log.d("SocketCamera", "bitmap built is null");
			}
			//render it to canvas, scaling if necessary
			if (
					bounds.right == bitmap.getWidth() &&
					bounds.bottom == bitmap.getHeight()) {
				canvas.drawBitmap(bitmap, 0, 0, null);
			} else {
				Rect dest;
				if (preserveAspectRatio) {
					dest = new Rect(bounds);
					dest.bottom = bitmap.getHeight() * bounds.right / bitmap.getWidth();
					dest.offset(0, (bounds.bottom - dest.bottom)/2);
				} else {
					dest = bounds;
				}
				canvas.drawBitmap(bitmap, null, dest, paint);
				Log.d("SocketCamera", "bitmap has been drawn");
				Log.d("SocketCamera", "bitmap:"+bitmap);
			}
			

		} catch (RuntimeException e) {
			Log.i(LOG_TAG, "Failed to obtain image over network", e);
			return false;
		} catch (IOException e) {
			Log.i(LOG_TAG, "Failed to obtain image over network", e);
			return false;
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
		return true;
	}
	

	public void close() {
		/* nothing to do */
	}

	public boolean saveImage(String savePath, String fileName) {

		//obtain the bitmap
		try {
			InputStream in = socket.getInputStream();
			Bitmap bitmap = BitmapFactory.decodeStream(in);
			FileOutputStream fos = new FileOutputStream(savePath + "/" + fileName);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
		} catch (IOException e) {
			e.printStackTrace();

			return false;
		}
		
		return true;
	}
	
	  static public void decodeYUV420SP(int[] rgbBuf, byte[] yuv420sp, int width, int height) {
	    	final int frameSize = width * height;
			if (rgbBuf == null)
				throw new NullPointerException("buffer 'rgbBuf' is null");
			if (rgbBuf.length < frameSize * 3)
				throw new IllegalArgumentException("buffer 'rgbBuf' size "
						+ rgbBuf.length + " < minimum " + frameSize * 3);

			if (yuv420sp == null)
				throw new NullPointerException("buffer 'yuv420sp' is null");

			if (yuv420sp.length < frameSize * 3 / 2)
				throw new IllegalArgumentException("buffer 'yuv420sp' size " + yuv420sp.length
						+ " < minimum " + frameSize * 3 / 2);
	    	
	    	int i = 0, y = 0;
	    	int uvp = 0, u = 0, v = 0;
	    	int y1192 = 0, r = 0, g = 0, b = 0;
	    	
	    	for (int j = 0, yp = 0; j < height; j++) {
	    		uvp = frameSize + (j >> 1) * width;
	    		u = 0;
	    		v = 0;
	    		for (i = 0; i < width; i++, yp++) {
	    			y = (0xff & ((int) yuv420sp[yp])) - 16;
	    			if (y < 0) y = 0;
	    			if ((i & 1) == 0) {
	    				v = (0xff & yuv420sp[uvp++]) - 128;
	    				u = (0xff & yuv420sp[uvp++]) - 128;
	    			}
	    			
	    			y1192 = 1192 * y;
	    			r = (y1192 + 1634 * v);
	    			g = (y1192 - 833 * v - 400 * u);
	    			b = (y1192 + 2066 * u);
	    			
	    			if (r < 0) r = 0; else if (r > 262143) r = 262143;
	    			if (g < 0) g = 0; else if (g > 262143) g = 262143;
	    			if (b < 0) b = 0; else if (b > 262143) b = 262143;
	    			
	    			rgbBuf[yp * 3] = (byte)(r >> 10);
	    			rgbBuf[yp * 3 + 1] = (byte)(g >> 10);
	    			rgbBuf[yp * 3 + 2] = (byte)(b >> 10);
	    			
	    			rgbBuf[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
	    		}
	    	}
	    }

}
