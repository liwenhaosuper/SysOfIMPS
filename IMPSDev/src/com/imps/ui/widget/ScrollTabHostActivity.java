package com.imps.ui.widget;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewAnimator;
import com.imps.R;
public abstract class ScrollTabHostActivity extends ActivityGroup{
	
	private static boolean DEBUG = true;
	public static final String EXTRA_DISPLAY_WHICH = "display_which";
	protected Activity[] childs;
	protected int curDisplayIndex = 0;
	protected int defaultAnimDuration = 300;
	protected Animation fadeLeftIn;
	protected Animation fadeLeftOut;
	protected Animation fadeRightIn;
	protected Animation fadeRightOut;
	private boolean onCreated = false;
	protected boolean onStopFinish = true;
	protected LinearLayout pageFlags;
	protected ImageView selectedIcon;
	protected TabDomain[] tabDomains;
	private int tabWidth;
	protected ViewAnimator tabcontent;
	protected RelativeLayout tabhost;
	protected LinearLayout tabs;
	private ScrollFlagImageAnimListener transAnimListener;
	
	private void displayDefaultTab(TabDomain paramTabDomain){
	    ImageView localImageView = this.selectedIcon;
	    int i = paramTabDomain.getIndex() * this.tabWidth;
	    TranslateAnimation localTranslateAnimation = new TranslateAnimation(0, -getWindowManager().getDefaultDisplay().getWidth(), 0, i, 2, 0.0F, 2, 0.0F);
	    localTranslateAnimation.setDuration(1000L);
	    localTranslateAnimation.setFillAfter(true);
	    if (this.transAnimListener == null)
	      this.transAnimListener = new ScrollFlagImageAnimListener();
	    this.transAnimListener.setCurIndex(-1);
	    localTranslateAnimation.setAnimationListener(this.transAnimListener);
	    ViewGroup.LayoutParams localLayoutParams = localImageView.getLayoutParams();
	    localLayoutParams.width = this.tabWidth;
	    localImageView.setLayoutParams(localLayoutParams);
	    localImageView.startAnimation(localTranslateAnimation);
	    displayTabContent(paramTabDomain);
	    this.curDisplayIndex = paramTabDomain.getIndex();
	    System.out.println("displayDefaultTab:"+this.curDisplayIndex);
	    ((ImageView)this.pageFlags.getChildAt(this.curDisplayIndex)).setImageResource(R.drawable.page_flag_enable);
	}
	private final void findAllView(){
		RelativeLayout localRelativeLayout = (RelativeLayout)findViewById(android.R.id.tabhost);
	    this.tabhost = localRelativeLayout;
	    ImageView localImageView = (ImageView)findViewById(android.R.id.selectedIcon);
	    this.selectedIcon = localImageView;
	    LinearLayout localLinearLayout1 = (LinearLayout)findViewById(android.R.id.tabs);
	    this.tabs = localLinearLayout1;
	    ViewAnimator localViewAnimator = (ViewAnimator)findViewById(android.R.id.tabcontent);
	    this.tabcontent = localViewAnimator;
	    LinearLayout localLinearLayout2 = (LinearLayout)findViewById(android.R.id.hint);
	    this.pageFlags = localLinearLayout2;
	    this.tabcontent.setLongClickable(true);
	}
	protected void addChild(int paramInt, Activity paramActivity){
		if(DEBUG) System.out.println("Add Child");
		this.childs[paramInt] = paramActivity;
	}
	protected void displayNext(){
	    int i = this.tabDomains.length;
	    if (this.curDisplayIndex < i - 1)
	    {
	      int j = 1 + this.curDisplayIndex;
	      scrollTo(j);
	      displayTabContent(this.tabDomains[j]);
	      this.curDisplayIndex = j;
	    }
	}
	protected void displayPrevious(){
	    if (this.curDisplayIndex > 0)
	    {
	      int i = this.curDisplayIndex - 1;
	      scrollTo(i);
	      displayTabContent(this.tabDomains[i]);
	      this.curDisplayIndex = i;
	    }
	}
	protected void displayTabContent(TabDomain paramTabDomain){
		int i = paramTabDomain.getIndex();
		View localView = null;
		if(DEBUG) System.out.println("displayTabContent"+i);
		if (!paramTabDomain.launched){
		    if (!(paramTabDomain instanceof ActivityTabDomain)){
		    	localView = getTabView(i);
				final GestureDetector detector = new GestureDetector(this, new TabOnGestureListener(this));
		        localView.setOnTouchListener(new View.OnTouchListener(){
		          public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
		          {		     
		        	  if(DEBUG) System.out.println("onTouch");
		              return detector.onTouchEvent(paramMotionEvent);
		          }
		        });
		    }else{
		    	Intent localIntent = ((ActivityTabDomain)paramTabDomain).getIntent();
			    localIntent.putExtra("tab_index", i);
			    if(DEBUG) System.out.println("tab_index:"+i);
			    localView = getLocalActivityManager().startActivity(String.valueOf(i), localIntent).getDecorView();
		    }
		    localView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
		    ((LinearLayout)this.tabcontent.getChildAt(i)).addView(localView);
		    paramTabDomain.setLaunched(true);
		}
	    if (this.fadeLeftIn == null)
            this.fadeLeftIn = AnimationUtils.loadAnimation(this, R.anim.translate_left_in);
        if (this.fadeRightOut == null)
            this.fadeRightOut = AnimationUtils.loadAnimation(this, R.anim.translate_left_out);
        this.tabcontent.setInAnimation(this.fadeLeftIn);
        this.tabcontent.setOutAnimation(this.fadeRightOut);
	    if (i != this.curDisplayIndex)
	    {
	        this.tabcontent.setDisplayedChild(i);
	    }else{
	    	try{
		    	if(DEBUG) System.out.println("displayTabContent:"+i+":activity:"+this.childs.length);
		    	((com.imps.ui.widget.i.Displayable)this.childs[i]).onDisplayed();
	    	}catch(NullPointerException e){
	    		return;
	    	}
	    }
	   
	}
	protected abstract View getTabView(int paramInt);
	protected abstract TabDomain[] initTabDomain();
	protected void initTabs(){
	    this.tabDomains = initTabDomain();
	    ScrollTabOnClickLisnter localScrollTabOnClickLisnter = new ScrollTabOnClickLisnter();
	    int i = getWindowManager().getDefaultDisplay().getWidth();
	    int j = this.tabDomains.length;
	    this.tabWidth = (i / j);
	    if (j<=0)
	    {
	      this.childs = new Activity[0];
	      return;
	    } 
	    for(int x=0;x<this.tabDomains.length;x++){
	    	ViewGroup.LayoutParams localLayoutParams1 = new ViewGroup.LayoutParams(this.tabWidth, -1);
	    	TabDomain localTabDomain = this.tabDomains[x];
	    	
	    	TextView localTextView = new TextView(this);
		    localTextView.setText(localTabDomain.getTitle());
	    	localTextView.setLayoutParams(localLayoutParams1);
	  	    localTextView.setGravity(17);
	  	    localTextView.setSingleLine(true);
	  	    localTextView.setPadding(10, 0, 10, 0);
	  	    localTextView.setTextColor(-1);
	  	    localTextView.setTag(localTabDomain);
	  	    localTextView.setOnClickListener(localScrollTabOnClickLisnter);
	  	    this.tabs.addView(localTextView);
	  	    
	  	    LinearLayout localLinearLayout = new LinearLayout(this);
	  	    ViewGroup.LayoutParams localLayoutParams2 = new ViewGroup.LayoutParams(-1, -1);
	  	    this.tabcontent.addView(localLinearLayout,x, localLayoutParams2);
	  	    ImageView localImageView = new ImageView(this);
	  	    LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(15, 15);
	  	    localLayoutParams.leftMargin = 15;
	  	    localLayoutParams.rightMargin = 15;
	  	    localImageView.setLayoutParams(localLayoutParams);
	  	    localImageView.setClickable(false);
	  	    localImageView.setFocusable(false);
	  	    localImageView.setImageResource(R.drawable.page_flag_disable);
	  	    this.pageFlags.addView(localImageView);
	  	 
	    }
	    
	}
	public void onConfigurationChanged(Configuration paramConfiguration){	
	    super.onConfigurationChanged(paramConfiguration);
	    //TODO: TBD
	}
	protected void onCreate(Bundle paramBundle){
	    super.onCreate(paramBundle);
	    int i = getIntent().getIntExtra("display_which", 0);
	    this.curDisplayIndex = i;
	}
	protected void onStart(){
	    super.onStart();
	    if (!this.onCreated){
	        findAllView();
	        initTabs();
	        this.onCreated = true;
	    }
	    displayDefaultTab(this.tabDomains[this.curDisplayIndex]);
	}
	protected void onStop(){
	    super.onStop();
	}
	protected void scrollTo(int paramInt){
		if(DEBUG) System.out.println("Scroll To "+paramInt);
	    ((ImageView)this.pageFlags.getChildAt(this.curDisplayIndex)).setImageResource(R.drawable.page_flag_disable);
	    int i = this.curDisplayIndex * this.tabWidth;
	    int j = paramInt * this.tabWidth;
	    TranslateAnimation localTranslateAnimation = new TranslateAnimation(0, i, 0, j, 2, 0.0F, 2, 0.0F);
	    localTranslateAnimation.setDuration(this.defaultAnimDuration);
	    localTranslateAnimation.setFillAfter(true);
	    if (this.transAnimListener == null)
	      this.transAnimListener = new ScrollFlagImageAnimListener();
	    this.transAnimListener.setCurIndex(this.curDisplayIndex);
	    localTranslateAnimation.setAnimationListener(this.transAnimListener);
	    this.selectedIcon.setAnimation(localTranslateAnimation);
	    ((ImageView)this.pageFlags.getChildAt(paramInt)).setImageResource(R.drawable.page_flag_enable);
	}
	
	
	public class TabDomain{
	    int index;
	    boolean launched = false;
	    String title;

