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
	/**
	 * 
	 * @param status
	 * @param httpCode 传送时的惟一标识（一般为HashCode）,用于标记传送内容
	 */
	public void onStatus(int status, int httpCode);
}
