/**
 * 
 */
package com.zhcar.utils;

/**
 * @author YC
 * @time 2016-7-12 下午4:21:36
 * TODO:
 */
public class BtUtils {

	/**蓝牙广播Action*/
	public static String BT_CONTROL_ACTION = "com.zhonghong.zuiserver.BROADCAST";
	/**拨打号码的Key*/
	public static String BT_CALL_PHONE_KEY = "BT_DIAL_KEY";
	
	/**未连接*/
	public static final int BT_STATE_DISCONNECT = 0;
	/**已连接*/
	public static final int BT_STATE_CONNECTED = 1;
	/**连接中*/
	public static final int BT_STATE_CONNECTING = 2;
	/**断开中*/
	public static final int BT_STATE_DISCONNECTING = 3;
	
	public static int BtDevState = BT_STATE_DISCONNECT;
	
	/**通话中*/
	public static final String BT_STATE_ACTIVE = "CALL_STATE_ACTIVE";
	/**响铃*/
	public static final String BT_STATE_ALERTING = "CALL_STATE_ALERTING";
	/**拨号*/
	public static final String BT_STATE_DIALING = "CALL_STATE_DIALING";
	/**挂起*/
	public static final String BT_STATE_HELD = "CALL_STATE_HELD";
	/**接听并挂起*/
	public static final String BT_STATE_HELD_BY_RESPONSE_AND_HOLD = "CALL_STATE_HELD_BY_RESPONSE_AND_HOLD";
	/**来电*/
	public static final String BT_STATE_INCOMING = "CALL_STATE_INCOMING";
	/**终止*/
	public static final String BT_STATE_TERMINATED = "CALL_STATE_TERMINATED";
	/**等待*/
	public static final String BT_STATE_WAITING = "CALL_STATE_WAITING";
	
	public static String BtCallState = BT_STATE_TERMINATED;
	
	/**是否已经连接上*/
	public static boolean isBtConnected(){
		return (BtDevState == BT_STATE_CONNECTED);
	}
	
	/**是否在通话状态*/
	public static boolean isBtCalling(){
		if (!isBtConnected())
			return false;
		if (BT_STATE_TERMINATED.equals(BtCallState))
			return false;
		return true;
	}
	
}
