/**
 * 
 */
package com.zhonghong.mediascanner.service;

import java.io.File;
import java.io.FileFilter;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.zhonghong.mediascanerlib.ScannerConst;
import com.zhonghong.mediascanerlib.aidl.ScannerCallback;
import com.zhonghong.mediascanerlib.aidl.ScannerInterface.Stub;
import com.zhonghong.mediascanner.ScanManager;
import com.zhonghong.mediascanner.utils.L;

/**
 * @author YC
 * @time 2016-12-8 下午9:04:20
 * TODO:
 */
public class ScannerService extends Service implements Runnable{
	
	private static final String TAG = "ScannerService";
	private volatile Looper mServiceLooper;
    private volatile ServiceHandler mServiceHandler;
    private ScanManager mScanManager;
	
	@Override
	public IBinder onBind(Intent intent) {
		waitForServiceHandler();
		return myBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mScanManager = new ScanManager();
		mScanManager.onObjectCreate();
		new Thread(this, "ScannerService").start();
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		waitForServiceHandler();
		if (intent != null){
			String cmd = intent.getStringExtra(ScannerConst.SService_KEY_CMD);
			String path = intent.getStringExtra(ScannerConst.SService_KEY_PATH);
			Log.i(TAG, "cmd = " + cmd + ", path = " + path);
			if (ScannerConst.SService_CMD_MOUNT.equals(cmd)){
				Message msg = mServiceHandler.obtainMessage(ServiceHandler.MSG_START_SCAN_DEVICE);
				Bundle bundle = new Bundle();
				bundle.putString(ServiceHandler.KEY_PATH, path);
				msg.setData(bundle);
				mServiceHandler.sendMessage(msg);
			}
			else if (ScannerConst.SService_CMD_UNMOUNT.equals(cmd)){
				Message msg = mServiceHandler.obtainMessage(ServiceHandler.MSG_UNMOUNT_DEVICE);
				Bundle bundle = new Bundle();
				bundle.putString(ServiceHandler.KEY_PATH, path);
				msg.setData(bundle);
				mServiceHandler.sendMessage(msg);
			}
		}
		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}
	
	private Stub myBinder = new Stub() {
		
		@Override
		public void getFileInfo(String device, int type, ScannerCallback callback)
				throws RemoteException {
			if (!TextUtils.isEmpty(device) && callback != null){
				mScanManager.getFileInfo(device, type, callback);
			}
			else{
				Log.e(TAG, "device or callback == null");
			}
		}
	};
	
	@Override
	public void onDestroy() {
		waitForServiceHandler();
		mScanManager.onObjectDestory();
		mServiceLooper.quit();
		super.onDestroy();
	}
	
	@Override
	public void run() {
		Process.setThreadPriority(Process.THREAD_PRIORITY_DISPLAY);
		Looper.prepare();
		mServiceLooper = Looper.myLooper();
		mServiceHandler = new ServiceHandler();
		Looper.loop();
	}
	
	/**
	 * 等待服务Handle初始化完成
	 */
	private void waitForServiceHandler() {
		while (mServiceHandler == null) {
			synchronized (this) {
				try {
					wait(100);
				} catch (InterruptedException e) {
				}
			}
		}
	}
	
	/**
	 * 消息处理
	 */
	private class ServiceHandler extends Handler {
		
		public final static String KEY_PATH = "path"; 
		
		/**开始扫描*/
		public final static int MSG_START_SCAN_DEVICE = 1; 
		
		public final static int MSG_UNMOUNT_DEVICE = 2; 
		
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_START_SCAN_DEVICE:{
				/*File dir = new File("/mnt/USB1/TEST");
				L.startTime("只扫描文件夹");
				File[] subFiles = dir.listFiles(new FileFilter() {
					
					@Override
					public boolean accept(File pathname) {
						if (pathname.isDirectory()){
							return true;
						}
						return false;
					}
				});
				L.i(TAG, "文件夹个数为：" +  subFiles.length);
				L.endUseTime("只扫描文件夹");*/
				String path = msg.getData().getString(KEY_PATH);
				mScanManager.startScanDevice(path);
			}break;
			case MSG_UNMOUNT_DEVICE:{
				String path = msg.getData().getString(KEY_PATH);
				mScanManager.unMountDevice(path);
			}break;

			default:
				break;
			}
		}
	};
	
	
}
