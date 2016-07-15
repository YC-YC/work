/**
 * Copyright (c) 深圳市众鸿科技股份有限公司
 * @file_name ZToast.java
 * @class com.zhonghong.framework.android.utils.ZToast
 * @create 下午6:29:46
 */
package com.zhonghong.sdk.android.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast封装类
 * <p>统一管理项目中的Toast</p>
 * 下午6:29:46
 *
 * @author ZH-SW-Lilc
 * @version 1.0.0
 */
public class ZToast {
	private static Toast mToast;

    /**
     * Toast短时间的提示
     * @param context 上下文
     * @param info
     *            需要显示的文字
     */
    public static void showShort(Context context,String info) {
        if (StringUtils.isNotEmpty(info)) {
            if (null == mToast) {
                mToast = Toast.makeText(context, info, Toast.LENGTH_SHORT);
            }
            mToast.setDuration(Toast.LENGTH_SHORT);
            mToast.setText(info);
            mToast.show();
        }
    }

    /**
     * Toast长时间的提示
     * @param context 上下文
     * @param info
     *            需要显示的文字
     */
    public static void showLong(Context context,String info) {
        if (StringUtils.isNotEmpty(info)) {
            if (null == mToast) {
                mToast = Toast.makeText(context, info, Toast.LENGTH_LONG);
            }
            mToast.setDuration(Toast.LENGTH_LONG);
            mToast.setText(info);
            mToast.show();
        }
    }
}
