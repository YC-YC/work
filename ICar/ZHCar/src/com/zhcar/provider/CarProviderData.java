/**
 * 
 */
package com.zhcar.provider;

/**
 * @author YC
 * @time 2016-7-7 下午2:26:18
 * TODO:定义一些常用变量
 */
public interface CarProviderData {

	/**车辆信息表名*/
	String CARINFO_TABLE = "carinfo";
	/**车辆号码表名*/
	String PHONENUM_TABLE = "phonenum";
	/**鉴权结果*/
	String PERMISSION_TABLE = "permission";
	
	/**车辆信息键值*/
	String KEY_VIN = "vin";
	String KEY_SN = "sn";
	String KEY_IMEI = "imei";
	String KEY_ICCID = "iccid";
	String KEY_TOKEN = "token";
	
	/**一键救援号码*/
	String KEY_SHOTCUT_RECUTE_NUM = "shotcutRecureNum";
	String KEY_SHOTCUT_RECUTE_TIME = "roadRecureNum";
	/**一键导航号码*/
	String KEY_SHOTCUT_NAVI_NUM = "shotcutNavNum";
	/**紧急联系人号码1*/
	String KEY_SHOTCUT_EMERGENCY_NUM1 = "shotcutEmergencyNum1";
	/**紧急联系人号码2*/
	String KEY_SHOTCUT_EMERGENCY_NUM2 = "shotcutEmergencyNum2";
	/**紧急联系人阀值*/
	String KEY_SHOTCUT_EMERGENCY_TIME = "shotcutEmergencyTime";
	/**呼叫凯翼号码*/
	String KEY_SHOTCUT_SERVICE_NUM = "shotcutServiceNum";
	
	/**鉴权结果*/
	String KEY_PERMISSION = "permission";
	
	
}
