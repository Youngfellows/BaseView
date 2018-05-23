package com.itheima55.parallaxdemo.ui;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.ListView;

import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;


/**
 * 视差特效的ListView
 * @author poplar
 *
 */
public class ParallaxList extends ListView {

	private ImageView iv_header;
	private int drawbleHeight;
	private int originalHeight;

	public ParallaxList(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ParallaxList(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ParallaxList(Context context) {
		super(context);
	}
	// 测量完毕之后, 设置进来, 获取高度
	public void setParallaxImage(ImageView iv_header) {
		this.iv_header = iv_header;
		
		// 160px , 记录下,以便做恢复动画使用
		originalHeight = iv_header.getHeight();
		
		// 获取图片自身的尺寸高度
		drawbleHeight = iv_header.getDrawable().getIntrinsicHeight();
		
	}
	@Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
			int scrollY, int scrollRangeX, int scrollRangeY,
			int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
		// deltaY: 超出拉动的偏移量 , 顶部下拉为-  , 底部上拉为+
		// scrollY: 滑动的距离
		// scrollRangeY: 滑动的距离范围
		// maxOverScrollY: 最大的滑动范围
		// isTouchEvent: 是否是触摸超出范围, true触摸, false惯性
		
		System.out.println("deltaY: " + deltaY + " scrollY: " + scrollY
				+ " scrollRangeY: " + scrollRangeY 
				+ " maxOverScrollY: " + maxOverScrollY 
				+ " isTouchEvent: " + isTouchEvent);
		
		if(isTouchEvent && deltaY < 0){
			// 如果是手指触摸, 并且是下拉
			
			// 把deltaY的绝对值, 添加给Header的高度
			int newHeight = (int) (iv_header.getHeight() + Math.abs(deltaY * 0.3f));
			
			if(newHeight <= drawbleHeight){
				// 让新的高度生效
				
				updateHeight(newHeight);
			}
		}
		
		return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
				scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
	}

	private void updateHeight(int newHeight) {
		iv_header.getLayoutParams().height = newHeight;
		iv_header.requestLayout();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		
		switch (ev.getAction()) {
			case MotionEvent.ACTION_UP:
				// 做一个恢复动画
				// 

				final int startHeight = iv_header.getHeight();
				ValueAnimator animator = ValueAnimator.ofInt(1);
				animator.addUpdateListener(new AnimatorUpdateListener() {
					
					@Override
					public void onAnimationUpdate(ValueAnimator value) {
						// 0.0 -> 1.0
						float percent = value.getAnimatedFraction();
						// startHeight --> originalHeight
						//  200 -> 160
						// 更新高度
						Integer newHeight = evaluate(percent, startHeight, originalHeight);

						System.out.println("percent: " + percent + " newHeight: " + newHeight);
						updateHeight(newHeight);
						
					}
				});
				animator.setInterpolator(new OvershootInterpolator());
				animator.setDuration(500);
				animator.start();
				
				break;

			default:
				break;
		}
		
		return super.onTouchEvent(ev);
	}
	
    public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
        int startInt = startValue;
        return (int)(startInt + fraction * (endValue - startInt));
    }
	
}











