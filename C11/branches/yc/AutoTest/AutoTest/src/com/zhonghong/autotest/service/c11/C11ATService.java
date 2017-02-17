/**
 * 
 */
package com.zhonghong.autotest.service.c11;

import com.zhonghong.autotest.service.ATService;
import com.zhonghong.datamode.IDataMode;
import com.zhonghong.datamode.McuServerDataMode;

/**
 * @author YC
 * @time 2016-11-30 上午11:59:45
 * TODO:
 */
public class C11ATService extends ATService {

	private ProcessManager mProcessManager; 
	
	@Override
	public void onCreate() {
		mProcessManager = new ProcessManager(this);
		super.onCreate();
		mProcessManager.onObjectCreate();
	}
	
	@Override
	public void onDestroy() {
		mProcessManager.onObjectDestory();
		super.onDestroy();
	}
	
	@Override
	protected byte[] onResponseStart() {
		byte[] data = {0x01, 0x00};
		return mProcessManager.genCANData(data, data.length);
	}

	@Override
	protected IDataMode getDataMode() {
		return new McuServerDataMode();
	}

	@Override
	protected void onReceive(byte[] caninfo) {
		if (caninfo[0] != 0x2E && caninfo[1] != 0x1A){
			return;
		}
		mProcessManager.onReceiveCanData(caninfo);
	}

}
