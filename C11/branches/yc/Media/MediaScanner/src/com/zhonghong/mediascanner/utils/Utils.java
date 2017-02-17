/**
 * 
 */
package com.zhonghong.mediascanner.utils;

import android.content.Intent;

import com.zhonghong.mediascanerlib.ScannerConst;
import com.zhonghong.mediascanner.BaseApplication;

/**
 * @author YC
 * @time 2016-12-26 下午2:18:56
 * TODO:
 */
public class Utils {

	/**
	 * 发送扫描广播
	 * @param key
	 * @param val
	 */
	public static void sendScannerBraodcast(String key, String val){
		Intent intent = new Intent(ScannerConst.SBroadcast_ACTION);
		intent.putExtra(key, val);
		BaseApplication.getApplication().sendBroadcast(intent);
	}
	
	/**
	 * 扫描开始
	 * @param device
	 */
	public static void sendStartScanBroadcast(String device){
		sendScannerBraodcast(ScannerConst.SBroadcast_START_SCAN, device);
	}
	
	/**
	 * 扫描结束
	 * @param device
	 */
	public static void sendFinishScanBroadcast(String device){
		sendScannerBraodcast(ScannerConst.SBroadcast_FINISH_SCAN, device);
	}
}
