package com.yac.yacapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yac.yacapp.R;
import com.yac.yacapp.icounts.BaseActivity;
import com.yac.yacapp.icounts.ICounts;
import com.yac.yacapp.utils.Utils2;
import com.yac.yacapp.views.MyToast;

public class SixServiceActivity extends BaseActivity implements ICounts, OnClickListener {

	private LinearLayout mBack_ll;
	private TextView mBack_tv, mActionbar_title_tv;
	private ImageView mClose_img;
	private Button mSix_service_btn;
	private WebView mWebView;
	private int fromCode = -1;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_six_service);
		fromCode = getIntent().getFlags();
		initView();
	}

	private void initView() {
		mBack_ll = (LinearLayout) findViewById(R.id.back_ll);
		mBack_tv = (TextView) findViewById(R.id.back_tv);
		mActionbar_title_tv = (TextView) findViewById(R.id.actionbar_title_tv);
		mClose_img = (ImageView) findViewById(R.id.close_img);
		mSix_service_btn = (Button) findViewById(R.id.six_service_btn);
		mWebView = (WebView) findViewById(R.id.webview);
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		
		mBack_ll.setVisibility(View.VISIBLE);
		mBack_ll.setOnClickListener(this);
		if(fromCode == FROM_CARPARTS_DETAILS_SERVICE){
			mBack_tv.setText("部件详情");
		}else{
			mBack_tv.setText("服务");
		}
		mClose_img.setOnClickListener(this);
		mSix_service_btn.setOnClickListener(this);
		initData();
		mWebView.setWebViewClient(new WebViewClient(){
	           @Override
	        public boolean shouldOverrideUrlLoading(WebView view, String url) {
	            //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
	            view.loadUrl(url);
	            return true;
	        }
	     });
	}

	/*
	 * for title,webview
	 */
	private static final String WEB_BASE_URL = /*"http://120.132.59.94/static/services";*/
												"http://baseimg.yangaiche.com/service";
	private void initData() {
		switch (fromCode) {
		case FROM_CARPARTS_DETAILS_SERVICE:
		case KEEPER_PICK_SERVICE:
			mActionbar_title_tv.setText("接车服务");
			mWebView.loadUrl(WEB_BASE_URL+"1.html");
			break;
		case DOOR_SERVICE:
			mActionbar_title_tv.setText("汽车美容");
			mWebView.loadUrl(WEB_BASE_URL+"2.html");
			break;
		case VALIDATE_CAR_SERVICE:
			mActionbar_title_tv.setText("保险续险");
			mWebView.loadUrl(WEB_BASE_URL+"3.html");
			break;
		case KEEPER_TEST_SERVICE:
			mActionbar_title_tv.setText("管家检测");
			mWebView.loadUrl(WEB_BASE_URL+"4.html");
			break;
		case VERIFY_LOSS_SERVICE:
			mActionbar_title_tv.setText("代办定损");
			mWebView.loadUrl(WEB_BASE_URL+"5.html");
			break;
		case EMERGENCY_RESCUE_SERVICE:
			mActionbar_title_tv.setText("紧急救援");
			mWebView.loadUrl(WEB_BASE_URL+"6.html");
			mSix_service_btn.setText("救援电话：400-0102-766");
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_ll:
			finish();
			break;
		case R.id.six_service_btn:
			startSixService();
			break;
		case R.id.close_img:
			closeActivities();
			break;
		default:
			break;
		}
	}

	private void startSixService() {
		Intent intent = new Intent(this, OrderDetailsActivity.class);
		switch (fromCode) {
		case FROM_CARPARTS_DETAILS_SERVICE:
		case KEEPER_PICK_SERVICE:
			intent.addFlags(KEEPER_PICK_SERVICE);
			startActivity(intent);
			break;
		case DOOR_SERVICE:
			intent.addFlags(DOOR_SERVICE);
			startActivity(intent);
			break;
		case VALIDATE_CAR_SERVICE:
			intent.addFlags(VALIDATE_CAR_SERVICE);
			startActivity(intent);
			break;
		case KEEPER_TEST_SERVICE:
			intent.addFlags(KEEPER_TEST_SERVICE);
			startActivity(intent);
			break;
		case VERIFY_LOSS_SERVICE:
			intent.addFlags(VERIFY_LOSS_SERVICE);
			startActivity(intent);
			break;
		case EMERGENCY_RESCUE_SERVICE:
			try{
				Utils2.phoneCall(this, "4000102766");
			}catch (Exception e) {
				MyToast.makeText(this, R.color.pay_color, "设备拨号异常", MyToast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
	}
	
}
