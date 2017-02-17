/**
 * 
 */
package com.zhonghong.mediascanner.saver;

/**
 * @author YC
 * @time 2016-12-21 上午10:11:38
 * TODO:接口工厂类
 */
public class RecordDeviceFactory {

	private static IRecordDevice mRecordDevice;
	
	public static synchronized IRecordDevice getRecordDevice(){
		if (mRecordDevice == null){
			mRecordDevice = new FileRecordDevice();
		}
		return mRecordDevice;
	}
}
