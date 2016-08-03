/**
 * 
 */
package com.zhcar.base;

import android.app.Application;
import android.util.Log;

import com.yc.external.GetAppRecordInfo;
import com.zhcar.carflow.CarFlowManager;
import com.zhcar.data.GlobalData;

/**
 * @author YC
 * @time 2016-7-20 上午11:56:49
 * TODO:
 */
public class BaseApplication extends Application {

	private static final String TAG = "ZhCarApplication";
	
	private static BaseApplication instanse;
	private GetAppRecordInfo mGetAppRecordInfo;
	
	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate");
		super.onCreate();
		instanse = this;
		GlobalData.mContext = this;
		mGetAppRecordInfo = new GetAppRecordInfo(this);
		CarFlowManager.getInstance(this);
	}
	
	public static BaseApplication getInstanse(){
		return instanse;
	}
	

}
