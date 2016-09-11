package com.example.tt;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.semisky.outer.newinterface.PhoneUtil;
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
		Log.i(TAG, "openZuiApp");
//		openZuiApp(this, SO_MCUUPGRADE, DATA_MCUUPGRADE, "/mnt/USB1/zh_arm_mcu.bin");
		Log.i(TAG, "tst = " + Integer.valueOf(9965)/100);
	}
	
	@Override
	protected void onDestroy() {
		//解绑aidl服务
		ConnExternal.getInstance().unbindService();
		super.onDestroy();
	}
	

	private boolean bPermission = false;
	private boolean bCarRun = false;
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
		case R.id.dial:
			PhoneUtil.getInstance().dial("15820201347");
			break;
		case R.id.hangup:
			PhoneUtil.getInstance().hangUp();
			break;
		case R.id.upgrade_can:
			openZuiApp(this, SO_CANUPGRADE, DATA_CANUPGRADE, "A:/mnt/USB1/autolink_can_mcu.bin");
			break;
		case R.id.upgrade_iar:
			openZuiApp(this, SO_AIRUPGRADE, DATA_AIRUPGRADE, "A:/mnt/USB1/autolink_air_mcu.bin");
			break;
		case R.id.test_regex:
			testRegex();
			break;
		case R.id.sendpermissionbroadcast:
			sendBroadcast("com.tima.Permission", "status", "succeed");
			break;
		case R.id.sendcarrunbroadcast:
			bCarRun = !bCarRun;
			sendBroadcast("com.zhonghong.zuiserver.BROADCAST", "CARRUN_INFO", bCarRun ? "1":"0");
			break;
		}
	}
	
	private void sendBroadcast(String action, String key, String val){
		Intent intent = new Intent();
		intent.setAction(action);
		intent.putExtra(key, val);
		sendBroadcast(intent);
	}
	
	private void testRegex(){
		Log.i(TAG, "parseInt 05 = " + Integer.parseInt("090"));
		String versionA = "HE-2016-08-31-V2.03-51";
		String versionB = "HE-2016-08-31-V2.04-51";
//		Pattern pattern = Pattern.compile("(\\d+\\.)+\\d+");
		Pattern pattern = Pattern.compile("([0-9]+\\.)+[0-9]+");
		String versionCodeA = "";
		String versionCodeB = "";
		Matcher matcher = pattern.matcher(versionA);
		if (matcher.find()){
			versionCodeA = matcher.group();
			Log.i(TAG, "find versionCodeA = " + versionCodeA);
		}
		matcher = pattern.matcher(versionB);
		if (matcher.find()){
			versionCodeB = matcher.group();
			Log.i(TAG, "find versionCodeB = " + versionCodeB);
		}
		if (isVersonNew(versionCodeA, versionCodeB)){
			Log.i(TAG, "isVersonNew true");
		}
		else{
			Log.i(TAG, "isVersonNew false");
		}
	}
	
	private boolean isVersonNew(String oldVersion, String newVersion){
		if (TextUtils.isEmpty(oldVersion) || TextUtils.isEmpty(newVersion)){
			return false;
		}
		String[] piecesOld=oldVersion.split("\\.");
		String[] piecesNew=newVersion.split("\\.");
		for(int i=0;i<piecesOld.length&&i<piecesNew.length;i++)
		{
			if(Integer.parseInt(piecesNew[i])>Integer.parseInt(piecesOld[i]))
			{
				return true;
			}
		}
		return false;
	}
	
	private final String SO_CANUPGRADE = "libCanUpgrade.so";
	private final String DATA_CANUPGRADE = "ZH_PUBLIC_APPCANUPGRADE_10";
	
	private final String SO_AIRUPGRADE = "libAirUpgrade.so";
	private final String DATA_AIRUPGRADE = "ZH_PUBLIC_APPAIRUPGRADE_10";
	
	private final String SO_8836UPGRADE = "libAppUpdate8836.so";
	private final String DATA_8836UPGRADE = "ZH_PUBLIC_APPCANUPGRADE_10";
	
	private final String SO_MCUUPGRADE = "libAppUpdateMcu.so";
	private final String DATA_MCUUPGRADE = "ZH_PUBLIC_APPUPDATEMCU_10";
	
	/**
	 * cmd格式为“A:filePath”
	 */
	private void openZuiApp(Context context, String soName, String data,String cmd) {
			Intent intent = new Intent("widget_entrance");
			intent.setComponent(new ComponentName("com.example.zuiserver", "com.example.zuiserver.StartActivity"));
			intent.putExtra("et1", data);
			intent.putExtra("et2", soName);
			intent.putExtra("et3", cmd);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			context.startActivity(intent);
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
	
	private String queryVIN() {
		Uri uri = Uri.parse("content://cn.com.semisky.carProvider/carInfo");
		ContentResolver resolver = getContentResolver();
		Cursor cursor = resolver.query(uri, null, null, null, null);
		String vin = null;
		if(cursor != null && cursor.moveToNext()){
			vin = cursor.getString(cursor.getColumnIndex("vin"));
		}
		if (cursor != null){
			cursor.close();
			cursor = null;
		}
		return vin;
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
		values.put(CarProviderData.KEY_PHONENUM_EMERGENCY_NUM1, "15820201347");
		values.put(CarProviderData.KEY_PHONENUM_EMERGENCY_NUM2, "17180591430");
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
