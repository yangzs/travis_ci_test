package com.yac.yacapp.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.yac.yacapp.R;
import com.yac.yacapp.activities.CarPartUpdateDataActivity;
import com.yac.yacapp.domain.Part;
import com.yac.yacapp.utils.Utils2;
import com.yac.yacapp.views.RoundProgressBar;

public class CarPartsDetailsAdapter extends BaseAdapter {

	private Activity mContext;
	private LayoutInflater mInflater;
	private Resources mResources;
	private List<Part> mParts;
	private Map<Integer, Integer> partsImgResIds;
	@SuppressLint("UseSparseArrays")
	private void initMap() {
		if (partsImgResIds == null) {
			partsImgResIds = new HashMap<Integer, Integer>();
			partsImgResIds.put(1, R.drawable.home_tire);
			partsImgResIds.put(2, R.drawable.home_tire);
			partsImgResIds.put(3, R.drawable.home_tire);
			partsImgResIds.put(4, R.drawable.home_tire);
			partsImgResIds.put(5, R.drawable.home_brake_pads);
			partsImgResIds.put(6, R.drawable.home_brake_pads);
			partsImgResIds.put(7, R.drawable.home_wiper);
			partsImgResIds.put(8, R.drawable.home_wiper);
			partsImgResIds.put(9, R.drawable.engine_oil);
			partsImgResIds.put(10, R.drawable.home_engine_oil);
			partsImgResIds.put(11, R.drawable.home_engine_oil);
			partsImgResIds.put(12, R.drawable.home_engine_oil);
			partsImgResIds.put(13, R.drawable.home_transmission_oil);
			partsImgResIds.put(14, R.drawable.home_fuel_oil);
			partsImgResIds.put(15, R.drawable.home_brake_fluid);
			partsImgResIds.put(16, R.drawable.home_battery);
			partsImgResIds.put(17, R.drawable.home_timing_belt);
			partsImgResIds.put(18, R.drawable.home_engine_oil);
			partsImgResIds.put(19, R.drawable.home_spark_plug);
		}
	}
	public CarPartsDetailsAdapter(Activity context, List<Part> parts){
		this.mContext = context;
		this.mInflater = LayoutInflater.from(mContext);
		mResources = mContext.getResources();
		this.mParts = parts;
		initMap();
	}
	
	public void updateData(List<Part> parts){
		this.mParts = parts;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return mParts.size();
	}

	@Override
	public Part getItem(int position) {
		return mParts.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		View view = convertView;
		if(view == null){
			holder = new ViewHolder();
			view = mInflater.inflate(R.layout.item_carpart_details, null);
			holder.car_part_img = (ImageView) view.findViewById(R.id.car_part_img);
			holder.car_part_tv = (TextView) view.findViewById(R.id.car_part_tv);
			holder.car_part_status_tv = (TextView) view.findViewById(R.id.car_part_status_tv);
			holder.car_part_used_time_tv = (TextView) view.findViewById(R.id.car_part_used_time_tv);
			holder.car_part_uses_miles_tv = (TextView) view.findViewById(R.id.car_part_uses_miles_tv);
			holder.car_part_rpb = (RoundProgressBar) view.findViewById(R.id.car_part_rpb);
			holder.update_data_btn = (Button) view.findViewById(R.id.update_data_btn);
			holder.car_part_remark_tv = (TextView) view.findViewById(R.id.car_part_remark_tv);
			
			view.setTag(holder);
		}else{
			holder = (ViewHolder) view.getTag();
		}
		final Part part = getItem(position);
		holder.car_part_img.setImageResource(partsImgResIds.get(part.type));
		holder.car_part_tv.setText(part.name);
		int months = Utils2.getMonths(part.last_update_time);
		holder.car_part_used_time_tv.setText(String.valueOf(months));
		holder.car_part_uses_miles_tv.setText(String.valueOf(part.used_miles));
		if(part.score <= 30){
			holder.car_part_status_tv.setTextColor(mResources.getColor(R.color.carpart_red));
			holder.car_part_rpb.setCricleProgressColor(mResources.getColor(R.color.carpart_red));
		}else if(part.score <= 60){
			holder.car_part_status_tv.setTextColor(mResources.getColor(R.color.carpart_yellow));
			holder.car_part_rpb.setCricleProgressColor(mResources.getColor(R.color.carpart_yellow));
		}else if(part.score <= 75){
			holder.car_part_status_tv.setTextColor(mResources.getColor(R.color.carpart_blue));
			holder.car_part_rpb.setCricleProgressColor(mResources.getColor(R.color.carpart_blue));
		}else{
			holder.car_part_status_tv.setTextColor(mResources.getColor(R.color.carpart_green));
			holder.car_part_rpb.setCricleProgressColor(mResources.getColor(R.color.carpart_green));
		}
		holder.car_part_status_tv.setText(part.status);
		holder.car_part_remark_tv.setText(part.comment);
		new Thread(new Runnable() {
			@Override
			public void run() {
				int progress = 0;
				while (progress <= part.score) {
					holder.car_part_rpb.setProgress(progress);
					try {
						Thread.sleep(8);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					progress++;
				}
			}
		}).start();
		holder.update_data_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, CarPartUpdateDataActivity.class);
				intent.putExtra("part", part);
				mContext.startActivityForResult(intent, 1);
			}
		});
		return view;
	}

	class ViewHolder{
		ImageView car_part_img;
		TextView car_part_tv, car_part_status_tv, car_part_used_time_tv, car_part_uses_miles_tv, car_part_remark_tv;
		RoundProgressBar car_part_rpb;
		Button update_data_btn;
	}
}
