/**
 * 
 */
package com.zhcar.data;

import android.content.Context;

/**
 * @author YC
 * @time 2016-7-7 下午5:59:52
 * TODO:共用的数据
 */
public class GlobalData {
	public static Context mContext;
	/**鉴权状态*/
	public static boolean bPermissionStatus = false;
	
	public static final String AppKey = "9909756346";
	public static final String SecretKey = "9fd4b70a1892bec00906d5dedf5ebea6";

	/**流量信息*/
	public static FlowInfoBean flowInfo = new FlowInfoBean();
	
	/**发送3G相关信息的ACTION*/
	public static final String ACTION_3G = "com.zhcar.ACTION_CONTROL";
	/**需不需要ARM拨号, on:ARM拨号, off:ARM不需要拨号*/
	public static final String KEY_3G_CONTROL = "control";
	/**3G拨号状态 on:已连接, off:断开*/
	public static final String KEY_3G_STATE = "state";
}
