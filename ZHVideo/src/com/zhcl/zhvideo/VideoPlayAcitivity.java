package com.zhcl.zhvideo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.zh.uitls.L;
import com.zh.uitls.Utils;
import com.zhcl.dao.VideoInfo;
import com.zhcl.service.PlayerCode;
import com.zhcl.service.service.client.VideoPlayerClient;
import com.zhcl.video.CurrentPlayManager;
import com.zhcl.video.CurrentPlayManager.PlayListener;
import com.zhonghong.zhvideo.R;
import com.zhonghong.zhvideo.R.drawable;
import com.zhonghong.zui.ZuiConn;

public class VideoPlayAcitivity extends Activity {

	private static final String tag = "VideoPlayAcitivity";
	/** 视频层 */
	private static SurfaceView videoSurface;
	/** 当前状态 */
	private int currentState = -1; 
	/** 后台线程是否正在执行 */
	private boolean isBackThreadRun;
	private Handler hander;
	private PlayThread mPlayThread;
	/** 更新时间 */
	private final int UI_UPDATA_TIME = 1;
	private ImageButton preBut;
	private ImageButton playPauseBut;
	private ImageButton nextBut;
	private SeekBar seekbar;
	private TextView currentTime;
	private TextView allTime;
	/** 是否允许改变当前时间 */
	private boolean isAllowChangeTime = true;
	/** 列表键 */
	private ImageButton menu;
	private View surfaceBg;
	/** 操作按键图层 */
	private View faceLayout;
	/** 做触摸用 */
	private View touchLayout;
	/** 指定多少秒后一定隐藏 */
	private final int HIDE_TIME = 5000;
	private View buttonLayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_video_play);
		CurrentPlayManager.getInstance().setPlayListener(mPlayListener);
		initDo(getIntent());
		initView();
		hander = new UIHandler();
		startThread();
	}
	
	
	private void initView(){
		videoSurface = (SurfaceView)findViewById(R.id.videoSurface);
		videoSurface.getHolder().addCallback(mCallback);
		preBut = (ImageButton)findViewById(R.id.preBut);
		playPauseBut = (ImageButton)findViewById(R.id.playPauseBut);
		nextBut = (ImageButton)findViewById(R.id.nextBut);
		preBut.setOnClickListener(mOnClickListener);
		playPauseBut.setOnClickListener(mOnClickListener);
		nextBut.setOnClickListener(mOnClickListener);
		seekbar = (SeekBar)findViewById(R.id.seekbar);
		currentTime = (TextView)findViewById(R.id.currentTime);
		allTime = (TextView)findViewById(R.id.allTime);
		seekbar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
		surfaceBg = (View)findViewById(R.id.surfaceBg);
		
		menu = (ImageButton)findViewById(R.id.menu);
		menu.setOnClickListener(mOnClickListener);
		
		faceLayout = (View)findViewById(R.id.faceLayout);
		touchLayout = (View)findViewById(R.id.touchLayout);
		touchLayout.setOnTouchListener(mOnTouchListener);
		buttonLayout = (View)findViewById(R.id.buttonLayout);
		buttonLayout.setOnTouchListener(mButtonLayoutOnTouchListener);
	}
	
	OnTouchListener mButtonLayoutOnTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			setFaceViewShowAndHide(true);
			return true;
		}
	};
	/**
	 * 触摸监听
	 */
	private OnTouchListener mOnTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch(event.getAction()){
			case MotionEvent.ACTION_UP:
				L.e(tag, "ACTION_UP");
				faceViewShowAndHide();
				break;
			}
			return true;
		}
	};
	
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		L.i(tag, "主动被调度");
		initDo(intent);
	}
	
	@Override
	protected void onResume() {
		L.w(tag, "onResume");
		super.onResume();
		CurrentPlayManager.getInstance().setAllowRecordVideoInfo(true);
		ZuiConn.getInstance().enterPage();
	}

	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		L.w(tag, "onPause");
		//记忆
		CurrentPlayManager.getInstance().setAllowRecordVideoInfo(false);
		CurrentPlayManager.getInstance().beRecord();
		CurrentPlayManager.getInstance().stop();	//停止播放
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		L.w(tag, "onDestroy");
		videoSurface = null;		//在销毁时一定置空
	}

	
	private void initDo(Intent intent){
		currentState =  intent.getIntExtra("playstate", -1);
	}
	
	/**
	 * 获得surfaceview
	 */
	public static SurfaceView getSurfaceView(){
		return videoSurface;
	}
	
	/**
	 * 是否创建
	 */
	public static boolean isSurfaceViewCreate(){
		return isCreate;
	}
	
	/**
	 * 获取显示大小
	 * @return
	 */
	public int[] getShowSize(){
		return new int[]{surfaceBg.getMeasuredWidth(), surfaceBg.getMeasuredHeight()};
	}
	
	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			CurrentPlayManager.getInstance().stop();
		}
		return super.onKeyDown(keyCode, event);
	}


	private static boolean isCreate;
	/**
	 * 视频生命周期
	 */
	private Callback mCallback = new Callback() {
		
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			//记录断点
			L.i(tag, "surfaceDestroyed");
			isCreate = false;
		}
		
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			L.e(tag, "surfaceCreated");
			isCreate = true;
			switch(currentState){
			case PlayerCode.VIDEO_PLAY_TYPE_FIRST:
				CurrentPlayManager.getInstance().playRecordCache();
				break;
			case PlayerCode.VIDEO_PLAY_TYPE_RECORD:
				CurrentPlayManager.getInstance().palyRecord();
				break;
			case PlayerCode.VIDEO_PLAY_TYPE_RESUME:
				CurrentPlayManager.getInstance().palyRecord();
				break;
			}
			initSeekBar();
			currentState = PlayerCode.VIDEO_PLAY_TYPE_RESUME;
		} 
		
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		}
	};

	
	/**
	 * 设置分辨率
	 */
	private void setFixed(SurfaceView surfaceView, int[] showSize, int fixedType, String path){
		int[] fixed = CurrentPlayManager.getInstance().computerFixedSize(fixedType, showSize, path);
		if(fixed == null){
			surfaceView.getHolder().setFixedSize(showSize[0], showSize[1]);
		}else{
			surfaceView.getHolder().setFixedSize(fixed[0], fixed[1]);
		}
	}
	
	/**
	 * 初始化seekbar
	 */
	private void initSeekBar(){
		int duration = VideoPlayerClient.getInstance().getDuration();
		allTime.setText(Utils.getInstance().formatLongToTimeStr(duration));
		seekbar.setMax(duration);
		seekbar.setProgress(VideoPlayerClient.getInstance().getCurrentPosition());
	}
	
	/**
	 * 更新播放状态
	 */
	private void updataPlayState(){
		if(CurrentPlayManager.getInstance().isPlay()){
			playPauseBut.setImageResource(drawable.landscape_player_btn_pause_xml);
		}else{
			playPauseBut.setImageResource(R.drawable.landscape_player_btn_play_xml);
		}
	}
	
	/**
	 * 播放监听
	 */
	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {   
			switch(v.getId()){
			case R.id.preBut:
				CurrentPlayManager.getInstance().playPreVideo();
				break;
			case R.id.playPauseBut:
				CurrentPlayManager.getInstance().playPause();
				break;
			case R.id.nextBut:
				CurrentPlayManager.getInstance().playNextVideo();
				break;
			case R.id.menu:
				CurrentPlayManager.getInstance().showCurrentVideoMenu(VideoPlayAcitivity.this);
				break;
			}
			setFaceViewShowAndHide(true);
		} 
	};
	
	
	 /**
     * 是否允许改变当前时间
     * @return
     */
    private boolean isAllowSetCurrentTime(){
    	return isAllowChangeTime;
    }
    
    /**
     * 设置是否允许改变时间
     * @param isAllowChange
     */
    private void setAllowChangeCurrentTime(boolean isAllowChange){
    	isAllowChangeTime = isAllowChange;
    }
	
	 /**
     * seekbar进度条监听
     */
    private OnSeekBarChangeListener mOnSeekBarChangeListener = new OnSeekBarChangeListener() {
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			L.i(tag, "onStopTrackingTouch" + seekBar.getProgress());
			setAllowChangeCurrentTime(true);
			VideoPlayerClient.getInstance().seekTo(seekBar.getProgress());
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			L.i(tag, "onStartTrackingTouch");
			setAllowChangeCurrentTime(false);
		}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//			L.i(tag, "onProgressChanged");
		}
	};
	
	
	/**
	 * 播放监听
	 */
	private PlayListener mPlayListener = new PlayListener() {
		
		@Override
		public void newPlayBefore(VideoInfo videoInfo) {
//			setFixed(videoSurface, getShowSize(), CurrentPlayManager.VIDEO_SHOW_TYPE_NORMAL_FULL_SCREEN/*VIDEO_SHOW_TYPE_FINAL_FIXED*//*VIDEO_SHOW_TYPE_FULL_SCREEN*//*VIDEO_SHOW_TYPE_NORMAL_FULL_SCREEN*/, CurrentPlayManager.getInstance().getVideoInfo().getFileName());
		}
		
		@Override
		public void newPlay(VideoInfo videoInfo) {
			initSeekBar();
		}
		
		@Override
		public void complate() { 
			
		}

		@Override
		public void playStateChange(int state) {
			updataPlayState();
		}
	};
	
	/**
	 * 启动播放线程
	 * show含进度条界面的时候启动
	 */
	private void startThread(){
		if(isBackThreadRun){
			L.w(tag, "线程后台已在运行");
			return;
		}
		isBackThreadRun = true;
		mPlayThread = new PlayThread();
		mPlayThread.start();
	}
	
	/**
	 * 播放中线程
	 */
	class PlayThread extends Thread{
		public void run(){
			backRun();
		}
	}
	
	/**
	 * 后台执行
	 */
	private void backRun(){
		while(isBackThreadRun){
			hander.sendEmptyMessage(UI_UPDATA_TIME);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * ui控制
	 */
	@SuppressLint("HandlerLeak")
	private class UIHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case UI_UPDATA_TIME:
				if(VideoPlayerClient.getInstance().isPlaying()){
					int time = VideoPlayerClient.getInstance().getCurrentPosition();
					currentTime.setText(Utils.getInstance().formatLongToTimeStr(time));
					if(isAllowSetCurrentTime()){
						seekbar.setProgress(time);
					}
				}
				break;
			}
		}
	};
	
	
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// 检测屏幕的方向：纵向或横向
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// 当前为横屏， 在此处添加额外的处理代码
		} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			// 当前为竖屏， 在此处添加额外的处理代码
		}
		// 检测实体键盘的状态：推出或者合上
		if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
			// 实体键盘处于推出状态，在此处添加额外的处理代码
		} else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
			// 实体键盘处于合上状态，在此处添加额外的处理代码
		}
	}
	
	
	/**
	 * 调度隐藏或显示
	 */
	private void faceViewShowAndHide(){
		setFaceViewShowAndHide(faceLayout.getVisibility() != View.VISIBLE);
	}
	
	/**
	 * 设置是否显示状态栏
	 * @param isShow
	 */
	private void setFaceViewShowAndHide(boolean isShow){
		if(isShow){
			faceLayout.setVisibility(View.VISIBLE);
			setStatusShowAndHide(isShow);
		}else{
			faceLayout.setVisibility(View.GONE);
			setStatusShowAndHide(isShow);
		}
		hander.removeCallbacks(mRunnable);
		if(faceLayout.getVisibility() == View.VISIBLE){
			hander.postDelayed(mRunnable, HIDE_TIME);
		}
	}
	
	/**
	 * 设置是否隐藏状态栏
	 * @param isShow
	 */
	private void setStatusShowAndHide(boolean isShow){
		WindowManager.LayoutParams attrs = getWindow().getAttributes();
		if(isShow){
			attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
		}else{
			attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
		}
		getWindow().setAttributes(attrs);
		
	}
	
	/**
	 * 指定时间隐藏
	 */
	private Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			setFaceViewShowAndHide(false);
		}
	};
}
