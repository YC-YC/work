/**
 * 
 */
package com.zhonghong.can;

import com.zhonghong.aidl.CanInfoParcel;
import com.zhonghong.aidl.ICanDataChangeCallback;
import com.zhonghong.aidl.ITaskBinder;
import com.zhonghong.utils.T;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * @author YC
 * @time 2016-4-11 下午4:48:55
 */
public class CanManager {

	private static final String TAG = "CanManager";

	private static CanManager instance;
	
	private Handler mHandler;
	private ITaskBinder mTaskBinder;
	
	public static CanManager getInstace(){
		if (instance == null){
			instance = new CanManager();
		}
		return instance;
	}
	
	public void setHandle(Handler handle){
		mHandler = handle;
		
	}
	
	public void bindCanService(Context context){
		Log.i(TAG, "bindCanService");
		Intent it = new Intent("com.zhonghong.aidl.CanAidlService");
		context.bindService(it, conn, Context.BIND_AUTO_CREATE);
	}
	
	public void unBindCanService(Context context){
		
	}
	
	
	public CanInfoParcel getCanInfo(){
		if (mTaskBinder != null){
			try {
				return mTaskBinder.getCanInfo();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public void writecmdCan(byte[] data){
		if (mTaskBinder != null){
			try {
				mTaskBinder.writecmdCan(data);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	private volatile ServiceConnection conn = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i(TAG, "onServiceConnected");
			mTaskBinder = ITaskBinder.Stub.asInterface(service);
			try {
				mTaskBinder.registerDataChangeCallback(new ICanDataChangeCallback.Stub() {
					
					@Override
					public void notifyDataChange() throws RemoteException {
						Log.i(TAG, "notifyDataChange");
						if (mHandler != null)
						{
							mHandler.sendEmptyMessage(T.UpdateUiCmd.UPDATE_ALL);
						}
					}
				});
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			if (mHandler != null)
			{
				mHandler.sendEmptyMessage(T.UpdateUiCmd.UPDATE_ALL);
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mTaskBinder = null;
		}
		
	};
	
	
	private CanManager(){
		Log.i(TAG, "new CanManager");
	}
}
