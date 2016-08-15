/**
 * 
 */
package com.zhcar.phonestate;

import android.content.Context;
import android.content.Intent;

/**
 * @author YC
 * @time 2016-8-1 下午12:15:15
 * TODO:
 */
public class PhoneStateUtils {
	
	public static final String ACTION_PHONESTATE = "com.cowin.phone3g.CALL_STATUS";
			
	/**有来电，但没接通*/
	public static final String PHONE_STATE_INCOMIMG = "1";
	/**通话中*/
	public static final String PHONE_STATE_TALKING = "2";
	/**通话延时*/
	public static final String PHONE_STATE_DELAY = "3";
	/**挂断电话*/
	public static final String PHONE_STATE_IDLE = "4";
		
	/**
	 * 发送电话广播
	 * @param context
	 * @param state
	 */
	public static void sendPhoneStatuBroadcast(Context context, String state){
		Intent it = new Intent(ACTION_PHONESTATE);
		it.putExtra("state", state);
		context.sendBroadcast(it);
	}
}
