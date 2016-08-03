/**
 * 
 */
package com.zhcar.utils;

import java.util.HashMap;
import java.util.Iterator;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * @author YC
 * @time 2016-7-12 下午2:44:26
 * TODO:
 */
public class Utils {
	
	/**购买流量包名，类名*/
	public static final String ZH_BUY_PKG = "com.kaiyi.tservicebuy";
	public static final String ZH_BUY_CLZ = "com.kaiyi.tservicebuy.MainActivity";
	
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
