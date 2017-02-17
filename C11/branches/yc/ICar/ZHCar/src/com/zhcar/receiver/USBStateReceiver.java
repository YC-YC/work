/**
 * 
 */
package com.zhcar.receiver;

import java.io.File;

import com.zhcar.readnumber.IUSBStateChange;
import com.zhcar.readnumber.ReadExcelManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * @author YC
 * @time 2016-8-1 下午3:54:10
 * TODO:USB状态广播
 */
public class USBStateReceiver extends BroadcastReceiver{

	private static final String TAG = "USBStateReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		final String action = intent.getAction();
		Log.i(TAG, "Received a broadcast: " + action);
		// USB设备已插入
		if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
			String path = getChangePath(intent);
			if (path != null){
				onMounted(path);
			}
		}
		// USB设备拔出
		else if (Intent.ACTION_MEDIA_EJECT.equals(action)) {
			String path = getChangePath(intent);
			if (path != null){
				onUnmounted(path);
			}
		}
	}
	
	private void onUnmounted(String path) {
		Log.i(TAG, "onUnmounted");
		ReadExcelManager.getInstance().onUnmounted(path);
	}

	private void onMounted(String path) {
		Log.i(TAG, "onMounted");
		ReadExcelManager.getInstance().onMounted(path);
	}
	
	private String getChangePath(Intent intent){
		final Uri uri = intent.getData();
		String path = null;
		if (uri != null) {
			path = uri.getPath();
			Log.i(TAG, "path: " + path);
			if (!TextUtils.isEmpty(path)) {
				return path+File.separator;
            }
		}
		return null;
	}

}
