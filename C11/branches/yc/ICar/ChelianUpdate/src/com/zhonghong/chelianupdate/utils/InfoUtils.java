package com.zhonghong.chelianupdate.utils;


import java.util.HashMap;
import java.util.Map;

import com.zhonghong.chelianupdate.base.CarProviderData;
import com.zhonghong.chelianupdate.base.GlobalData;
import com.zhonghong.chelianupdate.base.MyApp;
import com.zhonghong.chelianupdate.bean.CarInfo;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;


public class InfoUtils {
	
	private static final String TAG="InfoUtils";
	
	public static CarInfo queryCarinfo() {
		ContentResolver resolver = MyApp.mContext.getContentResolver();
		Cursor cursor = resolver.query(CarProviderData.URI_CARINFO, null, null, null, null);
		if(cursor != null && cursor.moveToNext()){
			CarInfo carInfo=new CarInfo();
			carInfo.setVin(cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_VIN)));
			carInfo.setSn(cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_SN)));
			carInfo.setMeid(cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_MEID)));
			carInfo.setImei(cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_IMEI)));
			carInfo.setImsi(cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_IMSI)));
			carInfo.setIccid(cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_ICCID)));
			carInfo.setToken(cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_TOKEN)));
			
			Log.i(TAG, "查询carinfo结果为：vin = " + carInfo.getVin() + ", sn = " + carInfo.getSn() 
					+ ", imei = " + carInfo.getImei() + ", iccid = " + carInfo.getIccid() + ", token = " + carInfo.getToken());
			return carInfo;
			}
		else{
			if (cursor == null){
				Log.i(TAG, "无查询结果为null");				
			}
			Log.i(TAG, "无查询结果");
			return null;
		}
	}
	
	/**
	 * 更新车辆信息到provider
	 */
	public static void updateCarInfo() {
		ContentResolver resolver = MyApp.mContext.getContentResolver();
		ContentValues values = new ContentValues();
		//可以只选其中一个
		values.put(CarProviderData.KEY_CARINFO_VIN, "LVTDB11B1TS000004");
		values.put(CarProviderData.KEY_CARINFO_ICCID, "8986031690021000151");
		values.put(CarProviderData.KEY_CARINFO_SN, "CL6230004TS0");
		values.put(CarProviderData.KEY_CARINFO_MEID, "A100001FA0D7DA");
		values.put(CarProviderData.KEY_CARINFO_IMEI, "A100001FA0D7DA");
		
		Cursor cursor = resolver.query(CarProviderData.URI_CARINFO, null, null, null, null);
		if(cursor != null && cursor.moveToNext()){
			//int row = resolver.update(CarProviderData.URI_CARINFO, values, null, null);
		}
		else{
			resolver.insert(CarProviderData.URI_CARINFO, values);
		}
	}
	
	
	public static void queryPhoneInfo() {
		ContentResolver resolver = MyApp.mContext.getContentResolver();
		Cursor cursor = resolver.query(CarProviderData.URI_PHONEINFO, null, null, null, null);
		if(cursor != null && cursor.moveToNext()){
			String recuteNum = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_PHONENUM_RECUTE_NUM));
			int recuteTime = cursor.getInt(cursor.getColumnIndex(CarProviderData.KEY_PHONENUM_RECUTE_TIME));
			String NaviNum = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_PHONENUM_NAVI_NUM));
			String emergencyNum1 = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_PHONENUM_EMERGENCY_NUM1));
			String emergencyNum2 = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_PHONENUM_EMERGENCY_NUM2));
			int emergencyTime = cursor.getInt(cursor.getColumnIndex(CarProviderData.KEY_PHONENUM_EMERGENCY_TIME));
			String serviceNum = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_PHONENUM_KAIYI_NUM));
			
			Log.i(TAG, "查询numinfo结果为：recuteNum = " + recuteNum + ", recuteTime = " + recuteTime 
					+ ", NaviNum = " + NaviNum + ", emergencyNum1 = " + emergencyNum1 + ", emergencyNum2 = " + emergencyNum2
					+ ", emergencyTime = " + emergencyTime + ", serviceNum = " + serviceNum);
			}
		else{
			Log.i(TAG, "无查询结果");
		}
	}
	
	/**
	 * 更新流量信息到provider
	 */
	public static void updatePhoneInfo() {
		ContentResolver resolver = MyApp.mContext.getContentResolver();
		ContentValues values = new ContentValues();
		//可以只选其中一个
		values.put(CarProviderData.KEY_PHONENUM_EMERGENCY_NUM1, "10086");
		values.put(CarProviderData.KEY_PHONENUM_EMERGENCY_NUM2, "10087");
		values.put(CarProviderData.KEY_PHONENUM_EMERGENCY_TIME, 20);
		values.put(CarProviderData.KEY_PHONENUM_KAIYI_NUM, "10088");
		values.put(CarProviderData.KEY_PHONENUM_NAVI_NUM, "10089");
		values.put(CarProviderData.KEY_PHONENUM_RECUTE_NUM, "10000");
		values.put(CarProviderData.KEY_PHONENUM_RECUTE_TIME, 30);
		Cursor cursor = resolver.query(CarProviderData.URI_PHONEINFO, null, null, null, null);
		if(cursor != null && cursor.moveToNext()){
			//int row = resolver.update(CarProviderData.URI_PHONEINFO, values, null, null);
		}
		else{
			resolver.insert(CarProviderData.URI_PHONEINFO, values);
		}
	}
	
	/**
	 * 从provider获取流量信息
	 * @return
	 */
	public static boolean queryFlowInfo() {
		ContentResolver resolver = MyApp.mContext.getContentResolver();
		Cursor cursor = resolver.query(CarProviderData.URI_FLOWINFO, null, null, null, null);
		if(cursor != null && cursor.moveToNext()){
			String totalStr = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_FLOWINFO_CURRFLOWTATAL));
			String usedStr = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_FLOWINFO_USEFLOW));
			String surplusStr = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_FLOWINFO_SURPLUSFLOW));
			String remindValStr = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_FLOWINFO_REMINDVALUE));
			
			Log.i(TAG, "查询流量结果为：totalStr = " + totalStr + ", usedStr = " + usedStr 
					+ ", surplusStr = " + surplusStr + ", remindValStr = " + remindValStr);
			return true;
		}
		else{
			if (cursor == null){
				Log.i(TAG, "无查询结果为null");
			}
			Log.i(TAG, "无查询结果");
			return false;
		}
	}
	
	public static float querySurplusFlow() {
		ContentResolver resolver = MyApp.mContext.getContentResolver();
		Cursor cursor = resolver.query(CarProviderData.URI_FLOWINFO, null, null, null, null);
		if(cursor != null && cursor.moveToNext()){
			String surplusStr = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_FLOWINFO_SURPLUSFLOW));
			float surplus=0f;
			try{
				surplus=Float.parseFloat(surplusStr);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			return surplus;
		}
		else{
			return 0f;
		}
	}
	
	/**
	 * 更新流量信息到provider
	 */
	public static void updateFlowInfoProvider() {
		ContentResolver resolver = MyApp.mContext.getContentResolver();
		ContentValues values = new ContentValues();
		values.put(CarProviderData.KEY_FLOWINFO_REMINDVALUE, 100);
		Cursor cursor = resolver.query(CarProviderData.URI_FLOWINFO, null, null, null, null);
		if(cursor != null && cursor.moveToNext()){
			//int row = resolver.update(CarProviderData.URI_FLOWINFO, values, null, null);
		}
		else{
			resolver.insert(CarProviderData.URI_FLOWINFO, values);
		}
	}
	
	public static String getUrlPart(String host,String requestSource,String appId,String status)
	{
		
		ContentResolver resolver = MyApp.mContext.getContentResolver();
		Cursor cursor = resolver.query(CarProviderData.URI_CARINFO, null, null, null, null);
		if(cursor != null && cursor.moveToNext()){
			StringBuilder builder=new StringBuilder();
			CarInfo carInfo=new CarInfo();
			carInfo.setVin(cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_VIN)));
			carInfo.setSn(cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_SN)));
			carInfo.setMeid(cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_MEID)));
			carInfo.setImei(cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_IMEI)));
			carInfo.setImsi(cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_IMSI)));
			carInfo.setIccid(cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_ICCID)));
			carInfo.setToken(cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_TOKEN)));
			
			String sign = null;
			Map<String, String> params = new HashMap<String, String>();
			params.put("appkey", Saver.getAppKey());
			params.put("vin", carInfo.getVin());
			params.put("token", carInfo.getToken());
			params.put("appId",appId);
			params.put("status",status);
			try {
				sign = SignatureGenerator.generate(requestSource, params,
						Saver.getSecretKey());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(sign==null)
			{
				return null;
			}
			
			builder.append(host);
			builder.append(requestSource);
			builder.append("?appkey="+Saver.getAppKey());
			builder.append("&vin="+carInfo.getVin());
			builder.append("&sign="+sign);
			builder.append("&token="+carInfo.getToken());
			builder.append("&appId="+appId);
			builder.append("&status="+status);
			Log.i("Update",builder.toString());
			return builder.toString();
		}
		else
		{
			return null;
		}
	}
	public static String getUrlPart(String host,String requestSource)
	{
		
		ContentResolver resolver = MyApp.mContext.getContentResolver();
		Cursor cursor = resolver.query(CarProviderData.URI_CARINFO, null, null, null, null);
		if(cursor != null && cursor.moveToNext()){
			StringBuilder builder=new StringBuilder();
			CarInfo carInfo=new CarInfo();
			carInfo.setVin(cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_VIN)));
			carInfo.setSn(cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_SN)));
			carInfo.setMeid(cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_MEID)));
			carInfo.setImei(cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_IMEI)));
			carInfo.setImsi(cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_IMSI)));
			carInfo.setIccid(cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_ICCID)));
			carInfo.setToken(cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_TOKEN)));
			
			String sign = null;
			Map<String, String> params = new HashMap<String, String>();
			params.put("appkey", Saver.getAppKey());
			params.put("vin", carInfo.getVin());
			params.put("token", carInfo.getToken());
			try {
				sign = SignatureGenerator.generate(requestSource, params,
						Saver.getSecretKey());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(sign==null)
			{
				return null;
			}
			
			builder.append(host);
			builder.append(requestSource);
			builder.append("?appkey="+Saver.getAppKey());
			builder.append("&vin="+carInfo.getVin());
			builder.append("&sign="+sign);
			builder.append("&token="+carInfo.getToken());
			Log.i("Update",builder.toString());
			return builder.toString();
		}
		else
		{
			return null;
		}
	}
}
