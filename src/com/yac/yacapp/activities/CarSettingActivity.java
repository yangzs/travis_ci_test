package com.yac.yacapp.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.yac.yacapp.R;
import com.yac.yacapp.domain.Car;
import com.yac.yacapp.domain.CarModelBean;
import com.yac.yacapp.domain.Licence;
import com.yac.yacapp.icounts.App;
import com.yac.yacapp.icounts.BaseActivity;
import com.yac.yacapp.icounts.ICounts;
import com.yac.yacapp.utils.Utils;
import com.yac.yacapp.views.MyProgressDialog;
import com.yac.yacapp.views.MyToast;

public class CarSettingActivity extends BaseActivity implements ICounts, OnClickListener {

	private static final int CRETAE_CAR_WHAT = 0;
	private LinearLayout mBack_ll;
	private TextView mBack_tv, mActionbar_title_tv;
	private ImageView mClose_img;
	private TextView province_tv;
	private EditText number_ed;
	private RelativeLayout buy_year_rl;
	private TextView buy_year_tv;
	private EditText miles_ed, chassis_number_ed, engine_number_ed;
	private ImageView chassis_img, engine_img;
	private Button car_setting_btn;
	private ImageView car_brand_img;
	private TextView car_licence_tv, car_brand_tv;

	private int flag;// FROM_MYCAR = 1; FROM_CARMODEL = 2;
	private Car mCar;
	private CarModelBean mCurrentModelBean;
	private String mBrand_img;
	private PopupWindow mPopupWindow;
	private PopupWindow mProvincePopupWin;
	private View parentView;
	private MyProgressDialog myProgressDialog;
	private AsyncHttpClient mClient;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case CRETAE_CAR_WHAT:
				String content = (String) msg.obj;
				if(debug)System.out.println("content:"+content);
				Utils.updateUserInfoJson(CarSettingActivity.this, mClient, mHandler);
				break;
			case UPDATE_USERINFO_WHAT:
				String userinfo_json = (String) msg.obj;
				updateUserInfoJson(userinfo_json);
				break;
			case PARSERJSON_SUCCESS:
				if (myProgressDialog != null && myProgressDialog.isShowing()) {
					myProgressDialog.dismiss();
				}
				if (flag == FROM_CARMODEL) {
					closeActivities();
				}else if(flag == FROM_MYCAR){
					setResult(RESULT_OK);
					finish();
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
		parentView = LayoutInflater.from(this).inflate(R.layout.activity_car_setting, null);
		setContentView(parentView);
		Intent intent = getIntent();
		flag = intent.getFlags();
		if (flag == FROM_MYCAR) {
			mCar = (Car) intent.getSerializableExtra("car");
		} else if (flag == FROM_CARMODEL) {
			mCurrentModelBean = (CarModelBean) getIntent().getSerializableExtra("car_model");
			mBrand_img = getIntent().getStringExtra("brand_img");
		}
		myProgressDialog = MyProgressDialog.createDialog(this);
		myProgressDialog.setMessage(getString(R.string.progress_msg));
		mClient = new AsyncHttpClient();
		initView();
		initData();
		initPopup();
	}

	private void initPopup() {
		mPopupWindow = new PopupWindow(this);
		mPopupWindow.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
		mPopupWindow.setWidth(LayoutParams.WRAP_CONTENT);
		mPopupWindow.setHeight(LayoutParams.WRAP_CONTENT);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setFocusable(false);
		mPopupWindow.setAnimationStyle(R.style.popup_translate_bottom_anim);
		ImageView imageView = new ImageView(this);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		imageView.setLayoutParams(params);
		imageView.setImageResource(R.drawable.legend);
		mPopupWindow.setContentView(imageView);
	}

