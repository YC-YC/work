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
public class McuServierDataMode implements IDataMode {

	private static final String TAG = "McuServierData";
	private CanProxy mCanProxy;
	private DataListener mDataListener;
	
	public McuServierDataMode() {
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
		mDataListener = null;
	}

	@Override
	public synchronized void sendData(byte[] data) {
		mCanProxy.sendCanDataToMcu(data);
	}
	
	/** Can的信息有变化就是回调 */
	ICanInfoChangedListener caninfolistener = new ICanInfoChangedListener() {
		@Override
		public void notify(int[] changeCMDs, short[] caninfo) {
//			Log.i(TAG, String.format("getCanInfo [%d][%d][%d][%d] packet", caninfo[0], caninfo[1], caninfo[2], caninfo[3]));
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
