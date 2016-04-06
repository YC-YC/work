/**
 * 
 */
package com.zhcl.remote.ui;

import android.content.Context;

/**
 * @author chen
 */
public interface IRemote {
	/** 上一曲 */
	public static final int MUSIC_PLAYER_PRE = 0x001;
	/** 下一曲 */
	public static final int MUSIC_PLAYER_NEXT = 0x002;
	/** 播放 */
	public static final int MUSIC_PLAYER_PLAY = 0x003;
	/** 暂停 */
	public static final int MUSIC_PLAYER_PAUSE = 0x004;
	/** 播放/暂停 */
	public static final int MUSIC_PLAYER_PLAY_PAUSE = 0x005;
	/** 小部件可用  */
	public static final int MUSIC_WIDGET_ENABLE = 0x006;
	/** 所有小部件不可用 */
	public static final int MUSIC_WIDGET_DISENABLE = 0x007;
	/** 默认进入界面 */
	public static final int PAGE_ENTER_DEFAULT_PAGE = 0x020;
	/** 进列表界面 */
	public static final int PAGE_ENTER_LIST_PAGE = 0x021;
	
	/**
	 * 远端ui操作回调控制
	 * @param cmd
	 */
	public void requestRemoteCtrl(int cmd);
	
	/**
	 * 请求更新远端UI
	 */
	public void requestUpdateRemoteUI(Context context);
}
