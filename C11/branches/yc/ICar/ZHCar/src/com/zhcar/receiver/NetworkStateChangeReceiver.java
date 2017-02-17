/**
 * 
 */
package com.zhcar.receiver;

import com.zhcar.apprecord.RecordManager;
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
			if (gprsManager.isNetWorkValuable()){
				Utils.sendBroadcast(context, GlobalData.ACTION_ZHCAR_TO_ZUI, GlobalData.KEY_3G_STATE, "on");
//				Utils.ToastThread("网络已连接");
				Log.i(TAG, "网络已连");
				RecordManager.getInstance().onNetworkStateChange(true);
			}
			else{
				Utils.sendBroadcast(context, GlobalData.ACTION_ZHCAR_TO_ZUI, GlobalData.KEY_3G_STATE, "off");
//				Utils.ToastThread("网络已断开");
				Log.i(TAG, "网络已断开");
				RecordManager.getInstance().onNetworkStateChange(false);
			}
		}
	}

}
