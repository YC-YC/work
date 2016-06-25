/**
 * 
 */
package com.zhonghong.leftitem;

import java.util.List;


/**
 * @author YC
 * @time 2016-6-22 下午3:49:36
 * TODO:读写文件的接口
 */
public interface IRecorder {
	public List<Integer> read();
	public void write(List<Integer> listInfo);
}
