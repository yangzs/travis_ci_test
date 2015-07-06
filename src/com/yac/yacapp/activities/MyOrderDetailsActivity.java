package com.yac.yacapp.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.pingplusplus.android.PaymentActivity;
import com.yac.yacapp.R;
import com.yac.yacapp.adapter.MyOrderItemAdapter;
import com.yac.yacapp.domain.Order;
import com.yac.yacapp.domain.OrderProduct;
import com.yac.yacapp.icounts.App;
import com.yac.yacapp.icounts.BaseActivity;
import com.yac.yacapp.utils.Utils;
import com.yac.yacapp.views.LinearLayoutForListView;
import com.yac.yacapp.views.MyProgressDialog;
import com.yac.yacapp.views.MyToast;

public class MyOrderDetailsActivity extends BaseActivity implements OnClickListener {

	protected static final int GETORDER_WHAT = 0;
	private static final int REQUEST_CODE_PAYMENT = 1;
	private LinearLayout back_ll;
	private TextView back_tv, actionbar_title_tv;
	private ImageView mClose_img;
	private TextView client_name_tv, client_phone_tv, client_car_tv, client_address_tv;
	// 增项
	private LinearLayout adds_item_ll;
	private TextView adds_item_status_tv;
	private LinearLayoutForListView adds_items_contains_ll;
//	private TextView suggest_textView1, suggest_total_price_tv;
//	private Button suggest_accounts_btn;

	private TextView order_item_status_tv;
	private LinearLayoutForListView order_items_contains_ll;
	private TextView coupons_num_tv;
	private RelativeLayout driver_rl;
	private TextView driver_tv, operator_tv;
	// 自选
	private TextView self_textView1, self_textView2, self_total_price_tv;
	private Button self_accounts_btn;
	// 检测报告单
	private Button look_cheekbook_btn;

