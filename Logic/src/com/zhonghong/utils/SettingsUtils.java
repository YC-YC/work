/**
 * 
 */
package com.zhonghong.utils;

import android.app.Instrumentation;

/**
 * @author YC
 * @time 2016-7-2 下午2:16:42
 * TODO:设置相关
 */
public class SettingsUtils {

	/**
	 * 发送系统按键
	 * @param keycode = KeyEvent.keycode
	 */
	public static void sendSyskey(final int keycode){
		new Thread(new Runnable() {
			@Override
			public void run() {
				new Instrumentation().sendKeyDownUpSync(keycode);
			}
		}).start();
	}
}
