package com.itheima55.draglayout.ui;

import com.nineoldandroids.view.ViewHelper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * 侧滑面板, 继承ViewGroup
 * @author poplar
 *
 */
public class DragLayout extends FrameLayout {

	private ViewDragHelper mDragHelper;	
	private ViewGroup mLeftContent;
	private ViewGroup mMainContent;
	private int mHeight;
	private int mWidth;
	private int mRange;
	

	public static enum Status{
		Close, Open, Draging
	}
	
	/**
	 * 定义拖拽状态更新的回调
	 * @author poplar
	 *
	 */
	public interface OnDragChangeListener{
		void onClose();
		void onOpen();
		void onDraging(float percent);
	}
	
	private Status status = Status.Close;
	private OnDragChangeListener onDragChangeListener;
	
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public OnDragChangeListener getOnDragChangeListener() {
		return onDragChangeListener;
	}

	public void setOnDragChangeListener(OnDragChangeListener onDragChangeListener) {
		this.onDragChangeListener = onDragChangeListener;
	}
	
	public DragLayout(Context context) {
		this(context, null);
	}

	public DragLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DragLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		// 在这里进行初始化
		// 1. 通过静态方法获取对象
		mDragHelper = ViewDragHelper.create(this, 1.0f, mCallback);
		
	}
	
	// 2. 将事件转交ViewDragHelper
	@Override
	public boolean onInterceptTouchEvent(android.view.MotionEvent ev) {
		// 交由ViewDragHelper判断是否该拦截
		return mDragHelper.shouldInterceptTouchEvent(ev);
	};
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		// 交由ViewDragHelper处理拦截后的事件
		try {
			mDragHelper.processTouchEvent(event);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return true;
	}

	
	// 3. 处理事件回调
	ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {

		// 1. 决定了当前child是否可以被拖拽
		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			// child: 按下的View
			// pointerId: 多点触摸的id
			System.out.println("tryCaptureView: " + child.toString());
			return true;
		}
		
		// 当View被捕获时, 被调用
		@Override
		public void onViewCaptured(View capturedChild, int activePointerId) {
			super.onViewCaptured(capturedChild, activePointerId);
		}


		// 2. 获取横向的拖拽范围, 不限制真正的左右范围
		// 用于确定动画执行的时长, 横向是否可以滑动 > 0 即可
		@Override
		public int getViewHorizontalDragRange(View child) {
			return mRange;
		}

		// 3. 返回值 决定了要移动到的位置(水平/横向), 此时还未发生真正的移动: 修正将要移动到的位置
		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
//			System.out.println("clampViewPositionHorizontal: left: " + left + "= dx: " + dx + " oldLeft: " + oldLeft);
			// child 被拖拽的控件
			// left 建议移动到的位置
			// dx 建议值和当前位置的偏差 , 向右为+ , 向左为-
			// left = oldLeft + dx;
