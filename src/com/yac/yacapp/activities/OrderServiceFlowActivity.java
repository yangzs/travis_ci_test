package com.yac.yacapp.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.yac.yacapp.R;
import com.yac.yacapp.adapter.MyServiceFlowAdapter;
import com.yac.yacapp.domain.OrderServiceFlow;
import com.yac.yacapp.icounts.BaseActivity;
import com.yac.yacapp.icounts.ICounts;
import com.yac.yacapp.utils.Utils;
import com.yac.yacapp.views.MyProgressDialog;
import com.yac.yacapp.views.MyToast;

public class OrderServiceFlowActivity extends BaseActivity implements ICounts, OnClickListener {

	private static final int GET_SERVICEFLOW_WHAT = 0;
	private LinearLayout back_ll;
	private TextView back_tv, actionbar_title_tv;
	private ImageView mClose_img;
	private ListView flow_listview;

	private Long mOrder_Id;
	private List<OrderServiceFlow> mOrderServiceFlows;
	private MyServiceFlowAdapter myServiceFlowAdapter;
	private MyProgressDialog myProgressDialog;
	private AsyncHttpClient mClient;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GET_SERVICEFLOW_WHAT:
				String content = (String) msg.obj;
				parserServiceFlowJson(content);
				break;
			case PARSERJSON_SUCCESS:
				if (myProgressDialog != null && myProgressDialog.isShowing()) {
					myProgressDialog.dismiss();
				}
				invilateView();
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
		setContentView(R.layout.activity_order_serviceflow);
		myProgressDialog = MyProgressDialog.createDialog(this);
		myProgressDialog.setMessage(getString(R.string.progress_msg));
		mClient = new AsyncHttpClient();
		mOrder_Id = getIntent().getLongExtra("order_id", -1);
		initView();
		getNetData();
	}

	private void getNetData() {
		myProgressDialog.show();
		Map<String, String> map = new HashMap<String, String>();
		map.put("order_id", String.valueOf(mOrder_Id));
		String testurl = "http://192.168.2.151:8080/mockjsdata/2/v1/api/order/service_flow?order_id=";
		Utils.get(this, mClient, SERVICE_FLOW_SUBURL, map, mHandler, GET_SERVICEFLOW_WHAT, true, null);
	}

	private void initView() {
		back_ll = (LinearLayout) findViewById(R.id.back_ll);
		back_tv = (TextView) findViewById(R.id.back_tv);
		actionbar_title_tv = (TextView) findViewById(R.id.actionbar_title_tv);
		mClose_img = (ImageView) findViewById(R.id.close_img);
		flow_listview = (ListView) findViewById(R.id.flow_listview);

		back_ll.setVisibility(View.VISIBLE);
		back_ll.setOnClickListener(this);
		back_tv.setText("我的订单");
		actionbar_title_tv.setText("服务流程");
		mClose_img.setOnClickListener(this);
		if (mOrderServiceFlows == null) {
			mOrderServiceFlows = new ArrayList<OrderServiceFlow>();
		}
		myServiceFlowAdapter = new MyServiceFlowAdapter(this, mOrderServiceFlows);
		flow_listview.setAdapter(myServiceFlowAdapter);
	}

	protected void invilateView() {
		myServiceFlowAdapter.updateData(mOrderServiceFlows);
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.back_ll){
			finish();
		}else if(v.getId() == R.id.close_img){
			closeActivities();
		}
	}

	protected void parserServiceFlowJson(String content) {
		try {
			JSONArray jsonArray = new JSONArray(content);
			if (jsonArray != null && jsonArray.length() > 0) {
				Gson gson = new Gson();
				for (int i = 0; i < jsonArray.length(); i++) {
					String str = jsonArray.optString(i);
					OrderServiceFlow serviceFlow = gson.fromJson(str, OrderServiceFlow.class);
					if (serviceFlow != null) {
						mOrderServiceFlows.add(serviceFlow);
					}
				}
			}
			mHandler.sendEmptyMessage(PARSERJSON_SUCCESS);
		} catch (JSONException e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(PARSERJSON_FAILURE);
		}
	}

}
