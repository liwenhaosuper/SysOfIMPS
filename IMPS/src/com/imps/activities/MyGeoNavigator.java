package com.imps.activities;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;

/**
 * 定位类
 * @author Administrator
 *
 */
public class MyGeoNavigator {

	/**
	 * private members
	 */
	//定位方法
	private final static String Satellite = "卫星";
	private final static String BaseLocation = "基站";
	private final static String NullDevice = "暂无定位信息";
	private enum LocOfType{  //定位类型
		GPS_LOCATION,  //gps定位
		BASE_TOWER,    //基站定位
		WIFI_LOCATION  //wifi定位
	}
	private static MyGeoNavigator navigator = null;//实例 
	private static Context myContext;     
	private LocationManager mLocationManager;	
	private Location mLocation;
	private String strLocationProvider;
	//private LocationListener mLocationListener;
	private static LocationCallBack mCallback;  //map activity回调变量
	private GeoPoint currentGeoPoint;   //用户当前位置
	private TelephonyManager tm;
	private static LocOfType MyLocType;      //当前定位方法
	private static Handler mHandler;         
	//卫星定位相关
	private List<GpsSatellite> numSatelliteList = new ArrayList<GpsSatellite>(); // 卫星信号
	private static final int MINTIME = 2000;   //最小定位更新时间间隔
	private static final int MININSTANCE = 2;  //最小定位更新位置距离
	//private GpsStatus.Listener statusListener; //卫星状态监听器
	private int satelliteCount = 0;//卫星数量
	public final static int LOCATION_BASE_ID = 100;
	private Timer timer;
	
	//获取定位实例
	public static MyGeoNavigator getInstance()
	{
		if(navigator==null)
			navigator = new MyGeoNavigator();
		return navigator;
	}
	
	//初始化卫星状态监听器
	private GpsStatus.Listener statusListener = new GpsStatus.Listener() {		
		@Override
		public void onGpsStatusChanged(int event) {
			// TODO Auto-generated method stub
			GpsStatus status = mLocationManager.getGpsStatus(null);
			switch(event)
			{
			case GpsStatus.GPS_EVENT_STOPPED:
				numSatelliteList.clear();
				satelliteCount = 0;
				break;
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				break;
			case GpsStatus.GPS_EVENT_STARTED:
				break;
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				int maxSatellites = status.getMaxSatellites();
				Iterator<GpsSatellite> it = status.getSatellites().iterator();
				numSatelliteList.clear();
				int count = 0;
				while (it.hasNext() && count <= maxSatellites) {
					GpsSatellite s = it.next();
					numSatelliteList.add(s);
					count++;
				}
				satelliteCount = numSatelliteList.size();
				break;
			}
		}
	};
   //初始化位置监听器
	private LocationListener mLocationListener = new LocationListener(){

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			Log.d("MyGeoNavigator", "location update!");
			if(location==null)
			{
				Log.d("MyGeoNavigator", "onlocationchanged:location is null");
			}
			updateToNewLocation(location);
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			Log.d("MyGeoNavigator", "location provider disable!");
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			Log.d("MyGeoNavigator", "location provider enable!");
			
		}

