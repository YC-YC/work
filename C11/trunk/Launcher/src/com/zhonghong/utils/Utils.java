/**
 * 
 */
package com.zhonghong.utils;

import java.util.HashMap;
import java.util.Iterator;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * @author YC
 * @time 2016-4-11 上午10:04:49
 */
public class Utils {

	/**收音机包名，类名*/
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
	public static final String ZH_BTMUSIC_PKG = "com.zhonghong.bluetooth";
	public static final String ZH_BTMUSIC_CLZ = "com.zhonghong.bluetooth.music.ui.MainActivity";
	/**蓝牙包名，类名*/
	public static final String ZH_BTPHONE_PKG = "com.zhonghong.bluetooth";
	public static final String ZH_BTPHONE_CLZ = "com.zhonghong.bluetooth.phone.ui.activity.MainActivity";
	/**设置包名，类名*/
	public static final String ZH_SETTINGS_PKG = "com.zhonghong.settings";
	public static final String ZH_SETTINGS_CLZ = "com.zhonghong.settings.SettingsActivity";
	/**导航包名，类名*/
	public static final String ZH_NAVI_PKG = " ";
	public static final String ZH_NAVI_CLZ = " ";
	/**商城包名，类名*/
	public static final String ZH_APPSTORE_PKG = "com.yesway.c11.store";
	public static final String ZH_APPSTORE_CLZ = "com.yesway.c11.store.MainActivity";
	/**一键呼叫包名，类名*/
	public static final String ZH_ICALL_PKG = "com.yesway.c11.icall";
	public static final String ZH_ICALL_CLZ = "com.yesway.c11.icall.MainActivity";
	/**语音记事本包名，类名*/
	public static final String ZH_RECORDER_PKG = "com.yesway.c11.recorder";
	public static final String ZH_RECORDER_CLZ = "com.yesway.c11.recorder.activity.MainActivity";
	/**用户包名，类名*/
	public static final String ZH_USER_PKG = " ";
	public static final String ZH_USER_CLZ = " ";
	/**Carlife包名，类名*/
	public static final String ZH_CARLIFE_PKG = "com.zhonghong.carlife";
	public static final String ZH_CARLIFE_CLZ = "com.zhonghong.carlife.MainActivity";
	/**扩展包名，类名*/
	public static final String ZH_EXTEND_PKG = "com.zhonghong.expandapps";
	public static final String ZH_EXTEND_CLZ = "com.zhonghong.expandapps.ui.activity.MainActivity";
	/**LED包名，类名*/
	public static final String ZH_LED_PKG = "com.zhonghong.ledscreen";
	public static final String ZH_LED_CLZ = "com.zhonghong.ledscreen.LedScreenMainActivity";
	
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
	
	
	/**发送广播*/
	public static void sendBroadcast(Context context, String action, HashMap<String, String> extras){
		Intent it = new Intent();
		if (action != null)
			it.setAction(action);
		if (extras != null){
			Iterator<String> iterator = extras.keySet().iterator();
			while(iterator.hasNext()){
				String key = iterator.next();
				it.putExtra(key, extras.get(key));
			}
		}
		context.sendBroadcast(it);
	}
}
