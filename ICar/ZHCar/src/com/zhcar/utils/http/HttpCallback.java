/**
 * 
 */
package com.zhcar.utils.http;

/**
 * @author YC
 * @time 2016-7-6 上午11:43:46
 * TODO:Http请求回调
 */
public interface HttpCallback {
	public void onReceive(String response);
}
