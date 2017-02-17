/**
 * 
 */
package com.zhonghong.mediascanerlib;

/**
 * @author YC
 * @time 2016-12-8 下午9:23:59
 * TODO:常量定义
 */
public interface ScannerConst {
	
	/**ScannerService服务Action*/
	String SService_ACTION = "com.zhonghong.mediascanner.service.ScannerService.ACTION";
	String SService_KEY_CMD = "key_cmd";
	String SService_CMD_MOUNT = "mount";
	String SService_CMD_UNMOUNT = "unmount";
	String SService_KEY_PATH = "key_path";
	
	/**扫描广播*/
	String SBroadcast_ACTION = "com.zhonghong.mediascanner.broadcast.ACTION";
	String SBroadcast_START_SCAN = "start_scan";
	String SBroadcast_FINISH_SCAN = "finish_scan";
	

	public interface FileType{
		/**音乐信息*/
		int FILE_TYPE_AUDIO = 1;
		/**视频信息*/
		int FILE_TYPE_VIDEO = 2;
		/**图片信息*/
		int FILE_TYPE_IMAGE = 3;
		/**其它信息*/
		int FILE_TYPE_OTHER = 4;
	}
	
	public interface OnEndCode{
		int CODE_FINISH = 1;
		int CODE_ERROR = 2;
		/**没有设备相关信息*/
		int CODE_NO_DATA = 3;
		/**设备被卸载*/
		int CODE_UNMOUNT = 4;
		/**未知*/
		int CODE_UNKNOWN = 5;
	}
	
}
