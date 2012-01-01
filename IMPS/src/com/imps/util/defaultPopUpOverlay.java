package com.imps.util;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.ItemizedOverlay.OnFocusChangeListener;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.imps.R;
import com.imps.activities.FriendTab;
import com.imps.activities.My_Map;

@SuppressWarnings("rawtypes")
public class defaultPopUpOverlay extends ItemizedOverlay implements OnFocusChangeListener,OnClickListener{
	private static final String TAG = "defaultPopUpOverlay";
	private List<OverlayItem> overlays = new ArrayList<OverlayItem>();
	private My_Map mContext;
	private GeoPoint point = null;
	private String desc = "";
	private String car_title = "";
	private int layout_x = 0; // 用于设置popview 相对某个位置向x轴偏移
	private int layout_y = -30; // 用于设置popview 相对某个位置向x轴偏移
	
	private MapView mMapView;
	private MapController mMapCtrl;
	private View mPopView;
	
	private Drawable itemDrawable;
	private Drawable itemSelectDrawable;
	private OverlayItem selectItem;
	private OverlayItem lastItem;
	public void setItemSelectDrawable(Drawable itemSelectDrawable) {
		this.itemSelectDrawable = itemSelectDrawable;
	}

	public defaultPopUpOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}

	public defaultPopUpOverlay(Drawable defaultMarker, Context context, MapView mapView, View popView, MapController mapCtrl) {
		super(boundCenterBottom(defaultMarker));
		itemDrawable = defaultMarker;
		itemSelectDrawable = defaultMarker;
		mContext = (My_Map) context;
		setOnFocusChangeListener(this);
		layout_x = itemDrawable.getBounds().centerX();
		layout_y = - itemDrawable.getBounds().height();
		mMapView =  mapView;
		mPopView = popView;
		mMapCtrl = mapCtrl;
		this.populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return overlays.get(i);
	}

	@Override
	public int size() {
		return overlays.size();
	}

	public void addOverlay(OverlayItem item) {
		overlays.add(item);
		populate();
	}

	public void removeOverlay(int location) {
		overlays.remove(location);
	}

	@Override
	public boolean onTap(GeoPoint p, MapView mapView) {
		return super.onTap(p, mapView);
	}

	@Override
	protected boolean onTap(int index) {
		return super.onTap(index);
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
	}

	@Override
	public void onFocusChanged(ItemizedOverlay overlay, OverlayItem newFocus) {
		Log.d(TAG , "item focus changed!");
		if (null != newFocus) {
			Log.d(TAG , "centerY : " + itemDrawable.getBounds().centerY() + "; centerX :" + itemDrawable.getBounds().centerX());
			Log.d(TAG , " height : " + itemDrawable.getBounds().height());
			MapView.LayoutParams params = (MapView.LayoutParams) mPopView.getLayoutParams();
			params.x = this.layout_x;//Y轴偏移
			params.y = this.layout_y;//Y轴偏移
			point = newFocus.getPoint();
			params.point = point;
			mMapCtrl.animateTo(point);
			TextView title_TextView = (TextView) mPopView.findViewById(R.id.map_bubbleTitle);
			title_TextView.setText(newFocus.getTitle());
			TextView desc_TextView = (TextView) mPopView.findViewById(R.id.map_bubbleText);
			if(null == newFocus.getSnippet() || "".equals(newFocus.getSnippet())){
				desc_TextView.setVisibility(View.GONE);
			}else{
				desc = newFocus.getSnippet();
				desc_TextView.setText(desc);
				desc_TextView.setVisibility(View.VISIBLE);
			}
			RelativeLayout button = (RelativeLayout) mPopView.findViewById(R.id.map_bubblebtn);
			button.setOnClickListener(this);
			mMapView.updateViewLayout(mPopView, params);
			mPopView.setVisibility(View.VISIBLE);
			selectItem = newFocus;
		}
		else{
			mPopView.setVisibility(View.INVISIBLE);
			selectItem = null;
		}
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
			if(!"我".equals(selectItem.getTitle())&&!"地址名称".equals(selectItem.getTitle()))
			{
				String fUserName = selectItem.getTitle();
				ComponentName cn=new ComponentName(mContext,"com.imps.activities.ChatView");
				Intent intent=new Intent();
				intent.setComponent(cn);
				intent.putExtra("fUsername", fUserName);
				mContext.startActivity(intent);
			}
			break;		
		}
		}
	}
	
}