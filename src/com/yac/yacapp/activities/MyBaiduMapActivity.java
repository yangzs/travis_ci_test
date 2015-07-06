package com.yac.yacapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.yac.yacapp.R;
import com.yac.yacapp.domain.Location;
import com.yac.yacapp.icounts.BaseActivity;
import com.yac.yacapp.views.MyToast;

/**
 * 没有搜索按钮，当点击确定时，直接返回输入框中的 1，输入地址，点击建议地址，直接进行搜索 2，点击地图上的poi地点，进行tv的更新，确定返回地址
 */
public class MyBaiduMapActivity extends BaseActivity implements OnClickListener, OnGetPoiSearchResultListener, OnGetSuggestionResultListener {

	private ImageView back_img;
	private AutoCompleteTextView address_tv;
	private Button ok_btn;
	/**
	 * MapView 是地图主控件
	 */
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	// 定位相关
	private LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	boolean isFirstLoc = true;// 是否首次定位

	private SDKReceiver mReceiver;
	private PoiSearch mPoiSearch = null;
	private SuggestionSearch mSuggestionSearch = null;
	/**
	 * 搜索关键字输入窗口
	 */
	private AutoCompleteTextView keyWorldsView = null;
	private ArrayAdapter<String> sugAdapter = null;
	private int load_Index = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mybaidumap);
		initView();
		// 监听地图的初始化
		initMapSDKReceiver();
		// 开启定位图层
		startMapLocation();
		initMapViewListener();
		initSearch();
	}

	private void startMapLocation() {
		mBaiduMap.setMyLocationEnabled(true);
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(LocationMode.NORMAL, true, null));
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		// option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
	}

	private void initView() {
		findViewById(R.id.head_ll).setOnClickListener(mSoftInputOnClickListener);
		back_img = (ImageView) findViewById(R.id.back_img);
		address_tv = (AutoCompleteTextView) findViewById(R.id.address_tv);
		ok_btn = (Button) findViewById(R.id.ok_btn);

		back_img.setOnClickListener(this);
		address_tv.setOnClickListener(this);
		ok_btn.setOnClickListener(this);
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		mMapView.removeViewAt(1); // 去掉百度logo
	}

	private void initSearch() {
		// 初始化搜索模块，注册搜索事件监听
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);
		mSuggestionSearch = SuggestionSearch.newInstance();
		mSuggestionSearch.setOnGetSuggestionResultListener(this);
		sugAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line);
		address_tv.setAdapter(sugAdapter);
		address_tv.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable cs) {
				if (!TextUtils.isEmpty(cs))
					mSuggestionSearch.requestSuggestion((new SuggestionSearchOption()).keyword(cs.toString()).city("北京"));
			}

			@Override
			public void beforeTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
				if (!TextUtils.isEmpty(cs))
					mSuggestionSearch.requestSuggestion((new SuggestionSearchOption()).keyword(cs.toString()).city("北京"));
			}

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
				// 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
				if (!TextUtils.isEmpty(cs))
					mSuggestionSearch.requestSuggestion((new SuggestionSearchOption()).keyword(cs.toString()).city("北京"));
			}
		});
		address_tv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				searchButtonProcess();
			}
		});
	}

	ReverseGeoCodeOption mReCodeOption;
	GeoCoder mGetCoder;
	GetAddress mGetAddressListtener;

	private void initMapViewListener() {
		// 当点击的时候，弹出覆盖物气泡。并记录下当前的position
		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {
			public void onMapClick(LatLng point) {
				if (debug)
					System.out.println("onMapClick is click.point:" + point);
				if (mReCodeOption == null)
					mReCodeOption = new ReverseGeoCodeOption();
				mReCodeOption.location(point);
				if (mGetCoder == null)
					mGetCoder = GeoCoder.newInstance();
				mGetCoder.reverseGeoCode(mReCodeOption);
				if (mGetAddressListtener == null)
					mGetAddressListtener = new GetAddress();
				mGetCoder.setOnGetGeoCodeResultListener(mGetAddressListtener);
				// mark(point);
			}

			public boolean onMapPoiClick(MapPoi poi) {
				if (debug)
					System.out.println("onMapClick is click.poi:" + poi);
				onMapClick(poi.getPosition());
				return false;
			}
		});
	}

	class GetAddress implements OnGetGeoCoderResultListener { // 点击地图获取点击位置处的详细地址

		@Override
		public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
		}

		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
			mark(reverseGeoCodeResult.getLocation(), reverseGeoCodeResult.getAddress());
			// address_tv.setText(arg0.getAddress());
		}
	}

	// 构建Marker图标
	private BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.address);;
	// 构建MarkerOption，用于在地图上添加Marker
	private MarkerOptions option;

	private void mark(LatLng point, String title) {// 显示覆盖物
		mBaiduMap.clear();
		// 定义Maker坐标点
		if (option == null)
			option = new MarkerOptions().icon(bitmap);
		option.position(point);
		// 在地图上添加Marker，并显示
		mBaiduMap.addOverlay(option);
		address_tv.setText(title);
		if (mCurrentLatLng != null) {
			mCurrentLatLng = null;
		}
		mCurrentLatLng = point;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_img:
			finish();
			break;
		case R.id.ok_btn:
			closeAndPassLocation();
			break;
		default:
			break;
		}
	}

	private void closeAndPassLocation() {
		Location location = null;
		if (mCurrentLatLng != null) {
			location = new Location();
			location.name = address_tv.getText().toString();
			location.address = address_tv.getText().toString();
			location.latitude = mCurrentLatLng.latitude;
			location.longitude = mCurrentLatLng.longitude;
		} else if (!TextUtils.isEmpty(address_tv.getText().toString())) {
			location = new Location();
			location.name = address_tv.getText().toString();
			location.address = address_tv.getText().toString();
			location.latitude = 2.0;
			location.longitude = 2.0;
		}
		Intent intent = new Intent();
		intent.putExtra("location", location);
		setResult(RESULT_OK, intent);// (RESULT_OK);
		finish();
	}

	private void initMapSDKReceiver() {
		// 注册 SDK 广播监听者
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mReceiver = new SDKReceiver();
		registerReceiver(mReceiver, iFilter);
	}

	/**
	 * 构造广播监听类，监听 SDK key 验证以及网络异常广播
	 */
	public class SDKReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				MyToast.makeText(getApplicationContext(), R.color.pay_color, "key 验证出错!", MyToast.LENGTH_SHORT).show();
			} else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				MyToast.makeText(getApplicationContext(), R.color.pay_color, "网络出错", MyToast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (debug)
				System.out.println("onReceiveLocation is run.location:" + location.getLongitude());
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
			// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			try {
				if (isFirstLoc) {
					isFirstLoc = false;
					LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
					MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 15);// 设置地图中心点以及缩放级
					if (u != null)
						mBaiduMap.animateMapStatus(u);
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		mMapView.onDestroy();
		unregisterReceiver(mReceiver);
		mPoiSearch.destroy();
		mSuggestionSearch.destroy();
		mLocClient.stop();
		mLocClient.unRegisterLocationListener(myListener);
		mLocClient = null;
		super.onDestroy();
	}

	// 没有搜索功能
	public void searchButtonProcess() {
		String searchKey = address_tv.getText().toString().trim();
		mPoiSearch.searchInCity((new PoiCitySearchOption()).city("北京市").keyword(searchKey).pageNum(load_Index));
	}

	private LatLng mCurrentLatLng;

	@Override
	public void onGetPoiDetailResult(PoiDetailResult result) {
		if (result.error != SearchResult.ERRORNO.NO_ERROR) {
			MyToast.makeText(MyBaiduMapActivity.this, R.color.pay_color, "抱歉，未搜到结果", MyToast.LENGTH_SHORT).show();
		} else {
			mCurrentLatLng = result.getLocation();
			address_tv.setText(result.getAddress());
			address_tv.setSelection(result.getAddress().length());
		}
	}

	@Override
	public void onGetPoiResult(PoiResult result) {
		if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			MyToast.makeText(MyBaiduMapActivity.this, R.color.pay_color, "未搜到结果", MyToast.LONGTIME).show();
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			mBaiduMap.clear();
			PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
			mBaiduMap.setOnMarkerClickListener(overlay);
			overlay.setData(result);
			overlay.addToMap();
			overlay.zoomToSpan();
			return;
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

			// 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
			String strInfo = "在";
			for (CityInfo cityInfo : result.getSuggestCityList()) {
				strInfo += cityInfo.city;
				strInfo += ",";
			}
			strInfo += "找到结果";
			MyToast.makeText(MyBaiduMapActivity.this, R.color.pay_color, strInfo, MyToast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onGetSuggestionResult(SuggestionResult res) {
		if (res == null || res.getAllSuggestions() == null) {
			return;
		}
		sugAdapter.clear();
		for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
			if (info.key != null)
				sugAdapter.add(info.city + info.district + info.key);
		}
		sugAdapter.notifyDataSetChanged();
	}

	private class MyPoiOverlay extends PoiOverlay {

		public MyPoiOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public boolean onPoiClick(int index) {
			super.onPoiClick(index);
			PoiInfo poi = getPoiResult().getAllPoi().get(index);
			// if (poi.hasCaterDetails) {
			mPoiSearch.searchPoiDetail((new PoiDetailSearchOption()).poiUid(poi.uid));
			// }
			return true;
		}
	}
}
