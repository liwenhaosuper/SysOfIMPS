package com.imps.ui;

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

import com.imps.IMPSDev;
import com.imps.activities.CoupleDoodle;
import com.imps.basetypes.DoodleAction;
import com.imps.net.handler.MessageFactory;
import com.imps.net.handler.UserManager;
import com.imps.services.impl.DoodleSenderService;

public class DoodleView extends View{
	 private static final float MINP = 0.25f;
     private static final float MAXP = 0.75f;
 	private static boolean DEBUG = IMPSDev.isDEBUG();
	private static String TAG = DoodleView.class.getCanonicalName();
     private DoodleSenderService mSender;
     
     public Bitmap  mBitmap;
     private Canvas  mCanvas;
     private Path    mPath;
     private Paint   mBitmapPaint;
     private Paint       mPaint;
     private DisplayMetrics metrics;
     //Parameter of friend’s
     private Path mFriPath;
     private float mFriX, mFriY;

	 public DoodleView(Context context) {
		 super(context);
		 metrics = new DisplayMetrics();
         ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
         mBitmap = Bitmap.createBitmap(metrics.widthPixels,metrics.heightPixels, Bitmap.Config.ARGB_8888);
         mCanvas = new Canvas(mBitmap);
         mCanvas.drawColor(0xFFFFFFFF);
         mPath = new Path();
         mBitmapPaint = new Paint(Paint.DITHER_FLAG);
         //friend
         initFriendParameter();
	}
	 public DoodleView(Context context,AttributeSet attrs){
		 super(context, attrs);
		 metrics = new DisplayMetrics();
         ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
         mBitmap = Bitmap.createBitmap(metrics.widthPixels,metrics.heightPixels, Bitmap.Config.ARGB_8888);
         mCanvas = new Canvas(mBitmap);
         mCanvas.drawColor(0xFFFFFFFF);
         mPath = new Path();
         mBitmapPaint = new Paint(Paint.DITHER_FLAG);
         //friend
         initFriendParameter();
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
         //friend path
         canvas.drawPath(mFriPath, mPaint);
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
         if(DEBUG) Log.d(TAG,"Touch Event.Sender:"+mSender.isStarted());
         switch (event.getAction()) {
             case MotionEvent.ACTION_DOWN:
            	 if(mSender.isStarted()){
            		 mSender.addMsg(MessageFactory.createCDoodleData(UserManager.getGlobaluser().getUsername(),
            				 CoupleDoodle.roomMaster,DoodleAction.ACTION_DOWN,x,y));
            	 }
                 touch_start(x, y);
                 invalidate();
                 break;
             case MotionEvent.ACTION_MOVE:
            	 if(mSender.isStarted()){
            		 mSender.addMsg(MessageFactory.createCDoodleData(UserManager.getGlobaluser().getUsername(),
            				 CoupleDoodle.roomMaster,DoodleAction.ACTION_MOVE,x,y));
            	 }
                 touch_move(x, y);
                 invalidate();
                 break;
             case MotionEvent.ACTION_UP:
            	 if(mSender.isStarted()){
            		 mSender.addMsg(MessageFactory.createCDoodleData(UserManager.getGlobaluser().getUsername(),
            				 CoupleDoodle.roomMaster,DoodleAction.ACTION_UP,x,y));
            	 }
                 touch_up();
                 invalidate();
                 break;
         }
         return true;
     }

     //friend action
	 public void initFriendParameter(){
		 mFriPath = new Path();
	 }
	 
	//the following functions can only be called by non-main thread
	 
	 public void touch_start_fri(float x, float y) {
	     mFriPath.reset();
	     mFriPath.moveTo(x, y);
	     mFriX = x;
	     mFriY = y;
	     postInvalidate();
	 }
	 public void touch_move_fri(float x, float y) {
         float dx = Math.abs(x - mFriX);
         float dy = Math.abs(y - mFriY);
         if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
             mFriPath.quadTo(mFriX, mFriY, (x + mFriX)/2, (y + mFriY)/2);
             mFriX = x;
             mFriY = y;
         }
         postInvalidate();
     }
     public void touch_up_fri() {
         mFriPath.lineTo(mFriX, mFriY);
         // commit the path to our offscreen
         mCanvas.drawPath(mFriPath, mPaint);
         // kill this so we don't double draw
         mFriPath.reset();
         postInvalidate();
     }
	public void setmSender(DoodleSenderService mSender) {
		this.mSender = mSender;
	}
	public DoodleSenderService getmSender() {
		return mSender;
	}
}
