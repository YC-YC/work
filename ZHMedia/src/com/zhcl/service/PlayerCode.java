/**
 * 
 */
package com.zhcl.service;

/**
 * @author ChenLi
 */
public class PlayerCode {
	/** 失败*/
	public static final int RESULT_FAIL = -1;
	/** 成功 */
	public static final int RESULT_SECCESS = 1;
	
	
	/**循环-播放模式 */
	public static final int PLAY_MODE_ALL_CYCLE = 0x101;
	/** 单曲 - 播放博士 */
	public static final int PLAY_MODE_SINGLE_CYCLE = 0x102;
	/**随机-播放模式 */
	public static final int PLAY_MODE_RANDOM = 0x103;
	/** 顺序 - 播放模式 */
	public static final int PLAY_MODE_ORDER = 0x104;
	
	
	/** 播放(状态) */
	public static final int PLAY_STATE_PLAYING = 1;
	/** 暂停（状态） */
	public static final int PLAY_STATE_PAUSE = 2;
	/** 停止（状态） */
	public static final int PLAY_STATE_STOP = 3;
	
	
//回调定义·············
	/** mediaplayer session 回调*/
	public static final int CALL_BACK_PLAY_SESSIONID = 0x200;
	/** 歌词下载完成,但并不代表下载成功，以返回的路径为准 ，如果OK返回的一定是缓存的目录下的歌词*/
	public static final int CLL_BACK_LRC_COMPLETE = 0x210;
	/** 封面下载完成,但并不代表下载成功，以返回的路径为准 */
	public static final int CLL_BACK_IMAGE_COMPLETE = 0x211;
	
	
	/** 拔卡通知 */
	public static final int NOTIFY_DEVICE_EJECT = 0x250;
	/** 插卡通知 */
	public static final int NOTIFY_DEVICE_MOUNT = 0x251;
	/** 切换横屏 */
	public static final int NOTIFY_ORIENTATION_LANDSCAPE = 0x260;
}
