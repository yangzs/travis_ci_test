package com.yac.yacapp.activities;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.yac.yacapp.R;
import com.yac.yacapp.domain.Order;
import com.yac.yacapp.domain.OrderKeeperBasic;
import com.yac.yacapp.icounts.BaseActivity;
import com.yac.yacapp.icounts.ICounts;

public class MyOrderKeepersActivity extends BaseActivity implements ICounts, OnClickListener {

	private LinearLayout back_ll;
	private TextView back_tv, actionbar_title_tv;
	private ImageView mClose_img;
	private ListView keepers_listview;
	private Order mOrder;
	private List<MateItem> mateItems;
	private MyKeeperItemAdapter mAdapter;
	private DisplayImageOptions options;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myorder_keepers);
		mOrder = (Order) getIntent().getSerializableExtra("order");
		options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.default_avatar).showImageOnFail(R.drawable.default_avatar).build();

		initView();
		initData();
	}

	private void initData() {
		mateItems = new ArrayList<MyOrderKeepersActivity.MateItem>();
		if (mOrder != null && mOrder.keeper_basics != null && mOrder.keeper_basics.size() > 0) {
			for (OrderKeeperBasic keeperBasic : mOrder.keeper_basics) {
				MateItem mateItem = new MateItem();
				mateItem.id = keeperBasic.id;
				mateItem.avatar_img = keeperBasic.avatar_img;
				mateItem.ID_number = keeperBasic.ID_number;
				mateItem.name = keeperBasic.name;
				mateItem.phone_number = keeperBasic.phone_number;
				mateItem.star_count = keeperBasic.star_count;
				mateItem.type = keeperBasic.type;
				mateItems.add(mateItem);
			}
		}
		if (mOrder != null && mOrder.operator != null) {
			MateItem mateItem = new MateItem();
			mateItem.id = mOrder.operator.id;
			mateItem.avatar_img = mOrder.operator.avatar_img;
			mateItem.name = mOrder.operator.name;
			mateItem.phone_number = mOrder.operator.phone_number;
			mateItem.type = mOrder.operator.type;
			mateItems.add(mateItem);
		}
		mAdapter = new MyKeeperItemAdapter(this, mateItems);
		keepers_listview.setAdapter(mAdapter);
	}

	private void initView() {
		back_ll = (LinearLayout) findViewById(R.id.back_ll);
		back_tv = (TextView) findViewById(R.id.back_tv);
		actionbar_title_tv = (TextView) findViewById(R.id.actionbar_title_tv);
		mClose_img = (ImageView) findViewById(R.id.close_img);
		keepers_listview = (ListView) findViewById(R.id.keepers_listview);

		back_ll.setVisibility(View.VISIBLE);
		back_ll.setOnClickListener(this);
		back_tv.setText("订单详情");
		actionbar_title_tv.setText("管家技师详细");
		mClose_img.setOnClickListener(this);
	}

	private class MyKeeperItemAdapter extends BaseAdapter {

		private Context mContext;
		List<MateItem> mateItems;
		private LayoutInflater mInflater;

		public MyKeeperItemAdapter(Context context, List<MateItem> mateItems) {
			this.mContext = context;
			this.mateItems = mateItems;
			mInflater = LayoutInflater.from(mContext);
		}

		public void updateData(List<MateItem> mateItems) {
			this.mateItems = mateItems;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mateItems.size();
		}

		@Override
		public MateItem getItem(int position) {
			return mateItems.get(position);
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
				view = mInflater.inflate(R.layout.item_myorder_keeper, null);
				holder.keeper_img = (ImageView) view.findViewById(R.id.keeper_img);
				holder.text1 = (TextView) view.findViewById(R.id.text1);
				holder.keeper_name = (TextView) view.findViewById(R.id.keeper_name);
				holder.keeper_rating_tv = (TextView) view.findViewById(R.id.keeper_rating_tv);
				holder.keeper_phone_tv = (TextView) view.findViewById(R.id.keeper_phone_tv);
				holder.keeper_avator_tv = (TextView) view.findViewById(R.id.keeper_avator_tv);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			MateItem mateItem = getItem(position);
			mImageLoader.displayImage(mateItem.avatar_img, holder.keeper_img, options);
			if(debug)System.out.println("mateItem.avatar_img:" + mateItem.avatar_img);
			if ("operator".equals(mateItem.type)) {
				holder.text1.setText("技        师：");
			}
			holder.keeper_name.setText(mateItem.name);
			holder.keeper_rating_tv.setText(mContext.getString(R.string.rating_str, mateItem.star_count));
			holder.keeper_phone_tv.setText(mateItem.phone_number);
			holder.keeper_avator_tv.setTag(mateItem.ID_number);
			return view;
		}

		class ViewHolder {
			ImageView keeper_img;
			TextView text1, keeper_name, keeper_rating_tv, keeper_phone_tv, keeper_avator_tv;
		}
	}

	class MateItem {
		public Long id;
		public String avatar_img;
		public Integer star_count;
		public String name;
		public String phone_number;
		public String type;
		public String ID_number;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_ll:
			finish();
			break;
		case R.id.close_img:
			closeActivities();
			break;
		default:
			break;
		}
	}
}
