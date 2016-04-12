package com.zhonghong.cancusom;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.zhonghong.canbase.service.CanService;
import com.zhonghong.utils.Utils;

public class CustomService extends CanService{
	
	private String TAG = getClass().getSimpleName();
	
	Context mContext;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		mContext = getApplicationContext();
		mParser = new CustomParse(mContext);
		Log.d("wendan","CustomService oncreate");
		super.onCreate();
		Utils.sendBroadcast(mContext, Utils.ACTION_CAN_START_OK);
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		super.onStartCommand(intent, flags, startId);
		/**服务被异常kill掉，系统会自动重启该服务，并将Intent的值传入*/
		return START_STICKY;
//		return START_REDELIVER_INTENT;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "onBind");
		return new LocalBinder();
	}

	public class LocalBinder extends Binder{
		public CustomService getService(){
			return CustomService.this;
		}
	}
}
