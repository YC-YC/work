/**
 * 
 */
package com.zhcar.utils.http;

/**
 * @author YC
 * @time 2016-7-6 上午11:43:46
 * TODO:Http请求状态回调
 */
public interface HttpStatusCallback {
	
	/**data error*/
	int RESULT_DATA_ERR = -2;
	/**connection error*/
	int RESULT_CONN_ERR = -1;
	/**Response Success*/
	int RESULT_SUCCESS = 0;
	/**Response Failed*/
	int RESULT_FAILED = 1;
	
	public void onStatus(int status);
}
