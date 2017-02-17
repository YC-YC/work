/**
 * 
 */
package com.zhonghong.mediascanner;

import android.app.Application;
import android.util.Log;

/**
 * 
 * @author YC
 * @time 2016-12-10 下午12:18:40
 * TODO:
 */
public class BaseApplication extends Application {

	private static final String TAG = "ZhCarApplication";
	
	private static BaseApplication instanse;
	
	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate");
		super.onCreate();
		instanse = this;
	}
	
	public static BaseApplication getApplication(){
		return instanse;
	}
	
	

}
