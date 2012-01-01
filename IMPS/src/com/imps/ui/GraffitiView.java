package com.imps.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class GraffitiView extends View{
	
	 private static final float MINP = 0.25f;
     private static final float MAXP = 0.75f;
     
     public Bitmap  mBitmap;
     private Canvas  mCanvas;
     private Path    mPath;
     private Paint   mBitmapPaint;
     private Paint       mPaint;
     private DisplayMetrics metrics;

	 public GraffitiView(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		 metrics = new DisplayMetrics();
         ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
         mBitmap = Bitmap.createBitmap(metrics.widthPixels,metrics.heightPixels, Bitmap.Config.ARGB_8888);
         mCanvas = new Canvas(mBitmap);
         mCanvas.drawColor(0xFFFFFFFF);
         mPath = new Path();
         mBitmapPaint = new Paint(Paint.DITHER_FLAG);
	}
	 public GraffitiView(Context context,AttributeSet attrs){
		 super(context, attrs);
		 metrics = new DisplayMetrics();
         ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
         mBitmap = Bitmap.createBitmap(metrics.widthPixels,metrics.heightPixels, Bitmap.Config.ARGB_8888);
         mCanvas = new Canvas(mBitmap);
         mCanvas.drawColor(0xFFFFFFFF);
         mPath = new Path();
         mBitmapPaint = new Paint(Paint.DITHER_FLAG);
	 }
	 public void init(Paint paint){
		 mPaint = paint;
	 }
	 public void clear(){
		 mBitmap = Bitmap.createBitmap(metrics.widthPixels,metrics.heightPixels, Bitmap.Config.ARGB_8888);
		 mCanvas = new Canvas(mBitmap);
         mCanvas.drawColor(0xFFFFFFFF);
         invalidate();
	 }
		
		 @Override
	        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	            super.onSizeChanged(w, h, oldw, oldh);
	        }
	        
	        @Override
	        protected void onDraw(Canvas canvas) {
	            canvas.drawColor(0xFFFFFFFF);
	            
	            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
	            
	            canvas.drawPath(mPath, mPaint);
	        }
	        
	        private float mX, mY;
	        private static final float TOUCH_TOLERANCE = 4;
	        
	        private void touch_start(float x, float y) {
	            mPath.reset();
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
	            // commit the path to our offscreen
	            mCanvas.drawPath(mPath, mPaint);
	            // kill this so we don't double draw
	            mPath.reset();
	        }
	        
	        @Override
	        public boolean onTouchEvent(MotionEvent event) {
	            float x = event.getX();
	            float y = event.getY();
	            
	            switch (event.getAction()) {
	                case MotionEvent.ACTION_DOWN:
	                    touch_start(x, y);
	                    invalidate();
	                    break;
	                case MotionEvent.ACTION_MOVE:
	                    touch_move(x, y);
	                    invalidate();
	                    break;
	                case MotionEvent.ACTION_UP:
	                    touch_up();
	                    invalidate();
	                    break;
	            }
	            return true;
	        }	
	        
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
	        	try {
	    			fos = new FileOutputStream(res);
	    			if(mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)){
	    				//Toast.makeText(getApplicationContext(), "save successfully...", Toast.LENGTH_LONG);
	    				Log.d("tag", "succ..."+res);
	                    fos.close();			
	    			}
	    			else{
	    				//Toast.makeText(getApplicationContext(), "save failed...", Toast.LENGTH_LONG);
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