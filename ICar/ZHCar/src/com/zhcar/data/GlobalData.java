/**
 * 
 */
package com.zhcar.data;

/**
 * @author YC
 * @time 2016-7-7 下午5:59:52
 * TODO:共用的数据
 */
public class GlobalData {
	/**没获取到相关信息*/
	public static final int PERMISSION_STATUS_NO_STATUS = 0;
	/**鉴权中...*/
	public static final int PERMISSION_STATUS_RUNNING = 1;
	/**鉴权成功*/
	public static final int PERMISSION_STATUS_SUCCEED = 2;
	/**鉴权失败*/
	public static final int PERMISSION_STATUS_FAILED = 3;
	
	/**鉴权状态*/
	public static int permissionStatus = PERMISSION_STATUS_NO_STATUS;

}
