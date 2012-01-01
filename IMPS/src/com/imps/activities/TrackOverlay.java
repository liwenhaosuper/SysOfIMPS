package com.imps.activities;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

/**
* 地图上的线型图层:包括一个起点,一个终点,以及之间的曲线
* @author superwang
*/
public class TrackOverlay extends ItemizedOverlay<OverlayItem> {
	private static final int LAYER_FLAGS = Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG | 
	Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG | Canvas.CLIP_TO_LAYER_SAVE_FLAG;
	
	/**
	 * 用于保存起点/终点数据
	 */
	private final ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

	/**
	 * 用于保存构成曲线的点的数据
	 */
	private final ArrayList<GeoPoint> linePoints = new ArrayList<GeoPoint>();

	/**
	 * @param defaultMarker
	 */
	public TrackOverlay() {
		super(null);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.google.android.maps.ItemizedOverlay#createItem(int)
	 */
	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	/* (non-Javadoc)
	 * @see com.google.android.maps.ItemizedOverlay#size()
	 */
	@Override
	public int size() {
		// TODO Auto-generated method stub
		return mOverlays.size();
	}

	/**
	 * 调价起点/终点
	 * description:
	 * @param overlay
	 */
	public void addOverlay(OverlayItem overlay) {
		mOverlays.add(overlay);
		populate();
	}

	/**
	 * 添加曲线中的点
	 * description:
	 * @param point
	 */
	public void addLinePoint(GeoPoint point) {
		linePoints.add(point);
	}

	public ArrayList<GeoPoint> getLinePoints() {
		return linePoints;
	}

	/**
	 * 画起点/终点/轨迹
	 */
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		if (!shadow) {
			//System.out.println("!!!!!!!!!!!!!!");
			
			canvas.save(LAYER_FLAGS);
			//canvas.save();
			
			Projection projection = mapView.getProjection();
			int size = mOverlays.size();
			Point point = new Point();
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			OverlayItem overLayItem;

			//画起点/终点
			for (int i = 0; i < size; i++) {
				overLayItem = mOverlays.get(i);

				Drawable marker = overLayItem.getMarker(0);
				//marker.getBounds()
				/* 象素点取得转换 */
				projection.toPixels(overLayItem.getPoint(), point);

				if (marker != null) {
					boundCenterBottom(marker);
				}

				/* 圆圈 */
				//Paint paintCircle = new Paint();
				//paintCircle.setColor(Color.RED);
				paint.setColor(Color.RED);
				canvas.drawCircle(point.x, point.y, 5, paint);

				/* 文字设置 */
				/* 标题 */
				String title = overLayItem.getTitle();
				/* 简介 */
				//    String snippet = overLayItem.getSnippet();
				//
				//    StringBuffer txt = new StringBuffer();
				//    if (title != null && !"".equals(title))
				//     txt.append(title);
				//
				//    if (snippet != null && !"".equals(snippet)) {
				//     if (txt.length() > 0) {
				//      txt.append(":");
				//     }
				//     txt.append(snippet);
				//    }    
				//Paint paintText = new Paint();

				if (title != null && title.length() > 0) {
					paint.setColor(Color.BLACK);
					paint.setTextSize(15);
					canvas.drawText(title, point.x, point.y, paint);
				}

			}

			//画线
			boolean prevInBound = false;//前一个点是否在可视区域
			Point prev = null;
			int mapWidth = mapView.getWidth();
			int mapHeight = mapView.getHeight();
			//Paint paintLine = new Paint();
			paint.setColor(Color.RED);
			//paint.setPathEffect(new CornerPathEffect(10));
			paint.setStrokeWidth(2);
			int count = linePoints.size();

			//Path path = new Path();
			//path.setFillType(Path.FillType.INVERSE_WINDING);
			for (int i = 0; i < count; i++) {
				GeoPoint geoPoint = linePoints.get(i);
				//projection.toPixels(geoPoint, point); //这一行似乎有问题
				point = projection.toPixels(geoPoint, null);
				if (prev != null) {
					if (point.x >= 0 && point.x <= mapWidth && point.y >= 0 && point.y <= mapHeight) {
						if ((Math.abs(prev.x - point.x) > 2 || Math.abs(prev.y - point.y) > 2)) {
							//这里判断点是否重合，重合的不画线，可能会导致画线不在路上
							canvas.drawLine(prev.x, prev.y, point.x, point.y, paint);
							//path.lineTo(point.x, point.y);

							prev = point;
							prevInBound = true;

						}
					} 
					else {
						//在可视区与之外
						if (prevInBound) {//前一个点在可视区域内，也需要划线
							//path.lineTo(point.x, point.y);
							canvas.drawLine(prev.x, prev.y, point.x, point.y, paint);
						}
						prev = point;
						prevInBound = false;
					}
				} 
				else {
					//path.moveTo(point.x, point.y);
					prev = point;
				}
			}
			//canvas.drawPath(path, paint);
			canvas.restore();
			//DebugUtils.showMemory();
		}
		super.draw(canvas, mapView, shadow);
	}
}