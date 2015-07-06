package com.yac.yacapp.activities;

import java.util.ArrayList;
import java.util.List;

import com.yac.yacapp.R;
import com.yac.yacapp.adapter.CarPartsDetailsAdapter;
import com.yac.yacapp.domain.Car;
import com.yac.yacapp.domain.Part;
import com.yac.yacapp.icounts.App;
import com.yac.yacapp.icounts.BaseActivity;
import com.yac.yacapp.icounts.ICounts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class CarPartsDetailsActivity extends BaseActivity implements ICounts, OnClickListener {

	private LinearLayout back_ll;
	private TextView back_tv, actionbar_title_tv;
	private ImageView mClose_img;
	private ListView carParts_listview;
	private Button book_now_btn;

	private List<Part> mParts;
	private CarPartsDetailsAdapter partsDetailsAdapter;
	private Car mCar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_carparts_details);
		initData();
		initView();
	}

	private void initData() {
		mCar = App.mCurrentCar;
		if (mCar != null) {
			mParts = mCar.parts;
		}
	}

	private void initView() {
		back_ll = (LinearLayout) findViewById(R.id.back_ll);
		back_tv = (TextView) findViewById(R.id.back_tv);
		actionbar_title_tv = (TextView) findViewById(R.id.actionbar_title_tv);
		mClose_img = (ImageView) findViewById(R.id.close_img);
		carParts_listview = (ListView) findViewById(R.id.order_listview);
		book_now_btn = (Button) findViewById(R.id.book_now_btn);

		back_ll.setVisibility(View.VISIBLE);
		back_ll.setOnClickListener(this);
		back_tv.setText("车辆");
		actionbar_title_tv.setText("部件详情");
		mClose_img.setOnClickListener(this);
		if(mParts == null)
				mParts = new ArrayList<Part>();
		partsDetailsAdapter = new CarPartsDetailsAdapter(this, mParts);
		carParts_listview.setAdapter(partsDetailsAdapter);
		book_now_btn.setOnClickListener(this);
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
		case R.id.book_now_btn:
			Intent intent = new Intent(this, SixServiceActivity.class);
			intent.addFlags(FROM_CARPARTS_DETAILS_SERVICE);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK && requestCode == 1){
			mCar = (Car) data.getSerializableExtra("Car");
			if(mCar != null){
				mParts = mCar.parts;
				partsDetailsAdapter.updateData(mParts);
			}
		}
	}

}
