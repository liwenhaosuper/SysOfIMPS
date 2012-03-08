package com.imps.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;

import com.imps.IMPSActivity;
import com.imps.R;
import com.imps.ui.widget.CircleFlowIndicator;
import com.imps.ui.widget.ViewFlow;

public class FaceDialog extends IMPSActivity {
	
	private ViewFlow viewFlow1, viewFlow2;
	private int[][] faceNum = {{R.drawable.exp_01, R.drawable.exp_02,
			R.drawable.exp_03, R.drawable.exp_04, R.drawable.exp_05, 
			R.drawable.exp_06, R.drawable.exp_07, R.drawable.exp_08, 
			R.drawable.exp_09, R.drawable.exp_10, R.drawable.exp_11, 
			R.drawable.exp_12, R.drawable.exp_13, R.drawable.exp_14, 
			R.drawable.exp_15, R.drawable.exp_16, R.drawable.exp_17, 
			R.drawable.exp_18, R.drawable.exp_19, R.drawable.exp_20, 
			R.drawable.exp_21, R.drawable.exp_22, R.drawable.exp_23, 
			R.drawable.exp_24, R.drawable.exp_25, R.drawable.exp_26},
			{R.drawable.e001, R.drawable.e002, R.drawable.e003, R.drawable.e004,
			R.drawable.e005, R.drawable.e00d, R.drawable.e00e, R.drawable.e00f,
			R.drawable.e010, R.drawable.e011, R.drawable.e012, R.drawable.e020,
			R.drawable.e021, R.drawable.e022, R.drawable.e023, R.drawable.e036,
			R.drawable.e038, R.drawable.e03e, R.drawable.e048, R.drawable.e049,
			R.drawable.e04a, R.drawable.e04b, R.drawable.e04c, R.drawable.e04e,
			R.drawable.e056, R.drawable.e057, R.drawable.e058, R.drawable.e059,
			R.drawable.e05a, R.drawable.e105, R.drawable.e106, R.drawable.e107,
			R.drawable.e108, R.drawable.e10c, R.drawable.e111, R.drawable.e115,
			R.drawable.e117, R.drawable.e11a, R.drawable.e11c, R.drawable.e11d,
			R.drawable.e13c, R.drawable.e13d, R.drawable.e14c, R.drawable.e14d,
			R.drawable.e152, R.drawable.e153, R.drawable.e155, R.drawable.e156,
			R.drawable.e157, R.drawable.e201, R.drawable.e21c, R.drawable.e21d,
			R.drawable.e21e, R.drawable.e21f, R.drawable.e220, R.drawable.e221,
			R.drawable.e222, R.drawable.e22e, R.drawable.e22f, R.drawable.e230,
			R.drawable.e231, R.drawable.e253, R.drawable.e31d, R.drawable.e31e,
			R.drawable.e31f, R.drawable.e326, R.drawable.e327, R.drawable.e328,
			R.drawable.e329, R.drawable.e32a, R.drawable.e32b, R.drawable.e32c,
			R.drawable.e32d, R.drawable.e32e, R.drawable.e32f, R.drawable.e330,
			R.drawable.e331, R.drawable.e334, R.drawable.e401, R.drawable.e402,
			R.drawable.e403, R.drawable.e404, R.drawable.e405, R.drawable.e406,
			R.drawable.e407, R.drawable.e408, R.drawable.e409, R.drawable.e40a,
			R.drawable.e40b, R.drawable.e40c, R.drawable.e40d, R.drawable.e40e,
			R.drawable.e40f, R.drawable.e410, R.drawable.e411, R.drawable.e412,
			R.drawable.e413, R.drawable.e414, R.drawable.e415, R.drawable.e416,
			R.drawable.e417, R.drawable.e418, R.drawable.e419, R.drawable.e41a,
			R.drawable.e41b, R.drawable.e41c, R.drawable.e41d, R.drawable.e41e,
			R.drawable.e41f, R.drawable.e420, R.drawable.e421, R.drawable.e422,
			R.drawable.e423, R.drawable.e424, R.drawable.e425, R.drawable.e426,
			R.drawable.e427, R.drawable.e428, R.drawable.e429, R.drawable.e436,
			R.drawable.e437, R.drawable.e438, R.drawable.e439, R.drawable.e43a,
			R.drawable.e43b, R.drawable.e443, R.drawable.e515, R.drawable.e516,
			R.drawable.e517, R.drawable.e518, R.drawable.e519, R.drawable.e51a,
			R.drawable.e51b, R.drawable.e51c, R.drawable.e51e, R.drawable.e51f,
			R.drawable.e536}};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.face_dialog);
