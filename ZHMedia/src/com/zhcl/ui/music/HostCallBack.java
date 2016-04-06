/**
 * 
 */
package com.zhcl.ui.music;

/**
 * 宿主回调
 * @author ChenLi
 */
public interface HostCallBack {
	/** 成功 */
	int STATE_SUCCESS = 1;
	/** 失败 */
	int STATE_FAIL = -1;
	
	/** 播放 （播放监听）*/
	public int PLAY_LISTENER_PALY_NEW = 0x10;
	/** 播放完成（监听）*/
	public int PLAY_LISTENER_PALY_COMPLATE = 0x11;
	/** 播放状态改变（监听）*/
	public int PLAY_LISTENER_PALY_STATE_CH = 0x12;
	/** 断点读取完成 （播放监听）*/
	public int PLAY_LISTENER_READ_RECORD = 0x13;
	/** 进入列表界面*/
	public int PAGE_ENTER_LIST = 0x20;
	/** 进入播放界面 */
	public int PAGE_ENTER_PLAY = 0x21;
	
	/** 申请隐藏当前播放界面（底部小界面）*/
	public static final int REQUESY_HIDE_CURRENT_PLAYBAR = 0x220;
	/** 申请显示当前播放界面（底部小界面）*/
	public static final int REQUESY_SHOW_CURRENT_PLAYBAR = 0x221;
	/** 申请显示当前播放界面 */
	public static final int REQUESY_SHOW_CURRENT_PLAYPAGE = 0x222;
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
	
	/**
	 * 移除回调
	 */
	public void removeChildCallBack(ChildCallBack mChildCallBack);
}
