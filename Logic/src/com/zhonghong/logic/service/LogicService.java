/**
 * 
 */
package com.zhonghong.logic.service;

import com.zhonghong.logic.record.RecordAppManager;
import com.zhonghong.logic.record.RecordInfoBean;
import com.zhonghong.logic.utils.AppUtils;

import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * @author YC
 * @time 2016-6-22 上午10:16:31
 * Logic服务
 */
public class LogicService extends Service {

	private static final String TAG = "LogicService";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	//只在服务创建的时候调用
	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate");
		super.onCreate();
		RecordInfoBean appCellInfo = RecordAppManager.getInstaces(this).getRecordInfo();
		RunningTaskInfo topAppInfo = AppUtils.getTopAppInfo(this);
		if (topAppInfo.topActivity.getPackageName() != appCellInfo.getLastPkgName() ||
				topAppInfo.topActivity.getClassName() != appCellInfo.getLastClassName())
		AppUtils.startOtherActivity(this, appCellInfo.getLastPkgName(), appCellInfo.getLastClassName());
	}

	//每次调用StartService都会调用
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "onStartCommand");
		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy");
		super.onDestroy();
	}

	@Override
	public void onLowMemory() {
		Log.i(TAG, "onLowMemory");
		super.onLowMemory();
	}

	

}
