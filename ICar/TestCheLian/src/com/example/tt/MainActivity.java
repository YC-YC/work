package com.example.tt;

import java.util.HashMap;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.yc.conn.ConnExternal;
import com.yc.external.Cmd;
import com.yc.external.JsonHelper;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";

	private TextView mContent;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		
		//绑定aidl服务
		ConnExternal.getInstance().bindService(this);
	}
	
	@Override
	protected void onDestroy() {
		//解绑aidl服务
		ConnExternal.getInstance().unbindService();
		super.onDestroy();
	}
	

	public void doClick(View view) {
		switch (view.getId()) {
		case R.id.queryflowinfo:
			queryFlowInfo();
			break;
		case R.id.updateflowinfo:
			updateFlowInfoProvider();
			break;
		case R.id.queryphoneinfo:
			queryPhoneInfo();
			break;
		case R.id.updatephoneinfo:
			updatePhoneInfo();
			break;
		case R.id.querycarinfo:
			queryCarinfo();
			break;
		case R.id.updatecarinfo:
			updateCarInfo();
			break;
		case R.id.postappinfo:
			enterApp();
			postAppProgram();
			postAppProgramAuth();
			exitApp();
			break;
		case R.id.opencheck:
			Settings.System.putString(getContentResolver(), "cartype", "mc22");
			startOtherActivity(this, "com.zhcar", "com.zhcar.check.CheckActivity");
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					Intent intent = new Intent("com.zhonghong.zuiserver.BROADCAST");
					intent.putExtra("score", "85");
					MainActivity.this.sendBroadcast(intent);
				}
			}, 10*1000);
			break;
		}
	}
	
	/** 打开应用方法 */
	private boolean startOtherActivity(Context context, String pkgName, String className){
		try {
			Intent it = new Intent(Intent.ACTION_MAIN); 
			ComponentName cn = new ComponentName(pkgName, className);              
			it.setComponent(cn);  
			it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK/* | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED*/);
			context.startActivity(it);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private void queryCarinfo() {
		ContentResolver resolver = getContentResolver();
		Cursor cursor = resolver.query(CarProviderData.URI_CARINFO, null, null, null, null);
		if(cursor != null && cursor.moveToNext()){
			String vin = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_VIN));
			String sn = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_SN));
			String meid = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_MEID));
			String imei = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_IMEI));
			String imsi = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_IMSI));
			String iccid = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_ICCID));
			String token = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_TOKEN));
			
			Log.i(TAG, "查询carinfo结果为：vin = " + vin + ", sn = " + sn 
					+ ", imei = " + meid + ", iccid = " + iccid + ", token = " + token);
			}
		else{
			if (cursor == null){
				Log.i(TAG, "无查询结果为null");
			}
			Log.i(TAG, "无查询结果");
		}
	}
	
	/**
	 * 更新车辆信息到provider
	 */
	private void updateCarInfo() {
		ContentResolver resolver = getContentResolver();
		ContentValues values = new ContentValues();
		//可以只选其中一个
		values.put(CarProviderData.KEY_CARINFO_VIN, "LVTDB11B1TS000004");
		values.put(CarProviderData.KEY_CARINFO_ICCID, "8986031690021000151");
		values.put(CarProviderData.KEY_CARINFO_SN, "CL6230004TS0");
		values.put(CarProviderData.KEY_CARINFO_MEID, "A100001FA0D7DA");
		values.put(CarProviderData.KEY_CARINFO_IMEI, "A100001FA0D7DA");
		
		Cursor cursor = resolver.query(CarProviderData.URI_CARINFO, null, null, null, null);
		if(cursor != null && cursor.moveToNext()){
			int row = resolver.update(CarProviderData.URI_CARINFO, values, null, null);
		}
		else{
			resolver.insert(CarProviderData.URI_CARINFO, values);
		}
	}
	
	
	private void queryPhoneInfo() {
		ContentResolver resolver = getContentResolver();
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
	private void updatePhoneInfo() {
		ContentResolver resolver = getContentResolver();
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
			int row = resolver.update(CarProviderData.URI_PHONEINFO, values, null, null);
		}
		else{
			resolver.insert(CarProviderData.URI_PHONEINFO, values);
		}
	}
	
	/**
	 * 从provider获取流量信息
	 * @return
	 */
	private boolean queryFlowInfo() {
		ContentResolver resolver = getContentResolver();
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
	
	/**
	 * 更新流量信息到provider
	 */
	private void updateFlowInfoProvider() {
		ContentResolver resolver = getContentResolver();
		ContentValues values = new ContentValues();
		values.put(CarProviderData.KEY_FLOWINFO_REMINDVALUE, 100);
		Cursor cursor = resolver.query(CarProviderData.URI_FLOWINFO, null, null, null, null);
		if(cursor != null && cursor.moveToNext()){
			int row = resolver.update(CarProviderData.URI_FLOWINFO, values, null, null);
		}
		else{
			resolver.insert(CarProviderData.URI_FLOWINFO, values);
		}
	}
	

	private void enterApp(){
		HashMap<String, String> maps = new HashMap<String, String>();
		maps.put(Cmd.POST_RECORD_KEY_APPNAME, "appRadio");
		ConnExternal.getInstance().postInfo(Cmd.POST_RECORD_ENTER_APP, new JsonHelper().map2Json(maps));
	}
	
	private void exitApp(){
		HashMap<String, String> maps = new HashMap<String, String>();
		maps.put(Cmd.POST_RECORD_KEY_APPNAME, "appRadio");
		ConnExternal.getInstance().postInfo(Cmd.POST_RECORD_EXIT_APP, new JsonHelper().map2Json(maps));
	}

	private void postAppProgram(){
		HashMap<String, String> maps = new HashMap<String, String>();
		//需要同时将应用名发送过去
		maps.put(Cmd.POST_RECORD_KEY_APPNAME, "appRadio");
		maps.put(Cmd.POST_RECORD_KEY_APPPROGRAM, "城市之音");
		ConnExternal.getInstance().postInfo(Cmd.POST_RECORD_APPPROGRAM, new JsonHelper().map2Json(maps));
	}
	
	private void postAppProgramAuth(){
		HashMap<String, String> maps = new HashMap<String, String>();
		maps.put(Cmd.POST_RECORD_KEY_APPNAME, "appRadio");
		maps.put(Cmd.POST_RECORD_KEY_APPPROGRAMAUTH, "张三");
		ConnExternal.getInstance().postInfo(Cmd.POST_RECORD_APPPROGRAMAUTH, new JsonHelper().map2Json(maps));
	}
	
}
