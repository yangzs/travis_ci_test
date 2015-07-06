package com.yac.yacapp.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class AdPageAdapter extends PagerAdapter {
	private List<View> views = null;

	public AdPageAdapter(List<View> views) {
		this.views = views;
	}

	public void updateDate(List<View> views) {
		this.views = views;
		notifyDataSetChanged();
	}
	
	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		//((ViewPager) container).removeView(views.get(position));
		((ViewPager) container).removeView((View)object);
	}

	@Override
	public int getCount() {
		return views.size();
	}

	@Override
	public Object instantiateItem(View container, int position) {
		((ViewPager) container).addView(views.get(position), 0);
		return views.get(position);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}
}