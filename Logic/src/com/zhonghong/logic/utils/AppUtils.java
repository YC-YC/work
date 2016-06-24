/**
 * 
 */
package com.zhonghong.logic.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author YC
 * @time 2016-6-22 上午11:23:31
 * TODO:
 */
public class AppUtils {

	private static final String TAG = "AppUtils";

	/** 打开应用方法 */
	public static boolean startOtherActivity(Context context, String pkgName, String className){
		try {
			Intent it = new Intent(Intent.ACTION_MAIN); 
			ComponentName cn = new ComponentName(pkgName, className);              
			it.setComponent(cn);  
			it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK/* | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED*/);
			context.startActivity(it);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean isServiceRunning(Context context, String className){
		
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> services = manager.getRunningServices(30);
		if (services == null){
			return false;
		}
		
//		Log.i(TAG, "get service size = " + services.size() + ", className = " + className);
		for (RunningServiceInfo service : services) {
//			Log.i(TAG, "service.getClass() = " + service.service.getClassName());
			if (service.service.getClassName().equals(className)){
				return true;
			}
		}
		return false;
	}
	
	
	//获取顶层包名
	public static RunningTaskInfo getTopAppInfo(Context context) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//			List<RunningAppProcessInfo> processes = manager.getRunningAppProcesses();
//			for (RunningAppProcessInfo process : processes) {
//				Log.i(TAG, "get RunningApp = pkg = " + process.processName);
//			}
		List<RunningTaskInfo> tasks = manager.getRunningTasks(1);

		if (tasks == null)
			return null;
	/*	
		for (RunningTaskInfo task: tasks)
		{
			Log.i(TAG, "get tasks pkg = " + task.toString() + " pkg = " + task.topActivity.getPackageName() + ", clazz = " + task.topActivity.getClassName());
		}*/
		return tasks.get(0);
	}
}
