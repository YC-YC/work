/**
 * 
 */
package com.yc.external;

/**
 * @author YC
 * @time 2016-7-15 下午12:19:19
 * TODO:CMD定义
 */
public interface Cmd {
	/**启动的应用程序*/
	int POST_RECORD_ENTER_APP = 100;
	/**退出的应用程序*/
	int POST_RECORD_EXIT_APP = 101;
	/**车辆vin码*/
	int POST_RECORD_VIN = 102;
	/**应用程序使用状态（连接SP服务成功或者失败"SUCCESS"/"FAILED"）*/
	int POST_RECORD_APPSTATUS = 103;
	/**应用程序栏目（应用程序下的节目名称和音乐名称）*/
	int POST_RECORD_APPPROGRAM = 104;
	/**应用程序作者（应用程序下的节目主持人和音乐演唱者）*/
	int POST_RECORD_APPPROGRAMAUTH = 105;
	
	/**CAN握手成功*/
	int POST_CAN_OK = 110;
	
	String POST_RECORD_KEY_APPNAME = "appName";
	String POST_RECORD_KEY_APPSTATUS = "appStatus";
	String POST_RECORD_KEY_APPPROGRAM = "appProgram";
	String POST_RECORD_KEY_APPPROGRAMAUTH = "appProgramAuth";
	
}
