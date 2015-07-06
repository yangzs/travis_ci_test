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
import com.yac.yacapp.adapter.MyCategoryAdapter;
import com.yac.yacapp.domain.CarBrand;
import com.yac.yacapp.domain.CarCategoryBean;
import com.yac.yacapp.icounts.BaseActivity;
import com.yac.yacapp.icounts.ICounts;
import com.yac.yacapp.utils.Utils;
import com.yac.yacapp.views.MyProgressDialog;
import com.yac.yacapp.views.MyToast;

public class BrandCategoryActivity extends BaseActivity implements ICounts, OnClickListener {

	private static final int GET_CAR_CATEGORY = 0;
	private static final int CATEGORY_PARSERJSON_SUCCESS = 2;
	private LinearLayout mBack_ll;
	private TextView mBack_tv, mActionbar_title_tv;
	private ImageView mClose_img;
	private ListView mCategory_listview;

	private List<CarCategoryBean> mCategoryBeans;
	private MyCategoryAdapter myCategoryAdapter;
	private CarBrand mCarBrand;
	private MyProgressDialog myProgressDialog;
	private AsyncHttpClient mClient;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GET_CAR_CATEGORY:
				String content = (String) msg.obj;
				parserCategoryJson(content);
				break;
			case CATEGORY_PARSERJSON_SUCCESS:
				if (myProgressDialog != null && myProgressDialog.isShowing()) {
					myProgressDialog.dismiss();
				}
				myCategoryAdapter.updateData(mCategoryBeans);
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
		mCarBrand = (CarBrand) getIntent().getSerializableExtra("car_brand");
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
		mBack_tv.setText("选择车辆");
		mActionbar_title_tv.setText("车型信息");
		mClose_img.setOnClickListener(this);
		mCategoryBeans = new ArrayList<CarCategoryBean>();
		myCategoryAdapter = new MyCategoryAdapter(this, mCategoryBeans);
		mCategory_listview.setAdapter(myCategoryAdapter);
		mCategory_listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				CarCategoryBean carCategoryBean = mCategoryBeans.get(position);
				Intent intent = new Intent(getApplicationContext(), CarModelActivity.class);
				intent.putExtra("category_type", carCategoryBean.category_type);
				intent.putExtra("brand_img", mCarBrand.img_url);
				startActivity(intent);
			}
		});
	}

	private void getNetData() {
		myProgressDialog.show();
		Map<String, String> map = new HashMap<String, String>();
		map.put("brand_type", String.valueOf(mCarBrand.brand_type));
		Utils.get(this, mClient, GET_CATEGORY_SUBURL, map, mHandler, GET_CAR_CATEGORY, true);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.back_ll) {
			finish();
		}else if(v.getId() == R.id.close_img){
			closeActivities();
		}
	}

	protected void parserCategoryJson(String content) {
		try {
			JSONArray array = new JSONArray(content);
			mCategoryBeans.clear();
			JSONObject jsonObject;
			for (int i = 0; i < array.length(); i++) {
				jsonObject = array.optJSONObject(i);
				CarCategoryBean carCategoryBean = new CarCategoryBean();
				carCategoryBean.brand_name = jsonObject.optString(CarCategoryBean.KEY_BRAND_NAME);
				carCategoryBean.category_name = jsonObject.optString(CarCategoryBean.KEY_CATEGORY_NAME);
				carCategoryBean.category_type = jsonObject.optInt(CarCategoryBean.KEY_CATEGORY_TYPE);
				mCategoryBeans.add(carCategoryBean);
			}
			mHandler.sendEmptyMessage(CATEGORY_PARSERJSON_SUCCESS);
		} catch (JSONException e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(PARSERJSON_FAILURE);
		}
	}

}
