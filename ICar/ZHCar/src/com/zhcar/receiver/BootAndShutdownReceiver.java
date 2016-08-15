/**
 * 
 */
package com.zhcar.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * @author Administrator
 * @time 2016-8-9 下午1:52:12
 * TODO: 开机关广播
 */
public class BootAndShutdownReceiver extends BroadcastReceiver {

	private static final String TAG = "BootAndShutdownReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (Intent.ACTION_BOOT_COMPLETED.equals(action)){
			//TODO 开机完成
			Log.i(TAG, "开机完成");
		}
		else if (Intent.ACTION_SHUTDOWN.equals(action)){
			//TODO 关机
			Log.i(TAG, "关机完成");
//			Toast.makeText(context, "关机完成", 100).show();
		}
	}

}
