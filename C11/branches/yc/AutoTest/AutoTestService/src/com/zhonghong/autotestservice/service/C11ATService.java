/**
 * 
 */
package com.zhonghong.autotestservice.service;

import com.zhonghong.autotestservice.bean.ATEvent;
import com.zhonghong.datamode.IDataMode;
import com.zhonghong.datamode.McuServierDataMode;

/**
 * @author YC
 * @time 2016-11-30 上午11:59:45
 * TODO:
 */
public class C11ATService extends ATService {

	private ProcessManager mProcessManager; 
	
	@Override
	public void onCreate() {
		super.onCreate();
		mProcessManager = new ProcessManager(this);
	}
	
	@Override
	protected byte[] onResponseStart() {
		byte[] data = {0x00, 0x01};
		return mProcessManager.genCANData(data, data.length, ProcessManager.TYPE_POSITIVE_RESPONSE);
	}

	@Override
	protected ATEvent onConvert(byte[] caninfo) {
		if (caninfo[0] != 0x2E && caninfo[1] != 0xBB){
			return null;
		}
		int contentLen = caninfo[2]*0xFF;
		if (contentLen <2){
			return null;
		}
		return mProcessManager.onProcessConvert(caninfo);
	}

	@Override
	protected byte[] onRevert(int type, int cmd, String info) {
		return mProcessManager.onProcessRevert(type, cmd, info);
	}

	@Override
	protected IDataMode getDataMode() {
		return new McuServierDataMode();
	}

}
