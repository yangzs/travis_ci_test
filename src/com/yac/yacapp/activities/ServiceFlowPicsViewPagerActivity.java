package com.yac.yacapp.activities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import uk.co.senab.photoview.HackyViewPager;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher.OnViewTapListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yac.yacapp.R;
import com.yac.yacapp.domain.Picture;
import com.yac.yacapp.domain.Pictures4Transfer;
import com.yac.yacapp.icounts.App;
import com.yac.yacapp.utils.Utils2;
import com.yac.yacapp.views.MyToast;

public class ServiceFlowPicsViewPagerActivity extends Activity {

	private ViewPager mViewPager;
	private TextView mTextView;

	private List<Picture> mPictures;
	private int mCurrentPosition;
	private ImageLoader mImageLoader;
	private DisplayImageOptions options;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_serviceflow_pics_viewpager);
		mImageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder().cacheOnDisk(true).showImageOnLoading(R.drawable.car_brand_default).showImageOnFail(R.drawable.car_brand_default).build();
		initData();
		initView();
	}

	private void initView() {
		mViewPager = (HackyViewPager) findViewById(R.id.photoview_viewpager);
		mTextView = (TextView) findViewById(R.id.photoview_tv);
		mViewPager.setAdapter(new SamplePagerAdapter(this, mPictures));
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				mCurrentPosition = position;
				mTextView.setText(getString(R.string.numofpics, mCurrentPosition + 1, mPictures.size()));
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		mViewPager.setCurrentItem(mCurrentPosition);
		mTextView.setText(getString(R.string.numofpics, mCurrentPosition + 1, mPictures.size()));
	}

	private void initData() {
		Intent intent = getIntent();
		Pictures4Transfer pictures4Transfer =  (Pictures4Transfer) intent.getSerializableExtra("Pictures4Transfer");
		mPictures = pictures4Transfer.pictures;
		mCurrentPosition = pictures4Transfer.position;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.viewpager_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.save_pic_sd_menu:
			downloadImg(mPictures.get(mCurrentPosition).original_url);
			return true;
		case R.id.cancel_menu:
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void downloadImg(final String img_url) {
		new AsyncTask<String, Object, String>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}
			@Override
			protected String doInBackground(String... params) {
				String name = Utils2.createImageName();
				File file = new File(App.rootFile, name);
				if (file.exists()) {
					file.delete();
				} 
				try {
					// 从网络上获取图片
					URL url = new URL(img_url);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setConnectTimeout(5000);
					conn.setRequestMethod("GET");
					conn.setDoInput(true);
					if (conn.getResponseCode() == 200) {
						InputStream is = conn.getInputStream();
						FileOutputStream fos = new FileOutputStream(file);
						byte[] buffer = new byte[1024];
						int len = 0;
						while ((len = is.read(buffer)) != -1) {
							fos.write(buffer, 0, len);
						}
						is.close();
						fos.close();
						return file.getAbsolutePath();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			};
			protected void onPostExecute(String result) {
				if(result == null){
					MyToast.makeText(getApplicationContext(), R.color.pay_color, "保存图片失败，稍后重试", MyToast.LENGTH_SHORT).show();
				}else{
					MyToast.makeText(getApplicationContext(), R.color.yac_green, "保存图片:"+result, MyToast.LENGTH_SHORT).show();
				}
			}
		}.execute(img_url, null, null);
	}

	class SamplePagerAdapter extends PagerAdapter {

		private Context mContext;
		private List<Picture> mPictures;

		public SamplePagerAdapter(Context context, List<Picture> pictures) {
			this.mContext = context;
			this.mPictures = pictures;
		}

		@Override
		public int getCount() {
			return mPictures.size();
		}

		@Override
		public View instantiateItem(ViewGroup container, int position) {
			PhotoView photoView = new PhotoView(container.getContext());
			mImageLoader.displayImage(mPictures.get(position).original_url, photoView, options);
			photoView.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					ServiceFlowPicsViewPagerActivity.this.openOptionsMenu();
					return true;
				}
			});
			photoView.setOnViewTapListener(new OnViewTapListener() {
				@Override
				public void onViewTap(View view, float x, float y) {
					((Activity) mContext).finish();
				}
			});
			// Now just add PhotoView to ViewPager and return it
			container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

			return photoView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		class ViewHolder {
			PhotoView photoView;
		}

	}

}
