/**
 * 
 */
package com.zhonghong.aidl;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.zhonghong.cancusom.CanDataManager;
import com.zhonghong.cancusom.CanDataManager.CanDataChangeCallback;
import com.zhonghong.cancusom.DasAutoXBS;

/**
 * @author YC
 * @time 2016-4-11 下午5:22:32
 */
public class CanAidlService extends Service {

	private static final int MSG_UPDATE = 100;
	
	private String TAG = getClass().getSimpleName();
	
	private MyHandle mHandler = new MyHandle();
	
	@Override
	public IBinder onBind(Intent intent) {
		CanDataManager.getInstance().register(mCanDataChangeCallback);
		return mBinder;
	}
	
	CanDataChangeCallback mCanDataChangeCallback = new CanDataChangeCallback() {
		
		@Override
		public void callback() {
			//CanDataManager.currentAirInfo
			mHandler.sendEmptyMessageDelayed(MSG_UPDATE, 0);
		}
	};
	
	
	private final ITaskBinder.Stub mBinder = new ITaskBinder.Stub() {
		
		@Override
		public void unregisterDataChangeCallback(ICanDataChangeCallback callback)
				throws RemoteException {
			if (callback != null){
				mRemoteCallbackList.unregister(callback);
			}
		}
		
		@Override
		public void registerDataChangeCallback(ICanDataChangeCallback callback)
				throws RemoteException {
			if (callback != null){
				mRemoteCallbackList.register(callback);
				Log.i(TAG, "register client num = " + mRemoteCallbackList.getRegisteredCallbackCount());
			}
		}


		@Override
		public void writecmdCan(byte[] data) throws RemoteException {
			CanDataManager.writecmdCan(data);
		}

		@Override
		public CanInfoParcel getCanInfo() throws RemoteException {
			CanInfoParcel caninfo = CanDataManager.getInstance().getCanInfoParcel();
			return caninfo;
		}
	};

	final RemoteCallbackList<ICanDataChangeCallback> mRemoteCallbackList = new RemoteCallbackList<ICanDataChangeCallback>();
	
	/**回调所有注册*/
	private void serviceCallback()
	{
		int num = mRemoteCallbackList.beginBroadcast();
		Log.i(TAG, "callback client num = " + num);
//		DasAutoXBS airInfo = CanDataManager.currentAirInfo;
		for (int i = 0; i < num; i++)
		{
			try {
				mRemoteCallbackList.getBroadcastItem(i).notifyDataChange();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		mRemoteCallbackList.finishBroadcast();
	}
	
	class MyHandle extends Handler
	{
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == MSG_UPDATE)
			{
				serviceCallback();
			}
		}
	}
}
