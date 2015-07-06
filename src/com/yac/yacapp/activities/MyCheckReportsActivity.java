package com.yac.yacapp.activities;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.yac.yacapp.R;
import com.yac.yacapp.adapter.MyCheckReportsAdapter;
import com.yac.yacapp.domain.CheckReport;
import com.yac.yacapp.domain.UserInfo;
import com.yac.yacapp.icounts.BaseActivity;
import com.yac.yacapp.icounts.ICounts;
import com.yac.yacapp.utils.Utils;
import com.yac.yacapp.views.MyProgressDialog;
import com.yac.yacapp.views.MyToast;
import com.yac.yacapp.views.XListView;
import com.yac.yacapp.views.XListView.IXListViewListener;

public class MyCheckReportsActivity extends BaseActivity implements ICounts, OnClickListener, IXListViewListener {
	
	private LinearLayout back_ll;
	private TextView back_tv, actionbar_title_tv;
	private ImageView mClose_img;
	private XListView reportsListView;
	
	private List<CheckReport> mCheckReports;
	private MyCheckReportsAdapter myCheckReportsAdapter;
	private MyProgressDialog myProgressDialog;
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case UPDATE_USERINFO_WHAT:
				String userinfo_json = (String) msg.obj;
				updateUserInfoJson(userinfo_json);
				break;
			case PARSERJSON_FAILURE:
			case NET_FAILURE:
				if (myProgressDialog != null && myProgressDialog.isShowing()) {
					myProgressDialog.dismiss();
				}
				reportsListView.stopRefresh();
				String message = (String) msg.obj;
				handlePARSERJSON_NET_FAILURE(message);
				break;
			default:
				break;
			}
		};
	}; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listview_page);
		myProgressDialog = MyProgressDialog.createDialog(this);
		initView();
		initData();
	}

	private void initData() {
		//获取本地的缓存，然后下拉可以进行刷新
		String userinfo_str = mSP.getString(SP_UserInfo, null);
		UserInfo userinfo = new Gson().fromJson(userinfo_str, UserInfo.class);
		mCheckReports = userinfo.check_reports;
		if(mCheckReports != null){
			myCheckReportsAdapter.updateData(mCheckReports);
		}else{
			Utils.updateUserInfoJson(this, null, mHandler);
		}
	}

	private void initView() {
		back_ll = (LinearLayout) findViewById(R.id.back_ll);
		back_tv = (TextView) findViewById(R.id.back_tv);
		actionbar_title_tv = (TextView) findViewById(R.id.actionbar_title_tv);
		mClose_img = (ImageView) findViewById(R.id.close_img);
		reportsListView = (XListView) findViewById(R.id.order_listview);
		
		back_ll.setVisibility(View.VISIBLE);
		back_ll.setOnClickListener(this);
		back_tv.setText("我");
		actionbar_title_tv.setText("我的检测报告单");
		mClose_img.setOnClickListener(this);
		mCheckReports = new ArrayList<CheckReport>();
		myCheckReportsAdapter = new MyCheckReportsAdapter(this, mCheckReports);
		reportsListView.setAdapter(myCheckReportsAdapter);
		reportsListView.setPullLoadEnable(false);
		reportsListView.setXListViewListener(this);
		reportsListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				CheckReport checkReport = mCheckReports.get(position-1);
				Intent intent = new Intent(getApplicationContext(), CheckReportsActivity.class);
				intent.putExtra("checkReport", checkReport);
				startActivity(intent);
			}
		});
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.back_ll){
			finish();
		}else if(v.getId() == R.id.close_img){
			closeActivities();
		}
	}

	protected void updateUserInfoJson(String userinfo_json) {
		mSP.edit().putString(SP_UserInfo, userinfo_json).commit();
		UserInfo userInfo = new Gson().fromJson(userinfo_json, UserInfo.class);
		mCheckReports = userInfo.check_reports;
		myCheckReportsAdapter.updateData(mCheckReports);
		reportsListView.stopRefresh();
		reportsListView.setRefreshTime("刚刚");
	}

	@Override
	public void onRefresh() {
		//下拉刷新
		Utils.updateUserInfoJson(this, null, mHandler);
	}

	@Override
	public void onLoadMore() {
		//底部加载更多，不需要
	}
	
}
