package com.yac.yacapp.activities;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.yac.yacapp.R;
import com.yac.yacapp.domain.CarUser;
import com.yac.yacapp.icounts.App;
import com.yac.yacapp.icounts.BaseActivity;
import com.yac.yacapp.icounts.ICounts;
import com.yac.yacapp.utils.Utils;
import com.yac.yacapp.views.MyProgressDialog;
import com.yac.yacapp.views.MyToast;

@SuppressWarnings("deprecation")
public class VerifyActivity extends BaseActivity implements ICounts, OnClickListener {

	private static final int GET_VERIFYCODE = 0;
	private static final int SIGN_UP = 1;
	public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
	private ImageView mBack_img;
	private TextView mTitle;
	private EditText mPhone_num_et, mVerify_code_et;
	private Button mGetcode_bt, mVerify_bt;

	private AsyncHttpClient mClient;
	private MyProgressDialog myProgressDialog;
	private SMSBroadcastReceiver mReceiver;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GET_VERIFYCODE:// 成功，进行下一步
				if (myProgressDialog != null && myProgressDialog.isShowing()) {
					myProgressDialog.dismiss();
				}
				break;
			case SIGN_UP:// 验证成功
				String content = (String) msg.obj;
				parserSignUpJson(content);
				break;
			case PARSERJSON_FAILURE:
			case NET_FAILURE:
				if (myProgressDialog != null && myProgressDialog.isShowing()) {
					myProgressDialog.dismiss();
				}
				String message = (String) msg.obj;
				handlePARSERJSON_NET_FAILURE(message);
				handler.removeMessages(0);
				handler.sendEmptyMessage(1);
				break;
			default:
				break;
			}
		};
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_verify);
		mClient = new AsyncHttpClient();
		myProgressDialog = MyProgressDialog.createDialog(this);
		myProgressDialog.setMessage(getString(R.string.progress_msg));
		initView();
		initData();
		registerBroadcast();
	}

	private void initData() {
		TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
		String phontenum = telephonyManager.getLine1Number();
		if (!TextUtils.isEmpty(phontenum)) {
			String sub_str = phontenum.substring(0, 3);
			Log.i("TAG", "sub_str=" + sub_str);
			if ("+86".equals(sub_str)) {
				String phone = phontenum.substring(3);
				mPhone_num_et.setText(phone);
				mPhone_num_et.setSelection(phone.length());
			}
		}
	}

	private void initView() {
		findViewById(R.id.head_ll).setOnClickListener(mSoftInputOnClickListener);
		mBack_img = (ImageView) findViewById(R.id.back_img);
		mTitle = (TextView) findViewById(R.id.actionbar_title_tv);
		mPhone_num_et = (EditText) findViewById(R.id.phone_num_et);
		mVerify_code_et = (EditText) findViewById(R.id.ver_code_et);
		mGetcode_bt = (Button) findViewById(R.id.getcode_bt);
		mVerify_bt = (Button) findViewById(R.id.verify_bt);
		findViewById(R.id.close_img).setVisibility(View.GONE);
		
		mBack_img.setVisibility(View.GONE);
		mBack_img.setOnClickListener(this);
		mTitle.setText("验证");
		mGetcode_bt.setOnClickListener(this);
		mVerify_bt.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_img:
			finish();
			break;
		case R.id.getcode_bt:
			// 发送请求，获取验证码
			requestCode();
			break;
		case R.id.verify_bt:
			verifyPhoneNum();
			break;
		default:
			break;
		}
	}

	private void verifyPhoneNum() {
		String verify_code = mVerify_code_et.getText().toString().trim();
		if (TextUtils.isEmpty(verify_code)) {
			MyToast.makeText(this, R.color.pay_color, getString(R.string.verify_code_null), MyToast.LENGTH_SHORT).show();
			return;
		}
		if (mReceiver != null) {
			unregisterReceiver(mReceiver);
			mReceiver = null;
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("phone", mPhone_num_et.getText().toString().trim());
		map.put("verify_code", verify_code);
		map.put("sign_origin", "app");
		Utils.post(this, mClient, SIGN_UP_SUBURL, map, mHandler, SIGN_UP, true);
	}

	protected void parserSignUpJson(String content) {
		try {
			JSONObject jsonObject = new JSONObject(content);
			App.mCarUser.user_id = jsonObject.optLong(CarUser.KEY_USER_ID);
			App.mCarUser.gender = jsonObject.optString(CarUser.KEY_GENDER);
			App.mCarUser.phone = jsonObject.optString(CarUser.KEY_PHONE);
			App.mCarUser.token = jsonObject.optString(CarUser.KEY_TOKEN);
			App.mCarUser.sign_origin = jsonObject.optString(CarUser.KEY_SIGN_ORIGIN);
			App.mCarUser.verify_code = jsonObject.optString(CarUser.KEY_VERIFY_CODE);
			App.mCarUser.name = jsonObject.optString(CarUser.KEY_NAME);
			Editor edit = mSP.edit();
			edit.putLong(CarUser.KEY_USER_ID, App.mCarUser.user_id);
			edit.putString(CarUser.KEY_PHONE, App.mCarUser.phone);
			edit.putString(CarUser.KEY_TOKEN, App.mCarUser.token);
			edit.putString(CarUser.KEY_SIGN_ORIGIN, App.mCarUser.sign_origin);
			edit.putString(CarUser.KEY_VERIFY_CODE, App.mCarUser.verify_code);
			edit.putString(CarUser.KEY_GENDER, App.mCarUser.gender);
			edit.putString(CarUser.KEY_NAME, App.mCarUser.name);
			edit.commit();
			if (myProgressDialog != null && myProgressDialog.isShowing()) {
				myProgressDialog.dismiss();
			}
			App.needNetDate = true;
			// 打开主界面
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			finish();
		} catch (JSONException e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(PARSERJSON_FAILURE);
		}
	}

	private void requestCode() {
		String phoneNum = mPhone_num_et.getText().toString().trim();
		if (TextUtils.isEmpty(phoneNum)) {
			MyToast.makeText(this, R.color.pay_color, "电话号码不能为空", MyToast.LENGTH_SHORT).show();
			return;
		}
		startCount();
		myProgressDialog.show();
		Map<String, String> map = new HashMap<String, String>();
		Utils.get(this, mClient, SIGN_VERIFY_SUBURL + "/" + phoneNum, map, mHandler, GET_VERIFYCODE);
	}

	private void registerBroadcast() {
		if (mReceiver == null)
			mReceiver = new SMSBroadcastReceiver();
		IntentFilter filter = new IntentFilter(SMS_RECEIVED_ACTION);
		filter.setPriority(Integer.MAX_VALUE);
		registerReceiver(mReceiver, filter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mReceiver != null) {
			unregisterReceiver(mReceiver);
			mReceiver = null;
		}
	}

	private int count;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				mGetcode_bt.setText(count + "秒");
				count--;
				if (count >= 0) {
					handler.sendEmptyMessageDelayed(0, 1000);
				} else {
					handler.sendEmptyMessage(1);
				}
				break;
			case 1:
				mGetcode_bt.setClickable(true);
				mGetcode_bt.setText("获取验证码");
				mGetcode_bt.setBackgroundResource(R.drawable.bg_buttom_normal);
				break;
			default:
				break;
			}
		};
	};

	private void startCount() {
		count = 60;
		mGetcode_bt.setClickable(false);
		mGetcode_bt.setBackgroundResource(R.drawable.bg_buttom_pressed);
		handler.sendEmptyMessage(0);
	};

	private class SMSBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i("TAG", "短信来了");
			if (intent.getAction().equals(SMS_RECEIVED_ACTION)) {
				Object[] pdus = (Object[]) intent.getExtras().get("pdus");
				for (Object pdu : pdus) {
					SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
					String sender = smsMessage.getDisplayOriginatingAddress();
					String content = smsMessage.getMessageBody();
					Log.i("TAG", "sender:" + sender + ",content:" + content);
					// if(DefaultPhoneNum.equals(sender)){
					String[] strings = content.split("：");
					if (strings.length == 2) {
						String sub_str = strings[1].substring(0, 6);
						try {
							if (Integer.valueOf(sub_str) != null)
								mVerify_code_et.setText(sub_str);
						} catch (NumberFormatException e) {

						}
					}
					// }
				}
			}
		}
	}
}