	private void initData() {
		if (flag == FROM_CARMODEL && mCurrentModelBean != null) {
			StringBuffer buffer = new StringBuffer();
			buffer.append(mCurrentModelBean.brand_name).append(" ").append(mCurrentModelBean.category_name).append(" ").append(mCurrentModelBean.car_model_Name);
			car_brand_tv.setText(buffer.toString());
			mImageLoader.displayImage(mBrand_img, car_brand_img);
		} else if (flag == FROM_MYCAR && mCar != null) {
			StringBuffer buffer = new StringBuffer();
			buffer.append(mCar.brand).append(" ").append(mCar.category).append(" ").append(mCar.model);
			car_licence_tv.setText(mCar.licence.toString());
			car_brand_tv.setText(buffer.toString());
			mImageLoader.displayImage(mCar.brand_img_url.thumbnail_url, car_brand_img);
			province_tv.setText(mCar.licence.province);
			number_ed.setText(mCar.licence.number);
			String[] split = mCar.bought_time.split("T");
			if (split.length > 0)
				buy_year_tv.setText(split[0]);
			if(mCar.miles != null && mCar.miles >= 0)
				miles_ed.setText(String.valueOf(mCar.miles));
			chassis_number_ed.setText(mCar.chassis_number);
			engine_number_ed.setText(mCar.engine_number);
		}
	}

