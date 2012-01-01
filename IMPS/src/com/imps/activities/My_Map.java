package com.imps.activities;


import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.imps.R;
import com.imps.activities.MyGeoNavigator.LocationCallBack;
import com.imps.base.User;
import com.imps.base.userStatus;
import com.imps.handler.IMService;
import com.imps.handler.RegularUserCheck;
import com.imps.handler.UserManager;
import com.imps.logger.GPSLoggerServiceManager;
import com.imps.main.Client;
import com.imps.media.rtp.core.NetworkFactory;
import com.imps.util.LongPressOverlay;
import com.imps.util.defaultPopUpOverlay;

public class My_Map extends MapActivity implements LocationCallBack,View.OnClickListener {

	public static MapView map;
	private static MapController mc;
	private static int intZoomLevel = 12;
	private View popView;
	private static final int MENU_Search = Menu.FIRST;
	private static final int MENU_Directions = Menu.FIRST + 1;
	private static final int MENU_Point = Menu.FIRST + 2;
	private static final int MENU_Location = Menu.FIRST + 3;
	private static final int MENU_Layers = Menu.FIRST + 5;
	private static final int MENU_Track = Menu.FIRST + 6;
	private static final int MENU_Exit = Menu.FIRST + 4;
	private static final int MENU_Configue=Menu.FIRST+7;
	private static final int MENU_About = Menu.FIRST + 8;
	//好友位置 string:friname  geopoint: position
	private static HashMap<User,GeoPoint> friPosList = null;
	public static GeoPoint currentGeoPoint ;//我的位置
	private static boolean addPointFlag = false;
	private static ImageButton curSessionsTab, friendListTab, mainMenuTab;	
	private static String username = null;
	private ExitReceiver exitReceiver = new ExitReceiver();
	private RegularUserCheck check = null;
	private static ArrayList<String> pointItems_address = new ArrayList<String>();
	private static MyGeoNavigator myNavigator = null;
	private Handler myhander;
	private defaultPopUpOverlay myLocationOverlay;  //我的位置层
	private defaultPopUpOverlay onlineFriLocationOverlay; //在线好友位置层
	private defaultPopUpOverlay offlineFriLocationOverlay; //离线好友位置层
	private defaultPopUpOverlay longPressOverlay;  //长按时间层
	private List<Overlay> mapOverlays;
	public GeoPoint locPoint;
	public final int MSG_VIEW_LONGPRESS = 10001;
	public final int MSG_VIEW_ADDRESSNAME = 10002;
	public final int MSG_VIEW_ADDRESSNAME_FAIL = 10004;
	public final int MSG_VIEW_LOCATIONLATLNG = 10003;
	public final int MSG_VIEW_LOCATIONLATLNG_FAIL = 10005;
	private ImageButton loction_Btn;
	private ImageButton layer_Btn;
	private ImageButton about_Btn;
	private Button search_btn;
	private GPSLoggerServiceManager mLoggerServiceManager;
	