//		init();
		DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        getWindow().setLayout(metrics.widthPixels, metrics.heightPixels*62/100);
        
		TabHost tabHost = (TabHost) findViewById(R.id.tabhost);  
        tabHost.setup(); 
        TabWidget tabWidget = tabHost.getTabWidget();
          
        tabHost.addTab(tabHost.newTabSpec("tab1")  
                .setIndicator(getResources().getString(R.string.default_smiley))  
                .setContent(R.id.layout1));  
          
        tabHost.addTab(tabHost.newTabSpec("tab2")  
                .setIndicator(getResources().getString(R.string.emoji_smiley)) 
                .setContent(R.id.layout2));
        tabHost.setCurrentTab(1);
        int count = tabWidget.getChildCount();
        for(int i=0; i < count; i ++){
        	View view = tabWidget.getChildTabViewAt(i);
            view.getLayoutParams().height = 50;
        }
        
		viewFlow1 = (ViewFlow) findViewById(R.id.viewflow1);
		viewFlow1.setSideBuffer(faceNum[0].length / 40 + 1);
		viewFlow1.setAdapter(new FaceAdapter(faceNum[0]),0);
		CircleFlowIndicator indic1 = (CircleFlowIndicator) findViewById(R.id.viewflowindic1);
		viewFlow1.setFlowIndicator(indic1);
		
		viewFlow2 = (ViewFlow) findViewById(R.id.viewflow2);
		viewFlow2.setSideBuffer(faceNum[1].length / 40 + 1);
		viewFlow2.setAdapter(new FaceAdapter(faceNum[1]),0);
		CircleFlowIndicator indic2 = (CircleFlowIndicator) findViewById(R.id.viewflowindic2);
		viewFlow2.setFlowIndicator(indic2);	
	}
	@Override
	public void onStop(){
		super.onStop();
	}
	public class FaceAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		private int[][] ids;
		private int length;
		private final int pageSize = 40;
		public FaceAdapter()
		{
		}
		public FaceAdapter(int[] face) {
			// TODO Auto-generated constructor stub
			mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			int pages = face.length / pageSize + 1;
			length = face.length;
			ids = new int[pages][];
			for(int i = 0; i < pages; i++){
				ids[i] = new int[pageSize];
			}
			for(int j = 0; j < face.length; j++)
			{
				ids[j / pageSize][j % pageSize] = face[j];
			}
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
				convertView = mInflater.inflate(R.layout.face_grid, null);
			}
			GridView gv = (GridView)convertView.findViewById(R.id.face_view);
			gv.setAdapter(new ExpressionAdapter(position));
			gv.setEnabled(true);  
			final int pos = position;
		    gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		    	@Override  
	            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) { 
		    		if (pos * pageSize + arg2 < length){
			    		Intent i = new Intent();
			    		i.putExtra("selectedFace", ids[pos][arg2]);
			    		setResult(0, i);
			    		finish();
		    		}
	            }  
		    }); 
			return convertView;
		}
		
		public class ExpressionAdapter extends BaseAdapter{
		
				private int pos;
				public ExpressionAdapter(int position) {
					// TODO Auto-generated constructor stub
					pos = position;
				}

				@Override
				public int getCount() {
					// TODO Auto-generated method stub
					return ids[pos].length;
				}
		
				@Override
				public Object getItem(int position) {
					// TODO Auto-generated method stub
					return getResources().getDrawable(ids[pos][position]);
				}
		
				@Override
				public long getItemId(int position) {
					// TODO Auto-generated method stub
					return ids[pos][position];
				}
		
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					// TODO Auto-generated method stub
					ImageView i;
		
					if (convertView == null) {
						i = new ImageView(FaceDialog.this);
						i.setScaleType(ImageView.ScaleType.FIT_CENTER);
						i.setLayoutParams(new GridView.LayoutParams(48, 48));
					} else {
						i = (ImageView) convertView;
					}
					i.setImageResource(ids[pos][position]);
					
					return i;
				}
				
			}

	}

}
