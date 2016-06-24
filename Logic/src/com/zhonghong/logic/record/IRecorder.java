/**
 * 
 */
package com.zhonghong.logic.record;


/**
 * @author YC
 * @time 2016-6-22 下午3:49:36
 * TODO:读写文件的接口
 */
public interface IRecorder {
	public RecordInfoBean read();
	public void write(RecordInfoBean cellInfo);
}
