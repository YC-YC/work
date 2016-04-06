/**
 * 
 */
package com.zhcl.ui.video;

/**
 * 宿主回调
 * @author ChenLi
 */
public interface HostCallBack {
	/** 成功 */
	int STATE_SUCCESS = 1;
	/** 失败 */
	int STATE_FAIL = -1;
	
	/**
	 * 获得状态
	 */
	public int getState();
	
	
	/**
	 * 预留通信
	 */
	public String connString(int cmd, String value);
	
	/**
	 * 设置回调
	 */
	public void addChildCallBack(ChildCallBack mChildCallBack);
}
