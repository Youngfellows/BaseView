package com.itheima55.swipe.adapter;

import java.util.HashSet;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.itheima55.swipe.MainActivity;
import com.itheima55.swipe.R;
import com.itheima55.swipe.ui.SwipeLayout;
import com.itheima55.swipe.ui.SwipeLayout.OnSwipeChangeListener;
import com.itheima55.swipe.util.Cheeses;
import com.itheima55.swipe.util.Utils;

public class MyAdapter extends BaseAdapter {

	private HashSet<SwipeLayout> openedItems = new HashSet<SwipeLayout>();
	
	public MyAdapter(Context context) {
		super();
		this.context = context;
	}

	private Context context;
	
	@Override
	public int getCount() {
		return Cheeses.NAMES.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		if(convertView == null){
			view = View.inflate(context, R.layout.item_person, null);
		}else {
			view = convertView;
		}
		
		TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
		String str = Cheeses.NAMES[position];
		tv_name.setText(str);
		
		SwipeLayout sl = (SwipeLayout)view;
		sl.close(false);
		
		sl.setOnSwipeChangeListener(new OnSwipeChangeListener() {
			
			@Override
			public void onStartOpen(SwipeLayout layout) {
				for (SwipeLayout swipeLayout : openedItems) {
					swipeLayout.close();
				}
				openedItems.clear();
			}
			
			@Override
			public void onStartClose(SwipeLayout layout) {
			}
			
			@Override
			public void onOpen(SwipeLayout layout) {
				openedItems.add(layout);
			}
			
			@Override
			public void onDraging(SwipeLayout layout) {
				
			}
			
			@Override
			public void onClose(SwipeLayout layout) {
				openedItems.remove(layout);
			}
		});
		
		return view;
	}
}
