/**
 * 
 */
package com.zh.uitls;

import android.util.Log;

/**
 * @author zhonghong.chenli date:2015-6-12上午9:36:29 <br/>
 */
public class L {
	static boolean d = true;
	static boolean e = true; 
	static boolean i = true;
	static boolean w = true;

	static {
		d = true;
		w = true;
	}

	public static void d(String tag, String str) {
		if (d)
			Log.d(tag, str);
	}

	public static void e(String tag, String str) {
		if (e)
			Log.e(tag, str);
		eDoSomeThing();
	}

	private static void eDoSomeThing() {
	}
 
	public static void i(String tag, String str) {
		if (i)
			Log.i(tag, str);
	}

	public static void w(String tag, String str) {
		if (w)
			Log.w(tag, str);
	}
}
