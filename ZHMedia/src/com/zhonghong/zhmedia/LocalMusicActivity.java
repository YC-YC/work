/**
 * 
 */
package com.zhonghong.zhmedia;

import java.io.File;
import java.util.HashSet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.zh.dao.SongInfo;
import com.zh.uitls.L;
import com.zh.uitls.Utils;
import com.zhcl.filescanner.FileScanner;
import com.zhcl.filescanner.FilterUtil;
import com.zhcl.filescanner.FilterUtil.IDirFilter;
import com.zhcl.filescanner.FilterUtil.IFileFilter;
import com.zhcl.filescanner.LocalFileCacheManager.ScannerListener;
import com.zhcl.media.CurrentPlayManager;
import com.zhcl.media.ImageManager;
import com.zhcl.media.CurrentPlayManager.PlayListener;
import com.zhcl.media.SongManager;
import com.zhcl.service.AudioBGService;
import com.zhcl.service.PlayerCode;
import com.zhcl.ui.music.ChildCallBack;
import com.zhcl.ui.music.CurrentPlayFragment;
import com.zhcl.ui.music.HostCallBack;
import com.zhcl.ui.music.LocalMusicFragment;
import com.zhonghong.zhmedia.intent.IntentCtrl;
import com.zhonghong.zui.NotifyLife;

/**
 * 只有第一次进入时是同步数据库中数据，后续的改变需要提交一个改变的集合，更新数据库的同时，同步进UI
 * @author ChenLi
 * 
 */
