/**
 * 
 */
package com.zhcl.video;

import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout.LayoutParams;

import com.zh.broadcast.MediaButtonBroadcast;
import com.zh.broadcast.MountBroadcast.MountListener;
import com.zh.uitls.L;
import com.zh.uitls.Utils;
import com.zhcl.dao.RecordPlayStateInfo;
import com.zhcl.dao.VideoInfo;
import com.zhcl.service.PlayerCode;
import com.zhcl.service.service.client.VideoPlayerClient;
import com.zhcl.service.service.client.VideoPlayerClient.InitListener;
import com.zhcl.service.service.client.VideoPlayerClient.VideoPlayerCallback;
import com.zhcl.ui.video.CurrentVideoListView;
import com.zhcl.zhvideo.VideoPlayAcitivity;
import com.zhonghong.zhvideo.R;

/** 
 * @Description: 当前播放管理
 * 	1、当前歌单
 * 	2、当前播放歌曲；
 * 	3、回调当前状态；
 * 	4、记录断点；
 * 	5、断电续播；
 * 	6、收藏管理；
 * 	7、歌单管理；
 * 	8、播放顺序管理；
 * 	传入当前歌单以及当前播放歌曲，开始播放，写数据库记录歌单。
 * 	
 * 	需将最后的操作放在线程中执行
 * @author zhonghong.chenli
 * @date 2015-11-19 下午10:27:53 
 */
public class CurrentPlayManager {
	/**
	 * 当前监听
	 */
	public interface PlayListener{
		
		/**
		 * 开始播放前调用
		 */
		public void newPlayBefore(VideoInfo videoInfo);
		/**
		 * 开始播放
		 */
		public void newPlay(VideoInfo videoInfo);
		/**
		 * 播放完成
		 */
		public void complate();
		/**
		 * 播放状态监听
		 */
		public void playStateChange(int state);
	}
	private static final String tag = "CurrentPlayManager";
	Handler hander;
	private boolean isHaveAudioFocus = false;
	private CurrentPlayManager(){
	}
	private static CurrentPlayManager mCurrentPlayManager;
	public static CurrentPlayManager getInstance(){
		if(mCurrentPlayManager == null){
			mCurrentPlayManager = new CurrentPlayManager();
		}
		return mCurrentPlayManager;
	}
	/** 当前歌单 */
	private Vector<VideoInfo> videoMenu = new Vector<VideoInfo>();
	/** 当前播放的歌曲 */
	private VideoInfo videoInfo;
	/** 当前播放模式 */
	private int playMode = PlayerCode.PLAY_MODE_ALL_CYCLE;
	/** 随机列表 */
	private Vector<VideoInfo> randomArray = new Vector<VideoInfo>();
	/** 播放监听 */
	private PlayListener mPlayListener;
	/** 当前歌曲image */
	private Bitmap currentBitmap;
	/** 是否允许记录断点 */
	private boolean isAllowRecordoVideoinfo = false;
	/** 避免堵塞扫描线程，加此handler*/
	HandlerThread playHandlerThread;
	/** 避免堵塞扫描线程，加此handler*/
	Handler playHandler;
	/** 模式顺序 */
	private final int[] PLAY_MODE_LIST = {
			PlayerCode.PLAY_MODE_ALL_CYCLE,
			PlayerCode.PLAY_MODE_SINGLE_CYCLE,
			PlayerCode.PLAY_MODE_RANDOM,
//			PlayerCode.PLAY_MODE_ORDER,
	}; 
	Context context;
	public void init(Context context){
		if(this.context != null){
			L.w(tag, "已经初始化过");
			return;
		}
		this.context = context;
		initAudioManegr(context);
		hander = new Handler(Looper.getMainLooper());
		VideoPlayerClient.getInstance().setVideoPlayerCallback(mVideoPlayerCallback);	//注册监听
		VideoPlayerClient.getInstance().setInitListener(mInitListener);
		initPlayHandler();
	}
	
