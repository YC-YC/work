/**
 * 
 */
package com.zhonghong.logic.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zhonghong.logic.data.GlobalData;
import com.zhonghong.logic.service.LogicService;

/**
 * @author YC
 * @time 2016-6-22 上午10:12:29
 * 开机广播
 */
public class BootReceiver extends BroadcastReceiver {

private static final String ACTION_BOOT = "android.intent.action.BOOT_COMPLETED";
	
	private static String TAG = "BootReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (ACTION_BOOT.equals(intent.getAction()))
		{
			context.startService(new Intent(GlobalData.LOGIC_SERVICE_CLASS));
			Log.i(TAG , "open Logicservice");
		}
	}

}
