/**
 * 
 */
package com.example.tt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author YC
 * @time 2016-7-18 下午2:52:54
 * TODO:
 */
public class BootReceiver extends BroadcastReceiver {

	private static final String TAG = "BootReceiver";

	@Override
	public void onReceive(Context context, Intent arg1) {
		Log.i(TAG, "接收到开机广播");
		Intent intent = new Intent(context, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

}
