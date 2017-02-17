/**
 * 
 */
package com.zhonghong.autotestclient;

import com.zhonghong.autotestlib.bean.ATConst;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * @author YC
 * @time 2016-12-6 下午6:05:03
 * TODO:
 */
public class ATBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (ATConst.Broadcast.ACTION.equals(intent.getAction())){
			String extra = null;
			extra = intent.getStringExtra(ATConst.Broadcast.KEY_START_AT);
			Intent it = new Intent("com.zhonghong.autotestclient.ATClientService");
			if (extra != null){
				context.startService(it);
			}
			
			extra = intent.getStringExtra(ATConst.Broadcast.KEY_END_AT);
			if (extra != null){
				context.stopService(it);
			}
		}
	}

}
