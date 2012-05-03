package com.imps.activities;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imps.R;
import com.imps.ui.widget.PopupMenu;
import com.imps.ui.widget.ScrollTabHostActivity;

public class SnsMain1 extends ScrollTabHostActivity implements AdapterView.OnItemClickListener{

	public static final String TAG = "SnsMain1";
	private PopupMenu popupMenu;
	//fun page
	List< AppPlugin> mItem = new ArrayList< AppPlugin>();
	@Override
	protected View getTabView(int paramInt) {
	    switch (paramInt){
	    case 0:case 1:case 2:
	        GridView localGridView = new GridView(this);
	        localGridView.setVerticalSpacing(10);
	        localGridView.setHorizontalSpacing(10);
	        localGridView.setStretchMode(2);
	        localGridView.setNumColumns(3);
	        localGridView.setGravity(3);
	        localGridView.setBackgroundResource(R.drawable.mid_bg_unit);
	        LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(-1, -1);
	        localLayoutParams.setMargins(5, 5, 5, 5);
	        localGridView.setLayoutParams(localLayoutParams);
	        localGridView.setAdapter(new PlugsGridViewAdapter(mItem));
	        return localGridView;
	    default:
	    	break;
	    }
	    return null;
	}

	@Override
	protected TabDomain[] initTabDomain() {
	    ScrollTabHostActivity.TabDomain[] arrayOfTabDomain = new ScrollTabHostActivity.TabDomain[3];
	    arrayOfTabDomain[0] = new ScrollTabHostActivity.TabDomain(0, "IMPS服务0");
	    arrayOfTabDomain[1] = new ScrollTabHostActivity.TabDomain(1, "IMPS服务1");
	    arrayOfTabDomain[2] = new ScrollTabHostActivity.TabDomain(2, "IMPS服务2");
	    return arrayOfTabDomain;
	}
	public void initData(){

		//0
		AppPlugin item0 = new AppPlugin();
		item0.cls  = CoupleDoodle.class;
		item0.name = getResources().getString(R.string.coupledoodle);
		item0.icon = R.drawable.draw_icon;
		mItem.add(item0);
		//1
		AppPlugin item1 = new AppPlugin();
		item1.cls  = About.class;
		item1.name = "1";
		item1.icon = R.drawable.ic_launcher;
		mItem.add(item1);
		//2
		AppPlugin item2 = new AppPlugin();
		item2.cls  = About.class;
		item2.name = "2";
		item2.icon = R.drawable.ic_launcher;
		mItem.add(item2);
		//3
		AppPlugin item3 = new AppPlugin();
		item3.cls  = About.class;
		item3.name = "3";
		item3.icon = R.drawable.ic_launcher;
		mItem.add(item3);
		//4
		AppPlugin item4 = new AppPlugin();
		item4.cls  = About.class;
		item4.name = "4";
		item4.icon = R.drawable.ic_launcher;
		mItem.add(item4);
		//5
		AppPlugin item5 = new AppPlugin();
		item5.cls  = About.class;
		item5.name = "5";
		item5.icon = R.drawable.ic_launcher;
		mItem.add(item5);
		//6
		AppPlugin item6 = new AppPlugin();
		item6.cls  = About.class;
		item6.name = "6";
		item6.icon = R.drawable.ic_launcher;
		mItem.add(item6);
	}
	@Override
	public void onCreate(Bundle paramBundle){
	    super.onCreate(paramBundle);
	    setContentView(R.layout.snsmain1);
	    initData();
	    ((ImageView)findViewById(R.id.menuBtn)).setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
        	  SnsMain1.this.menuOnClick(paramView);
          }
        });
        ((ImageView)findViewById(R.id.avatar)).setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
        	  startActivity(new Intent(SnsMain1.this,MyCard.class));
          }
        });
	}
	public void menuOnClick(View paramView){
    	if (this.popupMenu == null){
    	    int[] arrayOfInt2 = new int[2];
    	    paramView.getLocationInWindow(arrayOfInt2);
    	    PopupMenu.Builder localBuilder = new PopupMenu.Builder(this);
    	    Display localDisplay = getWindowManager().getDefaultDisplay();
    	    int i = (int)(0.45D * localDisplay.getWidth());
    	    int j = (int)(0.1D * localDisplay.getHeight());
    	    PopupMenu.PopupMenuDomain[] arrayOfPopupMenuDomain = new PopupMenu.PopupMenuDomain[2];
    	    arrayOfPopupMenuDomain[0] = new PopupMenu.PopupMenuDomain(0, R.drawable.setting, getString(R.string.setting), new View.OnClickListener(){
    	        public void onClick(View paramView){
    	            
    	        }
    	      });
    	    arrayOfPopupMenuDomain[1] = new PopupMenu.PopupMenuDomain(0, R.drawable.setting, getString(R.string.setting), new View.OnClickListener(){
    	        public void onClick(View paramView){
    	            
    	        }
    	      });
    	    localBuilder.setWidth(i);
    	    localBuilder.setHeight(j * arrayOfPopupMenuDomain.length);
    	    localBuilder.setLocationInWindow(arrayOfInt2);
    	    localBuilder.setPopupMenuDomains(arrayOfPopupMenuDomain);
    	    this.popupMenu = localBuilder.create();
    	    this.popupMenu.showAtLocation((View)paramView.getParent(), 51, arrayOfInt2[0], arrayOfInt2[1] + paramView.getHeight());
    	}else{
        	if(this.popupMenu.isShowing())
            {
              this.popupMenu.close();
            }else{
            	int[] arrayOfInt1 = new int[2];
                paramView.getLocationInWindow(arrayOfInt1);
                this.popupMenu.showAtLocation((View)paramView.getParent(), 51, arrayOfInt1[0], arrayOfInt1[1] + paramView.getHeight());
            }
    	}
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		
	}
	private class AppPlugin{
		AppPlugin(){
			
		}
		AppPlugin(String nm,int id){
			this.name = nm;this.icon = id;
		}
		AppPlugin(String nm,int id,Class<?> cls){
			this.name = nm;this.icon = id;this.cls = cls;
		}
		private String name;
		private int icon;
		public Class<?> cls;   //activity 
	}

	private class PlugsGridViewAdapter extends BaseAdapter
	  {
	    private List<AppPlugin> appPlugs;
	    private LayoutInflater inflater;
	    private PackageManager packageManager;
	    private int updatable = 0;

	    public PlugsGridViewAdapter(){
	    }
	    public PlugsGridViewAdapter(List<AppPlugin> data){
	    	appPlugs = data;
	    }
	    public int getCount()
	    {
	      return this.appPlugs==null?0:this.appPlugs.size();
	    }

	    public AppPlugin getItem(int paramInt)
	    {
	      return (AppPlugin)this.appPlugs.get(paramInt);
	    }

	    public long getItemId(int paramInt)
	    {
	      return paramInt;
	    }

	    public View getView(final int paramInt, View paramView, ViewGroup paramViewGroup)
	    {
	      Context localContext = paramViewGroup.getContext();
	      if (this.inflater == null)
	        this.inflater = ((LayoutInflater)localContext.getSystemService("layout_inflater"));
	      if (paramView == null)
	        paramView = this.inflater.inflate(R.layout.plugs_gridview_item, null);
	      ImageView localImageView1 = (ImageView)paramView.findViewById(R.id.plugsIcon);
	      localImageView1.setBackgroundResource(appPlugs.get(paramInt).icon);
	      TextView localTextView = (TextView)paramView.findViewById(R.id.plugsName);
	      localTextView.setText(appPlugs.get(paramInt).name);
	      localImageView1.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(SnsMain1.this,mItem.get(paramInt).cls);
					intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					startActivity(intent);
				}});
	      return paramView;
	    }
	  }
}
