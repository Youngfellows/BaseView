package com.itheima55.draglayout;

import java.util.Random;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima55.draglayout.domain.Cheeses;
import com.itheima55.draglayout.ui.DragLayout;
import com.itheima55.draglayout.ui.MyLinearLayout;
import com.itheima55.draglayout.ui.DragLayout.OnDragChangeListener;
import com.itheima55.draglayout.util.Util;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

public class MainActivity extends Activity {

	private ListView lv_left;
	private ListView lv_main;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		final ImageView iv_header = (ImageView) findViewById(R.id.iv_header);
		
		DragLayout dl = (DragLayout) findViewById(R.id.dl);
		
		MyLinearLayout mll = (MyLinearLayout) findViewById(R.id.mll);
		
		mll.setDragLayout(dl);
		
		dl.setOnDragChangeListener(new OnDragChangeListener() {
			
			@Override
			public void onOpen() {
				Util.showToast(getApplicationContext(), "onOpen");
				
				Random random = new Random();
				lv_left.smoothScrollToPosition(random.nextInt(50));
			}
			
			@Override
			public void onDraging(float percent) {
				System.out.println("percent: " + percent);
				Util.showToast(getApplicationContext(), "onDraging: " + percent);
				
				// 1.0 -> 0.0
				ViewHelper.setAlpha(iv_header, 1 - percent);
			}
			
			@Override
			public void onClose() {
				Util.showToast(getApplicationContext(), "onClose");
//				iv_header.setTranslationX(translationX)
				ObjectAnimator animator = ObjectAnimator.ofFloat(iv_header, "translationX", 15f);
				animator.setInterpolator(new CycleInterpolator(4));
				animator.setDuration(500);
				animator.start();
			}
		});
		
		lv_left = (ListView) findViewById(R.id.lv_left);
		lv_main = (ListView) findViewById(R.id.lv_main);
		
		lv_left.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Cheeses.sCheeseStrings){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				((TextView)view).setTextColor(Color.WHITE);
				return view;
			}
		});
		lv_main.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Cheeses.NAMES));
		
		
	}

}
