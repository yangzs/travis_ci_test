package com.yac.yacapp.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.yac.yacapp.R;
import com.yac.yacapp.domain.Location;
import com.yac.yacapp.domain.UserInfo;
import com.yac.yacapp.icounts.App;
import com.yac.yacapp.icounts.BaseActivity;
import com.yac.yacapp.icounts.ICounts;
import com.yac.yacapp.utils.Utils;
import com.yac.yacapp.utils.Utils2;
import com.yac.yacapp.views.ListViewCompat4Location;
import com.yac.yacapp.views.MyProgressDialog;
import com.yac.yacapp.views.MyToast;
import com.yac.yacapp.views.SlideView;
import com.yac.yacapp.views.SlideView.OnSlideListener;

public class MyLocationActivity extends BaseActivity implements ICounts, OnSlideListener, OnClickListener {

	private static final int GET_USERINFO = 0;
	private static final int DELETE_LOCATION_WHAT = 2;
	private static final int REQUEST_CODE_PICKADDRESS = 3;
	private static final int CREATE_LOCATION_WHAT = 4;
	private List<MessageItem> mMessageItems = new ArrayList<MessageItem>();
	private List<Location> mLocations = new ArrayList<Location>();
	private LinearLayout mBack_ll;
	private TextView mBack_tv, mActionbar_title_tv;
	private ImageView mClose_img;
	private ListViewCompat4Location location_listview;
	private Button add_btn;
	
    private SlideView mLastSlideViewWithStatusOn;
	private SlideAdapter mAdapter;
	
