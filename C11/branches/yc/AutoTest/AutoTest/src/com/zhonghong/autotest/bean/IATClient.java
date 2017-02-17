/**
 * 
 */
package com.zhonghong.autotest.bean;

/**
 * @author YC
 * @time 2017-2-8 上午11:22:24
 * TODO:
 */
public interface IATClient {

	/**
	 * 命令分发
	 * @param event
	 * @return
	 */
	public boolean onDispatch(ATEvent event);
}
