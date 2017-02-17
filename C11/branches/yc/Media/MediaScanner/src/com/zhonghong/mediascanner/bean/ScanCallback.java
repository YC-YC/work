/**
 * 
 */
package com.zhonghong.mediascanner.bean;

/**
 * @author YC
 * @time 2016-12-9 下午5:06:57
 * TODO:扫描回调
 */
public interface ScanCallback {
	
	/**开始加载数据库*/
	int STATUS_START_DB_LOAD = 1;
	/**加载数据库完成*/
	int STATUS_FINISH_DB_LOAD = 2;
	/**开始保存数据库*/
	int STATUS_START_DB_SAVE = 3;
	/**保存数据库完成*/
	int STATUS_FINISH_DB_SAVE = 4;
	
	/**开始扫描*/
	int STATUS_START_SCAN = 5;
	/**中断扫描*/
	int STATUS_BREAK_SCAN = 6;
	/**扫描完成*/
	int STATUS_FINISH_SCAN = 7;
	/**设备在扫描中*/
	int STATUS_IN_SCANNING = 8;
	
	/**
	 * 状态回调
	 * @param status {@link #STATUS_START_SCAN #STATUS_BREAK_SCAN #STATUS_FINISH_SCAN #STATUS_BREAK_SCAN}
	 * @param info
	 */
	void onStatus(String device, int status, String info);
	
}
