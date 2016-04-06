/**
 * 
 */
package com.zh.broadcast;

import java.util.HashSet;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

import com.zh.uitls.L;
import com.zhcl.filescanner.FileScanner;
import com.zhcl.filescanner.LocalFileCacheManager;
import com.zhcl.media.SongManager;

/**
 * 插拔卡、usb广播处理
 * 
 * @author zhonghong.chenli date:2015-6-12上午9:27:58 <br/>
 */
public class MountBroadcast extends AbsBroadcast {
	private static MountListener mMountListener;
	/**
	 * mount监听 ,目前定位只做UI通知
	 */
	public interface MountListener{
		public void notifyMountInfo(String action, String path);
	}
	
	/**
	 * 注册插拔监听
	 * @param mMountListener
	 */
	public static void setMountListener(MountListener mMountListener){
		MountBroadcast.mMountListener = mMountListener;
	}
	
	private Context context;
	private static String tag = MountBroadcast.class.getSimpleName();
	public static final String MEDIA_EJECT = "android.intent.action.MEDIA_EJECT";
	public static final String MEDIA_MOUNTED = "android.intent.action.MEDIA_MOUNTED";
	public static final String MEDIA_UNMOUNTED = "android.intent.action.MEDIA_UNMOUNTED";
	public static final String MEDIA_SCAN_FILE = Intent.ACTION_MEDIA_SCANNER_SCAN_FILE;//"android.intent.action.MEDIA_SCANNER_SCAN_FILE";
	public static final String MEDIA_SCAN_START = Intent.ACTION_MEDIA_SCANNER_STARTED;
	public static final String MEDIA_SCAN_FINISH = Intent.ACTION_MEDIA_SCANNER_FINISHED;
	
	private static MountBroadcast mMountBroadcast;
	public static MountBroadcast getInstence(){
		return mMountBroadcast;
	}
	{
		if(mMountBroadcast != null){
			throw new IllegalAccessError(tag + ":请保持本对象单例，感谢");
		}
		mMountBroadcast = this;
	}
	public MountBroadcast(Context paramContext) {
		this.context = paramContext;
		ZuiBroadcast.getInstance().registLogicListner(this);
	}

	
	/**
	 * 缓存提交搜索
	 * @param intent
	 */
	private void delayMountScan(String[] strs){
//		L.i(tag, "路劲： " + strs);
		FileScanner.getInstance(context).setScanMode(FileScanner.SCANMODE_MOUNT);
		FileScanner.getInstance(context).setMountPath(strs);
		FileScanner.getInstance(context).start();	
	}
	
	HashSet<String> mountStr = new HashSet<String>();
	
	/**
	 * mount和unmount处理
	 * @param intent
	 */
	private void mountOrUnmount(final Intent intent) {
		if(intent != null){
			mountStr.add(intent.getData().getPath()); 
		}
		Handler handler = LocalFileCacheManager.getInstance().getWorkHandler();
		if(!FileScanner.getInstance(context).isScanfEnd()){
			L.e(tag, "还在搜索");
			if(handler != null){ 
				handler.removeCallbacks(mRunnable);  
				handler.postDelayed(mRunnable, 500);
			}
		}else{   
			L.e(tag, "开始本地扫描");   
			delayMountScan((String[])mountStr.toArray(new String[mountStr.size()]));
			mountStr.clear();
		}
	}
	
	Runnable mRunnable = new Runnable() {
		@Override 
		public void run() { 
			mountOrUnmount(null);
		}
	};

	@Override
	IntentFilter getIntentFilter() {
		IntentFilter localIntentFilter = new IntentFilter();
		localIntentFilter.addAction(MEDIA_MOUNTED);
		localIntentFilter.addAction(MEDIA_UNMOUNTED);
		localIntentFilter.addAction(MEDIA_EJECT);
		localIntentFilter.addAction(MEDIA_SCAN_START);
		localIntentFilter.addAction(MEDIA_SCAN_FINISH);
		localIntentFilter.addAction(MEDIA_SCAN_FILE);
		localIntentFilter.setPriority(1000);
		localIntentFilter.addDataScheme("file");
		return localIntentFilter;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		String action = intent.getAction();
		//Log.e(tag, "action : " + action);
		if (MEDIA_MOUNTED.equals(action)) {
			L.i(tag, "mount 挂载路径为 ： " + intent.getData().getPath());
			SongManager.getInstance().delRemovePath(intent.getData().getPath());
			mountOrUnmount(intent);
			notifyListener(action, intent.getData().getPath());
		} else if (MEDIA_EJECT.equals(action)) {
			L.i(tag, "EJECT 移除SD卡路径为 ： " + intent.getData().getPath());
			SongManager.getInstance().addRemovePath(intent.getData().getPath());
			mountOrUnmount(intent); 
			notifyListener(action, intent.getData().getPath());
		} else if (MEDIA_UNMOUNTED.equals(action)) { // 此广播完全移除完
			L.i(tag, "unmount 移除SD卡路径为 ： " + intent.getData().getPath());
//			SongManager.getInstance().addRemovePath(intent.getData().getPath());
//			mountOrUnmount(intent);
//			notifyListener(action, intent.getData().getPath());
		} else if(MEDIA_SCAN_START.equals(action)){
			Log.e(tag, "开始扫描");
		} else if(MEDIA_SCAN_FINISH.equals(action)){ 
			Log.e(tag, "扫描结束"); 
//			Utils.getInstance().startTime("MEDIA_SCAN_FINISH"); 
//			MainActivity.mount(); 
//			Utils.getInstance().endUseTime("MEDIA_SCAN_FINISH");
		} else if(MEDIA_SCAN_FILE.equals(action)){
			String str = intent.getData().getPath();
//			Log.e(tag, "MEDIA_SCAN_FILE :" + str);
		}
	}
	
	/**
	 * 通知监听
	 */
	private void notifyListener(String action, String path){
		if(MountBroadcast.mMountListener != null){
			MountBroadcast.mMountListener.notifyMountInfo(action, path);
		}
	}

}
