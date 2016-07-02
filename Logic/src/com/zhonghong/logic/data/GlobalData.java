/**
 * 
 */
package com.zhonghong.logic.data;

/**
 * @author YC
 * @time 2016-6-22 上午11:35:52
 * TODO:
 */
public class GlobalData {

	/**Logic服务*/
	public static String LOGIC_SERVICE_CLASS = "com.zhonghong.logic.service.LogicService";

	
	
	public interface T{
		String test = "test";
	}
	
	public interface Logic{
		String INIT_OK = "com.zhonghong.ACTION_LOGIC_INIT_OK";
	}
	
	public interface SettingsConfig{
		int MAX_VOL = 39;
	}
}
