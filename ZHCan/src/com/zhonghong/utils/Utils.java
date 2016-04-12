/**
 * 
 */
package com.zhonghong.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author YC
 * @time 2016-4-11 下午4:29:26
 */
public class Utils {
	/**Can服务启动广播*/
	public static final String ACTION_CAN_START_OK = "com.zhonghong.ACTION_CAN_START_OK";
	
	public static void sendBroadcast(Context context, String action){
		context.sendBroadcast(new Intent(action));
		Log.i("ZHBroadcast", "sendBroadcast action = " + action);
	}
}