	/**
	 * 初始化播放handler
	 */
	private void initPlayHandler(){
		playHandlerThread = new HandlerThread("download");
		playHandlerThread.start(); //创建HandlerThread后一定要记得start()
		playHandler = new Handler(playHandlerThread.getLooper());
	}
	
	/**
	 * aidl状态监听
	 */ 
	private InitListener mInitListener = new InitListener() {
		@Override
		public void state(int state, String content) { 
			L.i(tag, "state = " + state + " content = " + content);
			switch (state) {
			case InitListener.STATE_SUCCESS: // 收到初始化成功才可执行控制命令（如，挂断电话）
//				initRecord();
				break;
			}   
		}   
	}; 
	
	
	/**
	 * 是否允许记录断点
	 */
	public boolean isAllowRecordVideoInfo(){
		return isAllowRecordoVideoinfo;
	}
	
	/**
	 * 设置是否允许记忆断点
	 * @param isAllow
	 */
	public void setAllowRecordVideoInfo(boolean isAllow){
		isAllowRecordoVideoinfo = isAllow;
	}
	
	/**
	 * 强制记忆断点记忆
	 */
	public void beRecord(){
		RecordManager.getInstance().record();
	}
	
	RecordPlayStateInfo mRecordPlayStateInfo;
	
	/**
	 * 读取断点
	 */
	@SuppressWarnings("static-access")
	private boolean readRecord(){
		mRecordPlayStateInfo = RecordManager.getInstance().getRecordPlayStateInfo();
		Vector<VideoInfo> tempVideoMenu = RecordManager.getInstance().getMenuList();
		if(mRecordPlayStateInfo == null || tempVideoMenu == null){
			L.e(tag, "没有断点");
			return false;
		}
		if(tempVideoMenu != null){
			videoMenu.clear();
			videoMenu.addAll(tempVideoMenu);
		}
		this.videoInfo = mRecordPlayStateInfo.getVideoInfo();
		this.playMode = mRecordPlayStateInfo.getPlayMode();
		//因为从序列化中读出，所以不能检索到,在这里匹配一次
		for(VideoInfo videoInfoR : videoMenu){
			if(videoInfo != null && videoInfoR != null && videoInfoR.getFileName().equals(videoInfo.getFileName())){
				this.videoInfo = videoInfoR; 
				break;
			}
		}
		if(this.videoInfo == null || !mRecordPlayStateInfo.isPlay()){
			L.e(tag, "!mRecordPlayStateInfo.isPlay()");
			return false;
		}
		return true;
	}
	/**
	 * 初始化，记忆断点等 ,可能异常退出导致记忆备份有异常
	 */
	public void initRecord(){
		Utils.getInstance().startTime("读断点");
		if(readRecord()){
//			play(videoInfo, videoMenu, PlayerCode.VIDEO_PLAY_TYPE_RECORD);
		}else{  
			L.e(tag, "断点读取异常");  
		}
		Utils.getInstance().endUseTime("读断点");   
	}  
	
	
	/**
	 * 播放断点
	 */
	public void palyRecord(){
		L.w(tag, "播放断点");
//		if(mRecordPlayStateInfo == null){
			if(!readRecord()){
				L.e(tag, "断点读取异常！"); 
				return;
			}
//		}
		if(mRecordPlayStateInfo.isPlay()){
			if(playFromVideoMenu(videoInfo, videoMenu)){
				L.w(tag, "palyRecord11");
				playRecordCache();
				VideoPlayerClient.getInstance().seekTo(mRecordPlayStateInfo.getCurrentPlayTime());
			}
		}else{
			if(playFromVideoMenu(videoInfo, videoMenu)){
				playRecordCache();
				VideoPlayerClient.getInstance().seekTo(mRecordPlayStateInfo.getCurrentPlayTime());
				VideoPlayerClient.getInstance().pause();
				abandAudioFocus();
				L.w(tag, "palyRecord22");
			}
		}
		mRecordPlayStateInfo = null;
	}
	
	
	/**
	 * 设置播放监听
	 */
	public void setPlayListener(PlayListener mPlayListener){
		this.mPlayListener = mPlayListener;
	}
	
	
	/**
	 * 解析当前歌曲缩略图
	 */
	private void parseVideoImage(VideoInfo videoInfo){
		currentBitmap = Utils.getInstance().createAlbumArt(videoInfo.getFileName());
	}
	
