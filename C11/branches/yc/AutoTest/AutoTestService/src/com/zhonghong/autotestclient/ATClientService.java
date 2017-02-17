/**
 * 
 */
package com.zhonghong.autotestclient;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.zhonghong.autotestlib.ATCallback;
import com.zhonghong.autotestlib.ATConn;
import com.zhonghong.autotestlib.bean.ATConst;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

/**
 * @author YC
 * @time 2016-12-6 下午6:05:37
 * TODO:
 */
public class ATClientService extends Service {

	
	private static final String TAG = "ATClientService";
	
	private ATConn mATConnService = null;
	private GPSManager mGPSManager = null;
	private LogicManager mLogicManager = null;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "onCreate");
		mGPSManager = new GPSManager(this);
		mLogicManager = new LogicManager();
		Intent it = new Intent(ATConst.Service.ACTION);
		bindService(it, conn, BIND_AUTO_CREATE);
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy");
		if (mATConnService != null){
			try {
				mATConnService.unregisterATClient(ATConst.GPS.TYPE_GPS, mATCallback);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		try {
			unbindService(conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}

	@Override
	public void onLowMemory() {
		Log.i(TAG, "onLowMemory");
		super.onLowMemory();
	}
	
	private ATCallback mATCallback = new ATCallback() {
		
		@Override
		public IBinder asBinder() {
			return null;
		}
		
		@Override
		public boolean onDispatch(int type, int cmd, String info)
				throws RemoteException {
			Log.i(TAG, "type = " + type + ", cmd = " + cmd + ", info = " + info);
			switch (type) {
			case ATConst.GPS.TYPE_GPS:{
				switch (cmd) {
				case ATConst.GPS.CMD_GPS_SET_GPS_ON_OFF:
					if ("on".equals(info)){
						
					}
					else if ("off".equals(info)){
						
					}
					return true;
				case ATConst.GPS.CMD_GPS_GET_GPS_NUMBER:
					if ("used_num".equals(info)){
						
					}
					else if ("visible_num".equals(info)){
						
					}

					return true;
				case ATConst.GPS.CMD_GPS_GET_GPS_TIME:
					String time = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")
					.format(new Date(mGPSManager.getGPSTime()));
					mATConnService.onResponse(ATConst.GPS.TYPE_GPS, 
							ATConst.GPS.CMD_GPS_GET_GPS_TIME, time);
					return true;

				case ATConst.GPS.CMD_GPS_GET_GPS_INFO:
					if ("direction".equals(info)){
						
					}
					else if ("speed".equals(info)){
						
					}

					return true;
				}
			}
				break;
			case ATConst.Logic.TYPE_LOGIC:{
				return mLogicManager.onProcess(cmd, info);
				
			}
			default:
				break;
			}
			return false;
		}
	};
	
	
	ServiceConnection conn = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mATConnService = null;
			mLogicManager.setATConnService(mATConnService);
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mATConnService = ATConn.Stub.asInterface(service);
			mLogicManager.setATConnService(mATConnService);
			try {
				mATConnService.registerATClient(ATConst.GPS.TYPE_GPS, mATCallback);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	};
	
	

}
