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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.yac.yacapp.R;
import com.yac.yacapp.domain.Car;
import com.yac.yacapp.domain.CarCareBook;
import com.yac.yacapp.domain.Licence;
import com.yac.yacapp.domain.Part;
import com.yac.yacapp.domain.Picture;
import com.yac.yacapp.domain.UserInfo;
import com.yac.yacapp.icounts.App;
import com.yac.yacapp.icounts.BaseActivity;
import com.yac.yacapp.icounts.ICounts;
import com.yac.yacapp.utils.Utils;
import com.yac.yacapp.utils.Utils2;
import com.yac.yacapp.views.ListViewCompat;
import com.yac.yacapp.views.MyProgressDialog;
import com.yac.yacapp.views.MyToast;
import com.yac.yacapp.views.SlideView;
import com.yac.yacapp.views.SlideView.OnSlideListener;

public class MyCarActivity extends BaseActivity implements ICounts, OnSlideListener, OnClickListener {

	private static final int GET_USERINFO = 0;
	private static final int REQUEST_SETTING = 1;
	private static final int DELETE_CAR_WHAT = 2;
	private List<MessageItem> mMessageItems = new ArrayList<MessageItem>();
	private List<Car> mCars = new ArrayList<Car>();
	private LinearLayout mBack_ll;
	private TextView mBack_tv, mActionbar_title_tv;
	private ImageView mClose_img;
	private ListViewCompat car_listview;
	private Button add_car_btn;
	
    private SlideView mLastSlideViewWithStatusOn;
    
