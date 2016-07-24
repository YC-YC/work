/**
 * 
 */
package com.zhonghong.base;

import com.zhonghong.can.CanManager;
import com.zhonghong.sdk.android.ZHAppSdk;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

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
		bindCanService();
		ZHAppSdk.initSDK(this);
	}
	
	
	public void bindCanService(){
		CanManager.getInstace().bindCanService(this);
	}
	
}
