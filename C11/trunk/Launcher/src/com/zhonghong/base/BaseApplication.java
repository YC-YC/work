/**
 * 
 */
package com.zhonghong.base;

import android.app.Application;
import android.util.Log;

import com.zhonghong.sdk.android.ZHAppSdk;

/**
 * @author YC
 * @time 2016-4-9 上午9:41:43
 */
public class BaseApplication extends Application {


	private String TAG = getClass().getSimpleName();
	
	private static BaseApplication instanse;
	
	public static BaseApplication getInstanse(){
		return instanse;
	}
	
	@Override
	public void onCreate() {
		Log.i(TAG , "onCreate");
		super.onCreate();
		instanse = this;
		ZHAppSdk.initSDK(this);
	}
	
}
