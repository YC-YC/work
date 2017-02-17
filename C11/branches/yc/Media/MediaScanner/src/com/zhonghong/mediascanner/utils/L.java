/**
 * 
 */
package com.zhonghong.mediascanner.utils;

import java.util.HashMap;
import java.util.Map;

import android.os.SystemClock;
import android.util.Log;

/**
 */
public class L {
	private static final String TAG = "L";
	static boolean d = true;
	static boolean e = true; 
	static boolean i = true;
	static boolean w = true;

	static {
		d = true;
		w = true;
	}

	public static void d(String tag, String str) {
		if (d)
			Log.d(tag, str);
	}

	public static void e(String tag, String str) {
		if (e)
			Log.e(tag, str);
		eDoSomeThing();
	}

	private static void eDoSomeThing() {
	}
 
	public static void i(String tag, String str) {
		if (i)
			Log.i(tag, str);
	}

	public static void w(String tag, String str) {
		if (w)
			Log.w(tag, str);
	}
	
private static Map<String, Long> timeRecord;	
	
	/**
	 * 打印开始时间
	 * @param tip
	 */
	public static void startTime(String tip){
		if (timeRecord == null)
		{
			timeRecord = new HashMap<String, Long>();
		}
		timeRecord.put(tip, SystemClock.uptimeMillis());
	}
	
	/**
	 * 结束打印时间
	 * @param tip
	 */
	public static void endUseTime(String tip){
		if (timeRecord != null && !timeRecord.containsKey(tip))
		{
			Log.i(TAG, "未配置开始时间");
			return;
		}
		
		long diff = SystemClock.uptimeMillis() - timeRecord.get(tip);
		timeRecord.remove(tip);
		Log.i(TAG, tip + "耗时：" + diff);
	}
}
