/**
 * 
 */
package com.zhcar.emergencycall;

import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.zhcar.R;
import com.zhcar.base.BaseApplication;
import com.zhcar.phonestate.PhoneStateUtils;
import com.zhcar.provider.CarProviderData;
import com.zhcar.utils.DialogManager;

/**
 * 
 * @author YC
 * @time 2016-8-3 下午5:56:14
 * TODO:呼叫紧急联系人
 */
public class EmergencyCallManager {

	private static final String TAG = "EmergencyCall";
	private static final String ACTION_PHONE_STATE = "com.cowin.phone3g.CALL_STATUS";
	private static EmergencyCallManager instance;
	public static EmergencyCallManager getInstance(){
		if (instance == null){
			synchronized (EmergencyCallManager.class) {
				if (instance == null){
					instance = new EmergencyCallManager();
				}
			}
		}
		return instance;
	}
	
	private EmergencyCallManager(){
		
	}
	
	private String mEmergencyNum1;
	private String mEmergencyNum2;
	private int mEmergencyTime;
	private Handler mHandler;
	private PhoneStateReceiver mPhoneStateReceiver;
	private enum CallState{
		Idle, EmergencyNum1Calling, EmergencyNum1Accept, EmergencyNum2Calling, EmergencyNum2Accept
	}
	private CallState mCallState = CallState.Idle;
	
		
	/**
	 * 呼叫紧急联系人
	 */
	public void startcallEmergency(){
		
		if (queryEmergencyNum()){
			Log.i(TAG, "startcallEmergency");
			registerPhoneStateReceiver();
			callEmergencyNum1();
		}
	}
	
	private void callEmergencyNum1(){
		//PhoneUtil.getInstance().dial(mEmergencyNum1);
		setCallState(CallState.EmergencyNum1Calling);
		DialogManager.getInstance().showEmergencyCallDialog(BaseApplication.getInstanse(), 
				BaseApplication.getInstanse().getResources().getString(R.string.calling_emergency) + mEmergencyNum1);
		if (mHandler == null){
			mHandler = new Handler();
		}
		mHandler.postDelayed(callEmergencyNum2Runnable, mEmergencyTime*1000);
	}
	
	private Runnable callEmergencyNum2Runnable = new Runnable() {
		public void run() {
			toEmergencyNum2();
		}
	};
	
	private void toEmergencyNum2(){
		//PhoneUtil.getInstance().handUp();
		//PhoneUtil.getInstance().dial(mEmergencyNum2);
		setCallState(CallState.EmergencyNum2Calling);
		DialogManager.getInstance().updateEmergencyCallContent(
				BaseApplication.getInstanse().getResources().getString(R.string.calling_emergency) + mEmergencyNum2);
	
	}

	public void endCallEmergency(){
		Log.i(TAG, "endCallEmergency");
		if (mHandler != null){
			mHandler.removeCallbacks(callEmergencyNum2Runnable);
		}
		unregisterPhoneStateReceiver();
		DialogManager.getInstance().hideEmergencyCallDialog();
	}
	
	private void setCallState(CallState state){
		mCallState = state;
	}
	
	
	/**
	 * 注册电话状态广播
	 */
	private void registerPhoneStateReceiver() {
		IntentFilter intentFilter=new IntentFilter();
		intentFilter.addAction(ACTION_PHONE_STATE);
		mPhoneStateReceiver = new PhoneStateReceiver();
		BaseApplication.getInstanse().registerReceiver(mPhoneStateReceiver,intentFilter); 
	}
	
	/**
	 * 注销电话状态广播
	 */
	private void unregisterPhoneStateReceiver() {
		if (mPhoneStateReceiver != null){
			BaseApplication.getInstanse().unregisterReceiver(mPhoneStateReceiver);
			mPhoneStateReceiver = null;
		}
	}

	private class PhoneStateReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_PHONE_STATE.equals(action)){
				String state = intent.getStringExtra("state");
				if (PhoneStateUtils.PHONE_STATE_IDLE.equals(state)){
					if (mCallState == CallState.EmergencyNum1Accept 
							|| mCallState == CallState.EmergencyNum2Calling
							|| mCallState == CallState.EmergencyNum2Accept){
						endCallEmergency();
					}
				}
				else if (PhoneStateUtils.PHONE_STATE_TALKING.equals(state)){
					if (mCallState == CallState.EmergencyNum1Calling){
						setCallState(CallState.EmergencyNum1Accept);
					}
					else if (mCallState == CallState.EmergencyNum2Calling){
						setCallState(CallState.EmergencyNum2Accept);
					}
				}
				else if (PhoneStateUtils.PHONE_STATE_DELAY.equals(state)){
					
				}
				else if (PhoneStateUtils.PHONE_STATE_INCOMIMG.equals(state)){
	
				}
			}
		}
	};
	
	/**
	 * 从provider 紧急联系人电话信息
	 */
	private boolean queryEmergencyNum() {
		Cursor cursor = BaseApplication.getInstanse().getContentResolver().query(CarProviderData.URI_PHONEINFO, null, null, null, null);
		if(cursor != null && cursor.moveToNext()){
			mEmergencyNum1 = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_PHONENUM_EMERGENCY_NUM1));
			mEmergencyNum2 = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_PHONENUM_EMERGENCY_NUM2));
			mEmergencyTime = cursor.getInt(cursor.getColumnIndex(CarProviderData.KEY_PHONENUM_EMERGENCY_TIME));
			if (mEmergencyTime <= 0){
				mEmergencyTime = 20;
			}
			Log.i(TAG, "mEmergencyNum1 = " + mEmergencyNum1 + ", mEmergencyNum2 = " + mEmergencyNum2 
					+ ", mEmergencyTime = " + mEmergencyTime);
			cursor.close();
			cursor = null;
			return true;
		}
		if (cursor != null){
			cursor.close();
			cursor = null;
		}
		return false;
	}
	
	
	
	
}