	    public TabDomain(String arg2){
	      this.index = 0;//??
	      this.title = "";//??
	    }
	    public TabDomain(int in,String str){
	    	this.index = in;
	    	this.title = str;
	    }
	    public int getIndex(){
	      return this.index;
	    }
	    public String getTitle(){
	      return this.title;
	    }
	    public boolean isLaunched(){
	      return this.launched;
	    }
	    public void setIndex(int paramInt){
	      this.index = paramInt;
	    }
	    public void setLaunched(boolean paramBoolean){
	      this.launched = paramBoolean;
	    }
	    public void setTitle(String paramString){
	      this.title = paramString;
	    }
	}
	public class ActivityTabDomain extends ScrollTabHostActivity.TabDomain{
	    private Intent intent;
	    public ActivityTabDomain(String paramIntent, Intent arg3){
	      super(paramIntent);
	      this.intent = arg3;
	    }
	    public ActivityTabDomain(int in,String str,Intent intent){
	    	super(in,str);
	    	this.intent = intent;
	    }
	    public Intent getIntent()
	    {
	      return this.intent;
	    }
	    public void setIntent(Intent paramIntent){
	      this.intent = paramIntent;
	    }
	}
	public class ScrollFlagImageAnimListener implements Animation.AnimationListener{

		int curIndex;
	    public int getCurIndex()
	    {
	      return this.curIndex;
	    }
	    public void setCurIndex(int paramInt)
	    {
	      this.curIndex = paramInt;
	    }
		@Override
		public void onAnimationEnd(Animation arg0) {
			((TextView)ScrollTabHostActivity.this.tabs.getChildAt(ScrollTabHostActivity.this.curDisplayIndex)).setTextColor(-12887656);
		}

