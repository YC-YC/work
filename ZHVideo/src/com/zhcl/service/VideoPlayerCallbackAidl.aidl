package com.zhcl.service;

/**
 * @author ChenLi
 *
 */
 interface VideoPlayerCallbackAidl {
	/**
	 * 播放完成
	 */
	 void onCompletion();
	 
	/**
	 * 播放异常
	 */
	 boolean onError(int what, int extra);
	 
	 
	/**
	 * seek完成
	 */
	 void onSeekComplete();
}
