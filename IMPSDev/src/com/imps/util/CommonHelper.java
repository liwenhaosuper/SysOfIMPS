package com.imps.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.util.Log;

import com.google.android.maps.GeoPoint;

public class CommonHelper {
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

}
