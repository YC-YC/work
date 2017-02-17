/**
 * 
 */
package com.zhcar.readnumber;

/**
 * @author YC
 * @time 2016-8-1 下午5:48:49
 * TODO:设备状态改变
 */
public interface IUSBStateChange {

	public void onUnmounted(String devPath);
	public void onMounted(String devPath);
}
