/**
 * 
 */
package com.zhonghong.autotest.bean;

/**
 * @author YC
 * @time 2016-12-6 上午9:54:19
 * TODO:自动化测试事件
 */
public class ATEvent {
	public int type;
	public int cmd;
	public String info;
	
	public ATEvent(){
		
	}
	
	public ATEvent(int type, int cmd, String info){
		this.type = type;
		this.cmd = cmd;
		this.info = info;
	}
}
