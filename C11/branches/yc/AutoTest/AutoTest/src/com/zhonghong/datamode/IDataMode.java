/**
 * 
 */
package com.zhonghong.datamode;

/**
 * @author YC
 * @time 2016-11-30 下午2:59:12
 * TODO:数据模型接口
 */
public interface IDataMode {

	interface DataListener{
		void onDataIn(byte[] data);
	}
	
	void registerDataListener(DataListener listener);
	void unregisterDataListener(DataListener listener);
	void sendData(byte[] data);
}
