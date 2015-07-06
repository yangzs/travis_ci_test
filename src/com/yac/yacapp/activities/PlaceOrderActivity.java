package com.yac.yacapp.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.pingplusplus.android.PaymentActivity;
import com.yac.yacapp.R;
import com.yac.yacapp.domain.Coupon;
import com.yac.yacapp.domain.Location;
import com.yac.yacapp.domain.Order4Create;
import com.yac.yacapp.domain.PicktimeSegment;
import com.yac.yacapp.icounts.App;
import com.yac.yacapp.icounts.BaseActivity;
import com.yac.yacapp.icounts.ICounts;
import com.yac.yacapp.utils.Utils;
import com.yac.yacapp.utils.Utils2;
import com.yac.yacapp.views.MyPickerView;
import com.yac.yacapp.views.MyPickerView.onSelectListener;
import com.yac.yacapp.views.MyProgressDialog;
import com.yac.yacapp.views.MyToast;

public class PlaceOrderActivity extends BaseActivity implements ICounts, OnClickListener {

	private static final int PICK_TIME_WHAT = 0;
	private static final int START_COUPON = 1;
	private static final int OFFLINE_PLACEORDER = 2;
	private static final int ZFB_CHARGE_PLACEORDER = 3;
	private static final int REQUEST_CODE_PAYMENT = 4;
	private static final int REQUEST_CODE_PICKADDRESS = 5;
	private static final int GET_COUPON_WHAT = 6;

	private LinearLayout mBack_ll;
	private TextView mBack_tv;
	private TextView mActionbar_title_tv;
	private ImageView mClose_img;
	private EditText mOrder_name_et, mPhone_num_et;
	private RelativeLayout mPick_time_rl;
	private TextView mPick_time_tv;
	private RelativeLayout mPick_address_rl;
	private TextView mPick_address_tv;
	private TextView mCoupon_code_tv;
	private RadioGroup mPlace_order_radiogroup;
	private RadioButton mZfb_radiobtn;
	private EditText mOrder_comment_et;
	private Button mPlace_order_btn;

	private Order4Create mOrder4Create;

	private View mParentview;
	private PopupWindow mPopupWindow;
	private String mCurrentDataSegment;
	private String mCurrentTimeSegment;
	private int mCouponCount = 0;

