package com.yac.yacapp.icounts;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;

import com.yac.yacapp.utils.Utils2;

public class BaseFragmentActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
	}

	// 打开activity事件，不会立即响应，双击时会打开两次activity
	@Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (Utils2.isFastDoubleClick()) {
            	System.out.println("is double click.!!!");
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }
	
	@Override
	protected void onPause() {
		super.onPause();
		// PgyFeedbackShakeManager.unregister();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// PgyFeedbackShakeManager.register(this, App.mAppId);
	}

}
