package com.itheima55.quickindex.ui;

import com.itheima55.quickindex.utils.Util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 绘制A-Z的索引条目
 * @author poplar
 *
 */
public class QuickIndexBar extends View {
	
	private static final String[] LETTERS = new String[]{
		"A", "B", "C", "D", "E", "F",
		"G", "H", "I", "J", "K", "L",
		"M", "N", "O", "P", "Q", "R",
		"S", "T", "U", "V", "W", "X",
		"Y", "Z"};
	
	private Paint paint;

	private int cellWidth;

	private int mHeight;

	private float cellHeight;
	
	/**
	 * 字母更新监听
	 * @author poplar
	 *
	 */
	public interface OnLetterChangeListener{
		void onLetterChange(String letter);
	}
	private OnLetterChangeListener onLetterChangeListener;
	

	public OnLetterChangeListener getOnLetterChangeListener() {
		return onLetterChangeListener;
	}

	public void setOnLetterChangeListener(
			OnLetterChangeListener onLetterChangeListener) {
		this.onLetterChangeListener = onLetterChangeListener;
	}

	public QuickIndexBar(Context context) {
		this(context, null);
	}

	public QuickIndexBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public QuickIndexBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.WHITE);
		paint.setTextSize(12f);
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		
		// 计算坐标, 将A-Z字母绘制到界面上
		for (int i = 0; i < LETTERS.length; i++) {
			String text = LETTERS[i];
			
			float x = (int) (cellWidth * 0.5f - paint.measureText(text) * 0.5f);
			
			Rect bounds = new Rect();
			// 把矩形对象赋值
			paint.getTextBounds(text, 0, text.length(), bounds);
			int textHeight = bounds.height();
			float y = (int) (cellHeight * 0.5f + textHeight * 0.5 + cellHeight * i);
			
			// 如果当前绘制的字母和按下的字母索引一样, 用灰色的画笔
			paint.setColor(i == index ? Color.GRAY : Color.WHITE);
			
			canvas.drawText(text, x, y, paint);
		}
		
	}
	
	int index = -1;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		float y = -1;
		int currentIndex = -1;
		switch (MotionEventCompat.getActionMasked(event)) {
			case MotionEvent.ACTION_DOWN:
				y = event.getY();
				currentIndex  = (int) (y / cellHeight);
				if(currentIndex >= 0 && currentIndex < LETTERS.length){
					// 健壮性处理, 在正常范围内
					if(index != currentIndex){
						// 字母的索引发生了变化
						if(onLetterChangeListener != null){
							onLetterChangeListener.onLetterChange(LETTERS[currentIndex]);
						}
						index = currentIndex;
					}
				}
				
				break;
			case MotionEvent.ACTION_MOVE:
				y = event.getY();
				currentIndex  = (int) (y / cellHeight);
				if(currentIndex >= 0 && currentIndex < LETTERS.length){
					// 健壮性处理, 在正常范围内
					if(index != currentIndex){
						// 字母的索引发生了变化
						if(onLetterChangeListener != null){
							onLetterChangeListener.onLetterChange(LETTERS[currentIndex]);
						}
						index = currentIndex;
					}
				}
				
				break;
			case MotionEvent.ACTION_UP:
				index = -1;
				
				break;
	
			default:
				break;
		}
		invalidate();
		
		return true;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
		// 单元格宽度
		cellWidth = getMeasuredWidth();
		
		mHeight = getMeasuredHeight();
		
		// 单元格高度
		cellHeight = mHeight * 1.0f / LETTERS.length;
		
	}
	
	
}
