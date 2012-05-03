package com.imps.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;

import com.imps.R;
import com.imps.ui.widget.PopupMenu;
import com.imps.ui.widget.ScrollTabHostActivity;

public class FriendContainer1 extends ScrollTabHostActivity{
	public static final String TAG = "FriendContainer1";
	private PopupMenu popupMenu;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friendcontainer1);
        ((ImageView)findViewById(R.id.menuBtn)).setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
        	  FriendContainer1.this.menuOnClick(paramView);
          }
        });
        ((ImageView)findViewById(R.id.avatar)).setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
        	  startActivity(new Intent(FriendContainer1.this,MyCard.class));
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
	protected View getTabView(int paramInt) {
		return null;
	}

	@Override
	protected TabDomain[] initTabDomain() {
//	    ScrollTabHostActivity.TabDomain[] arrayOfTabDomain = new ScrollTabHostActivity.TabDomain[3];
//	    arrayOfTabDomain[0] = new ScrollTabHostActivity.TabDomain(0, getResources().getString(R.string.currentsession));
//	    arrayOfTabDomain[1] = new ScrollTabHostActivity.TabDomain(1, getResources().getString(R.string.friendlist));
//	    arrayOfTabDomain[2] = new ScrollTabHostActivity.TabDomain(2, getResources().getString(R.string.sysmsg));
//	    return arrayOfTabDomain;
	    int i = getIntent().getIntExtra("start_from", 0);
	    ScrollTabHostActivity.TabDomain[] arrayOfTabDomain = new ScrollTabHostActivity.TabDomain[3];
	    Intent localIntent1 = new Intent();
	    localIntent1.putExtra("start_from", i);
	    localIntent1.setClass(this, CurrentSessions.class);
	    arrayOfTabDomain[0] = new ScrollTabHostActivity.ActivityTabDomain(0, getResources().getString(R.string.currentsession), localIntent1);
	    Intent localIntent2 = new Intent();
	    localIntent2.putExtra("start_from", i);
	    localIntent2.setClass(this, FriendListTab.class);
	    arrayOfTabDomain[1] = new ScrollTabHostActivity.ActivityTabDomain(1, getResources().getString(R.string.friendlist), localIntent2);
	    Intent localIntent3 = new Intent();
	    localIntent3.putExtra("start_from", i);
	    localIntent3.setClass(this, SystemMsg.class);
	    arrayOfTabDomain[2] = new ScrollTabHostActivity.ActivityTabDomain(2, getResources().getString(R.string.sysmsg), localIntent3);
	    return arrayOfTabDomain;
	}
	
	

	
}
