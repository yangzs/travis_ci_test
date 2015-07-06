package com.yac.yacapp.utils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yac.yacapp.R;
import com.yac.yacapp.activities.VerifyActivity;
import com.yac.yacapp.icounts.App;
import com.yac.yacapp.icounts.ICounts;

public class Utils implements ICounts {

	/**
	 * 不带token请求
	 */
	public static String get(final Context context, AsyncHttpClient client, String suburl, Map<String, String> map, final Handler handler, int what) {
		return get(context, client, suburl, map, handler, what, false);
	}

	/**
	 * 是否使用token的get请求
	 * 
	 * @param context
	 *            上下文
	 * @param client
	 *            asynchttpclient
	 * @param suburl
	 *            请求路径
	 * @param map
	 *            params
	 * @param handler
	 *            处理的handler
	 * @param what
	 * @param flag
	 *            是否带token
	 * @return
	 */
	public static String get(final Context context, AsyncHttpClient client, String suburl, Map<String, String> map, final Handler handler, final int what, boolean flag) {
		return get(context, client, suburl, map, handler, what, flag, false);
	}

	/**
	 * 如果用到V2的借口的话，用这个
	 * 
	 * @param context
	 * @param client
	 * @param suburl
	 * @param map
	 * @param handler
	 * @param what
	 * @param flag
	 * @param useV2
	 * @return
	 */
	public static String get(final Context context, AsyncHttpClient client, String suburl, Map<String, String> map, final Handler handler, final int what, boolean flag, boolean useV2) {
		Header[] headers = null;
		if (flag) {
			Header header = new BasicHeader("API-Access-Token", App.mCarUser.token);
			headers = new Header[] { App.mContent_type_Header, App.mDevice_type_Header, App.mApp_name_Header, App.mApp_version_Header, header };
		} else {
			headers = new Header[] { App.mContent_type_Header, App.mDevice_type_Header, App.mApp_name_Header, App.mApp_version_Header };
		}
		RequestParams params = new RequestParams(map);
		String url = useV2 ? ROOTURL_V2 + suburl : ROOTURL + suburl;
		client.get(context, url, headers, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] bytes) {
				String content = new String(bytes);
				if (debug)
					Log.i("Utils", "content=" + content);
				parserContent(context, content, handler, what);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] bytes, Throwable error) {
				handlerOnFailure(statusCode, bytes, handler);
			}
		});
		return null;
	}

	/**
	 * 使用测试连接的get请求
	 */
	public static String get(final Context context, AsyncHttpClient client, String suburl, Map<String, String> map, final Handler handler, final int what, boolean flag, String testUrl) {
		Header[] headers = null;
		if (flag) {
			Header header = new BasicHeader("API-Access-Token", App.mCarUser.token);
			headers = new Header[] { App.mContent_type_Header, App.mDevice_type_Header, App.mApp_name_Header, App.mApp_version_Header, header };
		} else {
			headers = new Header[] { App.mContent_type_Header, App.mDevice_type_Header, App.mApp_name_Header, App.mApp_version_Header };
		}
		RequestParams params = new RequestParams(map);
		String url;
		if (TextUtils.isEmpty(testUrl)) {
			url = ROOTURL + suburl;
		} else {
			url = testUrl;
		}
		client.get(context, url, headers, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] bytes) {
				String content = new String(bytes);
				if (debug)
					Log.i("Utils", "content=" + content);
				parserContent(context, content, handler, what);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] bytes, Throwable error) {
				handlerOnFailure(statusCode, bytes, handler);
			}
		});
		return null;
	}

	public static String post(final Context context, AsyncHttpClient client, String suburl, Map<String, String> map, final Handler handler, final int what, boolean flag) {
		Header[] headers = null;
		if (flag) {
			headers = new Header[] { App.mContent_type_Header, App.mDevice_type_Header, App.mApp_name_Header, App.mApp_version_Header, new BasicHeader("API-Access-Token", App.mCarUser.token) };
		} else {
			headers = new Header[] { App.mContent_type_Header, App.mDevice_type_Header, App.mApp_name_Header, App.mApp_version_Header };
		}
		String url = ROOTURL + suburl;
		try {
			JSONObject jsonObject = new JSONObject(map);
			if (debug)
				System.out.println(jsonObject.toString());
			HttpEntity entity = new StringEntity(jsonObject.toString(), "utf-8");
			client.post(context, url, headers, entity, null, new AsyncHttpResponseHandler() {

				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] bytes) {
					String content = new String(bytes);
					if (debug)
						Log.i("Utils", "content=" + content);
					parserContent(context, content, handler, what);
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] bytes, Throwable error) {
					handlerOnFailure(statusCode, bytes, handler);
				}
			});

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 使用string数据更新借口
	 * 
	 * @param context
	 * @param client
	 * @param suburl
	 * @param json_str
	 * @param handler
	 * @param what
	 * @param flag
	 * @return
	 */
	public static String post(final Context context, AsyncHttpClient client, String suburl, String json_str, final Handler handler, final int what, boolean flag) {
		return post(context, client, suburl, json_str, handler, what, flag, false);
	}

	/**
	 * 是否使用v2借口更新数据
	 * 
	 * @param context
	 * @param client
	 * @param suburl
	 * @param json_str
	 * @param handler
	 * @param what
	 * @param flag
	 * @param useV2
	 * @return
	 */
	public static String post(final Context context, AsyncHttpClient client, String suburl, String json_str, final Handler handler, final int what, boolean flag, boolean useV2) {
		Header[] headers = null;
		if (flag) {
			headers = new Header[] { App.mContent_type_Header, App.mDevice_type_Header, App.mApp_name_Header, App.mApp_version_Header, new BasicHeader("API-Access-Token", App.mCarUser.token) };
		} else {
			headers = new Header[] { App.mContent_type_Header, App.mDevice_type_Header, App.mApp_name_Header, App.mApp_version_Header };
		}
		String url = useV2 ? ROOTURL_V2 + suburl : ROOTURL + suburl;
		try {
			HttpEntity entity = new StringEntity(json_str, "utf-8");
			client.post(context, url, headers, entity, null, new AsyncHttpResponseHandler() {

				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] bytes) {
					String content = new String(bytes);
					if (debug)
						Log.i("Utils", "content=" + content);
					if (what == ZFB_CHARGE_WHAT) {
						Message msg = handler.obtainMessage();
						msg.obj = content;
						msg.what = what;
						handler.sendMessage(msg);
						return;
					}
					parserContent(context, content, handler, what);
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] bytes, Throwable error) {
					handlerOnFailure(statusCode, bytes, handler);
				}
			});

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected static void handlerOnFailure(int statusCode, byte[] bytes, Handler handler) {
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append("code:").append(statusCode).append(" ");
			Message msg = handler.obtainMessage();
			if (bytes != null && bytes.length > 0) {
				buffer.append(new String(bytes));
			}else{
				buffer.append("联网失败");
			}
			msg.obj = buffer.toString();
			msg.what = NET_FAILURE;
			handler.sendMessage(msg);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private static void parserContent(final Context context, String content, final Handler handler, int what) {
		try {
			JSONObject jsonObject = new JSONObject(content);
			String code = jsonObject.optString("code");
			if ("20007".equals(code)) {// 无效的凭证
				if (what == FIRST_CHECK_USERINFO_WHAT || what == GT_CLIENTID_UPDATE_WHAT) {
					handler.sendEmptyMessage(PARSERJSON_FAILURE);
					return;
				}
				AlertDialog.Builder builder = new Builder(context, R.style.tipsdialog_style);
				builder.setTitle("登陆失效");
				builder.setMessage("登陆失效，请重新进行短信验证登陆！");
				builder.setCancelable(false);
				builder.setPositiveButton("重新登陆", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						handler.sendEmptyMessage(PARSERJSON_FAILURE);
						dialog.dismiss();
						Intent intent = new Intent(context, VerifyActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(intent);
					}
				});
				builder.setNegativeButton("取消", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Message msg = handler.obtainMessage();
						msg.obj = "无效的凭证";
						msg.what = PARSERJSON_FAILURE;
						handler.sendMessage(msg);
					}
				});
				builder.show();
			} else if ("00000".equals(code)) {
				Message msg = handler.obtainMessage();
				msg.obj = jsonObject.optString("data");
				msg.what = what;
				handler.sendMessage(msg);
			} else {
				// 获取信息出错
				Message msg = handler.obtainMessage();
				msg.obj = code +" "+ jsonObject.optString("message");
				msg.what = PARSERJSON_FAILURE;
				handler.sendMessage(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
			handler.sendEmptyMessage(PARSERJSON_FAILURE);
		}
	}

	/**
	 * 更新sp中的userinfo的json数据
	 * 
	 * @param context
	 * @param client
	 * @param handler
	 * @param what
	 *            UPDATE_USERINFO_WHAT
	 */
	public static void updateUserInfoJson(Context context, AsyncHttpClient client, Handler handler) {
		if (client == null)
			client = new AsyncHttpClient();
		Map<String, String> map = new HashMap<String, String>();
		map.put("user_id", String.valueOf(App.mCarUser.user_id));
		Utils.get(context, client, USER_INFO_SUBURL, map, handler, UPDATE_USERINFO_WHAT, true);
	}

}
