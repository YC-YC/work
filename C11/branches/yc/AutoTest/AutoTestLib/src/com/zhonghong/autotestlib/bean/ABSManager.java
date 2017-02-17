/**
 * 
 */
package com.zhonghong.autotestlib.bean;

import com.zhonghong.autotestlib.ATConn;

/**
 * @author YC
 * @time 2017-2-7 上午9:41:34
 * TODO:子服务处理
 */
public abstract class ABSManager implements ILifeCycle{
	
	protected ATConn mATConnService;
	
	public void setATConnService(ATConn service){
		mATConnService = service;
	}
	
	/**
	 * 服务端处理
	 * @param cmd
	 * @param info
	 * @return
	 */
	public abstract boolean onProcess(int cmd, String info);
	
	
}
