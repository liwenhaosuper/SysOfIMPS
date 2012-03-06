package com.imps.activities;

import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BlurMaskFilter;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.imps.IMPSDev;
import com.imps.R;
import com.imps.basetypes.Constant;
import com.imps.net.handler.MessageFactory;
import com.imps.net.handler.UserManager;
import com.imps.receivers.IMPSBroadcastReceiver;
import com.imps.services.impl.DoodleSenderService;
import com.imps.services.impl.ServiceManager;
import com.imps.ui.BubbleDialog;
import com.imps.ui.DoodleView;

public class CoupleDoodle extends Activity implements OnClickListener{
	private static boolean DEBUG = IMPSDev.isDEBUG();
	private static String TAG = CoupleDoodle.class.getCanonicalName();
    private Paint       mPaint;
    private MaskFilter  mEmboss;
    private MaskFilter  mBlur;
    private DoodleView mDoodle;
    private Button mInviteFri;
    private TextView mFriList;
    private TextView mTitle;
    private TextView mConnectionStatus;
    private PopupWindow popupWindow;
    private View popViewItem;
    private TextView notify_text;
    public static String roomMaster = "";
    private List<String> friList = new ArrayList<String>();
    private InvitingProgress mInvitetask;
    private DoodleSenderService mSender = new DoodleSenderService();
    private CoupleDoodleReceiver receiver = new CoupleDoodleReceiver();
    public static List<String> onlineFriends = new ArrayList<String>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.coupledoodle);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.coupledoodle_title);
        
        mInviteFri = (Button)findViewById(R.id.inviteFriend);
        mInviteFri.setOnClickListener(this);
        
        mFriList = (TextView)findViewById(R.id.friendlist);
        mTitle = (TextView) findViewById(R.id.title_left_text);
        mConnectionStatus = (TextView) findViewById(R.id.title_right_text);
        //mConnectionStatus.setText(getResources().getString(R.string.not_connected));
        
		popViewItem = this.getLayoutInflater().inflate(R.layout.coupledoodle_notifyview, null);
		popupWindow = new PopupWindow(popViewItem, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	    popupWindow.setTouchable(true);
	    popupWindow.setFocusable(false);
	    popViewItem.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(popupWindow!=null&&popupWindow.isShowing())
				{
					popupWindow.dismiss();
				}
			}});
	    notify_text = (TextView)popViewItem.findViewById(R.id.notify_text);
        
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFF0000FF);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(8);        
        mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 },
                                       0.4f, 6, 3.5f);
        
        friList.add(UserManager.getGlobaluser().getUsername());
        mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);
        
        mDoodle = (DoodleView)findViewById(R.id.doodle_view);
        mDoodle.init(mPaint);
        mDoodle.setmSender(mSender);
        mDoodle.setClickable(false);
        updateUserListText();
    }
    public void updateUserListText(){
    	String text = getResources().getString(R.string.couplelist);
    	for(int i=0;i<friList.size();i++){
    		text+=friList.get(i)+";";
    	}
    	mFriList.setText(text);
    }
    public void showNotify(String text){
    	notify_text.setText(text);
    	popupWindow.showAsDropDown(mInviteFri);
    }
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if(popupWindow!=null&&popupWindow.isShowing())
		{
			popupWindow.dismiss();
		}
		return super.onTouchEvent(event);
	}
    @Override
    public void onResume(){
    	super.onResume();
    	ServiceManager.getmDoodleService().startDoodle(mDoodle);
    	registerReceiver(receiver,receiver.getFilter());
    	if(ServiceManager.getmDoodleService().getChannel()!=null&&ServiceManager.getmDoodleService().getChannel().isConnected()){
    		ServiceManager.getmDoodleService().getChannel().write(ChannelBuffers.wrappedBuffer(
    				MessageFactory.createCDoodleLogin(UserManager.getGlobaluser().getUsername()).build()));
    		if(DEBUG)  Log.d(TAG,"Logged in...");
    		mConnectionStatus.setText(getResources().getString(R.string.connected));
    	}else{
    		if(DEBUG) Log.d(TAG,"Logged in failed...");
    		mConnectionStatus.setText(getResources().getString(R.string.not_connected));
    	}
    }
    @Override
    public void onStop(){
    	super.onStop();
    	mSender.stopSending();
    	if(mSender.getState()==Thread.State.RUNNABLE){
    		mSender.stop();
    	}
    	ServiceManager.getmDoodleService().stopDoodle();
    	unregisterReceiver(receiver);
    }

	@Override
	public void onClick(View v) {
		if(v.getId()==mInviteFri.getId()){
			final String[] multiChoiceItems = new String[onlineFriends.size()];
			final boolean[] defaultSelectedStatus = new boolean[UserManager.AllFriList.size()];
			for(int i=0;onlineFriends!=null&&i<onlineFriends.size();i++){
				defaultSelectedStatus[i] = false;
				multiChoiceItems[i] = onlineFriends.get(i);
			}
			new AlertDialog.Builder(CoupleDoodle.this)
				.setTitle(getResources().getString(R.string.select_friends))
				.setMultiChoiceItems(multiChoiceItems, defaultSelectedStatus, new OnMultiChoiceClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which,
							boolean isChecked) {
						defaultSelectedStatus[which] = isChecked;
					}})
				.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String []sb = new String[defaultSelectedStatus.length];
						int index = 0;
						for(int i=0;i<defaultSelectedStatus.length;i++) {  
		                    if(defaultSelectedStatus[i]) {  
		                        sb[index++] =multiChoiceItems[i];  
		                    }  
		                }
						if(index==0){
							return;
						}

						if(mInvitetask!=null&&mInvitetask.getStatus()==AsyncTask.Status.RUNNING){
							mInvitetask.cancel(true);
						}
						String[] list = new String[index];
						for(int i=0;i<index;i++){
							list[i] = sb[i];
						}
						roomMaster = UserManager.getGlobaluser().getUsername();
						mInvitetask = new InvitingProgress();
						mInvitetask.execute(list);
						
					}})
				.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}})
				.show();
				
			
			

		}
	}
	public class InvitingProgress extends AsyncTask<String,Void,Integer>{
		@Override
		protected Integer doInBackground(String... params) {
	    	if(ServiceManager.getmDoodleService().getChannel()!=null&&ServiceManager.getmDoodleService().getChannel().isConnected()){
	    		ServiceManager.getmDoodleService().getChannel().write(ChannelBuffers.wrappedBuffer(
	    				MessageFactory.createCDoodleReq(UserManager.getGlobaluser().getUsername(),params).build()));
	    		return new Integer(1);
	    	}else{
	    		return new Integer(0);
	    	}
		}
		@Override
		protected void onPreExecute(){
			Toast.makeText(CoupleDoodle.this, getResources().getString(R.string.invition_sending), Toast.LENGTH_SHORT).show();
		}
		@Override 
		protected void onPostExecute(Integer res){
			if(res.intValue()==0){
				Toast.makeText(CoupleDoodle.this, getResources().getString(R.string.invition_failed), Toast.LENGTH_SHORT).show();
				mConnectionStatus.setText(getResources().getString(R.string.not_connected));
			}else{
				Toast.makeText(CoupleDoodle.this, getResources().getString(R.string.invition_success), Toast.LENGTH_SHORT).show();
				mConnectionStatus.setText(getResources().getString(R.string.connected));
			}
		}
	}
	public class CoupleDoodleReceiver extends IMPSBroadcastReceiver{
		@Override
		public void onReceive(Context context,Intent intent){
			super.onReceive(context, intent);
			if(intent.getAction().equals(Constant.DOODLEREQ)){
				if(DEBUG) Log.d(TAG,"Doodle req...");
				final String frireq = intent.getStringExtra(Constant.USERNAME);
				final BubbleDialog dialog = new BubbleDialog(CoupleDoodle.this);
		        dialog.build(getResources().getString(R.string.friend_invite_you_for_coupledoodle,frireq),getResources().getString(R.string.accept),getResources().getString(R.string.deny),new View.OnClickListener() {					
					@Override
					public void onClick(View v) {
						roomMaster = frireq;
						mTitle.setText(getResources().getString(R.string.roommaster,roomMaster));
						friList.add(roomMaster);
						if(!mSender.isStarted()){
							mSender.start();
							
							mDoodle.setClickable(true);
						}
						updateUserListText();
						if(ServiceManager.getmDoodleService().getChannel()!=null&&ServiceManager.getmDoodleService().getChannel().isConnected()){
				    		ServiceManager.getmDoodleService().getChannel().write(ChannelBuffers.wrappedBuffer(
				    				MessageFactory.createCDoodleRsp(UserManager.getGlobaluser().getUsername(),roomMaster,true).build()));
						}
						dialog.cancel();
					}
				},new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if(ServiceManager.getmDoodleService().getChannel()!=null&&ServiceManager.getmDoodleService().getChannel().isConnected()){
				    		ServiceManager.getmDoodleService().getChannel().write(ChannelBuffers.wrappedBuffer(
				    				MessageFactory.createCDoodleRsp(UserManager.getGlobaluser().getUsername(),roomMaster,false).build()));
						}
						dialog.cancel();
					}
				});
		        dialog.show();
			}else if(intent.getAction().equals(Constant.DOODLERSP)){
				String frirsp = intent.getStringExtra(Constant.USERNAME);
				boolean res = intent.getBooleanExtra(Constant.RESULT, true);
				String resstr = res==true?getResources().getString(R.string.accept):getResources().getString(R.string.deny);
		        if(DEBUG) Log.d(TAG,"Doodle rsp..."+res);
		        showNotify(getResources().getString(R.string.friend_rsp_yourinvite,frirsp,resstr));
		        if(res){
		        	friList.add(frirsp);
		        	updateUserListText();
					if(!mSender.isStarted()){
						mSender.start();
						mDoodle.setClickable(true);
						if(DEBUG) Log.d(TAG,"Start sender...");
					}else{
						if(DEBUG) Log.d(TAG,"not Start sender...");
					}
		        }
		        updateUserListText();
			}else if(intent.getAction().equals(Constant.DOODLELIST)){
				if(DEBUG) Log.d(TAG,"Doodle list");
			}else if(intent.getAction().equals(Constant.DOODLESTATUS)){
				String frirsp = intent.getStringExtra(Constant.USERNAME);
				boolean res = intent.getBooleanExtra(Constant.RESULT, true);
				if(DEBUG) Log.d(TAG,"Doodle notify");
				if(onlineFriends.contains(frirsp)){
					if(res==false){
						onlineFriends.remove(frirsp);
					}
				}else if(res==true){
					onlineFriends.add(frirsp);
				}
				if(friList.contains(frirsp)&&res==false){
					friList.remove(frirsp);
					updateUserListText();
				}
				//TODO: TO be continued...
			}
		}
		@Override
		public IntentFilter getFilter(){
			IntentFilter filter = super.getFilter();
			filter.addAction(Constant.DOODLEREQ);
			filter.addAction(Constant.DOODLERSP);
			filter.addAction(Constant.DOODLELIST);
			filter.addAction(Constant.DOODLESTATUS);
			return filter;
		}
	}

}
