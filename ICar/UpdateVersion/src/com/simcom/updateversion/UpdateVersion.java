package com.simcom.updateversion;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.zhonghong.service.IUpdate;

public class UpdateVersion extends Activity {

	private static final String UPDATE_FILE_NAME = "delta_1_2.delta";
	private static final String UPDATE_SERVER_ACTION = "com.zhonghong.onekey_update";
	/**升级， 1： 开始， 2：重启， 3设断电标识*/
	private static final int UPDATE_CMD = 0x6661;

	private static final String TAG = "UpdateVersion";
	
	private IUpdate IUpdateService;
	
	private Button mBtnStart,mBtnExit,mBtnVersion,mBtnCopy;
	private TextView mState;
	Context mcontext;
	ProgressDialog pd1;
	
	private Handler mHandler;
	private Handler mHandlerMain;
	private CopyFileThread mCopyFileThread;
	static String Version;
	int nReturn = 0;	
	private final int DISMISS_PRO = 10;
	private final int REBOOT_SYSTEM = 11;
	private String state;
	int CopyfileFlag = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mcontext = this;
		bindZuiService();
		initView();
		
		String stopRilArgs[] = {"stop", "ril-daemon"};
		String result = Utils.process(stopRilArgs);
		Log.i(TAG, "stopRilArgs rsult = " + result);
		mHandlerMain = new MainHandler();
		CopyfileFlag = 0;
		nReturn =0;
		String updateFile = getUpdateFile();
		if (!TextUtils.isEmpty(updateFile)){
			state = "升级文件为：" + updateFile;
			int indexOf = updateFile.lastIndexOf("/");
			String folder = updateFile.substring(0, indexOf);
			Log.i(TAG, "getFolder = " + folder);
			if (qset_Image_path(folder) == 1){
				state += "\r\n文件设置成功,请按Copy按键进行复制";
			}
			else{
				state += "\r\n文件出错";
			}
		}
		else{
			state = "\r\n无升级文件";
		}
		
