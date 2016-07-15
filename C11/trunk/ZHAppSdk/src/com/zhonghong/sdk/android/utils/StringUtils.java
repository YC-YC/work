/**
 * Copyright (c) 深圳市众鸿科技股份有限公司
 * @file_name StringUtils.java
 * @class com.zhonghong.framework.android.utils.StringUtils
 * @create 下午7:03:40
 */
package com.zhonghong.sdk.android.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串校验类
 * <p>
 * </p>
 * 下午7:03:40
 * 
 * @author ZH-SW-Lilc
 * @version 1.0.0
 */
public class StringUtils {

	/**
	 * 判断字符串是否为空
	 * 
	 * @param string 需要判断的字符串
	 * @return true:空;false:非空
	 */
	public static boolean isEmpty(String string) {
		return ((string == null) || (string.length() == 0));
	}

	/**
	 * 判断字符串是否不为空
	 * 
	 * @param string 需要判断的字符串
	 * @return true:非空;false:空
	 */
	public static boolean isNotEmpty(String string) {
		return string != null && string != "null" && string.length() > 0;
	}

	/**
	 * 判断字符串是否为数字
	 * 
	 * @param str 需要判断的字符串
	 * @return true:数字;false:非数字
	 */
	public static boolean isNumeric(String str) {
		if (str.matches("\\d*")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 校验手机格式
	 * 
	 * @param mobiles 需要判断的字符串
	 * @return true:手机号;false:非手机号
	 */
	public static boolean isMobile(String mobiles) {
		if (isNotEmpty(mobiles)) {
			// 总结起来就是第一位必定为1，其他位置的可以为0-9
			String telRegex = "[1]\\d{10}";// "[1]"代表第1位为数字1，"\\d{10}"代表后面是可以是0～9的数字，有10位。
			return mobiles.matches(telRegex);
		} else {
			return false;
		}

	}

	/**
	 * 电子邮件校验
	 * 
	 * @param str 需要判断的字符串
	 * @return true:邮箱;false:非邮箱
	 */
	public static boolean isEmail(String str) {
		if (isNotEmpty(str)) {
			String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(str);
			return matcher.matches();
		} else {
			return false;
		}
	}

	/**
	 * 检查是否为车牌号
	 * 
	 * @param carnumber 需要判断的字符串
	 * @return true:车牌号;false:非车牌号
	 */
	public static boolean isCarnumber(String carnumber) {
		/*
		 * 车牌号格式：汉字 + A-Z + 5位A-Z或0-9 （只包括了普通车牌号，教练车和部分部队车等车牌号不包括在内）
		 */
		String carnumRegex = "[\u4e00-\u9fa5]{1}[A-Z]{1}[A-Z_0-9]{5}";
		if (isEmpty(carnumber))
			return false;
		else
			return carnumber.matches(carnumRegex);
	}
}
