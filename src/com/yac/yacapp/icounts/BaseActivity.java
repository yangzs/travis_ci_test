package com.yac.yacapp.icounts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.yac.yacapp.R;
import com.yac.yacapp.utils.Utils2;
import com.yac.yacapp.views.BaseRelativeLayout;
import com.yac.yacapp.views.MyToast;

public class BaseActivity extends Activity implements ICounts {
	protected SharedPreferences mSP;
	protected ImageLoader mImageLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSP = getSharedPreferences(SP_NAME, MODE_PRIVATE);
		mImageLoader = ImageLoader.getInstance();
		if (!mImageLoader.isInited())
			mImageLoader.init(ImageLoaderConfiguration.createDefault(this));
		MyActivityManager.getInstance().addActivity(this);
	}
	
	//右滑操作
	public void setContentView(View view) {
		super.setContentView(getBaseView(view));
	}

	public void setContentView(int layoutResId) {
		setTheme(R.style.Transparent);
		View view = View.inflate(this, layoutResId, null);
		super.setContentView(getBaseView(view));
	}

	private RelativeLayout getBaseView(View view) {
		BaseRelativeLayout baseView = new BaseRelativeLayout(this);
		view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		baseView.addView(view);
		return baseView;
	}
	//右滑结束
	
	//打开activity事件，不会立即响应，双击时会打开两次activity
	@Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (Utils2.isFastDoubleClick()) {
            	if(debug)System.out.println("is double click.!!!");
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }
	
	@Override
	protected void onPause() {
		super.onPause();
//		PgyFeedbackShakeManager.unregister();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
//		PgyFeedbackShakeManager.register(this, App.mAppId);
	}

	protected void closeActivities() {
		MyActivityManager.getInstance().closeActivities();
	}

	protected void sendUpdateBroadcastReceiver() {
		Intent mIntent = new Intent(UPDATA_USERINFO_ACTION);
		sendBroadcast(mIntent);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		MyActivityManager.getInstance().removeActivity(this);
	}
	
	protected OnClickListener mSoftInputOnClickListener = new OnClickListener() {
        @Override  
        public void onClick(View v2) {  
            /* 关闭软键盘 */  
            View view = BaseActivity.this.getWindow().peekDecorView();  
            if (view != null && view.getWindowToken() != null) {  
                try {  
                    ((InputMethodManager) BaseActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(BaseActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);  
                } catch (Exception e) {  
                	if(debug)Log.w("AOS", "==软键盘关闭时出了异常");  
                }  
            }  
        }  
    }; 
    
    protected void handlePARSERJSON_NET_FAILURE(String message){
    	if(!TextUtils.isEmpty(message)){
			MyToast.makeText(this, R.color.pay_color, message, MyToast.LENGTH_SHORT).show();
		}else{
			MyToast.makeText(this, R.color.pay_color, getString(R.string.netfail_msg), MyToast.LENGTH_SHORT).show();
		}
    }
    
}
