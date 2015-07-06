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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.yac.yacapp.R;
import com.yac.yacapp.adapter.MyModelAdapter;
import com.yac.yacapp.domain.CarModelBean;
import com.yac.yacapp.icounts.BaseActivity;
import com.yac.yacapp.icounts.ICounts;
import com.yac.yacapp.utils.Utils;
import com.yac.yacapp.views.MyProgressDialog;
import com.yac.yacapp.views.MyToast;

public class CarModelActivity extends BaseActivity implements ICounts, OnClickListener {

	private static final int GET_CAR_MODEL = 1;
	private static final int MODEL_PARSERJSON_SUCCESS = 3;
	private LinearLayout mBack_ll;
	private TextView mBack_tv, mActionbar_title_tv;
	private ImageView mClose_img;
	private ListView mCategory_listview;

	private List<CarModelBean> mCarModelBeans;
	private MyModelAdapter myModelAdapter;

	private CarModelBean mCurrentModel;
	private int mCurrent_CategoryType;
	private String mBrand_img;
	private MyProgressDialog myProgressDialog;
	private AsyncHttpClient mClient;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GET_CAR_MODEL:
				String content1 = (String) msg.obj;
				parserModelJson(content1);
				break;
			case MODEL_PARSERJSON_SUCCESS:
				if (myProgressDialog != null && myProgressDialog.isShowing()) {
					myProgressDialog.dismiss();
				}
				myModelAdapter.updateData(mCarModelBeans);
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_brand_category);
		mCurrent_CategoryType = getIntent().getIntExtra("category_type", -1);
		mBrand_img = getIntent().getStringExtra("brand_img");
		mClient = new AsyncHttpClient();
		myProgressDialog = MyProgressDialog.createDialog(this);
		myProgressDialog.setMessage(getString(R.string.progress_msg));
		initView();
		getNetData();
	}

	private void initView() {
		mBack_ll = (LinearLayout) findViewById(R.id.back_ll);
		mBack_tv = (TextView) findViewById(R.id.back_tv);
		mActionbar_title_tv = (TextView) findViewById(R.id.actionbar_title_tv);
		mClose_img = (ImageView) findViewById(R.id.close_img);
		mCategory_listview = (ListView) findViewById(R.id.category_listview);

		mBack_ll.setVisibility(View.VISIBLE);
		mBack_ll.setOnClickListener(this);
		mBack_tv.setText("车型信息");
		mActionbar_title_tv.setText("具体型号");
		mClose_img.setOnClickListener(this);
		mCarModelBeans = new ArrayList<CarModelBean>();
		myModelAdapter = new MyModelAdapter(this, mCarModelBeans);
		mCategory_listview.setAdapter(myModelAdapter);
		mCategory_listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mCurrentModel = mCarModelBeans.get(position);
				Intent intent = new Intent(getApplicationContext(), CarSettingActivity.class);
				intent.addFlags(FROM_CARMODEL);
				intent.putExtra("car_model", mCurrentModel);
				intent.putExtra("brand_img", mBrand_img);
				startActivity(intent);
			}
		});
	}

	private void getNetData() {
		myProgressDialog.show();
		Map<String, String> map = new HashMap<String, String>();
		map.put("category_type", String.valueOf(mCurrent_CategoryType));
		Utils.get(this, mClient, GET_MODEL_SUBURL, map, mHandler, GET_CAR_MODEL, true);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.back_ll) {
			finish();
		}else if (v.getId() == R.id.close_img){
			closeActivities();
		}
	}

	protected void parserModelJson(String content1) {
		try {
			JSONArray array = new JSONArray(content1);
			mCarModelBeans.clear();
			JSONObject jsonObject;
			for (int i = 0; i < array.length(); i++) {
				jsonObject = array.optJSONObject(i);
				CarModelBean carModelBean = new CarModelBean();
				carModelBean.model_type = jsonObject.optInt(CarModelBean.KEY_MODEL_TYPE);
				carModelBean.brand_name = jsonObject.optString(CarModelBean.KEY_BRAND_NAME);
				carModelBean.category_name = jsonObject.optString(CarModelBean.KEY_CATEGORY_NAME);
				carModelBean.engine_displacement = jsonObject.optString(CarModelBean.KEY_ENGINE_DISPLACEMENT);
				carModelBean.production_year = jsonObject.optString(CarModelBean.KEY_PRODUCTION_YEAR);
				carModelBean.producer = jsonObject.optString(CarModelBean.KEY_PRODUCER);
				carModelBean.car_model_Name = jsonObject.optString(CarModelBean.KEY_CAR_MODEL_NAME);
				carModelBean.engine_oil_amount = jsonObject.optString(CarModelBean.KEY_ENGINE_OIL_AMOUNT);
				mCarModelBeans.add(carModelBean);
			}
			mHandler.sendEmptyMessage(MODEL_PARSERJSON_SUCCESS);
		} catch (JSONException e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(PARSERJSON_FAILURE);
		}
	}

}
