/**
 * 
 */
package com.yc.external;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.yc.external.ExternalConn.Stub;

/**
 * @author YC
 * @time 2016-7-15 上午10:54:47 
 * TODO: Aidl服务
 */
public class AidlService extends Service implements IGetFromClient{

	private static final String TAG = "AidlService";
	private AidlBinder mBinder;

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate");
		super.onCreate();
		mBinder = new AidlBinder();
		ConnManager.getInstance().setGetFromClient(this);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO dosomething
			}
		}, "service_thread").start();
	}

	// 每次调用StartService都会调用
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "onStartCommand");
		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy");
		super.onDestroy();
	}

	@Override
	public void onLowMemory() {
		Log.i(TAG, "onLowMemory");
		super.onLowMemory();
	}

	public class AidlBinder extends Stub {

		@Override
		public boolean postInfo(int cmd, String val) throws RemoteException {
			Log.i(TAG, "postInfo cmd = " + cmd + ", val = " + val);
			return ConnManager.getInstance().postInfo(cmd, val);
		}

		@Override
		public String getInfo(int cmd) throws RemoteException {
			// 暂时没用
			return null;
		}

		@Override
		public void registerCallFromService(CallFromService callback)
				throws RemoteException {
			if (callback != null)
				mRemoteCallbackList.register(callback);
		}

		@Override
		public void unregisterCallFromService(CallFromService callback)
				throws RemoteException {
			if (callback != null)
				mRemoteCallbackList.unregister(callback);
		}

	};
	
	final RemoteCallbackList<CallFromService> mRemoteCallbackList = new RemoteCallbackList<CallFromService>();
	
	/**
	 * 回调所有远程注册
	 */
	private String getInfoFromClient(final int cmd)
	{
		int num = mRemoteCallbackList.beginBroadcast();
		Log.i(TAG, "callback client num = " + num);
		String result = null;
		for (int i = 0; i < num; i++)
		{
			try {
				if ((result = mRemoteCallbackList.getBroadcastItem(i).getInfo(cmd)) != null)
					break;
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		mRemoteCallbackList.finishBroadcast();
		return result;
	}

	@Override
	public String getInfo(int cmd) {
		return getInfoFromClient(cmd);
	}

}
