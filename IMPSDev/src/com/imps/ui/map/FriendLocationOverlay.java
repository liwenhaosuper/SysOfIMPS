package com.imps.ui.map;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.ItemizedOverlay.OnFocusChangeListener;
import com.google.android.maps.OverlayItem;
import com.imps.IMPSDev;
import com.imps.R;

/*
 * use to display friends info 
 */
public class FriendLocationOverlay extends ItemizedOverlay implements OnFocusChangeListener,OnClickListener{
	private static boolean DEBUG = IMPSDev.isDEBUG();
	private static String TAG = FriendLocationOverlay.class.getCanonicalName();
	
    private View mPopView;
    private MapView mMapview;
    private MapController mMapCtrl;
    private Context context;
    private List<OverlayItem> overlays = new ArrayList<OverlayItem>();
	private int layout_x = 0; 
	private int layout_y = -30; 
	private OverlayItem selectItem;
	private OverlayItem lastItem;
	private Drawable itemDrawable;
	private Drawable itemSelectDrawable;
	private GeoPoint point = null;
	
	public FriendLocationOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}
	public FriendLocationOverlay(Drawable defaultMarker,Context context,MapView mapview,View popview){
		super(boundCenterBottom(defaultMarker));
		itemDrawable = defaultMarker;
		itemSelectDrawable = defaultMarker;
		this.context = context;
		this.mMapview = mapview;
		this.mPopView = popview;
		this.mMapCtrl = mMapview.getController();
		setOnFocusChangeListener(this);
		layout_x = itemDrawable.getBounds().centerX();
		layout_y = - itemDrawable.getBounds().height();
		this.populate();
	}
	public void removeAllItems(){
		overlays.clear();
		populate();
	}
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.map_bubbleTitle:
			break;
		case R.id.map_bubbleText:
			break;
		case R.id.map_bubblebtn:
		{
			if(selectItem==null)
				return;
			break;		
		}
		}
	
	}
	@Override
	public void onFocusChanged(ItemizedOverlay overlay, OverlayItem newFocus) {
		Log.d(TAG , "item focus changed!");
		if (null != newFocus) {
			Log.d(TAG , "centerY : " + itemDrawable.getBounds().centerY() + "; centerX :" + itemDrawable.getBounds().centerX());
			Log.d(TAG , " height : " + itemDrawable.getBounds().height());
			MapView.LayoutParams params = (MapView.LayoutParams) mPopView.getLayoutParams();
			params.x = this.layout_x;
			params.y = this.layout_y;
			point = newFocus.getPoint();
			params.point = point;
			mMapCtrl.animateTo(point);
			TextView title_TextView = (TextView) mPopView.findViewById(R.id.map_bubbleTitle);
			title_TextView.setText(newFocus.getTitle());
			TextView desc_TextView = (TextView) mPopView.findViewById(R.id.map_bubbleText);
			if(null == newFocus.getSnippet() || "".equals(newFocus.getSnippet())){
				desc_TextView.setVisibility(View.GONE);
			}else{
				String desc = newFocus.getSnippet();
				desc_TextView.setText(desc);
				desc_TextView.setVisibility(View.VISIBLE);
			}
			RelativeLayout button = (RelativeLayout) mPopView.findViewById(R.id.map_bubblebtn);
			button.setOnClickListener(this);
			mMapview.updateViewLayout(mPopView, params);
			mPopView.setVisibility(View.VISIBLE);
			selectItem = newFocus;
		}
		else{
			mPopView.setVisibility(View.INVISIBLE);
			selectItem = null;
		}
	}
	@Override
	protected OverlayItem createItem(int i) {
		return overlays.get(i);
	}
	public void addOverlay(OverlayItem item) {
		overlays.add(item);
		populate();
	}
	public void removeOverlay(int location) {
		overlays.remove(location);
	}
	@Override
	protected boolean onTap(int index) {
		return super.onTap(index);
	}
	@Override
	public boolean onTap(GeoPoint p, MapView mapView) {
		return super.onTap(p, mapView);
	}
	@Override
	public int size() {
		return overlays.size();
	}
}
