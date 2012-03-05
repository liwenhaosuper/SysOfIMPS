package com.imps.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.imps.IMPSDev;
import com.imps.services.impl.ConfigurationService;
import com.imps.services.impl.ServiceManager;
/**
 * Copyright (C) 2010-2020 IMPS Development Team
 * @author liwenhaosuper
 *
 */
public class ServiceManagerReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if(Intent.ACTION_BOOT_COMPLETED.equals(action)){
			SharedPreferences settings = context.getSharedPreferences(IMPSDev.getContext().getPackageName(), 0);
			 if(settings != null && settings.getBoolean(ConfigurationService.GENERAL_AUTOSTART,true)){
					Intent i = new Intent(context, ServiceManager.class);
					i.putExtra("autostarted", true);
					context.startService(i);
			 }
		}
	}

}
