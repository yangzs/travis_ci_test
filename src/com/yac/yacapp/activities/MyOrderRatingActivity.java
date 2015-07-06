package com.yac.yacapp.activities;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.yac.yacapp.R;
import com.yac.yacapp.domain.ClientFeedback;
import com.yac.yacapp.domain.Order;
import com.yac.yacapp.domain.OrderKeeperBasic;
import com.yac.yacapp.domain.OrderProduct;
import com.yac.yacapp.icounts.BaseActivity;
import com.yac.yacapp.icounts.ICounts;
import com.yac.yacapp.utils.Utils;
import com.yac.yacapp.views.MyProgressDialog;
import com.yac.yacapp.views.MyToast;

public class MyOrderRatingActivity extends BaseActivity implements ICounts, OnClickListener {

	private static final int FEEDBACK_WHAT = 1;
	private LinearLayout back_ll;
	private TextView back_tv, actionbar_title_tv;
	private ImageView mClose_img;
	private TextView order_product_tv, service_time_tv, order_price_tv, keeper_tv;
	private RatingBar keeper_rating, order_rating;
	private EditText rating_remark_edt;
	private Button commit_rating_btn;
	
	private Order mOrder;
	private AsyncHttpClient mClient;
	private MyProgressDialog myProgressDialog;
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case FEEDBACK_WHAT:
				mHandler.sendEmptyMessage(PARSERJSON_SUCCESS);
				break;
			case PARSERJSON_SUCCESS:
				if (myProgressDialog != null && myProgressDialog.isShowing()) {
					myProgressDialog.dismiss();
				}
				finish();
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
		setContentView(R.layout.activity_myorder_rating);
		mOrder = (Order) getIntent().getSerializableExtra("order");
		mClient = new AsyncHttpClient();
		myProgressDialog = MyProgressDialog.createDialog(this);
		myProgressDialog.setMessage(getString(R.string.progress_msg));
		initView();
		initData();
	}

	private void initView() {
		findViewById(R.id.head_ll).setOnClickListener(mSoftInputOnClickListener);
		back_ll = (LinearLayout) findViewById(R.id.back_ll);
		back_tv = (TextView) findViewById(R.id.back_tv);
		actionbar_title_tv = (TextView) findViewById(R.id.actionbar_title_tv);
		mClose_img = (ImageView) findViewById(R.id.close_img);
		order_product_tv = (TextView) findViewById(R.id.order_product_tv);
		service_time_tv = (TextView) findViewById(R.id.service_time_tv);
		order_price_tv = (TextView) findViewById(R.id.order_price_tv);
		keeper_tv = (TextView) findViewById(R.id.keeper_tv);
		keeper_rating = (RatingBar) findViewById(R.id.keeper_rating);
		order_rating = (RatingBar) findViewById(R.id.order_rating);
		rating_remark_edt = (EditText) findViewById(R.id.rating_remark_edt);
		commit_rating_btn = (Button) findViewById(R.id.commit_rating_btn);
		
		back_ll.setVisibility(View.VISIBLE);
		back_ll.setOnClickListener(this);
		back_tv.setText("我的订单");
		actionbar_title_tv.setText("评价");
		mClose_img.setOnClickListener(this);
		commit_rating_btn.setOnClickListener(this);
	}
	
	private void initData() {
		StringBuffer buffer = new StringBuffer();
		for(OrderProduct orderProduct : mOrder.products){
			buffer.append(orderProduct.product_name).append(" ");
		}
		order_product_tv.setText(buffer.toString());
		service_time_tv.setText(mOrder.start_time);
		order_price_tv.setText("￥"+mOrder.total_price);
		if(mOrder.keeper_basics != null){
			buffer = new StringBuffer();
			for(OrderKeeperBasic keeperBasic : mOrder.keeper_basics){
				buffer.append(keeperBasic.name).append(" ");
			}
			keeper_tv.setText(buffer.toString());
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
		case R.id.commit_rating_btn:
			commitRating();
			break;
		default:
			break;
		}
	}

	private void commitRating() {
		myProgressDialog.show();
		A a = new A();
		a.client_feedback = new ClientFeedback();
		a.client_feedback.keeper_stars = (int) keeper_rating.getRating();
		a.client_feedback.order_stars = (int) order_rating.getRating();
		String commit = rating_remark_edt.getText().toString().trim();
		if(!TextUtils.isEmpty(commit)){
			a.client_feedback.comment = commit;
		}
		a.order_id = mOrder.id;
		String json_str = new Gson().toJson(a);
		Utils.post(this, mClient, CLIENT_FEEDBACK_SUBURL, json_str, mHandler, FEEDBACK_WHAT, true);
	}
	
	class A{
		Long order_id;
		ClientFeedback client_feedback;
	}
	
}
