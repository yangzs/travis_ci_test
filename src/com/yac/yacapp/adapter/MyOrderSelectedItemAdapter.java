package com.yac.yacapp.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.yac.yacapp.R;
import com.yac.yacapp.domain.OrderProduct;

public class MyOrderSelectedItemAdapter extends BaseAdapter {

	private Context mContext;
	private List<OrderProduct> mProducts;
	private LayoutInflater mInflater;
	
	public MyOrderSelectedItemAdapter(Context context, List<OrderProduct> products){
		this.mContext = context;
		this.mProducts = products;
		mInflater = LayoutInflater.from(mContext);
	}
	
	public void updateData(List<OrderProduct> products){
		this.mProducts = products;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return mProducts.size();
	}

	@Override
	public OrderProduct getItem(int position) {
		return mProducts.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		View view = convertView;
		if(view == null){
			holder = new ViewHolder();
			view = mInflater.inflate(R.layout.item_myorder_item, null);
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			params.setMargins(0, 20, 0, 20);
			view.setLayoutParams(params);
			holder.item_selected_img = (ImageView) view.findViewById(R.id.item_selected_img);
			holder.item_selected_img.setVisibility(View.VISIBLE);
			holder.item_product_name_tv = (TextView) view.findViewById(R.id.item_product_name_tv);
			holder.item_product_number_tv = (TextView) view.findViewById(R.id.item_product_number_tv);
			holder.item_product_price_tv = (TextView) view.findViewById(R.id.item_product_price_tv);
			view.setTag(holder);
		}else{
			holder = (ViewHolder) view.getTag();
		}
		OrderProduct product = getItem(position);
		if(product.selection_mode == 3){
			holder.item_selected_img.setImageResource(R.drawable.selected);
		}else if(product.selection_mode == 2 || product.selection_mode == 4){
			holder.item_selected_img.setImageResource(R.drawable.not_selected);
		}
		holder.item_product_name_tv.setText(product.product_name);
		holder.item_product_number_tv.setText(mContext.getString(R.string.product_num, product.unit_count));
		holder.item_product_price_tv.setText("￥"+String.valueOf(product.price));
		return view;
	}
	
	class ViewHolder{
		ImageView item_selected_img;
		TextView item_product_name_tv, item_product_number_tv, item_product_price_tv;
	}

}
