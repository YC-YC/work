package com.example.testscanner;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.zhonghong.mediascanerlib.ScannerConst;
import com.zhonghong.mediascanerlib.aidl.ScannerCallback;
import com.zhonghong.mediascanerlib.aidl.ScannerInterface;
import com.zhonghong.mediascanerlib.filebean.AudioInfo;
import com.zhonghong.mediascanerlib.filebean.ImageInfo;
import com.zhonghong.mediascanerlib.filebean.VideoInfo;

public class MainActivity extends Activity {

	protected static final String TAG = "TestScanner";

	private ScannerInterface scannerInterface = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	public void doClick(View view){
		switch (view.getId()) {
		case R.id.bindservice:{
			Intent intent = new Intent(ScannerConst.SService_ACTION);
			
			bindService(intent, conn, BIND_AUTO_CREATE);
			
		}
			break;
		case R.id.unbindservice:{
			unbindService(conn);
		}
		break;
		case R.id.getfileinfo:
			if (scannerInterface != null){
				try {
					scannerInterface.getFileInfo("/mnt/USB1", ScannerConst.FileType.FILE_TYPE_AUDIO,scannerCallback);
					scannerInterface.getFileInfo("/mnt/USB1", ScannerConst.FileType.FILE_TYPE_VIDEO,scannerCallback);
					scannerInterface.getFileInfo("/mnt/USB1", ScannerConst.FileType.FILE_TYPE_IMAGE,scannerCallback);
					
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			break;

		default:
			break;
		}
	}
	
	private ScannerCallback scannerCallback = new ScannerCallback.Stub() {

		@Override
		public void onStart(String device, int type) throws RemoteException {
			//适合做一些初始化操作
			Log.i(TAG, "开始获取:" + device);
		}
		
		@Override
		public void onEnd(String device, int type, int code)
				throws RemoteException {
			//适合做一些完成的操作，code为相关状态
			Log.i(TAG, "结束获取:" + device + ", code = " +  code);
		}

		@Override
		public void onGetFileInfo(String device, int type, String fileInfo)
				throws RemoteException {
			switch (type) {
			case ScannerConst.FileType.FILE_TYPE_AUDIO:{
				AudioInfo audioInfo = new Gson().fromJson(fileInfo, AudioInfo.class);
				Log.i(TAG, "获取到的音乐信息为:" + audioInfo.toString());
			}break;
			case ScannerConst.FileType.FILE_TYPE_VIDEO:{
				VideoInfo videoInfo = new Gson().fromJson(fileInfo, VideoInfo.class);
				Log.i(TAG, "获取到的视频信息为:" + videoInfo.toString());
			}break;
			case ScannerConst.FileType.FILE_TYPE_IMAGE:{
				ImageInfo imageInfo = new Gson().fromJson(fileInfo, ImageInfo.class);
				Log.i(TAG, "获取到的图片信息为:" + imageInfo.toString());
			}break;
			default:
				break;
			}
		}

	};
	
	private ServiceConnection conn = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			scannerInterface = ScannerInterface.Stub.asInterface(service);
			Log.i(TAG, "连接上服务");
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			scannerInterface = null;
			Log.i(TAG, "连接断开");
		}
		
	};

}
