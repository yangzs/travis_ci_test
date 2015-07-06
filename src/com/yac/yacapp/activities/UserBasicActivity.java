package com.yac.yacapp.activities;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.yac.yacapp.R;
import com.yac.yacapp.domain.UserBasic;
import com.yac.yacapp.domain.UserInfo;
import com.yac.yacapp.icounts.BaseActivity;
import com.yac.yacapp.icounts.ICounts;
import com.yac.yacapp.utils.Utils;
import com.yac.yacapp.utils.Utils2;
import com.yac.yacapp.views.MyProgressDialog;
import com.yac.yacapp.views.MyToast;

public class UserBasicActivity extends BaseActivity implements ICounts, OnClickListener {

	private final String MALE = "male"; 
	private final String FEMALE = "female"; 
	private static final int COMMIT_USERINFO = 0;
	
	private LinearLayout back_ll;
	private TextView back_tv, actionbar_title_tv;
	private ImageView close_img;
	private EditText name_et;
	private RadioGroup user_basic_radiogroup;
	private RadioButton man_radiobtn, women_radiobtn;
	private TextView user_phone_tv, warn_text_tv;
	private Button commit_bt;

	private Gson mGson;
	private UserBasic mUserBasic;
	private MyProgressDialog myProgressDialog;
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case COMMIT_USERINFO:
				String content = (String) msg.obj;
				parserJsonUpdateUserinfo(content);
				break;
			case PARSERJSON_SUCCESS:
				if (myProgressDialog != null && myProgressDialog.isShowing()) {
					myProgressDialog.dismiss();
				}
				finish();
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
		setContentView(R.layout.activity_userbasic);
		myProgressDialog = MyProgressDialog.createDialog(this);
		myProgressDialog.setMessage(getString(R.string.progress_msg));
		initView();
		initData();
	}

	private void initView() {
		findViewById(R.id.head_ll).setOnClickListener(mSoftInputOnClickListener);
		back_ll = (LinearLayout) findViewById(R.id.back_ll);
		back_tv = (TextView) findViewById(R.id.back_tv);
		actionbar_title_tv = (TextView) findViewById(R.id.actionbar_title_tv);
		close_img = (ImageView) findViewById(R.id.close_img);
		name_et = (EditText) findViewById(R.id.name_et);
		user_basic_radiogroup = (RadioGroup) findViewById(R.id.user_basic_radiogroup);
		man_radiobtn = (RadioButton) findViewById(R.id.man_radiobtn);
		women_radiobtn = (RadioButton) findViewById(R.id.women_radiobtn);
		user_phone_tv = (TextView) findViewById(R.id.user_phone_tv);
		warn_text_tv = (TextView) findViewById(R.id.warn_text_tv);
		commit_bt = (Button) findViewById(R.id.commit_bt);

		back_ll.setVisibility(View.VISIBLE);
		back_tv.setText("我");
		back_ll.setOnClickListener(this);
		close_img.setOnClickListener(this);
		actionbar_title_tv.setText("个人信息");
		warn_text_tv.setOnClickListener(this);
		commit_bt.setOnClickListener(this);
	}
	
	private void initData() {
		//主界面传UserBasic类过来
		mUserBasic = (UserBasic) getIntent().getSerializableExtra("UserBasic");
		if(mUserBasic != null){
			if(!TextUtils.isEmpty(mUserBasic.name)){
				name_et.setText(mUserBasic.name);
				name_et.setSelection(mUserBasic.name.length());
			}
			if(MALE.equals(mUserBasic.gender)){
				man_radiobtn.setChecked(true);
			}else{
				women_radiobtn.setChecked(true);
			}
			if(!TextUtils.isEmpty(mUserBasic.phone_number) && !"null".equals(mUserBasic.phone_number)){
				user_phone_tv.setText("电话："+mUserBasic.phone_number);
			}
		}else{
			mUserBasic = new UserBasic();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_ll:
			finish();
			break;
		case R.id.close_img:
			closeActivities();
			break;
		case R.id.warn_text_tv:
			try{
				Utils2.phoneCall(this, "4000102766");
			}catch (Exception e) {
				MyToast.makeText(getApplicationContext(), R.color.pay_color, "设备拨号异常", MyToast.LENGTH_SHORT).show();
			}
			break;
		case R.id.commit_bt:
			commitUserBasic();
			break;
		default:
			break;
		}
	}

	private void commitUserBasic() {
		String name_str = name_et.getText().toString().trim();
		if(TextUtils.isEmpty(name_str)){
			MyToast.makeText(this, R.color.pay_color, R.string.name_not_null, MyToast.MIDDLETIME).show();
			return;
		}
		myProgressDialog.show();
		A a = new A();
		a.id = mUserBasic.id;
		a.user_name = name_str;
		if(R.id.man_radiobtn == user_basic_radiogroup.getCheckedRadioButtonId()){
			a.gender = MALE; 
		}else{
			a.gender = FEMALE;
		}
		a.phone_number = mUserBasic.phone_number;
		a.sing_in_origin = "app";
		if(mGson == null)
			mGson = new Gson();
		String json_str = mGson.toJson(a);
		if(debug)System.out.println("json_str:"+json_str);
		Utils.post(this, new AsyncHttpClient(), META_USER_SUBURL, json_str, mHandler, COMMIT_USERINFO, true);
	}

	protected void parserJsonUpdateUserinfo(String content) {
		A a = mGson.fromJson(content, A.class);
		mUserBasic.name = a.user_name;
		mUserBasic.gender = a.gender;
		String userinfo_json = mSP.getString(SP_UserInfo, "");
		UserInfo userInfo = mGson.fromJson(userinfo_json, UserInfo.class);
		userInfo.basic = mUserBasic;
		userinfo_json = mGson.toJson(userInfo);
		mSP.edit().putString(SP_UserInfo, userinfo_json).commit();
		sendUpdateBroadcastReceiver();
		mHandler.sendEmptyMessage(PARSERJSON_SUCCESS);
	}
	
	class A{
		public Long id;
		public String user_name;
		public String gender;
		public String phone_number;
		public String sing_in_origin;//for update
	}
	
}
