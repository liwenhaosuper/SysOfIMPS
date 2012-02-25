package com.imps.receivers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import com.imps.R;
import com.imps.activities.Register;
import com.imps.basetypes.Constant;

public class RegisterReceiver extends IMPSBroadcastReceiver{
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);
		if(intent.getAction().equals(Constant.REGISTERERROR)){
			Toast.makeText(context, context.getResources().getString(R.string.register_success), Toast.LENGTH_LONG);
			if(context.getClass().isInstance(Register.class)){
				((Activity)context).finish();
			}
		}else if(intent.getAction().equals(Constant.REGISTERSUCCESS)){
			String msg = intent.getStringExtra("errorMsg");
			if(msg==null||msg.equals("")){
				Toast.makeText(context, context.getResources().getString(R.string.register_fail), Toast.LENGTH_LONG);
			}else{
				Toast.makeText(context, msg, Toast.LENGTH_LONG);
			}
		}
	}
	@Override
	public IntentFilter getFilter() {
		// TODO Auto-generated method stub
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.REGISTERERROR);
		filter.addAction(Constant.REGISTERSUCCESS);
		return filter;
	}
}