	@Override
	public void onPause(){
		super.onPause();
		unregisterReceiver(exitReceiver);
	}
	@Override
    public void onResume()
    {
       super.onResume();
       IntentFilter ifilter = new IntentFilter();
       ifilter.addAction("exit");
       ifilter.addAction("fri_list");
       ifilter.addAction("status_notify");
       ifilter.addAction("go_to_loc");
       registerReceiver(exitReceiver,ifilter);
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//初始化UI
		setContentView(R.layout.my_map);
		System.out.println(" map creates");
		map = (MapView) findViewById(R.id.MapView);
		map.setBuiltInZoomControls(true);
		map.setClickable(true);
		mc = map.getController();
		initPopView();
		curSessionsTab = (ImageButton)findViewById(R.id.current_talk_menu);
		friendListTab =  (ImageButton)findViewById(R.id.friend_list_menu);
		mainMenuTab =    (ImageButton)findViewById(R.id.main_menu);
		
        loction_Btn = (ImageButton)findViewById(R.id.loction);
    	layer_Btn = (ImageButton)findViewById(R.id.layer);
    	about_Btn = (ImageButton)findViewById(R.id.about);
    	search_btn = (Button)findViewById(R.id.search);
    	
    	loction_Btn.setOnClickListener(this);
    	layer_Btn.setOnClickListener(this);
    	about_Btn.setOnClickListener(this);
    	search_btn.setOnClickListener(this);
		
		processTabClick();
		initHandler();
		mapOverlays = map.getOverlays();
		myLocationOverlay = new defaultPopUpOverlay(this.getResources().getDrawable(R.drawable.me),this,map,popView,mc);
		onlineFriLocationOverlay = new defaultPopUpOverlay(this.getResources().getDrawable(R.drawable.online),this,map,popView,mc);
		offlineFriLocationOverlay = new defaultPopUpOverlay(this.getResources().getDrawable(R.drawable.offline),this,map,popView,mc);
		longPressOverlay = new defaultPopUpOverlay(this.getResources().getDrawable(R.drawable.point_start),this,map,popView,mc);
		mapOverlays.add(new LongPressOverlay(this, map, myhander, mc));
		mc.setZoom(12);
		//定位管理
		MyGeoNavigator.init(My_Map.this.getApplicationContext(), My_Map.this, myhander);
		myNavigator = MyGeoNavigator.getInstance();
		displayFriLocation();
		displayMyLocation();
		//服务器通讯
		check = new RegularUserCheck(UserManager.getGlobaluser());//心跳检测。。。
		UserManager.getTimer().schedule(check, 1000*5,1000*20);
		try {
			NetworkFactory.loadFactory("com.imps.media.rtp.core.AndroidNetworkFactory");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//位置显示
	/*	this.startService(new Intent(Constants.SERVICENAME));
	      Object previousInstanceData = getLastNonConfigurationInstance();
	      if( previousInstanceData != null && previousInstanceData instanceof GPSLoggerServiceManager )
	      {
	         mLoggerServiceManager = (GPSLoggerServiceManager) previousInstanceData;
	      }
	      else
	      {
	         mLoggerServiceManager = new GPSLoggerServiceManager( (Context) this );
	      }
	      mLoggerServiceManager.startup();
	      Log.d("My_Map", "gps logger service start");*/
	}
	//菜单按钮事件
	//菜单按钮事件
	void processTabClick()
	{
		curSessionsTab.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
	       		ComponentName cn=new ComponentName(My_Map.this,CurrentSessions.class);
				
				Bundle bundle = new Bundle();
				if (username != null){
					bundle.putString("mUsername", username);
				}
				
				Intent intent=new Intent();
				intent.setComponent(cn);
				intent.putExtra("bundle", bundle);
				startActivityForResult(intent,0);
			}
		});
		friendListTab.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
	       		ComponentName cn=new ComponentName(My_Map.this,FriendTab.class);				
				Bundle bundle = new Bundle();
				if (username != null){
					bundle.putString("mUsername", username);
				}			
				Intent intent=new Intent();
				intent.setComponent(cn);
				intent.putExtra("bundle", bundle);
				startActivityForResult(intent,1);
			}
		});
	}
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	//called when location is changed
	@Override
	public void onCurrentLocation(GeoPoint location) {
		// TODO Auto-generated method stub
		Log.d("My_Map", "location update! ");
		GeoPoint point = location;
		if(point==null)
		{
			Log.d("My_Map", "location is null");
			return;
		}
		Log.d("My_Map", ""+location.getLatitudeE6()+":"+location.getLongitudeE6());	
		OverlayItem overlayitem = new OverlayItem(point, "我", "");
		if(myLocationOverlay.size() > 0){
			myLocationOverlay.removeOverlay(0);
		}
		myLocationOverlay.addOverlay(overlayitem);
		mapOverlays.add(myLocationOverlay);
		currentGeoPoint = point;
	}
	//显示好友位置
	public void displayFriLocation()
	{
		initialFriLocation();
		if(friPosList==null)
			return;
		Iterator<User> iter = friPosList.keySet().iterator();
		for(int i=0;i<friPosList.size();i++)
		{
			User tmpUser = iter.next();
			GeoPoint gp = friPosList.get(tmpUser);
			if(gp == null)
				return;
			if(tmpUser.getStatus()==userStatus.ONLINE)
			{
	            OverlayItem item = new OverlayItem(gp,tmpUser.getUsername(),"在线");
	            onlineFriLocationOverlay.addOverlay(item);
			}
			else{
	            OverlayItem item = new OverlayItem(gp,tmpUser.getUsername(),"离线");
	            offlineFriLocationOverlay.addOverlay(item);
			}
		}
		if(friPosList!=null)
		{
			mapOverlays.add(onlineFriLocationOverlay);
			mapOverlays.add(offlineFriLocationOverlay);
		}

	}
	//显示我的位置
	public void displayMyLocation()
	{
		User me=UserManager.getGlobaluser();
		if(currentGeoPoint==null)
		{
			currentGeoPoint = myNavigator.getBestLocation();
			if(currentGeoPoint==null)
			{
				Log.d("tag", "currentGeoPoint is null!");
				return;
			}
				
		}
		String str = "经度："+_fformat(currentGeoPoint.getLatitudeE6()/1E6)+"\n纬度: "+_fformat(currentGeoPoint.getLongitudeE6()/1E6);
		OverlayItem item = new OverlayItem(currentGeoPoint,"我",str);
	    myLocationOverlay.addOverlay(item);
	    mapOverlays.add(myLocationOverlay);
	}
	//初始化friPosList
	void initialFriLocation()
	{
		List<User> friendlist=UserManager.AllFriList;
		if(friendlist==null)
			return;
		friPosList = new HashMap<User,GeoPoint>();
		for(int i=0;i<friendlist.size();i++){
			User f=friendlist.get(i);
			GeoPoint gp=new GeoPoint((int)(f.getLocX()),(int)(f.getLocY()));
			friPosList.put(f, gp);
		}
	}
	//初始化popView
	public void initPopView()
	{
    	if(null == popView){
			popView = getLayoutInflater().inflate(R.layout.overlay_popup, null);
			map.addView(popView, new MapView.LayoutParams(
					MapView.LayoutParams.WRAP_CONTENT,
					MapView.LayoutParams.WRAP_CONTENT, null,
					MapView.LayoutParams.BOTTOM_CENTER));
			popView.setVisibility(View.GONE);
    	}
	}
	//初始化chengyuan
	public void initHandler()
	{
		myhander = new Handler(){
			@Override
			public void handleMessage(Message msg)
			{
				switch(msg.what)
				{
				case MSG_VIEW_LONGPRESS://处理长按时间返回位置信息
				{
					if(null == locPoint) 
					{
						return;
					}
					new Thread( new Runnable() {
						@Override
						public void run() {
							String addressName = "";
							
							int count = 0;
							while(true){
								try {
									Thread.sleep(100);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								count++;
								addressName = getLocationAddress(locPoint);
								Log.d("My_Map", "获取地址名称");
								//请求三次获取不到结果就返回
								if("".equals(addressName) && count > 4){
									Message msg1 = new Message();
									msg1.what = MSG_VIEW_ADDRESSNAME_FAIL;
									myhander.sendMessage(msg1);
									break;
								}else if("".equals(addressName) ){
									continue;
								}else{
									break;
								}
								
								
							}
							if(!"".equals(addressName) || count < 5){
								Message msg = new Message();
								msg.what = MSG_VIEW_ADDRESSNAME;
								msg.obj = addressName;
								myhander.sendMessage(msg);
							}
						}
					}
					).start();
					OverlayItem overlayitem = new OverlayItem(locPoint, "地址名称",
							"正在地址加载...");
					if(longPressOverlay.size() > 0){
						longPressOverlay.removeOverlay(0);
					}
					popView.setVisibility(View.GONE);
					longPressOverlay.addOverlay(overlayitem);
					longPressOverlay.setFocus(overlayitem);
					mc.animateTo(locPoint);
					map.invalidate();
					break;
				}
				case MSG_VIEW_ADDRESSNAME:
				{
					//获取到地址后显示在泡泡上
					TextView desc = (TextView) popView.findViewById(R.id.map_bubbleText);
					desc.setText((String)msg.obj);
					popView.setVisibility(View.VISIBLE);
					android.os.Handler hander= new android.os.Handler();
					//设定定时器并在设定时间后使对话框关闭
					    hander.postDelayed(new Runnable() {     
					     @Override
					     public void run() {
					    	 popView.setVisibility(View.INVISIBLE);
					     }
					    }, 6 *1000);
					break;
				}
				case MSG_VIEW_ADDRESSNAME_FAIL:
				{
					TextView desc = (TextView) popView.findViewById(R.id.map_bubbleText);
					desc.setText("获取地址失败");
					popView.setVisibility(View.VISIBLE);
					android.os.Handler hander= new android.os.Handler();
					//设定定时器并在设定时间后使对话框关闭
					    hander.postDelayed(new Runnable() {     
					     @Override
					     public void run() {
					    	 popView.setVisibility(View.INVISIBLE);
					     }
					    }, 3 *1000);
					break;
				}
				case MSG_VIEW_LOCATIONLATLNG:
				{
					break;
				}
				case MSG_VIEW_LOCATIONLATLNG_FAIL:
				{
					break;
				}
				case MyGeoNavigator.LOCATION_BASE_ID:
				{
					GeoPoint point = (GeoPoint)msg.obj;
					if(point==null||point.getLatitudeE6()<=0||point.getLongitudeE6()<=0)
						return;
					currentGeoPoint = point;
					OverlayItem overlayitem = new OverlayItem(point, "我的位置", "");
					mc.setZoom(16);
					if(myLocationOverlay.size() > 0){
						myLocationOverlay.removeOverlay(0);
					}
					myLocationOverlay.addOverlay(overlayitem);
					mapOverlays.add(myLocationOverlay);
					mc.animateTo(point);
					break;
				}
				}
			}
		};
	}
	//处理三个button的事件
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.loction:
		{
			GoToMyLocation();
			break;
		}			
		case R.id.search:
		{
			onSearchRequested();
			break;
		}		
		case R.id.about:
		{
			ComponentName cn=new ComponentName(My_Map.this,About.class);
			Intent intent=new Intent();
			intent.setComponent(cn);
			startActivity(intent);
			break;
		}
		case R.id.layer:
		{
			selectViewMode();  //选择视图模式，街景模式，卫星模式等
			break;
		}
		default:break;
		}
	}
	@Override
	public boolean onSearchRequested(){
		//打开浮动搜索框（第一个参数默认添加到搜索框的值）
		Log.d("My_Map", "search button clicked...");
		startSearch(null, false, null, false);
		return true;
	}
	
	//broadcast receiver for this activity
	public class ExitReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			System.out.println("broadcast has been received");
			if("exit".equals(intent.getAction()))
			{
				if(check!=null)
				{
					check.cancel();
				}
		    	if(!Client.session.isClose())
		    	{
		    		 Client.session.close();
		    	}
		    	finish();
		    	stopService(new Intent(My_Map.this,IMService.class));
			}
			else if("fri_list".equals(intent.getAction()))
			{
				//refresh the map
				System.out.println("refresh the map method has been called");
			}
			else if("status_notify".equals(intent.getAction()))
			{
				//refresh the map
				System.out.println("refresh the map method has been called");
			}
			else if("go_to_loc".equals(intent.getAction()))
			{
				System.out.println("go to loc");
			}
		}		
	}
	/**
	 * add options menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0,MENU_Configue,0,"我的信息").setIcon(
				android.R.drawable.ic_menu_preferences);
		menu.add(0, MENU_Search, 1, R.string.Search).setIcon(
				android.R.drawable.ic_search_category_default);
		menu.add(0, MENU_Directions, 2, R.string.Directions).setIcon(
				android.R.drawable.ic_menu_directions);
		menu.add(0, MENU_Track, 3, R.string.Track).setIcon(
				android.R.drawable.ic_menu_upload);
		menu.add(0, MENU_About, 4, R.string.about).setIcon(android.R.drawable.ic_menu_info_details);
		menu.add(0, MENU_Exit, 5, R.string.Exit).setIcon(
				android.R.drawable.ic_menu_close_clear_cancel);
		return super.onCreateOptionsMenu(menu);
	}
	/**
	 * add options menu event handler
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case MENU_Configue:
			//Configue();
			break;
		case MENU_Search:
			search();          //查找地点
			break;
		case MENU_Directions:
			directions();  
			break;
		case MENU_Track:
			Intent controlIntent = new Intent( this, ControlTracking.class );
            startActivityForResult( controlIntent, MENU_Track );
			break;
		case MENU_Exit:
			UserManager.getInstance().setStatus(userStatus.OFFLINE);
			UserManager.setInstance(null);
			Intent i = new Intent();
			i.setAction("exit");
			sendBroadcast(i);//退出
			break;
		case MENU_About:
			ComponentName cn=new ComponentName(My_Map.this,About.class);
			Intent intent=new Intent();
			intent.setComponent(cn);
			startActivity(intent);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void GoToMyLocation()
	{
		if(currentGeoPoint==null||currentGeoPoint.getLatitudeE6()<=0||currentGeoPoint.getLongitudeE6()<=0)
		{
			Toast.makeText(this, "暂时无法定位", Toast.LENGTH_LONG);
		}
		else if(currentGeoPoint.getLatitudeE6()==0&&currentGeoPoint.getLongitudeE6()==0)
		{
			Toast.makeText(this, "暂时无法定位", Toast.LENGTH_LONG);
		
		}
		else{
			mc.setZoom(18);
			mc.animateTo(currentGeoPoint);
		}
	}
	
	/**
	 * 通过经纬度获取地址
	 * @param point
	 * @return
	 */
	private String getLocationAddress(GeoPoint point){
		String add = "";
		Geocoder geoCoder = new Geocoder(getBaseContext(),
				Locale.getDefault());
		try {
			List<Address> addresses = geoCoder.getFromLocation(
					point.getLatitudeE6() / 1E6, point.getLongitudeE6() / 1E6, 1);
			Address address = addresses.get(0);
			int maxLine = address.getMaxAddressLineIndex();
			if(maxLine >= 2){
				add =  address.getAddressLine(1) + address.getAddressLine(2);
			}else {
				add = address.getAddressLine(1);
			}
		} catch (IOException e) {
			add = "";
			e.printStackTrace();
		}
		return add;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode){
		case KeyEvent.KEYCODE_BACK:
			new AlertDialog.Builder(this)
				.setTitle("退出提示")
				.setMessage("注销登录？")
				.setPositiveButton("确定", new OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						UserManager.getInstance().setStatus(userStatus.OFFLINE);
						UserManager.setInstance(null);
						Intent i = new Intent();
						i.setAction("exit");
						sendBroadcast(i);
//						Process.killProcess(pid)
					}
					
				})
				.setNegativeButton("取消", null)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.show();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	/**
	 * 搜索
	 */
	private void search() {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setClass(My_Map.this, Search.class);
		startActivityForResult(intent, 0);
	}
	
	/**
	 * 选择视图模式 :街景、卫星、交通
	 */
	private void selectViewMode() {
		// TODO Auto-generated method stub
		OnClickListener listener = new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				switch (which) {
				case 0:
					map.setTraffic(false);
					map.setSatellite(false);
					map.setStreetView(true);
					break;
				case 1:
					map.setSatellite(false);
					map.setStreetView(false);
					map.setTraffic(true);
					break;
				case 2:
					map.setStreetView(false);
					map.setTraffic(false);
					map.setSatellite(true);
					break;
				}

			}
		};

		String[] menu = { "街景模式", "交通流量", "卫星地图" };
		new AlertDialog.Builder(My_Map.this).setTitle("请选择地图模式").setItems(menu,
				listener).show();
	}
	
	/**
	 * 导航
	 */
