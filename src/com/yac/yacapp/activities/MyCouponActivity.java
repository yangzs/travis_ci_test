package com.yac.yacapp.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.yac.yacapp.R;
import com.yac.yacapp.domain.Coupon;
import com.yac.yacapp.icounts.App;
import com.yac.yacapp.icounts.BaseActivity;
import com.yac.yacapp.icounts.ICounts;
import com.yac.yacapp.utils.Utils;
import com.yac.yacapp.views.MyProgressDialog;
import com.yac.yacapp.views.MyToast;

public class MyCouponActivity extends BaseActivity implements ICounts, OnClickListener {

	private static final int GET_COUPON_WHAT = 0;
	private static final int CONVERSION_COUPON_WHAT = 1;
	private int flag;// flag:1,select coupon. 0,my coupon
	private LinearLayout back_ll;
	private TextView back_tv, actionbar_title_tv;
	private ImageView mClose_img;
	private LinearLayout conversion_ll;
	private EditText coupon_code_et;
	private Button conversion_bt;
	private ListView coupon_listview;
	private MyAdapter adapter;
	private List<Coupon> mCoupons;
	private Coupon mCurrentCoupon;
	private int mCurrentPosition = -1;
	private Coupon mSelectedCoupon;

	private AsyncHttpClient mClient;
	private MyProgressDialog myProgressDialog;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GET_COUPON_WHAT:
				String content = (String) msg.obj;
				parserCouponJson(content);
				break;
			case CONVERSION_COUPON_WHAT:
				String content1 = (String) msg.obj;
				parserCouponJson(content1);
				coupon_code_et.setText("");
				break;
			case PARSERJSON_SUCCESS:
				if (myProgressDialog != null && myProgressDialog.isShowing()) {
					myProgressDialog.dismiss();
				}
				invilateContentView();
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
		setContentView(R.layout.activity_coupon);
		flag = getIntent().getFlags();
		if (flag != 0) {
			mSelectedCoupon = (Coupon) getIntent().getSerializableExtra("selected_coupon");
		}
		mClient = new AsyncHttpClient();
		myProgressDialog = MyProgressDialog.createDialog(this);
		myProgressDialog.setMessage(getString(R.string.progress_msg));
		initView();
		initData();
	}

	private void initData() {
		myProgressDialog.show();
		Map<String, String> map = new HashMap<String, String>();
		map.put("user_id", String.valueOf(App.mCarUser.user_id));
		String testUrl = "";// "http://192.168.2.151:8080/mockjsdata/2/v1/api/coupons?user_id=";
		Utils.get(this, mClient, GET_COUPONS_SUBURL, map, mHandler, GET_COUPON_WHAT, true, testUrl);
	}

	private void initView() {
		findViewById(R.id.head_ll).setOnClickListener(mSoftInputOnClickListener);
		back_ll = (LinearLayout) findViewById(R.id.back_ll);
		back_tv = (TextView) findViewById(R.id.back_tv);
		actionbar_title_tv = (TextView) findViewById(R.id.actionbar_title_tv);
		mClose_img = (ImageView) findViewById(R.id.close_img);
		conversion_ll = (LinearLayout) findViewById(R.id.conversion_ll);
		coupon_code_et = (EditText) findViewById(R.id.coupon_code_et);
		conversion_bt = (Button) findViewById(R.id.conversion_bt);
		coupon_listview = (ListView) findViewById(R.id.coupon_listview);

		back_ll.setVisibility(View.VISIBLE);
		back_ll.setOnClickListener(this);
		mClose_img.setOnClickListener(this);
		if (flag != 0) {
			actionbar_title_tv.setText("选择优惠券");
			back_tv.setText("确认订单");
			conversion_ll.setVisibility(View.GONE);
		} else {
			actionbar_title_tv.setText("我的优惠券");
			back_tv.setText("我");
			conversion_ll.setVisibility(View.VISIBLE);
		}
		conversion_bt.setOnClickListener(this);
		adapter = new MyAdapter();
		mCoupons = new ArrayList<Coupon>();
		coupon_listview.setAdapter(adapter);
		if (flag != 0) {
			coupon_listview.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					if (mCurrentPosition == position) {
						mCurrentCoupon = null;
						mCurrentPosition = -1;
					} else {
						mCurrentCoupon = mCoupons.get(position);
						mCurrentPosition = position;
					}
					adapter.notifyDataSetChanged();
					Intent data = new Intent();
					data.putExtra("selected_coupon", mCurrentCoupon);
					setResult(RESULT_OK, data);
					finish();
				}
			});
		}
	}

	protected void invilateContentView() {
		if (mSelectedCoupon != null) {
			for (int i = 0; i < mCoupons.size(); i++) {
				if (mSelectedCoupon.number.equals(mCoupons.get(i).number)) {
					mCurrentPosition = i;
				}
			}
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_ll:
			finish();
			break;
		case R.id.close_img:
			closeActivities();
			break;
		case R.id.conversion_bt:
			conversionCoupon();
			break;
		default:
			break;
		}
	}

	private void conversionCoupon() {
		String couponCode = coupon_code_et.getText().toString().trim();
		if (TextUtils.isEmpty(couponCode)) {
			MyToast.makeText(this, R.color.pay_color, "优惠券不能为空", MyToast.LENGTH_SHORT).show();
			return;
		}
		A a = new A();
		a.user_id = App.mCarUser.user_id;
		B b = new B(couponCode);
		a.coupons = new ArrayList<B>();
		a.coupons.add(b);
		Gson gson = new Gson();
		String json_str = gson.toJson(a);
		// System.out.println("json_str:"+json_str);
		Utils.post(this, mClient, CONVERSION_COUPON_SUBURL, json_str, mHandler, CONVERSION_COUPON_WHAT, true);
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mCoupons.size();
		}

		@Override
		public Coupon getItem(int position) {
			return mCoupons.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView != null) {
				holder = (ViewHolder) convertView.getTag();
			} else {
				holder = new ViewHolder();
				convertView = getLayoutInflater().inflate(R.layout.item_coupon, null);
				holder.coupon_img = (ImageView) convertView.findViewById(R.id.coupon_img);
				holder.coupon_number_tv = (TextView) convertView.findViewById(R.id.coupon_number_tv);
				holder.coupon_name_tv = (TextView) convertView.findViewById(R.id.coupon_name_tv);
				holder.coupon_value_tv = (TextView) convertView.findViewById(R.id.coupon_value_tv);
				holder.coupon_status_tv = (TextView) convertView.findViewById(R.id.coupon_status_tv);
				holder.item_hook_img = (ImageView) convertView.findViewById(R.id.item_hook_img);
				holder.expired_time_tv = (TextView) convertView.findViewById(R.id.expired_time_tv);
				convertView.setTag(holder);
			}
			Coupon coupon = getItem(position);
			if ("未使用".equals(coupon.status)) {
				if (coupon.value < 50) {
					holder.coupon_img.setImageResource(R.drawable.couponsbackground3);
				} else if (coupon.value < 100) {
					holder.coupon_img.setImageResource(R.drawable.couponsbackground2);
				} else {
					holder.coupon_img.setImageResource(R.drawable.couponsbackground1);
				}
			} else {
				holder.coupon_img.setImageResource(R.drawable.couponsbackground4);
			}
			holder.coupon_number_tv.setText(getString(R.string.coupon_number_text, coupon.number));
			holder.coupon_name_tv.setText(coupon.name);
			holder.coupon_value_tv.setText("￥" + coupon.value);
			holder.coupon_status_tv.setText(coupon.status);
			String[] split = coupon.expired_time.split("T");
			holder.expired_time_tv.setText(split[0] + "止");
			if (position != mCurrentPosition) {
				holder.item_hook_img.setVisibility(View.GONE);
			} else {
				holder.item_hook_img.setVisibility(View.VISIBLE);
			}
			return convertView;
		}

		class ViewHolder {
			ImageView coupon_img, item_hook_img;
			TextView coupon_number_tv, coupon_name_tv, coupon_value_tv, coupon_status_tv, expired_time_tv;
		}
	}

	protected void parserCouponJson(String content) {
		try {
			JSONArray jsonArray = new JSONArray(content);
			JSONObject object;
			for (int i = 0; i < jsonArray.length(); i++) {
				object = jsonArray.optJSONObject(i);
				if (flag != 0 && !"未使用".equals(object.optString(Coupon.KEY_STATUS))) {
					continue;
				}
				Coupon coupon = new Coupon();
				coupon.id = object.optLong(Coupon.KEY_ID);
				coupon.name = object.optString(Coupon.KEY_NAME);
				coupon.number = object.optString(Coupon.KEY_NUMBER);
				coupon.status = object.optString(Coupon.KEY_STATUS);
				coupon.value = object.optDouble(Coupon.KEY_VALUE);
				coupon.expired_time = object.optString(Coupon.KEY_EXPIRED_TIME);
				mCoupons.add(coupon);
			}
			mHandler.sendEmptyMessage(PARSERJSON_SUCCESS);
		} catch (JSONException e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(PARSERJSON_FAILURE);
		}
	}

	class A {
		Long user_id;
		List<B> coupons;
	}

	class B {
		String code;

		public B(String code) {
			this.code = code;
		}
	}
}
