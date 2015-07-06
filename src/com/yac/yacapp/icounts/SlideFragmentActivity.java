package com.yac.yacapp.icounts;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.yac.yacapp.R;
import com.yac.yacapp.views.BaseRelativeLayout;

public class SlideFragmentActivity extends BaseFragmentActivity {

	//右滑操作
	public void setContentView(View view) {
		super.setContentView(getBaseView(view));
	}

	public void setContentView(int layoutResId) {
		setTheme(R.style.Transparent);
		View view = View.inflate(this, layoutResId, null);
		super.setContentView(getBaseView(view));
	}

	private RelativeLayout getBaseView(View view) {
		BaseRelativeLayout baseView = new BaseRelativeLayout(this);
		view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		baseView.addView(view);
		return baseView;
	}
	//右滑结束
	
}
