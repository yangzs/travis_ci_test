package com.yac.yacapp.activities;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;

import com.igexin.sdk.PushManager;
import com.loopj.android.http.AsyncHttpClient;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;
import com.yac.yacapp.R;
import com.yac.yacapp.domain.CarUser;
import com.yac.yacapp.icounts.App;
import com.yac.yacapp.icounts.ICounts;
import com.yac.yacapp.utils.Utils;

public class WelcomeActivity extends Activity implements ICounts {

	private static final int START_MAIN_ACTIVITY = 0;
	private static final int START_VERIFY_ACTIVITY = 2;
	private long startTime;
	private AsyncHttpClient mClient;
	protected SharedPreferences mSP;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case START_MAIN_ACTIVITY:
				startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
				finish();
				break;
			case START_VERIFY_ACTIVITY:
				startActivity(new Intent(WelcomeActivity.this, VerifyActivity.class));
				finish();
				break;
			case FIRST_CHECK_USERINFO_WHAT:
				String content = (String) msg.obj;
				mSP.edit().putString(SP_UserInfo, content).commit();
				mHandler.sendEmptyMessageDelayed(START_MAIN_ACTIVITY, getLeftTime());
				break;
			case GT_CLIENTID_UPDATE_WHAT:
				// initNetData();
				App.needNetDate = true;
				mHandler.sendEmptyMessageDelayed(START_MAIN_ACTIVITY, getLeftTime());
				break;
			case PARSERJSON_FAILURE:
			case NET_FAILURE:
				mHandler.sendEmptyMessageDelayed(START_VERIFY_ACTIVITY, getLeftTime());
				break;
			default:
				break;
			}
		};
	};

	private long getLeftTime() {
		long time = 1500 - (System.currentTimeMillis() - startTime);
		if(debug)Log.i("welecome", "time:" + time);
		return time >= 0 ? time : 0;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_welcome, null); 
		setContentView(view);
		mSP = getSharedPreferences(SP_NAME, MODE_PRIVATE);
		startAnimation(view);
		startTime = System.currentTimeMillis();
		mClient = new AsyncHttpClient();
		initCarUser();
		PushManager.getInstance().initialize(this.getApplicationContext());
		PgyUpdateManager.register(this, App.mAppId, new UpdateManagerListener() {
			@Override
			public void onUpdateAvailable(String result) {
				if(debug)System.out.println("onUpdateAvailable is on.result:"+result);
				try {
					JSONObject jsonObject = new JSONObject(result);
					JSONObject object = jsonObject.optJSONObject("data");
					final String downloadUrl = object.optString("downloadURL");
					String releaseNote = object.optString("releaseNote");
					Dialog dialog;
					Builder builder = new AlertDialog.Builder(WelcomeActivity.this);
					builder.setIcon(R.drawable.yac_icon);
					builder.setTitle("更新提醒");
					builder.setMessage(releaseNote);
					builder.setPositiveButton("下载", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							startDownloadTask(WelcomeActivity.this,downloadUrl);
						}
					});
					builder.setNegativeButton("取消", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							registerClientId(WelcomeActivity.this);
						}
					});
					dialog = builder.create();
					dialog.show();
				} catch (JSONException e) {
					e.printStackTrace();
					registerClientId(WelcomeActivity.this);
				}
			}
			@Override
			public void onNoUpdateAvailable() {
				registerClientId(WelcomeActivity.this);
			}
		});
	}
	
	private void startAnimation(View view){
		AlphaAnimation start_anima = new AlphaAnimation(0.3f, 1.0f);
		start_anima.setDuration(2000);
		view.startAnimation(start_anima);
	}

	private void initCarUser() {
		App.mCarUser.user_id = mSP.getLong(CarUser.KEY_USER_ID, 0);
		App.mCarUser.gender = mSP.getString(CarUser.KEY_GENDER, "");
		App.mCarUser.phone = mSP.getString(CarUser.KEY_PHONE, "");
		App.mCarUser.token = mSP.getString(CarUser.KEY_TOKEN, "");
		App.mCarUser.sign_origin = mSP.getString(CarUser.KEY_SIGN_ORIGIN, "");
		App.mCarUser.verify_code = mSP.getString(CarUser.KEY_VERIFY_CODE, "");
		App.mCarUser.name = mSP.getString(CarUser.KEY_NAME, "");
	}

	private void initNetData() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("user_id", String.valueOf(App.mCarUser.user_id));
		Utils.get(this, mClient, USER_INFO_SUBURL, map, mHandler, FIRST_CHECK_USERINFO_WHAT, true);
	}

	private void registerClientId(Context context) {
		String clientid = PushManager.getInstance().getClientid(this);
		String cid = mSP.getString(SP_GT_CLIENTID, "");
		if (cid.equals(clientid)) {
			if (debug)
				Log.i("welcome", "clientid clientid is ok.");
			mHandler.sendEmptyMessage(GT_CLIENTID_UPDATE_WHAT);
		} else {
			if (debug)
				Log.i("welcome", "clientid clientid is changed.");
			mSP.edit().putString(SP_GT_CLIENTID, clientid).commit();
			Map<String, String> map = new HashMap<String, String>();
			map.put("phone", App.mCarUser.phone);
			map.put("client_id", clientid);
			map.put("device_type", "android");
			map.put("user_role", "keeper");
			Utils.post(context, mClient, GETUI_CLIENTID_UPDATE, map, mHandler, GT_CLIENTID_UPDATE_WHAT, true);
		}
	}

}
