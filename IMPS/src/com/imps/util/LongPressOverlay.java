package com.imps.util;


import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.imps.activities.My_Map;

public class LongPressOverlay extends Overlay implements OnDoubleTapListener,OnGestureListener{

	private My_Map mContext;
	private MapView mMapView;
	private Handler mHandler;
	private MapController mMapCtrl;
	private GestureDetector gestureScanner = new GestureDetector(this);
	private int level = 0;
	
	public LongPressOverlay(My_Map context, MapView mapView, Handler handler,MapController mapCtrl){
		mContext = context;
		mMapView = mapView;
		mHandler = handler;
		mMapCtrl = mapCtrl;
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event, MapView mapView) {
		return gestureScanner.onTouchEvent(event);
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		if(++level % 3 == 0){
			mMapCtrl.zoomIn();
			level = 0;
		}
		return false;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		mContext.locPoint = mMapView.getProjection().fromPixels((int) e.getX(),
				(int) e.getY());
		mHandler.sendEmptyMessage(mContext.MSG_VIEW_LONGPRESS);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}
	
}

