package com.imps.services;

import com.google.android.maps.GeoPoint;

public interface IHeartBeatService extends IService{
	void fireBeat(GeoPoint point);
}
