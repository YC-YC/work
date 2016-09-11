/**
 * 
 */
package com.zhcar.ecall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.CLTX.outer.newinterface.PhoneUtil;
import com.zhcar.R;
import com.zhcar.base.BaseApplication;
import com.zhcar.data.GlobalData;
import com.zhcar.provider.CarProviderData;
import com.zhcar.utils.DialogManager;
import com.zhcar.utils.PhoneStateUtils;
import com.zhcar.utils.Utils;

/**
 * 
 * @author YC
 * @time 2016-8-3 下午5:56:14
 * TODO:呼叫紧急联系人处理逻辑
 */
public class ECallManager {

	private static final String TAG = "ECall";
	private static final String ACTION_PHONE_STATE = "com.cowin.phone3g.CALL_STATUS";
	private static ECallManager instance;
	public static ECallManager getInstance(){
		if (instance == null){
			synchronized (ECallManager.class) {
				if (instance == null){
					instance = new ECallManager();
				}
			}
		}
		return instance;
	}
	
	private ECallManager(){
		
	}
	
	private String mEmergencyNum1;
	private String mEmergencyNum2;
	private int mEmergencyTime;
	private Handler mHandler;
	private PhoneStateReceiver mPhoneStateReceiver;
	private enum CallState{
		Idle, 
		ECallNum1Dial, 
		ECallNum1Ring, 
		ECallNum1Accept, 
		ECallNum2Dial, 
		ECallNum2Ring, 
		ECallNum2Accept
	}
	private CallState mCallState = CallState.Idle;
	
		
	/**
	 * 呼叫紧急联系人
	 */
	public void startECall(){
		
		Utils.sendBroadcast(BaseApplication.getInstanse(), GlobalData.ACTION_3G, GlobalData.KEY_3G_ECALL, "on");
		if (queryEmergencyNum()){
			Log.i(TAG, "startECall");
			registerPhoneStateReceiver();
			ECallNum1();
		}
		else{
			Utils.ToastThread(Utils.getResourceString(R.string.no_ecall_num));
		}
	}
	
	/**
	 * 呼叫紧急联系人1
	 */
	private void ECallNum1(){
		PhoneUtil.getInstance().dial(mEmergencyNum1);
		setCallState(CallState.ECallNum1Dial);
		DialogManager.getInstance().showECallDialog(BaseApplication.getInstanse(), 
				"" + mEmergencyNum1);
//		if (mHandler == null){
//			mHandler = new Handler();
//		}
//		mHandler.postDelayed(callEmergencyNum2Runnable, mEmergencyTime*1000);
	}
	
	private Runnable ECallNum2Runnable = new Runnable() {
		public void run() {
			Log.i(TAG, "long time on answer, hangup");
			PhoneUtil.getInstance().hangUp();
		}
	};
	
	/**
	 * 呼叫紧急联系人2
	 */
	private void ECallNum2(){
		Log.i(TAG, "dial eCallNum2" + mEmergencyNum2);
		PhoneUtil.getInstance().dial(mEmergencyNum2);
		setCallState(CallState.ECallNum2Dial);
		DialogManager.getInstance().updateEmergencyCallContent(
				"" + mEmergencyNum2);
	
	}

	/**
	 * 呼叫紧急联系人完毕
	 */
	public void endECall(){
		Log.i(TAG, "endECall");
		PhoneUtil.getInstance().hangUp();
		if (mHandler != null){
			mHandler.removeCallbacks(ECallNum2Runnable);
		}
		unregisterPhoneStateReceiver();
		DialogManager.getInstance().hideECallDialog();
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
				String state = intent.getStringExtra("status");
				Log.i(TAG, "PhoneStateReceiver state = " + state);
				if (PhoneStateUtils.PHONE_STATE_IDLE.equals(state)){
					if (mHandler != null){
						mHandler.removeCallbacks(ECallNum2Runnable);
					}
					if (mCallState == CallState.ECallNum1Dial 
							|| mCallState == CallState.ECallNum1Ring){
						ECallNum2();
					}
					else{
						endECall();
					}
				}
				else if (PhoneStateUtils.PHONE_STATE_TALKING.equals(state)){
					if (mCallState == CallState.ECallNum1Ring){
						setCallState(CallState.ECallNum1Accept);
						if (mHandler != null){
							mHandler.removeCallbacks(ECallNum2Runnable);
						}
					}
					else if (mCallState == CallState.ECallNum2Ring){
						setCallState(CallState.ECallNum2Accept);
					}
				}
				else if (PhoneStateUtils.PHONE_STATE_ALERTING.equals(state)){
					if (mCallState == CallState.ECallNum1Dial){
						setCallState(CallState.ECallNum1Ring);
						if (mHandler == null){
							mHandler = new Handler();
						}
						mHandler.postDelayed(ECallNum2Runnable, mEmergencyTime*1000);
					}
					else if (mCallState == CallState.ECallNum2Dial){
						setCallState(CallState.ECallNum2Ring);
					}
				}
//				else if (PhoneStateUtils.PHONE_STATE_DILEING.equals(state)){
//					if (mCallState == CallState.ECallNum1Dial){
//						if (mHandler == null){
//							mHandler = new Handler();
//						}
//						mHandler.postDelayed(ECallNum2Runnable, mEmergencyTime*1000);
//					}
//				}
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
			if (TextUtils.isEmpty(mEmergencyNum1)){
				return false;
			}
			return true;
		}
		if (cursor != null){
			cursor.close();
			cursor = null;
		}
		return false;
	}
	
	
	
	
}
