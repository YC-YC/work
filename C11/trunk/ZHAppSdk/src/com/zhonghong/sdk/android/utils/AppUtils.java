/**
 * Copyright (c) 深圳市众鸿科技股份有限公司
 * @file_name AppUtils.java
 * @class com.zhonghong.framework.android.utils.AppUtils
 * @create 下午7:30:29
 */
package com.zhonghong.sdk.android.utils;

import java.io.File;

import com.zhonghong.sdk.android.ZHAppSdk;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.telephony.TelephonyManager;

/**
 * APP工具类
 * <p>
 * </p>
 * 下午7:30:29
 * 
 * @author ZH-SW-Lilc
 * @version 1.0.0
 */
public class AppUtils {

	/**
	 * 获取应用名称
	 * @return 应用名称
	 */
	public static String getApplicationName() {
		PackageManager packageManager = null;
		ApplicationInfo applicationInfo = null;
		String applicationName = "";
		try {
			packageManager = ZHAppSdk.mContext.getPackageManager();
			applicationInfo = packageManager.getApplicationInfo(
					ZHAppSdk.mContext.getPackageName(), 0);
			applicationName = (String) packageManager
					.getApplicationLabel(applicationInfo);
		} catch (PackageManager.NameNotFoundException e) {
			applicationInfo = null;
		}

		return applicationName;
	}

	/**
	 * 获取设备ID
	 * @return 设备IMEI
	 */
	public static String getDeviceId() {
		TelephonyManager telephonyManager = (TelephonyManager) ZHAppSdk.mContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();
	}

	/**
	 * 获取版本号
	 * @return 版本号
	 */
	public static int getVersionCode() {
		try {
			return ZHAppSdk.mContext.getPackageManager().getPackageInfo(
					ZHAppSdk.mContext.getPackageName(),
					PackageManager.GET_CONFIGURATIONS).versionCode;
		} catch (NameNotFoundException e) {
			return 0;
		}
	}

	/**
	 * 获取版本名称
	 * @return 版本名称
	 */
	public static String getVersionName() {
		try {
			return ZHAppSdk.mContext.getPackageManager().getPackageInfo(
					ZHAppSdk.mContext.getPackageName(),
					PackageManager.GET_CONFIGURATIONS).versionName;
		} catch (NameNotFoundException e) {
			return "";
		}
	}

	/**
	 * 获取APP目录
	 * @return APP目录URL
	 */
	public static String getAppPath() {
		// 判断是否有SD卡
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			String filePath = Environment.getExternalStorageDirectory() + "/"
					+ getApplicationName() + "/";
			File file = new File(filePath);
			if (!file.exists()) {
				file.mkdir();
			}
			return filePath;
		} else {
			return null;
		}
	}

	/**
	 * 获取Image目录
	 * @return Image目录URL
	 */
	public static String getImagePath() {
		if (StringUtils.isNotEmpty(getAppPath())) {
			String filePath = getAppPath() + "Image/";
			File file = new File(filePath);
			if (!file.exists()) {
				file.mkdir();
			}
			return filePath;
		} else {
			return null;

		}
	}

	/**
	 * 获取Cache目录
	 * @return Cache目录URL
	 */
	public static String getCachePath() {
		if (StringUtils.isNotEmpty(getAppPath())) {
			String filePath = getAppPath() + "Cache/";
			File file = new File(filePath);
			if (!file.exists()) {
				file.mkdir();
			}
			return filePath;
		} else {
			return null;

		}
	}

	/**
	 * 查看某程序是否已安装
	 * 
	 * @param packageName 程序的包名
	 * @return true:已安装;false:未安装
	 */
	public static boolean isInstall(String packageName) {
		if (packageName == null || "".equals(packageName))
			return false;
		try {
			ZHAppSdk.mContext.getPackageManager().getApplicationInfo(packageName,
					PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	/**
	 * 通过包名启动应用
	 * 
	 * @param packgeName 程序的包名
	 */
	public static void startAppByPackgeName(String packgeName) {
		PackageManager packageManager = ZHAppSdk.mContext.getPackageManager();
		Intent intent = packageManager.getLaunchIntentForPackage(packgeName);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ZHAppSdk.mContext.startActivity(intent);
	}

	/***
	 * 通过类名启动应用
	 * 
	 * @param className 类名（Activity类名）
	 */
	public static void startAppByClassName(Class<?> className) {
		Intent intent = new Intent(ZHAppSdk.mContext, className);
		ZHAppSdk.mContext.startActivity(intent);
	}

}
