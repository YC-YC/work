/**
 * Copyright (c) 深圳市众鸿科技股份有限公司
 * @file_name AESUtils.java
 * @class com.zhonghong.sdk.android.utils.AESUtils
 * @create 下午12:09:28
 */
package com.zhonghong.sdk.android.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加密工具类
 * <p></p>
 * 下午12:09:28
 *
 * @author ZH-SW-Lilc
 * @version 1.0.0
 */
public class AESUtils {
	/**密钥（必须是16位的）*/
	private static final String keyBytes = "zhonghongappsdk6";
	private static final String algorithmStr = "AES/ECB/PKCS5Padding";
	
	/**
	 * 加密
	 * @param content 需要加密的字符串
	 * @return 加密后的字符串
	 */
	public static String encode(String content) {
		// 加密之后的字节数组,转成16进制的字符串形式输出
		return parseByte2HexStr(encrypt(content, keyBytes));
	}

	/**
	 * 解密
	 * @param content 加密后的字符串
	 * @return 解密后的字符串
	 */
	public static String decode(String content) {
		// 解密之前,先将输入的字符串按照16进制转成二进制的字节数组,作为待解密的内容输入
		byte[] b = decrypt(parseHexStr2Byte(content), keyBytes);
		return new String(b);
	}

	private static byte[] encrypt(String content, String password) {
		try {
			byte[] keyStr = getKey(password);
			SecretKeySpec key = new SecretKeySpec(keyStr, "AES");
			Cipher cipher = Cipher.getInstance(algorithmStr);// algorithmStr
			byte[] byteContent = content.getBytes("utf-8");
			cipher.init(Cipher.ENCRYPT_MODE, key);// ʼ
			byte[] result = cipher.doFinal(byteContent);
			return result; //
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static byte[] decrypt(byte[] content, String password) {
		try {
			byte[] keyStr = getKey(password);
			SecretKeySpec key = new SecretKeySpec(keyStr, "AES");
			Cipher cipher = Cipher.getInstance(algorithmStr);// algorithmStr
			cipher.init(Cipher.DECRYPT_MODE, key);// ʼ
			byte[] result = cipher.doFinal(content);
			return result; //
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static byte[] getKey(String password) {
		byte[] rByte = null;
		if (password != null) {
			rByte = password.getBytes();
		} else {
			rByte = new byte[24];
		}
		return rByte;
	}

	private static String parseByte2HexStr(byte buf[]) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}

	private static byte[] parseHexStr2Byte(String hexStr) {
		if (hexStr.length() < 1)
			return null;
		byte[] result = new byte[hexStr.length() / 2];
		for (int i = 0; i < hexStr.length() / 2; i++) {
			int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2),
					16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}

}