	/**
	 * 获得当前歌曲缩略图
	 */
	public Bitmap getCurrentVideoImage(){
		return currentBitmap;
	}
	
	/**
	 * 请求播放视频
	 * @param video
	 * @param videoMenu
	 * @param videoPlayType
	 */
	public void play(Context context, VideoInfo video, Vector<VideoInfo> videoMenu, int videoPlayType){
		playFromVideoMenu(video, videoMenu);
		switch(videoPlayType){
		case PlayerCode.VIDEO_PLAY_TYPE_FIRST:
		case PlayerCode.VIDEO_PLAY_TYPE_RECORD:
			if(VideoPlayAcitivity.getSurfaceView() != null && VideoPlayAcitivity.isSurfaceViewCreate()){
				playRecordCache();
				L.i(tag, "play VIDEO_PLAY_TYPE_FIRST");
			}else{
				L.i(tag, "play startActivity");
				Intent it = new Intent(context, VideoPlayAcitivity.class);
				it.putExtra("playstate", videoPlayType);		//通知播放断点
				it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
				context.startActivity(it);
			}
			break;
		}
		
	}
	
	/**
	 * 从列表主动调度播放，只允许通过此函数传递当前歌单 ，需放在线程中执行
	 * @param video
	 * @param videoList
	 * @return
	 */
	private boolean playFromVideoMenu(VideoInfo video, Vector<VideoInfo> videoMenu){
		if(video == null || videoMenu == null){
			throw new IllegalAccessError("函数调用异常");
		}
		if(this.videoMenu != null && !this.videoMenu.equals(videoMenu)){
			this.videoMenu.clear();
			this.videoMenu.addAll(videoMenu);//只允许在这里赋值！
			RecordManager.getInstance().setMenuList(videoMenu);	//通知记忆
		}
//		this.videoMenu = videoMenu;		//不允许如此赋值，避免交叉，且可以独立管理
		this.videoInfo = video;
		return true;
	}
	
	
	/** 播放选中的视频 */
	public void playRecordCache(){
		play(this.videoInfo);
		if(this.playMode == PlayerCode.PLAY_MODE_RANDOM){
			shuffle();
		}
	}
	
	/**
	 * 播放歌曲
	 * 如果当前歌单中没有当前播放的歌曲则添加至当前歌单
	 */
	private boolean play(final VideoInfo videoInfo){
		this.videoInfo = videoInfo;
		if(!this.videoMenu.contains(videoInfo)){
			Collections.sort(this.videoMenu);
		}
//		parseVideoImage(videoInfo);
		
		if(mPlayListener != null){
			hander.post(new Runnable() {
				@Override   
				public void run() {
					mPlayListener.newPlayBefore(videoInfo);
				} 
			}); 
		}
		if(VideoPlayerClient.getInstance().openMedia(videoInfo.getFileName()) == PlayerCode.RESULT_FAIL){
			//播放失败则播放下一曲
//			playNextVideo();
			return false;
		}
		if(mPlayListener != null){
			hander.post(new Runnable() {
				@Override   
				public void run() {
					mPlayListener.newPlay(videoInfo);
				} 
			}); 
		}
		play();
		return true;
	}
	  
	
	/**
	 * 获得下一个模式
	 * @return
	 */
	private int nextPlayMode(){ 
		int result = PlayerCode.PLAY_MODE_ALL_CYCLE;
		for(int i = 0; i < PLAY_MODE_LIST.length; i++){
			if(this.playMode == PLAY_MODE_LIST[i]){
				if(i == PLAY_MODE_LIST.length - 1){
					result =  PLAY_MODE_LIST[0];
				}else{
					result = PLAY_MODE_LIST[i + 1];
				}
				break;
			}
		}
		return result;
	}
	/**
	 * 播放模式控制
	 * 切换顺序为
	 */
	public void changePlayMode(){
		this.playMode = nextPlayMode();
		if(this.playMode == PlayerCode.PLAY_MODE_RANDOM){	//如果是随机播放，则重新排序
			shuffle();
		}
	}
	
