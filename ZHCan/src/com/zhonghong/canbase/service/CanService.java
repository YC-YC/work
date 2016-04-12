/**
 * 
 */
package com.zhonghong.canbase.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.zhonghong.mcuservice.CanProxy;
import android.zhonghong.mcuservice.RegistManager.ICanInfoChangedListener;

/**
 * @author YC
 * @time 2016-4-8 18:04:22
 */
public class CanService extends Service {

	private String TAG = getClass().getSimpleName();
	
	protected CanParserBase mParser;
	private CanProxy mCanProxy;
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "onBind");
		return null;
	}

	/*public class LocalBinder extends Binder{
		public CanService getService(){
			return CanService.this;
		}
	}*/
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "onCreate");
		mCanProxy = new CanProxy();
		mCanProxy.registCanInfoChangedListener(caninfolistener);
	}

	/** Can的信息有变化就是回调 */
	ICanInfoChangedListener caninfolistener = new ICanInfoChangedListener() {
		@Override
		public void notify(int[] changeCMDs, short[] caninfo) {
			// TODO Auto-generated method stub
			if(mParser!=null){
				mParser.parsePacket(caninfo);
			}
		}
	};

	/**
	 * 写入can 命令
	 * @param data
	 */
	public void writecmdCan(byte[] data){
		mCanProxy.sendCanDataToMcu(data);
	}

}
