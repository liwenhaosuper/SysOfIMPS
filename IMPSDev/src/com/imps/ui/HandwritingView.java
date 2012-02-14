package com.imps.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.imps.R;

public class HandwritingView extends View {
    
    private static final float MINP = 0.25f;
    private static final float MAXP = 0.75f;
    
    private Bitmap  mBitmap;
    private Path    mPath;
    private Paint   mBitmapPaint;	    
    private long oldTime = System.currentTimeMillis();
    private long newTime = System.currentTimeMillis();
    private boolean isGenerated = false;
    private boolean isReady = false;
    private int firstCalled = 0;
    private Canvas mCanvas;
    private Bitmap tmap;
    private DisplayMetrics metrics;
    public int rowAndCol = 6;
    public List<Bitmap> mBitmapList;
    public int width = 50;
    public int height = 50;
    public int internal = 4;
    public Bitmap mCursor;
    private boolean isCursorVisible = false;
    private Paint       mPaint;
    public HandwritingView(Context c) {
        super(c);
        metrics = new DisplayMetrics();
        ((Activity)c).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mBitmap = Bitmap.createBitmap(metrics.widthPixels,metrics.heightPixels, Bitmap.Config.ARGB_8888);
       // mCanvas = new Canvas(mBitmap);
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        mBitmapList = new ArrayList<Bitmap>();
        width =  (int)(0.15*metrics.widthPixels);
        height = (int)(0.15*metrics.heightPixels);
        rowAndCol = 6;
        Bitmap tmaps = ((BitmapDrawable)(this.getResources().getDrawable(R.drawable.cursor_bg))).getBitmap();
        mCursor = Bitmap.createScaledBitmap(tmaps, internal, height,false);
        cursorThread.start();
    }
    public HandwritingView(Context c,AttributeSet attrs){
    	super(c, attrs);
    	 metrics = new DisplayMetrics();
         ((Activity)c).getWindowManager().getDefaultDisplay().getMetrics(metrics);
         mBitmap = Bitmap.createBitmap(metrics.widthPixels,metrics.heightPixels, Bitmap.Config.ARGB_8888);
        // mCanvas = new Canvas(mBitmap);
         mPath = new Path();
         mBitmapPaint = new Paint(Paint.DITHER_FLAG);
         mBitmapList = new ArrayList<Bitmap>();
         width =  (int)(0.15*metrics.widthPixels);
         height = (int)(0.15*metrics.heightPixels);
         rowAndCol = 6;
         Bitmap tmaps = ((BitmapDrawable)(this.getResources().getDrawable(R.drawable.cursor_bg))).getBitmap();
         mCursor = Bitmap.createScaledBitmap(tmaps, internal, height,false);
         cursorThread.start();
    }
    public void init(Paint paint){
    	mPaint = paint;
    }
    public void clear(){
    	mBitmapList = new ArrayList<Bitmap>();
    	invalidate();
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }	        
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(0xFFFFFFFF);	           
        if(firstCalled==0)
        {
        	firstCalled = 1;
        	generateFrame.start();
        }
        int x = 0;
    	int y = 0;
        for(int i=0;i<mBitmapList.size();i++)
        {
        	x = (i%rowAndCol)*(width+internal);
        	y = (i/rowAndCol)*height;
        	canvas.drawBitmap(mBitmapList.get(i), x, y,mBitmapPaint);	
        }
        x = (mBitmapList.size()%rowAndCol)*width;
        y = (mBitmapList.size()/rowAndCol)*height;
        if(isCursorVisible){
        	canvas.drawBitmap(mCursor, x, y,mBitmapPaint);
        }	            
        canvas.drawPath(mPath, mPaint);
    }
    
    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;
    
    private void touch_start(float x, float y) {
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }
    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;
        }
    }
    private void touch_up() {
    	mPath.lineTo(mX, mY);
    }	        
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();	            
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                isReady = false;
                isGenerated = false;
                oldTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                oldTime = System.currentTimeMillis();
                isReady = false;
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                oldTime = System.currentTimeMillis();
                isReady = true;
                break;
        }
        return true;
    }
    public void addFrame(){
    	tmap = Bitmap.createBitmap(metrics.widthPixels,metrics.heightPixels, Bitmap.Config.ARGB_8888);
    	mCanvas = new Canvas(tmap);
    	mCanvas.drawColor(0xFFFFFFFF);
    	mCanvas.drawPath(mPath, mPaint);
    	mPath.reset();        	
    	Matrix matrix = new Matrix(); 
    	matrix.postScale((float)0.15,(float)0.15);             	
    	Bitmap t = Bitmap.createBitmap(tmap, 0,0,tmap.getWidth(), tmap.getHeight(), matrix, true);
    	mBitmapList.add(t);
    }
    
   public Thread generateFrame = new Thread(){
	   @Override
	   public void run(){
		   while(true){
			   if(!isGenerated&&isReady)
			   {
				   newTime = System.currentTimeMillis();
				   if(newTime-oldTime>800)
				   {
					   //TODO: generate frame		   
					   addFrame();
					   postInvalidate();
					   isGenerated = true;      					   
				   }
			   }        			  
			   try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   }
	   }
   };
   public Thread cursorThread = new Thread(){
	   @Override
	   public void run(){
		   try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		   while(true){
			   if(isCursorVisible){
				   isCursorVisible = false;
			   }
			   else{
				   isCursorVisible = true;
			   }
			   postInvalidate();
			   try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   }
	   }
   };
   
   public String saveImage(){
	   String res = null;
	   Long timestamp = System.currentTimeMillis();
	   res ="/sdcard/imps/handwriting/paint" + timestamp.toString()+".jpeg";
	   File destDir = new File("/sdcard/imps/handwriting/");
   	   if(!destDir.exists())
       {
   		   destDir.mkdirs();
   	   }
   	   FileOutputStream fos;
   	  Canvas canvas = new Canvas(mBitmap);
	  canvas.drawColor(0xFFFFFFFF);
	  int x = 0;
	  int y = 0;
      for(int i=0;i<mBitmapList.size();i++)
      {
    	  x = (i%rowAndCol)*width;
    	  y = (i/rowAndCol)*height;
    	  canvas.drawBitmap(mBitmapList.get(i), x, y,mBitmapPaint);	
      }
      canvas.save();
      canvas.restore();
      Bitmap finalMap = null;
      int xwid = width,xheg=height;
      if(mBitmapList.size()>=rowAndCol){
    	  xwid = metrics.widthPixels;
      }else{
    	  xwid = mBitmapList.size()*width;
      }
      xheg = mBitmapList.size()/rowAndCol+height;
      if(mBitmapList.size()<=0){
    	  xheg = height;
    	  xwid = width;
      }
      finalMap = Bitmap.createBitmap(mBitmap, 0,0,xwid, xheg);
	  try {
		  fos = new FileOutputStream(res);
		  if(finalMap.compress(Bitmap.CompressFormat.JPEG, 100, fos)){
			  Log.d("tag", "succ..."+res);
              fos.close();			
	  }
		else{
			Log.d("tag", "failed..."+res);
			 fos.close();
		}
		
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	   return res;
   }
   
    	        
}