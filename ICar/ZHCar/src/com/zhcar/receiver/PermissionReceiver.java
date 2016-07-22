/**
 * 
 */
package com.zhcar.receiver;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.zhcar.carflow.CarFlowManager;
import com.zhcar.data.GlobalData;
import com.zhcar.provider.CarProviderData;
import com.zhcar.utils.Utils;

/**
 * @author YC
 * @time 2016-7-7 下午4:28:13
 * TODO:鉴权广播接收
 */
public class PermissionReceiver extends BroadcastReceiver {

	private static final String TAG = "PermissionReceiver";
	private static final String ACTION_PERMISSION = "com.tima.Permission";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (ACTION_PERMISSION.equals(intent.getAction())){
			String status = intent.getStringExtra("status");
			if ("succeed".equals(status)){
				Log.i(TAG, "Permission succeed");
				GlobalData.bPermissionStatus = true;
//				setPermissionProvider(context, true);
				checkCarFlow(context);
			}
			else{
				Log.i(TAG, "Permission failed status = " + status);
				GlobalData.bPermissionStatus = false;
//				setPermissionProvider(context, false);
			}
		}
	}

	private boolean checkCarFlow(Context context){
		Uri carInfoUri = Uri.parse("content://cn.com.semisky.carProvider/carInfo");
		Cursor cursor = context.getContentResolver().query(carInfoUri, null, null, null, null);
		if(cursor != null && cursor.moveToNext()){
			String iccid = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_ICCID));
			String token = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_TOKEN));
			if (!Utils.isEmpty(iccid) && !Utils.isEmpty(token)){
				CarFlowManager.getInstance(context).HttpRequestCarFlow(iccid, token);
			}
			return true;
		}
		else{
			return false;
		}
	}

}
