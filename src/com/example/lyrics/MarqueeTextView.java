package com.example.lyrics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

public class MarqueeTextView extends TextView implements Runnable {
	
	private float currentScrollX;// 当前滚动的位置
	private boolean isStop = false;
	private int textWidth;
	
	public MarqueeTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MarqueeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		getTextWidth();
	}

	/**
	 * 获取文字宽度
	 */
	private int getTextWidth() {
		Paint paint = this.getPaint();
		String str = this.getText().toString();
		textWidth = (int) paint.measureText(str);
		return textWidth;
	}

	@Override
	public void run() {
		if (currentScrollX - 0 < 0.1 && currentScrollX - 0 > -0.1) {
			currentScrollX += 0.5;// 滚动速度
			postDelayed(this, 2000);
			return;
		}
		currentScrollX += 0.5;// 滚动速度
		scrollTo((int) currentScrollX, 0);
		if (isStop) {
			return;
		}
		if (currentScrollX > this.textWidth) {
			currentScrollX = -getWidth();
		}
		postDelayed(this, 5);
	}

	// 开始滚动
	public void startScroll() {
		isStop = false;
		this.removeCallbacks(this);
		post(this);
	}

	// 停止滚动
	public void stopScroll() {
		isStop = true;
	}

	public void setMarqueeText(CharSequence text) {
		super.setText(text);
		getTextWidth();
		currentScrollX = -0.5f;
		if (getTextWidth() < getWidth()) {
			stopScroll();
		} else {
			startScroll();
		}
	}
}