	private List<PicktimeSegment> mPicktimeSegments;
	private AsyncHttpClient mClient;
	private MyProgressDialog myProgressDialog;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case PICK_TIME_WHAT:
				String content = (String) msg.obj;
				parserPickTimeSegmentJson(content);
				break;
			case GET_COUPON_WHAT:
				String coupon_content = (String) msg.obj;
				parserCouponJson(coupon_content);
				break;
			case UPDATE_USERINFO_WHAT:
				if (myProgressDialog != null && myProgressDialog.isShowing()) {
					myProgressDialog.dismiss();
				}
				String userinfo_str = (String) msg.obj;
				mSP.edit().putString(SP_UserInfo, userinfo_str).commit();
				sendUpdateBroadcastReceiver();
				closeActivities();
				break;
			case PARSERJSON_SUCCESS:
				if (myProgressDialog != null && myProgressDialog.isShowing()) {
					myProgressDialog.dismiss();
				}
				invilateContentView();
				break;
			case OFFLINE_PLACEORDER:
				if (myProgressDialog != null && myProgressDialog.isShowing()) {
					myProgressDialog.dismiss();
				}
				startOkActivity(true);
				break;
			case ZFB_CHARGE_PLACEORDER:
				String content1 = (String) msg.obj;
				parserNewOrderJson(content1);
				break;
			case ZFB_CHARGE_WHAT:
//				if (myProgressDialog != null && myProgressDialog.isShowing()) {
//					myProgressDialog.dismiss();
//				}
				String data = (String) msg.obj;
				startZFBActivity(data);
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
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mParentview = getLayoutInflater().inflate(R.layout.activity_place_order, null);
		setContentView(mParentview);
		mPicktimeSegments = new ArrayList<PicktimeSegment>();
		mClient = new AsyncHttpClient();
		myProgressDialog = MyProgressDialog.createDialog(this);
		myProgressDialog.setMessage(getString(R.string.progress_msg));
		initView();
		initData();
		initPopupWindow();
		initLocation();
	}

	public LocationClient mLocationClient;
	private MyLocationListenner myListener = new MyLocationListenner();

	private void initLocation() {
		mLocationClient = new LocationClient(this);
		mLocationClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setAddrType("all");// 返回的定位结果包含地址信息
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		mLocationClient.setLocOption(option);
		mLocationClient.start();// 开始定位
	}

	protected void startZFBActivity(String data) {
		Intent intent = new Intent();
		String packageName = getPackageName();
		ComponentName componentName = new ComponentName(packageName, packageName + ".wxapi.WXPayEntryActivity");
		intent.setComponent(componentName);
		intent.putExtra(PaymentActivity.EXTRA_CHARGE, data);
		startActivityForResult(intent, REQUEST_CODE_PAYMENT);
	}

	protected void invilateContentView() {
		// 设置接车时间
		if (mPicktimeSegments != null && mPicktimeSegments.size() > 0) {
			PicktimeSegment segment = mPicktimeSegments.get(0);
			mCurrentDataSegment = segment.key;
			mCurrentTimeSegment = segment.data.get(0);
			mPick_time_tv.setText(mCurrentDataSegment + mCurrentTimeSegment);
		}
		// 优惠券数量
		mCoupon_code_tv.setText(mCouponCount + "张可用");
	}

	private MyPickerView date_segment_pv, time_segment_pv;

	private void initPopupWindow() {
		mPopupWindow = new PopupWindow(this);
		View view = getLayoutInflater().inflate(R.layout.pick_time_popup, null);
		date_segment_pv = (MyPickerView) view.findViewById(R.id.date_segment_pv);
		time_segment_pv = (MyPickerView) view.findViewById(R.id.time_segment_pv);

		mPopupWindow.setWidth(LayoutParams.MATCH_PARENT);
		mPopupWindow.setHeight((int) Utils2.dp2px(getResources(), 230));
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopupWindow.setAnimationStyle(R.style.popup_bottom_translate_anim);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setContentView(view);
	}

	private void initData() {
		mOrder4Create = (Order4Create) getIntent().getSerializableExtra("order4create");
		if (mOrder4Create == null) {
			MyToast.makeText(this, R.color.pay_color, "数据传输错误", MyToast.LENGTH_SHORT).show();
		}
		mOrder_name_et.setText(App.mCarUser.name);
		if(!TextUtils.isEmpty(App.mCarUser.name))
			mOrder_name_et.setSelection(App.mCarUser.name.length());
		mPhone_num_et.setText(App.mCarUser.phone);
		getPickTimeSegments();
		getCouponNum();
	}

	private void getCouponNum() {
		mCouponCount = 0;
		// myProgressDialog.show();
		Map<String, String> map = new HashMap<String, String>();
		map.put("user_id", String.valueOf(App.mCarUser.user_id));
		Utils.get(this, mClient, GET_COUPONS_SUBURL, map, mHandler, GET_COUPON_WHAT, true, null);
	}

	private void initView() {
		mBack_ll = (LinearLayout) findViewById(R.id.back_ll);
		mBack_tv = (TextView) findViewById(R.id.back_tv);
		mActionbar_title_tv = (TextView) findViewById(R.id.actionbar_title_tv);
		mClose_img = (ImageView) findViewById(R.id.close_img);
		mOrder_name_et = (EditText) findViewById(R.id.order_name_et);
		mPhone_num_et = (EditText) findViewById(R.id.phone_num_et);
		mPick_time_rl = (RelativeLayout) findViewById(R.id.pick_time_rl);
		mPick_time_tv = (TextView) findViewById(R.id.pick_time_tv);
		mPick_address_rl = (RelativeLayout) findViewById(R.id.pick_address_rl);
		mPick_address_tv = (TextView) findViewById(R.id.pick_address_tv);
		mCoupon_code_tv = (TextView) findViewById(R.id.coupon_code_tv);
		mPlace_order_radiogroup = (RadioGroup) findViewById(R.id.place_order_radiogroup);
		mZfb_radiobtn = (RadioButton) findViewById(R.id.zfb_radiobtn);
		//mOffline_radiobtn = (RadioButton) findViewById(R.id.offline_radiobtn);
		mOrder_comment_et = (EditText) findViewById(R.id.order_comment_et);
		mPlace_order_btn = (Button) findViewById(R.id.place_order_btn);

		mBack_ll.setVisibility(View.VISIBLE);
		mBack_ll.setOnClickListener(this);
		mBack_tv.setText("服务信息");
		mActionbar_title_tv.setText("确认订单");
		mClose_img.setOnClickListener(this);
		mPick_time_rl.setOnClickListener(this);
		mPick_address_rl.setOnClickListener(this);
		mCoupon_code_tv.setOnClickListener(this);
		mZfb_radiobtn.setChecked(true);
		mPlace_order_btn.setOnClickListener(this);
	}

	private void getPickTimeSegments() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("service_type", "keeper");
		Utils.get(this, mClient, PICK_TIME_SUBURL, map, mHandler, PICK_TIME_WHAT, true);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_ll:
			finish();
			break;
		case R.id.pick_time_rl:
			showPopupWindow();
			break;
		case R.id.pick_address_rl:
			// 使用百度sdk获取位置,进入地图，进行定位，取地址
			openBaiDuSdkMap();
			break;
		case R.id.coupon_code_tv:
			Intent intent = new Intent(this, MyCouponActivity.class);
			intent.addFlags(1);// flag:1,select coupon. 0,my coupon
			if (mSelectedCoupon != null)
				intent.putExtra("selected_coupon", mSelectedCoupon);
			startActivityForResult(intent, START_COUPON);
			break;
		case R.id.place_order_btn:
			// 下单：支付宝，线下支付
			placeOrder();
			break;
		case R.id.close_img:
			closeActivities();
			break;
		default:
			break;
		}
	}

	private void openBaiDuSdkMap() {
		Intent intent = new Intent(this, MyLocationActivity.class);
		intent.addFlags(PICK_LOCATION_FLAG);
		startActivityForResult(intent, REQUEST_CODE_PICKADDRESS);
	}

	private void placeOrder() {
		// 判断一下姓名，电话
		if (TextUtils.isEmpty(mOrder_name_et.getText().toString().trim())) {
			MyToast.makeText(this, R.color.pay_color, "请填写联系人", MyToast.LENGTH_SHORT).show();
			mOrder_name_et.requestFocus();
			return;
		}
		if (TextUtils.isEmpty(mPhone_num_et.getText().toString().trim())) {
			MyToast.makeText(this, R.color.pay_color, "请填写手机号", MyToast.LENGTH_SHORT).show();
			mPhone_num_et.requestFocus();
			return;
		}
		myProgressDialog.show();
		int checkedId = mPlace_order_radiogroup.getCheckedRadioButtonId();
		if (checkedId == R.id.zfb_radiobtn) {
			zfbPlaceOrder();
		} else if (checkedId == R.id.offline_radiobtn) {
			offlinePlaceOrder();
		}
	}

	private void zfbPlaceOrder() {
		mOrder4Create.car_id = App.mCurrentCar.id;
		mOrder4Create.user_id = App.mCarUser.user_id;
		mOrder4Create.contact_name = mOrder_name_et.getText().toString().trim();
		mOrder4Create.phone_number = mPhone_num_et.getText().toString().trim();
		if (mLocation != null) {
			mOrder4Create.location = mLocation;
		} else {
			Location location = new Location();
			location.address = "默认";
			location.latitude = 21.1;
			location.longitude = 21.1;
			location.name = "默认地址";
			mOrder4Create.location = location;
		}
		if (mSelectedCoupon != null) {
			mOrder4Create.coupon_id = mSelectedCoupon.id;
		}
		mOrder4Create.pick_time = mCurrentDataSegment;
		mOrder4Create.pick_time_segment = mCurrentTimeSegment;
		mOrder4Create.comment = mOrder_comment_et.getText().toString().trim();
		mOrder4Create.peer_source = "android_client";
		mOrder4Create.pay_mode = 1;//线上支付

		Gson gson = new Gson();
		String json_str = gson.toJson(mOrder4Create);
		if(debug)Log.i("PlaceOrderActivity", "json_str:" + json_str);
		Utils.post(this, mClient, CREATE_ORDER_SUBURL, json_str, mHandler, ZFB_CHARGE_PLACEORDER, true, true);
	}

	private void offlinePlaceOrder() {
		mOrder4Create.car_id = App.mCurrentCar.id;
		mOrder4Create.user_id = App.mCarUser.user_id;
		mOrder4Create.contact_name = mOrder_name_et.getText().toString().trim();
		mOrder4Create.phone_number = mPhone_num_et.getText().toString().trim();
		if (mLocation != null) {
			mOrder4Create.location = mLocation;
		} else {
			Location location = new Location();
			location.address = "默认";
			location.latitude = 21.1;
			location.longitude = 21.1;
			location.name = "默认地址";
			mOrder4Create.location = location;
		}
		if (mSelectedCoupon != null) {
			mOrder4Create.coupon_id = mSelectedCoupon.id;
		}
		mOrder4Create.pick_time = mCurrentDataSegment;
		mOrder4Create.pick_time_segment = mCurrentTimeSegment;
		mOrder4Create.comment = mOrder_comment_et.getText().toString().trim();
		mOrder4Create.peer_source = "android_client";
		mOrder4Create.pay_mode = 2;//线下支付

		Gson gson = new Gson();
		String json_str = gson.toJson(mOrder4Create);
		Log.i("PlaceOrderActivity", "json_str:" + json_str);
		Utils.post(this, mClient, CREATE_ORDER_SUBURL, json_str, mHandler, OFFLINE_PLACEORDER, true, true);
	}

	// 解析order，并且进行支付宝请求
	protected void parserNewOrderJson(String content1) {
		try {
			JSONObject jsonObject = new JSONObject(content1);
			// total_price，order_id
			RequestCharge charge = new RequestCharge();
			charge.amount = jsonObject.optDouble("total_price");
			charge.amount *= 100;
			charge.order_id = jsonObject.optLong("id");
			charge.subject = "养爱车费用";
			charge.body = "养爱车费用";
			charge.channel = "alipay";
			B b = new B("");
			A a = new A();
			a.alipay_wap = b;
			charge.extra = a;
			String json_str = new Gson().toJson(charge);
			if(debug)System.out.println("json_str:" + json_str);
			Utils.post(this, mClient, ZFB_CHARGE_SUBURL, json_str, mHandler, ZFB_CHARGE_WHAT, true, true);
		} catch (JSONException e) {
			e.printStackTrace();
			MyToast.makeText(this, R.color.pay_color,  "下单失败", MyToast.LENGTH_SHORT).show();
			mHandler.sendEmptyMessage(PARSERJSON_FAILURE);
		}
	}

	class RequestCharge {
		Double amount;
		String subject;
		String body;
		Long order_id;
		String channel;
		A extra;
	}

	class A {
		B alipay_wap;
	}

	class B {
		String success_url;
		public B(String str) {
			success_url = str;
		}
	}

	private void showPopupWindow() {
		if (mPicktimeSegments == null || mPicktimeSegments.size() <= 0) {
			return;
		}
		List<String> date_listList = new ArrayList<String>();
		for (PicktimeSegment picktimeSegment : mPicktimeSegments) {
			date_listList.add(picktimeSegment.key);
		}
		date_segment_pv.setData(date_listList);
		date_segment_pv.setOnSelectListener(new onSelectListener() {
			@Override
			public void onSelect(String text) {
//				for (PicktimeSegment picktimeSegment : mPicktimeSegments) {
//					if (text.equals(picktimeSegment.key)) {
//						time_segment_pv.setData(picktimeSegment.data);
//					}
//				}
				mCurrentDataSegment = text;
				mPick_time_tv.setText(mCurrentDataSegment + mCurrentTimeSegment);
			}
		});
		time_segment_pv.setData(mPicktimeSegments.get(0).data);
		time_segment_pv.setOnSelectListener(new onSelectListener() {
			@Override
			public void onSelect(String text) {
				mCurrentTimeSegment = text;
				mPick_time_tv.setText(mCurrentDataSegment + mCurrentTimeSegment);
			}
		});
		mPopupWindow.showAtLocation(mPick_time_tv, Gravity.BOTTOM, 0, 0);
	}

	private void parserPickTimeSegmentJson(String content) {
		try {
			JSONArray jsonArray = new JSONArray(content);
			JSONObject object;
			for (int i = 0; i < jsonArray.length(); i++) {
				object = jsonArray.optJSONObject(i);
				PicktimeSegment picktimeSegment = new PicktimeSegment();
				picktimeSegment.key = object.optString(PicktimeSegment.KEY_KEY);
				picktimeSegment.data = new ArrayList<String>();
				JSONArray str_arr = object.optJSONArray(PicktimeSegment.KEY_DATA);
				for (int j = 0; j < str_arr.length(); j++) {
					picktimeSegment.data.add(str_arr.optString(j));
				}
				mPicktimeSegments.add(picktimeSegment);
			}
			mHandler.sendEmptyMessage(PARSERJSON_SUCCESS);
		} catch (JSONException e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(PARSERJSON_FAILURE);
		}
	};

	protected void parserCouponJson(String coupon_content) {
		try {
			JSONArray jsonArray = new JSONArray(coupon_content);
			JSONObject object;
			for (int i = 0; i < jsonArray.length(); i++) {
				object = jsonArray.optJSONObject(i);
				String status = object.optString(Coupon.KEY_STATUS);
				if ("未使用".equals(status)) {
					mCouponCount++;
				}
			}
			mHandler.sendEmptyMessage(PARSERJSON_SUCCESS);
		} catch (JSONException e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(PARSERJSON_FAILURE);
		}
	}

	private Coupon mSelectedCoupon;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == START_COUPON) {// 选择优惠券
			if (resultCode == RESULT_OK) {
				mSelectedCoupon = (Coupon) data.getSerializableExtra("selected_coupon");
				if(mSelectedCoupon != null){
					mCoupon_code_tv.setText("抵扣" + mSelectedCoupon.value + "元");
				}else{
					getCouponNum();
				}
			} 
		}else if (requestCode == REQUEST_CODE_PAYMENT) {// 支付页面返回处理
			if (myProgressDialog != null && myProgressDialog.isShowing()) {
				myProgressDialog.dismiss();
			}
			if (resultCode == Activity.RESULT_OK) {
				/*
				 * 处理返回值 "success" - payment succeed "fail" - payment failed
				 * "cancel" - user canceld "invalid" - payment plugin not
				 * installed
				 */
				String result = data.getExtras().getString("pay_result");
				if ("success".equals(result)) {
					startOkActivity(true);
				} else {
					MyToast.makeText(this, R.color.pay_color,  "取消支付，请到我的订单中重新支付", MyToast.LENGTH_SHORT).show();
					myProgressDialog.show();
					Utils.updateUserInfoJson(this, mClient, mHandler);
				}
			} else if (resultCode == Activity.RESULT_CANCELED) {
				showMsg("User canceled", "", "");
			}
		} else if (requestCode == REQUEST_CODE_PICKADDRESS) {
			if (resultCode == Activity.RESULT_OK) {
				Location location = (Location) data.getSerializableExtra("location");
				if (location != null) {
					mLocation = location;
					mPick_address_tv.setText(mLocation.address);
				}
			}
		}
	}
	
	private void startOkActivity(boolean flag){
		Intent intent = new Intent(this, PlaceOrderOKActivity.class);
		intent.putExtra("flag", flag);
		startActivity(intent);
	}

	public void showMsg(String title, String msg1, String msg2) {
		String str = title;
		if (msg1.length() != 0) {
			str += "\n" + msg1;
		}
		if (msg2.length() != 0) {
			str += "\n" + msg2;
		}
		AlertDialog.Builder builder = new Builder(PlaceOrderActivity.this);
		builder.setMessage(str);
		builder.setTitle("提示");
		builder.setPositiveButton("OK", null);
		builder.create().show();
	}

	private Location mLocation;

	/**
	 * 监听函数，有更新位置的时候，格式化成字符串，输出到屏幕中
	 */
	public class MyLocationListenner implements BDLocationListener {
		@Override
		// 接收位置信息
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			if (!"北京市".equals(location.getCity())) {
				MyToast.makeText(PlaceOrderActivity.this, R.color.pay_color,  "您的所在地 " + location.getCity() + ",养爱车目前只支持北京地区服务", MyToast.LENGTH_SHORT).show();
			} else {
				MyToast.makeText(PlaceOrderActivity.this, R.color.yac_green,  "您的所在地 " + location.getCity(), MyToast.LENGTH_SHORT).show();
			}
			mLocation = new Location();
			mLocation.latitude = location.getLatitude();
			mLocation.longitude = location.getLongitude();
			mLocation.address = location.getAddrStr();
			mLocation.name = location.getAddrStr();
			mPick_address_tv.setText(mLocation.address);
		}

		// 接收POI信息函数，我不需要POI，所以我没有做处理
		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mLocationClient.stop();// 停止定位
	}
}
