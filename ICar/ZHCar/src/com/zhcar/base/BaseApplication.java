/**
 * 
 */
package com.zhcar.base;

import android.app.Application;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.util.Log;

import com.yc.external.PostFromZUI;
import com.zhcar.carflow.CarFlowManager;
import com.zhcar.data.GlobalData;
import com.zhcar.provider.CarProviderData;
import com.zhcar.utils.Saver;
import com.zhcar.utils.UpdateUiManager;

/**
 * @author YC
 * @time 2016-7-20 上午11:56:49
 * TODO:
 */
public class BaseApplication extends Application {

	private static final String TAG = "ZhCarApplication";
	
	private static BaseApplication instanse;
	private PostFromZUI mPostFromZUI;
	
	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate");
		super.onCreate();
		instanse = this;
		GlobalData.mContext = this;
		mPostFromZUI = new PostFromZUI(this);
		CarFlowManager.getInstance(this);
		Saver.setEnvironment(GlobalData.bIsEnvTest);
	}
	
	public static BaseApplication getInstanse(){
		return instanse;
	}

}
