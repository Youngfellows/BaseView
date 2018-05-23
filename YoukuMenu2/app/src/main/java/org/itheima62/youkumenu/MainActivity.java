package org.itheima62.youkumenu;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MainActivity extends Activity implements OnClickListener {

	private static final String TAG = "MainActivity";
	private ImageView mIvHome;
	private ImageView mIvMenu;

	private RelativeLayout mRlLevel1;// 一级菜单的容器
	private RelativeLayout mRlLevel2;// 二级菜单的容器
	private RelativeLayout mRlLevel3;// 三级菜单的容器

	private boolean isLevel1Display = true;// 一级菜单是否显示的标记
	private boolean isLevel2Display = true;// 二级菜单是否显示的标记
	private boolean isLevel3Display = true;// 三级菜单是否显示的标记

	private int mAnimationCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 初始化View的操作
		initView();
	}

	private void initView() {
		mRlLevel1 = (RelativeLayout) findViewById(R.id.rl_level1);
		mRlLevel2 = (RelativeLayout) findViewById(R.id.rl_level2);
		mRlLevel3 = (RelativeLayout) findViewById(R.id.rl_level3);

		mIvHome = (ImageView) findViewById(R.id.level1_iv_home);
		mIvMenu = (ImageView) findViewById(R.id.level2_iv_menu);

		// 设置点击事件
		mIvHome.setOnClickListener(this);
		mIvMenu.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == mIvHome) {
			clickLevel1Home();
		} else if (v == mIvMenu) {
			clickLevel2Menu();
		}
	}

	private void clickLevel2Menu() {

		if (mAnimationCount > 0) {
			return;
		}

		// 如果三级菜单可见,隐藏三级菜单
		if (isLevel3Display) {
			hideLevel(mRlLevel3, 0);

			// 改变状态
			isLevel3Display = false;
		} else {
			showLevel(mRlLevel3, 0);

			// 改变状态
			isLevel3Display = true;
		}
	}

	// 如果点击home按钮，
	private void clickLevel1Home() {
		// if (当前有动画在执行) {
		if (mAnimationCount > 0) {
			return;
		}

		// 三级的菜单
		// 当 二级菜单和三级菜单都显示的情况下, 二级菜单和三级菜单隐藏
		if (isLevel2Display && isLevel3Display) {
			// 隐藏二级菜单
			hideLevel(mRlLevel2, 100);// 延时
			// 隐藏三级菜单
			hideLevel(mRlLevel3, 0);// 不延时
			// 状态的改变
			isLevel2Display = false;
			isLevel3Display = false;
			return;
		}

		// 当 二级菜单和三级菜单都不显示的情况下
		if (!isLevel2Display && !isLevel3Display) {
			// 显示二级菜单
			showLevel(mRlLevel2, 0);

			// 状态改变
			isLevel2Display = true;
			return;
		}

		// 当 二级菜单显示的情况，并且三级菜单不显示的情况
		if (isLevel2Display && !isLevel3Display) {

			// 隐藏二级菜单
			hideLevel(mRlLevel2, 0);

			// 状态改变
			isLevel2Display = false;
			return;
		}
	}

	private void clickHardwareMenu() {
		// 如果三个菜单都可见,隐藏三个菜单
		if (isLevel1Display && isLevel2Display && isLevel3Display) {
			hideLevel(mRlLevel3, 0);
			hideLevel(mRlLevel2, 100);
			hideLevel(mRlLevel1, 200);

			// 改变状态
			isLevel1Display = false;
			isLevel2Display = false;
			isLevel3Display = false;

			return;
		}

		// 如果三个菜单都不可见,显示三个菜单
		if (!isLevel1Display && !isLevel2Display && !isLevel3Display) {
			showLevel(mRlLevel1, 0);
			showLevel(mRlLevel2, 100);
			showLevel(mRlLevel3, 200);

			// 改变状态
			isLevel1Display = true;
			isLevel2Display = true;
			isLevel3Display = true;

			return;
		}

		// 如果三级菜单不可见，一级和二级可见,隐藏一级和二级
		if (!isLevel3Display && isLevel1Display && isLevel2Display) {
			hideLevel(mRlLevel2, 0);
			hideLevel(mRlLevel1, 100);

			// 改变状态
			isLevel1Display = false;
			isLevel2Display = false;
			return;
		}

		// 如果三级菜单和二级不可见，一级可见,隐藏一级
		if (!isLevel3Display && isLevel1Display && !isLevel2Display) {
			hideLevel(mRlLevel1, 0);

			// 改变状态
			isLevel1Display = false;
			return;
		}

	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			// 点击的是menu按钮
			Log.d(TAG, "点击了硬件的menu");

			clickHardwareMenu();

			// 消费menu点击事件
			return true;
		}

		return super.onKeyUp(keyCode, event);
	}

	/**
	 * 隐藏菜单
	 * 
	 * @param container
	 */
	private void hideLevel(RelativeLayout container, long startOffset) {
		// container.setVisibility(View.GONE);

		int count = container.getChildCount();
		for (int i = 0; i < count; i++) {
			container.getChildAt(i).setEnabled(false);
		}

		RotateAnimation animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 1f);
		animation.setDuration(400);
		animation.setFillAfter(true);
		animation.setStartOffset(startOffset);// 设置动画延时执行

		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// 动画开始播放的回调方法
				mAnimationCount++;
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// 动画结束播放的回调方法
				mAnimationCount--;
			}
		});

		container.startAnimation(animation);
	}

	private void showLevel(RelativeLayout container, long startOffset) {
		// container.setVisibility(View.VISIBLE);

		int count = container.getChildCount();
		for (int i = 0; i < count; i++) {
			container.getChildAt(i).setEnabled(true);
		}

		RotateAnimation animation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 1f);
		animation.setDuration(400);
		animation.setFillAfter(true);
		animation.setStartOffset(startOffset);// 设置动画延时执行

		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// 动画开始播放的回调方法
				mAnimationCount++;
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// 动画结束播放的回调方法
				mAnimationCount--;
			}
		});

		container.startAnimation(animation);
	}

}
