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
import com.yac.yacapp.domain.CheckReport;
import com.yac.yacapp.fragments.ReportsQualifiedFragment;
import com.yac.yacapp.fragments.ReportsUnqualifiedFragment;
import com.yac.yacapp.icounts.MyActivityManager;
import com.yac.yacapp.icounts.SlideFragmentActivity;
import com.yac.yacapp.views.AbSlidingSmoothFixTabView;

public class CheckReportsActivity extends SlideFragmentActivity implements OnClickListener {

	private LinearLayout back_ll;
	private TextView back_tv, actionbar_title_tv;
	private ImageView mClose_img;
	private AbSlidingSmoothFixTabView mAbSlidingTabView;
	private CheckReport mCheckReport;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_check_reports);
		MyActivityManager.getInstance().addActivity(this);
		initData();
		initView();
	}

	private void initData() {
		mCheckReport = (CheckReport) getIntent().getSerializableExtra("checkReport");
	}

	private void initView() {
		back_ll = (LinearLayout) findViewById(R.id.back_ll);
		back_tv = (TextView) findViewById(R.id.back_tv);
		actionbar_title_tv = (TextView) findViewById(R.id.actionbar_title_tv);	
		mClose_img = (ImageView) findViewById(R.id.close_img);
		mAbSlidingTabView = (AbSlidingSmoothFixTabView) findViewById(R.id.mAbSlidingTabView);
		
		back_ll.setVisibility(View.VISIBLE);
		back_ll.setOnClickListener(this);
		back_tv.setText("返回");
		actionbar_title_tv.setText("检测报告");
		mClose_img.setOnClickListener(this);
		mAbSlidingTabView.getViewPager().setOffscreenPageLimit(2);
		ReportsUnqualifiedFragment unqualifiedFragment = new ReportsUnqualifiedFragment();
		ReportsQualifiedFragment qualifiedFragment = new ReportsQualifiedFragment();
		List<Fragment> fragments = new ArrayList<Fragment>();
		fragments.add(unqualifiedFragment);
		fragments.add(qualifiedFragment);
		List<String> tabTexts = new ArrayList<String>();
		tabTexts.add(getString(R.string.unqualified_str, mCheckReport.unqualified_size));
		tabTexts.add(getString(R.string.qualified_str, mCheckReport.qualified_size));
		//设置样式
//		mAbSlidingTabView.setTabTextSize((int) getResources().getDimension(R.dimen.text_smallXX));
		mAbSlidingTabView.setTabColor(this.getResources().getColor(R.color.black_text1));
		mAbSlidingTabView.setTabSelectedColor(this.getResources().getColor(R.color.yac_green));
		mAbSlidingTabView.setTabLayoutBackgroundResource(R.color.white);
		mAbSlidingTabView.addItemViews(tabTexts, fragments);
		mAbSlidingTabView.setTabPadding(20, 15, 20, 15);
		//mAbSlidingTabView.setViewPagerScroll(true);
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
