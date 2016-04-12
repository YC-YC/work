/**
 * 
 */
package com.zhonghong.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zhonghong.cancusom.CustomService;

/**
 * @author YC
 * @time 2016-4-8 17:55:46
 * 开机广播
 */
public class BootReceiver extends BroadcastReceiver {

	private static final String ACTION_BOOT = "android.intent.action.BOOT_COMPLETED";
	
	private String TAG = getClass().getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		if (ACTION_BOOT.equals(intent.getAction()))
		{
			context.startService(new Intent(context, CustomService.class));
			Log.i(TAG , "开启Can服务");
		}
	}

}
