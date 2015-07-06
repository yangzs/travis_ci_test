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
import com.yac.yacapp.domain.Report;
import com.yac.yacapp.views.LinearLayoutForListView;

public class UnQualifiedReportsAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private List<Report> reports;

	public UnQualifiedReportsAdapter(Context context, List<Report> reports) {
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
			view = mInflater.inflate(R.layout.item_unqualified_report, null);
			holder.report_name_tv = (TextView) view.findViewById(R.id.report_name_tv);
			holder.report_comment_tv = (TextView) view.findViewById(R.id.report_comment_tv);
			holder.adds_items_ll = (LinearLayoutForListView) view.findViewById(R.id.adds_items_ll);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		Report report = getItem(position);
		holder.report_name_tv.setText(mContext.getString(R.string.report_name, position+1, report.name));
		if(TextUtils.isEmpty(report.status_comment)){
			holder.report_comment_tv.setVisibility(View.GONE);
		}else{
			holder.report_comment_tv.setText(report.status_comment);
		}
		if(report.imgs != null && report.imgs.size() > 0){
			holder.adds_items_ll.setVisibility(View.VISIBLE);
			holder.adds_items_ll.setAdapter(new MyPictureItemAdapter(mContext, report.imgs));
		}else{
			holder.adds_items_ll.setVisibility(View.GONE);
		}
		return view;
	}

	static class ViewHolder {
		TextView report_name_tv, report_comment_tv;
		LinearLayoutForListView adds_items_ll;
	}
}
