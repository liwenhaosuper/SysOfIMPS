package com.imps.ui.map;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.imps.IMPSDev;
import com.imps.R;
import com.imps.net.handler.UserManager;

public class CrashFixMyLocationOverlay extends MyLocationOverlay implements OnClickListener{
    private static final String TAG = CrashFixMyLocationOverlay.class.getCanonicalName();
    private static final boolean DEBUG = IMPSDev.isDEBUG();
    private View mPopView;
    private MapView mMapview;
    private MapController mMapCtrl;
    private boolean isShowing = false;
	private int layout_x = 0; // 用于设置popview 相对某个位置向x轴偏移
	private int layout_y = -30; // 用于设置popview 相对某个位置向x轴偏移
	private Context context;
    public CrashFixMyLocationOverlay(Context context, MapView mapView) {
        super(context, mapView);
    }
    public CrashFixMyLocationOverlay(Context context,MapView mapView,View popView){
    	super(context, mapView);
    	this.context = context;
    	this.mMapview = mapView;
    	this.mMapCtrl = mMapview.getController();
    	this.mPopView = popView;
    }

    @Override
    public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
        try {
            return super.draw(canvas, mapView, shadow, when);
        } catch (ClassCastException e) {
            if (DEBUG) Log.d(TAG, "Encountered overlay crash bug", e);
            return false;
        }
    }
    @Override
    public boolean onTap(GeoPoint point,MapView map){
    	if(DEBUG) Log.d(TAG,"onTap...");
    	if(super.onTap(point, map)){
    		if(DEBUG) Log.d(TAG,"You Click me...");
    		if(isShowing){
    			isShowing = false;
    			mPopView.setVisibility(View.GONE);
    		}else{
    			isShowing = true;
    			MapView.LayoutParams params = (MapView.LayoutParams) mPopView.getLayoutParams();
    			params.x = this.layout_x;//Y轴偏移
    			params.y = this.layout_y;//Y轴偏移
    			point = this.getMyLocation();
    			params.point = point;
    			mMapCtrl.animateTo(point);
    			TextView title_TextView = (TextView) mPopView.findViewById(R.id.map_bubbleTitle);
    			title_TextView.setText(context.getResources().getString(R.string.iamhear));
    			TextView desc_TextView = (TextView) mPopView.findViewById(R.id.map_bubbleText);
    			if(UserManager.getGlobaluser().getDescription().equals("")){
    				desc_TextView.setText("不说话的网友不是好网友...");
    			}else{
    				desc_TextView.setText(UserManager.getGlobaluser().getDescription());
    			}
    			RelativeLayout button = (RelativeLayout) mPopView.findViewById(R.id.map_bubblebtn);
    			button.setOnClickListener(this);    			
    			mMapview.updateViewLayout(mPopView, params);
    			mPopView.setVisibility(View.VISIBLE);
    		}
    	}else if(isShowing){
			isShowing = false;
			mPopView.setVisibility(View.GONE);
    	}
    	return true;
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.map_bubbleTitle:
			break;
		case R.id.map_bubbleText:
			break;
		case R.id.map_bubblebtn:
			if(DEBUG) Log.d(TAG,"Why click me?");
			break;				
		}
	}
}
