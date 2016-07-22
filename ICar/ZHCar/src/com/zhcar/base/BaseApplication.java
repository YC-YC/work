/**
 * 
 */
package com.zhcar.base;

import com.zhcar.carflow.CarFlowManager;
import com.zhcar.provider.CarProviderData;

import android.app.Application;
import android.content.ContentResolver;
import android.database.ContentObservable;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

/**
 * @author YC
 * @time 2016-7-20 上午11:56:49
 * TODO:
 */
public class BaseApplication extends Application {

	private static final String TAG = "ZhCarApplication";
	
	private ContentResolver resolver;
	
	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate");
		super.onCreate();
		CarFlowManager.getInstance(this);
	}
	

}
