/**
 * Copyright (c) 深圳市众鸿科技股份有限公司
 * @file_name AssetsUtils.java
 * @class com.zhonghong.framework.android.utils.AssetsUtils
 * @create 下午8:14:19
 */
package com.zhonghong.sdk.android.utils;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Assets目录工具类
 * <p></p>
 * 下午8:14:19
 *
 * @author ZH-SW-Lilc
 * @version 1.0.0
 */
public class AssetsUtils {
	
	/**
	 * 获取assetsPath下的自定义字体
	 * @param context 上下文
	 * @param assetsPath 字体URL
	 * @return 字体对象
	 */
	public static Typeface getCustomTypeface(Context context,String assetsPath){
		return Typeface.createFromAsset(context.getAssets(), assetsPath);
	}

}
