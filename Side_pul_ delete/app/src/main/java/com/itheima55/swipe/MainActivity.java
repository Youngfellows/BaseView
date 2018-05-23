package com.itheima55.swipe;

import com.itheima55.swipe.adapter.MyAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ListView lv = (ListView) findViewById(R.id.lv);
		lv.setAdapter(new MyAdapter(this));
		
	}

}
