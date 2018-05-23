package com.itheima55.parallaxdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.itheima55.parallaxdemo.domain.Cheeses;
import com.itheima55.parallaxdemo.ui.ParallaxList;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final ParallaxList pl = (ParallaxList) findViewById(R.id.pl);
		
		final View header = View.inflate(this, R.layout.layout_header, null);
		final ImageView iv_header = (ImageView) header.findViewById(R.id.iv_header);
		
		
		header.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				// 当header被界面添加完毕(测量结束), 执行此方法, 可以获取到ImageView的宽高属性
				pl.setParallaxImage(iv_header);
				header.getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}
		});
		
		
		pl.addHeaderView(header);

		// 给ListView添加一个Header, 随着ListView的下拉, 更新Header的高度
		
		pl.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Cheeses.NAMES));
	}

}