	private int flag = 0;
	private MyProgressDialog myProgressDialog;
	private AsyncHttpClient mClient;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GET_USERINFO:
				String content1 = (String) msg.obj;
				parserUserInfoJson(content1);
				break;
			case PARSERJSON_SUCCESS:
				if (myProgressDialog != null && myProgressDialog.isShowing()) {
					myProgressDialog.dismiss();
				}
				initMessageItemData();
				break;
			case DELETE_LOCATION_WHAT:
				App.needNetDate = true;
				getNetData();
				break;
			case CREATE_LOCATION_WHAT://添加地址
				App.needNetDate = true;
				getNetData();
				break;
			case PARSERJSON_FAILURE:
			case NET_FAILURE:
				if (myProgressDialog != null && myProgressDialog.isShowing()) {
					myProgressDialog.dismiss();
				}
				String message = (String) msg.obj;
				handlePARSERJSON_NET_FAILURE(message);
				break;
			default:
				break;
			}
		};
	};	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mylocation);
		mClient = new AsyncHttpClient();
		myProgressDialog = MyProgressDialog.createDialog(this);
		myProgressDialog.setMessage(getString(R.string.progress_msg));
		flag = getIntent().getFlags();
		initView();
		getNetData();
	}

	protected void initMessageItemData() {
		if(mLocations != null && mMessageItems != null){
			mMessageItems.clear();
			for(Location location : mLocations){
				MessageItem item = new MessageItem();
				item.location = location;
				mMessageItems.add(item);
			}
			mAdapter.notifyDataSetChanged();
			Utils2.setListViewHeightBasedOnChildren(location_listview);
		}
	}

	private void getNetData() {
		if(App.needNetDate){
			App.needNetDate = false;
			myProgressDialog.show();
			Map<String, String> map = new HashMap<String, String>();
			map.put("user_id", String.valueOf(App.mCarUser.user_id));
			Utils.get(this, mClient, USER_INFO_SUBURL, map, mHandler, GET_USERINFO, true);
		}else{
			String userinfo_str = mSP.getString(SP_UserInfo, null);
			UserInfo userInfo = new Gson().fromJson(userinfo_str, UserInfo.class);
			mLocations = userInfo.location;
			mHandler.sendEmptyMessage(PARSERJSON_SUCCESS);
		}
	}

	private void initView() {
		mBack_ll = (LinearLayout) findViewById(R.id.back_ll);
		mBack_tv = (TextView) findViewById(R.id.back_tv);
		mActionbar_title_tv = (TextView) findViewById(R.id.actionbar_title_tv);
		mClose_img = (ImageView) findViewById(R.id.close_img);
		location_listview = (ListViewCompat4Location) findViewById(R.id.car_listview);
		add_btn = (Button) findViewById(R.id.add_car_btn);
		
		mBack_ll.setVisibility(View.VISIBLE);
		mBack_ll.setOnClickListener(this);
		mActionbar_title_tv.setText("我的地址");
		mClose_img.setOnClickListener(this);
		add_btn.setText("+添加新地址");
		add_btn.setOnClickListener(this);
		mAdapter = new SlideAdapter();
		location_listview.setAdapter(mAdapter);
		Utils2.setListViewHeightBasedOnChildren(location_listview);
		if(flag != PICK_LOCATION_FLAG){
			mBack_tv.setText("我");
		}else{
			mBack_tv.setText("确认订单");
			location_listview.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					setActivityResult(mLocations.get(position));
				}
			});
		}
	}
	
	private void setActivityResult(Location location){
		Intent intent = new Intent();
		intent.putExtra("location", location);
		setResult(RESULT_OK, intent);
		finish();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PICKADDRESS) {
			Location location = (Location) data.getSerializableExtra("location");
			if(flag == PICK_LOCATION_FLAG){
				setActivityResult(location);
			}else if (location != null) {
				createNewlocation(location);
			}
		}
	}

	private void createNewlocation(Location location) {
		myProgressDialog.show();
		location.user_id = App.mCarUser.user_id;
		String loc_str = new Gson().toJson(location);
		Utils.post(this, mClient, CREATE_ADDRESS_SUBURL, loc_str, mHandler, CREATE_LOCATION_WHAT, true);
	}

	private class SlideAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		SlideAdapter() {
			super();
			mInflater = getLayoutInflater();
		}

		@Override
		public int getCount() {
			return mMessageItems.size();
		}

		@Override
		public Object getItem(int position) {
			return mMessageItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			SlideView slideView = (SlideView) convertView;
			if (slideView == null) {
				View itemView = mInflater.inflate(R.layout.item_location_info, null);

				slideView = new SlideView(MyLocationActivity.this);
				slideView.setContentView(itemView);

				holder = new ViewHolder(slideView);
				slideView.setOnSlideListener(MyLocationActivity.this);
				slideView.setTag(holder);
			} else {
				holder = (ViewHolder) slideView.getTag();
			}
			MessageItem item = mMessageItems.get(position);
			item.slideView = slideView;
			item.slideView.shrink();

			holder.location_address_tv.setText(item.location.address);
			holder.deleteHolder.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					deleteLocation(mMessageItems.get(position).location.id);
				}
			});
			return slideView;
		}
	}

	public class MessageItem {
		public Location location;
		public SlideView slideView;
	}

	private static class ViewHolder {
		public TextView location_address_tv;
		public ViewGroup deleteHolder;

		ViewHolder(View view) {
			location_address_tv = (TextView) view.findViewById(R.id.location_address_tv);
			deleteHolder = (ViewGroup) view.findViewById(R.id.holder);
		}
	}

	@Override
	public void onSlide(View view, int status) {
        if (mLastSlideViewWithStatusOn != null && mLastSlideViewWithStatusOn != view) {
            mLastSlideViewWithStatusOn.shrink();
        }
        if (status == SLIDE_STATUS_ON) {
            mLastSlideViewWithStatusOn = (SlideView) view;
        }
	}
	
	protected void deleteLocation(long id) {
		myProgressDialog.show();
		StringBuffer buffer = new StringBuffer();
		buffer.append("[").append(id).append("]");
		Utils.post(this, mClient, DELETE_ADDRESS_SUBURL, buffer.toString(), mHandler, DELETE_LOCATION_WHAT, true);
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(id == R.id.back_ll){
			finish();
		}else if(id == R.id.add_car_btn){
			//startActivity(new Intent(this, PickCarActivity.class));
			openBaiDuSdkMap();
		}else if (id == R.id.close_img){
			closeActivities();
		}
	}
	
	private void openBaiDuSdkMap() {
		Intent intent = new Intent(this, MyBaiduMapActivity.class);
		startActivityForResult(intent, REQUEST_CODE_PICKADDRESS);
	}

	protected void parserUserInfoJson(String content1) {
		Gson gson = new Gson();
		UserInfo userInfo = gson.fromJson(content1, UserInfo.class);
		mSP.edit().putString(SP_UserInfo, content1).commit();
		mLocations.clear();
		mLocations = userInfo.location;
		sendUpdateBroadcastReceiver();
		mHandler.sendEmptyMessage(PARSERJSON_SUCCESS);
	}
}
