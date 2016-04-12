/**
 * 
 */
package com.zhonghong.launcher.can;

import com.zhonghong.launcher.BaseApplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * @author YC
 * @time 2016-4-11 下午4:39:02
 */
public class CanBootReceiver extends BroadcastReceiver {
	private static final String ACTION_CAN_START_OK = "com.zhonghong.ACTION_CAN_START_OK";
	private String TAG = getClass().getSimpleName();
	private Context mContext;
//	private static final int 
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (ACTION_CAN_START_OK.equals(intent.getAction())){
			Log.i(TAG, "onReceive bindCanService");
			mContext = context;
			mHandler.sendEmptyMessageDelayed(100, 100);
//			BaseApplication.getInstanse().bindCanService();
		}
	}
	
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 100:
				if (BaseApplication.getInstanse() == null)
				{
					Log.i(TAG, "BaseApplication null");
					sendEmptyMessageDelayed(100, 100);
				}
				else
				{
					BaseApplication.getInstanse().bindCanService();
				}
				break;
			}
		};

	};

}
