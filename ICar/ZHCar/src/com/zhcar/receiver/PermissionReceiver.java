/**
 * 
 */
package com.zhcar.receiver;

import com.zhcar.data.GlobalData;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author YC
 * @time 2016-7-7 下午4:28:13
 * TODO:鉴权广播接收
 */
public class PermissionReceiver extends BroadcastReceiver {

	private static final String TAG = "PermissionBroadcastReceiver";
	private static final String ACTION_PERMISSION = "com.tima.Permission";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (ACTION_PERMISSION.equals(intent.getAction())){
			String status = intent.getStringExtra("status");
			if ("succeed".equals(status)){
				Log.i(TAG, "Permission succeed");
				GlobalData.permissionStatus = GlobalData.PERMISSION_STATUS_SUCCEED;
			}
			else{
				Log.i(TAG, "Permission failed status = " + status);
				GlobalData.permissionStatus = GlobalData.PERMISSION_STATUS_FAILED;
			}
		}
		
	}

}
