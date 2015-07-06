package com.yac.yacapp.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yac.yacapp.R;
import com.yac.yacapp.domain.OrderServiceFlow;
import com.yac.yacapp.domain.OrderServiceFlowItem;
import com.yac.yacapp.domain.Picture;
import com.yac.yacapp.utils.Utils2;
import com.yac.yacapp.views.LinearLayoutForListView;

public class MyServiceFlowAdapter extends BaseAdapter {

	private Context context;
	private Resources resources;
	private List<OrderServiceFlow> mServiceFlows;
	private LayoutInflater mInflater;

	public MyServiceFlowAdapter(Context context, List<OrderServiceFlow> flows) {
		this.context = context;
		resources = context.getResources();
		mInflater = LayoutInflater.from(context);
		this.mServiceFlows = flows;
	}

	public void updateData(List<OrderServiceFlow> flows) {
		this.mServiceFlows = flows;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mServiceFlows.size();
	}

	@Override
	public OrderServiceFlow getItem(int position) {
		return mServiceFlows.get(position);
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
			view = mInflater.inflate(R.layout.item_order_serviceflow, null);
			holder.time_tv = (TextView) view.findViewById(R.id.time_tv);
			holder.data_tv = (TextView) view.findViewById(R.id.data_tv);
			holder.service_name_tv = (TextView) view.findViewById(R.id.service_name_tv);
			holder.point_img = (ImageView) view.findViewById(R.id.point_img);
			holder.up_line_view = view.findViewById(R.id.up_line_view);
			holder.down_line_view = view.findViewById(R.id.down_line_view);
			holder.adds_items_ll = (LinearLayoutForListView) view.findViewById(R.id.adds_items_ll);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		OrderServiceFlow serviceFlow = getItem(position);
		if (!TextUtils.isEmpty(serviceFlow.time)) {
			String[] times = Utils2.getDateFromUTC(serviceFlow.time);
			holder.time_tv.setText(times[0]);
			holder.data_tv.setText(times[1]);
		}
		holder.up_line_view.setVisibility(View.VISIBLE);
		holder.down_line_view.setVisibility(View.VISIBLE);
		if(position == 0){
			holder.up_line_view.setVisibility(View.INVISIBLE);
		}
		if(position == getCount()-1){
			holder.down_line_view.setVisibility(View.INVISIBLE);
		}
		if (serviceFlow.status == -1) {//没有完成
			holder.point_img.setImageResource(R.drawable.gender2);
			holder.up_line_view.setBackgroundColor(resources.getColor(R.color.grey));
			holder.down_line_view.setBackgroundColor(resources.getColor(R.color.grey));
		} else if (serviceFlow.status == 0) {//已经完成
			holder.point_img.setImageResource(R.drawable.dot01);
			holder.up_line_view.setBackgroundColor(resources.getColor(R.color.green_text));
			holder.down_line_view.setBackgroundColor(resources.getColor(R.color.green_text));
		} else if (serviceFlow.status == 1) {//正在处理
			holder.point_img.setImageResource(R.drawable.current_position);
			holder.up_line_view.setBackgroundColor(resources.getColor(R.color.green_text));
			holder.down_line_view.setBackgroundColor(resources.getColor(R.color.grey));
		}
		holder.service_name_tv.setText(serviceFlow.name);
		if (serviceFlow.items != null && serviceFlow.items.size() > 0) {
			holder.adds_items_ll.setAdapter(new MyPictureItemAdapter(context, getPictures(serviceFlow)));
		}else{
			holder.adds_items_ll.setVisibility(View.GONE);
		}
		return view;
	}
	
	private List<Picture> getPictures(OrderServiceFlow serviceFlow){
		List<Picture> pictures = new ArrayList<Picture>();
		for (OrderServiceFlowItem item : serviceFlow.items) {
			pictures.addAll(item.pics);
			if(pictures.size() > 4)
				break;
		}
		return pictures;
	}

	class ViewHolder {
		TextView time_tv, data_tv, service_name_tv;
		ImageView point_img;
		View up_line_view,down_line_view;
		LinearLayoutForListView adds_items_ll;
	}

}
