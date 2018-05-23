package org.itheima62.propertyanimation;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private ImageView iv;
	private View v;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		iv = (ImageView) findViewById(R.id.iv);
		v = findViewById(R.id.v);

		iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "点击了我",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	public void click(View view) {

		// 位移动画

		// 补间动画的实现
		// TranslateAnimation animation = new TranslateAnimation(0, 300, 0, 0);
		// animation.setDuration(4000);
		// iv.startAnimation(animation);

		// android 3.0之后的操作

		// target：谁要做动画
		ObjectAnimator animator = ObjectAnimator.ofFloat(iv, "translationX",
				0f, 300f);
		animator.setDuration(4000);
		animator.start();

		// setXXXX(参数为1)
		// 参数类型

		// v.setBackgroundColor();
		ObjectAnimator colorOA = ObjectAnimator.ofObject(v, "backgroundColor",
				new ArgbEvaluator(), Color.RED, Color.BLACK, Color.BLUE);
		colorOA.setDuration(2000);
		colorOA.setRepeatCount(ObjectAnimator.INFINITE);
		colorOA.setRepeatMode(ObjectAnimator.REVERSE);
		colorOA.start();

	}
}
