package com.imps.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.imps.R;
import com.imps.activities.IMPSContainer;
import com.imps.activities.Login;
import com.imps.services.impl.ConfigurationService;
import com.imps.services.impl.ServiceManager;

public class GuideAdapter extends BaseAdapter {
	
	private LayoutInflater mInflater;
	private static final int[] ids = { R.drawable.guide1, R.drawable.guide2, R.drawable.guide3, R.drawable.guide4};
	private static Context mContext;
	public GuideAdapter(Context context){
		mContext = context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return ids.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return ids[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.guide_imageview, null);
		}
		((ImageView) convertView.findViewById(R.id.imgView)).setImageResource(ids[position]);
		if(position==ids.length-1){
			convertView.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(ServiceManager.isStarted&&ServiceManager.getmAccount().isLogined()){
					startMainActivity();
				}else{
					startLoginActivity();
				}
			}});
			
		}
		return convertView;
	}
	public void startLoginActivity(){
        Editor editor = ServiceManager.getmConfig().getPreferences().edit();
        editor.putBoolean(ConfigurationService.PREFERENCE_SHOW_PRELAUNCH_ACTIVITY, false);
        editor.commit();
        Intent intent = new Intent(mContext, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
        ((Activity)mContext).finish();
	}
	public void startMainActivity(){
        Editor editor = ServiceManager.getmConfig().getPreferences().edit();
        editor.putBoolean(ConfigurationService.PREFERENCE_SHOW_PRELAUNCH_ACTIVITY, false);
        editor.commit();
        Intent intent = new Intent(mContext, IMPSContainer.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
        ((Activity)mContext).finish();
	}
}
