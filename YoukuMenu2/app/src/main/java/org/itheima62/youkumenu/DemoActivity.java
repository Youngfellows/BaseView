package org.itheima62.youkumenu;

import android.app.Activity;
import android.os.Bundle;

public class DemoActivity extends Activity {

	private YoukuView view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_demo);

		view = (YoukuView) findViewById(R.id.yv);

//		view.setOnClickChannel4(null);
	}
}
