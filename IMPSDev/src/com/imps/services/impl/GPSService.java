package com.imps.services.impl;

import java.util.Date;
import java.util.List;
import java.util.Observable;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.imps.IMPSDev;
import com.imps.basetypes.GeoLocation;
import com.imps.services.IGPSService;

public class GPSService extends Observable implements IGPSService,LocationListener{
	private static boolean DEBUG = IMPSDev.isDEBUG();
	private static String TAG = GPSService.class.getCanonicalName();
	private boolean isStarted = false;
	private LocationManager mLocationManager;
	
	private static final int MINTIME = 60000;   
	private static final int MINDINSTANCE = 2; 
    public static final float REQUESTED_FIRST_SEARCH_ACCURACY_IN_METERS = 100.0f;
    public static final int REQUESTED_FIRST_SEARCH_MAX_DELTA_THRESHOLD = 1000 * 60 * 5;
    public static final long LOCATION_UPDATE_MAX_DELTA_THRESHOLD = 1000 * 60 * 5;
    
	private Location mLastLocation;
	private GeoLocation mCurrent;
	
	public GPSService(){
		mLocationManager = (LocationManager)IMPSDev.getContext().getSystemService(Context.LOCATION_SERVICE);
	}
	@Override
	public boolean start() {
		if(DEBUG)Log.d(TAG,"Starting GPS Service");
		if (mLocationManager!=null&&mLocationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER )) {
			if(DEBUG) Log.d(TAG,"GPS Not available");
			isStarted = false;
			return false;
		}
		//init
        List<String> providers = mLocationManager.getProviders(true);
        int providersCount = providers.size();
        for (int i = 0; i < providersCount; i++) {
            String providerName = providers.get(i);
            if (mLocationManager.isProviderEnabled(providerName)) {
                updateLocation(mLocationManager.getLastKnownLocation(providerName));
            }
        }
		
		
		Criteria mcriteria = new Criteria();
		mcriteria.setAccuracy(Criteria.ACCURACY_FINE);
		mcriteria.setAltitudeRequired(false);
		mcriteria.setBearingRequired(false); 
		mcriteria.setCostAllowed(true); 
		mcriteria.setPowerRequirement(Criteria.POWER_MEDIUM);
		String provider = mLocationManager.getBestProvider(mcriteria, true);
		Location location = mLocationManager.getLastKnownLocation(provider);
		updateLocation(location);
		mLocationManager.requestLocationUpdates(provider,MINTIME,MINDINSTANCE, this);
		isStarted = true;
		return true;
	}
	@Override
	public boolean stop() {
		isStarted = false;
		mLocationManager.removeUpdates(this);
		return isStarted;
	}

	@Override
	public GeoLocation getGeoLocation() {
		if(mLastLocation==null){
			return null;
		}
		GeoPoint point = getGeoByLocation(mLastLocation);
		mCurrent = new GeoLocation(point.getLatitudeE6(),point.getLongitudeE6());
		mCurrent.setGeoType(GeoLocation.TYPE_GPS);
		return mCurrent;
	}

	@Override
	public boolean isAvailable() {
		return mLocationManager!=null&&mLocationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
	}

	@Override
	public boolean isStarted() {
		return isStarted;
	}
	public void openGPSSettings() {
        Toast.makeText(IMPSDev.getContext(), "请开启GPS", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
        IMPSDev.getContext().startActivity(intent); //此为设置完成后返回到获取界面
    }
	@Override
	public void onLocationChanged(Location location) {
		if (DEBUG) Log.d(TAG, "onLocationChanged: " + location);
        updateLocation(location);
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
	@Override
	public void onProviderEnabled(String provider) {
	}
	@Override
	public void onProviderDisabled(String provider) {
	}
    synchronized public void onBestLocationChanged(Location location) {
        if (DEBUG) Log.d(TAG, "onBestLocationChanged: " + location);
        mLastLocation = location;
        setChanged();
        notifyObservers(location);
    }
	
	public void updateLocation(Location location){
        if (DEBUG) {
            Log.d(TAG, "updateLocation: Old: " + mLastLocation);
            Log.d(TAG, "updateLocation: New: " + location);
        }
        if (location == null) {
            if (DEBUG) Log.d(TAG, "updated location is null, doing nothing");
            return;
        }
        long now = new Date().getTime();
        long locationUpdateDelta = now - location.getTime();
        long lastLocationUpdateDelta = now - mLastLocation.getTime();
        boolean locationIsInTimeThreshold = locationUpdateDelta <= LOCATION_UPDATE_MAX_DELTA_THRESHOLD;
        boolean lastLocationIsInTimeThreshold = lastLocationUpdateDelta <= LOCATION_UPDATE_MAX_DELTA_THRESHOLD;
        boolean locationIsMostRecent = locationUpdateDelta <= lastLocationUpdateDelta;

        boolean accuracyComparable = location.hasAccuracy() || mLastLocation.hasAccuracy();
        boolean locationIsMostAccurate = false;
        if (accuracyComparable) {
            // If we have only one side of the accuracy, that one is more
            // accurate.
            if (location.hasAccuracy() && !mLastLocation.hasAccuracy()) {
                locationIsMostAccurate = true;
            } else if (!location.hasAccuracy() && mLastLocation.hasAccuracy()) {
                locationIsMostAccurate = false;
            } else {
                // If we have both accuracies, do a real comparison.
                locationIsMostAccurate = location.getAccuracy() <= mLastLocation.getAccuracy();
            }
        }
        if (DEBUG) {
            Log.d(TAG, "locationIsMostRecent:\t\t\t" + locationIsMostRecent);
            Log.d(TAG, "locationUpdateDelta:\t\t\t" + locationUpdateDelta);
            Log.d(TAG, "lastLocationUpdateDelta:\t\t" + lastLocationUpdateDelta);
            Log.d(TAG, "locationIsInTimeThreshold:\t\t" + locationIsInTimeThreshold);
            Log.d(TAG, "lastLocationIsInTimeThreshold:\t" + lastLocationIsInTimeThreshold);

            Log.d(TAG, "accuracyComparable:\t\t\t" + accuracyComparable);
            Log.d(TAG, "locationIsMostAccurate:\t\t" + locationIsMostAccurate);
        }
        // Update location if its more accurate and w/in time threshold or if
        // the old location is
        // too old and this update is newer.
        if (accuracyComparable && locationIsMostAccurate && locationIsInTimeThreshold) {
            onBestLocationChanged(location);
        } else if (locationIsInTimeThreshold && !lastLocationIsInTimeThreshold) {
            onBestLocationChanged(location);
        }
	}
    public boolean isAccurateEnough(Location location) {
        if (location != null && location.hasAccuracy()
                && location.getAccuracy() <= REQUESTED_FIRST_SEARCH_ACCURACY_IN_METERS) {
            long locationUpdateDelta = new Date().getTime() - location.getTime();
            if (locationUpdateDelta < REQUESTED_FIRST_SEARCH_MAX_DELTA_THRESHOLD) {
                if (DEBUG) Log.d(TAG, "Location is accurate: " + location.toString());
                return true;
            }
        }
        if (DEBUG) Log.d(TAG, "Location is not accurate: " + String.valueOf(location));
        return false;
    }
    
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
