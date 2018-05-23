package com.itheima55.quickindex;

import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.itheima55.quickindex.adapter.MyAdapter;
import com.itheima55.quickindex.domain.Cheeses;
import com.itheima55.quickindex.domain.GoodMan;
import com.itheima55.quickindex.ui.QuickIndexBar;
import com.itheima55.quickindex.ui.QuickIndexBar.OnLetterChangeListener;
import com.itheima55.quickindex.utils.Util;

public class MainActivity extends Activity {

	private TextView tv_index_center;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		tv_index_center = (TextView) findViewById(R.id.tv_index_center);
		
		final ListView lv_content = (ListView) findViewById(R.id.lv_content);
		
		final ArrayList<GoodMan> persons = new ArrayList<GoodMan>();
		
		// 填充数据, 并排序
		fillAndSortData(persons);
		
		lv_content.setAdapter(new MyAdapter(persons, this));
		
		
		QuickIndexBar bar = (QuickIndexBar) findViewById(R.id.bar);
		bar.setOnLetterChangeListener(new OnLetterChangeListener() {
			@Override
			public void onLetterChange(String letter) {
				System.out.println("letter: " + letter);
//				Util.showToast(getApplicationContext(), letter);
				
				showLetter(letter);
				
				// 从集合中查找第一个拼音首字母为letter的索引, 进行跳转
				for (int i = 0; i < persons.size(); i++) {
					GoodMan goodMan = persons.get(i);
					String s = goodMan.getPinyin().charAt(0) + "";
					if(TextUtils.equals(s, letter)){
						// 匹配成功, 中断循环, 跳转到i位置
						lv_content.setSelection(i);
						break;
					}
				}
			}
		});
		
		
	}
	
	private Handler mHandler = new Handler();

	/**
	 * 显示字母提示
	 * @param letter
	 */
	protected void showLetter(String letter) {
		tv_index_center.setVisibility(View.VISIBLE);
		tv_index_center.setText(letter);
		
		// 取消掉刚刚所有的演示操作
		mHandler.removeCallbacksAndMessages(null);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// 隐藏
				tv_index_center.setVisibility(View.GONE);
			}
		}, 1000);
		
	}

	/**
	 * 填充,排序
	 * @param persons
	 */
	private void fillAndSortData(ArrayList<GoodMan> persons) {
		for (int i = 0; i < Cheeses.NAMES.length; i++) {
			persons.add(new GoodMan(Cheeses.NAMES[i]));
		}
		// 排序
		Collections.sort(persons);
	}

}
