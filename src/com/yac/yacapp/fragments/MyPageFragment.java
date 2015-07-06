package com.yac.yacapp.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.yac.yacapp.R;
import com.yac.yacapp.activities.MyLocationActivity;
import com.yac.yacapp.activities.MyCarActivity;
import com.yac.yacapp.activities.MyCheckReportsActivity;
import com.yac.yacapp.activities.MyCouponActivity;
import com.yac.yacapp.activities.MyOrderPageActivity;
import com.yac.yacapp.activities.UserBasicActivity;
import com.yac.yacapp.domain.UserBasic;
import com.yac.yacapp.domain.UserInfo;
import com.yac.yacapp.icounts.ICounts;

public class MyPageFragment extends Fragment implements ICounts, OnClickListener {

	private ImageView user_avatar_img;
	private TextView user_avator_tv;
	private RelativeLayout myorder_rl, mycar_rl, myshopping_rl, mycoupons_rl, myreport_rl, myaddress_rl;
	private TextView myorder_num_tv, mycar_num_tv, myshopping_num_tv, mycoupons_num_tv, myreport_num_tv, myaddress_num_tv;
	private RelativeLayout myorder_num_rl, mycar_num_rl, myshopping_num_rl, mycoupons_num_rl, myreport_num_rl, myaddress_num_rl;
	private SharedPreferences mSp;
	private Gson mGson;
	private ImageLoader mImageLoader;
	private DisplayImageOptions mOptions;
	private UserBasic mUserBasic;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_mypage, container, false);
		mSp = getActivity().getSharedPreferences("yacapp_sp", 0);
		mGson = new Gson();
		mImageLoader = ImageLoader.getInstance();
		if (!mImageLoader.isInited()) {
			mImageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
		}
		mOptions = new DisplayImageOptions.Builder().cacheOnDisk(true).showImageForEmptyUri(R.drawable.default_avatar).showImageOnFail(R.drawable.default_avatar).build();
		initView(view);
		initData();
		registerBoradcastReceiver();
		return view;
	}

	private void initData() {
		String userinfo_str = mSp.getString(SP_UserInfo, "");
		UserInfo userInfo = mGson.fromJson(userinfo_str, UserInfo.class);
		if (userInfo != null) {
			mUserBasic = userInfo.basic;
			if (mUserBasic != null && mUserBasic.avatar_img != null) {
				mImageLoader.displayImage(mUserBasic.avatar_img.thumbnail_url, user_avatar_img, mOptions);
			}
			if (mUserBasic != null)
				user_avator_tv.setText(mUserBasic.name);
			if (userInfo.orders != null && userInfo.orders.size() > 0) {
				myorder_num_tv.setText(String.valueOf(userInfo.orders.size()));
				myorder_num_rl.setVisibility(View.VISIBLE);
			}
			if (userInfo.cars != null && userInfo.cars.size() > 0) {
				mycar_num_tv.setText(String.valueOf(userInfo.cars.size()));
				mycar_num_rl.setVisibility(View.VISIBLE);
			}
			if (userInfo.coupons != null && userInfo.coupons.size() > 0) {
				mycoupons_num_tv.setText(String.valueOf(userInfo.coupons.size()));
				mycoupons_num_rl.setVisibility(View.VISIBLE);
			}
			if (userInfo.check_reports != null && userInfo.check_reports.size() > 0) {
				myreport_num_tv.setText(String.valueOf(userInfo.check_reports.size()));
				myreport_num_rl.setVisibility(View.VISIBLE);
			}
			if (userInfo.location != null && userInfo.location.size() > 0) {
				myaddress_num_tv.setText(String.valueOf(userInfo.location.size()));
				myaddress_num_rl.setVisibility(View.VISIBLE);
			}
		}
	}

	private void initView(View view) {
		user_avatar_img = (ImageView) view.findViewById(R.id.user_avatar_img);
		user_avator_tv = (TextView) view.findViewById(R.id.user_avator_tv);
		myorder_num_tv = (TextView) view.findViewById(R.id.myorder_num_tv);
		mycar_num_tv = (TextView) view.findViewById(R.id.mycar_num_tv);
		myshopping_num_tv = (TextView) view.findViewById(R.id.myshopping_num_tv);
		mycoupons_num_tv = (TextView) view.findViewById(R.id.mycoupons_num_tv);
		myreport_num_tv = (TextView) view.findViewById(R.id.myreport_num_tv);
		myaddress_num_tv = (TextView) view.findViewById(R.id.myaddress_num_tv);

		myorder_num_rl = (RelativeLayout) view.findViewById(R.id.myorder_num_rl);
		mycar_num_rl = (RelativeLayout) view.findViewById(R.id.mycar_num_rl);
		myshopping_num_rl = (RelativeLayout) view.findViewById(R.id.myshopping_num_rl);
		mycoupons_num_rl = (RelativeLayout) view.findViewById(R.id.mycoupons_num_rl);
		myreport_num_rl = (RelativeLayout) view.findViewById(R.id.myreport_num_rl);
		myaddress_num_rl = (RelativeLayout) view.findViewById(R.id.myaddress_num_rl);

		myorder_rl = (RelativeLayout) view.findViewById(R.id.myorder_rl);
		mycar_rl = (RelativeLayout) view.findViewById(R.id.mycar_rl);
		myshopping_rl = (RelativeLayout) view.findViewById(R.id.myshopping_rl);
		mycoupons_rl = (RelativeLayout) view.findViewById(R.id.mycoupons_rl);
		myreport_rl = (RelativeLayout) view.findViewById(R.id.myreport_rl);
		myaddress_rl = (RelativeLayout) view.findViewById(R.id.myaddress_rl);

		user_avatar_img.setOnClickListener(this);
		user_avator_tv.setOnClickListener(this);
		myorder_rl.setOnClickListener(this);
		mycar_rl.setOnClickListener(this);
		myshopping_rl.setOnClickListener(this);
		mycoupons_rl.setOnClickListener(this);
		myreport_rl.setOnClickListener(this);
		myaddress_rl.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.user_avatar_img:
		case R.id.user_avator_tv:
			Intent intent = new Intent(getActivity(), UserBasicActivity.class);
			intent.putExtra("UserBasic", mUserBasic);
			getActivity().startActivity(intent);
			break;
		case R.id.myorder_rl:
			getActivity().startActivity(new Intent(getActivity(), MyOrderPageActivity.class));
			break;
		case R.id.mycar_rl:
			getActivity().startActivity(new Intent(getActivity(), MyCarActivity.class));
			break;
		case R.id.myshopping_rl:
			break;
		case R.id.mycoupons_rl:
			getActivity().startActivity(new Intent(getActivity(), MyCouponActivity.class));
			break;
		case R.id.myreport_rl:
			getActivity().startActivity(new Intent(getActivity(), MyCheckReportsActivity.class));
			break;
		case R.id.myaddress_rl:
			getActivity().startActivity(new Intent(getActivity(), MyLocationActivity.class));
			break;
		default:
			break;
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		getActivity().unregisterReceiver(userinfoUpdateReceiver);
	}

	private void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(UPDATA_USERINFO_ACTION);
		// 注册广播
		getActivity().registerReceiver(userinfoUpdateReceiver, myIntentFilter);
	}

	/**
	 * userinfo is changed.
	 * 
	 * @author Administrator
	 */
	private BroadcastReceiver userinfoUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (UPDATA_USERINFO_ACTION.equals(action)) {
				initData();
			}
		}
	};

}
