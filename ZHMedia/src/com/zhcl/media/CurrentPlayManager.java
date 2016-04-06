/**
 * 
 */
package com.zhcl.media;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Vector;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.zh.broadcast.MediaButtonBroadcast;
import com.zh.broadcast.MountBroadcast;
import com.zh.broadcast.MountBroadcast.MountListener;
import com.zh.dao.RecordPlayStateInfo;
import com.zh.dao.SongInfo;
import com.zh.uitls.L;
import com.zh.uitls.Utils;
import com.zhcl.media.ImageManager.NetWorkLrcAndImageListener;
import com.zhcl.media.SongManager.CollectionCallBack;
import com.zhcl.remote.ui.MusicRemoteCtrl;
import com.zhcl.remote.ui.NotificationWidget;
import com.zhcl.service.PlayerCode;
import com.zhcl.service.service.client.AudioPlayerClient;
import com.zhcl.service.service.client.AudioPlayerClient.AudioPlayerCallback;
import com.zhcl.service.service.client.AudioPlayerClient.InitListener;
import com.zhcl.ui.music.CollectionSongListView;
import com.zhcl.ui.music.CurrentSongListView;
import com.zhcl.ui.music.HostCallBack;
import com.zhonghong.zhmedia.R;

/** 
 * @Description: 当前播放管理，所有需要与UI打交道的数据，都从这里取
 * 	1、当前歌单
 * 	2、当前播放歌曲；
 * 	3、回调当前状态；
 * 	4、记录断点；
 * 	5、断电续播；
 * 	6、收藏管理；
 * 	7、歌单管理；
 * 	8、播放顺序管理；
 * 	传入当前歌单以及当前播放歌曲，开始播放，写数据库记录歌单。
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
		 * 开始播放
		 */
		public void newPlay(SongInfo songInfo);
		/**
		 * 播放完成
		 */
		public void complate();
		/**
		 * 播放状态监听
		 */
		public void playStateChange(int state);
		
		/**
		 * 预留的回调接口
		 */
		public String callBackForString(int cmd, String str);
	}
	private static final String tag = "CurrentPlayManager";
	Handler handler;
	private boolean isHaveAudioFocus = false;
	private CurrentPlayManager(){ }
	private static CurrentPlayManager mCurrentPlayManager;
	public static CurrentPlayManager getInstance(){
		if(mCurrentPlayManager == null){
			mCurrentPlayManager = new CurrentPlayManager();
		}
		return mCurrentPlayManager;
	}
	/** 当前歌单 */
	private Vector<SongInfo> songMenu = new Vector<SongInfo>();
	/** 当前播放的歌曲 */
	private SongInfo songInfo;
	/** 当前播放模式 */
	private int playMode = PlayerCode.PLAY_MODE_ALL_CYCLE;
	/** 随机列表 */
	private Vector<SongInfo> randomArray = new Vector<SongInfo>();
	/** 播放监听 */
	private PlayListener mPlayListener;
	/** 当前歌曲image */
	private Bitmap currentBitmap;
	/** 当前sessionID */
	private int audioSessionID = -1;
	
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
		handler = new Handler(Looper.getMainLooper());
		AudioPlayerClient.getInstance().setAudioPlayerCallback(mAudioPlayerCallback);	//注册监听
		AudioPlayerClient.getInstance().setInitListener(mInitListener);
		MountBroadcast.setMountListener(mMountListener);
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
	 * mount 监听
	 */
	private MountListener mMountListener = new MountListener() {
		@Override
		public void notifyMountInfo(final String action, final String path){
			L.w(tag, "action : " + action + " path : " + path);
			if(Intent.ACTION_MEDIA_EJECT.equals(action)){
				unmountDo(path);				//拔出处理
			}else if(Intent.ACTION_MEDIA_MOUNTED.equals(action)){
				mountDo(path);					//插入mount处理
			}
			synchronized (mMountListenerSet) {
				for(MountListener mMountListener : mMountListenerSet){
					mMountListener.notifyMountInfo(action, path);
				}
			}
		}
	};
	
	
	/**
	 * u盘或sd卡拔出处理
	 */
	private void unmountDo(final String path){
//		L.i(tag, "拔出处理！：" + path + " songInfo :" + songInfo.getFileName());
		//如果当前播放的歌曲被移除，则停止播放
		if(this.songInfo != null && SongManager.getInstance().isRemove(songInfo.getFileName())){
			stop();
			this.songInfo = null;
			L.w(tag, "停止播放");
		}
		//剔除播放列表中移除的项目
		if(this.songMenu != null){
			ArrayList<SongInfo> tempList = new ArrayList<SongInfo>();
			synchronized (songMenu) {
				for(SongInfo tempSongInfo : songMenu){
					if(SongManager.getInstance().isRemove(tempSongInfo.getFileName())){
						tempList.add(tempSongInfo);
					}
				}
				songMenu.removeAll(tempList);	//移除被移除的歌曲
			}
		}
		//通知改变UI
		if(mPlayListener != null){
			handler.post(new Runnable() {
				@Override   
				public void run() {
					L.w(tag, "停止播放 newPlay");
					mPlayListener.callBackForString(PlayerCode.NOTIFY_DEVICE_EJECT, path);
				} 
			}); 
		}
		requestRemoteUI();	//请求更新远端ui
	}
	
	/**
	 * SD或USB拔出处理
	 */
	private void mountDo(final String path){
		if(mPlayListener != null){
			handler.post(new Runnable() {
				@Override   
				public void run() {
					L.w(tag, "停止播放 newPlay");
					mPlayListener.callBackForString(PlayerCode.NOTIFY_DEVICE_MOUNT, path);
				} 
			}); 
		}
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
	
	
	/** 是否执行过回调 */
	private boolean isPlayServiceInitOk = false;
	/** 是否读过断点 */
	private boolean isReadRecord = false;
	/** 是否进入过界面 */
	private boolean isEnterPage = false;
	
	/**
	 * 主应用界面启动
	 */
	public void mainPageRun(){
		isEnterPage = true;
		//执行过回调，且没有读过断点，则读断点
		if(mRecordPlayStateInfo == null){ 	//如果没有断点信息，则读取一次
			initRecord();
		}
		if(isPlayServiceInitOk && !isReadRecord){
			recordDo();
		}
	}
	
	/**
	 * aidl状态监听
	 */ 
	private InitListener mInitListener = new InitListener() {
		@Override
		public void state(int state, String content) { 
			L.i(tag, "state = " + state + " content = " + content);
			switch (state) {
			case InitListener.STATE_SUCCESS: 
				//如果界面已经起来则直接播放断电，如果界面没起来过，则不执行，等待界面起来后再执行
				isPlayServiceInitOk = true;
				if(mRecordPlayStateInfo == null){ 	//如果没有断点信息，则读取一次
					initRecord();
				}
				if(isEnterPage && !isReadRecord){
					recordDo();
				}
				break;
			}   
		}   
	}; 
	/**
	 * 断点记录对象
	 */
	RecordPlayStateInfo mRecordPlayStateInfo;
	/**
	 * 初始化，记忆断点等 ,可能异常退出导致记忆备份有异常
	 */
	public synchronized void initRecord(){
		Utils.getInstance().startTime("读断点");
		mRecordPlayStateInfo = RecordManager.getInstance().getRecordPlayStateInfo();
		Vector<SongInfo> tempSongMenu = RecordManager.getInstance().getMenuList();
		if(mRecordPlayStateInfo == null || tempSongMenu == null){
			L.e(tag, "没有断点");
			return;
		}
		L.e(tag, "断点信息：" + mRecordPlayStateInfo.toString());
		if(tempSongMenu != null){
			songMenu.addAll(tempSongMenu);
		}
		this.songInfo = mRecordPlayStateInfo.getSongInfo();
		this.playMode = mRecordPlayStateInfo.getPlayMode();
		//因为从序列化中读出，所以不能检索到,在这里匹配一次
		for(SongInfo songInfoR : songMenu){
			if(songInfo != null && songInfoR != null && songInfoR.getFileName().equals(songInfo.getFileName())){
				this.songInfo = songInfoR; 
				break;
			}
		}
		
		Utils.getInstance().endUseTime("读断点");  
	}
	
	
	/**
	 * 执行断点
	 */
	private void recordDo(){
		isReadRecord = true;
		if(this.songInfo == null || mRecordPlayStateInfo == null){
			L.e(tag, "断点执行异常，无断点");
			return;
		}
		if(mRecordPlayStateInfo.isPlay()){
			L.e(tag, "读断点播放！！！");
			if(playFromSongMenu(songInfo, songMenu)){
				AudioPlayerClient.getInstance().seekTo(mRecordPlayStateInfo.getCurrentPlayTime());
			}else{
				L.e(tag, "播放失败！");
			}
		}else{
			if(playFromSongMenu(songInfo, songMenu)){
				AudioPlayerClient.getInstance().seekTo(mRecordPlayStateInfo.getCurrentPlayTime());
				AudioPlayerClient.getInstance().pause();
				abandAudioFocus();
			}
		}
		
		if(mPlayListener != null){
			handler.post(new Runnable() {
				@Override   
				public void run() {
					L.w(tag, "断点读取通知");
					mPlayListener.callBackForString(HostCallBack.PLAY_LISTENER_READ_RECORD, null);
				} 
			}); 
		}
		requestRemoteUI();	//请求更新远端UI
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
	private void parseMusicImage(SongInfo songInfo){
		currentBitmap = Utils.getInstance().createAlbumArt(songInfo.getFileName());
	}
	
	/**
	 * 获得当前歌曲缩略图
	 */
	public Bitmap getCurrentMusicImage(){
		return currentBitmap;
	}
	
	/**
	 * 获得当前audioSessionID
	 */
	public int getAudioSessionID(){
		return this.audioSessionID;
	}
	
	/**
	 * 从列表主动调度播放，只允许通过此函数传递当前歌单 ，需放在线程中执行
	 * @param song
	 * @param songList
	 * @return
	 */
	public boolean playFromSongMenu(SongInfo song, Vector<SongInfo> songMenu){
		if(song == null || songMenu == null){
			throw new IllegalAccessError("函数调用异常");
		}
		synchronized (songMenu) {
			if(this.songMenu != null && !this.songMenu.equals(songMenu)){
				this.songMenu.clear();
				this.songMenu.addAll(songMenu);//只允许在这里赋值！
				RecordManager.getInstance().setMenuList(songMenu);	//通知记忆
			}
		}
//		this.songMenu = songMenu;		//不允许如此赋值，避免交叉，且可以独立管理
		this.songInfo = song;
		play(song);
		if(this.playMode == PlayerCode.PLAY_MODE_RANDOM){
			shuffle();
		}
		return true;
	}
	
	/**
	 * 播放歌曲
	 * 如果当前歌单中没有当前播放的歌曲则添加至当前歌单
	 */
	public boolean play(final SongInfo songInfo){
		this.songInfo = songInfo;
		if(!this.songMenu.contains(songInfo)){
			Collections.sort(this.songMenu);
		}
		playHandler.removeCallbacks(mPlayThread);
		mPlayThread.setSongInfo(songInfo);
		playHandler.post(mPlayThread);
		return true;
	}
	 
	/**
	 * 音乐播放请求对象
	 */
	private PlayThread mPlayThread = new PlayThread();
	
	/**
	 * 音乐播放请求
	 * @author chen
	 *
	 */
	private class PlayThread extends Thread{
		private SongInfo songInfo;
		public void setSongInfo(SongInfo songInfo){
			this.songInfo = songInfo;
		}
		
		public void run(){
			Utils.getInstance().startTime("play SongInfo");
			parseMusicImage(songInfo);
			Utils.getInstance().startTime("openMedia");
			if(AudioPlayerClient.getInstance().openMedia(songInfo.getFileName()) == PlayerCode.RESULT_FAIL){
				showToast("播放失败：" + songInfo.getTitle());
				L.e(tag, "播放失败：" + songInfo.getFileName());
				//播放失败则播放下一曲
//				playNextMusic();
				return ;
			}
			Utils.getInstance().endUseTime("openMedia");			//消耗531
			loadLrcAndImageFromNetwork(songInfo);
			if(mPlayListener != null){
				handler.post(new Runnable() {
					@Override   
					public void run() {
						mPlayListener.newPlay(songInfo);
					} 
				}); 
			}
			requestRemoteUI();				//请求更新远端UI			
			Utils.getInstance().startTime("play");
			play();
			Utils.getInstance().endUseTime("play");
			Utils.getInstance().endUseTime("play SongInfo");
		}
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
		randomArray.addAll(this.songMenu);
		if(randomArray.isEmpty()){
			L.e(tag, "当前歌单为空");
			return;
		}
		if(randomArray.size() <= 1){
			L.i(tag, "仅一首歌，不做随机排序");
			return;
		}
		int key;
		SongInfo temp;
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
			result = randomArray.indexOf(songInfo);
		}else{
			result = songMenu.indexOf(songInfo);
		} 
		return result;
	}
	
	/**
	 * 取出下一曲的歌曲信息对象
	 */
	private SongInfo getNextSongInfo(int currentIndex){
		SongInfo result = null;
		Vector<SongInfo> songInfoList = playMode == PlayerCode.PLAY_MODE_RANDOM ? randomArray : songMenu;
		if(songInfoList.size() == 1){
			result = songInfoList.get(0);
		}else if(currentIndex>= (songInfoList.size() - 1)){
			result = songInfoList.get(0);
		}else{
			result = songInfoList.get(currentIndex + 1);
		}
		return result;
	}
	
	/**
	 * 取出上一曲的歌曲信息
	 */
	private SongInfo getPreSongInfo(int currentIndex){
		SongInfo result = null;
		Vector<SongInfo> songInfoList =  playMode == PlayerCode.PLAY_MODE_RANDOM ? randomArray : songMenu;
		if(songInfoList.size() == 1){
			result = songInfoList.get(0);
		}else if(currentIndex <= 0){
			result = songInfoList.lastElement();
		}else{
			result = songInfoList.get(currentIndex - 1);
		}
		return result;
	}
	
	/**
	 * 是否允许切歌
	 * @return
	 */
	private boolean isAllowChangeMusic(){
		if(songMenu == null || songMenu.isEmpty()){
			return false;
		}
		return true;
	}
	
	/**
	 * 下一曲
	 */
	public boolean playNextMusic(){
		if(!isAllowChangeMusic()){
			L.w(tag, "当前无歌曲，不允许切歌");
			return false;
		}
		int currentIndex = getCurrentPlayIndex();	
		SongInfo nextSongInfo = getNextSongInfo(currentIndex);
		play(nextSongInfo);
 		return true;
	}
	
	/**
	 * 上一曲
	 */
	public boolean playPreMusic(){
		if(!isAllowChangeMusic()){
			L.w(tag, "当前无歌曲，不允许切歌");
			return false;
		}
		int currentIndex = getCurrentPlayIndex();
		SongInfo preSongInfo = getPreSongInfo(currentIndex);
		play(preSongInfo);
		return true;
	}
	
	/**
	 * 播放
	 */
	public boolean play(){
		if(!AudioPlayerClient.getInstance().isPlaying() && songInfo != null){	//如果正在播放
			AudioPlayerClient.getInstance().start();
			L.e(tag, "执行播放完成");
			handler.post(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(mPlayListener != null){
						mPlayListener.playStateChange(PlayerCode.PLAY_STATE_PLAYING);
					}
				}
			});
			requestRemoteUI();	//请求更新远端UI
		}
		if(!isReadRecord){
			recordDo();
		}
		L.i(tag, "requestAudioFocus 1111");
		requestAudioFocus(); 
		return true;
	}
	
	/**
	 * 暂停
	 */
	public boolean pause(){
		L.i(tag, "pause");
		if(AudioPlayerClient.getInstance().isPlaying()){	//如果正在播放
			AudioPlayerClient.getInstance().pause();
			handler.post(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(mPlayListener != null){
						mPlayListener.playStateChange(PlayerCode.PLAY_STATE_PAUSE);
					}
				}
			});
			requestRemoteUI();	//请求更新远端UI
		}
		return true;
	}
	
	/**
	 * 停止播放
	 */
	public boolean stop(){
//		L.w(tag, "暂未实现");
		L.i(tag, "stop");
		AudioPlayerClient.getInstance().closeMedia();
		return true;
	}
	
	/**
	 * 播放/暂停
	 */
	public boolean playPause(){
		if(AudioPlayerClient.getInstance().isPlaying()){	//如果正在播放
			pause();
		}else{
			//当前是否有歌曲在播放，如果有则回复播放
			if(songInfo != null){
				play();
			}else{//如果没有则，从所有歌曲的第一首开始播放
				Vector<SongInfo> songInfoList = SongManager.getInstance().getAllSong();
				if(songInfoList != null && !songInfoList.isEmpty()){
					playFromSongMenu(songInfoList.get(0), songInfoList);
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
		return AudioPlayerClient.getInstance().isPlaying();
	}
	
	/**
	 * 获得当前播放列表
	 * @return
	 */
	public Vector<SongInfo> getSongMenu(){
		return songMenu;
	}
	
	/**
	 * 获得当前歌曲
	 * @return
	 */
	public SongInfo getSongInfo() {
		return songInfo;
	}
	
	/**
	 * 下一曲
	 */
	private Runnable mNextRunnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			playNextMusic();
		}
	};

	/**
	 * 音频播放回调
	 */
	private AudioPlayerCallback mAudioPlayerCallback = new AudioPlayerCallback() {
		@Override
		public void onSeekComplete() {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public boolean onError(int what, int extra) {
			// TODO Auto-generated method stub
			L.e(tag, "what = " + what + " extra = " + extra);
			if(songMenu != null && songMenu.size() == 1){
				L.e(tag, "播放异常，当前只有一首歌，不继续播放。");
				return false;
			}
			playHandler.postDelayed(mNextRunnable, 1000);
			return false;
		}
		
		@Override
		public void onCompletion() {
			if(CurrentPlayManager.this.playMode == PlayerCode.PLAY_MODE_SINGLE_CYCLE){
				//重新播放
				L.e(tag, "重新播放");
				play(CurrentPlayManager.this.songInfo);
			}else{		//播放下一曲
				L.e(tag, "播放下一曲！");
				playNextMusic();
			}
		}

		@Override
		public String callBackForString(int cmd, String str) {
			String result = null;
			if(mPlayListener != null){
				result = mPlayListener.callBackForString(cmd, str);
			}
			switch(cmd){
			case PlayerCode.CALL_BACK_PLAY_SESSIONID:
				audioSessionID = Integer.parseInt(str);
				break;
			}
			return result;
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
				audioFocusGain();
				break;
			case AudioManager.AUDIOFOCUS_LOSS:						//永久丢失焦点除非重新主动获取
				L.i(tag, "AUDIOFOCUS_LOSS");
				audioFocusLoss();
				break;
			case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:	//短暂丢失焦点，压低后台音量
				L.i(tag, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
				audioFocusLossDuck();
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
	private void audioFocusGain(){
		if(isLossTransient){	//如果暂时丢失焦点
			isLossTransient = false;
			isHaveAudioFocus = true;
			mAudioManager.registerMediaButtonEventReceiver(mComponentName);
			play();
		}
		
		if(isLossDuck){			//如果压低声音试丢失
			isLossDuck = false;
			//恢复音量
			AudioPlayerClient.getInstance().setVolumeUpOrDown(true);
		}
	}
	
	/**
	 * 永久丢失焦点
	 */
	private void audioFocusLoss(){
		abandAudioFocus();
		isHaveAudioFocus = false;
		isLossTransient = true;
		pause();
	}
	 
	/**
	 * 短暂丢失焦点
	 */
	private void audioFocusLossDuck(){
		isLossDuck = true;
		//降低媒体音量
		AudioPlayerClient.getInstance().setVolumeUpOrDown(false);
	}
	
	
	
	/** 歌单对话框 */
	private AlertDialog dialog;
	CurrentSongListView mCurrentSongListView;
	/**
	 * 当前歌单弹出框显示
	 */
	public void showCurrentMusicMenu(Context context) {
		if(CurrentPlayManager.getInstance().getSongMenu() == null){
			L.w(tag, "当前无歌单");
			return;
		}
		if(dialog == null || mCurrentSongListView == null || (mCurrentSongListView != null && !mCurrentSongListView.getContext().equals(context))){
			 dialog = new AlertDialog.Builder(context).create();
			 mCurrentSongListView = new CurrentSongListView(context);
		}
		if(mCurrentSongListView.getParent() != null){
			((ViewGroup)mCurrentSongListView.getParent()).removeView(mCurrentSongListView);
		}
		dialog.show();
		Window window = dialog.getWindow();
		WindowManager.LayoutParams params = window.getAttributes();  
		params.dimAmount = 0f;  
		window.setAttributes(params); 
		window.setContentView(mCurrentSongListView);
		window.setGravity(Gravity.BOTTOM);  
		window.setLayout(LayoutParams.MATCH_PARENT, getWH(context)[1]/2/*LayoutParams.WRAP_CONTENT*/);
		window.setWindowAnimations(R.style.AnimBottom);
		mCurrentSongListView.load();
		L.i(tag, "准备加载歌单");
	}
	
	
	CollectionSongListView mCollectionSongListView;
	
	/**
	 * 当前收藏歌单弹出框显示
	 */
	public void showCollectionMusicMenu(Context context) {
		if(CurrentPlayManager.getInstance().getSongMenu() == null){
			L.w(tag, "当前无歌单");
			return;
		}
		if(dialog == null || mCollectionSongListView == null || (mCollectionSongListView != null && !mCollectionSongListView.getContext().equals(context))){
			 dialog = new AlertDialog.Builder(context).create();
			 mCollectionSongListView = new CollectionSongListView(context);
		}
		if(mCollectionSongListView.getParent() != null){
			((ViewGroup)mCollectionSongListView.getParent()).removeView(mCollectionSongListView);
		}
		dialog.show();
		Window window = dialog.getWindow();
		WindowManager.LayoutParams params = window.getAttributes();  
		params.dimAmount = 0f;  
		window.setAttributes(params); 
		window.setContentView(mCollectionSongListView);
		window.setGravity(Gravity.BOTTOM);  
		window.setLayout(LayoutParams.MATCH_PARENT, getWH(context)[1]/2/*LayoutParams.WRAP_CONTENT*/);
		window.setWindowAnimations(R.style.AnimBottom);
		mCollectionSongListView.load();
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
	 * 当前歌曲是否被收藏
	 */
	public boolean isCollection(){
		return SongManager.getInstance().isConllection(this.songInfo);
	}
	/**
	 * 收藏处理：
	 * 	如果没有收藏则收藏，如果收藏了则取消收藏
	 */
	public boolean ctrlCollectionCurrentSong(CollectionCallBack mCollectionCallBack){
		if(this.songInfo == null){
			L.w(tag, "当前未播放歌曲");
			return false;
		}
		boolean result = false;
		if(isCollection()){
			L.i(tag, "删除收藏");
			result = SongManager.getInstance().delCollection(this.songInfo, mCollectionCallBack);
		}else{
			L.i(tag, "添加收藏");
			result = SongManager.getInstance().addCollection(this.songInfo, mCollectionCallBack);
		}
		return result;
	}
	
	/**
	 * 网络下载歌词或图片
	 */
	private void loadLrcAndImageFromNetwork(SongInfo songInfo){
		ImageManager.getInstance().loadNetWorkLRCAndImage(songInfo, mNetWorkLrcAndImageListener, handler, context);
	}
	
	/**
	 * 确保每个回调实例都是唯一的
	 * @date 2015-12-19 上午5:09:02
	 */
	private NetWorkLrcAndImageListener mNetWorkLrcAndImageListener = new  NetWorkLrcAndImageListener(){

		@Override
		public void completeLRC(String lrcPath) {
			L.e(tag, "歌词下载完成：" + lrcPath);
			
			if(mPlayListener != null){
				mPlayListener.callBackForString(PlayerCode.CLL_BACK_LRC_COMPLETE, lrcPath);
			}
		}

		@Override
		public void completeImage(String imagePath) {
			L.e(tag, "图片下载完成：" + imagePath);
			if(mPlayListener != null){
				mPlayListener.callBackForString(PlayerCode.CLL_BACK_IMAGE_COMPLETE, imagePath);
			}
		}
	};
	
	/**
	 * 请求更新远端UI
	 */
	private void requestRemoteUI(){
		MusicRemoteCtrl.getInstance().requestUpdateRemoteUI(context);		//更新小部件
		NotificationWidget.getInstance().requestUpdateRemoteUI(context);	//更新通知栏
	}
	
	/**
	 * 提示
	 */
	public void showToast(final String text){
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
			}
		});
	}
}
