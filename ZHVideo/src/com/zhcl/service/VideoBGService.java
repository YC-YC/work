/**
 * 
 */
package com.zhcl.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.zh.broadcast.ZuiBroadcast;
import com.zh.uitls.L;
import com.zh.uitls.Utils;
import com.zhcl.filescanner.FileScanner;
import com.zhcl.service.service.client.VideoPlayerClient;
import com.zhcl.video.CurrentPlayManager;
import com.zhcl.video.RecordManager;
import com.zhcl.video.VideoManager;
import com.zhcl.zhvideo.LocalFragmentManager;

/** 
 * 后台服务
 * @author zhonghong.chenli
 * @date 2015-11-22 上午1:11:50 
 */
public class VideoBGService extends Service {
	private static final String tag = "VideoBGService";
	public static boolean isCreate;
	Context context;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		L.e(tag, "onCreate~~~~~~~~~~~~~~~");
		context = this;
		Utils.getInstance().endUseTime("启动服务");
//		init();
//		RecordManager.getInstance().startRecord();
		isCreate = true;
	}  
	
//	/**
//	 * 初始化
//	 */
//	private void init(){
//		FileScanner.getInstance(context);
//		LocalFragmentManager.getInstance();
//		VideoPlayerClient.getInstance().init(context);	//初始化媒体播放对象
//		CurrentPlayManager.getInstance().init(context);
//		VideoManager.getIntance(context);
//		initImageLoader(context);
//		new ZuiBroadcast(this).registerReceiver(); //注册广播
//	}
//
//	public  void initImageLoader(Context context) {
//		// This configuration tuning is custom. You can tune every option, you may tune some of them,
//		// or you can create default configuration by
//		//  ImageLoaderConfiguration.createDefault(this);
//		// method.
//		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
//		config.threadPriority(Thread.NORM_PRIORITY - 2);
//		config.denyCacheImageMultipleSizesInMemory();
//		config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
//		config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
//		config.tasksProcessingOrder(QueueProcessingType.LIFO);
//		config.writeDebugLogs(); // Remove for release app
//
//		// Initialize ImageLoader with configuration.
//		ImageLoader.getInstance().init(config.build());
//	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		isCreate = false;
		RecordManager.getInstance().endRecord();
	}
	
}
