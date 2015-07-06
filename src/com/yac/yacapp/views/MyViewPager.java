package com.yac.yacapp.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyViewPager extends ViewPager {

	/**
	 * false can't scroll
	 * true  can scroll
	 */
	private boolean isCanScroll = false;
	
	public MyViewPager(Context context) {
		super(context);
	}
	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public void setScroll(boolean scroll) {
		this.isCanScroll = scroll;
	}
	@Override
	public void scrollTo(int x, int y) {
		super.scrollTo(x, y);
	}
	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		if(isCanScroll)
			return super.onTouchEvent(arg0);
		else
			return false;
	}
	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		if(isCanScroll)
			return super.onInterceptTouchEvent(arg0);
		else
			return false;
	}
	@Override
	public void setCurrentItem(int item, boolean smoothScroll) {
		super.setCurrentItem(item, smoothScroll);
	}
	@Override
	public void setCurrentItem(int item) {
		super.setCurrentItem(item);
	}

}
