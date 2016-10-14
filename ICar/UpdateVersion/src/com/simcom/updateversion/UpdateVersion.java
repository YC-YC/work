package com.simcom.updateversion;

import java.io.File;
import android.os.Bundle;
import android.os.Message;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

	private Button mBtnStart;
	private Button mBtnExit;
	private Button mBtnVersion;
	private Button mBtnCopy;
	private TextView mTextImage;
	private TextView mTextCopy;
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
	//static String updateVersionFileFolder = "update_1111";
	int CopyfileFlag = 0;
	static String updateVersionFileFolder = "sim6320_fota";
	static String updateVersionFilePath = "/sim6320_fota";
	static {
		System.loadLibrary("updateversion");
	}
	//private final int DISMISS_PRO = 10;
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
		mBtnStart = (Button) findViewById(R.id.btn_start);
		mBtnStart.setOnClickListener(qdlButtonStart);

		mBtnExit = (Button) findViewById(R.id.btn_exit);
		mBtnExit.setOnClickListener(qdlButtonExit);
        
		mBtnExit = (Button) findViewById(R.id.btn_version);
		mBtnExit.setOnClickListener(qdlButtonVersion);
		
		mBtnCopy = (Button) findViewById(R.id.btn_copy);
		mBtnCopy.setOnClickListener(qdlButtonCopy);
		
		mTextImage = (TextView) findViewById(R.id.imagefolder);
		mTextCopy = (TextView) findViewById(R.id.textcopy);
		mTextCopy.setText("Before update version,file must be copyed to modem"+"\r\n"+
						  "Please press copy button to copy file:");
		pd1 = new ProgressDialog(mcontext);
		
		mHandlerMain = new MainHandler();
		CopyfileFlag = 0;
		nReturn =0;
		String sDStateString = Environment.getExternalStorageState();
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
		}
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
				Toast.makeText(mcontext, "Modem will be reset to update version!", Toast.LENGTH_LONG)
				.show();

				// mHandlerMain.sendEmptyMessage(DISMISS_PRO);
			} else {
				CopyfileFlag = 0;
				Toast.makeText(mcontext, "Update version falied!", Toast.LENGTH_LONG)
				.show();
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
					Toast.makeText(mcontext, "file copy sucess!!",
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(mcontext, "file copy failed",
							Toast.LENGTH_LONG).show();
					pd1.dismiss();
				}
				break;
			}
			default:
				break;
			}
		}
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