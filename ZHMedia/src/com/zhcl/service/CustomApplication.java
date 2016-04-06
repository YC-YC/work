/**
 * 
 */
package com.zhcl.service;

import java.io.File;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.tencent.bugly.crashreport.CrashReport;
import com.zh.broadcast.ZuiBroadcast;
import com.zh.uitls.L;
import com.zh.uitls.ThreadForeverManage;
import com.zhcl.filescanner.FileScanner;
import com.zhcl.filescanner.LocalFileCacheManager;
import com.zhcl.media.CurrentPlayManager;
import com.zhcl.media.RecordManager;
import com.zhcl.media.SongManager;
import com.zhcl.remote.ui.MusicRemoteCtrl;
import com.zhcl.service.service.client.AudioPlayerClient;
import com.zhonghong.zhmedia.LocalFragmentManager;
import com.zhonghong.zui.NotifyLife;

/**
 * 应用初始化部分
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author zhonghong.chenli
 * @date 2016-1-10 下午2:25:15
 */
public class CustomApplication extends Application {
	private static final String tag = CustomApplication.class.getSimpleName();
	private Context context;
	/** 断点文件存放位置 */
	public static String recordPath = "/mnt/sdcard/zhmusic/songInfoState.db";
	/** 播放列表存放位置 */
	public static String recordMenuListPath = "/mnt/sdcard/zhmusic/songMenuList.db";
	@Override
	public void onCreate() {
		L.e(tag, "onCreate!");
		super.onCreate();
		context = this;
		String pName = getCurProcessName(context);
		if("com.zhonghong.zhmedia:audioplayer".equals(pName)){		//避免多进程启动重复初始化
			return;
		}
		L.e(tag, "getCurProcessName:" + getCurProcessName(context));
		init();
		RecordManager.getInstance().startRecord();
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
		L.e(tag, "onLowMemory!");
	}

	/**
	 * 初始化
	 * 初始化顺序不要调整
	 */
	private void init() {
		L.e(tag, "Application初始化");
		CrashReport.initCrashReport(this, "900016045", true);
		NotifyLife.getInstance().init(context);
		ThreadForeverManage.getInstance();
		LocalFileCacheManager.getInstance().init(this, "/mnt/sdcard/zhmusic" + File.separator + "local_dir.db");
		FileScanner.getInstance(context);
		LocalFragmentManager.getInstance();
		AudioPlayerClient.getInstance().init(context); // 初始化媒体播放对象
		CurrentPlayManager.getInstance().init(context);
		SongManager.getInstance().init(context, "/mnt/sdcard/zhmusic" + File.separator + "zhclmusic.db");
		initImageLoader(context);
		new ZuiBroadcast(this).registerReceiver(); // 注册广播
		MusicRemoteCtrl.getInstance().init(context);	//远程UI控制对象
	}

	public void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
		config.threadPriority(Thread.NORM_PRIORITY - 2);
		config.denyCacheImageMultipleSizesInMemory();
		config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
		config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
		config.tasksProcessingOrder(QueueProcessingType.LIFO);
		config.writeDebugLogs(); // Remove for release app

		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config.build());
	}

	String getCurProcessName(Context context) {
		int pid = android.os.Process.myPid();
		ActivityManager mActivityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
				.getRunningAppProcesses()) {
			if (appProcess.pid == pid) {

				return appProcess.processName;
			}
		}
		return null;
	}

}
