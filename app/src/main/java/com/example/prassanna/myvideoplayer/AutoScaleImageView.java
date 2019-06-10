package com.example.prassanna.myvideoplayer;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;

public class AutoScaleImageView extends AppCompatImageView {
	
	public AutoScaleImageView(Context context) {
		super(context);
	}
	
	public AutoScaleImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public AutoScaleImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (getDrawable() == null || getDrawable().getIntrinsicWidth() == 0) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		} else {
			int width = View.MeasureSpec.getSize(widthMeasureSpec);
			int height = width * getDrawable().getIntrinsicHeight() / getDrawable().getIntrinsicWidth();
			setMeasuredDimension(width, height);
		}
	}
}
