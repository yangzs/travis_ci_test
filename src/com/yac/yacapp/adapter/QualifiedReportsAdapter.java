package com.yac.yacapp.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yac.yacapp.R;
import com.yac.yacapp.domain.Report;

public class QualifiedReportsAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private List<Report> reports;

	public QualifiedReportsAdapter(Context context, List<Report> reports) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		this.reports = reports;
	}

	public void updateData(List<Report> reports) {
		this.reports = reports;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return reports.size();
	}

	@Override
	public Report getItem(int position) {
		return reports.get(position);
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
			view = mInflater.inflate(R.layout.item_qualified_report, null);
			holder.report_name_tv = (TextView) view.findViewById(R.id.report_name_tv);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		Report report = getItem(position);
		holder.report_name_tv.setText(mContext.getString(R.string.report_name, position+1, report.name));
//		holder.report_status_tv.setText();
		return view;
	}

	static class ViewHolder {
		TextView report_name_tv, report_status_tv;
	}
}
