package com.imps.ui.map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.ItemizedOverlay.OnFocusChangeListener;
import com.google.android.maps.OverlayItem;
import com.imps.IMPSDev;

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
	
	public FriendLocationOverlay(Drawable defaultMarker) {
		super(defaultMarker);
	}
	@Override
	public void onClick(View v) {
	}
	@Override
	public void onFocusChanged(ItemizedOverlay overlay, OverlayItem newFocus) {
	}
	@Override
	protected OverlayItem createItem(int i) {
		return null;
	}
	@Override
	public int size() {
		return 0;
	}
}