		@Override
		public void onStatusChanged(String provider, int status,
				Bundle extras) {
			// TODO Auto-generated method stub
			Log.d("MyGeoNavigator", "location status change!");
		}
	};
	
	//初始化成员
	public void initMethod()
	{
	    timer = new Timer();
	    timer.schedule(new TimerTask(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Location mylocation = getLocationProvider(mLocationManager);
				if(mylocation==null)
				{
					Log.d("MyGeoNavigator", "net work location");
					mCallback.onCurrentLocation(getNetWorkLocation());
					return;
				}
				if(isBetterLocation(mylocation,mLocation))
				{
					Log.d("MyGeoNavigator", "gps location");
					mLocation = mylocation;
					mCallback.onCurrentLocation(getGeoByLocation(mLocation));
					return;
				}
				else{
					Log.d("MyGeoNavigator", "satelliteCount is " + satelliteCount);
					Log.d("MyGeoNavigator", "net work location");
					mCallback.onCurrentLocation(getNetWorkLocation());
					return;
				}
			}    	
	    }, 3000,10000);
	    
	}
	//初始化地图.该方法必须在生成实例前调用
	public static void init(Context context,LocationCallBack locationcallback,Handler handler)
	{
		myContext = context;
		mCallback = locationcallback;
		mHandler = handler;
	}
	//构造函数
	public MyGeoNavigator()
	{
		tm = (TelephonyManager) myContext.getSystemService(Context.TELEPHONY_SERVICE);
		mLocationManager = (LocationManager)myContext.getSystemService(Context.LOCATION_SERVICE);
		OpenGPSDevice(); //打开GPS
		mLocationManager.addGpsStatusListener(statusListener); //添加监听
		mLocation = getLocationProvider(mLocationManager);
		mLocationManager.requestLocationUpdates(strLocationProvider, 0, 0, mLocationListener);
		initMethod();
		Log.d("MyGeoNavigator", strLocationProvider);
		
	}
	
	/**
	 * 定位方式设置
	 * 
	 */
	
	//设置当前最适宜的定位方式,返回当前定位方式
	public LocOfType SetBestLocator()
	{
		return null;
	}
	//获取当前最适宜的定位方式
	public LocOfType GetBestLocator()
	{
		return null;
	}
	//设置当前定位方式为GPS定位，成功则返回true
	public boolean SetGPSLocator()
	{
		return false;
	}
	//设置当前定位方式为wifi定位，成功则返回true
	public boolean SetWifiLocator()
	{
		return false;
	}
	//设置当前定位方式为基站定位，成功则返回true
	public boolean SetBaseTowerLocator()
	{
		return false;
	}
	
	//更新地图到新的位置
	private void updateToNewLocation(Location location)
	{
		Log.d("MyNavigator", "updating location");
		if(location==null)
			return;
		Location lastLocation = mLocation;
		if(location!=null&&isBetterLocation(location,lastLocation))
		{
			currentGeoPoint = getGeoByLocation(location);
		}
		if(currentGeoPoint==null)
			currentGeoPoint = getGeoByLocation(location);
		if(currentGeoPoint==null)
			currentGeoPoint = getGeoByLocation(mLocation);
		mLocation = location;
		mCallback.onCurrentLocation(currentGeoPoint);
	}
    //开启GPS
	public boolean OpenGPSDevice()
	{
		if (mLocationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER ) && mLocationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)) {
			return true;
		}
		Toast.makeText(myContext, "请开启GPS与无线网络定位", Toast.LENGTH_LONG).show();
		return false;
	}
	//获取最精确的位置
	public GeoPoint getBestLocation()
	{	
		GeoPoint tpoint= getGeoByLocation(mLocation);
		if(tpoint!=null&&tpoint.getLatitudeE6()>0&&tpoint.getLongitudeE6()>0)
		{
			return tpoint;
		}
		if(satelliteCount<1)//卫星数量小于4颗,启用基站定位
		{
		    new Thread(new Runnable(){
				@Override
				public void run() {
					// TODO Auto-generated method stub	
					Log.d("MyGeoNavigator", "基站定位...");
					Message msg =  new Message();	
					msg.what = LOCATION_BASE_ID;
					msg.obj = getNetWorkLocation();
					mHandler.sendMessage(msg);
				}	    	
		    }).start();
		    Log.d("MyGeoNavigator", "networklocation");
		    return getNetWorkLocation();
		}
		else{
			Log.d("MyGeoNavigator", "gps location");
			return getGeoByLocation(mLocation);
		}
	}
	//GPS定位
	private Location getLocationProvider(LocationManager mLocationManager) {
		// TODO Auto-generated method stub
		Location location = null;
		try {
			Criteria mcriteria = new Criteria();
			mcriteria.setAccuracy(Criteria.ACCURACY_FINE);//定位精度
			mcriteria.setAltitudeRequired(false);//是否有海拔需要
			mcriteria.setBearingRequired(false); //是否有方向感的需要
			mcriteria.setCostAllowed(true); //允许定位消耗需要钱的流量
			mcriteria.setPowerRequirement(Criteria.POWER_MEDIUM);
			strLocationProvider = mLocationManager.getBestProvider(mcriteria,
					true);  //返回适合这种定位准则的最佳LocationManager
			location = mLocationManager.getLastKnownLocation(strLocationProvider);					
/*			if(getGeoByLocation(location)==null)
			{
				mCallback.onCurrentLocation(getNetWorkLocation());
			}
			else
			    mCallback.onCurrentLocation(getGeoByLocation(location));*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return location;
	}
	//基站定位
	private GeoPoint getNetWorkLocation(){
		tm = (TelephonyManager) myContext.getSystemService(Context.TELEPHONY_SERVICE);
		GsmCellLocation gcl = (GsmCellLocation) tm.getCellLocation();
		int cid = gcl.getCid();
		int lac = gcl.getLac();
		int mcc = Integer.valueOf(tm.getNetworkOperator().substring(0,
				3));
		int mnc = Integer.valueOf(tm.getNetworkOperator().substring(3,
				5));
		try {
			// 组装JSON查询字符串
			JSONObject holder = new JSONObject();
			holder.put("version", "1.1.0");
			holder.put("host", "maps.google.com");
			// holder.put("address_language", "zh_CN");
			holder.put("request_address", true);
			JSONArray array = new JSONArray();
			JSONObject data = new JSONObject();
			data.put("cell_id", cid); // 25070
			data.put("location_area_code", lac);// 4474
			data.put("mobile_country_code", mcc);// 460
			data.put("mobile_network_code", mnc);// 0
			array.put(data);
			holder.put("cell_towers", array);
			// 创建连接，发送请求并接受回应
			DefaultHttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(
					"http://www.google.com/loc/json");
			StringEntity se = new StringEntity(holder.toString());
			post.setEntity(se);
			HttpResponse resp = client.execute(post);
			HttpEntity entity = resp.getEntity();
			BufferedReader br = new BufferedReader(
					new InputStreamReader(entity.getContent()));
			StringBuffer sb = new StringBuffer();
			String result = br.readLine();
			while (result != null) {
				sb.append(result);
				result = br.readLine();
			}
			JSONObject jsonObject = new JSONObject(sb.toString());

			JSONObject jsonObject1 = new JSONObject(jsonObject
					.getString("location"));
			String lats=jsonObject1.getString("latitude");
			String lngs=jsonObject1.getString("longitude");
			double lat=Double.parseDouble(lats)*1E6;
			double lng=Double.parseDouble(lngs)*1E6;
			GeoPoint point = new GeoPoint((int)lat,(int)lng);
			return point;
		}catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	
	
	private static int ENOUGH_LONG = 1000 * 10;	 
	
	/** Determines whether one Location reading is better than the current Location fix 
	  * Copy from android developer site
	  * @param location  The new Location that you want to evaluate 
	  * @param currentBestLocation  The current Location fix, to which you want to compare the new one 
	  */ 
	private static boolean isBetterLocation(Location location, Location currentBestLocation) { 
	    if (currentBestLocation == null) { 
	        // A new location is always better than no location 
	        return true; 
	    } 	 
	    // Check whether the new location fix is newer or older 
	    long timeDelta = location.getTime() - currentBestLocation.getTime(); 
	    boolean isSignificantlyNewer = timeDelta > ENOUGH_LONG; 
	    boolean isSignificantlyOlder = timeDelta < -ENOUGH_LONG; 
	    boolean isNewer = timeDelta > 0; 
	 
	    // If it's been more than max interval since the current location, use the new location 
	    // because the user has likely moved 
	    if (isSignificantlyNewer) { 
	        return true; 
	    // If the new location is more than max interval older, it must be worse 
	    } else if (isSignificantlyOlder) { 
	        return false; 
	    } 	 
	    // Check whether the new location fix is more or less accurate 
	    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy()); 
	    boolean isLessAccurate = accuracyDelta > 0; 
	    boolean isMoreAccurate = accuracyDelta < 0; 
	    boolean isSignificantlyLessAccurate = accuracyDelta > 200; 
	 
	    // Check if the old and new location are from the same provider 
	    boolean isFromSameProvider = isSameProvider(location.getProvider(), 
	            currentBestLocation.getProvider()); 
	 
	    // Determine location quality using a combination of timeliness and accuracy 
	    if (isMoreAccurate) { 
	        return true; 
	    } else if (isNewer && !isLessAccurate) { 
	        return true; 
	    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) { 
	        return true; 
	    } 
	    return false; 
	}
	
	/** Checks whether two providers are the same */ 
	static boolean isSameProvider(String provider1, String provider2) { 
	    if (provider1 == null) { 
	      return provider2 == null; 
	    } 
	    return provider1.equals(provider2); 
	}
	
	//位置回调接口，将由map的activity实现
	public interface LocationCallBack
	{
		//location: 当前位置
		void onCurrentLocation(GeoPoint location);
	}
		
    //utility functions
	private GeoPoint getGeoByLocation(Location location)
	{
		GeoPoint res = null;
		if(location!=null)
		{
			double geolat = location.getLatitude() * 1E6;
			double geolng = location.getLongitude() * 1E6;
			res = new GeoPoint((int) (geolat), (int) (geolng));
		}
		return res;
	}
}
