/**
 * 
 */
package com.zhonghong.chelianupdate.utils;

import android.provider.Settings;

import com.zhonghong.chelianupdate.base.MyApp;
import com.zhonghong.sdk.android.utils.PreferenceUtils;

/**
 * @author Administrator
 * @time 2016-9-7 下午2:33:18
 * TODO 保存全局数据
 */
public class Saver {

	/** 稍后升级次数 */
	public static final String KEY_DELAY_UPGRADE_TIMES = "DelayUpdateTimes";
	/** 下载状态 */
	public static final String KEY_DOWNLOAD_STATE = "updatedownload";
	/**默认状态*/
	public static final String DOWNLOAD_STATE_DEFAULT = "default";
	/**开始下载*/
	public static final String DOWNLOAD_STATE_BEGIN = "begin";
	/**下载完成*/
	public static final String DOWNLOAD_STATE_FINISH = "finish";
	
	/** 升级状态 */
	public static final String KEY_INSTALL_STATE = "InstallState";
	/**默认状态*/
	public static final String INSTALL_STATE_DEFAULT = "default";
	/**开始升级*/
	public static final String INSTALL_STATE_BEGIN = "begin";
	/**取消升级，点击三次稍后后取消升级*/
	public static final String INSTALL_STATE_CANCEL = "cancel";
	/**升级完成*/
	public static final String INSTALL_STATE_FINISH = "finished";

	/**
	 * 稍后升级次数
	 * @return
	 */
	public static int getDelayUpgradeTimes(){
		return PreferenceUtils.getInt(KEY_DELAY_UPGRADE_TIMES, 0);
	}
	
	public static void setDelayUpgradeTimes(int times){
		PreferenceUtils.putInt(KEY_DELAY_UPGRADE_TIMES, times);
	}
	
	public static String getInstallState(){
		return PreferenceUtils.getString(KEY_INSTALL_STATE, INSTALL_STATE_DEFAULT);
	}
	
	public static void setInstallState(String state){
		PreferenceUtils.putString(KEY_INSTALL_STATE, state);
	}
	
	public static String getDownloadState(){
		String result = Settings.System.getString(MyApp.mContext.getContentResolver(), KEY_DOWNLOAD_STATE);
		return result == null ? DOWNLOAD_STATE_DEFAULT:result;
//		return PreferenceUtils.getString(KEY_DOWNLOAD_STATE, DOWNLOAD_STATE_DEFAULT);
	}
	
	public static void setDownloadState(String state){
		Settings.System.putString(MyApp.mContext.getContentResolver(), KEY_DOWNLOAD_STATE, state);
//		PreferenceUtils.putString(KEY_DOWNLOAD_STATE, state);
	}
}
