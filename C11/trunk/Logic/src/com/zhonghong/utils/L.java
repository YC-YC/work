/**
 * 
 */
package com.zhonghong.utils;

import java.util.HashMap;

import android.os.SystemClock;
import android.util.Log;

/**
 * @author YC
 * @time 2016-7-2 上午10:28:21
 * TODO:Log管理
 */
public class L {
	
	private static final String TAG = "LogUtils";
	
	private static HashMap<String, Long> mTimeRecord;
	
	public static void startTime(String paramString) {
		if (mTimeRecord == null){
			mTimeRecord = new HashMap<String, Long>();
		}
		mTimeRecord.put(paramString, Long.valueOf(SystemClock.uptimeMillis()));
	}

	public static void endUseTime(String paramString) {
		if ((mTimeRecord == null) || (!mTimeRecord.containsKey(paramString))) {
			Log.e(TAG, paramString + " 错误 ： 未设置起始时间！");
			return;
		}
		long l = SystemClock.uptimeMillis() - ((Long) mTimeRecord.get(paramString)).longValue();
		mTimeRecord.remove(paramString);
		Log.i(TAG, paramString + " 消耗时间 ： " + l);
	}
	
}
