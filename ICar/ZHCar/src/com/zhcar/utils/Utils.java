/**
 * 
 */
package com.zhcar.utils;

import java.util.HashMap;
import java.util.Iterator;

import android.content.Context;
import android.content.Intent;

/**
 * @author YC
 * @time 2016-7-12 下午2:44:26
 * TODO:
 */
public class Utils {
	/**发送广播*/
	public static void sendBroadcast(Context context, String action, HashMap<String, String> extras){
		Intent it = new Intent();
		if (action != null)
			it.setAction(action);
		if (extras != null){
			Iterator<String> iterator = extras.keySet().iterator();
			while(iterator.hasNext()){
				String key = iterator.next();
				it.putExtra(key, extras.get(key));
			}
		}
		context.sendBroadcast(it);
	}
	
	/**
	 * 判断字符串是否为空
	 * 
	 * @param string 需要判断的字符串
	 * @return true:空;false:非空
	 */
	public static boolean isEmpty(String string) {
		return ((string == null) || (string.length() == 0));
	}
}
