package com.yac.yacapp.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.yac.yacapp.activities.MainActivity;

public class Utils2 {

	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	/**
	 * call to customer
	 */
	public static void phoneCall(Context context, String number) throws Exception{
		// Intent intent = new Intent();
		// intent.setAction(Intent.ACTION_CALL);
		// intent.setData(Uri.parse("tel:" + number));
		// context.startActivity(intent);
		Intent intent1 = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
		intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent1);
	}

	public static void sendSms(Context context, String number) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SENDTO);
		intent.setData(Uri.parse("smsto:" + number));
		context.startActivity(intent);
	}

	public static void goHome(Context context) {
		Intent intent = new Intent(context, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(intent);
	}

	public static String[] getDateFromUTC(String utcTime) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
			Date dt = sdf.parse(utcTime);
			// dt.UTC(year, month, day, hour, minute, second)
			Calendar c = sdf.getCalendar();
			StringBuffer buffer = new StringBuffer();
			String time = buffer.append(c.get(Calendar.HOUR_OF_DAY)).append(":").append(c.get(Calendar.MINUTE)).toString();
			StringBuffer buffer1 = new StringBuffer();
			String date = buffer1.append((c.get(Calendar.MONTH) + 1)).append("/").append(c.get(Calendar.DAY_OF_MONTH)).toString();
			return new String[] { time, date };
		} catch (Exception pe) {
			pe.printStackTrace();
		}
		return null;
	}

	public static int getMonths(String utcTime) {
		//System.out.println("getMonths is run.utcTime:"+utcTime);
		try {
			Calendar currentCalender = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
			Date dt = sdf.parse(utcTime);
			Calendar utcCalendar = sdf.getCalendar();
			//System.out.println("getMonths is run.currentCalender:"+currentCalender+",utcCalendar:"+utcCalendar);
			if (currentCalender.equals(utcCalendar) || utcCalendar.after(currentCalender))
				return 0;
			if (currentCalender.get(Calendar.YEAR) >= utcCalendar.get(Calendar.YEAR)){//年份相比
				//System.out.println("getMonths is run.");
				return ((currentCalender.get(Calendar.YEAR) - utcCalendar.get(Calendar.YEAR)) * 12 + currentCalender.get(Calendar.MONTH)) - utcCalendar.get(Calendar.MONTH);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static String createImageName() {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_.jpg";
		return imageFileName;
	}

	public boolean isNetworkConnected(Context context) {
		// 判断网络是否连接
		ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		if (mNetworkInfo != null) {
			return mNetworkInfo.isAvailable();
		}
		return false;
	}
	
    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return  dp * scale + 0.5f;
    }

    public static float sp2px(Resources resources, float sp){
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }
    
    public static long lastClickTime;
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (timeD >= 0 && timeD <= 500) {
            return true;
        } else {
            lastClickTime = time;
            return false;
        }
    }
}
