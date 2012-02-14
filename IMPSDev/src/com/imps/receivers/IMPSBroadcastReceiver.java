package com.imps.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.imps.IMPSDev;
import com.imps.basetypes.Constant;

public class IMPSBroadcastReceiver extends BroadcastReceiver{
	
	protected static boolean DEBUG = IMPSDev.isDEBUG();
	protected String TAG = this.getClass().getCanonicalName();
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if(Constant.EXIT.equals(action)){
			if(DEBUG) Log.d(TAG,"Exit broadcast...");
			if(context instanceof Activity){
				((Activity)context).finish();
			}else{
				if(DEBUG) Log.d(TAG,"Exit skip...");
			}
		}
		
	}
	//get the intent filter of the instance
	public IntentFilter getFilter(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.EXIT);
		return filter;
	}
}