	/**
	 * 获得当前播放模式
	 * @return
	 */
	public int getPlayMode(){
		return this.playMode;
	}
	
	
	/**
	 * 从列表中随机获得一首歌
	 * Shuffle随机算法 ： 从有序到乱序的一个过程，俗称洗牌算法，支持当用户点击“上一首”时，能够回到刚刚播放的那一首歌曲。
	 */
	public void shuffle() {
		randomArray.clear();
		randomArray.addAll(this.videoMenu);
		if(randomArray.isEmpty()){
			L.e(tag, "当前歌单为空");
			return;
		}
		int key;
		VideoInfo temp;
		Random rand = new Random();
		//需要记忆当前播放歌曲的index
		for (int i = 0; i < randomArray.size(); i++) {
			key = rand.nextInt(randomArray.size() - 1);
			temp = randomArray.get(i);
			randomArray.set(i, randomArray.get(key));
			randomArray.set(key, temp);
		}
	}

	
	/**
	 * 根据播放模式获得当前歌曲在列表中的索引
	 */
	private int getCurrentPlayIndex(){
		int result = 0;
		if(this.playMode == PlayerCode.PLAY_MODE_RANDOM){	//随机
			result = randomArray.indexOf(videoInfo);
		}else{
			result = videoMenu.indexOf(videoInfo);
		} 
		return result;
	}
	
	/**
	 * 取出下一曲的歌曲信息对象
	 */
	private VideoInfo getNextVideoInfo(int currentIndex){
		VideoInfo result = null;
		Vector<VideoInfo> videoInfoList =  playMode == PlayerCode.PLAY_MODE_RANDOM ? randomArray : videoMenu;
		if(videoInfoList.size() == 1){
			result = videoInfoList.get(0);
		}else if(currentIndex>= (videoInfoList.size() - 1)){
			result = videoInfoList.get(0);
		}else{
			result = videoInfoList.get(currentIndex + 1);
		}
		return result;
	}
	
	/**
	 * 取出上一曲的歌曲信息
	 */
	private VideoInfo getPreVideoInfo(int currentIndex){
		VideoInfo result = null;
		Vector<VideoInfo> videoInfoList =  playMode == PlayerCode.PLAY_MODE_RANDOM ? randomArray : videoMenu;
		if(videoInfoList.size() == 1){
			result = videoInfoList.get(0);
		}else if(currentIndex <= 0){
			result = videoInfoList.lastElement();
		}else{
			result = videoInfoList.get(currentIndex - 1);
		}
		return result;
	}
	
	/**
	 * 是否允许切歌
	 * @return
	 */
	private boolean isAllowChangeVideo(){
		if(videoMenu == null || videoMenu.isEmpty()){
			return false;
		}
		return true;
	}
	
	/**
	 * 下一曲
	 */
	public boolean playNextVideo(){
		if(!isAllowChangeVideo()){
			L.w(tag, "当前无歌曲，不允许切歌");
			return false;
		}
		int currentIndex = getCurrentPlayIndex();	
		VideoInfo nextVideoInfo = getNextVideoInfo(currentIndex);
		play(nextVideoInfo);
 		return true;
	}
	
	/**
	 * 上一曲
	 */
	public boolean playPreVideo(){
		if(!isAllowChangeVideo()){
			L.w(tag, "当前无歌曲，不允许切歌");
			return false;
		}
		int currentIndex = getCurrentPlayIndex();
		VideoInfo preVideoInfo = getPreVideoInfo(currentIndex);
		play(preVideoInfo);
		return true;
	}
	
