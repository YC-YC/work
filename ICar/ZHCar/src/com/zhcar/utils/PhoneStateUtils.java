/**
 * 
 */
package com.zhcar.utils;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.CLTX.outer.newinterface.PhoneUtil;
import com.zhcar.base.BaseApplication;

/**
 * @author YC
 * @time 2016-8-1 下午12:15:15
 * TODO:电话状态转换
 */
public class PhoneStateUtils {
	
//	IDLE, ACTIVE, HOLDING, DIALING, ALERTING, INCOMING, WAITING, DISCONNECTED, DISCONNECTING;
	
	public static final String ACTION_PHONESTATE = "com.cowin.phone3g.CALL_STATUS";
			
	/**有来电，但没接通*/
	public static final String PHONE_STATE_INCOMIMG = "1";
	/**通话中*/
	public static final String PHONE_STATE_TALKING = "2";
	/**通话延时*/
	public static final String PHONE_STATE_DELAY = "3";
	/**挂断电话,空闲状态*/
	public static final String PHONE_STATE_IDLE = "4";
	/**拨出，还未接通*/
	public static final String PHONE_STATE_DILEING = "5";
	/**拨出，响铃*/
	public static final String PHONE_STATE_ALERTING = "6";


	protected static final String TAG = "PhoneStateUtils";
	
	private static PhoneStateUtils instance;
	
	public static PhoneStateUtils getInstance() {
		if (instance == null) {
			synchronized (PhoneStateUtils.class) {
				if (instance == null) {
					instance = new PhoneStateUtils();
				}
			}
		}
		return instance;
	}
	
	private PhoneStateUtils(){
		
	}
	
	/**
	 * 发送电话广播
	 * @param context
	 * @param state
	 */
	private void sendPhoneStatuBroadcast(String state){
		Log.i(TAG, "sendPhoneStatuBroadcast state = " + state);
		Intent it = new Intent(ACTION_PHONESTATE);
		it.putExtra("status", state);
		BaseApplication.getInstanse().sendBroadcast(it);
	}
	
	private Handler mHandler;
	private int mLastCallState = -1;
	
	/**
	 * 开始监听通话状态
	 */
	public void startListenCallState(){
		Log.i(TAG, "startListenCallState");
		Log.i(TAG, "PhoneUtil.IDLE = " + PhoneUtil.IDLE);
		if (mHandler == null){
			mHandler = new Handler();
		}
		mHandler.post(mRunnable);
	}
	
	public void endListenCallState(){
		Log.i(TAG, "endListenCallState");
		sendPhoneStatuBroadcast(PHONE_STATE_IDLE);
		if (mHandler != null){
			mHandler.removeCallbacks(mRunnable);
		}
	}
	
	private Runnable mRunnable = new Runnable(){
		@Override
		public void run(){
			int state = PhoneUtil.getInstance().getActiveFgCallState();
			
			if (state != mLastCallState){
				mLastCallState = state;
				Log.i(TAG, "getActiveFgCallState = " + state);
				
				if (state == PhoneUtil.ACTIVE){
					Log.i(TAG, "sendState = " + PHONE_STATE_TALKING);
					sendPhoneStatuBroadcast(PHONE_STATE_TALKING);
				}
//				else if (state == PhoneUtil.DIALING){
//					Log.i(TAG, "sendState = " + PHONE_STATE_DILEING);
//					sendPhoneStatuBroadcast(PHONE_STATE_DILEING);
//				}
				else if (state == PhoneUtil.ALERTING){
					Log.i(TAG, "sendState = " + PHONE_STATE_ALERTING);
					sendPhoneStatuBroadcast(PHONE_STATE_ALERTING);
				}
			}
			mHandler.postDelayed(this, 100);
		}
	};
}
