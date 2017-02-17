/**
 * 
 */
package com.zhonghong.updatecan;

/**
 * @author YC
 * @time 2016-10-17 下午8:03:17
 * TODO:升级状态回调
 */
public interface UpdateCanStatus {

	/**开始升级，value为文件大小*/
	int STATUS_START = 1;
	/**升级中，value为已发送数据大小*/
	int STATUS_UPDATEING = 2;
	/**升级完成*/
	int STATUS_FINISH = 3;
	/**升级出错， 0：文件不存在， 1：文件错误, 2:正在升级, 3:升级错误*/
	int STATUS_ERROR = 4;

	
	public void onStatus(int cmd, int value);
}
