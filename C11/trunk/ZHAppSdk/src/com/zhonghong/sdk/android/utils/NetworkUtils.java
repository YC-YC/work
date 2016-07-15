/**
 * Copyright (c) 深圳市众鸿科技股份有限公司
 * @file_name NetworkUtils.java
 * @class com.zhonghong.framework.android.utils.NetworkUtils
 * @create 下午7:20:07
 */
package com.zhonghong.sdk.android.utils;

import com.zhonghong.sdk.android.ZHAppSdk;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络工具类
 * <p>
 * </p>
 * 下午7:20:07
 * 
 * @author ZH-SW-Lilc
 * @version 1.0.0
 */
public class NetworkUtils {

	/**
	 * 判断网络是否有效.
	 * 
	 * @return true:有效;false:无效
	 */
	public static boolean isNetworkAvailable() {
		try {
			ConnectivityManager connectivity = (ConnectivityManager) ZHAppSdk.mContext
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	/**
	 * 判断是否为wifi
	 * 
	 * @return true:Wifi;false:其他
	 */
	public static boolean isWifi() {
		// 得到网络连接信息
		ConnectivityManager manager = (ConnectivityManager) ZHAppSdk.mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo.State gprs = manager.getNetworkInfo(
				ConnectivityManager.TYPE_MOBILE).getState();
		NetworkInfo.State wifi = manager.getNetworkInfo(
				ConnectivityManager.TYPE_WIFI).getState();
		if (gprs == NetworkInfo.State.CONNECTED
				|| gprs == NetworkInfo.State.CONNECTING) {
			return false;
		}
		if (wifi == NetworkInfo.State.CONNECTED
				|| wifi == NetworkInfo.State.CONNECTING) {
			return true;
		}
		return true;
	}
}
