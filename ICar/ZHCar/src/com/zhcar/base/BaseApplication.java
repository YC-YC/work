/**
 * 
 */
package com.zhcar.base;

import android.app.Application;
import android.util.Log;

import com.yc.external.PostFromZUI;
import com.zhcar.carflow.CarFlowManager;
import com.zhcar.data.GlobalData;
import com.zhcar.utils.GPRSManager;
import com.zhcar.utils.Utils;

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
	}
	
	public static BaseApplication getInstanse(){
		return instanse;
	}
	

}
