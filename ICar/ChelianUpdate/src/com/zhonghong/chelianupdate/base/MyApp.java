/**
 * Copyright (c) 深圳市众鸿科技股份有限公司
 * @file_name MyApp.java
 * @class com.zhonghong.chelianupdate.MyApp
 * @create 下午2:13:46
 */
package com.zhonghong.chelianupdate.base;


import java.io.File;

import com.zhonghong.sdk.android.ZHAppSdk;
import com.zhonghong.sdk.android.utils.AppManager;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class MyApp extends Application{
	public static Context mContext;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("Update","App create");
		mContext = getApplicationContext();
		ZHAppSdk.initSDK(mContext);
	}
	
	/**
	 * 显示打开activity，并传递bundle参数
	 * @param className 要打开的activity
	 * @param bundle 参数
	 */
	public static void startActivity(Context context,Class<?> className,Bundle bundle){
		Intent intent = new Intent(mContext,className);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		intent.putExtra("bundle", bundle);
		context.startActivity(intent);
	}
	
	@Override
    public void onTerminate() {
        AppManager.getAppManager().finishAllActivity();
        super.onTerminate();
    }
	
}
