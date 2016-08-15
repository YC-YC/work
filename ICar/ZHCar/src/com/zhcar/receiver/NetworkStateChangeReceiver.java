/**
 * 
 */
package com.zhcar.receiver;

import com.zhcar.data.GlobalData;
import com.zhcar.utils.GPRSManager;
import com.zhcar.utils.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

/**
 * @author Administrator
 * @time 2016-8-11 下午12:34:03
 * TODO: 网络状态监听
 */
public class NetworkStateChangeReceiver extends BroadcastReceiver {

	private static final String TAG = "NetworkStateChangeReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.i(TAG, "action = " + action);
		if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)){
			GPRSManager gprsManager = new GPRSManager(context);
			if (gprsManager.isNetWorkValilable()){
				Utils.sendBroadcast(context, GlobalData.ACTION_3G, GlobalData.KEY_3G_STATE, "on");
			}
			else{
				Utils.sendBroadcast(context, GlobalData.ACTION_3G, GlobalData.KEY_3G_STATE, "off");
			}
		}
	}

}
