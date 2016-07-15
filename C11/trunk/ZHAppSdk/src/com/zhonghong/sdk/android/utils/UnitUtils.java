/**
 * Copyright (c) 深圳市众鸿科技股份有限公司
 * @file_name UnitUtils.java
 * @class com.zhonghong.framework.android.utils.UnitUtils
 * @create 下午7:56:43
 */
package com.zhonghong.sdk.android.utils;

import java.math.BigDecimal;

/**
 * 存储空间单位转换
 * <p>
 * </p>
 * 下午7:56:43
 * 
 * @author ZH-SW-Lilc
 * @version 1.0.0
 */
public class UnitUtils {
	
	/***
	 * 单位转换。转换成带单位的字符串
	 * 
	 * @param data1 需要转换的数值
	 * @return 转换后的数值
	 */
	public static String changeUnit(long data1) {
		double data = (double) data1;
		if (data < 1024)
			return data + "B";
		else if (data >= 1024 && data < 1024 * 1024)
			return saveDouble(data / 1024, 2) + "KB";
		else if (data >= 1024 * 1024 && data < 1024 * 1024 * 1024)
			return saveDouble(data / 1024 / 1024, 2) + "MB";
		else if (data >= 1024 * 1024 * 1024)
			return saveDouble(data / 1024 / 1024 / 1024, 2) + "GB";
		return null;
	}

	/***
	 * 单位转换。转换成数值和单位的数组
	 * 
	 * @param data1 需要转换的数值
	 * @return 转换后的数组
	 */
	public static String[] changeUnitArray(long data1) {
		double data = (double) data1;
		String str[] = new String[2];
		if (data < 1024) {
			str[0] = String.valueOf(data);
			str[1] = "B";
		} else if (data >= 1024 && data < 1024 * 1024) {
			str[0] = String.valueOf(saveDouble(data / 1024, 2));
			str[1] = "KB";
		} else if (data >= 1024 * 1024 && data < 1024 * 1024 * 1024) {
			str[0] = String.valueOf(saveDouble(data / 1024 / 1024, 2));
			str[1] = "MB";
		} else if (data >= 1024 * 1024 * 1024) {
			str[0] = String.valueOf(saveDouble(data / 1024 / 1024 / 1024, 2));
			str[1] = "GB";
		}
		return str;
	}

	/***
	 * KB转换为MB。转换结果不带单位
	 * 
	 * @param data 需要转换的数值
	 * @return 转换后的结果
	 */
	public static double kb2mb(double data) {
		return data / 1024;
	}

	/**
	 * 保留X位小数
	 * 
	 * @param data 原始数据
	 * @param median 需要保留的小数位
	 * @return 转换后的结果
	 */
	public static double saveDouble(double data, int median) {
		BigDecimal bg = new BigDecimal(data);
		double processMemorySize = bg
				.setScale(median, BigDecimal.ROUND_HALF_UP).doubleValue();
		return processMemorySize;
	}
}
