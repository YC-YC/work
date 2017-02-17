/**
 * 
 */
package com.zhcar.carflow;

import com.zhcar.data.FlowInfoBean;
import com.zhcar.utils.http.HttpStatusCallback;


/**
 * @author YC
 * @time 2016-7-6 上午11:45:55
 * TODO:获取流量接口
 */
public abstract interface GetFlowAbs {

	 /**设置请求参数*/
	 public abstract void SetInfo(String iccid, String token);
	 /** 刷新 */
	 public abstract int Refresh();
	 /** 请求结果回调 */
	 public abstract void SetHttpStatusCallback(HttpStatusCallback callback);
	 /** 获取流量信息 */
	 public abstract FlowInfoBean GetFlowInfo();
	 
}
