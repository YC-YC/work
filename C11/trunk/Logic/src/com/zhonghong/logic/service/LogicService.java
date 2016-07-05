/**
 * 
 */
package com.zhonghong.logic.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.zhonghong.logic.IMain;
import com.zhonghong.logic.MainLogic;

/**
 * @author YC
 * @time 2016-6-22 上午10:16:31
 * Logic服务,通过服务启动整个Logic逻辑控制
 */
public class LogicService extends Service {

	private static final String TAG = "LogicService";

	private IMain mMain;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	//只在服务创建的时候调用
	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate");
		super.onCreate();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				mMain = new MainLogic(LogicService.this);
				mMain.onCreate();
			}
		}, "logic_thread").start();
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
		if (mMain != null)
			mMain.onDestroy();
	}

	@Override
	public void onLowMemory() {
		Log.i(TAG, "onLowMemory");
		super.onLowMemory();
	}

	

}
