package com.yac.yacapp.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yac.yacapp.R;
import com.yac.yacapp.activities.ServiceFlowPicsViewPagerActivity;
import com.yac.yacapp.domain.Picture;
import com.yac.yacapp.domain.Pictures4Transfer;

public class MyPictureItemAdapter extends BaseAdapter {

	private Context mContext;
	private List<Picture> mPictures;
	private LayoutInflater mInflater;
	private ImageLoader mImageLoader;
	private DisplayImageOptions options;
	
	public MyPictureItemAdapter(Context context, List<Picture> pictures){
		this.mContext = context;
		this.mPictures = pictures;
		mInflater = LayoutInflater.from(mContext);
		mImageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder().cacheOnDisk(true).showImageOnLoading(R.drawable.car_brand_default).showImageOnFail(R.drawable.car_brand_default).build();
	}
	
	public void updateData(List<Picture> pictures){
		this.mPictures = pictures;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		if(mPictures.size() >= 4){
			return 4;
		}
		return mPictures.size();
	}

	@Override
	public Picture getItem(int position) {
		return mPictures.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		View view = convertView;
		if(view == null){
			holder = new ViewHolder();
			view = mInflater.inflate(R.layout.item_image, null);
			holder.imageView = (ImageView) view.findViewById(R.id.image);
			view.setTag(holder);
		}else{
			holder = (ViewHolder) view.getTag();
		}
		Picture picture = getItem(position);
		if(position == 3 && mPictures.size() >4){
			holder.imageView.setImageResource(R.drawable.points);
		}else{
			mImageLoader.displayImage(picture.thumbnail_url, holder.imageView, options);
		}
		holder.imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, ServiceFlowPicsViewPagerActivity.class);
				Pictures4Transfer pictures4Transfer = new Pictures4Transfer(position, mPictures);
				intent.putExtra("Pictures4Transfer", pictures4Transfer);
				mContext.startActivity(intent);
			}
		});
		return view;
	}
	
	class ViewHolder{
		ImageView imageView;
	}

}
