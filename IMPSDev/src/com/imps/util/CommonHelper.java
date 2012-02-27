package com.imps.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.imps.IMPSDev;

public class CommonHelper {
	private static boolean DEBUG = IMPSDev.isDEBUG();
	private static String TAG = CommonHelper.class.getCanonicalName();
	public static String getLocalIpAddress() { 
		try { 
		    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
		    { 
		        NetworkInterface intf = en.nextElement(); 
		        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) { 
		            InetAddress inetAddress = enumIpAddr.nextElement(); 
		            if (!inetAddress.isLoopbackAddress()) { 
		                 return inetAddress.getHostAddress().toString(); 
		                 } 
		         } 
		      } 
		 } catch (SocketException ex) { 
		    Log.e("get_IP", ex.toString()); 
		} 
		return null; 
		 
	}
	
	/**
	 * get the distance giving two geography points
	 * @param gp1
	 * @param gp2
	 * @return
	 */
	public static double getDistance(GeoPoint gp1,GeoPoint gp2){
	    double Lat1r = ConvertDegreeToRadians(gp1.getLatitudeE6()/1E6);
	    double Lat2r = ConvertDegreeToRadians(gp2.getLatitudeE6()/1E6);
	    double Long1r= ConvertDegreeToRadians(gp1.getLongitudeE6()/1E6);
	    double Long2r= ConvertDegreeToRadians(gp2.getLongitudeE6()/1E6);
	    double R = 6371;
	    double d = Math.acos(Math.sin(Lat1r)*Math.sin(Lat2r)+
	               Math.cos(Lat1r)*Math.cos(Lat2r)*
	               Math.cos(Long2r-Long1r))*R;
	    return d*1000;
	}
	
	private static double ConvertDegreeToRadians(double degrees){
	    return (Math.PI/180)*degrees;
	}
	
	
	public static byte[] bitmapToBytes(Bitmap map){
		//map.
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		map.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		return baos.toByteArray();
	}
	public static Bitmap bytesToBitmap(byte[] bytes){
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
	}
	public static String getTime(){
		Date now = new Date();
		SimpleDateFormat d1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    String date = d1.format(now);
	    return date;
	}
    public static String saveImage(Bitmap finalMap){
    	String res = null;
    	Long timestamp = System.currentTimeMillis();
    	
    	res ="/sdcard/imps/handwriting/recv/image/" + timestamp.toString()+".jpeg";
    	File destDir = new File("/sdcard/imps/handwriting/recv/image");
    	if(!destDir.exists())
    	{
    		destDir.mkdirs();
    	}
    	FileOutputStream fos;
    	try {
			fos = new FileOutputStream(res);
			if(finalMap!=null&&finalMap.compress(Bitmap.CompressFormat.JPEG, 100, fos)){
				//Toast.makeText(getApplicationContext(), "save successfully...", Toast.LENGTH_LONG);
				Log.d(TAG, "succ..."+res);
                fos.close();			
			}
			else{
				//Toast.makeText(getApplicationContext(), "save failed...", Toast.LENGTH_LONG);
				Log.d(TAG, "failed..."+res);
				 fos.close();
			}   			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	return res;
    }
    
    

}
