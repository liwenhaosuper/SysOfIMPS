package com.imps.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.imps.IMPSDev;
import com.imps.R;
import com.imps.basetypes.UserStatus;
import com.imps.net.handler.UserManager;
import com.imps.receivers.IMPSBroadcastReceiver;
import com.imps.ui.map.CrashFixMyLocationOverlay;
import com.imps.ui.map.FriendLocationOverlay;

public class FriendLocation extends MapActivity implements View.OnClickListener{
	private static boolean DEBUG = IMPSDev.isDEBUG();
	private static String TAG = FriendLocation.class.getCanonicalName();

	private final static int RADIUS = 200;
	private final static long ANIMATION_TIME = 300;
	private final static long TIME_INTERVAL = 30;
	
	private Button mOption;
	private Button mCheckin;
	private Button mFriendsactivity;
	private Button mMyvalidlocation;
	private Button mNearbyactivity;
	
	private ImageButton loction_Btn;
	private ImageButton layer_Btn;
	private ImageButton about_Btn;
	private Button search_btn;

	private List<AnimationSet> mOutAnimatinSets = new ArrayList<AnimationSet>();
	private List<AnimationSet> mInAnimatinSets = new ArrayList<AnimationSet>();
	private List<Button> mList = new ArrayList<Button> ();
	private boolean isIn = true;
	private boolean isInit = false;
	private Rect[] after  =new Rect[4];
	
