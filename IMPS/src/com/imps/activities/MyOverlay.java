﻿package com.imps.activities;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class MyOverlay extends Overlay {
	private GeoPoint gp1,gp2;
	private int mode = 0;
	private int mRadius = 6;
	private final static String tag = "ssss";
    public MyOverlay(GeoPoint g1, GeoPoint g2, int mode){
    	this.gp1 = g1;
    	this.gp2 = g2;
    	this.mode = mode;
    }
    
    public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when){ 
      Projection projection = mapView.getProjection();
      if (shadow == false) 
      {      
        /* 设置笔刷 */ 
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);

        Point point = new Point();
        projection.toPixels(gp1, point);
        /* mode=1：创建起点 */
        if(mode==1)
        {
          /* 定义RectF对象 */
          RectF oval=new RectF(point.x - mRadius, point.y - mRadius,
                               point.x + mRadius, point.y + mRadius); 
          /* 绘制起点的圆形 */ 
          canvas.drawOval(oval, paint);
        }
        /* mode=2：画路线 */
        else if(mode==2)
        {
          Point point2 = new Point(); 
          projection.toPixels(gp2, point2);
          paint.setColor(Color.BLACK);
          paint.setStrokeWidth(5);
          paint.setAlpha(120);
          Log.d(tag, "hua");
          /* 画线 */ 
          canvas.drawLine(point.x, point.y, point2.x,point2.y, paint);
          Log.d(tag, "le");
        }
        /* mode=3：创建终点 */
        else if(mode==3)
        {
          /* 避免误差，先画最后一段的路线 */
          Point point2 = new Point(); 
          projection.toPixels(gp2, point2);
          paint.setStrokeWidth(5);
          paint.setAlpha(120);
          canvas.drawLine(point.x, point.y, point2.x,point2.y, paint);
          
          /* 定义RectF对象 */ 
          RectF oval=new RectF(point2.x - mRadius,point2.y - mRadius,  
                               point2.x + mRadius,point2.y + mRadius);
          /* 绘制终点的圆形 */
          paint.setAlpha(255);
          canvas.drawOval(oval, paint);
        }
      }
      return super.draw(canvas, mapView, shadow, when);
    }
}
