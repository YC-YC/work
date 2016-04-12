/**
 * 
 */
package com.zhonghong.base;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.zhonghong.canbase.service.CanService;
import com.zhonghong.cancusom.CustomService;
import com.zhonghong.cancusom.CustomService.LocalBinder;

/**
 * @author YC
 * @time 2016-4-9 上午9:41:43
 */
public class BaseApplication extends Application {

	private CanService mCanService;

	private String TAG = getClass().getSimpleName();
	
	private static BaseApplication instanse;
	
	public static BaseApplication getInstanse(){
		return instanse;
	}
	
	@Override
	public void onCreate() {
		Log.i(TAG , "onCreate");
		super.onCreate();
		instanse = this;
		this.bindService(new Intent(this, CustomService.class), conn, this.BIND_AUTO_CREATE);
	}
	
	private ServiceConnection conn = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mCanService = null;			
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mCanService = ((LocalBinder)service).getService();
		}
	};
	
	public CanService getCanService(){
		return mCanService;
	}
	
}
