/**
 * 
 */
package com.zhonghong.autotest.bean;

/**
 * @author YC
 * @time 2016-12-9 下午12:14:37
 * TODO:生命周期管理,需要在有生命周期的组件中主动调用，如Service，Activity
 */
public interface ILifeCycle {

	void onObjectCreate();
	void onObjectDestory();
}
