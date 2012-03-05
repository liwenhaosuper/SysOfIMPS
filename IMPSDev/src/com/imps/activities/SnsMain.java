package com.imps.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.imps.IMPSDev;
import com.imps.R;
import com.imps.ui.widget.CircleFlowIndicator;
import com.imps.ui.widget.ViewFlow;
/**
 * 
 * Interface for using different sns service for user
 * @author liwenhaosuper
 *
 */
public class SnsMain extends Activity{
	private static String TAG = SnsMain.class.getCanonicalName();
	private static boolean DEBUG = false;//IMPSDev.isDEBUG();
	private ViewFlow viewFlow;
	private Map<Integer,ArrayList< SnsItem>> mItems = new HashMap<Integer,ArrayList< SnsItem>>();
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.snsmain);
		viewFlow = (ViewFlow) findViewById(R.id.viewflow);
		initData();
		viewFlow.setSideBuffer(3);
		viewFlow.setAdapter(new SnsMainAdapter(),0);
		CircleFlowIndicator indic = (CircleFlowIndicator) findViewById(R.id.viewflowindic);
		viewFlow.setFlowIndicator(indic);
	}
	public void initData(){
		//fun page
		ArrayList< SnsItem> mItem = new ArrayList< SnsItem>();
		//0
		SnsItem item0 = new SnsItem();
		item0.cls  = CoupleDoodle.class;
		item0.name = getResources().getString(R.string.coupledoodle);
		item0.sid = R.drawable.draw_icon;
		mItem.add(item0);
		//1
		SnsItem item1 = new SnsItem();
		item1.cls  = About.class;
		item1.name = "1";
		item1.sid = R.drawable.ic_launcher;
		mItem.add(item1);
		//2
		SnsItem item2 = new SnsItem();
		item2.cls  = About.class;
		item2.name = "2";
		item2.sid = R.drawable.ic_launcher;
		mItem.add(item2);
		//3
		SnsItem item3 = new SnsItem();
		item3.cls  = About.class;
		item3.name = "3";
		item3.sid = R.drawable.ic_launcher;
		mItem.add(item3);
		//4
		SnsItem item4 = new SnsItem();
		item4.cls  = About.class;
		item4.name = "4";
		item4.sid = R.drawable.ic_launcher;
		mItem.add(item4);
		//5
		SnsItem item5 = new SnsItem();
		item5.cls  = About.class;
		item5.name = "5";
		item5.sid = R.drawable.ic_launcher;
		mItem.add(item5);
		//6
		SnsItem item6 = new SnsItem();
		item6.cls  = About.class;
		item6.name = "6";
		item6.sid = R.drawable.ic_launcher;
		mItem.add(item6);
		//Add to the list
		mItems.put(new Integer(0), mItem);
		mItems.put(new Integer(1), mItem);
	}
	
	
	public class SnsMainAdapter extends BaseAdapter{
		private static final int GAME = 0;
		private static final int PEOPLE = 1;
		@Override
		public int getCount() {
			return mItems==null?0:mItems.size();
		}

		@Override
		public Object getItem(int position) {
			return mItems.get(new Integer(position));
		}
	    @Override
	    public int getItemViewType(int position) {
	        return position;
	    }
		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView==null){
				convertView = LayoutInflater.from(SnsMain.this).inflate(R.layout.snsmain_grid, null);
				int index = getItemViewType(position);
				GridView gridview = (GridView)convertView.findViewById(R.id.gridview);
				gridview.setAdapter(new ItemAdapter(mItems.get(new Integer(index))));
				
			}
			return convertView;
		}
	}
	public class SnsItem{
		public String name;  //name
		public int sid;   //icon
		public Class<?> cls;   //activity 
	}
	public class ItemAdapter extends BaseAdapter{
		private List<SnsItem> mItem = new ArrayList<SnsItem>();
		public ItemAdapter(List<SnsItem> items){
			mItem = items;
		}
		@Override
		public int getCount() {
			return mItem==null?0:mItem.size();
		}

		@Override
		public Object getItem(int position) {
			return mItem.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if(DEBUG)Log.d(TAG,"POS:"+position);
			convertView = LayoutInflater.from(SnsMain.this).inflate(R.layout.snsmain_griditem,null);
			RelativeLayout rl = (RelativeLayout) convertView.findViewById(R.id.relaGrid);
			ImageView image = (ImageView) rl.findViewById(R.id.chooseImage);
            TextView text = (TextView) rl.findViewById(R.id.chooseText);
            text.setText(mItem.get(position).name);
            image.setImageDrawable(getResources().getDrawable(mItem.get(position).sid));
            image.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(SnsMain.this,mItem.get(position).cls);
					intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					startActivity(intent);
				}});
			return rl;
		}
		
	}
}
