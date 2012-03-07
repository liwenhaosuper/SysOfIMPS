package com.imps.services.impl;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.imps.IMPSDev;
import com.imps.services.INetworkService;

public class NetworkService implements INetworkService{

	private static String TAG = NetworkService.class.getCanonicalName();
	private static boolean DEBUG = IMPSDev.isDEBUG();
	
	private WifiManager wifiManager;
	private WifiLock wifiLock;
	private boolean acquired;
	private boolean isStarted = false;
	
	@Override
	public boolean isStarted() {
		// TODO Auto-generated method stub
		return isStarted;
	}

	@Override
	public boolean start() {
		// TODO Auto-generated method stub
		this.wifiManager = (WifiManager) IMPSDev.getContext().getSystemService(Context.WIFI_SERVICE);
		isStarted = true;
		return true;
	}

	@Override
	public boolean stop() {
		// TODO Auto-generated method stub
		isStarted = false;
		this.release();
		return true;
	}

	@Override
	public String getLocalIP(boolean ipv6) {
		// TODO Auto-generated method stub
	    try {
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
	            NetworkInterface intf = en.nextElement();
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
	                InetAddress inetAddress = enumIpAddr.nextElement();
	               // if(DEBUG) Log.d(TAG, inetAddress.getHostAddress().toString());
	                if (!inetAddress.isLoopbackAddress()) {
	                	if(((inetAddress instanceof Inet4Address) && !ipv6) || ((inetAddress instanceof Inet6Address) && ipv6)){
	                		return inetAddress.getHostAddress().toString();
	                	}
	                }
	            }
	        }
	    } catch (SocketException ex) {
	        if(DEBUG) Log.e(TAG, ex.toString());
	    }	    
	    //
	    // Hack
	    //
	    try {    	
			java.net.Socket socket = new java.net.Socket(ipv6 ? "ipv6.google.com": "google.com", 80);
			//if(DEBUG) Log.d(NetworkService.TAG, socket.getLocalAddress().getHostAddress());
			return socket.getLocalAddress().getHostAddress();
		} catch (UnknownHostException e) {
			if(DEBUG)Log.e(NetworkService.TAG, e.toString());
		} catch (IOException e) {
			if(DEBUG)Log.e(NetworkService.TAG, e.toString());
		}
		
	    return null;
	}

	@Override
	public boolean acquire() {
		// TODO Auto-generated method stub
		if(this.acquired){
			return true;
		}
		
		boolean connected = false;
		
		 ConnectivityManager connectivityManager = (ConnectivityManager) IMPSDev.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		 NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		 
		 if(networkInfo == null){
			 Toast.makeText(IMPSDev.getContext(), "Failed to get Network information", Toast.LENGTH_LONG).show();
			 if(DEBUG)  Log.d(NetworkService.TAG, "Failed to get Network information");
			 return false;
		 }
		 
		 int netType = networkInfo.getType();
		 int netSubType = networkInfo.getSubtype();
		 
		 if(DEBUG) Log.d(NetworkService.TAG, String.format("netType=%d and netSubType=%d", netType, netSubType));
		 boolean useWifi = true;
		 boolean use3G = true;
		if(useWifi && (netType == ConnectivityManager.TYPE_WIFI)){
			if(this.wifiManager.isWifiEnabled()){
				this.wifiLock = this.wifiManager.createWifiLock(NetworkService.TAG);
				final WifiInfo wifiInfo = this.wifiManager.getConnectionInfo();
				if(wifiInfo != null && this.wifiLock != null){
					final DetailedState detailedState = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
					if(detailedState == DetailedState.CONNECTED 
							|| detailedState == DetailedState.CONNECTING || detailedState == DetailedState.OBTAINING_IPADDR){
						this.wifiLock.acquire();
						connected = true;
					}
				}
			}
			else{
				Toast.makeText(IMPSDev.getContext(), "WiFi not enabled", Toast.LENGTH_LONG).show();
				if(DEBUG) Log.d(NetworkService.TAG, "WiFi not enabled");
			}
		}
		else if(use3G && (netType == ConnectivityManager.TYPE_MOBILE )){
			if(		(netSubType >= TelephonyManager.NETWORK_TYPE_UMTS) || // HACK
				    (netSubType == TelephonyManager.NETWORK_TYPE_GPRS) ||
				    (netSubType == TelephonyManager.NETWORK_TYPE_EDGE)
				    ){
				Toast.makeText(IMPSDev.getContext(), "Using 2.5G (or later) network", Toast.LENGTH_SHORT).show();
				connected = true;
			}
		}

		if(!connected){
			Toast.makeText(IMPSDev.getContext(), "No active network", Toast.LENGTH_LONG).show();
			if(DEBUG) Log.d(NetworkService.TAG, "No active network");
			return false;
		}
		
		this.acquired = true;
		return true;
	}

	@Override
	public boolean release() {
		// TODO Auto-generated method stub
		if(this.wifiLock != null && this.wifiLock.isHeld()){
			this.wifiLock.release();
		}
		
		this.acquired = false;
		return true;
	}

	@Override
	public String getDnsServer(int type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAvailable() {
		// TODO Auto-generated method stub
		if(getLocalIP(false)==null||getLocalIP(false).equals("")){
			return false;
		}
		 ConnectivityManager connectivityManager = (ConnectivityManager) IMPSDev.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		 NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		 return (networkInfo != null && networkInfo.isConnected());
	}

}
