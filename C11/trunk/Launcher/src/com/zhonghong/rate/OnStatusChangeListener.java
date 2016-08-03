/**
 * 
 */
package com.zhonghong.rate;

/**
 * @author YC
 * @time 2016-7-28 下午3:09:35
 * TODO:状态变化
 */
public interface OnStatusChangeListener {

	public void onStatusChange(int oldStatus, int newStatus);
}
