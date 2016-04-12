package com.zhonghong.canbase.service;

public abstract class CanParserBase {
	public abstract void parsePacket(short[] caninfo);
	/**
	 * 获取byte中连续几个bit位的int值
	 * @param b  解析第几个字节 
	 * @param bit 最小的是第几位
	 * @param len 需要几位的值
	 * @return
	 */
	public int getIntByBits(short b , int bit,int len){
		return ((b>>bit))&((int)(Math.pow(2, len)-1));
	}
}
