package com.zhonghong.logic.autotest;

interface ATInterface{
	/**
	 * 
	 * @param screenID
	 * @param val
	 */
	void setVolume(int screenID, int val);
	
	/**
	 * 
	 * @param screenID
	 */
	void setMute(int screenID);
}