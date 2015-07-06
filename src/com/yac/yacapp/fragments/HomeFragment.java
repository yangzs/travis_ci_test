package com.yac.yacapp.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.yac.yacapp.R;
import com.yac.yacapp.activities.CarPartsDetailsActivity;
import com.yac.yacapp.activities.PickCarActivity;
import com.yac.yacapp.adapter.AdPageAdapter;
import com.yac.yacapp.adapter.MyCarCareAdapter;
import com.yac.yacapp.adapter.MyPartsListViewAdapter;
import com.yac.yacapp.domain.Car;
import com.yac.yacapp.domain.CarCareBook;
import com.yac.yacapp.domain.Part;
import com.yac.yacapp.domain.UserInfo;
import com.yac.yacapp.icounts.App;
import com.yac.yacapp.icounts.ICounts;
import com.yac.yacapp.utils.Utils;
import com.yac.yacapp.views.HorizontalListView;
import com.yac.yacapp.views.LinearLayoutForListView;
import com.yac.yacapp.views.MyProgressDialog;
import com.yac.yacapp.views.MyToast;
import com.yac.yacapp.views.RoundProgressBar4Arc;

public class HomeFragment extends Fragment implements ICounts {

	private static final int GET_USERINFO = 0;
	private static final int CARPARTS_CODE = 1;
	private static final int UPDATE_VIEW = 2;
	private ViewGroup mPointGroup;
	private ViewPager adViewPager;
	private List<View> pageViews;
	private ImageView[] imageViews;
	private ImageView imageView;
	private AdPageAdapter adPageAdapter;
	private AdPageChangeListener adPageChangeListener;
	private HorizontalListView mHorizontalListView;
	private MyPartsListViewAdapter myListViewAdapter;
	private List<Part> mParts;
	private TextView baoyang_tv;

	private Context mContext;
	private LinearLayoutForListView carcare_book_contains_ll;
	private MyCarCareAdapter myCarCareAdapter;
	private List<CarCareBook> mCarCareBooks;
	private UserInfo mUserInfo;
	private List<Car> mCars;
	private SharedPreferences mSP;
	private Gson mGson;

