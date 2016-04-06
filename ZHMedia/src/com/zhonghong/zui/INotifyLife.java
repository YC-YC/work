/**
 * 
 */
package com.zhonghong.zui;

/**
 * 通知其他应用接口
 * @author chen
 */
public interface INotifyLife {
	/**
	 * 进入页面
	 */
	public void enterPage();
	
	/**
	 * 退出页面
	 */
	public void exitPage();
	
	/**
	 * 销毁
	 */
	public void destroy();
}
