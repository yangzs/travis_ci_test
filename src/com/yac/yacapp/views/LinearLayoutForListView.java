package com.yac.yacapp.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

public class LinearLayoutForListView extends LinearLayout {
	
	private BaseAdapter adapter;
	
	public LinearLayoutForListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public LinearLayoutForListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LinearLayoutForListView(Context context) {
		super(context);
	}

	/**
     * 绑定布局
     */
    public void bindLinearLayout() {
        int count = adapter.getCount();
        this.removeAllViews();
        for (int i = 0; i < count; i++) {
            View v = adapter.getView(i, null, null);
            addView(v, i);
        }
    }
    
	public void setAdapter(BaseAdapter adapter) {
		this.adapter = adapter;
		bindLinearLayout();
	}
}
