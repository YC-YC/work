/**
 * 
 */
package com.zhcar.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.zhcar.data.GlobalData;
import com.zhcar.ecall.ECallManager;
import com.zhcar.utils.GPRSManager;
import com.zhcar.utils.UpdateUiManager;
import com.zhcar.utils.Utils;

/**
 * 
 * @author YC
 * @time 2016-8-3 下午5:38:49
 * TODO:
 */
public class ZuiReceiver extends BroadcastReceiver {

	private static final String ACTION_ZUI = "com.zhonghong.zuiserver.BROADCAST";
	private static final String TAG = "ZuiReceiver";
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
//		Log.i(TAG, "ZuiReceiver action = " + action);
		if (ACTION_ZUI.equals(action)){
			String extra = intent.getStringExtra("hit");
			if (extra != null){
				//TODO: 拨打电话
				ECallManager.getInstance().startECall();
			}
			else if ((extra = intent.getStringExtra("score")) != null){
				final String val = extra;
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						UpdateUiManager.getInstances().callUpdate(UpdateUiManager.CMD_UPDATE_CHECK_SCORE, val);
					}
				}, 2*1000);
			}
			else if ((extra = intent.getStringExtra("canok")) != null){
				Log.i(TAG, "Receiver Can OK");
				GPRSManager gprsManager = new GPRSManager(context);
				if (!gprsManager.isEnable()){
					gprsManager.turnOn();
				}
				Utils.sendBroadcast(context, GlobalData.ACTION_ZHCAR_TO_ZUI, GlobalData.KEY_3G_CONTROL , "on");
				if (gprsManager.isNetWorkValuable()){
					Utils.sendBroadcast(context, GlobalData.ACTION_ZHCAR_TO_ZUI, GlobalData.KEY_3G_STATE, "on");
				}
				else{
					Utils.sendBroadcast(context, GlobalData.ACTION_ZHCAR_TO_ZUI, GlobalData.KEY_3G_STATE, "off");
				}
			}
		}
	}

}
