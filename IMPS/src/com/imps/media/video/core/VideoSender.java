package com.imps.media.video.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

import com.imps.activities.VideoContact;
import com.imps.media.video.codec.H263Config;
import com.imps.media.video.codec.H264Config;
import com.imps.media.video.codec.NativeH263Encoder;
import com.imps.media.video.codec.NativeH263EncoderParams;
import com.imps.media.video.codec.NativeH264Encoder;


public class VideoSender extends Thread {
	
	public int port;
	public List<Socket> clients = new ArrayList<Socket>();
	private ServerSocket serverSocket ;
	public VideoSender(int port)
	{
		super();
		this.port = port;
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void run(){
		while(true)
		{
			try {
				Log.d("VideoSender", "waiting for socket's connecting...");
				if(serverSocket.isClosed())
					return;
				Socket socket = serverSocket.accept();
				Log.d("VideoSender", "socket client added...");
				clients.add(socket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	public void stopThread()
	{
		try {
			for(int i=0;i<clients.size();i++)
			{
				if(clients.get(i).isClosed()==false)
				    clients.get(i).close();
			}
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void update(byte[] datas)
	{
		Log.d("VideoSender", "Updating data...size of client is "+clients.size()+" size of datas is "+datas.length);
		if(VideoContact.mCamera==null)
			return;
 
		/*int previewWidth = VideoContact.mCamera.getParameters().getPreviewSize().width;  
        int previewHeight = VideoContact.mCamera.getParameters().getPreviewSize().height;  
      //  byte[] bitmapData = new byte[previewWidth * previewHeight];  
        int[] rgbBuffer = new int[previewWidth * previewHeight * 3];
        decodeYUV420SP(rgbBuffer, datas, previewWidth, previewHeight); 
        Bitmap myBitmap = Bitmap.createBitmap(rgbBuffer,previewWidth, previewHeight, Bitmap.Config.RGB_565) ;
        Matrix matrix= new Matrix();
        matrix.postRotate(90);
        Bitmap mBitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(),myBitmap.getHeight(),matrix, true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] result = baos.toByteArray();*/
		for(int i=0;i<clients.size();i++)
		{
			Socket socket = clients.get(i);
			if(socket.isClosed())
			{
				clients.remove(i);
				Log.d("VideoSender", "socket client removed...");
				continue;
			}
			if(true)
			{
				try {
					OutputStream out = socket.getOutputStream();					
					//out.write(result);
					out.write(datas);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.d("VideoSender", "datas sent to client failed...");
					e.printStackTrace();
				} finally{
					try {
						clients.remove(i);
						Log.d("VideoSender", "socket client forced removed...");
						socket.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
			
		}
	}

  /*  static public void decodeYUV420SP(int[] rgbBuf, byte[] yuv420sp, int width, int height) {
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
    }*/

}