	private Order mOrder;
	private Long mOrder_id;
	private MyOrderItemAdapter myOrderAddItemAdapter, myOrderItemAdapter;
	private MyProgressDialog myProgressDialog;
	private AsyncHttpClient mClient;
	private Gson mGson;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GETORDER_WHAT:
				if (myProgressDialog != null && myProgressDialog.isShowing()) {
					myProgressDialog.dismiss();
				}
				String content = (String) msg.obj;
				mOrder = mGson.fromJson(content, Order.class);
				if (mOrder != null)
					initData();
				break;
			case ZFB_CHARGE_WHAT:
				if (myProgressDialog != null && myProgressDialog.isShowing()) {
					myProgressDialog.dismiss();
				}
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
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myorder_details);
		mOrder_id = getIntent().getLongExtra("order_id", -1);
		myProgressDialog = MyProgressDialog.createDialog(this);
		myProgressDialog.setMessage(getString(R.string.progress_msg));
		mClient = new AsyncHttpClient();
		mGson = new Gson();
		initView();
		getNetData();
	}

	private void initView() {
		back_ll = (LinearLayout) findViewById(R.id.back_ll);
		back_tv = (TextView) findViewById(R.id.back_tv);
		actionbar_title_tv = (TextView) findViewById(R.id.actionbar_title_tv);
		mClose_img = (ImageView) findViewById(R.id.close_img);
		client_name_tv = (TextView) findViewById(R.id.client_name_tv);
		client_phone_tv = (TextView) findViewById(R.id.client_phone_tv);
		client_car_tv = (TextView) findViewById(R.id.client_car_tv);
		client_address_tv = (TextView) findViewById(R.id.client_address_tv);
		adds_item_ll = (LinearLayout) findViewById(R.id.adds_item_ll);
		adds_item_status_tv = (TextView) findViewById(R.id.adds_item_status_tv);
		adds_items_contains_ll = (LinearLayoutForListView) findViewById(R.id.adds_items_contains_ll);
//		suggest_textView1 = (TextView) findViewById(R.id.suggest_textView1);
//		suggest_total_price_tv = (TextView) findViewById(R.id.suggest_total_price_tv);
//		suggest_accounts_btn = (Button) findViewById(R.id.suggest_accounts_btn);

		order_item_status_tv = (TextView) findViewById(R.id.order_item_status_tv);
		order_items_contains_ll = (LinearLayoutForListView) findViewById(R.id.order_items_contains_ll);
		coupons_num_tv = (TextView) findViewById(R.id.coupons_num_tv);
		driver_rl = (RelativeLayout) findViewById(R.id.driver_rl);
		driver_tv = (TextView) findViewById(R.id.driver_tv);
		operator_tv = (TextView) findViewById(R.id.operator_tv);
		self_textView1 = (TextView) findViewById(R.id.self_textView1);
		self_textView2 = (TextView) findViewById(R.id.self_textView2);
		self_total_price_tv = (TextView) findViewById(R.id.self_total_price_tv);
		self_accounts_btn = (Button) findViewById(R.id.self_accounts_btn);
		look_cheekbook_btn = (Button) findViewById(R.id.look_cheekbook_btn);

		back_ll.setVisibility(View.VISIBLE);
		back_ll.setOnClickListener(this);
		back_tv.setText("我的订单");
		actionbar_title_tv.setText("订单详情");
		mClose_img.setOnClickListener(this);
		driver_rl.setOnClickListener(this);
	}

	private void getNetData() {
		myProgressDialog.show();
		Map<String, String> map = new HashMap<String, String>();
		map.put("detail", "true");
		map.put("order_id", String.valueOf(mOrder_id));
		map.put("keeper_id", String.valueOf(App.mCarUser.user_id));
		Utils.get(this, mClient, GETORDER_SUBURL, map, mHandler, GETORDER_WHAT, true, true);
	}

	private void initData() {
		client_name_tv.setText(mOrder.client_basic.name);
		client_phone_tv.setText(mOrder.client_basic.phone_number);
		client_car_tv.setText(mOrder.car.licence.toString());
		client_address_tv.setText(mOrder.client_basic.location.address);
		coupons_num_tv.setText("优惠券抵用：￥" + mOrder.coupon_price + "元");
		if (mOrder.keeper_basics != null && mOrder.keeper_basics.size() > 0)
			driver_tv.setText(mOrder.keeper_basics.get(0).name);
		if (mOrder.operator != null) {
			operator_tv.setText(mOrder.operator.name);
		}
		initProductsItemsLL();
//		if (isAddPaid || isSelfPaied) {
//			look_cheekbook_btn.setVisibility(View.VISIBLE);
//			look_cheekbook_btn.setOnClickListener(this);
//		}
	}

	private List<OrderProduct> mIncreaseProducts;
	/**
	 * false 未支付   true 已支付
	 */
	private boolean isAddPaid = false;
	/**
	 * false 未支付   true 已支付
	 */
	private boolean isSelfPaied = false;
	private double addTotal = 0.00;
	private double selfTotal = 0.00;

	private void initProductsItemsLL() {
		//for 增项
		if (mOrder.increase_products != null && mOrder.increase_products.size() > 0) {
			mIncreaseProducts = new ArrayList<OrderProduct>();
			for (OrderProduct orderProduct : mOrder.increase_products) {
				if (orderProduct.selection_mode == 3) {
					addTotal += orderProduct.total_price;
					mIncreaseProducts.add(orderProduct);
					if (orderProduct.pay_status == 0) {
						isAddPaid = false;
					} else if (orderProduct.pay_status == 1) {
						isAddPaid = true;
					}
				}
			}
			if (mIncreaseProducts.size() > 0) {
				adds_item_ll.setVisibility(View.VISIBLE);
				myOrderAddItemAdapter = new MyOrderItemAdapter(this, mIncreaseProducts);
				adds_items_contains_ll.setAdapter(myOrderAddItemAdapter);
				if (mOrder.pay_mode == 2) {// 线下支付
					adds_item_status_tv.setText("已选择线下支付");
				} else if (mOrder.pay_mode == 1 && isAddPaid) {// 已支付
					adds_item_status_tv.setText("已支付");
				} else if(mOrder.pay_mode == 1 && !isAddPaid){// 未支付
					adds_item_status_tv.setText("未支付");
				}
			}
		}
		//for 自选商品
		myOrderItemAdapter = new MyOrderItemAdapter(this, mOrder.products);
		order_items_contains_ll.setAdapter(myOrderItemAdapter);
		for (OrderProduct orderProduct : mOrder.products) {
			if (orderProduct.selection_mode == 1) {
				selfTotal += orderProduct.total_price;
				if (orderProduct.pay_status == 0) {
					isSelfPaied = false;
				} else if (orderProduct.pay_status == 1) {
					isSelfPaied = true;
				}
			}
		}
		if (mOrder.pay_mode == 2) {// 线下支付
			order_item_status_tv.setText("已选择线下支付");
		} else if ((mOrder.pay_mode == 1 && isSelfPaied) || mOrder.paid) {// 已支付
			order_item_status_tv.setText("已支付");
		} else if(mOrder.pay_mode == 1 && !isSelfPaied){// 未支付
			order_item_status_tv.setText("未支付");
		}
		if(mOrder.pay_mode == 2 || (isAddPaid && isSelfPaied) || mOrder.paid){//线下支付,全部为支付过的
			self_textView1.setTextColor(getResources().getColor(R.color.grey));
			self_total_price_tv.setTextColor(getResources().getColor(R.color.grey));
			self_accounts_btn.setOnClickListener(null);
			self_total_price_tv.setText("￥" + (addTotal+selfTotal) + "元");
		}else if(mIncreaseProducts != null && mIncreaseProducts.size() > 0){//有增项的线上支付
			if(!isAddPaid && isSelfPaied){//增项未支付，自选已支付
				self_textView2.setText("（增加项目）");
				self_total_price_tv.setText("￥" + addTotal + "元");
			}else if(isAddPaid && !isSelfPaied){//增项已支付，自选未支付,这个是不可能出现的。
				self_textView2.setText("（自选项目）");
				self_total_price_tv.setText("￥" + selfTotal + "元");
			}else if(!isAddPaid && !isSelfPaied){//增项未支付，自选未支付
				self_textView2.setText("（增加+自选项目）");
				self_total_price_tv.setText("￥" + (addTotal+selfTotal) + "元");
			}
			self_accounts_btn.setBackgroundResource(R.drawable.orange_bg_buttom);
			self_accounts_btn.setOnClickListener(this);
		}else if(mIncreaseProducts == null || mIncreaseProducts.size() == 0){//没有增项的线上支付
			if(isSelfPaied){//已支付
				self_textView1.setTextColor(getResources().getColor(R.color.grey));
				self_total_price_tv.setTextColor(getResources().getColor(R.color.grey));
				self_accounts_btn.setOnClickListener(null);
				self_total_price_tv.setText("￥" + (addTotal+selfTotal) + "元");
			}else{//未支付
				self_textView2.setText("（自选项目）");
				self_total_price_tv.setText("￥" + selfTotal + "元");
				self_accounts_btn.setBackgroundResource(R.drawable.orange_bg_buttom);
				self_accounts_btn.setOnClickListener(this);
			}
		}
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
		case R.id.driver_rl:
			Intent intent = new Intent(this, MyOrderKeepersActivity.class);
			intent.putExtra("order", mOrder);
			startActivity(intent);
			break;
		case R.id.self_accounts_btn:
			if(mIncreaseProducts == null || mIncreaseProducts.size() == 0){
				settleAccounts(mOrder.products);
			}else if(mIncreaseProducts != null && mIncreaseProducts.size() > 0){
				if(!isAddPaid && !isSelfPaied){//增项未支付，自选未支付
					mIncreaseProducts.addAll(mOrder.products);
				}
				settleAccounts(mIncreaseProducts);
			}
			break;
		case R.id.look_cheekbook_btn:
			Intent intent1 = new Intent(this, AddProductItemsActivity.class);
			intent1.putExtra("order_id", mOrder_id);
			startActivity(intent1);
			break;
		default:
			break;
		}
	}
	
	protected void startZFBActivity(String data) {
		Intent intent = new Intent();
		String packageName = getPackageName();
		ComponentName componentName = new ComponentName(packageName, packageName + ".wxapi.WXPayEntryActivity");
		intent.setComponent(componentName);
		intent.putExtra(PaymentActivity.EXTRA_CHARGE, data);
		startActivityForResult(intent, REQUEST_CODE_PAYMENT);
	}
	
	// 解析order，并且进行支付宝请求
	private void settleAccounts(List<OrderProduct> orderProducts) {
		myProgressDialog.show();
		RequestCharge charge = new RequestCharge();
		charge.amount = selfTotal*100;
		charge.order_id = mOrder_id;
		charge.subject = "养爱车费用";
		charge.body = "养爱车费用";
		charge.channel = "alipay";
		B b = new B("");
		A a = new A();
		a.alipay_wap = b;
		charge.extra = a;
		charge.product_ids = new ArrayList<Long>();
		for(OrderProduct orderProduct : orderProducts){
			charge.product_ids.add(orderProduct.id);
		}
		String json_str = new Gson().toJson(charge);
		if(debug)System.out.println("MyorderDetailActivity json_str:" + json_str);
		Utils.post(this, mClient, ZFB_CHARGE_SUBURL, json_str, mHandler, ZFB_CHARGE_WHAT, true, true);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_PAYMENT) {// 支付页面返回处理
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
					MyToast.makeText(getApplicationContext(), R.color.pay_color, "取消支付，请稍后重新支付", MyToast.LENGTH_SHORT).show();
				}
			} else if (resultCode == Activity.RESULT_CANCELED) {
				showMsg("User canceled", "", "");
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
		AlertDialog.Builder builder = new Builder(MyOrderDetailsActivity.this);
		builder.setMessage(str);
		builder.setTitle("提示");
		builder.setPositiveButton("OK", null);
		builder.create().show();
	}
	
	
	class RequestCharge {
		Double amount;
		String subject;
		String body;
		Long order_id;
		String channel;
		List<Long> product_ids;
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


}