	private void initView() {
		findViewById(R.id.head_ll).setOnClickListener(mSoftInputOnClickListener);
		mBack_ll = (LinearLayout) findViewById(R.id.back_ll);
		mBack_tv = (TextView) findViewById(R.id.back_tv);
		mActionbar_title_tv = (TextView) findViewById(R.id.actionbar_title_tv);
		mClose_img = (ImageView) findViewById(R.id.close_img);
		province_tv = (TextView) findViewById(R.id.province_tv);
		number_ed = (EditText) findViewById(R.id.number_ed);
		buy_year_rl = (RelativeLayout) findViewById(R.id.buy_year_rl);
		buy_year_tv = (TextView) findViewById(R.id.buy_year_tv);
		miles_ed = (EditText) findViewById(R.id.miles_ed);
		chassis_number_ed = (EditText) findViewById(R.id.chassis_number_ed);
		engine_number_ed = (EditText) findViewById(R.id.engine_number_ed);
		chassis_img = (ImageView) findViewById(R.id.chassis_img);
		engine_img = (ImageView) findViewById(R.id.engine_img);
		car_setting_btn = (Button) findViewById(R.id.car_setting_btn);
		car_brand_img = (ImageView) findViewById(R.id.car_brand_img);
		car_licence_tv = (TextView) findViewById(R.id.car_licence_tv);
		car_brand_tv = (TextView) findViewById(R.id.car_brand_tv);

		mBack_ll.setVisibility(View.VISIBLE);
		mBack_ll.setOnClickListener(this);
		mClose_img.setOnClickListener(this);
		if (flag == FROM_MYCAR) {
			mBack_tv.setText("我的车");
			mActionbar_title_tv.setText("车信息");
		} else if (flag == FROM_CARMODEL) {
			mBack_tv.setText("具体型号");
			mActionbar_title_tv.setText("车信息");
		}
		province_tv.setOnClickListener(this);
		buy_year_rl.setOnClickListener(this);
		chassis_img.setOnClickListener(this);
		engine_img.setOnClickListener(this);
		car_setting_btn.setOnClickListener(this);
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
		case R.id.province_tv:
			showProvincePick();
			break;
		case R.id.buy_year_rl:
			boughtYear();
			break;
		case R.id.chassis_img:
		case R.id.engine_img:
			showLegend();
			break;
		case R.id.car_setting_btn:
			createCar();
			break;
		default:
			break;
		}
	}

	private void showProvincePick() {
		mProvincePopupWin = new PopupWindow(this);
		View view = getLayoutInflater().inflate(R.layout.pick_province_popup, null);
		GridView gridView = (GridView) view.findViewById(R.id.province_gridview);
		final MyListAdapter adapter = new MyListAdapter(this);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				province_tv.setText(adapter.getItem(position));
				mProvincePopupWin.dismiss();
			}
		});

		mProvincePopupWin.setWidth(LayoutParams.MATCH_PARENT);
		mProvincePopupWin.setHeight(LayoutParams.WRAP_CONTENT);
		mProvincePopupWin.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.grey_little)));
		mProvincePopupWin.setFocusable(true);
		mProvincePopupWin.setOutsideTouchable(true);
		mProvincePopupWin.setContentView(view);
		mProvincePopupWin.setAnimationStyle(R.style.popup_translate_bottom_anim);
		mProvincePopupWin.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
	}

	private void showLegend() {
		mPopupWindow.showAtLocation(parentView, Gravity.BOTTOM, 0, 200);
	}

	private void boughtYear() {
		Calendar calendar = Calendar.getInstance();
		String str = buy_year_tv.getText().toString().trim();
		if (!TextUtils.isEmpty(str)) {
			String[] split = str.split("-");
			calendar.set(Integer.parseInt(split[0]), Integer.parseInt(split[1]) - 1, Integer.parseInt(split[2]));
		}
		DatePickerDialog pickerDialog = new DatePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, new OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				StringBuffer date_buf = new StringBuffer();
				date_buf.append(year).append("-").append(monthOfYear + 1).append("-").append(dayOfMonth);
				buy_year_tv.setText(date_buf);
			}
		}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		pickerDialog.show();
	}

	private void createCar() {
		if (TextUtils.isEmpty(buy_year_tv.getText().toString().trim())) {
			MyToast.makeText(this, R.color.pay_color, "请填写购车时间", MyToast.LONGTIME).show();
			return;
		}
		try {
			Car car;
			if (flag == FROM_MYCAR) {
				car = mCar;
				car.model_type = mCar.model_type;
			} else {
				car = new Car();
				car.model_type = mCurrentModelBean.model_type;
			}
			car.user_id = App.mCarUser.user_id;
			car.licence = new Licence();
			car.licence.province = province_tv.getText().toString().trim();
			car.licence.number = number_ed.getText().toString().trim();
			StringBuffer year_buffer = new StringBuffer(buy_year_tv.getText().toString().trim());
			year_buffer.append("T").append("00:00:00.000");
			car.bought_time = year_buffer.toString();
			String miles_str = miles_ed.getText().toString().trim();
			car.miles = Integer.getInteger(miles_str);
			car.chassis_number = chassis_number_ed.getText().toString().trim();
			car.engine_number = engine_number_ed.getText().toString().trim();

			String car_str = new Gson().toJson(car);
			StringBuffer buffer = new StringBuffer();
			buffer.append("[").append(car_str).append("]");
			myProgressDialog.show();
			String suburl = CREATE_CAR_SUBURL;
			if(flag == FROM_MYCAR){
				suburl = UPDATE_CAR_SUBURL;
			}
			Utils.post(this, mClient, suburl, buffer.toString(), mHandler, CRETAE_CAR_WHAT, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void updateUserInfoJson(String userinfo_json) {
		mSP.edit().putString(SP_UserInfo, userinfo_json).commit();
		App.needNetDate = false;
		sendUpdateBroadcastReceiver();
		mHandler.sendEmptyMessage(PARSERJSON_SUCCESS);
	}

	private class MyListAdapter extends BaseAdapter{
		private Context mContext;
		private List<String> provinces;
		
		public MyListAdapter(Context context){
			mContext = context;
			this.provinces = initProvinces();
		}
		
		@Override
		public int getCount() {
			return provinces.size();
		}

		@Override
		public String getItem(int position) {
			return provinces.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView v = (TextView) LayoutInflater.from(mContext).inflate(R.layout.item_textview4gridview, null);
			String str = getItem(position);
			v.setText(str);
			if(str.equals(province_tv.getText().toString().trim())){
				v.setTextSize(mContext.getResources().getDimension(R.dimen.text_smallX));
			}
			return v;
		}
		
	}
	
	private List<String> initProvinces(){
		List<String> provinces = new ArrayList<String>();
		//京 津 冀 晋 蒙
		provinces.add("京");provinces.add("津");provinces.add("冀");provinces.add("晋");provinces.add("蒙");
		//皖 闽 赣 鲁 
		provinces.add("皖");provinces.add("沪");provinces.add("闽");provinces.add("赣");provinces.add("鲁");
		//渝 川 黔 滇 藏 
		provinces.add("渝");provinces.add("川");provinces.add("黔");provinces.add("滇");provinces.add("藏");
		//辽 吉 黑 苏 浙 
		provinces.add("辽");provinces.add("吉");provinces.add("黑");provinces.add("苏");provinces.add("浙");
		//鄂 湘 粤 桂 琼 
		provinces.add("鄂");provinces.add("湘");provinces.add("粤");provinces.add("桂");provinces.add("琼");
		//陕 甘 青 宁 新 
		provinces.add("陕");provinces.add("甘");provinces.add("青");provinces.add("宁");provinces.add("新");
		//台 港 澳
		provinces.add("台");provinces.add("港");provinces.add("澳");
		return provinces;
	}
	
}
