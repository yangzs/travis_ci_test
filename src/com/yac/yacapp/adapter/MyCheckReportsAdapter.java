package com.yac.yacapp.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yac.yacapp.R;
import com.yac.yacapp.domain.CheckReport;

public class MyCheckReportsAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private List<CheckReport> checkReports;

	public MyCheckReportsAdapter(Context context, List<CheckReport> checkReports) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		this.checkReports = checkReports;
	}

	public void updateData(List<CheckReport> checkReports) {
		this.checkReports = checkReports;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return checkReports.size();
	}

	@Override
	public CheckReport getItem(int position) {
		return checkReports.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		View view = convertView;
		if (view == null) {
			holder = new ViewHolder();
			view = mInflater.inflate(R.layout.item_check_report, null);
			holder.reprot_time_tv = (TextView) view.findViewById(R.id.reprot_time_tv);
			holder.reprot_name_tv = (TextView) view.findViewById(R.id.reprot_name_tv);
			holder.car_brand_tv = (TextView) view.findViewById(R.id.car_brand_tv);
			holder.car_model_tv = (TextView) view.findViewById(R.id.car_model_tv);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		CheckReport checkReport = getItem(position);
		if(!TextUtils.isEmpty(checkReport.complete_time)){
			String[] split = checkReport.complete_time.split("T");
			holder.reprot_time_tv.setText(split[0]);
		}
		holder.reprot_name_tv.setText(checkReport.name);
		holder.car_brand_tv.setText(checkReport.car.brand+" "+checkReport.car.category);
		holder.car_model_tv.setText(checkReport.car.model);
		return view;
	}

	static class ViewHolder {
		TextView reprot_time_tv, reprot_name_tv, car_brand_tv, car_model_tv;
	}
}
