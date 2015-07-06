package com.yac.yacapp.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yac.yacapp.R;
import com.yac.yacapp.domain.Part;
import com.yac.yacapp.views.RoundProgressBar;

public class MyPartsListViewAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private Resources mResources;
	private List<Part> mParts;
	private Map<Integer, Integer> partsImgResIds;

	public MyPartsListViewAdapter(Context context, List<Part> mParts) {
		mInflater = LayoutInflater.from(context);
		mResources = context.getResources();
		this.mParts = mParts;
		initMap();
	}
	
	public void updateData(List<Part> mParts){
		this.mParts = mParts;
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
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.listview_item_carpart, null);
			holder.car_part_img = (ImageView) convertView.findViewById(R.id.car_part_img);
			holder.roundProgressBar = (RoundProgressBar) convertView.findViewById(R.id.roundProgressBar);
			holder.car_part_name_tv = (TextView) convertView.findViewById(R.id.car_part_name_tv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final Part part = getItem(position);
		holder.car_part_name_tv.setText(part.name);
		if (part.score <= 30) {
			holder.roundProgressBar.setCricleProgressColor(mResources.getColor(R.color.carpart_red));
		} else if (part.score <= 60) {
			holder.roundProgressBar.setCricleProgressColor(mResources.getColor(R.color.carpart_yellow));
		} else if (part.score <= 75) {
			holder.roundProgressBar.setCricleProgressColor(mResources.getColor(R.color.carpart_blue));
		} else {
			holder.roundProgressBar.setCricleProgressColor(mResources.getColor(R.color.carpart_green));
		}
		//holder.roundProgressBar.setProgress(part.score);
		new Thread(new Runnable() {
			int progress = 0;
			@Override
			public void run() {
				while (progress <= part.score) {
					holder.roundProgressBar.setProgress(progress);
					try {
						Thread.sleep(8);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					progress++;
				}
			}
		}).start();
		holder.car_part_img.setImageResource(partsImgResIds.get(part.type));

		return convertView;
	}

	class ViewHolder {
		ImageView car_part_img;
		RoundProgressBar roundProgressBar;
		TextView car_part_name_tv;
	}
	
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
		/*
		 * "type": 1, "name": "左前轮胎", "type": 2, "name": "右前轮胎", "type": 3,
		 * "name": "左后轮胎", "type": 4, "name": "右后轮胎", "type": 5, "name": "前刹车片",
		 * "type": 6, "name": "后刹车片", "type": 7, "name": "雨刮片", "type": 8,
		 * "name": "雨刮器", "type": 9, "name": "机油", "type": 10, "name": "机滤",
		 * "type": 11, "name": "空气滤", "type": 12, "name": "汽滤", "type": 13,
		 * "name": "变速箱油", "type": 14, "name": "转向助力液", "type": 15, "name":
		 * "制动液", "type": 16, "name": "电瓶", "type": 17, "name": "正时皮带", "type":
		 * 18, "name": "空滤", "type": 19, "name": "火花塞",
		 */
	}

}
