/**
 * Copyright (c) 深圳市众鸿科技股份有限公司
 * @file_name ZSizeUtils.java
 * @class com.zhonghong.sdk.android.utils.ZSizeUtils
 * @create 上午11:42:59
 */
package com.zhonghong.sdk.android.utils;

import android.content.Context;

/**
 * 尺寸互转工具类
 * <p>将一种单位的尺寸转换为另一种单位尺寸，并保证大小不变</p>
 * 上午11:42:59
 * 
 * @author ZH-SW-Lilc
 * @version 1.0.0
 */
public class SizeUtils {
	/**
	 * 将px值转换为dip或dp值
	 * 
	 * @param context 上下文
	 * @param pxValue 需要转换的值
	 * @return 转换后的值
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 将dip或dp值转换为px值
	 * 
	 * @param context 上下文
	 * @param dipValue 需要转换的值
	 * @return 转换后的值
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * 将px值转换为sp值
	 * 
	 * @param context 上下文
	 * @param pxValue 需要转换的值
	 * @return 转换后的值
	 */
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * 将sp值转换为px值
	 * 
	 * @param context 上下文
	 * @param spValue 需要转换的值
	 * @return 转换后的值
	 */
	public static float sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (spValue * fontScale + 0.5f);
	}
}
