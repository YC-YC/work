/**
 * 
 */
package com.zhcl.service;

import java.io.File;
import java.io.FileInputStream;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.audiofx.Visualizer;
import android.os.IBinder;
import android.os.RemoteException;

import com.zh.uitls.L;
import com.zh.uitls.Utils;
import com.zhcl.media.MyVisualizer;

/**
 * @author ChenLi
 *
 */
public class AudioPlayerService extends Service{
	
	private String tag = "AudioPlayerService";

	private String path;
	private String url;
	private String state;
	private MediaPlayer mMediaPlayer;
	private AudioPlayerCallbackAidl mAudioPlayerCallbackAidl;
	Context context;
	private boolean isPrepare = false;
	/** 是否需要还原声音 */
	private boolean isMustDownVolume;
	/** 是否需要压低声音 */
	private boolean isMustUPVolume;
	@Override
	public void onCreate() {
		context = this;
	}
	
	/** 绑定的IBinder */
	private final IBinder mBinder = new LocalBinder();
	
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}
	

	
	public class LocalBinder extends AudioPlayerAidl.Stub {

		@Override
		public int openMedia(String path) throws RemoteException {
			// TODO Auto-generated method stub
			return AudioPlayerService.this.openMedia(path);
		}

		@Override
		public int start() throws RemoteException {
			// TODO Auto-generated method stub
			return AudioPlayerService.this.start();
		}

		@Override
		public int pause() throws RemoteException {
			// TODO Auto-generated method stub
			return AudioPlayerService.this.pause();
		}

		@Override
		public int seekTo(int seek) throws RemoteException {
			return AudioPlayerService.this.seekTo(seek);
		}

		@Override
		public int closeMedia() throws RemoteException {
			// TODO Auto-generated method stub
			return AudioPlayerService.this.closeMedia();
		}

		@Override
		public int getDuration() throws RemoteException {
			// TODO Auto-generated method stub
			return AudioPlayerService.this.getDuration();
		}

		@Override
		public int getCurrentPosition() throws RemoteException {
			// TODO Auto-generated method stub
			return AudioPlayerService.this.getCurrentPosition();
		}

		@Override
		public boolean isPlaying() throws RemoteException {
			// TODO Auto-generated method stub
			return AudioPlayerService.this.isPlaying();
		}

		@Override
		public void register(AudioPlayerCallbackAidl mAudioPlayerCallback) throws RemoteException {
			AudioPlayerService.this.mAudioPlayerCallbackAidl = mAudioPlayerCallback;
		}

		@Override
		public boolean setVolumeUpOrDown(boolean isUp) throws RemoteException {
			return AudioPlayerService.this.setVolumeUpOrDown(isUp);
		}
	}
	
	
	/**
	 * 打开媒体准备播放
	 * @param path
	 */
	public int openMedia(String path){
		L.e(tag, "openMedia :" + path);
		FileInputStream fis = null;
		isPrepare = false;
		this.path = path;
		try {
			File file = new File(path); 
			if(!file.exists()){
				L.e("chenli", "文件是否存在：" + file.exists() + ",文件路径：" + file.getAbsolutePath());
				return PlayerCode.RESULT_FAIL;
			}
			mMediaPlayer = createMediaPlayer();
			addListener(mMediaPlayer);
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);  
			fis = new FileInputStream(file); 
			mMediaPlayer.setDataSource(fis.getFD()); 
			mMediaPlayer.prepare();
			L.d(tag, "open ok");
			isPrepare = true;
			callbackForString(PlayerCode.CALL_BACK_PLAY_SESSIONID, "" + mMediaPlayer.getAudioSessionId());	//回调给予
		} catch (Exception e) {
			// TODO Auto-generated catch block
			L.e(tag, "open fail");
			e.printStackTrace();
			return PlayerCode.RESULT_FAIL;
		}finally{
			try {
				if(fis != null){
					fis.close();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(isPrepare){
			if(isMustUPVolume){
				isMustUPVolume = false;
				setVolumeUpOrDown(false);
			}else if(isMustDownVolume){
				isMustDownVolume = false;
				setVolumeUpOrDown(true);
			}
		}
		return PlayerCode.RESULT_SECCESS;
	}
	
	/**
	 * 添加一系列监听
	 */
	private void addListener(MediaPlayer mMediaPlayer){
		mMediaPlayer.setOnCompletionListener(mOnCompletionListener);	//设置播放完成监听
		mMediaPlayer.setOnErrorListener(mOnErrorListener);
		mMediaPlayer.setOnSeekCompleteListener(mOnSeekCompleteListener);
	}
	/**
	 * 是否允许调度
	 */
	private boolean isAllowCall(){
		return isPrepare;
	}
	
	/**
	 * seek完成监听
	 */
	private OnSeekCompleteListener mOnSeekCompleteListener = new OnSeekCompleteListener() {
		@Override
		public void onSeekComplete(MediaPlayer mp) {
			L.i(tag, "onSeekComplete");
			try {
				AudioPlayerService.this.mAudioPlayerCallbackAidl.onSeekComplete();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	
	String errorPath = "";
	
	/**
	 * 异常监听
	 */
	private OnErrorListener mOnErrorListener = new OnErrorListener() {
		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			// TODO Auto-generated method stub
			L.i(tag, "onError what = " + what + " extra = " + extra);
			isPrepare = false;
			boolean result = false;
			try {
				switch(what){
				case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
					break;
					default:
						break;
				}
				if(mMediaPlayer != null){
					try{
						mMediaPlayer.reset();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				result = AudioPlayerService.this.mAudioPlayerCallbackAidl.onError(what, extra);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			errorPath = path;
			return result;
		}
	};
	
	/**
	 * 播放完成
	 */
	private OnCompletionListener mOnCompletionListener = new OnCompletionListener() {
		@Override
		public void onCompletion(MediaPlayer mp) {
			// TODO Auto-generated method stub
			L.i(tag, "onCompletion 播放完成");
			if(errorPath != null && errorPath.equals(path)){
				L.e(tag, "播放错误，不执行完成监听");
				errorPath = null;
				return;
			}
			try {
				AudioPlayerService.this.mAudioPlayerCallbackAidl.onCompletion();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	
	
	public int start(){
		
		if(isAllowCall() && mMediaPlayer != null){
			 mMediaPlayer.start();
		}
		return PlayerCode.RESULT_SECCESS;
	}
	
	public int pause(){
		if(isAllowCall() && mMediaPlayer != null){
			 mMediaPlayer.pause();
		}
		return PlayerCode.RESULT_SECCESS;
	}
	
	public int seekTo(int seek){
		if(isAllowCall() && mMediaPlayer != null){
			 mMediaPlayer.seekTo(seek);
		}
		return PlayerCode.RESULT_SECCESS;
	}
	

	public int getDuration(){
		if(isAllowCall() && mMediaPlayer != null){
			 return mMediaPlayer.getDuration();
		}
		return PlayerCode.RESULT_FAIL;
	}

	public int getCurrentPosition() throws RemoteException {
		if(isAllowCall() && mMediaPlayer != null){
			 return mMediaPlayer.getCurrentPosition();
		}
		return PlayerCode.RESULT_FAIL;
	}
	
	public int closeMedia(){
		if(isAllowCall() && mMediaPlayer != null){
			mMediaPlayer.stop();
		}
		return PlayerCode.RESULT_SECCESS;
	}
	
	/**
	 * 析构旧有mediaplayer构造新的mediaplayer
	 * @return
	 */
	private MediaPlayer createMediaPlayer(){
		if(mMediaPlayer != null){
			mMediaPlayer.stop();
			mMediaPlayer.reset();			//消耗时间长，在px2上大约消耗500ms
//			mMediaPlayer.release();
		}else{
			mMediaPlayer = new MediaPlayer();
		}
		return mMediaPlayer;//new MediaPlayer();		//避免压低声音后下一曲声音回复，暂不每次都重新构造
	}
	
	/**
	 * 是否正在播放
	 */
	private boolean isPlaying(){
		if(isAllowCall() && mMediaPlayer != null){
			return mMediaPlayer.isPlaying();
		}
		return false;
	}
	
	/**
	 * 压低和还原声音
	 * @param isUp
	 * @return
	 */
	public boolean setVolumeUpOrDown(boolean isUp){
		if(mMediaPlayer == null){
			return false;
		}
		if(isUp){
			L.e(tag, "还原声音");
			isMustDownVolume = false;
			isMustUPVolume = true;
			mMediaPlayer.setVolume(1.0f, 1.0f);
		}else{
			L.e(tag, "压低声音");
			isMustDownVolume = true;
			isMustUPVolume = false;
			mMediaPlayer.setVolume(0.3f, 0.3f);
		}
		return true;
	}
	
	
	/**
	 * 执行回调
	 * @param cmd
	 * @param str
	 */
	public String callbackForString(int cmd, String str){
		String result = null;
		try {
			result = AudioPlayerService.this.mAudioPlayerCallbackAidl.callBackForString(cmd, str);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}
