/**
 * 
 */
package com.zhcl.service.service.client;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.zh.uitls.L;
import com.zhcl.service.PlayerCode;
import com.zhcl.service.VideoPlayerAidl;

/**
 * @author ChenLi
 */
public class VideoPlayerClient {
	/**
	 * 音频播放回调
	 */
	public interface VideoPlayerCallback{
		public void onCompletion();
		public boolean onError(int what, int extra);
		public void onSeekComplete();
		
	}
	
	/**
	 * 初始化状态监听
	 */
	public interface InitListener{
		/** 初始化成功 */
		public static final int STATE_SUCCESS = 1;
		/** 重新初始化 */
		public static final int STATE_RECONN = 2;
		/** 初始化失败*/
		public static final int STATE_FAIL = -1;
		/** 状态回调 */
		public void state(int state, String content);
	}
	
	private final String tag = "VideoPlayerClient";
	private Context context;
	/** 回调监听 */
	VideoPlayerCallback mVideoPlayerCallback;
	
	private InitListener mInitListener;
	private VideoPlayerClient() {}

	private static VideoPlayerClient mVideoPlayerClient;

	public static VideoPlayerClient getInstance() {
		if (mVideoPlayerClient == null) {
			mVideoPlayerClient = new VideoPlayerClient();
		}
		return mVideoPlayerClient;
	}

	/**
	 * 初始化
	 * @param context
	 * @return
	 */
	public boolean init(Context context) {
		if (isInit()) {
			L.w(tag, "请勿重复初始化");
			return false;
		}
		this.context = context;
		connServer();
		return true;
	}

	/**
	 * 销毁
	 */
	public void destroy() {
		L.i(tag, "destroy");
		if (!isInit()) {
			L.w(tag, "尚未初始化");
			return ;
		}
		this.context = null;
		context.unbindService(mServiceConnection);
	}

	public void setVideoPlayerCallback(VideoPlayerCallback mVideoPlayerCallback){
		this.mVideoPlayerCallback = mVideoPlayerCallback;
	}
	
	/**
	 * 连接服务
	 */
	private void connServer() {
		Intent it = new Intent("com.zhcl.videoplayerservice");
		context.bindService(it, mServiceConnection, Service.BIND_AUTO_CREATE);
	}

	/**
	 * 是否初始化完成
	 * 
	 * @return
	 */
	private boolean isInit() {
		return this.context != null;
	}
	
	/**
	 * 是否已连接
	 */
	public boolean isConn(){
		return mVideoPlayerAidl != null;
	}
	
	VideoPlayerAidl mVideoPlayerAidl = null;
	
	
	public void setInitListener(InitListener mInitListener){
		this.mInitListener = mInitListener;
	} 
	/**
	 * 连接对象
	 */
	private ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mVideoPlayerAidl = null;
		}

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			mVideoPlayerAidl = VideoPlayerAidl.Stub.asInterface(arg1);
			try {
				mVideoPlayerAidl.register(new VideoCallback());
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(mInitListener != null){
				mInitListener.state(InitListener.STATE_SUCCESS, null);
			}
			L.w(tag, "服务连接成功");
		}
	};
	 
	/**
	 * 打开媒体，但不播放
	 * @param path
	 * @return 状态码
	 */
	public int openMedia(String path){
		int result = PlayerCode.RESULT_FAIL;
		if(isConn()){
			try {
				result = mVideoPlayerAidl.openMedia(path);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
	
	/**
	 * 播放
	 * @return 状态码
	 */
	public int start(){
		int result = PlayerCode.RESULT_FAIL;
		if(isConn()){
			try {
				result = mVideoPlayerAidl.start();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
	
	/**
	 * 暂停
	 * @return 状态码
	 */
	public int pause(){
		int result = PlayerCode.RESULT_FAIL;
		if(isConn()){
			try {
				result = mVideoPlayerAidl.pause();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
	
	/**
	 * 跳转播放
	 * @param seek 位置
	 * @return 状态码
	 */
	public int seekTo(int seek){
		int result = PlayerCode.RESULT_FAIL;
		if(isConn()){
			try {
				result = mVideoPlayerAidl.seekTo(seek);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
	
	/**
	 * 关闭媒体
	 * @return 状态码
	 */
	public int closeMedia(){
		int result = PlayerCode.RESULT_FAIL;
		if(isConn()){
			try {
				result = mVideoPlayerAidl.closeMedia();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
	
	/**
	 * 获得总时间
	 * @return 状态码
	 */
	public int getDuration(){
		int result = PlayerCode.RESULT_FAIL;
		if(isConn()){
			try {
				result = mVideoPlayerAidl.getDuration();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
	
	
	/**
	 * 获得当前时间
	 * @return
	 */
	public int getCurrentPosition(){
		int result = PlayerCode.RESULT_FAIL;
		if(isConn()){
			try {
				result = mVideoPlayerAidl.getCurrentPosition();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
	
	/**
	 * 当前播放状态
	 */
	public boolean isPlaying(){
		boolean result = false;
		if(isConn()){
			try {
				result = mVideoPlayerAidl.isPlaying();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
	
	/**
	 * 压低和还原声音
	 */
	public boolean setVolumeUpOrDown(boolean isUp){
		boolean result = false;
		if(isConn()){
			try {
				result = mVideoPlayerAidl.setVolumeUpOrDown(isUp);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
	//回调VideoPlayerCallbackAidl.Stub
	public class VideoCallback extends com.zhcl.service.VideoPlayerCallbackAidl.Stub{

		@Override
		public void onCompletion() throws RemoteException {
			Log.i(tag, "onCompletion");
			if(mVideoPlayerCallback != null){
				mVideoPlayerCallback.onCompletion();
			}
		}

		@Override
		public boolean onError(int what, int extra) throws RemoteException {
			Log.i(tag, "onError");
			boolean result = false;
			if(mVideoPlayerCallback != null){
				result = mVideoPlayerCallback.onError(what, extra);
			}
			return result;
		}

		@Override
		public void onSeekComplete() throws RemoteException {
			Log.i(tag, "onSeekComplete");
			if(mVideoPlayerCallback != null){
				mVideoPlayerCallback.onSeekComplete();
			}
		}
		
	}
}
