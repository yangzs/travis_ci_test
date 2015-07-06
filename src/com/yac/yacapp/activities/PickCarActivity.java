package com.yac.yacapp.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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
import com.yac.yacapp.adapter.SortAdapter;
import com.yac.yacapp.domain.CarBrand;
import com.yac.yacapp.icounts.BaseActivity;
import com.yac.yacapp.icounts.ICounts;
import com.yac.yacapp.utils.PinyinComparator;
import com.yac.yacapp.utils.Utils;
import com.yac.yacapp.views.MyProgressDialog;
import com.yac.yacapp.views.MyToast;
import com.yac.yacapp.views.SideBar;

public class PickCarActivity extends BaseActivity implements ICounts, OnClickListener {
	
	private static final int GET_CAR_BRAND = 0;
	private LinearLayout mBack_ll;
	private TextView mBack_tv, mActionbar_title_tv;
	private ImageView mClose_img;
	private ListView mCar_brands_listview;
	private TextView mDialog;
	private SideBar mSideBar;
	
	private List<CarBrand> mCarBrands;
	private SortAdapter mAdapter;
	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;
	
	private AsyncHttpClient mClient;
	private MyProgressDialog myProgressDialog;
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GET_CAR_BRAND:
				String content = (String) msg.obj;
				parserCarBrandJson(content);
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
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pick_car);
		pinyinComparator = new PinyinComparator();
		mClient = new AsyncHttpClient();
		myProgressDialog = MyProgressDialog.createDialog(this);
		myProgressDialog.setMessage(getString(R.string.progress_msg));
		initView();
		getNetData();
	}

	protected void invilateContentView() {
		Collections.sort(mCarBrands, pinyinComparator);
		mAdapter.updateListView(mCarBrands);
	}

	private void getNetData() {
		myProgressDialog.show();
		Utils.get(this, mClient, GET_CAR_BRAND_SUBURL, new HashMap<String, String>(), mHandler, GET_CAR_BRAND, true);
	}

	private void initView() {
		mBack_ll = (LinearLayout) findViewById(R.id.back_ll);
		mBack_tv = (TextView) findViewById(R.id.back_tv);
		mActionbar_title_tv = (TextView) findViewById(R.id.actionbar_title_tv);
		mClose_img = (ImageView) findViewById(R.id.close_img);
		mCar_brands_listview = (ListView) findViewById(R.id.car_brands_listview);
		mDialog = (TextView) findViewById(R.id.dialog);
		mSideBar = (SideBar) findViewById(R.id.sidrbar);

		mBack_ll.setVisibility(View.VISIBLE);
		mBack_ll.setOnClickListener(this);
		mBack_tv.setText("主页");
		mActionbar_title_tv.setText("选择车辆");
		mClose_img.setOnClickListener(this);
		mSideBar.setTextView(mDialog);
		// 设置右侧触摸监听
		mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = mAdapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					mCar_brands_listview.setSelection(position);
				}
			}
		});
		mCarBrands = new ArrayList<CarBrand>();
		// 根据a-z进行排序源数据
		Collections.sort(mCarBrands, pinyinComparator);
		mAdapter = new SortAdapter(this, mCarBrands);
		mCar_brands_listview.setAdapter(mAdapter);
		mCar_brands_listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				CarBrand carBrand = mCarBrands.get(position);
				Intent intent = new Intent(getApplicationContext(), BrandCategoryActivity.class);
				intent.putExtra("car_brand", carBrand);
				startActivity(intent);
			}
		});
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
		default:
			break;
		}
	}

	protected void parserCarBrandJson(String content) {
		try {
			JSONArray jsonArray = new JSONArray(content);
			JSONObject object;
			for(int i=0; i<jsonArray.length(); i++){
				object = jsonArray.optJSONObject(i);
				CarBrand carBrand = new CarBrand();
				carBrand.brand_name = object.optString(CarBrand.KEY_BRAND_NAME);
				carBrand.brand_pinyin = object.optString(CarBrand.KEY_BRAND_PINYIN);
				carBrand.brand_type = object.optInt(CarBrand.KEY_BRAND_TYPE);
				carBrand.first_letter = object.optString(CarBrand.KEY_FIRST_LETTER).toUpperCase();//全部转为大写
				carBrand.img_url = object.optString(CarBrand.KEY_IMG_URL);
				carBrand.logo_attachment_id = object.optLong(CarBrand.KEY_LOGO_ATTACHMENT_ID);
				mCarBrands.add(carBrand);
			}
			mHandler.sendEmptyMessage(PARSERJSON_SUCCESS);
		} catch (JSONException e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(PARSERJSON_FAILURE);
		}
	}
	
}
