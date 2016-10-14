package com.simcom.updateversion;

import java.io.File;

import com.simcom.updateversion.CommandExecution.CommandResult;

import android.os.Bundle;
import android.os.Message;
import android.os.Environment;
import android.os.PowerManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.app.ProgressDialog;
import android.os.Handler;

public class UpdateVersion extends Activity {

	private static final String UPDATE_FILE_NAME = "delta_1_2.delta";

	private static final String TAG = "UpdateVersion";
	
	
	private Button mBtnStart;
	private Button mBtnExit;
	private Button mBtnVersion;
	private Button mBtnCopy;
	private TextView mState;
//	private TextView mTextImage;
//	private TextView mTextCopy;
	static String sdcard_path;
	Context mcontext;
	ProgressDialog pd1;
	
	private Handler mHandler;
	private Handler mHandlerMain;
	private CopyFileThread mCopyFileThread;
	//private DownloadThread mDownloadThread;
	static String gs_sdcard_path;
	static String VersionFilePath;
	static String fileName;
	static String Version;
	int nReturn = 0;	
	private final int DISMISS_PRO = 10;
	private final int REBOOT_SYSTEM = 11;
	private String state;
	int CopyfileFlag = 0;
	static String updateVersionFileFolder = "sim6320_fota";
	static String updateVersionFilePath = "/sim6320_fota";
	static {
		System.loadLibrary("updateversion");
	}
	public native int gProcessing();
	public native int qset_Image_path(String Imagefilepath);
	public native String qgetVersion();  
	//public native int qget_download_state();
	public native int gCopyFile();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mcontext = this;
		initView();
		
		/*String[] commands = new String[]{
				"chmod 777 /dev/ttyUSB2",
				"stop ril-daemon"
		};*/
//		CommandResult result = CommandExecution.execCommand("stop ril-daemon", false);
//		Log.i(TAG, "exec result = " + result.);
		
//		String chmodArgs[] = {"chmod", "777" , "/dev/ttyUSB2"};
//		String result = Utils.process(chmodArgs);
//		Log.i(TAG, "chmodArgs rsult = " + result);
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
				state += "\r\n文件设置成功,请按Copy按键进行复制\r\n";
			}
			else{
				state += "\r\n文件出错\r\n";
			}
		}
		else{
			state = "\r\n无升级文件\r\n";
		}
		
		mState.setText(state);
		/*String sDStateString = Environment.getExternalStorageState();
		if (sDStateString.equals(Environment.MEDIA_MOUNTED)) {
			try {
				boolean fileExist = true;
				// File ImageFilePath = new File(SDPATH +
				// updateVersionFilePath);
					File SDFile = Environment.getExternalStorageDirectory();
					String gs_sdcard_path = SDFile.getAbsolutePath();
					
					String VersionFilePathTemp = gs_sdcard_path+ updateVersionFilePath;
					File ImageFilePath = new File(gs_sdcard_path,updateVersionFileFolder);
					ImageFilePath.mkdir();
					mTextImage.setText(VersionFilePathTemp);
					Log.v("CONFIGPATH", "ImageFilePath = " + VersionFilePathTemp);
				    qset_Image_path(VersionFilePathTemp);
			} catch (Exception e) {
				System.out
						.println("======== getFileList(out) Exception========");

				return;
			}
		} else {
			int nRet;
			Toast.makeText(mcontext, "No SDcard!", Toast.LENGTH_LONG)
					.show();
			String VersionFilePathNOsd = "data/user/sim6320";
			
			mTextImage.setText(VersionFilePathNOsd);
			
			 File FilePath = new File(VersionFilePathNOsd);
			FilePath.mkdir();
			// mTextImage.setText(VersionFilePath);
			nRet = qset_Image_path(VersionFilePathNOsd);
			if (nRet == 1) {
				Log.v("DownloadThread", "VersionFilePath = "
						+ VersionFilePathNOsd);

			} else {
				Toast.makeText(mcontext, "Set path failed",
						Toast.LENGTH_LONG).show();

			}
		}*/
	}
	/**
	 * 
	 */
	private void initView() {
		mBtnStart = (Button) findViewById(R.id.btn_start);
		mBtnStart.setOnClickListener(qdlButtonStart);

		mBtnExit = (Button) findViewById(R.id.btn_exit);
		mBtnExit.setOnClickListener(qdlButtonExit);
        
		mBtnVersion = (Button) findViewById(R.id.btn_version);
		mBtnVersion.setOnClickListener(qdlButtonVersion);
		
		mBtnCopy = (Button) findViewById(R.id.btn_copy);
		mBtnCopy.setOnClickListener(qdlButtonCopy);
		
		mState = (TextView) findViewById(R.id.state);
//		mTextImage = (TextView) findViewById(R.id.imagefolder);
//		mTextCopy = (TextView) findViewById(R.id.textcopy);
//		mTextCopy.setText("Before update version,file must be copyed to modem"+"\r\n"+
//						  "Please press copy button to copy file:");
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

	protected void onStart() {
		super.onStart();
		
		// mBtnStart.setClickable(false);
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
		super.onDestroy();
	}

	private Button.OnClickListener qdlButtonStart = new Button.OnClickListener() {
		public void onClick(View v) {
			CopyfileFlag = 0;
		    nReturn = gProcessing();
			Log.v("DownloadThread", "nreturn.." + nReturn);

			if (nReturn == 1) {
				CopyfileFlag = 1;
				state += "\r\n机器将会重启进行升级...";
				mState.setText(state);
				 mHandlerMain.sendEmptyMessageDelayed(REBOOT_SYSTEM, 1*1000);
//				Toast.makeText(mcontext, "Modem will be reset to update version!", Toast.LENGTH_LONG)
//				.show();

				// mHandlerMain.sendEmptyMessage(DISMISS_PRO);
			} else {
				CopyfileFlag = 0;
				state += "\r\n更新失败！！！";
				mState.setText(state);
//				Toast.makeText(mcontext, "Update version falied!", Toast.LENGTH_LONG)
//				.show();
			}
		}
		// new DownloadThread().start();
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
//					Toast.makeText(mcontext, "file copy sucess!!",
//							Toast.LENGTH_LONG).show();
				} else {
					state += "\r\n复制失败！！！";
					mState.setText(state);
//					Toast.makeText(mcontext, "file copy failed",
//							Toast.LENGTH_LONG).show();
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
	
	private void resetSystem(){
		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		powerManager.reboot("upgrade reboot");
	}
	
	
	private Button.OnClickListener qdlButtonVersion = new Button.OnClickListener() {
		public void onClick(View v) {
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
	};
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
				//Toast.makeText(mcontext, "file copy sucess!", Toast.LENGTH_LONG)
				//.show();
				 mHandlerMain.sendEmptyMessage(DISMISS_PRO);
			} else {
				CopyfileFlag = 0;
				 mHandlerMain.sendEmptyMessage(DISMISS_PRO);
				//Toast.makeText(mcontext, "file copy sucess!", Toast.LENGTH_LONG)
				//.show();			
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
	private Button.OnClickListener qdlButtonCopy = new Button.OnClickListener() {
		public void onClick(View v) {
			// show progress
		 	showProgressDialog();
		 	
			if (mCopyFileThread != null) {
				mCopyFileThread.interrupt();
			}
			mCopyFileThread = new CopyFileThread();
			mCopyFileThread.start();
		}
	};
	private Button.OnClickListener qdlButtonExit = new Button.OnClickListener() {
		public void onClick(View v) {
			//qtty_get_version();// maxx temp
			finish();
		}
	};
}