package com.yac.yacapp.activities;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.yac.yacapp.R;
import com.yac.yacapp.icounts.BaseActivity;
import com.yac.yacapp.utils.Utils;
import com.yac.yacapp.views.MyProgressDialog;
import com.yac.yacapp.views.MyToast;

public class PlaceOrderOKActivity extends BaseActivity implements OnClickListener {

	private TextView mActionbar_title_tv;
	private ImageView mClose_img;
	private Button mClose_btn;
	
	private MyProgressDialog myProgressDialog;
	private AsyncHttpClient mClient;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case UPDATE_USERINFO_WHAT:
				if (myProgressDialog != null && myProgressDialog.isShowing()) {
					myProgressDialog.dismiss();
				}
				String userinfo_str = (String) msg.obj;
				mSP.edit().putString(SP_UserInfo, userinfo_str).commit();
				sendUpdateBroadcastReceiver();
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
		setContentView(R.layout.activity_place_order_ok);
		initView();
		updateUserinfo();
	}

	private void updateUserinfo() {
		mClient = new AsyncHttpClient();
		myProgressDialog = MyProgressDialog.createDialog(this);
		myProgressDialog.setMessage(getString(R.string.progress_msg));
		myProgressDialog.show();
		Utils.updateUserInfoJson(this, mClient, mHandler);
	}

	private void initView() {
		mActionbar_title_tv = (TextView) findViewById(R.id.actionbar_title_tv);
		mClose_img = (ImageView) findViewById(R.id.close_img);
		mClose_btn = (Button) findViewById(R.id.close_btn);
		
		mActionbar_title_tv.setText("下单成功");
		mClose_img.setVisibility(View.GONE);
		mClose_btn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.close_btn:
			closeActivities();
			break;
		default:
			break;
		}
	}
	
}