	/**
	 * 播放
	 */
	public boolean play(){
		if(!VideoPlayerClient.getInstance().isPlaying() && videoInfo != null){	//如果正在播放
			VideoPlayerClient.getInstance().start();
			hander.post(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(mPlayListener != null){
						mPlayListener.playStateChange(PlayerCode.PLAY_STATE_PLAYING);
					}
				}
			});
		}
		L.e(tag, "requestVideoFocus 1111");
		requestAudioFocus(); 
		return true;
	}
	
	/**
	 * 暂停
	 */
	public boolean pause(){
		L.e(tag, "pause");
		if(VideoPlayerClient.getInstance().isPlaying()){	//如果正在播放
			VideoPlayerClient.getInstance().pause();
			hander.post(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(mPlayListener != null){
						mPlayListener.playStateChange(PlayerCode.PLAY_STATE_PAUSE);
					}
				}
			});
		}
		return true;
	}
	
	/**
	 * 停止播放
	 */
	public boolean stop(){
		VideoPlayerClient.getInstance().closeMedia();
		return true;
	}
	
	/**
	 * 播放/暂停
	 */
	public boolean playPause(){
		if(VideoPlayerClient.getInstance().isPlaying()){	//如果正在播放
			pause();
		}else{
			//当前是否有歌曲在播放，如果有则回复播放
			if(videoInfo != null){
				play();
			}else{//如果没有则，从所有歌曲的第一首开始播放
				Vector<VideoInfo> videoInfoList = VideoManager.getIntance(null).getAllVideo();
				if(videoInfoList != null && !videoInfoList.isEmpty()){
					playFromVideoMenu(videoInfoList.get(0), videoInfoList);
				}else{
					L.w(tag, "没有歌曲");
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * 获得当前播放状态
	 */
	public boolean isPlay(){
		return VideoPlayerClient.getInstance().isPlaying();
	}
	
	/**
	 * 获得当前播放列表
	 * @return
	 */
	public Vector<VideoInfo> getVideoMenu(){
		return videoMenu;
	}


	/**
	 * 获得当前歌曲
	 * @return
	 */
	public VideoInfo getVideoInfo() {
		return videoInfo;
	}

	/**
	 * 音频播放回调
	 */
	private VideoPlayerCallback mVideoPlayerCallback = new VideoPlayerCallback() {
		@Override
		public void onSeekComplete() {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public boolean onError(int what, int extra) {
			// TODO Auto-generated method stub
			L.e(tag, "onError!!");
			playHandler.postDelayed(mNextRunnable, 1000);
			return false;
		}
		
		@Override
		public void onCompletion() {
			if(CurrentPlayManager.this.playMode == PlayerCode.PLAY_MODE_SINGLE_CYCLE){
				//重新播放
				L.e(tag, "重新播放");
				play(CurrentPlayManager.this.videoInfo);
			}else{		//播放下一曲
				L.e(tag, "播放下一曲！");
				playNextVideo();
			}
		}
	};
	
	
	/**
	 * 下一曲
	 */
	private Runnable mNextRunnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			playNextVideo();
		}
	};
	
	/**
	 * 是否存在音频焦点
	 */
	public boolean isHaveAudioFocus(){
		return isHaveAudioFocus;
	}
	
	
	/** 音频管理对象 */
	protected AudioManager mAudioManager;
	/** 媒体广播信息 */
	protected ComponentName mComponentName;
	/** 是否是暂时丢失 */
	private boolean isLossTransient = false;
	/** 是否是短暂丢失（压低声音的方式） */
	private boolean isLossDuck = false;
	/**
	 * 初始化音频管理相关
	 */
	private void initAudioManegr(Context context){
		L.e(tag, "initAudioManegr");
		mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		mComponentName = new ComponentName(context.getPackageName(), MediaButtonBroadcast.class.getName());
		mAudioManager.registerMediaButtonEventReceiver(mComponentName);
	}
	
	/**
	 * 请求音频焦点
	 */
	private boolean requestAudioFocus(){
		L.e(tag, "requestAudioFocus");
		int result = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
		mAudioManager.registerMediaButtonEventReceiver(mComponentName);
		isHaveAudioFocus = true;
		return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
	}
	
	/**
	 * 释放音频焦点
	 */
	private void abandAudioFocus(){
		mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
		mAudioManager.unregisterMediaButtonEventReceiver(mComponentName);
		isHaveAudioFocus = false;
	}
	
	/**
	 * 音频焦点处理
	 */
	private OnAudioFocusChangeListener mOnAudioFocusChangeListener = new OnAudioFocusChangeListener() {
		@Override
		public void onAudioFocusChange(int focusChange) {
			switch(focusChange){
			case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:			//暂时丢失焦点，可重新获得焦点
				L.i(tag, "AUDIOFOCUS_LOSS_TRANSIENT");
				lossTransient();
				break;
			case AudioManager.AUDIOFOCUS_GAIN:						//重新获得焦点
				L.i(tag, "AUDIOFOCUS_GAIN");
				videoFocusGain();
				break;
			case AudioManager.AUDIOFOCUS_LOSS:						//永久丢失焦点除非重新主动获取
				L.i(tag, "AUDIOFOCUS_LOSS");
				videoFocusLoss();
				break;
			case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:	//短暂丢失焦点，压低后台音量
				L.i(tag, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
				videoFocusLossDuck();
				break;
			}
		}
	};
	
	/**
	 * 短暂丢失焦点处理
	 */
	private void lossTransient(){
		//暂停播放
		isLossTransient = true;
		isHaveAudioFocus = false;
		mAudioManager.unregisterMediaButtonEventReceiver(mComponentName);
		pause();
	}
	
	/**
	 * 重新获得焦点
	 */
	private void videoFocusGain(){
		if(isLossTransient){	//如果暂时丢失焦点
			isLossTransient = false;
			isHaveAudioFocus = true;
			mAudioManager.registerMediaButtonEventReceiver(mComponentName);
			play();
		}
		
		if(isLossDuck){			//如果压低声音试丢失
			isLossDuck = false;
			//恢复音量
			VideoPlayerClient.getInstance().setVolumeUpOrDown(true);
		}
	}
	
	/**
	 * 永久丢失焦点
	 */
	private void videoFocusLoss(){
		abandAudioFocus();
		isHaveAudioFocus = false;
		isLossTransient = true;
		pause();
	}
	 
	/**
	 * 短暂丢失焦点
	 */
	private void videoFocusLossDuck(){
		isLossDuck = true;
		//降低媒体音量
		VideoPlayerClient.getInstance().setVolumeUpOrDown(false);
	}
	
	
	
	/** 歌单对话框 */
	private AlertDialog dialog;
	CurrentVideoListView mCurrentVideoListView;
	/**
	 * 当前歌单弹出框显示
	 */
	public void showCurrentVideoMenu(Context context) {
		if(CurrentPlayManager.getInstance().getVideoMenu() == null){
			L.w(tag, "当前无歌单");
			return;
		}
		if(dialog == null || (mCurrentVideoListView != null && !mCurrentVideoListView.getContext().equals(context))){
			 dialog = new AlertDialog.Builder(context).create();
			 mCurrentVideoListView = new CurrentVideoListView(context);
		}
		if(mCurrentVideoListView.getParent() != null){
			((ViewGroup)mCurrentVideoListView.getParent()).removeView(mCurrentVideoListView);
		}
		dialog.show();
		Window window = dialog.getWindow();
		WindowManager.LayoutParams params = window.getAttributes();  
		params.dimAmount = 0f;  
		window.setAttributes(params); 
		window.setContentView(mCurrentVideoListView);
		window.setGravity(Gravity.BOTTOM);  
		window.setLayout(LayoutParams.MATCH_PARENT, /*getWH(context)[1]/2*/LayoutParams.WRAP_CONTENT);
		window.setWindowAnimations(R.style.AnimBottom);
		mCurrentVideoListView.load();
		L.i(tag, "准备加载歌单");
	}
	
	/**
	 * 获得屏幕宽高
	 * @return
	 */
	public int[] getWH(Context context){
		WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
		L.e(tag, "w = " + width + " h = " + height);
		return new int[]{width, height};
	}
	
	/**
	 * 正常全屏分辨率
	 */
	public static final int VIDEO_SHOW_TYPE_NORMAL_FULL_SCREEN = 0x001;
	
	/**
	 * 铺满全屏
	 */
	public static final int VIDEO_SHOW_TYPE_FULL_SCREEN	 = 0x002;
	
	/**
	 * 绝对分辨率 
	 */
	public static final int VIDEO_SHOW_TYPE_FINAL_FIXED = 0x003;
	/**
	 *  计算分辨率
	 * @param type			计算类型
	 * @param showSize		要显示的区域大小
	 * @param path			视频文件
	 * @return				分辨率大小
	 */
	public int[] computerFixedSize(int type, int[] showSize, String path){
		int[] result = null;
		switch(type){
		case VIDEO_SHOW_TYPE_NORMAL_FULL_SCREEN:
			result = computerNormalFullScreen(showSize, path);
			break;
		case VIDEO_SHOW_TYPE_FULL_SCREEN:
			result = showSize;
			break;
		case VIDEO_SHOW_TYPE_FINAL_FIXED:
			result = computerFinalFixed(path);
			break;
		}
		return result;
	}
	
	/**
	 * 正常全屏显示
	 */
	private int[] computerNormalFullScreen(int[] showSize, String path){
		try{
			int surfaceW = showSize[0];// surface.getWidth();
			int surfaceH = showSize[1];// surface.getHeight();
			// 取得视频的长度(单位为毫秒)
			int[] finalFixed = computerFinalFixed(path);
			if(finalFixed == null){
				return finalFixed;
			}
			int width = finalFixed[0];
			int height = finalFixed[1];
			float currentScale = surfaceW * 1.0f / surfaceH;
			float trueScale = width * 1.0f / height;
			int resultW = 0, resultH = 0;
			if(trueScale >= currentScale) { // 宽大于等于长
				resultW = surfaceW;
				resultH = (int) (resultW / trueScale);
			} else {
				resultH = surfaceH;
				resultW = (int) (resultH * trueScale);
			}
//		     L.e(tag, "surfaceW = " + surfaceW + " surfaceH = " + surfaceH);
//		     L.e(tag, "trueScale = " + trueScale + " width = " + width + " height = " + height);
//		     L.e(tag, "resultW = " + resultW + " resultH = " + resultH);
		     return new int[]{resultW, resultH};
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 计算绝对分辨率
	 */
	private int[] computerFinalFixed(String path){
		MediaMetadataRetriever retriever;
		try{
			retriever = new MediaMetadataRetriever();
			retriever.setDataSource(path);
			// 取得视频的长度(单位为毫秒)
			 int width = Integer.parseInt(retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
		     int height = Integer.parseInt(retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
		     return new int[]{width, height};
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 设备状态监听集合
	 */
	HashSet<MountListener> mMountListenerSet = new HashSet<MountListener>();
	/**
	 * 添加设备状态监听
	 * @param mMountListener
	 */
	public void addMountListener(MountListener mMountListener){
		synchronized (mMountListenerSet) {
			mMountListenerSet.add(mMountListener);
		}
	}
	
	/**
	 * 移除设备状态监听
	 * @param mMountListener
	 */
	public void delMountListener(MountListener mMountListener){
		synchronized (mMountListenerSet) {
			mMountListenerSet.remove(mMountListener);
		}
	}
}