public class LocalMusicActivity extends FragmentActivity implements HostCallBack{
	private static final String tag = "LocalMusicActivity";
	/** 当前播放歌曲fragment */
	CurrentPlayFragment mCurrentPlayFragment;
	Context context;
	/** ChildCallBack集合 */
	private HashSet<ChildCallBack> mChildCallBackSet = new HashSet<ChildCallBack>();
	private boolean isSync = false;
	/** 背景 */
	public View bgView;
	Handler handler;
	/** 显示所有页面上面的view*/
	View gtop;
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		L.w(tag, "onCreate");
		context = this;
		CurrentPlayManager.getInstance().mainPageRun();
		initConfig();
		requestWindowFeature(Window.FEATURE_NO_TITLE);//
		initFilter();
		initView();
		initBGService();
		handler = new Handler();
//		intentHandler(getIntent());
	}
	
	/**
	 * 初始化配置
	 */
	@SuppressLint("InlinedApi")
	private void initConfig(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
	    } 
	}
	

	private void initBGService(){
		Utils.getInstance().startTime("启动服务");
		L.i(tag, "准备启动服务");
		Intent it = new Intent("com.zhcl.audiobgservice");
		this.startService(it);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		NotifyLife.getInstance().enterPage();
		fileScanner();
		intentHandler(getIntent());
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		NotifyLife.getInstance().exitPage();
	}

	@SuppressLint("NewApi")
	private void initView() { 
		setContentView(R.layout.local_music_activity);
		loadBodyFragment();
		loadCurrentFragment(); 
		bgView = (View)findViewById(R.id.bg);
		gtop = findViewById(R.id.gtop);
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.main_bg);
		if(bitmap != null){
			Utils.getInstance().blurBitmap(bitmap, 30, 6/*9*/);
//			bgView.setBackground(new BitmapDrawable(bitmap));
			
			this.getWindow().setBackgroundDrawable(new BitmapDrawable(bitmap));
		}
	}
	
	LocalMusicFragment mLocalMusicFragment;
	private void loadBodyFragment(){
		 mLocalMusicFragment = new LocalMusicFragment();
		FragmentManager mFragmentManager = getSupportFragmentManager();
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		ft.replace(R.id.currenBody, mLocalMusicFragment, mLocalMusicFragment.hashCode() + "");
//		ft.addToBackStack(null);
		ft.commit(); 
	} 
	
	private void loadCurrentFragment(){
		mCurrentPlayFragment = new CurrentPlayFragment();
		FragmentManager mFragmentManager = getSupportFragmentManager();
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		ft.replace(R.id.currentFragment, mCurrentPlayFragment, mCurrentPlayFragment.hashCode() + "");
//		ft.addToBackStack(null);
		ft.commit(); 
	}
	
	/**
	 * 初始化过滤条件
	 */
	private void initFilter(){    
		/** 需过滤的目录 */ 
		final String[] filterDir = {"import/", "diagnosis/", "album/", "miniAlbum/", "singer/", "miniSinger/", "config/", "skin/", "oltmp/", "lyric/", "qrc/", "UE/", "tmp/", "head/", "splash/", "imageex/", "upgrade/", "DtsUpgrade/", "log/", "ringtones/", "speedtest/", "fingerPrint/", "screenshot/", "song/", "mv/", "offline/", "cache/", "firstPiece/", "dts/", "dts_auto/", "encrypt/", "eup/", "qbiz/", "localcover/", "downloadalbum/", "welcome/", "report/", "images/" };
		FilterUtil.setSupportedFileTypes(new String[] {"MP3", "WMA", "OGG", "AAC", "MA4", "FLAC", "APE", "WAV", "RA", /*"AMR",*/ "M4A", "M4R", "MP2"});
		
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
//				Log.e(tag, "dir:" + paramString); 
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
		setIsSync(false);
	} 
	
	/**
	 * 搜索监听
	 */
	private ScannerListener mScannerListener = new ScannerListener() {
		@Override
		public void onScanBegin(boolean bFirst) {
			Log.e(tag, "onScanBegin!!!!!!!!!!!!");
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
//					CurrentPlayManager.getInstance().showScanProgress(context);
					gtop.setVisibility(View.VISIBLE);
				}
			});
			
		}

		@Override   
		public void onScanEnd(boolean needUpdate) {
			while(!AudioBGService.isCreate){
				try {
					L.e(tag, "服务还未构造");
					Thread.sleep(20);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			CurrentPlayManager.getInstance().setPlayListener(mPlayListener);
			SongManager.getInstance().updataAllSongInfo();		//同步歌曲信息到数据库
			SongManager.getInstance().loadAllMusicInfo();		//从数据库加载所有歌曲信息
			handler.post(new Runnable() {
				@Override
				public void run() {
//					CurrentPlayManager.getInstance().hideScanProgress();
					gtop.setVisibility(View.GONE);
				}
			});
			
			setIsSync(true);											//设置数据同步完成
			notifyChildScanfEnd();										//通知child可以更新列表
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
	 * 播放监听 然后通知给所有子fragment
	 */
	private PlayListener mPlayListener = new PlayListener() {
		@Override
		public void newPlay(SongInfo songInfo) {
			notifyAllChild(HostCallBack.PLAY_LISTENER_PALY_NEW, songInfo);
		}
		
		@Override
		public void complate() { 
			notifyAllChild(HostCallBack.PLAY_LISTENER_PALY_COMPLATE, null);
		}

		@Override
		public void playStateChange(int state) {
			notifyAllChild(HostCallBack.PLAY_LISTENER_PALY_STATE_CH, null);
		}

		@Override
		public String callBackForString(int cmd, String str) {
			// TODO Auto-generated method stub
			notifyAllChild(cmd, str);
			return null;
		}
	};
	
	/**
	 * 通知所有child scanf 完成
	 */  
	private void notifyChildScanfEnd(){   
		notifyAllChild(HostCallBack.STATE_SUCCESS, null);
	} 
	

	/**
	 * 通知所有child（Fragment）
	 */
	private void notifyAllChild(int cmd, Object o){
		for(ChildCallBack mChildCallBack : mChildCallBackSet){
			mChildCallBack.notifyInfo(cmd, o);
		}
	}
	@Override
	public int getState() {
		return isSync ? HostCallBack.STATE_SUCCESS : HostCallBack.STATE_FAIL;
	}

	@Override
	public String connString(int cmd, String value) {
		switch(cmd){
		case HostCallBack.REQUESY_SHOW_CURRENT_PLAYBAR:
		case HostCallBack.REQUESY_HIDE_CURRENT_PLAYBAR:
		case HostCallBack.REQUESY_SHOW_CURRENT_PLAYPAGE:
			notifyAllChild(cmd, value);
			break;
		}
		return null;
	}


	@Override
	public void addChildCallBack(ChildCallBack mChildCallBack) { 
		synchronized (mChildCallBack) {
			mChildCallBackSet.add(mChildCallBack);
		}
	}
	
	@Override
	public void removeChildCallBack(ChildCallBack mChildCallBack) {
		synchronized (mChildCallBack) {
			mChildCallBackSet.remove(mChildCallBack);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		L.e(tag, "onDestroy");
		NotifyLife.getInstance().destroy();
		ImageManager.getInstance().onActivityDestroy();
		FileScanner.getInstance(this).setScanListener(null);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// 检测屏幕的方向：纵向或横向
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// 当前为横屏， 在此处添加额外的处理代码
			L.e(tag, "横屏");
			//销毁所有
			notifyAllChild(PlayerCode.NOTIFY_ORIENTATION_LANDSCAPE, null);
			
		} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			// 当前为竖屏， 在此处添加额外的处理代码
			L.e(tag, "竖屏");
		}
		// 检测实体键盘的状态：推出或者合上
		if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
			// 实体键盘处于推出状态，在此处添加额外的处理代码
		} else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
			// 实体键盘处于合上状态，在此处添加额外的处理代码
		}
	}

	@Override
	protected void onNewIntent(Intent intent){
		super.onNewIntent(intent);
//		intentHandler(intent);
		setIntent(intent);
	}
	
	/**
	 * handler 处理
	 * 当应用界面完全退出后，近期任务启动会发上一次Intent过来。
	 * @param intent
	 */
	private void intentHandler(Intent intent){
		if(intent == null){
			return;
		}
		String page = intent.getStringExtra("page");
		setIntent(intent);
		L.e(tag, "onNewIntent page = " + page);
		if("list".equals(page) && mLocalMusicFragment != null){
			L.e(tag, "intentHandler");
			notifyAllChild(HostCallBack.PAGE_ENTER_LIST, null);
			FragmentManager mFragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
			mFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		}
		IntentCtrl.getInstance().intentDo(this);
		setIntent(null);
	}
	
	
}
