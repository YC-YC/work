/**
 * 
 */
package com.zhonghong.autotest.client;

import com.zhonghong.autotest.bean.IATClient;
import com.zhonghong.autotest.bean.ILifeCycle;
import com.zhonghong.autotest.service.ATService;
import com.zhonghong.autotest.service.c11.ProcessManager;


/**
 * @author YC
 * @time 2017-2-7 上午9:41:34
 * TODO:子服务处理
 */
public abstract class ABSClient implements ILifeCycle, IATClient{
	
	protected ProcessManager mProcessManager;
	
	public ABSClient(ProcessManager processManager){
		this.mProcessManager = processManager;
	}
	
	@Override
	public void onObjectCreate() {
		this.mProcessManager.registerClient(this);
	}

	@Override
	public void onObjectDestory() {
		this.mProcessManager.unregisterClient(this);
	}
	
	
	protected abstract void onResponse(int cmd, String info);
}
