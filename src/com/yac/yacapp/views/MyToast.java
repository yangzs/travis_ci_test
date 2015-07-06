package com.yac.yacapp.views;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.yac.yacapp.R;

public class MyToast {

	private final static String TAG = "MyToast";
	public final static int HIDETOAST = 0;
	public final static long SHORTTIME = 1000;
	public final static long MIDDLETIME = 1500;
	public final static long LONGTIME = 2000;
	public final static long LENGTH_SHORT = 1500;

	private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
	private WindowManager mWM;
	private View mShowView;
	private long mDuration;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int what = msg.what;
			switch (what) {
			case HIDETOAST:
				hide();
				break;
			default:
				break;
			}
		}
	};

	public MyToast(Context context) {
		mWM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mParams.width = WindowManager.LayoutParams.MATCH_PARENT;
		mParams.format = PixelFormat.TRANSLUCENT;
		mParams.windowAnimations = R.style.mytoast_anim;
		mParams.type = WindowManager.LayoutParams.TYPE_TOAST;
		mParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
		mParams.x = 0;
		int height = mWM.getDefaultDisplay().getHeight();
		mParams.y = -height / 2;
	}

	public static MyToast makeText(Context context,  int bgColorId, CharSequence text, long duration) {
		MyToast result = new MyToast(context);
		
		LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflate.inflate(R.layout.mytoast, null);
		TextView tv = (TextView) v.findViewById(R.id.toast_msg);
		tv.setText(text);
		tv.setBackgroundColor(context.getResources().getColor(bgColorId));

		result.mShowView = v;
		result.mDuration = duration;

		return result;
	}

	public static MyToast makeText(Context context, int bgColorId, int resId, long duration) {
		try{
			return makeText(context, bgColorId, context.getResources().getText(resId), duration);
		}catch(Exception exception){
			exception.printStackTrace();
		}
		return null;
	}

	public void show() {
		if (mShowView.getParent() != null) {
			mWM.removeView(mShowView);
		}
		mWM.addView(mShowView, mParams);
		mHandler.sendEmptyMessageDelayed(HIDETOAST, mDuration);
	}

	public void hide() {
		if (mShowView != null) {
			if (mShowView.getParent() != null) {
				mWM.removeView(mShowView);
				mShowView = null;
			}
		}
	}

}
