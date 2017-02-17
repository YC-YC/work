/**
 * 
 */
package com.zhonghong.datamode;

/**
 * @author YC
 * @time 2016-12-29 上午9:37:45
 * TODO:
 */
public class DataFactory {

	private static IDataMode mDataMode;
	
	public static synchronized IDataMode getRecordDevice(){
		if (mDataMode == null){
			mDataMode = new McuServierDataMode();
		}
		return mDataMode;
	}
}
