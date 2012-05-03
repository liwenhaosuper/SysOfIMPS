package com.imps.ui.widget;

import android.app.ListActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.imps.ui.widget.i.Displayable;
import com.imps.ui.widget.i.Gesturable;

public abstract class ScrollTabListActivity extends ListActivity implements Displayable, Gesturable{
	  public static final String EXTRA_TAB_INDEX = "tab_index";
	  protected View.OnTouchListener contentViewTouchListener = new View.OnTouchListener()
	  {
	    public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
	    {
	      return ScrollTabListActivity.this.gestureDetector.onTouchEvent(paramMotionEvent);
	    }
	  };
	  protected GestureDetector gestureDetector;
	  private boolean onCreated = false;
	  protected ScrollTabHostActivity parent;
	  
	  protected void onStart()
	  {
	    super.onStart();
	    if (!this.onCreated)
	    {
	      this.parent = ((ScrollTabHostActivity)getParent());
	      int i = getIntent().getIntExtra("tab_index", -1);
	      this.parent.addChild(i, this);
	      this.gestureDetector = new GestureDetector(this, new ScrollTabHostActivity.TabOnGestureListener(this.parent));
	      getGestureExecutor().setOnTouchListener(new View.OnTouchListener()
	      {
	        public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
	        {
	          return ScrollTabListActivity.this.gestureDetector.onTouchEvent(paramMotionEvent);
	        }
	      });
	      this.onCreated = true;
	    }
	  }

	  protected void onStopNotFinish()
	  {
	    ((ScrollTabHostActivity)getParent()).onStopFinish = false;
	  }
	    
}
