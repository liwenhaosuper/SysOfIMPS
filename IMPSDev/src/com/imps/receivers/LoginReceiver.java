package com.imps.receivers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import com.imps.R;
import com.imps.activities.FriendListTab;
import com.imps.basetypes.Constant;
import com.imps.services.impl.ServiceManager;

public class LoginReceiver extends IMPSBroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);
		if(intent.getAction().equals(Constant.LOGINERROR)){
			
		}else if(intent.getAction().equals(Constant.LOGINSUCCESS)){
			if(DEBUG){ Log.d(TAG, "Login success...");}
			ServiceManager.getmContact().sendFriListReq();
			Toast.makeText(context, context.getResources().getString(R.string.login_success), Toast.LENGTH_LONG);
			Intent start = new Intent(context,FriendListTab.class);
			context.startActivity(start);
			((Activity)context).finish();
		}
	}

	@Override
	public IntentFilter getFilter() {
		// TODO Auto-generated method stub
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.LOGINERROR);
		filter.addAction(Constant.LOGINSUCCESS);
		return filter;
	}
}
