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
	/**图片包名，类名*/
	public static final String ZH_PHOTO_PKG = "com.zhonghong.newphoto";
	public static final String ZH_PHOTO_CLZ = "com.zhonghong.media.photo.PhotoLauncher";
	
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
