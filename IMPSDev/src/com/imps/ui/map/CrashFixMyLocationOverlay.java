package com.imps.ui.map;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.Canvas;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.imps.IMPSDev;
import com.imps.R;

public class CrashFixMyLocationOverlay extends MyLocationOverlay implements OnClickListener{
    private static final String TAG = CrashFixMyLocationOverlay.class.getCanonicalName();
    private static final boolean DEBUG = IMPSDev.isDEBUG();
    private View mPopView;
    private MapView mMapview;
    private MapController mMapCtrl;
    private boolean isShowing = false;
	private int layout_x = 0; 
	private int layout_y = -30; 
	private Context context;
	private String streetName = "";
	private final int ADDRESSLOADING = 1;
	private final int ADDRESSLOADED = 2;
	private final int ADDRESSLOADFAILED = 3;
	
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
    public Handler handler = new Handler(){
    	@Override
    	public void handleMessage(Message msg){
    		switch(msg.what){
    		case ADDRESSLOADING:
		        TextView addView = (TextView) mPopView.findViewById(R.id.map_bubbleText);	
    			if(addView!=null) addView.setText(loadingStreet());
    			if(getMyLocation()==null){
    				Message resmsg = new Message();
    				resmsg.what = ADDRESSLOADFAILED;
    				handler.sendMessage(resmsg);
    			}else{
    				streetName = getStreet();
    				Message loadres = new Message();
    				loadres.what = ADDRESSLOADED;
    				handler.sendMessage(loadres);
    			}
    			break;
    		case ADDRESSLOADED:
		        TextView desc_TextView = (TextView) mPopView.findViewById(R.id.map_bubbleText);	
    			if(desc_TextView!=null) desc_TextView.setText(streetName);
    			break;
    		case ADDRESSLOADFAILED:
		        TextView desc = (TextView) mPopView.findViewById(R.id.map_bubbleText);	
    			if(desc!=null) desc.setText(context.getResources().getString(R.string.positioning_unavailable));
    			break;
    		default:
    			break;
    		}
    	}
    };
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
    			params.x = this.layout_x;
    			params.y = this.layout_y;
    			point = this.getMyLocation();
    			params.point = point;
    			mMapCtrl.animateTo(point);
    			TextView title_TextView = (TextView) mPopView.findViewById(R.id.map_bubbleTitle);
    			title_TextView.setText(context.getResources().getString(R.string.iamhear));
/*    			TextView desc_TextView = (TextView) mPopView.findViewById(R.id.map_bubbleText);
    			
    			desc_TextView.setText(getStreet());*/
    			Message msg = new Message();
    			msg.what = ADDRESSLOADING;
    			handler.sendMessage(msg);
    			
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
	private String getStreet(){
		if(this.getMyLocation()==null){
			return context.getResources().getString(R.string.addressnotavailable);
		}
		Geocoder gecoder = new Geocoder(context,Locale.getDefault());
		String strname = context.getResources().getString(R.string.longitude)+":"+this.getMyLocation().getLongitudeE6()/1E6+","+
		 context.getResources().getString(R.string.latitude)+":"+this.getMyLocation().getLatitudeE6()/1E6+"\n";
		try {
			//getAddressLine(0):country，getAddressLine(1):area，getAddressLine(2):street
			List places = gecoder.getFromLocation(this.getMyLocation().getLatitudeE6()/1E6,this.getMyLocation().getLongitudeE6()/1E6,5);
			if(places!=null&&places.size()>0){
				strname+=context.getResources().getString(R.string.locatedat)+((Address)places.get(0)).getAddressLine(0)+""+((Address)places.get(0)).getAddressLine(1)+
				((Address)places.get(0)).getAddressLine(2);
			}
		} catch (IOException e) {
			if(DEBUG) e.printStackTrace();
			return strname;
		}
		return strname;
	}
	private String loadingStreet(){
		if(this.getMyLocation()==null){
			return context.getResources().getString(R.string.addressnotavailable);
		}
		String strname = context.getResources().getString(R.string.longitude)+":"+this.getMyLocation().getLongitudeE6()/1E6+","+
		 context.getResources().getString(R.string.latitude)+":"+this.getMyLocation().getLatitudeE6()/1E6+"\n";
		strname+=context.getResources().getString(R.string.loading_your_address);
		return strname;
	}
	public void animateToMyLocation(int zoomLevel){
		if(getMyLocation()==null){
			Toast.makeText(context, context.getResources().getString(R.string.positioning_unavailable), Toast.LENGTH_LONG);
			return;
		}
		mMapCtrl.setZoom(zoomLevel);
		mMapCtrl.animateTo(getMyLocation());
	}
}