private void directions() {
	// TODO Auto-generated method stub
	final LayoutInflater factory = LayoutInflater.from(My_Map.this);
	final View dialogview = factory.inflate(R.layout.direction, null);

	new AlertDialog.Builder(this)
		.setTitle("请输入地址")
		.setView(dialogview)
		.setPositiveButton(R.string.menu_yes, 
				new AlertDialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						String from = ((EditText)dialogview
								.findViewById(R.id.edit_from)).getText()
								.toString();
						String to = ((EditText)dialogview
								.findViewById(R.id.edit_to)).getText()
								.toString();
						
						String url = "http://maps.google.com/maps/api/directions/xml?origin="
								   + geoPointToString(getGeoPoint(getLocationInfo(from)))
								   + "&destination="
								   + geoPointToString(getGeoPoint(getLocationInfo(to)))
								   + "&sensor=false&mode=walking";   
						
						HttpGet get = new HttpGet(url);   
						String strResult = "";   
						try {   
							HttpParams httpParameters = new BasicHttpParams();   
							HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);   
							HttpClient httpClient = new DefaultHttpClient(httpParameters);    
							
							HttpResponse httpResponse = null;   
							httpResponse = httpClient.execute(get);   
							
							if (httpResponse.getStatusLine().getStatusCode() == 200){   
								strResult = EntityUtils.toString(httpResponse.getEntity());   
							}   
						} catch (Exception e) {   
							return; 
						}   
						
						if (-1 == strResult.indexOf("<status>OK</status>")){   
							Toast.makeText(My_Map.this, "获取导航路线失败!", Toast.LENGTH_SHORT).show();   
							return;   
						}   
						
						int pos = strResult.indexOf("<overview_polyline>");   
						pos = strResult.indexOf("<points>", pos + 1);   
						int pos2 = strResult.indexOf("</points>", pos);   
						strResult = strResult.substring(pos + 8, pos2);   
						
						List<GeoPoint> points = decodePoly(strResult);
						System.out.println("points size:" + points.size());
						
						List<Overlay> mapOverlays = map.getOverlays();
						mapOverlays.clear();
						DirectionOverlay mTrack = new DirectionOverlay(points);
						mapOverlays.add(mTrack);
						
						if (points.size() >= 2){
							intZoomLevel = map.getZoomLevel();
							mc.animateTo(points.get(0));   
							mc.setZoom(intZoomLevel);
						}   
						
						map.invalidate();   
					}})
		.setNegativeButton(R.string.menu_no, null).show();
	}
