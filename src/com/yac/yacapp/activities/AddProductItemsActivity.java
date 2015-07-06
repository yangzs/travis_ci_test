package com.yac.yacapp.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.yac.yacapp.R;
import com.yac.yacapp.adapter.MyOrderSelectedItemAdapter;
import com.yac.yacapp.domain.Order;
import com.yac.yacapp.domain.OrderProduct;
import com.yac.yacapp.icounts.App;
import com.yac.yacapp.icounts.BaseActivity;
import com.yac.yacapp.icounts.ICounts;
import com.yac.yacapp.icounts.MyActivityManager;
import com.yac.yacapp.utils.Utils;
import com.yac.yacapp.utils.Utils2;
import com.yac.yacapp.views.MyProgressDialog;
import com.yac.yacapp.views.MyToast;

public class AddProductItemsActivity extends BaseActivity implements ICounts, OnClickListener {

	private static final int GETORDER_WHAT = 1;
	private static final int UPDATE_ADDPRODUCTS_WHAT = 2;
	private LinearLayout back_ll;
	private TextView back_tv, actionbar_title_tv;
	private ImageView mClose_img;
	private ListView increate_products_listview;
	private TextView emptyview_tv;
	private TextView total_price_tv;
	private Button refuse_btn, agree_btn;
	private TextView contact_tv;
	
	private Long mOrder_id;
	private List<OrderProduct> mIncreateProducts;
	private MyOrderSelectedItemAdapter selectedItemAdapter;
	private MyProgressDialog myProgressDialog;
	private AsyncHttpClient mClient;
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GETORDER_WHAT:
				String content = (String) msg.obj;
				parserOrderJson(content);
				break;
			case UPDATE_ADDPRODUCTS_WHAT:
				//更新成功
				Intent intent = new Intent(AddProductItemsActivity.this, MyOrderDetailsActivity.class);
				intent.putExtra("order_id", mOrder_id);
				startActivity(intent);
				finish();
				break;
			case PARSERJSON_SUCCESS:
				if (myProgressDialog != null && myProgressDialog.isShowing()) {
					myProgressDialog.dismiss();
				}
				if(mIncreateProducts != null && mIncreateProducts.size() > 0){
					selectedItemAdapter.updateData(mIncreateProducts);
					updateTotalPrice();
				}else{
					agree_btn.setOnClickListener(null);
					agree_btn.setBackgroundResource(R.drawable.bg_black_buttom);
				}
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
		setContentView(R.layout.activity_add_product_items);
		initView();
		initData();
	}

	private void initView() {
		back_ll = (LinearLayout) findViewById(R.id.back_ll);
		back_tv = (TextView) findViewById(R.id.back_tv);
		actionbar_title_tv = (TextView) findViewById(R.id.actionbar_title_tv);
		mClose_img = (ImageView) findViewById(R.id.close_img);
		increate_products_listview = (ListView) findViewById(R.id.increate_products_listview);
		emptyview_tv = (TextView) findViewById(R.id.emptyview_tv);
		total_price_tv = (TextView) findViewById(R.id.total_price_tv);
		refuse_btn = (Button) findViewById(R.id.refuse_btn);
		agree_btn = (Button) findViewById(R.id.agree_btn);
		contact_tv = (TextView) findViewById(R.id.contact_tv);

		back_ll.setVisibility(View.VISIBLE);
		back_ll.setOnClickListener(this);
		back_tv.setText("返回");
		actionbar_title_tv.setText("新增项目提示");
		mClose_img.setOnClickListener(this);
		refuse_btn.setOnClickListener(this);
		agree_btn.setOnClickListener(this);
		contact_tv.setOnClickListener(this);
		increate_products_listview.setEmptyView(emptyview_tv);
		mIncreateProducts = new ArrayList<OrderProduct>();
		selectedItemAdapter = new MyOrderSelectedItemAdapter(this, mIncreateProducts);
		increate_products_listview.setAdapter(selectedItemAdapter);
		increate_products_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				OrderProduct orderProduct = mIncreateProducts.get(position);
				if(orderProduct.selection_mode == 3){
					orderProduct.selection_mode = 4;
				}else if(orderProduct.selection_mode == 2 || orderProduct.selection_mode ==4){
					orderProduct.selection_mode = 3;
				}
				selectedItemAdapter.updateData(mIncreateProducts);
				updateTotalPrice();
			}
		});
	}
	
	private void initData() {
		mOrder_id = getIntent().getLongExtra("order_id", -1);
		myProgressDialog = MyProgressDialog.createDialog(this);
		myProgressDialog.setMessage(getString(R.string.progress_msg));
		myProgressDialog.show();
		mClient = new AsyncHttpClient();
		Map<String, String> map = new HashMap<String, String>();
		map.put("detail", "true");
		map.put("order_id", String.valueOf(mOrder_id));
		map.put("keeper_id", String.valueOf(App.mCarUser.user_id));
		Utils.get(this, mClient, GETORDER_SUBURL, map, mHandler, GETORDER_WHAT, true, true);
	}

	private void updateTotalPrice() {
		Double total = 0.00;
		for(OrderProduct orderProduct : mIncreateProducts){
			if(orderProduct.selection_mode == 3){
				total += orderProduct.total_price;
			}
		}
		total_price_tv.setText("总计：￥"+total);
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
		case R.id.refuse_btn:
			// 返回主界面
			finish();
			break;
		case R.id.agree_btn:
			// 将选中的product添加到order中
			updateOrderProducts();
			break;
		case R.id.contact_tv:
			try{
				Utils2.phoneCall(this, contact_tv.getText().toString().trim());
			}catch (Exception e) {
				MyToast.makeText(getApplicationContext(), R.color.pay_color, "设备拨号异常", MyToast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
	}
	
	private void updateOrderProducts() {
		myProgressDialog.show();
		A a = new A();
		a.id = mOrder_id;
		a.increase_products = mIncreateProducts;
		String json_str = new Gson().toJson(a);
		Utils.post(this, mClient, UPDATE_ORDER_SUBURL, json_str, mHandler, UPDATE_ADDPRODUCTS_WHAT, true, true);
	}

	protected void parserOrderJson(String content) {
		Order order = new Gson().fromJson(content, Order.class);
		mIncreateProducts = order.increase_products;
		mHandler.sendEmptyMessage(PARSERJSON_SUCCESS);
	}
	
	class A{
		Long id;
		List<OrderProduct> increase_products;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		MyActivityManager.getInstance().removeActivity(this);
	}

}
