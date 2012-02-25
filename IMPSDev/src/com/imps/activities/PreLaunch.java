package com.imps.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.imps.IMPSDev;
import com.imps.R;
import com.imps.services.impl.ConfigurationService;
import com.imps.services.impl.ServiceManager;
import com.imps.ui.widget.CircleFlowIndicator;
import com.imps.ui.widget.GuideAdapter;
import com.imps.ui.widget.ViewFlow;

public class PreLaunch extends Activity{
	private static String TAG = PreLaunch.class.getCanonicalName();
	private static boolean DEBUG = IMPSDev.isDEBUG();
	private ViewFlow viewFlow;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if(DEBUG){Log.d(TAG,"onCreate...");}
		setContentView(R.layout.guide);
		setTitle("IMPS 更新说明");
		ensureUI();
	}
	public void ensureUI(){
		viewFlow = (ViewFlow) findViewById(R.id.viewflow);
		viewFlow.setSideBuffer(4);
		viewFlow.setAdapter(new GuideAdapter(this),0);
		CircleFlowIndicator indic = (CircleFlowIndicator) findViewById(R.id.viewflowindic);
		viewFlow.setFlowIndicator(indic);
	}

}
