package com.imps.basetypes;

import com.google.android.maps.GeoPoint;

public class GeoLocation extends GeoPoint {
	
	public static byte TYPE_GPS = 1;
	public static byte TYPE_BSSTATION = 2;
	private byte geoType = TYPE_GPS;
	private boolean isLastAddress = false;
	
	public GeoLocation(int latitudeE6,int longitudeE6){
		super(latitudeE6,longitudeE6);
	}
	public void setGeoType(byte geoType) {
		this.geoType = geoType;
	}
	public byte getGeoType() {
		return geoType;
	}
	public void setLastAddress(boolean isLastAddress) {
		this.isLastAddress = isLastAddress;
	}
	public boolean isLastAddress() {
		return isLastAddress;
	}
}