//			int oldLeft = mMainContent.getLeft();
			
			// 限定拖拽位置
			if(child == mMainContent){
				left = fixLeft(left);
			}
			
			return left;
		}

		// 4. 处理当View位置改变时, 要做的事情: 伴随动画, 更新状态, 执行回调
		@Override
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {
			// changedView 位置发生改变的View
			// left 最新移动到的位置
			// dx 刚刚发生的偏移量
			
			System.out.println("onViewPositionChanged: left: " + left + " dx: " + dx);
			
			// 如果拖拽的是左面板, 强制放回去
			if(changedView == mLeftContent){
				mLeftContent.layout(0, 0, 0 + mWidth, 0 + mHeight);
				// 将mLeftContent的偏移量转交给主面板
				int newLeft = mMainContent.getLeft() + dx;
				// 再次修正
				newLeft = fixLeft(newLeft);
				// 手动让主面板新的位置生效
				mMainContent.layout(newLeft, 0, newLeft + mWidth, 0 + mHeight);
			}
			
			// 做伴随动画, 更新状态, 执行回调
			dispathDragEvent(mMainContent.getLeft());
			
			// 为了兼容低版本, 手动重绘界面
			invalidate();
		}

		// 5. 松手时做的事情, 恢复动画
		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			// releasedChild : 被释放的孩子
			// xvel : 释放时横向速度, 像素/秒  向右+, 向左-
			// yvel : 释放时纵向速度, 像素/秒  向下+, 向上-
			System.out.println("onViewReleased: xvel: " + xvel);
			
			if(xvel == 0 && mMainContent.getLeft() > mRange * 0.5f){
				// 速度是0, 位置大于拖拽范围的一半
				open();
			}else if (xvel > 0) {
				open();
			}else {
				close();
			}
		}		
		
		@Override
		public void onViewDragStateChanged(int state) {
			// TODO Auto-generated method stub
			super.onViewDragStateChanged(state);
		}
	};

	/**
	 * 修正left值
	 * @param left
	 * @return
	 */
	private int fixLeft(int left) {
		if(left < 0){
			return 0;
		}else if (left > mRange) {
			return mRange;
		}
		return left;
	}

	/**
	 * 伴随动画, 状态更新, 执行回调
	 * @param left
	 */
	protected void dispathDragEvent(int left) {
		// 0.0 -> 1.0
		float percent = left * 1.0f / mRange; 
//		System.out.println(" percent : " + percent);
		
		// 执行动画
		animViews(percent);
		
		
		// 更新状态
		Status preStatus = status;
		status = updateStatus(percent);
		
		if(onDragChangeListener != null){
			
			// 拖拽状态更新
			onDragChangeListener.onDraging(percent);
			
			if(preStatus != status){
				// 状态发生了改变
				if(status == Status.Open){
					onDragChangeListener.onOpen();
				}else if (status == Status.Close) {
					onDragChangeListener.onClose();
				}
			}
		}

	}

	private Status updateStatus(float percent) {
		if(percent == 0f){
			return Status.Close;
		}else if (percent == 1.0f) {
			return Status.Open;
		}
		return Status.Draging;
	}

	private void animViews(float percent) {
		//		- 主面板: 缩放动画
				// 1.0 -> 0.8 >>> 0.8f + (1 - percent) * 0.2f
				
		//		mMainContent.setScaleX(0.8f + (1 - percent) * 0.2f);
		//		mMainContent.setScaleY(0.8f + (1 - percent) * 0.2f);
		//		float f = 0.8f + (1 - percent) * 0.2f;
				float f = evaluate(percent, 1.0f, 0.8f);
				ViewHelper.setScaleX(mMainContent, f);
				ViewHelper.setScaleY(mMainContent, f);
				
		//		- 左面板: 缩放动画, 平移动画, 透明度动画
				// 0.5 -> 1.0
				ViewHelper.setScaleX(mLeftContent, evaluate(percent, 0.5f, 1.0f));
				ViewHelper.setScaleY(mLeftContent, evaluate(percent, 0.5f, 1.0f));
				// -mWidth/2  -> 0
				ViewHelper.setTranslationX(mLeftContent, evaluate(percent, -mWidth * 0.5f, 0));
				// 0.2 -> 1.0
				ViewHelper.setAlpha(mLeftContent, evaluate(percent, 0.2f, 1.0f));
		//		- 背  景: 亮度变化 黑色 -> 透明
				getBackground().setColorFilter((Integer)evaluateColor(percent, Color.BLACK, Color.TRANSPARENT), Mode.SRC_OVER);
	}
	// 计算过度值
    public Float evaluate(float fraction, Number startValue, Number endValue) {
        float startFloat = startValue.floatValue();
        return startFloat + fraction * (endValue.floatValue() - startFloat);
    }
    /**
     * 计算过度颜色值
     * @param fraction
     * @param startValue
     * @param endValue
     * @return
     */
    public Object evaluateColor(float fraction, Object startValue, Object endValue) {
        int startInt = (Integer) startValue;
        int startA = (startInt >> 24) & 0xff;
        int startR = (startInt >> 16) & 0xff;
        int startG = (startInt >> 8) & 0xff;
        int startB = startInt & 0xff;

        int endInt = (Integer) endValue;
        int endA = (endInt >> 24) & 0xff;
        int endR = (endInt >> 16) & 0xff;
        int endG = (endInt >> 8) & 0xff;
        int endB = endInt & 0xff;

        return (int)((startA + (int)(fraction * (endA - startA))) << 24) |
                (int)((startR + (int)(fraction * (endR - startR))) << 16) |
                (int)((startG + (int)(fraction * (endG - startG))) << 8) |
                (int)((startB + (int)(fraction * (endB - startB))));
    }
    
	@Override
	public void computeScroll() {
		super.computeScroll();
		
		// b. 维持动画的继续, 返回true, 继续执行
		if(mDragHelper.continueSettling(true)){
			// 执行延时的重绘界面操作
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}
	
	/**
	 * 执行关闭动画
	 */
	public void close() {
		close(true);
	}
	
	public void close(boolean isSmooth){
		int finalLeft = 0;
		if(isSmooth){
			// a. 触发一个平滑动画 . 需要滑动时, 会返回true, 需要手动重回界面
			if(mDragHelper.smoothSlideViewTo(mMainContent, finalLeft, 0)){
				// 执行延时的重绘界面操作
				ViewCompat.postInvalidateOnAnimation(this);
			}
		}else {
			mMainContent.layout(finalLeft, 0, finalLeft + mWidth, 0 + mHeight);
		}
	}
	
	/**
	 * 执行开启动画
	 */
	public void open() {
		open(true);
	}
	
	public void open(boolean isSmooth){
		int finalLeft = mRange;
		if(isSmooth){
			// a. 触发一个平滑动画 . 需要滑动时, 会返回true, 需要手动重回界面
			if(mDragHelper.smoothSlideViewTo(mMainContent, finalLeft, 0)){
				// 执行延时的重绘界面操作
				ViewCompat.postInvalidateOnAnimation(this);
			}
		}else {
			mMainContent.layout(finalLeft, 0, finalLeft + mWidth, 0 + mHeight);
		}
	}
	

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	// 当测量完毕, 发现控件的宽高有变化时, 会被调用
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
		// 获取宽高
		mHeight = getMeasuredHeight();
		mWidth = getMeasuredWidth();
		
		// 计算拖拽范围
		mRange = (int) (mWidth * 0.6f);
		
	}
	
	/**
	 * 当xml所有View填充完毕时调用 , 查找控件比较合适
	 */
	protected void onFinishInflate() {
		super.onFinishInflate();
		
		// 健壮性检查
		// Github
		// 孩子的个数
		if(getChildCount() < 2){
			throw new IllegalStateException("You must have 2 children at least! 至少有俩孩子!");
		}
		// 孩子的类型检查, 必须是ViewGroup的子类
		if(!(getChildAt(0) instanceof ViewGroup) || !(getChildAt(1) instanceof ViewGroup)){
			throw new IllegalArgumentException("Your child must be an instance of ViewGroup!, 孩子必须是ViewGroup的子类!");
		}
		
		mLeftContent = (ViewGroup) getChildAt(0);
		mMainContent = (ViewGroup) getChildAt(1);
		
	};
	
}







