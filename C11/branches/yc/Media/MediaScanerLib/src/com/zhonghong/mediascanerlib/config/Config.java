/**
 * 
 */
package com.zhonghong.mediascanerlib.config;

import java.util.HashSet;

/**
 * @author YC
 * @time 2016-12-8 下午2:49:00
 * TODO:配置信息
 */
public interface Config {
	
	/**数据库版本号*/
	int DB_VERSION = 1;
	
	/**文件及数据库保存路径*/
	String DIR_PATH = "/mnt/sdcard/zhmedia/";
	/**设备的数据库文件*/
	String DB_FINE_START_TAG = "device_";
	
	String DIR_DB_TABLE = "Dir_table";
	String MUSIC_DB_TABLE = "Music_table";
	String VIDEO_DB_TABLE = "Video_table";
	String IMAGE_DB_TABLE = "Image_table";
	String OTHER_DB_TABLE = "Other_table";
	
	/**限制保存个数*/
	boolean LIMITED_SAVE_DEVICE_NUM = true;
	/**保存设备信息个数*/
	int SAVER_DEVICE_NUM = 20;
	
	/**限制保存DeviceInfo个数*/
	boolean LIMITED_SAVE_DEVICEINFO_NUM = true;
	/**限制个数*/
	int SAVE_DEVICEINFO_NUM = 4;
	
	/**限制文件夹级数*/
	boolean LIMITED_SCAN_FOLDER_NUM = true;
	/**最高文件夹级数*/
	int MAX_SCAN_FOLDER_NUM = 18;
	
	
	/**是否存储OtherInfo信息*/
	boolean bGetOtherInfo = false;
	
	HashSet<String> AUDIO_EXTENSION = new HashSet<String>(){
		{
			add("aac");
			add("mp3");
			add("raw");
			add("wav");
			add("wma");
		}
	};

	HashSet<String> VIDEO_EXTENSION = new HashSet<String>(){
		{
			add("3gp");
			add("flv");
			add("avi");
			add("mkv");
			add("mp4");
			
			add("mpeg");
			add("mov");
			add("rm");
			add("rmvb");
			add("ts");
			add("tts");
			add("mpg");
		}
	};

	HashSet<String> IMAGE_EXTENSION = new HashSet<String>(){
		{
			add("jpg");
			add("jpeg");
			add("png");
			add("bmp");
		}
	};

	HashSet<String> FOLDER_BLOCKLIST = new HashSet<String>(){
		{
			add("/alarms");
			add("/notifications");
			add("/ringtones");
			add("/media/alarms");
			add("/media/notifications");
			add("/media/ringtones");
			add("/media/audio/alarms");
			add("/media/audio/notifications");
			add("/media/audio/ringtones");
			add("/Android/data");
			add("/LOST.DIR");
			add("/NaviOne");
			add("/iflytek");
			add("/amapauto");
		}
	};
}
