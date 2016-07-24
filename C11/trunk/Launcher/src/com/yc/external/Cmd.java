/**
 * 
 */
package com.yc.external;

/**
 * @author YC
 * @time 2016-7-15 下午12:19:19
 * TODO:CMD定义
 */
public interface Cmd {

	/**
	 * postInfo cmd
	 */
	int POST_RADIO_INFO = 100;
	int POST_MUSIC_INFO = 101;
	
	/**
	 * getInfo cmd
	 */
	int GET_RADIO_INFO = 100;
	int GET_MUSIC_INFO = 101;
}
