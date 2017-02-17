/**
 * 
 */
package com.zhonghong.datamode;

import android.zhonghong.mcuservice.CanProxy;
import android.zhonghong.mcuservice.RegistManager.ICanInfoChangedListener;

/**
 * @author YC
 * @time 2016-11-30 下午3:03:29
 * TODO:mcuserver的数据模型
 */
public class McuServerDataMode implements IDataMode {

	private CanProxy mCanProxy;
	private DataListener mDataListener;
	
	public McuServerDataMode() {
		super();
		this.mCanProxy = new CanProxy();
	}

	@Override
	public void registerDataListener(DataListener listener) {
		mCanProxy.registCanInfoChangedListener(caninfolistener);
		mDataListener = listener;
	}

	@Override
	public void unregisterDataListener(DataListener listener) {
		mCanProxy.unregistCanInfoChangeListener(caninfolistener);
	}

	@Override
	public void sendData(byte[] data) {
		mCanProxy.sendCanDataToMcu(data);
	}
	
	/** Can的信息有变化就是回调 */
	ICanInfoChangedListener caninfolistener = new ICanInfoChangedListener() {
		@Override
		public void notify(int[] changeCMDs, short[] caninfo) {
			if (mDataListener != null){
				byte[] data = new byte[caninfo.length];
				for (int i = 0; i < caninfo.length; i++){
					data[i] = (byte) caninfo[i];
				}
				mDataListener.onDataIn(data);
			}
		}
	};

}