		@Override
		public void onAnimationRepeat(Animation arg0) {
		}

		@Override
		public void onAnimationStart(Animation arg0) {
		      if(DEBUG) System.out.println("anim:"+this.curIndex);
		      if (this.curIndex >= 0)
		        ((TextView)ScrollTabHostActivity.this.tabs.getChildAt(this.curIndex)).setTextColor(-1);
		}
		
	}
	public class ScrollTabOnClickLisnter implements View.OnClickListener{

		private ScrollTabHostActivity tabActivity = ScrollTabHostActivity.this;
		@Override
		public void onClick(View paramView) {
		     ScrollTabHostActivity.TabDomain localTabDomain = (ScrollTabHostActivity.TabDomain)paramView.getTag();
		      int i = localTabDomain.getIndex();
		      if(DEBUG) System.out.println("Tab Click at"+i+": from"+ScrollTabHostActivity.this.curDisplayIndex);
		      if (i != ScrollTabHostActivity.this.curDisplayIndex)
		      {
		        this.tabActivity.scrollTo(i);
		        this.tabActivity.displayTabContent(localTabDomain);
		        ScrollTabHostActivity.this.curDisplayIndex = i;
		      }
		}
	}
	public static class TabOnGestureListener extends GestureDetector.SimpleOnGestureListener{
	    private ScrollTabHostActivity tabHistActivity;
	    public TabOnGestureListener(ScrollTabHostActivity paramScrollTabHostActivity)
	    {
	      this.tabHistActivity = paramScrollTabHostActivity;
	    }
	    
	    public boolean onFling(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2){
	        if(DEBUG) System.out.println("onFling");
	    	if ((paramMotionEvent1 != null) && (paramMotionEvent2 != null)){
	        	if ((paramMotionEvent1.getX() - paramMotionEvent2.getX() > 90.0F) && (Math.abs(paramFloat1) > 120.0F))
		        {
		            this.tabHistActivity.displayNext();
		            if(DEBUG) System.out.println("next");
		            return true;
		        }else if((paramMotionEvent2.getX() - paramMotionEvent1.getX() > 90.0F) && (Math.abs(paramFloat1) > 120.0F)) {
		        	 this.tabHistActivity.displayPrevious();
		        	 if(DEBUG) System.out.println("Prev");
		        	 return true;
		        }
	        }
	        return false;
	    }
	    public boolean onDown(MotionEvent e){
	    	return true;
	    }
	}
}
