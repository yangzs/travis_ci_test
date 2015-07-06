package com.yac.yacapp.activities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.yac.yacapp.R;
import com.yac.yacapp.domain.Car;
import com.yac.yacapp.domain.Part;
import com.yac.yacapp.icounts.App;
import com.yac.yacapp.icounts.BaseActivity;
import com.yac.yacapp.icounts.ICounts;
import com.yac.yacapp.utils.Utils;
import com.yac.yacapp.views.MyProgressDialog;

public class CarPartUpdateDataActivity extends BaseActivity implements ICounts, OnClickListener {

	private static final int UPDATE_CARPART_WHAT = 0;
	private LinearLayout back_ll;
	private TextView back_tv, actionbar_title_tv;
	private ImageView mClose_img;
	private EditText miles_et;
	private TextView datetime_et;
	private Button save_update_btn;

	private Part mPart;
	private AsyncHttpClient mClient;
	private MyProgressDialog myProgressDialog;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case UPDATE_CARPART_WHAT:
				String content = (String) msg.obj;
				parserJson(content);
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
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mClient = new AsyncHttpClient();
		myProgressDialog = MyProgressDialog.createDialog(this);
		myProgressDialog.setMessage(getString(R.string.progress_msg));
		setContentView(R.layout.activity_carpart_update);
		initView();
		initDate();
	}

	private void initDate() {
		mPart = (Part) getIntent().getSerializableExtra("part");
		miles_et.setText(String.valueOf(mPart.used_miles));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		try {
			Date dt = sdf.parse(mPart.last_update_time);
			Calendar calendar = sdf.getCalendar();
			String date_str = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH)+1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
			datetime_et.setText(date_str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private void initView() {
		findViewById(R.id.head_ll).setOnClickListener(mSoftInputOnClickListener);
		back_ll = (LinearLayout) findViewById(R.id.back_ll);
		back_tv = (TextView) findViewById(R.id.back_tv);
		actionbar_title_tv = (TextView) findViewById(R.id.actionbar_title_tv);
		mClose_img = (ImageView) findViewById(R.id.close_img);
		miles_et = (EditText) findViewById(R.id.miles_et);
		datetime_et = (TextView) findViewById(R.id.datetime_et);
		save_update_btn = (Button) findViewById(R.id.save_update_btn);

		back_ll.setVisibility(View.VISIBLE);
		back_ll.setOnClickListener(this);
		back_tv.setText("部件详情");
		actionbar_title_tv.setText("更新部件");
		mClose_img.setOnClickListener(this);
		datetime_et.setOnClickListener(this);
		save_update_btn.setOnClickListener(this);
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
		case R.id.datetime_et:
			showDataPick();
			break;
		case R.id.save_update_btn:
			saveUpdate();
			break;
		default:
			break;
		}
	}

	private void showDataPick() {
		Calendar calendar = Calendar.getInstance();
		String str = datetime_et.getText().toString().trim();
		if (!TextUtils.isEmpty(str)) {
			String[] split = str.split("-");
			calendar.set(Integer.parseInt(split[0]), Integer.parseInt(split[1]) - 1, Integer.parseInt(split[2]));
		}
		DatePickerDialog pickerDialog = new DatePickerDialog(this, new OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				StringBuffer date_buf = new StringBuffer();
				date_buf.append(year).append("-").append(monthOfYear + 1).append("-").append(dayOfMonth);
				datetime_et.setText(date_buf);
			}
		}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		pickerDialog.show();
	}

	private void saveUpdate() {
		myProgressDialog.show();
		A a = new A();
		a.id = App.mCurrentCar.id;
		a.parts = new ArrayList<Part>();
		mPart.used_miles = Integer.parseInt(miles_et.getText().toString().trim());
		StringBuffer year_buffer = new StringBuffer(datetime_et.getText().toString().trim());
		year_buffer.append("T").append("00:00:00.000");
		mPart.last_update_time = year_buffer.toString();
		a.parts.add(mPart);
		String json = new Gson().toJson(a);
		StringBuffer buffer = new StringBuffer();
		buffer.append("[").append(json).append("]");
		if (debug)
			System.out.println("buffer.tostring:" + buffer.toString());
		Utils.post(this, mClient, UPDATE_CAR_SUBURL, buffer.toString(), mHandler, UPDATE_CARPART_WHAT, true);
	}

	class A {
		Long id;
		List<Part> parts;
	}

	protected void parserJson(String content) {
		if (debug) System.out.println("content:" + content);
		if (!TextUtils.isEmpty(content)) {
			JSONArray jsonArray;
			try {
				jsonArray = new JSONArray(content);
				if (debug)
					System.out.println("jsonArray.length:" + jsonArray.length() + ",content:" + content);
				if (jsonArray.length() == 1) {
					String str = jsonArray.getString(0);
					Car car = new Gson().fromJson(str, Car.class);
					if (car != null) {
						Intent intent = new Intent();
						intent.putExtra("Car", car);
						setResult(RESULT_OK, intent);
						App.needNetDate = true;
						sendUpdateBroadcastReceiver();
						mHandler.sendEmptyMessage(PARSERJSON_SUCCESS);
					} else {
						mHandler.sendEmptyMessage(PARSERJSON_FAILURE);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
				mHandler.sendEmptyMessage(PARSERJSON_FAILURE);
			}
		}
	}
}
