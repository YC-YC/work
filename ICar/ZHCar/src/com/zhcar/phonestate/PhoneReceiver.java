/**
 * 
 */
package com.zhcar.phonestate;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * @author YC
 * @time 2016-8-1 上午11:57:23
 * TODO:电话状态
 */
public class PhoneReceiver extends BroadcastReceiver {

	private static final String TAG = "PhoneReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		  Log.i(TAG, "action = " + action);
		if (Intent.ACTION_NEW_OUTGOING_CALL.equals(action)){
//			incomingFlag = false;  
//			PhoneUtils.sendPhoneStatuBroadcast(context, PhoneUtils.PHONE_STATE_TALKING);
		}
		else {
			//如果是来电  
            TelephonyManager tm = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);                          
            Log.i(TAG, "tm.getCallState() = " + tm.getCallState() + ", intent = " + intent.toString());
            switch (tm.getCallState()) {  
            case TelephonyManager.CALL_STATE_IDLE:   //闲置状态                       
//                    if(incomingFlag){  
//                            Log.i(TAG, "incoming IDLE");                                  
//                    }  
            	PhoneStateUtils.sendPhoneStatuBroadcast(context, PhoneStateUtils.PHONE_STATE_IDLE);
            	break;  
            case TelephonyManager.CALL_STATE_RINGING:  
//            	incomingFlag = true;//标识当前是来电  
            	PhoneStateUtils.sendPhoneStatuBroadcast(context, PhoneStateUtils.PHONE_STATE_INCOMIMG);
//              incoming_number = intent.getStringExtra("incoming_number");  
                    break;  
            case TelephonyManager.CALL_STATE_OFFHOOK:  //存在至少有一个呼叫拨号，活跃，或 保留，并没有来电振铃或等待                   
//                    if(incomingFlag){  
//                            Log.i(TAG, "incoming ACCEPT :"+ incoming_number);  
//                    }
                    PhoneStateUtils.sendPhoneStatuBroadcast(context, PhoneStateUtils.PHONE_STATE_TALKING);
                    break;  
            }   
		}
	}

}
