package com.yac.yacapp.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yac.yacapp.R;
import com.yac.yacapp.domain.CarCareBook;

public class MyCarCareAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private List<CarCareBook> mCarCareBooks;

	public MyCarCareAdapter(Context context, List<CarCareBook> mCarCareBooks) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		this.mCarCareBooks = mCarCareBooks;
	}
	
	public void updateData(List<CarCareBook> mCarCareBooks){
		this.mCarCareBooks = mCarCareBooks;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (mCarCareBooks.size() == 0) {
			return 1;
		} else {
			return mCarCareBooks.size();
		}
	}

	@Override
	public CarCareBook getItem(int position) {
		if (mCarCareBooks.size() == 0) {
			return null;
		} else {
			return mCarCareBooks.get(position);
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView != null) {
			holder = (ViewHolder) convertView.getTag();
		} else {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_carcare_book, null);
			holder.item_carcare_book_ll = (LinearLayout) convertView.findViewById(R.id.item_carcare_book_ll);
			holder.carcare_miles_tv = (TextView) convertView.findViewById(R.id.carcare_miles_tv);
			holder.point_img = (ImageView) convertView.findViewById(R.id.point_img);
			holder.carcare_books_ll = (LinearLayout) convertView.findViewById(R.id.carcare_books_ll);
			convertView.setTag(holder);
		}
		if (mCarCareBooks.size() == 0) {
			// 这样做的目的在于，当listview为空的时候，也加载一个item，然后再到下一页时，就不会把viewpager顶上去
			holder.item_carcare_book_ll.setVisibility(View.INVISIBLE);
			return convertView;
		}
		holder.item_carcare_book_ll.setVisibility(View.VISIBLE);
		CarCareBook carCareBook = getItem(position);
		holder.carcare_miles_tv.setText(String.valueOf(carCareBook.miles));
		if (position == 0) {
			holder.point_img.setImageResource(R.drawable.point_orange);
		} else {
			holder.point_img.setImageResource(R.drawable.point_grey);
		}
		holder.carcare_books_ll.removeAllViews();
		for (int i = 0; i < carCareBook.events.size(); i++) {
			View view = mInflater.inflate(R.layout.item_carcare, null);
			TextView textView = (TextView) view.findViewById(R.id.index_tv);
			textView.setText(String.valueOf(i + 1));
			TextView textView2 = (TextView) view.findViewById(R.id.carcare_name_tv);
			textView2.setText(carCareBook.events.get(i));
			if (position == 0) {
				ImageView index_bg_img = (ImageView) view.findViewById(R.id.index_bg_img);
				index_bg_img.setImageResource(R.drawable.point_orange);
				textView2.setTextColor(mContext.getResources().getColor(R.color.black));
			}
			holder.carcare_books_ll.addView(view);
		}
		// 动态添加的linearlayout，可以帮助listview计算本item的高度
		android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 4);
		holder.carcare_books_ll.setLayoutParams(params);

		return convertView;
	}

	class ViewHolder {
		TextView carcare_miles_tv;
		ImageView point_img;
		LinearLayout item_carcare_book_ll, carcare_books_ll;
	}
}