		mState.setText(state);
	}
	

	protected void onStart() {
		super.onStart();
	}

	protected void onRestart() {
		super.onRestart();
	}

	protected void onResume() {
		super.onResume();
	}

	protected void onPause() {
		super.onPause();
	}

	protected void onStop() {
		super.onStop();
	}

	protected void onDestroy() {
		unbindZuiService();
		super.onDestroy();
	}

	private OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.btn_version:
				getVersion();
				break;
			case R.id.btn_start:
				onStartUpdate();
				break;
			case R.id.btn_copy:
				onCopyFile();
				break;
			case R.id.btn_exit:
				finish();
				break;
			default:
				break;
			}
		}
	};

	
	private class MainHandler extends Handler {
		public void handleMessage(Message msg) {
			Log.v("msg.what", "what=" + msg.what);
			switch (msg.what) {
			case DISMISS_PRO: {
				if (CopyfileFlag == 1) {
					pd1.dismiss();
					state += "\r\n复制成功,请开始升级！";
					mState.setText(state);
				} else {
					state += "\r\n复制失败！！！";
					mState.setText(state);
					pd1.dismiss();
				}
				break;
			}
			case REBOOT_SYSTEM:
				resetSystem();
				break;
			default:
				break;
			}
		}
	}
	
	private void initView() {
		mBtnStart = (Button) findViewById(R.id.btn_start);
		mBtnStart.setOnClickListener(onClickListener);

		mBtnExit = (Button) findViewById(R.id.btn_exit);
		mBtnExit.setOnClickListener(onClickListener);
        
		mBtnVersion = (Button) findViewById(R.id.btn_version);
		mBtnVersion.setOnClickListener(onClickListener);
		
		mBtnCopy = (Button) findViewById(R.id.btn_copy);
		mBtnCopy.setOnClickListener(onClickListener);
		
		mState = (TextView) findViewById(R.id.state);
		pd1 = new ProgressDialog(mcontext);
	}
	
	private String getUpdateFile(){
		String path = "mnt/USB/" + UPDATE_FILE_NAME;
		if (Utils.hasFileExits(path)){
			return path;
		}
		path = "mnt/USB1/" + UPDATE_FILE_NAME;
		if (Utils.hasFileExits(path)){
			return path;
		}
		path = "mnt/USB2/" + UPDATE_FILE_NAME;
		if (Utils.hasFileExits(path)){
			return path;
		}
		return null;
	}
	
	private void bindZuiService(){
		if (IUpdateService == null){
			Intent intent = new Intent(UPDATE_SERVER_ACTION);
			bindService(intent, conn, Service.BIND_AUTO_CREATE);
		}
		
	}
	
	private void unbindZuiService(){
		try {
			unbindService(conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private ServiceConnection conn = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.i(TAG, "onServiceDisconnected");
			IUpdateService = null;			
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			IUpdateService = IUpdate.Stub.asInterface(service);
		}
	};
	
	
	private void resetSystem(){
		
		if (IUpdateService != null){
			try {
				IUpdateService.updateItem(UPDATE_CMD, 2);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		powerManager.reboot("upgrade reboot");
	}
	
	private void getVersion() {
		Version = qgetVersion();
		//qtty_get_version();// maxx temp
		AlertDialog ad=new AlertDialog.Builder(UpdateVersion.this).create();  
		ad.setTitle("version");  
		ad.setIcon(R.drawable.ic_launcher);  
		ad.setMessage("current version:"+Version);  
		ad.setButton("cancel", new DialogInterface.OnClickListener() {  
			@Override  
			public void onClick(DialogInterface dialog, int which) {            
				}  
		});  
		ad.setButton2("ok", new DialogInterface.OnClickListener() {  
			@Override  
			public void onClick(DialogInterface dialog, int which) {      
				}  
			}); 
		ad.show();
	}
	
	private void onStartUpdate() {
		CopyfileFlag = 0;
	    nReturn = gProcessing();
		Log.v("DownloadThread", "nreturn.." + nReturn);

		if (nReturn == 1) {
			CopyfileFlag = 1;
			state += "\r\n机器将会重启进行升级...";
			mState.setText(state);
			 mHandlerMain.sendEmptyMessageDelayed(REBOOT_SYSTEM, 1*1000);
			// mHandlerMain.sendEmptyMessage(DISMISS_PRO);
		} else {
			CopyfileFlag = 0;
			state += "\r\n更新失败！！！";
			mState.setText(state);
		}
	}
	
	private void onCopyFile() {
		showProgressDialog();
	 	
		if (mCopyFileThread != null) {
			mCopyFileThread.interrupt();
		}
		mCopyFileThread = new CopyFileThread();
		mCopyFileThread.start();
	}
	
	public class CopyFileThread extends Thread {
		public void run() {
			int nReturn = 0;
			// download
			// Thread.sleep(1000);
			CopyfileFlag = 0;
			nReturn = gCopyFile();
			Log.v("DownloadThread", "nreturn.." + nReturn);
			//pd1.dismiss();
			if (nReturn == 1) {
				Log.v("sendEmptyMessage", "nReturn" + nReturn);
				CopyfileFlag = 1;
				 mHandlerMain.sendEmptyMessage(DISMISS_PRO);
			} else {
				CopyfileFlag = 0;
				 mHandlerMain.sendEmptyMessage(DISMISS_PRO);
			}
		
		}
	}
	void pd1_init() {
		pd1.setTitle("Copy file");
		pd1.setCancelable(false);
		pd1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd1.setIndeterminate(false);
	}
	void showProgressDialog() {
		//if (pd1 == null) {
			pd1.setMessage("copying packet,please wait...");
			//pd1_init();
		//}
		pd1_init();
		pd1.show();
	}
	
	static {
		System.loadLibrary("updateversion");
	}
	public native int gProcessing();
	public native int qset_Image_path(String Imagefilepath);
	public native String qgetVersion();  
	//public native int qget_download_state();
	public native int gCopyFile();
}