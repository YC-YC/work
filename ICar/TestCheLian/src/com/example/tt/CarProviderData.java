/**
 * 
 */
package com.example.tt;

import android.net.Uri;

/**
 * @author YC
 * @time 2016-7-7 下午2:26:18
 * TODO:定义一些常用变量
 */
public interface CarProviderData {

	Uri URI_CARINFO = Uri.parse("content://cn.com.semisky.carProvider/carInfo");
	Uri URI_PHONEINFO = Uri.parse("content://cn.com.semisky.carProvider/phonenum");
	Uri URI_PERMISSION = Uri.parse("content://cn.com.semisky.carProvider/permission");
	Uri URI_FLOWINFO = Uri.parse("content://cn.com.semisky.carProvider/flowInfo");

	
	/**车辆信息表名*/
	String CARINFO_TABLE = "carinfo";
	/**车辆号码表名*/
	String PHONENUM_TABLE = "phonenum";
	/**鉴权结果*/
	String PERMISSION_TABLE = "permission";
	/**sid*/
	String SIDS_TABLE = "sids";
	/**流量信息*/
	String FLOW_TABLE = "flowInfo";
	/**统计信息*/
	String ACCOUNT_TABLE = "account";
	
	/**车辆信息键值,对应的值全为String*/
	String KEY_CARINFO_VIN = "vin";
	String KEY_CARINFO_SN = "sn";
	String KEY_CARINFO_MEID = "meid";
	String KEY_CARINFO_ICCID = "iccid";
	String KEY_CARINFO_TOKEN = "token";
	String KEY_CARINFO_TSP_KEY = "tsp_key";
	String KEY_CARINFO_CLD_KEY = "cld_key";
	String KEY_CARINFO_IMSI = "imsi";
	String KEY_CARINFO_IMEI = "imei";
	
	/**一键救援号码,对应的值为String*/
	String KEY_PHONENUM_RECUTE_NUM = "shotcutRecureNum";
	/**一键救援电话阀值,对应的值为int*/
	String KEY_PHONENUM_RECUTE_TIME = "roadRecureNum";
	/**一键导航号码,对应的值为String*/
	String KEY_PHONENUM_NAVI_NUM = "shotcutNavNum";
	/**紧急联系人号码1,对应的值为String*/
	String KEY_PHONENUM_EMERGENCY_NUM1 = "emergencyNum1";
	/**紧急联系人号码2,对应的值为String*/
	String KEY_PHONENUM_EMERGENCY_NUM2 = "emergencyNum2";
	/**紧急联系人阀值,对应的值为int*/
	String KEY_PHONENUM_EMERGENCY_TIME = "emergencyTime";
	/**呼叫凯翼号码,对应的值为String*/
	String KEY_PHONENUM_KAIYI_NUM = "kaiyiNum";
	
	/**鉴权结果*/
	String KEY_PERMISSION_ABLE = "able";
	String KEY_PERMISSION_SID = "sid";
	String KEY_PERMISSION_PID = "pid";
	
	/**SID*/
	String KEY_SID = "sid";
	String KEY_SID_EXPIRED = "expired";
	
	/**提醒值,对应的值为String*/
	String KEY_FLOWINFO_REMINDVALUE = "remindValue";
	/**sim号码,对应的值为String*/
	String KEY_FLOWINFO_PHONENUM = "phoneNum";
	/**是否提醒,对应的值为String*/
	String KEY_FLOWINFO_REMIDE = "remind";
	/**已用流量,对应的值为String*/
	String KEY_FLOWINFO_USEFLOW = "useFlow";
	/**剩余流量,对应的值为String*/
	String KEY_FLOWINFO_SURPLUSFLOW = "surplusFlow";
	/**总流量,对应的值为String*/
	String KEY_FLOWINFO_CURRFLOWTATAL = "currFlowTotal";
	
	String KEY_ACCOUNT_STATUS = "status";
	String KEY_ACCOUNT_UID = "uid";
	String KEY_ACCOUNT_AID = "aid";
	String KEY_ACCOUNT_MOBILE = "mobile";
	String KEY_ACCOUNT_IDNUMBER = "idNumber";
}
