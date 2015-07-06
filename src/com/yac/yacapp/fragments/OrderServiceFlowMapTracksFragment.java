package com.yac.yacapp.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.yac.yacapp.R;
import com.yac.yacapp.domain.TrackDomain;
import com.yac.yacapp.icounts.ICounts;
import com.yac.yacapp.utils.Utils;
import com.yac.yacapp.views.MyProgressDialog;
import com.yac.yacapp.views.MyToast;

public class OrderServiceFlowMapTracksFragment extends Fragment implements ICounts {

	private static final int GET_TRACKS_WHAT = 0;
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private RoutePlanSearch mSearch;

	private Boolean useDefaultIcon = false;
	private Boolean usedrawLines = false;
	private Gson mGson;
	private Long mOrder_id;
	private MyProgressDialog myProgressDialog;
	private AsyncHttpClient mClient;
	private List<TrackDomain> mTrackDomains;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GET_TRACKS_WHAT:
				String content = (String) msg.obj;
				if (debug)
					System.out.println("tracks content:" + content);
				parserTacksJson(content);
				break;
			case PARSERJSON_SUCCESS:
				if (myProgressDialog != null && myProgressDialog.isShowing()) {
					myProgressDialog.dismiss();
				}
				onDrawCarTrack();
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

	protected void handlePARSERJSON_NET_FAILURE(String message) {
		if (!TextUtils.isEmpty(message)) {
			MyToast.makeText(getActivity(), R.color.pay_color, message, MyToast.LENGTH_SHORT).show();
		} else {
			MyToast.makeText(getActivity(), R.color.pay_color, getString(R.string.netfail_msg), MyToast.LENGTH_SHORT).show();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_orderflow_map, null);
		mOrder_id = getActivity().getIntent().getLongExtra("order_id", -1);
		myProgressDialog = MyProgressDialog.createDialog(getActivity());
		myProgressDialog.setMessage(getString(R.string.progress_msg));
		mClient = new AsyncHttpClient();
		mGson = new Gson();
		initView(view);
		initData();

		return view;
	}

	private void initView(View view) {
		mMapView = (MapView) view.findViewById(R.id.bmapView);
		//mPrompt_order_btn = (Button) view.findViewById(R.id.prompt_order_btn);
		mBaiduMap = mMapView.getMap();
		mMapView.removeViewAt(1); // 去掉百度logo
	}

	private void initData() {
		myProgressDialog.show();
		Map<String, String> map = new HashMap<String, String>();
		map.put("order_id", String.valueOf(mOrder_id));
		Utils.get(getActivity(), mClient, GET_TRACKS_SUBURL, map, mHandler, GET_TRACKS_WHAT, true, null);
	}

	protected void onDrawCarTrack() {
		List<LatLng> latLngs = new ArrayList<LatLng>();
		for (TrackDomain trackDomain : mTrackDomains) {
			latLngs.add(new LatLng(trackDomain.latitude, trackDomain.longitude));
		}
		// latLngs.add(new LatLng(31.40, 121.50));
		System.out.println("latLngs.size():" + latLngs.size());
		try {
			if (latLngs.size() >= 2/* && latLngs.size() < 2000 */) {
				if(usedrawLines){
					dravingLinesTracks(latLngs);
				}else{
					dravingTracks(latLngs);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void dravingTracks(List<LatLng> latLngs) {
		mSearch = RoutePlanSearch.newInstance();
		mSearch.setOnGetRoutePlanResultListener(listener);
		PlanNode stNode = null, enNode = null;
		List<PlanNode> planNodes = new ArrayList<PlanNode>();
		for (int i = 0; i < latLngs.size(); i++) {
			if (i == 0) {
				stNode = PlanNode.withLocation(latLngs.get(i));
				continue;
			}
			if (i == latLngs.size() - 1) {
				enNode = PlanNode.withLocation(latLngs.get(i));
				continue;
			}
			if(latLngs.size() >= 100 && i % 10 == 0)
				planNodes.add(PlanNode.withLocation(latLngs.get(i)));
		}
		System.out.println("planNodes.size():" + planNodes.size());//当经过的点太多时，不能搜索到结果
		DrivingRoutePlanOption routePlanOption = new DrivingRoutePlanOption();
		routePlanOption.from(stNode).to(enNode).passBy(planNodes);
		boolean flag = mSearch.drivingSearch(routePlanOption);
//		if (flag) {
//			MyToast.makeText(getActivity(), R.color.yac_green, "获取成功", MyToast.LENGTH_SHORT).show();
//		} else {
//			MyToast.makeText(getActivity(), R.color.pay_color, "搜索失败", MyToast.LENGTH_SHORT).show();
//		}
	}

	private void dravingLinesTracks(List<LatLng> latLngs) {
		// 在地图上添加多边形Option，用于显示
		OverlayOptions polylineOption = new PolylineOptions().width(5).color(Color.RED).points(latLngs);
		mBaiduMap.addOverlay(polylineOption);
		LatLng last = latLngs.get(latLngs.size() - 1);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(last, 14);// 设置地图中心点以及缩放级
		if (u != null)
			mBaiduMap.animateMapStatus(u);
	}

	private OnGetRoutePlanResultListener listener = new OnGetRoutePlanResultListener() {
		public void onGetWalkingRouteResult(WalkingRouteResult result) {
			// 获取步行线路规划结果
		}

		public void onGetTransitRouteResult(TransitRouteResult result) {
			// 获取公交换乘路径规划结果
		}

		public void onGetDrivingRouteResult(DrivingRouteResult result) {
			mBaiduMap.clear();
			System.out.println("onGetDrivingRouteResult is run.");
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				Toast.makeText(getActivity(), "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
			}
			if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
				return;
			}
			if (result.error == SearchResult.ERRORNO.NO_ERROR) {
				DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiduMap);
				mBaiduMap.setOnMarkerClickListener(null);
				overlay.setData(result.getRouteLines().get(0));
				overlay.addToMap();
				overlay.zoomToSpan();
			}
		}
	};

	// 定制RouteOverly
	private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

		public MyDrivingRouteOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public BitmapDescriptor getStartMarker() {
			if (useDefaultIcon) { 
				return BitmapDescriptorFactory.fromResource(R.drawable.greenaddress);
			}
			return null;
		}

		@Override
		public BitmapDescriptor getTerminalMarker() {
			if (useDefaultIcon) {
				return BitmapDescriptorFactory.fromResource(R.drawable.redaddress);
			}
			return null;
		}
	}

	protected void parserTacksJson(String content) {
		try {
			JSONArray jsonArray = new JSONArray(content);
			if (mTrackDomains == null)
				mTrackDomains = new ArrayList<TrackDomain>();
			for (int i = 0; i < jsonArray.length(); i++) {
				TrackDomain trackDomain = mGson.fromJson(jsonArray.optString(i), TrackDomain.class);
				mTrackDomains.add(trackDomain);
			}
			mHandler.sendEmptyMessage(PARSERJSON_SUCCESS);
		} catch (JSONException e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(PARSERJSON_FAILURE);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mMapView != null)
			mMapView.onDestroy();
		if (mSearch != null)
			mSearch.destroy();
	}
}
