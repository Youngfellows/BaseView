package com.itheima55.gooview.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.itheima55.gooview.util.GeometryUtil;
import com.itheima55.gooview.util.Utils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

/**
 * 粘性控件
 * 
 * @author poplar
 * 
 */
public class GooView extends View {

	private Paint paint;

	public GooView(Context context) {
		this(context, null);
	}

	public GooView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public GooView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		// 初始化一个抗锯齿的画笔
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.RED);

	}
	
	public interface OnEventUpdateListener{
		void onDisappear();
		void onReset(boolean isOutOfRange);
	}
	private OnEventUpdateListener onEventUpdateListener;
	
	public OnEventUpdateListener getOnEventUpdateListener() {
		return onEventUpdateListener;
	}

	public void setOnEventUpdateListener(OnEventUpdateListener onEventUpdateListener) {
		this.onEventUpdateListener = onEventUpdateListener;
	}

	PointF[] mStickPoints = new PointF[]{
			new PointF(250f, 250f),	
			new PointF(250f, 350f)
	};
	
	PointF[] mDragPoints = new PointF[]{
			new PointF(50f, 250f),	
			new PointF(50f, 350f)
	};
	PointF mControlPoint = new PointF(150f, 300f);
	PointF mDragCenter = new PointF(100f, 100f);
	float mDragRadius = 18f;
	
	PointF mStickCenter = new PointF(150f, 150f);
	float mStickRadius = 12f;

	private int statusBarHeight;

	@Override
	protected void onDraw(Canvas canvas) {

		// 计算连接部分

			// 计算固定圆半径(根据两圆圆心的距离)
			float tempStickRadius = getTempStickRadius();
			
			// 四个附着点坐标
			float yOffset = mStickCenter.y - mDragCenter.y;
			float xOffset = mStickCenter.x - mDragCenter.x;
			Double lineK = null;
			if(xOffset != 0){
				lineK = (double) (yOffset / xOffset);
			}
			// 获取过拖拽圆圆心的直线的两个交点
			mDragPoints = GeometryUtil.getIntersectionPoints(mDragCenter, mDragRadius, lineK);
			// 获取过固定圆圆心的直线的两个交点
			mStickPoints = GeometryUtil.getIntersectionPoints(mStickCenter, tempStickRadius, lineK);
			
			// 控制点坐标
			mControlPoint = GeometryUtil.getMiddlePoint(mDragCenter, mStickCenter);
			
			
			
		// 保存移动之前画布的位置, 平移画布
		canvas.save();
		canvas.translate(0, -statusBarHeight);
			
			// 画最大范围的圆环
			paint.setStyle(Style.STROKE);
			canvas.drawCircle(mStickCenter.x, mStickCenter.y, farestDistance, paint);
			paint.setStyle(Style.FILL);

			// 没有消失才绘制所有控件
		if(!isDisappear){
			// 没有超出范围, 才绘制 固定圆和连接部分
			if(!isOutOfRange){
					// 画出4个附着点 (参考用)
					paint.setColor(Color.BLUE);
					canvas.drawCircle(mDragPoints[0].x, mDragPoints[0].y, 3f, paint);
					canvas.drawCircle(mDragPoints[1].x, mDragPoints[1].y, 3f, paint);
					canvas.drawCircle(mStickPoints[0].x, mStickPoints[0].y, 3f, paint);
					canvas.drawCircle(mStickPoints[1].x, mStickPoints[1].y, 3f, paint);
					paint.setColor(Color.RED);
				
				// 画中间连接部分
				Path path = new Path();
				// 把路径的起点设置为250,250.
				path.moveTo(mStickPoints[0].x, mStickPoints[0].y);
				// 从 1 -> 2
				path.quadTo(mControlPoint.x, mControlPoint.y, mDragPoints[0].x, mDragPoints[0].y);
				// 从2 -> 3
				path.lineTo(mDragPoints[1].x, mDragPoints[1].y);
				// 从3 -> 4
				path.quadTo(mControlPoint.x, mControlPoint.y, mStickPoints[1].x, mStickPoints[1].y);
				// 封闭区域
				path.close();
				
				canvas.drawPath(path, paint);
				
				// 画固定圆
				canvas.drawCircle(mStickCenter.x, mStickCenter.y, tempStickRadius, paint);
			}
			
			// 画拖拽圆
			canvas.drawCircle(mDragCenter.x, mDragCenter.y, mDragRadius, paint);
		}
		
		
		// 恢复上一次保存的状态
		canvas.restore();
	}
	
	float farestDistance = 80f;

	private boolean isOutOfRange = false;

	private boolean isDisappear;
	private float getTempStickRadius() {
		// 获取两圆圆心距离
		float distance = GeometryUtil.getDistanceBetween2Points(mDragCenter, mStickCenter);
		
		// 让distance不大于80f
		distance = Math.min(distance, farestDistance);
		
		// 0 -> 80f
		float percent = distance / farestDistance;
		
		System.out.println("percent: " + percent);
		
		// 0.0 -> 1.0
		// 半径12f -> 4f
		return evaluate(percent, mStickRadius, mStickRadius * 0.33f);
	}
	
    public Float evaluate(float fraction, Number startValue, Number endValue) {
        float startFloat = startValue.floatValue();
        return startFloat + fraction * (endValue.floatValue() - startFloat);
    }

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		float rawX;
		float rawY;
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				isOutOfRange = false;
				isDisappear = false;
				
				rawX = event.getRawX();
				rawY = event.getRawY();
				
				// 更新拖拽圆圆心坐标, 重绘界面
				updateDragCenter(rawX, rawY);
				
				break;
			case MotionEvent.ACTION_MOVE:
				rawX = event.getRawX();
				rawY = event.getRawY();
				
				// 更新拖拽圆圆心坐标, 重绘界面
				updateDragCenter(rawX, rawY);
				
				// 判断两圆圆心的距离, 超出最大范围,断开
				float d = GeometryUtil.getDistanceBetween2Points(mDragCenter, mStickCenter);
				if(d > farestDistance){
					isOutOfRange = true;
					invalidate();
				}
				
				break;
			case MotionEvent.ACTION_UP:
				if(isOutOfRange){
					// 刚刚超出了范围
					float distance = GeometryUtil.getDistanceBetween2Points(mDragCenter, mStickCenter);
					if(distance > farestDistance){
						if(onEventUpdateListener != null){
							onEventUpdateListener.onDisappear();
						}
						
						// 消失了
						isDisappear = true;
						invalidate();
					}else {
						// 将拖拽圆恢复到固定圆圆心
						updateDragCenter(mStickCenter.x, mStickCenter.y);
						if(onEventUpdateListener != null){
							onEventUpdateListener.onReset(true);
						}
					}
				}else {
					// 刚刚没有超出范围
					// 不断更新拖拽圆圆心坐标, 靠近固定圆圆心
					final PointF startP = new PointF(mDragCenter.x, mDragCenter.y);
					
					ValueAnimator anim = ValueAnimator.ofFloat(1.0f);
					anim.addUpdateListener(new AnimatorUpdateListener() {
						
						@Override
						public void onAnimationUpdate(ValueAnimator animation) {
							float percent = animation.getAnimatedFraction();
							// 0.0 -> 1.0
							PointF newP = GeometryUtil.getPointByPercent(startP, mStickCenter, percent);
							updateDragCenter(newP.x, newP.y);
						}
					});
					// 设置动画执行的状态监听
					anim.addListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							if(onEventUpdateListener != null){
								onEventUpdateListener.onReset(false);
							}
						}
					});
					
					anim.setInterpolator(new OvershootInterpolator(3));
					anim.setDuration(300);
					anim.start();
				}
				
				break;
			default:
				break;
		}
		
		return true;
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
		statusBarHeight = Utils.getStatusBarHeight(this);
	}

	/**
	 * 更新拖拽圆圆心坐标,并重绘界面
	 * @param rawX
	 * @param rawY
	 */
	private void updateDragCenter(float rawX, float rawY) {
		mDragCenter.set(rawX, rawY);
		invalidate();
	}

}
