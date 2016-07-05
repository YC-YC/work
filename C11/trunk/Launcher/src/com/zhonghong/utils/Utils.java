/**
 * 
 */
package com.zhonghong.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * @author YC
 * @time 2016-4-11 上午10:04:49
 */
public class Utils {

	/**视频包名，类名*/
	public static final String ZH_RADIO_PKG = "com.zh.radio";
	public static final String ZH_RADIO_CLZ = "com.zh.radio.ui.MainActivity";
	
	
	/**视频包名，类名*/
	public static final String ZH_VIDEO_PKG = "com.zhonghong.zhvideo";
	public static final String ZH_VIDEO_CLZ = "com.zhcl.zhvideo.LocalVideoActivity";
	/**音乐包名，类名*/
	public static final String ZH_AUDIO_PKG = "com.zh.ui";
	public static final String ZH_AUDIO_CLZ = "com.zh.ui.media.activity.MediaListActivity";
	/**音乐2包名，类名*/
	public static final String ZH_AUDIO2_PKG = "com.zhonghong.music2";
	public static final String ZH_AUDIO2_CLZ = "com.zh.ui.media.activity.MediaListActivity";
	/**USB包名，类名*/
	public static final String ZH_USB_PKG = "com.zhonghong.usb.media";
	public static final String ZH_USB_CLZ = "com.zhonghong.usb.media.UsbMainActivity";
	
	
	
	/**图片包名，类名*/
	public static final String ZH_PHOTO_PKG = "com.zhonghong.newphoto";
	public static final String ZH_PHOTO_CLZ = "com.zhonghong.media.photo.PhotoLauncher";
	/**蓝牙音乐包名，类名*/
	public static final String ZH_BTMUSIC_PKG = "com.zhonghong.bt";
	public static final String ZH_BTMUSIC_CLZ = "com.zhonghong.bt.ui.BtAudioActivity";
	
	/**蓝牙包名，类名*/
	public static final String ZH_BTPHONE_PKG = "com.zhonghong.bluetooth.phone";
	public static final String ZH_BTPHONE_CLZ = "com.zhonghong.bluetooth.phone.ui.activity.MainActivity";
	
	/**蓝牙包名，类名*/
	public static final String ZH_SETTINGS_PKG = "com.zhonghong.settings";
	public static final String ZH_SETTINGS_CLZ = "com.zhonghong.settings.SettingsActivity";
	
	/** 打开应用方法 */
	public static boolean startOtherActivity(Context context, String pkgName, String className){
		try {
			Intent it = new Intent(Intent.ACTION_MAIN); 
			ComponentName cn = new ComponentName(pkgName, className);              
			it.setComponent(cn);  
			it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK/* | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED*/);
			context.startActivity(it);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
}
