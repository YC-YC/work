/**
 * 
 */
package com.zhonghong.autotestservice.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.RemoteException;
import android.util.Log;

import com.zhonghong.autotestlib.ATCallback;
import com.zhonghong.autotestlib.ATConn.Stub;
import com.zhonghong.autotestlib.bean.ATConst;
import com.zhonghong.autotestservice.bean.ATEvent;
import com.zhonghong.datamode.IDataMode;
import com.zhonghong.datamode.IDataMode.DataListener;
import com.zhonghong.datamode.NoneDataMode;

/**
 * @author YC
 * @time 2016-11-29 下午6:07:17
 * TODO:自动化测试服务基类，所有项目都只需要继承该类并完成没实现函数，同时，需要在
 */
public abstract class ATService extends Service{
	
	protected static final String TAG = "ATService";

	protected IDataMode mDataMode = new NoneDataMode();
	private HashMap<Integer, HashMap<String, ATCallback>> mClients = new HashMap<Integer, HashMap<String, ATCallback>>();
	private HashMap<String, CallbackDeathRecipient> mCallbackDeathRecipients = new HashMap<String, CallbackDeathRecipient>();

	@Override
	public IBinder onBind(Intent intent) {
		return myBinder;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "onCreate");
		IDataMode dataMode = getDataMode();
		if (dataMode != null){
			mDataMode = dataMode;
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "onStartCommand");
		mDataMode.registerDataListener(mDataListener);
		sendATBroadcast(ATConst.Broadcast.KEY_START_AT, "true");
		byte[] responseStart = onResponseStart();
		if (responseStart != null){
			sendData(responseStart);
		}
		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		Log.i(TAG, "onLowMemory");
	}
	
	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy");
		mDataMode.unregisterDataListener(mDataListener);
		super.onDestroy();
	}
	
	
	private DataListener mDataListener = new DataListener() {
		
		@Override
		public void onDataIn(byte[] data) {
			ATEvent atEvent = onConvert(data);
			if (atEvent != null){
				dispatch(atEvent);
			}
		}
	};
	
	/**获取数据模型*/
	protected abstract IDataMode getDataMode();
	/**回复启动应答*/
	protected abstract byte[] onResponseStart();
	/**根据项目具体要求实现外部协议转成内部协议*/
	protected abstract ATEvent onConvert(byte[] caninfo);
	/**根据项目具体要求实现内部协议转成外部协议*/
	protected abstract byte[] onRevert(int type, int cmd, String info);
	
	/**
	 * 分发数据
	 * @param atEvent
	 */
	private void dispatch(ATEvent atEvent){
		HashMap<String, ATCallback> callbacks = mClients.get(atEvent.type);
		Iterator<Entry<String, ATCallback>> iterator = callbacks.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, ATCallback> callback = iterator.next();
			try {
				callback.getValue().onDispatch(atEvent.type, atEvent.cmd, atEvent.info);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 发数据给MCU
	 * @param data
	 */
	public void sendData(byte[] data){
		mDataMode.sendData(data);
	}
	
	/**
	 * 发送AT广播
	 * @param key
	 * @param val
	 */
	public void sendATBroadcast(String key, String val){
		Intent it = new Intent(ATConst.Broadcast.ACTION);
		it.putExtra(key, val);
		sendBroadcast(it);
	}
	
	private Stub myBinder = new Stub() {
		
		@Override
		public void onResponse(int type, int cmd, String info)
				throws RemoteException {
			byte[] data = onRevert(type, cmd, info);
			if (data != null){
				sendData(data);
			}
		}

		@Override
		public void registerATClient(int type, ATCallback callback)
				throws RemoteException {
			if (callback == null){
				return;
			}
			HashMap<String, ATCallback> typeCallback = mClients.get(type);
			if (typeCallback == null){
				typeCallback = new HashMap<String, ATCallback>();
				mClients.put(type, typeCallback);
			}
			String callbackId = callback.asBinder().toString();
			if (!typeCallback.containsKey(callbackId)){
				typeCallback.put(callbackId, callback);
				CallbackDeathRecipient deathRecipient = new CallbackDeathRecipient(type, callbackId);
				callback.asBinder().linkToDeath(deathRecipient, 0);
				mCallbackDeathRecipients.put(callbackId, deathRecipient);
			}
			
		}

		@Override
		public void unregisterATClient(int type, ATCallback callback)
				throws RemoteException {
			if (callback == null){
				return;
			}
			String callbackId = callback.asBinder().toString();
			HashMap<String, ATCallback> typeCallback = mClients.get(type);
			if (typeCallback != null){
				typeCallback.remove(callbackId);
				CallbackDeathRecipient deathRecipient = mCallbackDeathRecipients.get(callbackId);
				if (deathRecipient != null){
					callback.asBinder().unlinkToDeath(deathRecipient, 0);
				}
				mCallbackDeathRecipients.remove(callbackId);
				
			}
		}
	};
	
	class CallbackDeathRecipient implements DeathRecipient {
		
		private int mType;
		private String mCallbackId;

		public CallbackDeathRecipient(int type, String id) {
			mCallbackId = id;
			mType = type;
		}

		@Override
		public void binderDied() {
			Log.i(TAG,"callback died: " + mCallbackId);
			if (mCallbackId != null) {
				HashMap<String, ATCallback> callbacks = mClients.get(mType);
				if (callbacks != null) {
					Log.i(TAG,"removed callback record: " + mType);
					callbacks.remove(mCallbackId);
				}
				mCallbackDeathRecipients.remove(mCallbackId);
			}
		}
	};

}
