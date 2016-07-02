/**
 * 
 */
package com.zhonghong.logic.receiver;

import com.zhonghong.logic.data.GlobalData;
import com.zhonghong.recordinfo.RecordAppManager;
import com.zhonghong.recordinfo.RecordInfoBean;
import com.zhonghong.utils.AppUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 
 * @author YC
 * @time 2016-6-22 上午11:19:05
 * TODO:应用启动广播
 */
public class AppChangeReceiver extends BroadcastReceiver {

	private static final String TAG = "AppChangeReceiver";
	private static final String ACTION_START_APP = "com.zhonghong.change.activity";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "action = " + intent.getAction());
		if (ACTION_START_APP.equals(intent.getAction())){
			String pkgName = intent.getStringExtra("packageName");
			String clazzName = intent.getStringExtra("className");
			String status = intent.getStringExtra("status");
//			Log.i(TAG, "get Action pkgName = " + pkgName + ", class = " + clazzName + ", status = " + status);
			if ("onResume".equals(status)){
				if (!AppUtils.isServiceRunning(context, GlobalData.LOGIC_SERVICE_CLASS)){
					context.startService(new Intent(GlobalData.LOGIC_SERVICE_CLASS));
					Log.i(TAG , "check open Logicservice");
				}
				RecordInfoBean recordInfoBean = RecordAppManager.getInstaces(context).getRecordInfo();
				recordInfoBean.setLastPkgName(pkgName);
				recordInfoBean.setLastClassName(clazzName);
				RecordAppManager.getInstaces(context).setRecordInfo(recordInfoBean);
			}
		}
	}

}
