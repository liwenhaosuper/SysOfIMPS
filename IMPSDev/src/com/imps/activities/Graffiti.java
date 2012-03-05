package com.imps.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BlurMaskFilter;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imps.R;
import com.imps.ui.GraffitiView;
import com.imps.ui.HandwritingView;
import com.imps.ui.widget.ColorPickerDialog;
/**
 * 
 * @author liwenhaosuper
 *
 */
public class Graffiti extends Activity
implements ColorPickerDialog.OnColorChangedListener {
    /** Called when the activity is first created. */
    private Paint       mPaint;
    private MaskFilter  mEmboss;
    private MaskFilter  mBlur;
    private HandwritingView handwriteView;
    private GraffitiView graffitiView;
    private int mode = 0;  //0:hand write mode 1:graffiti mode
    private TextView handWritingImv; 
    private TextView graffitiImv;
    private TextView sendImv;
    private ImageView backImv;
    private ImageView undoImv;
    private LinearLayout sendbar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.graffiti);
        String title = getResources().getString(R.string.hand_write_gesture);
        setTitle(title);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFF0000FF);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(8);        
        mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 },
                                       0.4f, 6, 3.5f);
        mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);
        handwriteView = (HandwritingView)findViewById(R.id.hand_write_view);
        graffitiView = (GraffitiView)findViewById(R.id.graffiti_view);
        handwriteView.init(mPaint);
        graffitiView.init(mPaint);
        handWritingImv = (TextView)findViewById(R.id.hand_write_btn_gesture);
        handWritingImv.setOnClickListener(listener);
        graffitiImv = (TextView)findViewById(R.id.hand_write_btn_graffiti);
        graffitiImv.setOnClickListener(listener);
        sendImv = (TextView)findViewById(R.id.send_graffiti);
        sendImv.setOnClickListener(listener);
        backImv = (ImageView)findViewById(R.id.graffiti_exit);
        backImv.setOnClickListener(listener);
        undoImv = (ImageView)findViewById(R.id.graffiti_undo);
        undoImv.setOnClickListener(listener);
        sendbar = (LinearLayout)findViewById(R.id.send_bar);
        sendbar.setOnClickListener(listener);
        handWritingImv.setBackgroundDrawable(getResources().getDrawable(R.drawable.friend_top_left_pressed));
		graffitiImv.setBackgroundDrawable(getResources().getDrawable(R.drawable.friend_top_right_normal));  
    }
	@Override
	public void colorChanged(int color) {
		// TODO Auto-generated method stub
		 mPaint.setColor(color);
	}
    private static final int COLOR_MENU_ID = Menu.FIRST;
    private static final int EMBOSS_MENU_ID = Menu.FIRST + 1;
    private static final int BLUR_MENU_ID = Menu.FIRST + 2;
    private static final int ERASE_MENU_ID = Menu.FIRST + 3;
    private static final int SRCATOP_MENU_ID = Menu.FIRST + 4;
    private static final int CLEAR_ID = Menu.FIRST+5;
    private static final int SAVE_ID = Menu.FIRST+6;
    private static final int SWITCH_ID = Menu.FIRST+7;
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        
        menu.add(0, COLOR_MENU_ID, 0, getResources().getString(R.string.graffiti_color)).setShortcut('3', 'c');
        menu.add(0, EMBOSS_MENU_ID, 0, getResources().getString(R.string.graffiti_highlight)).setShortcut('4', 's');
        menu.add(0, BLUR_MENU_ID, 0, getResources().getString(R.string.graffiti_bold)).setShortcut('5', 'z');
        menu.add(0, ERASE_MENU_ID, 0, getResources().getString(R.string.graffiti_eraser)).setShortcut('5', 'z');
        menu.add(0, SRCATOP_MENU_ID, 0, getResources().getString(R.string.graffiti_trace)).setShortcut('5', 'z');
        menu.add(0,CLEAR_ID,0,getResources().getString(R.string.graffiti_clear)).setShortcut('5','z');
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xFF);
        switch (item.getItemId()) {
            case COLOR_MENU_ID:
                new ColorPickerDialog(this, this, mPaint.getColor()).show();
                return true;
            case EMBOSS_MENU_ID:
                if (mPaint.getMaskFilter() != mEmboss) {
                    mPaint.setMaskFilter(mEmboss);
                } else {
                    mPaint.setMaskFilter(null);
                }
                return true;
            case BLUR_MENU_ID:
                if (mPaint.getMaskFilter() != mBlur) {
                    mPaint.setMaskFilter(mBlur);
                } else {
                    mPaint.setMaskFilter(null);
                }
                return true;
            case ERASE_MENU_ID:
                mPaint.setXfermode(new PorterDuffXfermode(
                                                        PorterDuff.Mode.CLEAR));
                return true;
            case SRCATOP_MENU_ID:
                mPaint.setXfermode(new PorterDuffXfermode(
                                                    PorterDuff.Mode.SRC_ATOP));
                mPaint.setAlpha(0x80);              
                return true;
            case CLEAR_ID:
            	//clear
            	if(mode==0){
            		handwriteView.clear();
            	}
            	else if(mode==1){
            		graffitiView.clear();
            	}
            	return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public String saveImage()
    {
    	String path = null;
    	if(mode==0){
    		path = handwriteView.saveImage();
    	}
    	else if(mode==1){
    		path = graffitiView.saveImage();
    	}
    	return path;
    }
    private View.OnClickListener listener = new View.OnClickListener()
    {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v.getId()==handWritingImv.getId())
			{
				Log.d("Graffiti", "hand write view clicked...");
				if(mode!=0){
					mode = 0;
					handwriteView.setVisibility(View.VISIBLE);
					graffitiView.setVisibility(View.GONE);
					//pressed
					handWritingImv.setBackgroundDrawable(getResources().getDrawable(R.drawable.friend_top_left_pressed));
					graffitiImv.setBackgroundDrawable(getResources().getDrawable(R.drawable.friend_top_right_normal));
				}
			}
			else if(v.getId()==graffitiImv.getId()){
				Log.d("Graffiti", "graffiti view clicked...");
				if(mode!=1){
					mode = 1;
					handwriteView.setVisibility(View.GONE);
					graffitiView.setVisibility(View.VISIBLE);	
					handWritingImv.setBackgroundDrawable(getResources().getDrawable(R.drawable.friend_top_left_normal));
					graffitiImv.setBackgroundDrawable(getResources().getDrawable(R.drawable.friend_top_right_pressed));
				}
			}
			else if(v.getId()==sendImv.getId()){
				String path = saveImage();
				Intent i = new Intent();
				i.putExtra("path", path);
				setResult(1,i);
				finish();
			}
			else if(v.getId()==backImv.getId()){
				Intent intent = new Intent();
				setResult(0,intent);
				finish();
			}
			else if(v.getId()==undoImv.getId()){
				//undo	
				if(mode==0){
            		handwriteView.clear();
            	}
            	else if(mode==1){
            		graffitiView.clear();
            	}
			}
			else if(v.getId()==sendbar.getId()){
				String path = saveImage();
				Intent i = new Intent();
				i.putExtra("path", path);
				setResult(1,i);
				finish();
			}
			
		}
    	
    };
}
