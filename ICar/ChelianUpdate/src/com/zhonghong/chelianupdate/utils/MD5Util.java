package com.zhonghong.chelianupdate.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**生成字符串的MD5摘要，生成的MD5字符串中的字母均为小写。
 * @author: mike.ma
 * @Date: 2014/6/12
 */
public class MD5Util {
    public final static String md5(String s) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        byte[] inputBytes = s.getBytes("UTF-8");
        MessageDigest mdInst = MessageDigest.getInstance("md5");
        mdInst.update(inputBytes);
        byte[] md = mdInst.digest();
        int j = md.length;
        char str[] = new char[j * 2];
        int k = 0;
        for (int i = 0; i < j; i++) {
            byte byte0 = md[i];
            str[k++] = hexDigits[byte0 >>> 4 & 0xf];
            str[k++] = hexDigits[byte0 & 0xf];
        }
        return new String(str);
    }
}
