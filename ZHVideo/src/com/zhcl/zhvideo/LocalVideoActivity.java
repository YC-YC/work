/**
 * 
 */
package com.zhcl.zhvideo;

import java.io.File;
import java.util.HashSet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.zh.uitls.L;
import com.zh.uitls.Utils;
import com.zhcl.dao.RecordPlayStateInfo;
import com.zhcl.filescanner.FileScanner;
import com.zhcl.filescanner.FilterUtil;
import com.zhcl.filescanner.FilterUtil.IDirFilter;
import com.zhcl.filescanner.FilterUtil.IFileFilter;
import com.zhcl.filescanner.LocalFileCacheManager.ScannerListener;
import com.zhcl.service.PlayerCode;
import com.zhcl.ui.video.ChildCallBack;
import com.zhcl.ui.video.HostCallBack;
import com.zhcl.ui.video.LocalVideoFragment;
import com.zhcl.video.RecordManager;
import com.zhcl.video.VideoManager;
import com.zhonghong.zhvideo.R;
import com.zhonghong.zui.ZuiConn;

/**
 * 只有第一次进入时是同步数据库中数据，后续的改变需要提交一个改变的集合，更新数据库的同时，同步进UI
 * @author ChenLi
 * 
 */
public class LocalVideoActivity extends FragmentActivity implements HostCallBack{
	private static final String tag = "LocalVideoActivity";
	Context context;
	/** ChildCallBack集合 */
	private HashSet<ChildCallBack> mChildCallBackSet = new HashSet<ChildCallBack>();
	private boolean isSync = false;
	/** 背景 */
	public View bgView;
	View topBase;
	/** 显示所有页面上面的view*/
	View gtop;
	Handler handler;
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		L.i(tag, "onCreate");
		context = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);//
		initFilter();
		initView();
		initBGService();
		handler = new Handler();
		//是否继续上次断电播放？
		judgeOpenPlayPage();
	}
	
	/**
	 * 判断是否判断继续播放
	 */
	private void judgeOpenPlayPage(){
		if(isResumePlay()){
			Toast.makeText(this, "继续上一次播放", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * 读断点
	 * @return
	 */
	private boolean isResumePlay(){
		boolean isResume = false;
		RecordPlayStateInfo  mRecordPlayStateInfo  = RecordManager.getRecordPlayStateInfo();
		do{
			if(mRecordPlayStateInfo == null){
				isResume = false;
				break;
			}
			if(mRecordPlayStateInfo.isPlay()){
				isResume = true;
				break;
			}
		}while(false);
		
		return isResume;
	}

	private void initBGService(){
		Utils.getInstance().startTime("启动服务");
		Intent it = new Intent("com.zhcl.videobgservice");
		this.startService(it);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		L.i(tag, "onResume");
		fileScanner();
		ZuiConn.getInstance().enterPage();
	}

	@SuppressLint("NewApi")
	private void initView() {
		setContentView(R.layout.local_video_activity);
		loadBodyFragment();
		bgView = (View)findViewById(R.id.bg);
		gtop = findViewById(R.id.gtop);
		topBase = (View)findViewById(R.id.topBase);
		Utils.getInstance().updateViewHToStatusH(topBase);
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.main_bg);
		if(bitmap != null){
			Utils.getInstance().blurBitmap(bitmap, 30, 6/*9*/);
			bgView.setBackground(new BitmapDrawable(bitmap));
		}
	}
	
	LocalVideoFragment mLocalVideoFragment;
	private void loadBodyFragment(){
		if(mLocalVideoFragment == null){
			L.e(tag, "mLocalVideoFragment == null");
			mLocalVideoFragment = new LocalVideoFragment();
		}
		FragmentManager mFragmentManager = getSupportFragmentManager();
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		ft.replace(R.id.currenBody, mLocalVideoFragment, mLocalVideoFragment.hashCode() + "");
//		ft.addToBackStack(null);
		ft.commit(); 
	} 
	
	/**
	 * 初始化过滤条件
	 */
	private void initFilter(){    
		/** 需过滤的目录 */ 
		final String[] filterDir = { "import/", "diagnosis/", "album/", "miniAlbum/", "singer/", "miniSinger/", "config/", "skin/", "oltmp/", "lyric/", "qrc/", "UE/", "tmp/", "head/", "splash/", "imageex/", "upgrade/", "DtsUpgrade/", "log/", "ringtones/", "speedtest/", "fingerPrint/", "screenshot/", "song/", "mv/", "offline/", "cache/", "firstPiece/", "dts/", "dts_auto/", "encrypt/", "eup/", "qbiz/", "localcover/", "downloadalbum/", "welcome/", "report/", "images/" };
		FilterUtil.setSupportedFileTypes(new String[] {"AVI", "WMV", "MPG", "MP4", "RMVB", "TS", "M2TS", "TP", "MOV", "VOB", "MKV", "3GP", "3GA", "ASF", "DIVX", "FLV", "RM", "MPEG", "M4V", "M2V",/* "DAT"*/});
		
		//设置文件夹过滤条件
		FilterUtil.setDirFilter(new IDirFilter() {
			@Override
			public boolean match(String paramString) {
				for(String filterStr : filterDir){
					if(paramString.contains(filterStr)){  
//						Log.e(tag, "过滤的：" + paramString);
						return false;  
					} 
				} 
				Log.e(tag, "dir:" + paramString); 
				return true;
			}
		}); 
		//设置过滤文件大小
		final long fileSizeFilter = 200 * 1024;
		//设置文件过滤条件
		FilterUtil.setFileFilter(new IFileFilter() {
			@Override
			public boolean match(String paramString) {
				//文件大小过滤
				if((new File(paramString)).length() < fileSizeFilter){
					return false;
				}
				return true;
			}
		});
	}  
	/**   
	 * 扫描目录、文件
	 * 扫描模式：FileScanner.getInstance(MainActivity.this).setScanMode(FileScanner.SCANMODE_DEFUALT);
	 * 扫描所有：FileScanner.getInstance(MainActivity.this).setScanMode(FileScanner.SCANMODE_ALL);
	 */
	private void fileScanner() {
		//注册加载监听
		FileScanner.getInstance(this).setScanListener(mScannerListener);
		FileScanner.getInstance(this).start();   
	} 
	
	/**
	 * 搜索监听
	 */
	private ScannerListener mScannerListener = new ScannerListener() {
		@Override
		public void onScanBegin(boolean bFirst) {
			Log.e(tag, "onScanBegin!!!!!!!!!!!!");
			// do sth here 
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
//					CurrentPlayManager.getInstance().showScanProgress(context);
					gtop.setVisibility(View.VISIBLE);
				}
			});
			setIsSync(false);
		}

		@Override   
		public void onScanEnd(boolean needUpdate) {
//			while(!AudioBGService.isCreate){
//				try {
//					Thread.sleep(20);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			VideoManager.getIntance(context).updataAllVideoInfo();	//同步歌曲信息到数据库
			VideoManager.getIntance(context).loadAllVideoInfo();		//从数据库加载所有歌曲信息
			
			
			handler.post(new Runnable() {
				
				@Override
				public void run() {
//					CurrentPlayManager.getInstance().hideScanProgress();
					gtop.setVisibility(View.GONE);
				}
			});
			setIsSync(true);										//设置数据同步完成
			notifyChildScanfEnd();									//通知child可以更新列表
			Log.e(tag, "--------------onScanEnd-----------");
		}
	};
	
	/**
	 * 设置是否同步完成  
	 * @param isSync
	 */
	private void setIsSync(boolean isSync){
		this.isSync = isSync;
	}
	
	/**
	 * 通知所有child scanf 完成
	 */  
	private void notifyChildScanfEnd(){   
		for(ChildCallBack mChildCallBack : mChildCallBackSet){
			mChildCallBack.notifyInfo(HostCallBack.STATE_SUCCESS, null);
		}
	} 
	
	@Override
	public int getState() {
		return isSync ? HostCallBack.STATE_SUCCESS : HostCallBack.STATE_FAIL;
	}

	@Override
	public String connString(int cmd, String value) {
		// TODO 暂未使用
		return null;
	}


	@Override
	public void addChildCallBack(ChildCallBack mChildCallBack) { 
		synchronized (mChildCallBack) {
			mChildCallBackSet.add(mChildCallBack);
		}
	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		//不执行如下这名，否则会有返回后再加载时出现加载不出的问题
//		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		L.w(tag, "onDestroy");
		FileScanner.getInstance(this).setScanListener(null);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		L.e(tag, "onNewIntent");
		if(!isResumePlay()){
			return;
		}
		Intent it = new Intent(context, VideoPlayAcitivity.class);
		it.putExtra("playstate", PlayerCode.VIDEO_PLAY_TYPE_RESUME);		//通知播放断点
		it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION); 
		context.startActivity(it);
		overridePendingTransition(0, 0);  
	}
	
}
