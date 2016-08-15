/**
 * 
 */
package com.zhcar.receiver;

import com.zhcar.emergencycall.EmergencyCallManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 
 * @author YC
 * @time 2016-8-3 下午5:38:49
 * TODO:
 */
public class ZuiReceiver extends BroadcastReceiver {

	private static final String ACTION_ZUI = "com.zhonghong.zuiserver.BROADCAST";
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (ACTION_ZUI.equals(action)){
			String extra = intent.getStringExtra("hit");
			if (extra != null){
				//TODO: 拨打电话
				EmergencyCallManager.getInstance().startcallEmergency();
			}
		}
	}

}
