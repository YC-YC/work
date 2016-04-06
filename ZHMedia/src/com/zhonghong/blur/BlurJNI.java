/**
 * 
 */
package com.zhonghong.blur;

import android.graphics.Bitmap;

/**
 * 高斯模糊jni计算
 * @author 陈立
 * @date 2015-10-18 上午1:29:27
 */
public class BlurJNI {
	public static native void blurIntArray(int[] pImg, int w, int h, int r);
	public static native void blurBitMap(Bitmap bitmap, int r);
	static {
		System.loadLibrary("JNI_ImageBlur");
	}
}
