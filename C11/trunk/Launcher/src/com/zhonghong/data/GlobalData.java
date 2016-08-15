/**
 * 
 */
package com.zhonghong.data;

/**
 * @author YC
 * @time 2016-7-24 上午11:16:35
 * TODO:共有数据
 */
public class GlobalData {

	
	/**加锁，解锁Key*/
	public static final String KEY_LOCK = "lock";
	/**加锁，解锁 广播的Action*/
	public static final String ACTION_LOCK = "com.zhonghong.launcher.lock";

	public static final int MEDIA_WIDGET_TYPE_DEFAULT = 0;
	public static final int MEDIA_WIDGET_TYPE_RADIO = 1;
	public static final int MEDIA_WIDGET_TYPE_MUSIC = 2;
	public static final int MEDIA_WIDGET_TYPE_BTMUSIC = 3;
	
	/**标记媒体widget的状态*/
	public static int MediaWidgetType = MEDIA_WIDGET_TYPE_DEFAULT;
	/**
	 * @author YC
	 * @time 2016-7-24 上午11:17:50
	 * TODO:收音机widget信息
	 */
	public static class Radio{
		/**当前频率*/
		public static String CUR_FREQ = "";
		/**标题*/
		public static String TITLE = "";
	}
	
	/**
	 * @author YC
	 * @time 2016-7-24 上午11:22:00
	 * TODO:音乐widget信息
	 */
	public static class Music{
		/**标题*/
		public static String TITLE = "";
		/**作曲家*/
		public static String ARTISE = "";
		/**当前播放路径*/
		public static String CUR_PLAY_PATH = "";
	}
	
	public static class BtMusic{
		/**标题*/
		public static String TITLE = "";
	}
}
