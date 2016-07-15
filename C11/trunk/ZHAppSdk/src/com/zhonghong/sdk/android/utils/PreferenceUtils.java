/**
 * Copyright (c) 深圳市众鸿科技股份有限公司
 * @file_name PreferenceUtils.java
 * @class com.zhonghong.framework.android.utils.PreferenceUtils
 * @create 下午7:11:56
 */
package com.zhonghong.sdk.android.utils;

import com.zhonghong.sdk.android.ZHAppSdk;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * SharedPreferences封装类
 * <p>
 * </p>
 * 下午7:11:56
 * 
 * @author ZH-SW-Lilc
 * @version 1.0.0
 */
public class PreferenceUtils {
	/*public static Context mContext;
	private static SharedPreferences settings;

	public PreferenceUtils(Context context) {
		mContext = context;
		settings = PreferenceManager
				.getDefaultSharedPreferences(mContext);
	}*/

	private static SharedPreferences settings = PreferenceManager
			.getDefaultSharedPreferences(ZHAppSdk.mContext);

	/**
	 * 存储String类型数据
	 * @param key 键
	 * @param value 值
	 */
	public static void putString(String key, String value) {
		settings.edit().putString(key, value).commit();
	}

	/**
	 * 获取String类型数据
	 * @param key 键
	 * @param defaultValue 默认值
	 * @return 值
	 */
	public static String getString(String key, String defaultValue) {
		return settings.getString(key, defaultValue);
	}
	
	/**
	 * 存储boolean类型数据
	 * @param key 键
	 * @param value 值
	 */
	public static void putBoolean(String key, boolean value) {
		settings.edit().putBoolean(key, value).commit();
	}

	/**
	 * 获取boolean类型数据
	 * @param key 键
	 * @param defaultValue 默认值
	 * @return 值
	 */
	public static boolean getBoolean(String key, boolean defaultValue) {
		return settings.getBoolean(key, defaultValue);
	}

	/**
	 * 存储int类型数据
	 * @param key 键
	 * @param value 值
	 */
	public static void putInt(String key, int value) {
		settings.edit().putInt(key, value).commit();
	}

	/**
	 * 获取int类型数据
	 * @param key 键
	 * @param defaultValue 默认值
	 * @return 值
	 */
	public static int getInt(String key, int defaultValue) {
		return settings.getInt(key, defaultValue);
	}

	/**
	 * 存储float类型数据
	 * @param key 键
	 * @param value 值
	 */
	public static void putFloat(String key, float value) {
		settings.edit().putFloat(key, value).commit();
	}

	/**
	 * 获取float类型数据
	 * @param key 键
	 * @param defaultValue 默认值
	 * @return 值
	 */
	public static float getFloat(String key, float defaultValue) {
		return settings.getFloat(key, defaultValue);
	}

	/**
	 * 存储long类型数据
	 * @param key 键
	 * @param value 值
	 */
	public static void putLong(String key, long value) {
		settings.edit().putLong(key, value).commit();
	}

	/**
	 * 获取long类型数据
	 * @param key 键
	 * @param defaultValue 默认值
	 * @return 值
	 */
	public static long getLong(String key, long defaultValue) {
		return settings.getLong(key, defaultValue);
	}

	/**
	 * 判断Preference中是否有键
	 * @param key 键
	 * @return true:有;false:无
	 */
	public static boolean hasKey(String key) {
		return PreferenceManager.getDefaultSharedPreferences(ZHAppSdk.mContext)
				.contains(key);
	}

	/**
	 * 清除当前的Preferences数据
	 */
	public static void clearPreference() {
		Editor editor = settings.edit();
		editor.clear();
		editor.commit();
	}
}
