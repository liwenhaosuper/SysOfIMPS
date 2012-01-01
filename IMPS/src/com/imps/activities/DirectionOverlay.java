package com.imps.activities;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point; 

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class DirectionOverlay extends Overlay {
	List<GeoPoint> mPoints;
	
	DirectionOverlay(List<GeoPoint> points){
		mPoints = points;
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {   
		// TODO Auto-generated method stub   
		super.draw(canvas, mapView, shadow);   
		// »­±Ê   
		Paint paint = new Paint();   
		paint.setColor(Color.RED);   
		paint.setDither(true);   
		paint.setStyle(Paint.Style.STROKE);   
		paint.setStrokeJoin(Paint.Join.ROUND);   
		paint.setStrokeCap(Paint.Cap.ROUND);   
		paint.setStrokeWidth(2);   
		Projection projection = mapView.getProjection();   
		if (mPoints != null && mPoints.size() > 0){
			Point p1 = new Point();
			projection.toPixels(mPoints.get(0), p1);
			Path path = new Path();
			path.moveTo(p1.x, p1.y);
			
			for (int i = 1; i < mPoints.size(); i++){
				projection.toPixels(mPoints.get(i), p1);
				path.lineTo(p1.x, p1.y);
			}
			canvas.drawPath(path, paint); //»­³öÂ·¾¶   
		}
	}
}