    private MapView mMapView;
    private MapController mMapController;
    private CrashFixMyLocationOverlay mMyLocationOverlay;
    private FriendLocationOverlay mOnlineFrilocOverlay;
    private FriendLocationOverlay mOfflineFrilocOverlay;
    private View mMyPopView;
    private View mFriLocPopView;
    private FriendLocationReceiver receiver = new FriendLocationReceiver();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friendlocation);
        initViews();
        initAnimation();
        //initOverlays();
    }
    @Override
    public void onResume(){
    	super.onResume();
    	registerReceiver(receiver,receiver.getFilter());
    	mMyLocationOverlay.enableMyLocation();
    	mMyLocationOverlay.enableCompass();
    }
    @Override
    public void onStop(){
    	super.onStop();
    	unregisterReceiver(receiver);
    }
    public void initPopview(){
    	if(null == mMyPopView){
    		mMyPopView = getLayoutInflater().inflate(R.layout.mypopview, null);
			mMapView.addView(mMyPopView, new MapView.LayoutParams(
					MapView.LayoutParams.WRAP_CONTENT,
					MapView.LayoutParams.WRAP_CONTENT, null,
					MapView.LayoutParams.BOTTOM_CENTER));
			mMyPopView.setVisibility(View.GONE);
    	}
    	if(null == mFriLocPopView){
    		mFriLocPopView = getLayoutInflater().inflate(R.layout.friendlocationoverlay, null);
			mMapView.addView(mFriLocPopView, new MapView.LayoutParams(
					MapView.LayoutParams.WRAP_CONTENT,
					MapView.LayoutParams.WRAP_CONTENT, null,
					MapView.LayoutParams.BOTTOM_CENTER));
			mFriLocPopView.setVisibility(View.GONE);
    	}
    }
	public void initViews(){
        loction_Btn = (ImageButton)findViewById(R.id.loction);
    	layer_Btn = (ImageButton)findViewById(R.id.layer);
    	about_Btn = (ImageButton)findViewById(R.id.about);
    	search_btn = (Button)findViewById(R.id.search);
    	loction_Btn.setOnClickListener(this);
    	layer_Btn.setOnClickListener(this);
    	about_Btn.setOnClickListener(this);
    	search_btn.setOnClickListener(this);
		
		
		mMapView = (MapView) findViewById(R.id.MapView);
		//mMapView.setBuiltInZoomControls(true);
	    mMapController = mMapView.getController();
	    mMapView.setClickable(true);
	    
		mOption = (Button)findViewById(R.id.map_option);
		mCheckin = (Button)findViewById(R.id.map_checkin);
		mFriendsactivity = (Button)findViewById(R.id.map_friendsactivity);
		mMyvalidlocation = (Button)findViewById(R.id.map_myvalidlocation);
		mNearbyactivity = (Button)findViewById(R.id.map_nearbyactivity);
		mOption.setOnClickListener(this);
		mCheckin.setOnClickListener(this);
		mFriendsactivity.setOnClickListener(this);
		mMyvalidlocation.setOnClickListener(this);
		mNearbyactivity.setOnClickListener(this);
    	mList.add(mCheckin);
    	mList.add(mFriendsactivity);
    	mList.add(mMyvalidlocation);
    	mList.add(mNearbyactivity);
    	mOption.setOnClickListener(this);
    	
    	initPopview();
    	
    	mMyLocationOverlay = new CrashFixMyLocationOverlay(this, mMapView,mMyPopView);
    	mMapView.getOverlays().add(mMyLocationOverlay);
    	
    	mOnlineFrilocOverlay = new FriendLocationOverlay(this.getResources().getDrawable(R.drawable.online),this,mMapView,
    			mFriLocPopView);
    	mOfflineFrilocOverlay = new FriendLocationOverlay(this.getResources().getDrawable(R.drawable.offline),this,mMapView,
    			mFriLocPopView);
    	mMapView.getOverlays().add(mOnlineFrilocOverlay);
    	mMapView.getOverlays().add(mOfflineFrilocOverlay);
    	
	}
	public void initAllFriendLocationOverlays(){
		mOnlineFrilocOverlay.removeAllItems();
		mOfflineFrilocOverlay.removeAllItems();
		for(int i=0;i<UserManager.AllFriList.size();i++){
			GeoPoint point = new GeoPoint((int)(UserManager.AllFriList.get(i).getLocX()*1E6),(int)(UserManager.AllFriList.get(i).getLocY()*1E6));
			OverlayItem item = new OverlayItem(point,UserManager.AllFriList.get(i).getUsername(),
					getResources().getString(R.string.lastupdatetime)+":"+UserManager.AllFriList.get(i).getLoctime()+"\n"+UserManager.AllFriList.get(i).getDescription());
			if(UserManager.AllFriList.get(i).getStatus()==UserStatus.ONLINE){
				mOnlineFrilocOverlay.addOverlay(item);
			}else{
				mOfflineFrilocOverlay.addOverlay(item);
			}
			
		}
	}
	
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public void onClick(View v) {
		if(v==mOption){
			if(DEBUG) Log.d(TAG,"Option click...");
			if (isIn) {
				startOutAnimation();
				isIn = false;
			} else {
				startInAnimation();
				isIn = true;
			}	
		}else if(v==mCheckin){
			if(DEBUG) Log.d(TAG,"Check in click...");
			startInAnimation();
			isIn = true;
		}else if(v==mFriendsactivity){
			if(DEBUG) Log.d(TAG,"Friends Activity click...");
			startInAnimation();
			isIn = true;
			Toast.makeText(FriendLocation.this,getResources().getString(R.string.friendlocation_loading), Toast.LENGTH_SHORT).show();
			initAllFriendLocationOverlays();
		}else if(v==mMyvalidlocation){
			if(DEBUG) Log.d(TAG,"My location click...");
			startInAnimation();
			isIn = true;
		}else if(v==mNearbyactivity){
			if(DEBUG) Log.d(TAG,"Nearby click...");
			startInAnimation();
			isIn = true;
		}
		else{
			if(DEBUG) Log.d(TAG,"click:"+v.getId());
			switch(v.getId()){
			case R.id.loction:
			{
				AnimateToMyLocation(18);
				break;
			}			
			case R.id.search:
			{
				onSearchRequested();
				break;
			}		
			case R.id.about:
			{
				ComponentName cn=new ComponentName(FriendLocation.this,About.class);
				Intent intent=new Intent();
				intent.setComponent(cn);
				startActivity(intent);
				break;
			}
			case R.id.layer:
			{ 
				selectViewMode();
				break;
			}
			default:break;
			}
		}
	}
	public void AnimateToMyLocation(int zoomLevel){
		mMyLocationOverlay.animateToMyLocation(zoomLevel);
	}
	
    private void initAnimation() {
    	RotateAnimation outRotaAni = new RotateAnimation(0, 720, 0, 0);
    	outRotaAni.setDuration(ANIMATION_TIME);
    	RotateAnimation inRotaAni = new RotateAnimation(720, 0, 0, 0);
    	inRotaAni.setDuration(ANIMATION_TIME);
    	int size = mList.size();
    	for (int i = 0; i < size; i ++) {
    		final Button button = mList.get(i);
    		int x;
    		int y;
    		double angle;
    		if (i == 0) {
    			x = 0;
    			y = RADIUS;
    			angle = 0;
    		} else if (i == mList.size() - 1) {
    			x = RADIUS;
    			y = 0;
    			angle = 90;
    		} else {
    			angle = (90 / (size - 1)) * i;
    			x = (int) (RADIUS * Math.sin(Math.toRadians(angle)));
    			y = (int) (RADIUS * Math.cos(Math.toRadians(angle)));
    		}
    		long time = ANIMATION_TIME - i * TIME_INTERVAL;
    		TranslateAnimation outTranAni = new TranslateAnimation(0, x ,0 , -y);
    		outTranAni.setDuration(time);
    		AnimationSet outSet = new AnimationSet(true);
    		outSet.addAnimation(outTranAni);
    		outSet.setFillAfter(false);
    		mOutAnimatinSets.add(outSet);
    		//
    		final AnimationSet outAfterSet = new AnimationSet(true);
    		TranslateAnimation outAfterTranAni = new TranslateAnimation(x,x * 8 / 9,-y ,-y * 8 / 9);
    		after[i] = new Rect(x*8/9, y*8/9, x*8/9, y*8/9);
    		outAfterTranAni.setDuration(time);
    		outAfterSet.addAnimation(outAfterTranAni);
    		outAfterSet.setFillAfter(false);
    		outSet.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationEnd(Animation animation) {
					button.startAnimation(outAfterSet);
				}
				@Override
				public void onAnimationRepeat(Animation animation) {
				}
				@Override
				public void onAnimationStart(Animation animation) {
				}
    			
    		});
    		outAfterSet.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationEnd(Animation animation) {
					if(button==mCheckin){
						button.layout(after[0].left, after[0].top,after[0].right,after[0].bottom);
					}else if(button==mFriendsactivity){
						button.layout(after[1].left, after[1].top,after[1].right,after[1].bottom);
					}else if(button==mMyvalidlocation){
						button.layout(after[2].left, after[2].top,after[2].right,after[2].bottom);
					}else if(button==mNearbyactivity){
						button.layout(after[3].left, after[3].top,after[3].right,after[3].bottom);
					}	
					button.clearAnimation();
				}
				@Override
				public void onAnimationRepeat(Animation animation) {
				}
				@Override
				public void onAnimationStart(Animation animation) {
				}
    			
    		});
    		//
    		TranslateAnimation inTranAni = new TranslateAnimation(0, -x*8/9,0 ,y*8/9);
    		inTranAni.setDuration(time);
    		AnimationSet inSet = new AnimationSet(true);
    		inSet.addAnimation(inTranAni);
    		inSet.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationEnd(Animation animation) {
					if(button!=mOption) button.layout(mOption.getLeft(), mOption.getTop(), mOption.getRight(), mOption.getBottom());
				}
				@Override
				public void onAnimationRepeat(Animation animation) {
				}
				@Override
				public void onAnimationStart(Animation animation) {
				}
    			
    		});
    		inSet.setFillAfter(true);
    		mInAnimatinSets.add(inSet);
    		
		}
    }
	private void startInAnimation() {
		for (int i = 0; i < mList.size(); i ++) {
			Button button = mList.get(i);
			button.startAnimation(mInAnimatinSets.get(i));
		}
	}

	private void startOutAnimation() {
		for (int i = 0; i < mList.size(); i ++) {
			Button button = mList.get(i);
			if(!isInit){
				after[i] = new Rect(after[i].left+button.getLeft(),button.getTop()-after[i].top,button.getRight()+after[i].right,button.getBottom()-after[i].bottom);
			}
			button.startAnimation(mOutAnimatinSets.get(i));
		}
		isInit=true;
	}
	private void selectViewMode() {
		OnClickListener listener = new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					mMapView.setTraffic(false);
					mMapView.setSatellite(false);
					mMapView.setStreetView(true);
					break;
				case 1:
					mMapView.setSatellite(false);
					mMapView.setStreetView(false);
					mMapView.setTraffic(true);
					break;
				case 2:
					mMapView.setStreetView(false);
					mMapView.setTraffic(false);
					mMapView.setSatellite(true);
					break;
				}

			}
		};

		String[] menu = { getResources().getString(R.string.street_view), getResources().getString(R.string.traffic_view), getResources().getString(R.string.satellite_view) };
		new AlertDialog.Builder(FriendLocation.this.getParent()).setTitle(getResources().getString(R.string.select_mapview_mode)).setItems(menu,
				listener).show();
	}
	
	
	
	public class FriendLocationReceiver extends IMPSBroadcastReceiver{
		@Override
		public void onReceive(Context context,Intent intent){
			super.onReceive(context, intent);
			
		}
		@Override
		public IntentFilter getFilter(){
			IntentFilter filter = super.getFilter();
			return filter;
		}
	}

}
