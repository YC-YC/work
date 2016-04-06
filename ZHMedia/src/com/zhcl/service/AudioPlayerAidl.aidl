package com.zhcl.service;
import com.zhcl.service.AudioPlayerCallbackAidl;

/**
 * @author ChenLi
 *
 */
 interface AudioPlayerAidl {
 
 	/**
	 * 注册回调
	 * @param path
	 */
	 void register(AudioPlayerCallbackAidl mAudioPlayerCallbackAidl);
 
	/**
	 * 打开媒体，但不播放
	 * @param path
	 * @return 状态码
	 */
	 int openMedia(String path);
	
	/**
	 * 播放
	 * @return 状态码
	 */
	 int start();
	
	/**
	 * 暂停
	 * @return 状态码
	 */
	 int pause();
	
	/**
	 * 跳转播放
	 * @param seek 位置
	 * @return 状态码
	 */
	 int seekTo(int seek);
	
	/**
	 * 关闭媒体
	 * @return 状态码
	 */
	 int closeMedia();
	
	/**
	 * 获得总时间
	 * @return 状态码
	 */
	 int getDuration();
	
	
	/**
	 * 获得当前时间
	 * @return
	 */
	 int getCurrentPosition();
	 
	 /**
	  * 是否正在播放
	  */
	 boolean isPlaying();
	 
	 /**
	  * 设置音频流降低音量
	  */
	 boolean setVolumeUpOrDown(boolean isUp);
	 	
	
}