	private SlideAdapter mAdapter;
	private MyProgressDialog myProgressDialog;
	private AsyncHttpClient mClient;
	private DisplayImageOptions options;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GET_USERINFO:
				String content1 = (String) msg.obj;
				parserUserInfoJson(content1);
				break;
			case PARSERJSON_SUCCESS:
				if (myProgressDialog != null && myProgressDialog.isShowing()) {
					myProgressDialog.dismiss();
				}
				initMessageItemData();
				break;
			case DELETE_CAR_WHAT:
				App.needNetDate = true;
				getNetData();
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
		setContentView(R.layout.activity_mycar);
		mClient = new AsyncHttpClient();
		myProgressDialog = MyProgressDialog.createDialog(this);
		myProgressDialog.setMessage(getString(R.string.progress_msg));
		options = new DisplayImageOptions.Builder().cacheOnDisk(true).showImageOnLoading(R.drawable.car_brand_default).showImageOnFail(R.drawable.car_brand_default).build();
		initView();
		//getNetData();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getNetData();
	}

	protected void initMessageItemData() {
		if(mCars != null && mMessageItems != null){
			mMessageItems.clear();
			for(Car car : mCars){
				MessageItem item = new MessageItem();
				item.car = car;
				mMessageItems.add(item);
			}
			mAdapter.notifyDataSetChanged();
			Utils2.setListViewHeightBasedOnChildren(car_listview);
		}
	}

	private void getNetData() {
		if(App.needNetDate){
			App.needNetDate = false;
			myProgressDialog.show();
			Map<String, String> map = new HashMap<String, String>();
			map.put("user_id", String.valueOf(App.mCarUser.user_id));
			Utils.get(this, mClient, USER_INFO_SUBURL, map, mHandler, GET_USERINFO, true);
		}else{
			String userinfo_str = mSP.getString(SP_UserInfo, null);
			UserInfo userInfo = new Gson().fromJson(userinfo_str, UserInfo.class);
			mCars = userInfo.cars;
			mHandler.sendEmptyMessage(PARSERJSON_SUCCESS);
		}
	}

	private void initView() {
		mBack_ll = (LinearLayout) findViewById(R.id.back_ll);
		mBack_tv = (TextView) findViewById(R.id.back_tv);
		mActionbar_title_tv = (TextView) findViewById(R.id.actionbar_title_tv);
		mClose_img = (ImageView) findViewById(R.id.close_img);
		car_listview = (ListViewCompat) findViewById(R.id.car_listview);
		add_car_btn = (Button) findViewById(R.id.add_car_btn);
		
		mBack_ll.setVisibility(View.VISIBLE);
		mBack_ll.setOnClickListener(this);
		mBack_tv.setText("我");
		mActionbar_title_tv.setText("我的车");
		mClose_img.setOnClickListener(this);
		add_car_btn.setOnClickListener(this);
		mAdapter = new SlideAdapter();
		car_listview.setAdapter(mAdapter);
		Utils2.setListViewHeightBasedOnChildren(car_listview);
		car_listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Car car = mMessageItems.get(position).car;
				Intent intent = new Intent(MyCarActivity.this, CarSettingActivity.class);
				intent.addFlags(FROM_MYCAR);
				intent.putExtra("car", car);
				startActivityForResult(intent, REQUEST_SETTING);
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK && requestCode == REQUEST_SETTING){
			getNetData();
		}
	}

	private class SlideAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		SlideAdapter() {
			super();
			mInflater = getLayoutInflater();
		}

		@Override
		public int getCount() {
			return mMessageItems.size();
		}

		@Override
		public Object getItem(int position) {
			return mMessageItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			SlideView slideView = (SlideView) convertView;
			if (slideView == null) {
				View itemView = mInflater.inflate(R.layout.item_car_info, null);

				slideView = new SlideView(MyCarActivity.this);
				slideView.setContentView(itemView);

				holder = new ViewHolder(slideView);
				slideView.setOnSlideListener(MyCarActivity.this);
				slideView.setTag(holder);
			} else {
				holder = (ViewHolder) slideView.getTag();
			}
			MessageItem item = mMessageItems.get(position);
			item.slideView = slideView;
			item.slideView.shrink();

			mImageLoader.displayImage(item.car.brand_img_url.thumbnail_url, holder.car_brand_img, options);
			holder.car_licence_tv.setText(item.car.licence.toString());
			holder.car_brand_tv.setText(item.car.brand+" "+item.car.category+" "+item.car.model);
			holder.deleteHolder.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					deleteCar(mMessageItems.get(position).car.id);
				}
			});
			return slideView;
		}
	}

	public class MessageItem {
		public Car car;
		public SlideView slideView;
	}

	private static class ViewHolder {
		public ImageView car_brand_img;
		public TextView car_licence_tv;
		public TextView car_brand_tv;
		public ViewGroup deleteHolder;

		ViewHolder(View view) {
			car_brand_img = (ImageView) view.findViewById(R.id.car_brand_img);
			car_licence_tv = (TextView) view.findViewById(R.id.car_licence_tv);
			car_brand_tv = (TextView) view.findViewById(R.id.car_brand_tv);
			deleteHolder = (ViewGroup) view.findViewById(R.id.holder);
		}
	}

	@Override
	public void onSlide(View view, int status) {
        if (mLastSlideViewWithStatusOn != null && mLastSlideViewWithStatusOn != view) {
            mLastSlideViewWithStatusOn.shrink();
        }
        if (status == SLIDE_STATUS_ON) {
            mLastSlideViewWithStatusOn = (SlideView) view;
        }
	}
	
	protected void deleteCar(long id) {
		myProgressDialog.show();
		StringBuffer buffer = new StringBuffer();
		buffer.append("[").append(id).append("]");
		Utils.post(this, mClient, DELETE_CAR_SUBURL, buffer.toString(), mHandler, DELETE_CAR_WHAT, true);
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(id == R.id.back_ll){
			finish();
		}else if(id == R.id.add_car_btn){
			startActivity(new Intent(this, PickCarActivity.class));
		}else if (id == R.id.close_img){
			closeActivities();
		}
	}

	protected void parserUserInfoJson(String content1) {
		Gson gson = new Gson();
		UserInfo userInfo = gson.fromJson(content1, UserInfo.class);
		mSP.edit().putString(SP_UserInfo, content1).commit();
		mCars.clear();
		mCars = userInfo.cars;
		sendUpdateBroadcastReceiver();
		mHandler.sendEmptyMessage(PARSERJSON_SUCCESS);
	}
	
	protected void parserUserInfoJson1(String content) {
		try {
			JSONObject jsonObject = new JSONObject(content);
			mCars.clear();
			JSONArray cars_arr = jsonObject.getJSONArray(UserInfo.KEY_CARS);
			JSONObject object;
			for (int i = 0; i < cars_arr.length(); i++) {
				Car car = new Car();
				object = cars_arr.optJSONObject(i);
				car.id = object.optLong(Car.KEY_ID);
				car.disabled = object.optBoolean(Car.KEY_DISABLED);
				// for car licence
				JSONObject licence_obj = object.optJSONObject(Car.KEY_LICENCE);
				Licence licence = new Licence();
				licence.province = licence_obj.optString(Licence.KEY_PROVINCE);
				licence.number = licence_obj.optString(Licence.KEY_NUMBER);
				car.licence = licence;
				car.brand = object.optString(Car.KEY_BRAND);
				car.category = object.optString(Car.KEY_CATEGORY);
				car.model = object.optString(Car.KEY_MODEL);
				car.bought_time = object.optString(Car.KEY_BOUGHT_TIME);
				car.miles = object.optInt(Car.KEY_MILES);
				car.chassis_number = object.optString(Car.KEY_CHASSIS_NUMBER);
				car.engine_number = object.optString(Car.KEY_ENGINE_NUMBER);
				//for brand_img_url
				JSONObject img_url_obj = object.optJSONObject(Car.KEY_BRAND_IMG_URL);
				Picture picture = new Picture();
				picture.id = img_url_obj.getLong(Picture.KEY_ID);
				picture.original_url = img_url_obj.getString(Picture.KEY_ORIGINAL_URL);
				picture.thumbnail_url = img_url_obj.getString(Picture.KEY_THUMBNAIL_URL);
				car.brand_img_url = picture;
				car.model_type = object.optInt(Car.KEY_MODEL_TYPE);
				car.user_id = object.optLong(Car.KEY_USER_ID);
				// "carcare_book": [ ], for car care book
				JSONArray carcare_arr = object.optJSONArray(Car.KEY_CARCARE_BOOK);
				List<CarCareBook> carCareBooks;
				if (carcare_arr.length() > 0) {
					carCareBooks = new ArrayList<CarCareBook>();
					JSONObject carcare_obj;
					for (int k = 0; k < carcare_arr.length(); k++) {
						carcare_obj = carcare_arr.optJSONObject(k);
						CarCareBook carCareBook = new CarCareBook();
						carCareBook.miles = carcare_obj.optInt(CarCareBook.KEY_MILES);
						JSONArray str_arr = carcare_obj.optJSONArray(CarCareBook.KEY_EVENTS);
						List<String> strs = new ArrayList<String>();
						for (int m = 0; m < str_arr.length(); m++) {
							strs.add(str_arr.optString(m));
						}
						carCareBook.events = strs;
						carCareBooks.add(carCareBook);
					}
					car.carcare_book = carCareBooks;
				}
				car.whole_score = object.optInt(Car.KEY_WHOLE_SCORE);
				// for parts
				JSONArray part_arr = object.optJSONArray(Car.KEY_PARTS);
				List<Part> parts = new ArrayList<Part>();
				JSONObject part_obj;
				for (int j = 0; j < part_arr.length(); j++) {
					part_obj = part_arr.optJSONObject(j);
					Part part = new Part();
					part.id = part_obj.optLong(Part.KEY_ID);
					part.score = part_obj.optInt(Part.KEY_SCORE);
					part.status = part_obj.optString(Part.KEY_STATUS);
					part.comment = part_obj.optString(Part.KEY_COMMENT);
					part.car_id = part_obj.optLong(Part.KEY_CAR_ID);
					part.type = part_obj.optInt(Part.KEY_TYPE);
					part.name = part_obj.optString(Part.KEY_NAME);
					part.last_update_time = part_obj.optString(Part.KEY_LAST_UPDATE_TIME);
					part.used_miles = part_obj.optInt(Part.KEY_USED_MILES);
					parts.add(part);
				}
				car.parts = parts;
				mCars.add(car);
			}
			mHandler.sendEmptyMessage(PARSERJSON_SUCCESS);
		} catch (JSONException e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(PARSERJSON_FAILURE);
		}
	}
	
}
