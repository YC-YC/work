/**
 * Copyright (c) 深圳市众鸿科技股份有限公司
 * @file_name ZHAppSdk.java
 * @class com.zhonghong.sdk.android.utils.ZHAppSdk
 * @create 上午10:30:04
 */
package com.zhonghong.sdk.android;

import android.content.Context;

/**
 * SDK主类
 * <p></p>
 * 上午10:30:04
 *
 * @author ZH-SW-Lilc
 * @version 1.0.0
 */
public class ZHAppSdk {
	public static Context mContext;
	
	/**
	 * 初始化SDK
	 * @param context 上下文
	 */
	public static void initSDK(Context context){
		mContext = context;
	}
	
}
