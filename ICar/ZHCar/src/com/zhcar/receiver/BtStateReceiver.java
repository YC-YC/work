/**
 * 
 */
package com.zhcar.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.zhcar.utils.BtUtils;
import com.zhcar.utils.UpdateUiManager;

/**
 * @author YC
 * @time 2016-7-12 下午3:39:49
 * TODO:蓝牙状态广播
 */
public class BtStateReceiver extends BroadcastReceiver {

	private static final String BT_DEV_STATE = "com.zhonghong.bluetooth.BROADCAST_BT_DEVICE_STATE";
	private static final String BT_CALL_STATE = "com.zhonghong.bluetooth.BROADCAST_CALLSTATE";
	private static final String TAG = "BtStateReceiver";
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (BT_DEV_STATE.equals(action)){
			BtUtils.BtDevState = intent.getIntExtra("state", 0);
//			Log.i(TAG, "receive bt dev state = " + BtUtils.BtDevState);
			UpdateUiManager.getInstances().callUpdate(UpdateUiManager.CMD_UPDATE_BTSTATE);
		}
		else if (BT_CALL_STATE.equals(action)){
			BtUtils.BtCallState = intent.getStringExtra("state");
//			Log.i(TAG, "receive bt call state = " + BtUtils.BtCallState);
			UpdateUiManager.getInstances().callUpdate(UpdateUiManager.CMD_UPDATE_BTSTATE);
		}
	}

}
