package com.zhcl.service;

/**
 * @author ChenLi
 *
 */
 interface AudioPlayerCallbackAidl {
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
	 
	/**
	 * 预留回调接口
	 */
	 String callBackForString(int cmd, String str);
	 
}
