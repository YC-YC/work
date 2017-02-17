/**
 * 
 */
package com.zhcar.apprecord;

import com.zhcar.data.AppUseRecord;
import com.zhcar.utils.http.HttpStatusCallback;

/**
 * 
 * @author YC
 * @time 2016-7-21 下午5:54:08
 * TODO:网络上传App信息接口
 */
public abstract interface IPostAppRecord {

	/**设置请求参数*/
	 public abstract void SetInfo(AppUseRecord recordInfo);
	 
	 public abstract int Refresh();
	 /** 请求结果回调 */
	 public abstract void SetHttpStatusCallback(HttpStatusCallback callback);
	 
}
