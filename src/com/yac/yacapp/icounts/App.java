package com.yac.yacapp.icounts;

import java.io.File;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import android.app.Application;
import android.os.Environment;

import com.baidu.mapapi.SDKInitializer;
import com.pgyersdk.crash.PgyCrashManager;
import com.yac.yacapp.domain.Car;
import com.yac.yacapp.domain.CarUser;

public class App extends Application {
	public static Header mContent_type_Header;
	public static Header mDevice_type_Header;
	public static Header mApp_name_Header;
	public static Header mApp_version_Header;
	public static Header mApp_gzip_Header;
	public static CarUser mCarUser;
	public static String mAppId = "39d1cf2bd7ec269a30959dd8e0bfa616";//正青/*"f8e96a8178ee707079cfb4a7daaf1c00";*///自测//
	public static Car mCurrentCar;
	public static boolean needNetDate = false;
	public static File rootFile;
	@Override
	public void onCreate() {
		super.onCreate();
		mContent_type_Header = new BasicHeader("Content-Type", "application/json;charset=utf-8");
		mDevice_type_Header = new BasicHeader("API-Client-Device-Type", "enterprise_android");
		mApp_name_Header = new BasicHeader("API-Client-App-Name", "yangaiche");
		mApp_version_Header = new BasicHeader("API-Client-App-Version", "2.0.0");
		mApp_gzip_Header = new BasicHeader("Accept-Encoding", "gzip");
		mCarUser = new CarUser();
		PgyCrashManager.register(this,mAppId);
		SDKInitializer.initialize(this);
		
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			rootFile = new File(Environment.getExternalStorageDirectory().toString()+"/养爱车");
			if (!rootFile.exists()) {
				rootFile.mkdir();
			}
		}
	}
}
