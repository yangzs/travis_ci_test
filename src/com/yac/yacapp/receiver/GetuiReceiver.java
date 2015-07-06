package com.yac.yacapp.receiver;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.igexin.sdk.PushConsts;
import com.yac.yacapp.R;
import com.yac.yacapp.activities.WelcomeActivity;
import com.yac.yacapp.icounts.ICounts;

public class GetuiReceiver extends BroadcastReceiver implements ICounts {

	protected static final String TAG = "GetuiReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		if(debug)Log.i("GetuiReceiver", "onReceiver is run.action:" + bundle.getInt("action"));
		switch (bundle.getInt(PushConsts.CMD_ACTION)) {
		case PushConsts.GET_MSG_DATA:// 个推的透传消息
			byte[] payload = bundle.getByteArray("payload");
			if (payload != null) {
				String data = new String(payload);
				if(debug)Log.i("GetuiReceiver", "data:" + data);
				showNotification(data, context);
			}
			break;
		case PushConsts.GET_CLIENTID:
			// 获取ClientID(CID)
			/*
			 * 第三方应用需要将ClientID上传到第三方服务器，并且将当前用户帐号和ClientID进行关联，
			 * 以便以后通过用户帐号查找ClientID进行消息推送
			 * 有些情况下ClientID可能会发生变化，为保证获取最新的ClientID，请应用程序在每次获取ClientID广播后
			 * ，都能进行一次关联绑定
			 */
			String cid = bundle.getString("clientid");
			if(debug)Log.i("GetuiReceiver", "Got ClientID:" + cid);
			break;
		default:
			break;
		}
	}


	private void showNotification(String data, Context context) {
		String message = data;
		try {
			JSONObject jsonObject = new JSONObject(data);
			message = jsonObject.getString("message");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.notification_bar);
		views.setImageViewResource(R.id.icon, R.drawable.icon1);
		views.setTextViewText(R.id.trackname, "养爱车");
		views.setTextViewText(R.id.artistalbum, message);
		Intent intent = new Intent(context, WelcomeActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
		mBuilder.setContent(views)
		 		.setContentIntent(pendingIntent)
				.setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
				.setTicker(message).setPriority(Notification.PRIORITY_MAX)// 设置该通知优先级
				.setDefaults(Notification.DEFAULT_ALL)
				.setOngoing(false).setSmallIcon(R.drawable.welcome_icon);
		Notification notification = mBuilder.build();
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		mNotificationManager.notify(1, notification);
	}
}
