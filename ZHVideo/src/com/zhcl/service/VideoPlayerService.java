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
import android.os.IBinder;
import android.os.RemoteException;
import android.view.SurfaceHolder;

import com.zh.uitls.L;
import com.zhcl.zhvideo.VideoPlayAcitivity;

/**
 * @author ChenLi
 *
 */
public class VideoPlayerService extends Service{
	
	private String tag = "VideoPlayerService";

	private String path;
	private String url;
	private String state;
	private MediaPlayer mMediaPlayer;
	private VideoPlayerCallbackAidl mVideoPlayerCallbackAidl;
	private boolean isPrepare = false;
	Context context;
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
	

	
	public class LocalBinder extends VideoPlayerAidl.Stub {

		@Override
		public int openMedia(String path) throws RemoteException {
			// TODO Auto-generated method stub
			return VideoPlayerService.this.openMedia(path);
		}

		@Override
		public int start() throws RemoteException {
			// TODO Auto-generated method stub
			return VideoPlayerService.this.start();
		}

		@Override
		public int pause() throws RemoteException {
			// TODO Auto-generated method stub
			return VideoPlayerService.this.pause();
		}

		@Override
		public int seekTo(int seek) throws RemoteException {
			return VideoPlayerService.this.seekTo(seek);
		}

		@Override
		public int closeMedia() throws RemoteException {
			// TODO Auto-generated method stub
			return VideoPlayerService.this.closeMedia();
		}

		@Override
		public int getDuration() throws RemoteException {
			// TODO Auto-generated method stub
			return VideoPlayerService.this.getDuration();
		}

		@Override
		public int getCurrentPosition() throws RemoteException {
			// TODO Auto-generated method stub
			return VideoPlayerService.this.getCurrentPosition();
		}

		@Override
		public boolean isPlaying() throws RemoteException {
			// TODO Auto-generated method stub
			return VideoPlayerService.this.isPlaying();
		}

		@Override
		public void register(VideoPlayerCallbackAidl mVideoPlayerCallback) throws RemoteException {
			VideoPlayerService.this.mVideoPlayerCallbackAidl = mVideoPlayerCallback;
		}

		@Override
		public boolean setVolumeUpOrDown(boolean isUp) throws RemoteException {
			return VideoPlayerService.this.setVolumeUpOrDown(isUp);
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
		try {
			File file = new File(path); 
			if(!file.exists()){
				L.e("chenli", "文件是否存在：" + file.exists() + ",文件路径：" + file.getAbsolutePath());
				return PlayerCode.RESULT_FAIL;
			}
			mMediaPlayer = createMediaPlayer();
			addListener(mMediaPlayer);
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC); 
			if(VideoPlayAcitivity.getSurfaceView() != null){
				SurfaceHolder surfaceHolder = VideoPlayAcitivity.getSurfaceView().getHolder();
				mMediaPlayer.setDisplay(surfaceHolder);
			}else{
				return PlayerCode.RESULT_FAIL;
			}
			fis = new FileInputStream(file); 
			mMediaPlayer.setDataSource(fis.getFD()); 
			mMediaPlayer.prepare();
			isPrepare = true;
			L.d(tag, "open ok");
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
	
//	/**
//	 * 重新设置视频分辨率
//	 * surfaceView被包裹在指定区域，将视频适配到指定区域大小
//	 */
//	private boolean resetVideoFixed(SurfaceView surface, String path){
//		MediaMetadataRetriever retriever = null;
//		SurfaceHolder surfaceHolder = null;
//		try{
//			surfaceHolder = surface.getHolder();
//			retriever = new MediaMetadataRetriever();
//			retriever.setDataSource(path);
//			int surfaceW = VideoPlayAcitivity.getWH()[0];//surface.getWidth();
//			int surfaceH = VideoPlayAcitivity.getWH()[1];//surface.getHeight();
//			// 取得视频的长度(单位为毫秒)
//			 int width = Integer.parseInt(retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
//		     int height = Integer.parseInt(retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
//		     float currentScale = surfaceW * 1.0f / surfaceH;
//		     float trueScale = width * 1.0f / height;
//		     int resultW = 0, resultH = 0;
//		     if(trueScale >= currentScale){			//宽大于等于长
//		    	 resultW = surfaceW;
//		    	 resultH = (int)(resultW / trueScale);
//		     }else{
//		    	 resultH = surfaceH;
//		    	 resultW = (int)(resultH * trueScale);
//		     } 
//		     L.e(tag, "surfaceW = " + surfaceW + " surfaceH = " + surfaceH);
//		     L.e(tag, "trueScale = " + trueScale + " width = " + width + " height = " + height);
//		     L.e(tag, "resultW = " + resultW + " resultH = " + resultH);
//		     surfaceHolder.setFixedSize(resultW, resultH); //显示的分辨率,不设置为视频默认
//		}catch(Exception e){
//			e.printStackTrace();
//			surfaceHolder.setFixedSize(surface.getMeasuredWidth(), surface.getMeasuredHeight());
//			return false;
//		}finally{
//			retriever.release();
//		}
//		return true;
//	}
	
	/**
	 * 添加一系列监听
	 */
	private void addListener(MediaPlayer mMediaPlayer){
		mMediaPlayer.setOnCompletionListener(mOnCompletionListener);	//设置播放完成监听
		mMediaPlayer.setOnErrorListener(mOnErrorListener);
		mMediaPlayer.setOnSeekCompleteListener(mOnSeekCompleteListener);
	}
	
	/**
	 * seek完成监听
	 */
	private OnSeekCompleteListener mOnSeekCompleteListener = new OnSeekCompleteListener() {
		@Override
		public void onSeekComplete(MediaPlayer mp) {
			L.i(tag, "onSeekComplete");
			try {
				VideoPlayerService.this.mVideoPlayerCallbackAidl.onSeekComplete();
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
				result = VideoPlayerService.this.mVideoPlayerCallbackAidl.onError(what, extra);
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
			if(errorPath != null && errorPath.equals(path)){
				L.e(tag, "播放错误，不执行完成监听");
				errorPath = null;
				return;
			}
			try {
				VideoPlayerService.this.mVideoPlayerCallbackAidl.onCompletion();
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
			mMediaPlayer.reset();
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
		if(isAllowCall() && mMediaPlayer == null){
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
	 * 是否允许调度
	 */
	private boolean isAllowCall(){
		return isPrepare;
	}
	
}
