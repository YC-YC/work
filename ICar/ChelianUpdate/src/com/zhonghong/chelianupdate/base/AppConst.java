package com.zhonghong.chelianupdate.base;

//import android.os.Environment;

public class AppConst {
	public static final String DOWNLOAD_TARGET="/mnt/sdcard/Download/update/";
	public static final String ACTION_CHELIAN_UPDATE="zhonghong.chelian.service.update.action";
	public static final String ACTION_CHELIAN_DOWNLOAD="zhonghong.chelian.service.download.action";
	public static final String ACTION_CHELIAN_INSTALL_UPDATE="zhonghong.chelian.service.install.action";
	public static final String UPDATE_INFO_FILE_NAME="/updateInfo.txt";
	public static final String URL_SOURCE_PART="update-center/getUpdateInfoByVin";
	public static final String URL_REPORT_UPDATE_STATUS="update-center/reportUpdateStatus";
//	public static final String URL_HOST="http://cowinmguat.timasync.com/";
	/**生产环境*/
	public static final String URL_HOST_PRODUCT = "http://cowinmg.timasync.com/";
	/**测试环境*/
	public static final String URL_HOST_TEST = "http://cowinmguat.timasync.com/";
	
	
	//private static final String APP_NAME_OS="os";
	public static final String APP_NAME_AIR="air";
	public static final String APP_NAME_ANDROID="android";
		//private static final String APP_NAME_APP="app";
	public static final String APP_NAME_MCU="mcu";
	public static final String APP_NAME_8836="app8836";
	public static final String APP_ID_8836="app8836";
	public static final String APP_NAME_CAN="can";
}
