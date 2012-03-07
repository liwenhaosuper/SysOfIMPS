package com.imps.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.imps.IMPSDev;
import com.imps.R;

public class FaceDialog extends Activity {
	private static String TAG = FaceDialog.class.getCanonicalName();
	private static boolean DEBUG = IMPSDev.isDEBUG();
	private Window window = null;
	private GridView gv;
	private int[] faceNum = {R.drawable.exp_01, R.drawable.exp_02,
			R.drawable.exp_03, R.drawable.exp_04, R.drawable.exp_05, 
			R.drawable.exp_06, R.drawable.exp_07, R.drawable.exp_08, 
			R.drawable.exp_09, R.drawable.exp_10, R.drawable.exp_11, 
			R.drawable.exp_12, R.drawable.exp_13, R.drawable.exp_14, 
			R.drawable.exp_15, R.drawable.exp_16, R.drawable.exp_17, 
			R.drawable.exp_18, R.drawable.exp_19, R.drawable.exp_20, 
			R.drawable.exp_21, R.drawable.exp_22, R.drawable.exp_23, 
			R.drawable.exp_24, R.drawable.exp_25, R.drawable.exp_26};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.face_grid);
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		setProperty();
		initGridView();
	}

	private void setProperty() {
		// TODO Auto-generated method stub
		//window = getWindow();				//获得对话框窗口
		//window.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.left));
      //  WindowManager.LayoutParams wmlp = window.getAttributes();
      //  wmlp.alpha = 0.0f;					//设置透明度
      //  wmlp.dimAmount = 0.0f;				//设置黑度
       // wmlp.gravity = Gravity.CENTER;		//对话框位置
     //   window.setAttributes(wmlp); 
	}
	
	private void initGridView() {
		// TODO Auto-generated method stub
		gv = (GridView)findViewById(R.id.face_view);
		gv.setAdapter(new FaceAdapter());
		gv.setEnabled(true);  
	    gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	    	@Override  
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {  
	    		Intent i = new Intent();
	    		i.putExtra("selectedFace", faceNum[arg2]);
	    		if(DEBUG) Log.d(TAG,"face:"+faceNum[arg2]);
	    		setResult(0, i);
	    		finish();
            }  
	    });  
	}
	
	public class FaceAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return faceNum.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return getResources().getDrawable(faceNum[position]);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return faceNum[position];
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ImageView i;

			if (convertView == null) {
				i = new ImageView(FaceDialog.this);
				i.setScaleType(ImageView.ScaleType.FIT_CENTER);
				i.setLayoutParams(new GridView.LayoutParams(24, 24));
			} else {
				i = (ImageView) convertView;
			}
			i.setImageResource(faceNum[position]);
			
			return i;
		}
		
	}

}
