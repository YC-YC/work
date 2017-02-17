/**
 * 
 */
package com.zhonghong.datamode;

/**
 * @author YC
 * @time 2016-12-29 上午9:29:05
 * TODO:数据模型
 */
public interface IDataMode {

	interface DataListener{
		void onDataIn(byte[] data);
	}
	
	
	void registerDataListener(DataListener listener);
	void unregisterDataListener(DataListener listener);
	void sendData(byte[] data);
}