	private AsyncHttpClient mClient;
	private MyProgressDialog myProgressDialog;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GET_USERINFO:
				String content = (String) msg.obj;
				String userinfo_json = mSP.getString(SP_UserInfo, "");
				if (!content.equals(userinfo_json)) {
					parserUserInfoJson(content);
				} else {
					//MyToast.makeText(mContext, R.color.yac_green, R.string.new_data_str, MyToast.MIDDLETIME).show();
				}
				break;
			case PARSERJSON_SUCCESS:
				if (myProgressDialog != null && myProgressDialog.isShowing()) {
					myProgressDialog.dismiss();
				}
				invilateViewPagerViews();
				break;
			case UPDATE_VIEW:
				updateListAndTreeView(msg.arg1);
				break;
			case PARSERJSON_FAILURE:
			case NET_FAILURE:
				if (myProgressDialog != null && myProgressDialog.isShowing()) {
					myProgressDialog.dismiss();
				}
				String message = (String) msg.obj;
				handlePARSERJSON_NET_FAILURE(message);
				break;
			default:
				break;
			}
		};
	};
	
    protected void handlePARSERJSON_NET_FAILURE(String message){
    	if(!TextUtils.isEmpty(message)){
			MyToast.makeText(mContext, R.color.pay_color, message, MyToast.LENGTH_SHORT).show();
		}else{
			MyToast.makeText(mContext, R.color.pay_color, getString(R.string.netfail_msg), MyToast.LENGTH_SHORT).show();
		}
    }
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (debug)
			Log.i("TAG", "HomeFragment onCreateView is run.");
		View v = inflater.inflate(R.layout.fragment_home, container, false);
		mContext = getActivity();
		mSP = mContext.getSharedPreferences("yacapp_sp", Activity.MODE_PRIVATE);
		mGson = new Gson();
		myProgressDialog = MyProgressDialog.createDialog(getActivity());
		myProgressDialog.setMessage(getString(R.string.progress_msg));
		mClient = new AsyncHttpClient();
		initView(v);
		initData();
		registerBoradcastReceiver();
		return v;
	}

	private void initView(View v) {
		if (debug)
			Log.i("TAG", "HomeFragment initView is run.");
		baoyang_tv = (TextView) v.findViewById(R.id.baoyang_tv);
		adViewPager = (ViewPager) v.findViewById(R.id.home_viewpager);
		mPointGroup = (ViewGroup) v.findViewById(R.id.viewGroup);
		mHorizontalListView = (HorizontalListView) v.findViewById(R.id.horizontalListView);
		mParts = new ArrayList<Part>();
		myListViewAdapter = new MyPartsListViewAdapter(getActivity(), mParts);
		mHorizontalListView.setAdapter(myListViewAdapter);

		carcare_book_contains_ll = (LinearLayoutForListView) v.findViewById(R.id.carcare_book_contains_ll);
		mCarCareBooks = new ArrayList<CarCareBook>();
		myCarCareAdapter = new MyCarCareAdapter(getActivity(), mCarCareBooks);
		carcare_book_contains_ll.setAdapter(myCarCareAdapter);

		initPageAdapter();
		initCirclePoint();
		adViewPager.setAdapter(adPageAdapter);
		adPageChangeListener = new AdPageChangeListener();
		adViewPager.setOnPageChangeListener(adPageChangeListener);
	}

	private void initData() {
		myProgressDialog.show();
		getSpData();
		getNetData();
	}

	private void updateUserinfoData() {
		if (App.needNetDate) {
			getNetData();
		} else {
			if (!getSpData()) {
				getNetData();
			}
		}
	}

	private void getNetData() {
		if (App.needNetDate) {
			App.needNetDate = false;
			Map<String, String> map = new HashMap<String, String>();
			map.put("user_id", String.valueOf(App.mCarUser.user_id));
			Utils.get(mContext, mClient, USER_INFO_SUBURL, map, mHandler, GET_USERINFO, true);
		}
	}

	private boolean getSpData() {
		String userinfo_json = mSP.getString(SP_UserInfo, "");
		if (!TextUtils.isEmpty(userinfo_json)) {
			mUserInfo = mGson.fromJson(userinfo_json, UserInfo.class);
			if (mUserInfo != null) {
				if (mCars != null) {
					mCars.clear();
					mCars = null;
				}
				mCars = mUserInfo.cars;
				mHandler.sendEmptyMessage(PARSERJSON_SUCCESS);
			} else {
				return false;
			}
		} else {
			return false;
		}
		return true;
	}

	private void initPageAdapter() {
		if (debug)
			Log.i("TAG", "HomeFragment initPageAdapter is run.");
		pageViews = new ArrayList<View>();
		pageViews.add(getFirstVpView());
		adPageAdapter = new AdPageAdapter(pageViews);
	}

	private View getFirstVpView() {
		View view = getActivity().getLayoutInflater().inflate(R.layout.view_add_newcar, null);
		ImageView add_image = (ImageView) view.findViewById(R.id.add_image);
		add_image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(mContext, PickCarActivity.class));
			}
		});
		return view;
	}

	private void invilateViewPagerViews() {
		if (pageViews != null) {
			pageViews.clear();
			pageViews.add(getFirstVpView());
			if (mCars != null && mCars.size() > 0) {
				for (int i = 0; i < mCars.size(); i++) {
					final Car car = mCars.get(i);
					View view1 = LayoutInflater.from(mContext).inflate(R.layout.view_carpart_detail_v2, null);
					view1.findViewById(R.id.beijing_tv).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							MyToast.makeText(mContext, R.color.pay_color, R.string.only_support_bj, MyToast.MIDDLETIME).show();
						}
					});
					if (car.licence != null)
						((TextView) view1.findViewById(R.id.car_licence_tv)).setText(car.licence.toString());
					view1.findViewById(R.id.car_details_btn).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(mContext, CarPartsDetailsActivity.class);
							startActivityForResult(intent, CARPARTS_CODE);
						}
					});
					RelativeLayout relativeLayout = (RelativeLayout) view1.findViewById(R.id.carpart_detail_rll);
					if (car.whole_score <= 30) {
						relativeLayout.setBackgroundResource(R.color.carpart_red);// undColor(color);
					} else if (car.whole_score <= 60) {
						relativeLayout.setBackgroundResource(R.color.carpart_yellow);
					} else if (car.whole_score <= 75) {
						relativeLayout.setBackgroundResource(R.color.carpart_blue);
					} else {
						relativeLayout.setBackgroundResource(R.color.carpart_green);
					}
					final RoundProgressBar4Arc progressBar4Arc = (RoundProgressBar4Arc) view1.findViewById(R.id.roundProgressBar3);
					if(i == mCars.size()-1){
						new Timer().schedule(new TimerTask() {
							@Override
							public void run() {
								getActivity().runOnUiThread(new Runnable() {
									@Override
									public void run() {
										int n = progressBar4Arc.getProgress();
										if(n >= car.whole_score){
											cancel();
										}else{
											progressBar4Arc.setProgress(n + 1);
										}
									}
								});
							}
						}, 0, 25);
					}else{
						progressBar4Arc.setProgress(car.whole_score);
					}
					pageViews.add(view1);
				}
			}
			initCirclePoint();
			adPageAdapter.updateDate(pageViews);
			adViewPager.setCurrentItem(pageViews.size(), false);
			adPageChangeListener.onPageSelected(pageViews.size() - 1);
		}
	}

	private void initCirclePoint() {
		if (debug)
			Log.i("TAG", "HomeFragment initCirclePoint is run.");
		imageViews = new ImageView[pageViews.size()];
		mPointGroup.removeAllViews();
		for (int i = 0; i < pageViews.size(); i++) {
			// 创建一个ImageView, 并设置宽高. 将该对象放入到数组中
			imageView = new ImageView(mContext);
			LayoutParams params = new LayoutParams(30, 15);
			imageView.setLayoutParams(params);
			imageViews[i] = imageView;
			if (i == pageViews.size()) {
				imageViews[i].setImageResource(R.drawable.point_white);
			} else {
				imageViews[i].setImageResource(R.drawable.point_gray);
			}
			mPointGroup.addView(imageViews[i]);
		}
	}

	private final class AdPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int state) {
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		}

		@Override
		public void onPageSelected(int position) {
			if(debug)System.out.println("onPageSelected, postion:" + position);
			for (int i = 0; i < imageViews.length; i++) {
				imageViews[position].setImageResource(R.drawable.point_white);
				if (position != i) {
					imageViews[i].setImageResource(R.drawable.point_gray);
				}
			}
			mHandler.removeMessages(UPDATE_VIEW);
			Message msg = mHandler.obtainMessage();
			msg.what = UPDATE_VIEW;
			msg.arg1 = position;
			mHandler.sendMessageDelayed(msg, 400);
		}
	}

	private void updateListAndTreeView(int position) {
		if(myViewpagerPositionListener != null)
			myViewpagerPositionListener.pageHasChanged(position);
		if (position == 0) {
			baoyang_tv.setVisibility(View.INVISIBLE);
			myListViewAdapter.updateData(new ArrayList<Part>());
			mCarCareBooks = new ArrayList<CarCareBook>();
			App.mCurrentCar = null;
		} else if (position > 0) {
			baoyang_tv.setVisibility(View.VISIBLE);
			Car car = mCars.get(position - 1);
			mParts = car.parts;
			myListViewAdapter.updateData(mParts);
			if (car.carcare_book != null) {
				mCarCareBooks = car.carcare_book;
			} else {
				mCarCareBooks = new ArrayList<CarCareBook>();
			}
			App.mCurrentCar = car;
		}
		myCarCareAdapter.updateData(mCarCareBooks);
		carcare_book_contains_ll.setAdapter(myCarCareAdapter);
	}

	protected void parserUserInfoJson(String content) {
		mUserInfo = mGson.fromJson(content, UserInfo.class);
		if (mUserInfo != null) {
			mSP.edit().putString(SP_UserInfo, content).commit();
			mCars = mUserInfo.cars;
			mHandler.sendEmptyMessage(PARSERJSON_SUCCESS);
		} else {
			mHandler.sendEmptyMessage(PARSERJSON_FAILURE);
		}
	}

	public MyViewpagerPositionListener myViewpagerPositionListener;

	public void setListener(MyViewpagerPositionListener myViewpagerPositionListener) {
		this.myViewpagerPositionListener = myViewpagerPositionListener;
	}

	public interface MyViewpagerPositionListener {
		void pageHasChanged(int position);
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
	 */
	private BroadcastReceiver userinfoUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (UPDATA_USERINFO_ACTION.equals(action)) {
				updateUserinfoData();
			}
		}
	};

}