/**
 * Copyright (c) 深圳市众鸿科技股份有限公司
 * @file_name ZUtils.java
 * @class com.zhonghong.sdk.android.utils.ZUtils
 * @create 上午11:30:59
 */
package com.zhonghong.sdk.android.utils;

/**
 * 一些零散的工具类
 * <p>
 * </p>
 * 上午11:30:59
 * 
 * @author ZH-SW-Lilc
 * @version 1.0.0
 */
public class ZUtils {
	/** 最后点击按钮的时间 */
	private static long lastClickTime;
	/** 双击时间间隔 */
	private static final long INTERVAL = 500;

	/**
	 * 判断按钮是否为连续点击（Button,ImageButton等）
	 * 点击按钮的时间间隔够不够0.5秒
	 * @return true:连续点击;false:非连续点击
	 */
	public static boolean isDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < INTERVAL) {
			return true;
		}
		lastClickTime = time;
		return false;
	}
}
