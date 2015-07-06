package com.yac.yacapp.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yac.yacapp.R;
import com.yac.yacapp.activities.MyOrderRatingActivity;
import com.yac.yacapp.activities.OrderServiceFlowFragmentActivity;
import com.yac.yacapp.domain.Order;
import com.yac.yacapp.domain.OrderProduct;
import com.yac.yacapp.icounts.ICounts;
import com.yac.yacapp.utils.Utils2;

public class MyOrderListviewAdapter extends BaseAdapter implements ICounts{
	private Context context;
	private LayoutInflater inflater;
	private List<Order> orders;

	public MyOrderListviewAdapter(Context context, List<Order> orders) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.orders = orders;
	}

	public void updateData(List<Order> orders) {
		this.orders = orders;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return orders.size();
	}

	@Override
	public Order getItem(int position) {
		return orders.get(position);
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
			view  = inflater.inflate(R.layout.item_order_listview, null);
			holder.car_licence_tv = (TextView) view.findViewById(R.id.car_licence_tv);
			holder.order_status_tv = (TextView) view.findViewById(R.id.order_status_tv);
			holder.server_time_tv = (TextView) view.findViewById(R.id.server_time_tv);
			holder.server_item_tv = (TextView) view.findViewById(R.id.server_item_tv);
			holder.right_arrow_img = (ImageView) view.findViewById(R.id.right_arrow_img);
			holder.check_server_tv = (TextView) view.findViewById(R.id.check_server_tv);
			holder.comment_tv = (TextView) view.findViewById(R.id.comment_tv);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		final Order order = getItem(position);
		holder.car_licence_tv.setText(order.car.licence.toString());
//		if(order.pay_mode == 1){
		if(order.pay_status == 1){
			holder.order_status_tv.setText("已支付");
		}else if(order.pay_status == 2){
			if(order.paid){
				holder.order_status_tv.setText("已支付");
			}else{
				holder.order_status_tv.setText("未支付");
			}
		}else if(order.pay_status == 3){
			holder.order_status_tv.setText("部分支付");
		}
//		}else if(order.pay_mode == 2){
//			holder.order_status_tv.setText("线下支付");
//		}
		if(debug)System.out.println("order.start_time:"+order.start_time);
		if(!TextUtils.isEmpty(order.start_time)){
			String[] fromUTC = Utils2.getDateFromUTC(order.start_time);
			if(fromUTC.length == 2){
				holder.server_time_tv.setText(fromUTC[1]);
			}
		}
		StringBuffer buffer = new StringBuffer();
		for(OrderProduct product: order.products){
			buffer.append(product.product_name).append(" ");
		}
		holder.server_item_tv.setText(buffer.toString());
		holder.check_server_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, /*OrderServiceFlowActivity*/OrderServiceFlowFragmentActivity.class);
				intent.putExtra("order_id", order.id);
				context.startActivity(intent);
			}
		});
		if(order.pay_status == 1 || order.pay_status == 3 || (order.pay_status == 2 && order.paid)){
			holder.comment_tv.setEnabled(true);
			holder.comment_tv.setTextColor(context.getResources().getColor(R.color.yac_green));
			holder.comment_tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, MyOrderRatingActivity.class);
					intent.putExtra("order", order);
					context.startActivity(intent);
				}
			});
		}else if(order.pay_status == 2 && !order.paid){
			holder.comment_tv.setEnabled(false);
			holder.comment_tv.setTextColor(context.getResources().getColor(R.color.grey));
			holder.comment_tv.setOnClickListener(null);
		}
		return view;
	}

	class ViewHolder {
		TextView car_licence_tv, order_status_tv, server_time_tv, server_item_tv, check_server_tv, comment_tv;
		ImageView right_arrow_img;
	}
}