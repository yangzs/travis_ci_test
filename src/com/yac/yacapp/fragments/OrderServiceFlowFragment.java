package com.yac.yacapp.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.yac.yacapp.R;
import com.yac.yacapp.adapter.MyServiceFlowAdapter;
import com.yac.yacapp.domain.OrderServiceFlow;
import com.yac.yacapp.icounts.ICounts;
import com.yac.yacapp.utils.Utils;
import com.yac.yacapp.views.MyProgressDialog;
import com.yac.yacapp.views.MyToast;

public class OrderServiceFlowFragment extends Fragment implements ICounts {

	private static final int GET_SERVICEFLOW_WHAT = 0;
	private ListView flow_listview;
	private List<OrderServiceFlow> mOrderServiceFlows;
	private MyServiceFlowAdapter myServiceFlowAdapter;
	
	private Long mOrder_Id;
	private MyProgressDialog myProgressDialog;
	private AsyncHttpClient mClient;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GET_SERVICEFLOW_WHAT:
				String content = (String) msg.obj;
				parserServiceFlowJson(content);
				break;
			case PARSERJSON_SUCCESS:
				if (myProgressDialog != null && myProgressDialog.isShowing()) {
					myProgressDialog.dismiss();
				}
				myServiceFlowAdapter.updateData(mOrderServiceFlows);
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
	
    protected void handlePARSERJSON_NET_FAILURE(String message){
    	if(!TextUtils.isEmpty(message)){
			MyToast.makeText(getActivity(), R.color.pay_color, message, MyToast.LENGTH_SHORT).show();
		}else{
			MyToast.makeText(getActivity(), R.color.pay_color, getString(R.string.netfail_msg), MyToast.LENGTH_SHORT).show();
		}
    }
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_order_serviceflow, null);
		myProgressDialog = MyProgressDialog.createDialog(getActivity());
		myProgressDialog.setMessage(getString(R.string.progress_msg));
		mClient = new AsyncHttpClient();
		mOrder_Id = getActivity().getIntent().getLongExtra("order_id", -1);
		initView(view);
		getNetData();
		
		return view;
	}
	
	private void initView(View view) {
		flow_listview = (ListView) view.findViewById(R.id.flow_listview);
		if (mOrderServiceFlows == null) {
			mOrderServiceFlows = new ArrayList<OrderServiceFlow>();
		}
		myServiceFlowAdapter = new MyServiceFlowAdapter(getActivity(), mOrderServiceFlows);
		flow_listview.setAdapter(myServiceFlowAdapter);
	}
	
	private void getNetData() {
		myProgressDialog.show();
		Map<String, String> map = new HashMap<String, String>();
		map.put("order_id", String.valueOf(mOrder_Id));
		Utils.get(getActivity(), mClient, SERVICE_FLOW_SUBURL, map, mHandler, GET_SERVICEFLOW_WHAT, true, null);
	}
	
	protected void parserServiceFlowJson(String content) {
		try {
			JSONArray jsonArray = new JSONArray(content);
			if (jsonArray != null && jsonArray.length() > 0) {
				Gson gson = new Gson();
				for (int i = 0; i < jsonArray.length(); i++) {
					String str = jsonArray.optString(i);
					OrderServiceFlow serviceFlow = gson.fromJson(str, OrderServiceFlow.class);
					if (serviceFlow != null) {
						mOrderServiceFlows.add(serviceFlow);
					}
				}
			}
			mHandler.sendEmptyMessage(PARSERJSON_SUCCESS);
		} catch (JSONException e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(PARSERJSON_FAILURE);
		}
	}
}
