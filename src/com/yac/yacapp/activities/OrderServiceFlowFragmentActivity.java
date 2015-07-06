package com.yac.yacapp.activities;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yac.yacapp.R;
import com.yac.yacapp.fragments.OrderServiceFlowFragment;
import com.yac.yacapp.fragments.OrderServiceFlowMapTracksFragment;
import com.yac.yacapp.icounts.BaseFragmentActivity;
import com.yac.yacapp.icounts.MyActivityManager;
import com.yac.yacapp.icounts.SlideFragmentActivity;
import com.yac.yacapp.views.AbSlidingSmoothFixTabView;

public class OrderServiceFlowFragmentActivity extends SlideFragmentActivity implements OnClickListener {

	private LinearLayout back_ll;
	private TextView back_tv, actionbar_title_tv;
	private ImageView mClose_img;
	private AbSlidingSmoothFixTabView mAbSlidingTabView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_check_reports);
		MyActivityManager.getInstance().addActivity(this);
		initView();
	}

	private void initView() {
		back_ll = (LinearLayout) findViewById(R.id.back_ll);
		back_tv = (TextView) findViewById(R.id.back_tv);
		actionbar_title_tv = (TextView) findViewById(R.id.actionbar_title_tv);	
		mClose_img = (ImageView) findViewById(R.id.close_img);
		mAbSlidingTabView = (AbSlidingSmoothFixTabView) findViewById(R.id.mAbSlidingTabView);
		
		back_ll.setVisibility(View.VISIBLE);
		back_ll.setOnClickListener(this);
		back_tv.setText("我的订单");
		actionbar_title_tv.setText("服务流程");
		mClose_img.setOnClickListener(this);
		mAbSlidingTabView.getViewPager().setOffscreenPageLimit(2);
		OrderServiceFlowFragment serviceFlowFragment = new OrderServiceFlowFragment();
		OrderServiceFlowMapTracksFragment mapTracksFragment = new OrderServiceFlowMapTracksFragment();
		List<Fragment> fragments = new ArrayList<Fragment>();
		fragments.add(serviceFlowFragment);
		fragments.add(mapTracksFragment);
		List<String> tabTexts = new ArrayList<String>();
		tabTexts.add("养车进度");
		tabTexts.add("爱车位置");
		//设置样式
		mAbSlidingTabView.setTabColor(this.getResources().getColor(R.color.black_text1));
		mAbSlidingTabView.setTabSelectedColor(this.getResources().getColor(R.color.yac_green));
		mAbSlidingTabView.setTabLayoutBackgroundResource(R.color.white);
		mAbSlidingTabView.addItemViews(tabTexts, fragments);
		mAbSlidingTabView.setTabPadding(20, 15, 20, 15);
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.back_ll){
			finish();
		}else if(v.getId() == R.id.close_img){
			MyActivityManager.getInstance().closeActivities();
		}
	}
	
}
