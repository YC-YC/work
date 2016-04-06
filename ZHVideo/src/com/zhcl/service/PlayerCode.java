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
	
	
	/** 第一次播放 - 播放类型 */
	public static final int VIDEO_PLAY_TYPE_FIRST = 1;
	/** 继续播放 - 播放类型 */
	public static final int VIDEO_PLAY_TYPE_RESUME = 2;
	/** 断点续播 - 播放类型*/
	public static final int VIDEO_PLAY_TYPE_RECORD = 3;
}
