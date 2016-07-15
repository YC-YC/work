/**
 * Copyright (c) 深圳市众鸿科技股份有限公司
 * @file_name ZLog.java
 * @class com.zhonghong.framework.android.utils.ZLog
 * @create 下午7:01:42
 */
package com.zhonghong.sdk.android.utils;

import com.zhonghong.sdk.android.ZHAppSdk;

import android.util.Log;

/**
 * Log工具类
 * <p>原生Log类基础上扩展了Log开关及对包名、类名、行号打印</p>
 * 下午7:01:42
 *
 * @author ZH-SW-Lilc
 * @version 1.0.0
 */
public class ZLog {
	/**打印开关（测试环境打开，正式环境关闭）*/
	public static boolean PRINTLOG = true;
	private static String TAG;

	private final static int logLevel = Log.VERBOSE;

	private static ZLog mCustomLog;

	private ZLog() {

	}

	public static ZLog getLog() {
		if (mCustomLog == null) {
			mCustomLog = new ZLog();
		}
		return mCustomLog;
	}
	
	/**
	 * 等级为i的日志
	 * 
	 * @param str 日志信息
	 */
	public static void i(Object str) {
		getLog();
		if (PRINTLOG) {
			if (logLevel <= Log.INFO) {
				String name = getFunctionName();
				if (name != null) {
					Log.i(TAG, name + " - " + str);
				} else {
					Log.i(TAG, str.toString());
				}
			}
		}
	}

	/**
	 * 等级为d的日志
	 * 
	 * @param str 日志信息
	 */
	public static void d(Object str) {
		getLog();
		if (PRINTLOG) {
			if (logLevel <= Log.DEBUG) {
				String name = getFunctionName();
				if (name != null) {
					Log.d(TAG, name + " - " + str);
				} else {
					Log.d(TAG, str.toString());
				}
			}
		}
	}

	/**
	 * 等级为v的日志
	 * 
	 * @param str 日志信息
	 */
	public static void v(Object str) {
		getLog();
		if (PRINTLOG) {
			if (logLevel <= Log.VERBOSE) {
				String name = getFunctionName();
				if (name != null) {
					Log.v(TAG, name + " - " + str);
				} else {
					Log.v(TAG, str.toString());
				}
			}
		}
	}

	/**
	 * 等级为w的日志
	 * 
	 * @param str 日志信息
	 */
	public static void w(Object str) {
		getLog();
		if (PRINTLOG) {
			if (logLevel <= Log.WARN) {
				String name = getFunctionName();
				if (name != null) {
					Log.w(TAG, name + " - " + str);
				} else {
					Log.w(TAG, str.toString());
				}
			}
		}
	}

	/**
	 * 等级为e的普通日志
	 * 
	 * @param str 日志信息
	 */
	public static void e(Object str) {
		getLog();
		if (PRINTLOG) {
			if (logLevel <= Log.ERROR) {
				String name = getFunctionName();
				if (name != null) {
					Log.e(TAG, name + " - " + str);
				} else {
					Log.e(TAG, str.toString());
				}
			}
		}
	}

	/**
	 * 输出异常日志。一般用于try catch时
	 * @param e
	 */
	public static void doException(Exception e) {
		if (PRINTLOG) {
			Log.e(TAG, e.toString());
		}
	}

	private static String getFunctionName() {
		StackTraceElement[] sts = Thread.currentThread().getStackTrace();
		if (sts == null) {
			return null;
		}
		for (StackTraceElement st : sts) {
			if (st.isNativeMethod()) {
				continue;
			}
			if (st.getClassName().equals(Thread.class.getName())) {
				continue;
			}
			if (st.getClassName().equals(getLog().getClass().getName())) {
				continue;
			}
			TAG = ZHAppSdk.mContext.getPackageName() + "---" + st.getFileName();
			return "[ " + Thread.currentThread().getName() + ": " + st.getFileName() + ":" + st.getLineNumber() + " "
					+ st.getMethodName() + " ]";
		}
		return null;
	}

	/**
	 * 等级为e的错误日志
	 * 
	 * @param ex 日志信息
	 */
	public void e(Exception ex) {
		if (PRINTLOG) {
			if (logLevel <= Log.ERROR) {
				Log.e(TAG, "error", ex);
			}
		}
	}

	/**
	 * 等级为e的详细日志
	 * 
	 * @param log 日志信息
	 * @param tr 异常信息
	 */
	public void e(String log, Throwable tr) {
		if (PRINTLOG) {
			String line = getFunctionName();
			Log.e(TAG, "{Thread:" + Thread.currentThread().getName() + "}" + "[" + line + ":] " + log + "\n", tr);
		}
	}
}
