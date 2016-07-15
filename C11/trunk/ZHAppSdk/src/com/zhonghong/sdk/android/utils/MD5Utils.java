/**
 * Copyright (c) 深圳市众鸿科技股份有限公司
 * @file_name MD5Utils.java
 * @class com.zhonghong.sdk.android.utils.MD5Utils
 * @create 下午12:01:26
 */
package com.zhonghong.sdk.android.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密工具类
 * <p>
 * </p>
 * 下午12:01:26
 * 
 * @author ZH-SW-Lilc
 * @version 1.0.0
 */
public class MD5Utils {
	/** 全局数组 */
	private final static String[] strDigits = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	/***
	 * 加密字符串
	 * 
	 * @param strObj
	 *            需要加密的字符串
	 * @return 加密后的字符串
	 */
	public static String encrypt(String strObj) {
		String resultString = null;
		try {
			resultString = new String(strObj);
			MessageDigest md = MessageDigest.getInstance("MD5");
			// md.digest() 该函数返回值为存放哈希值结果的byte数组
			resultString = byteToString(md.digest(strObj.getBytes()));
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		}
		return resultString;
	}

	private static String byteToArrayString(byte bByte) {
		int iRet = bByte;
		if (iRet < 0) {
			iRet += 256;
		}
		int iD1 = iRet / 16;
		int iD2 = iRet % 16;
		return strDigits[iD1] + strDigits[iD2];
	}

	private static String byteToString(byte[] bByte) {
		StringBuffer sBuffer = new StringBuffer();
		for (int i = 0; i < bByte.length; i++) {
			sBuffer.append(byteToArrayString(bByte[i]));
		}
		return sBuffer.toString();
	}

}
