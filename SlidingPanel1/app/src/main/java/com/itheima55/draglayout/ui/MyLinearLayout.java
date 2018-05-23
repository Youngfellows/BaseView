package com.itheima55.draglayout.ui;

import com.itheima55.draglayout.ui.DragLayout.Status;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * 自定义主面板
 * 获取当前的 控件打开状态, 非关闭状态时, 不能自由滑动
 * @author poplar
 *
 */
public class MyLinearLayout extends LinearLayout {

	private DragLayout dl;

	public MyLinearLayout(Context context) {
		super(context);
	}

	public MyLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setDragLayout(DragLayout dl) {
		this.dl = dl;
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if(dl.getStatus() == Status.Close){
			// 当前是关闭状态, 按之前的处理方式来
			return super.onInterceptTouchEvent(ev);
		}else {
			return true;
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(dl.getStatus() == Status.Close){
			// 当前是关闭状态, 按之前的处理方式来
			return super.onTouchEvent(event);
		}else {
			
			if(event.getAction() == MotionEvent.ACTION_UP){
				// 手指抬起时, 关闭控件
				dl.close();
			}
			
			return true;
		}
	}
	
	
	

}
