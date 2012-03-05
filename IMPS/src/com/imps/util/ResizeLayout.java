package com.imps.util;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.imps.R;

public class ResizeLayout extends LinearLayout {

	int count = 0;
	int count1 = 0;
	int count2 = 0;
	//����Ĭ�ϵ��������С�߶ȣ�����Ϊ�˱���onSizeChanged��ĳЩ����������³��ֵ����⡣
	private static final int SOFTKEYPAD_MIN_HEIGHT = 50;
	private Handler uiHandler = new Handler();
	private static final String TAG = "ResizeLayout";

	public ResizeLayout(Context context) {
		super(context);
	}

	public ResizeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onSizeChanged(int w, final int h, int oldw, final int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		Log.i(TAG, "onSizeChanged " + count++ + "=>onResize called! w=" + w + ",h=" + h + ",oldw=" + oldw + ",oldh=" + oldh);

		uiHandler.post(new Runnable() {

			@Override
			public void run() {
				if (oldh - h > SOFTKEYPAD_MIN_HEIGHT)
					// ��������ΪView.GONE��ռ�ռ�
					findViewById(R.id.talk_panel).setVisibility(View.GONE);
				else
					findViewById(R.id.talk_panel).setVisibility(View.VISIBLE);
			}
		});
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		Log.e(TAG, "onLayout " + count1++ + "=>OnLayout called! l=" + l + ", t=" + t + ",r=" + r + ",b=" + b);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		Log.e(TAG, "onMeasure " + count2++ + "=>onMeasure called! widthMeasureSpec=" + widthMeasureSpec + ", heightMeasureSpec="
				+ heightMeasureSpec);

	}
}
