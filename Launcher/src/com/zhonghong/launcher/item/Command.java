/**
 * 
 */
package com.zhonghong.launcher.item;

import android.content.Context;

/**
 * @author YC
 * @time 2016-4-11 上午9:51:55
 */
public interface Command {
	/**打开应用,正常打开返回true*/
	boolean execute(Context context);
}
