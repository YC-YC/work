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

	public static final String AppKeyProduct = "0559796315";
	public static final String SecretKeyProduct = "cfcc221d052fb704a5e7c2f54af510c8";

	/**生产环境URL*/
//	public static final String URL_HOST_PROCDUCT = "http://cowinmg.timasync.com/";
	public static final String URL_HOST_PROCDUCT = "http://cowinmg.icartsp.com/";
	/**测试环境URL*/
//	public static final String URL_HOST_TEST = "http://cowinmguat.timasync.com/";
	public static final String URL_HOST_TEST = "http://cowinmguat.icartsp.com/";
	/**是否是测试环境，测试环境与生产环境有区别是URL不一样*/
	public static final boolean bIsEnvTest = true;
	
	/**流量信息*/
	public static FlowInfoBean flowInfo = new FlowInfoBean();
	
	/**发送3G相关信息的ACTION*/
	public static final String ACTION_ZHCAR_TO_ZUI = "com.zhcar.ACTION_CONTROL";
	/**需不需要ARM拨号, on:ARM拨号, off:ARM不需要拨号*/
	public static final String KEY_3G_CONTROL = "control";
	/**3G拨号状态 on:已连接, off:断开*/
	public static final String KEY_3G_STATE = "state";
	/**ARM拨打紧急联系人状态 on:arm拨打, off:arm不拨打，can拨打*/
	public static final String KEY_3G_ECALL = "ecall";
	
	/**更新VIN码*/
	public static final String KEY_UPDATE_VIN = "update_vin";
	/**更新SKEY码*/
	public static final String KEY_UPDATE_SKEY = "update_skey";
	/**更新五码*/
	public static final String KEY_UPDATE_FIVE_NUMBER = "update_five_number";
	
	

}