/**
 * convert geography point to string format
 * @param gp
 * @return
 */
private String geoPointToString(GeoPoint gp){
	String str = "";
	try {
		if (gp != null) {
			double geolat = (int) gp.getLatitudeE6() / 1E6;
			double geolng = (int) gp.getLongitudeE6() / 1E6;
			str = String.valueOf(geolat) + "," + String.valueOf(geolng);
		}
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return str;
}
public static GeoPoint getGeoPoint(JSONObject jsonObject) {

	Double lon = new Double(0);
	Double lat = new Double(0);
	try {
		lon = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
			.getJSONObject("geometry").getJSONObject("location")
			.getDouble("lng");
		lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
			.getJSONObject("geometry").getJSONObject("location")
			.getDouble("lat");
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		Log.d("exception:getGeoByAddres", e.toString());
	}
	return new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));
}	
/**   
 * 解析返回xml中overview_polyline的路线编码  
 *   
 * @param encoded  
 * @return  
*/  
private List<GeoPoint> decodePoly(String encoded) {
	List<GeoPoint> poly = new ArrayList<GeoPoint>();   
	int index = 0, len = encoded.length();   
	int lat = 0, lng = 0;   
	
	while (index < len) {   
		int b, shift = 0, result = 0;   
		do {   
			b = encoded.charAt(index++) - 63;   
			result |= (b & 0x1f) << shift;   
			shift += 5;   
		} while (b >= 0x20);   
		int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));   
		lat += dlat;   
		
		shift = 0;   
		result = 0;   
		do {   
			b = encoded.charAt(index++) - 63;   
			result |= (b & 0x1f) << shift;   
			shift += 5;   
		} while (b >= 0x20);   
		int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));   
		lng += dlng;   
		
		GeoPoint p = new GeoPoint((int) (((double) lat / 1E5) * 1E6),   
				(int) (((double) lng / 1E5) * 1E6));   
		poly.add(p);  
	}  
	return poly;   
}

/**
 * 获取位置信息
 * @param address
 * @return
 */
public static JSONObject getLocationInfo(String address) {

	HttpGet httpGet = new HttpGet("http://maps.google."
			+ "com/maps/api/geocode/json?address=" + address
			+ "ka&sensor=false");
	HttpClient client = new DefaultHttpClient();
	HttpResponse response;
	StringBuilder stringBuilder = new StringBuilder();
	try {
		response = client.execute(httpGet);
		HttpEntity entity = response.getEntity();
		InputStream stream = entity.getContent();
		int b;
		while ((b = stream.read()) != -1) {
			stringBuilder.append((char) b);
		}
	} catch (ClientProtocolException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
	JSONObject jsonObject = new JSONObject();
	try {
		jsonObject = new JSONObject(stringBuilder.toString());
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return jsonObject;
}
	   public String _fformat(double value)
	   {
		   NumberFormat format = new DecimalFormat("###.##");
		   String s = format.format(value);
		   return s;
	   }

	
}