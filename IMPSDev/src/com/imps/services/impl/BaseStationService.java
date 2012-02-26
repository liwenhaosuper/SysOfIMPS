package com.imps.services.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.imps.IMPSDev;
import com.imps.basetypes.GeoLocation;
import com.imps.services.IBaseStationService;

public class BaseStationService implements IBaseStationService{

	private static boolean DEBUG = IMPSDev.isDEBUG();
	private static String TAG = BaseStationService.class.getCanonicalName();
	private GeoLocation mCurrent;
	private TelephonyManager tm;
	private CellLocation cl;
	private boolean isStarted = false;
	private Timer checker = new Timer();
	@Override
	public boolean isStarted() {
		return isStarted;
	}
	@Override
	public boolean start() {
		tm = (TelephonyManager) IMPSDev.getContext().getSystemService(Context.TELEPHONY_SERVICE);
		if(tm==null&&DEBUG){
			Log.d(TAG,"TelephonyManager is null....");
		}
		if(tm.getCellLocation() instanceof GsmCellLocation){
			cl = (GsmCellLocation) tm.getCellLocation();
		}else if(tm.getCellLocation() instanceof  CdmaCellLocation){
			cl = (CdmaCellLocation)tm.getCellLocation();
		}
		if(cl==null&&DEBUG){
			Log.d(TAG,"CellLocation is null...");
		}
		checker.schedule(new TimerTask(){
			@Override
			public void run() {
				if(cl!=null&&tm!=null){
					CellLocation.requestLocationUpdate();
					getNetWorkLocation();
				}
			}
			
		},10000,600000);
		isStarted = true;
		return true;
	}

	@Override
	public boolean stop() {
		isStarted = false;
		return true;
	}

	@Override
	public boolean isAvailable() {
		return false;
	}

	//warning:it may return NULL
	@Override
	public GeoLocation getGeoLocation() {
		
		return mCurrent;
	}
	private void getNetWorkLocation(){
		if(tm==null||cl==null){
			return;
		}
		if(cl instanceof GsmCellLocation){
			int cid ;
			int lac;
			cid = ((GsmCellLocation)cl).getCid();
			lac = ((GsmCellLocation)cl).getLac();
			int mcc = Integer.valueOf(tm.getNetworkOperator().substring(0,
					3));
			int mnc = Integer.valueOf(tm.getNetworkOperator().substring(3,
					5));
			try {
				if(!ServiceManager.getmNet().isAvailable()){
					return;
				}
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
				mCurrent = new GeoLocation((int)lat,(int)lng);
				mCurrent.setGeoType(GeoLocation.TYPE_BSSTATION);
			}catch(Exception e)
			{
				if(DEBUG) e.printStackTrace();
			}
		}else{
			int lat = (int)((CdmaCellLocation)cl).getBaseStationLatitude();
			int lon = (int)((CdmaCellLocation)cl).getBaseStationLongitude();
			if(lat==Integer.MAX_VALUE||lon==Integer.MAX_VALUE){
				return;
			}
			mCurrent = new GeoLocation(lat,lon);
			mCurrent.setGeoType(GeoLocation.TYPE_BSSTATION);
		}
		
	}
}
