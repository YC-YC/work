/**
 * 
 */
package com.zhonghong.mediascanner.utils;


/**
 * @author YC
 * @time 2016-11-18 上午11:31:27
 * TODO:初始化线程，防止onCreate等UI线程中中处理太多事件，造成加载界面慢，可将一些不涉及UI的工作放在这里
 * 最后调用waitFinish等待初始化完成
 */
public abstract class InitThread extends Thread{

	@Override
	public void run() {
		loadData();
	}
	
	/**加载数据*/
	protected abstract void loadData();
}
