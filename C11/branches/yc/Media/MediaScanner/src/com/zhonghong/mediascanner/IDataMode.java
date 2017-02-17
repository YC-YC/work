/**
 * 
 */
package com.zhonghong.mediascanner;

import com.zhonghong.mediascanner.bean.DeviceInfo;

/**
 * @author YC
 * @time 2016-12-19 下午8:03:56
 * TODO:提供各类间数据交互的接口
 */
public interface IDataMode {

	/**获取数据库的DeviceInfo*/
	public DeviceInfo getLoadedDeviceInfo(String uuid);
	/**设置数据库的DeviceInfo*/
	public void setLoadedDeviceInfo(DeviceInfo info);
	/**是否有客户端正在请求设备信息*/
	public boolean isReqDeviceInfo(String device);
	
}
