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
import com.yac.yacapp.adapter.MyOrderListviewAdapter;
import com.yac.yacapp.domain.Order;
import com.yac.yacapp.domain.UserInfo;
import com.yac.yacapp.icounts.BaseActivity;
import com.yac.yacapp.icounts.ICounts;
import com.yac.yacapp.utils.Utils;
import com.yac.yacapp.views.MyToast;
import com.yac.yacapp.views.XListView;
import com.yac.yacapp.views.XListView.IXListViewListener;

public class MyOrderPageActivity extends BaseActivity implements ICounts, OnClickListener, IXListViewListener {

	private LinearLayout back_ll;
	private TextView back_tv, actionbar_title_tv;
	private ImageView mClose_img;
	private XListView order_listview;

	private MyOrderListviewAdapter myOrderListviewAdapter;
	private List<Order> mOrders;
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case UPDATE_USERINFO_WHAT:
				String userinfo_json = (String) msg.obj;
				updateUserInfoJson(userinfo_json);
				break;
			case PARSERJSON_FAILURE:
			case NET_FAILURE:
				order_listview.stopRefresh();
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
		initView();
		initData();
	}

	private void initData() {
		String userinfo_json = mSP.getString(SP_UserInfo, null);
		Gson gson = new Gson();
		UserInfo userInfo = gson.fromJson(userinfo_json, UserInfo.class);
		mOrders = userInfo.orders;
		myOrderListviewAdapter.updateData(mOrders);
	}

	private void initView() {
		back_ll = (LinearLayout) findViewById(R.id.back_ll);
		back_tv = (TextView) findViewById(R.id.back_tv);
		actionbar_title_tv = (TextView) findViewById(R.id.actionbar_title_tv);
		mClose_img = (ImageView) findViewById(R.id.close_img);
		order_listview = (XListView) findViewById(R.id.order_listview);

		back_ll.setVisibility(View.VISIBLE);
		back_ll.setOnClickListener(this);
		back_tv.setText("我");
		actionbar_title_tv.setText("我的订单");
		mClose_img.setOnClickListener(this);

		mOrders = new ArrayList<Order>();
		myOrderListviewAdapter = new MyOrderListviewAdapter(this, mOrders);
		order_listview.setAdapter(myOrderListviewAdapter);
		order_listview.setPullLoadEnable(false);
		order_listview.setXListViewListener(this);
		order_listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Order order = mOrders.get(position-1);
				Intent intent = new Intent(MyOrderPageActivity.this, MyOrderDetailsActivity.class);
				intent.putExtra("order_id", order.id);
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
		mOrders = userInfo.orders;
		myOrderListviewAdapter.updateData(mOrders);
		order_listview.stopRefresh();
	}

	@Override
	public void onRefresh() {
		Utils.updateUserInfoJson(this, null, mHandler);
	}

	@Override
	public void onLoadMore() {
	}
}
