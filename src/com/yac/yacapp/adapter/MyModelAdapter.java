package com.yac.yacapp.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yac.yacapp.R;
import com.yac.yacapp.domain.CarModelBean;

public class MyModelAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private List<CarModelBean> categoryBeans;

	public MyModelAdapter(Context context, List<CarModelBean> categoryBeans) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		this.categoryBeans = categoryBeans;
	}

	public void updateData(List<CarModelBean> categoryBeans) {
		this.categoryBeans = categoryBeans;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return categoryBeans.size();
	}

	@Override
	public CarModelBean getItem(int position) {
		return categoryBeans.get(position);
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
			view = mInflater.inflate(R.layout.item_textview, null);
			holder.textview = (TextView) view.findViewById(R.id.item_textview);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		CarModelBean item = getItem(position);
		StringBuffer buffer = new StringBuffer();
		buffer.append(item.brand_name).append(" ").append(item.production_year).append(" ").append(item.engine_displacement).append(" ").append(item.car_model_Name);
		holder.textview.setText(buffer.toString());
		return view;
	}

	static class ViewHolder {
		TextView textview;
	}
}
