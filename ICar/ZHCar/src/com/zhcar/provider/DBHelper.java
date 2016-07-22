/**
 * 
 */
package com.zhcar.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author YC
 * @time 2016-7-7 上午11:03:04
 * TODO:
 */
public class DBHelper extends SQLiteOpenHelper {

	private static final String TAG = "DBHelper";
	/**数据库名称*/	
	private static final String DATABASE_NAME = "carinfo.db"; 
	/**版本号*/
	private static final int DATABASE_VERSION = 1;// 数据库版本
	

	
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(TAG, "创建数据库");
//		"create table if not exists " + "person" +"(_id integer primary key autoincrement, vin text, sn text)"
		createCarInfo(db);
		setDefaultCarInfo(db);
		createPhoneNum(db);
		setDefaultPhoneNum(db);
		createPermission(db);
		setDefaultPermission(db);
		createSids(db);
		createFlowInfo(db);
		createAccount(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(TAG, "版本更新,直接删除");
		db.execSQL("DROP TABLE IF EXISTS " + CarProviderData.CARINFO_TABLE);
		createCarInfo(db);
		setDefaultCarInfo(db);
		db.execSQL("DROP TABLE IF EXISTS " + CarProviderData.PHONENUM_TABLE);
		createPhoneNum(db);
		setDefaultPhoneNum(db);
		db.execSQL("DROP TABLE IF EXISTS " + CarProviderData.PERMISSION_TABLE);
		createPermission(db);
		setDefaultPermission(db);
		createSids(db);
		createFlowInfo(db);
		createAccount(db);
	}
	
	private void createCarInfo(SQLiteDatabase db) {
		db.execSQL("create table if not exists " + CarProviderData.CARINFO_TABLE +"(_id integer primary key autoincrement, " 
		+ CarProviderData.KEY_CARINFO_VIN + " text,"
		+ CarProviderData.KEY_CARINFO_SN + " text,"
		+ CarProviderData.KEY_CARINFO_MEID + " text,"
		+ CarProviderData.KEY_CARINFO_ICCID + " text,"
		+ CarProviderData.KEY_CARINFO_TSP_KEY + " text,"
		+ CarProviderData.KEY_CARINFO_CLD_KEY + " text,"
		+ CarProviderData.KEY_CARINFO_IMEI + " text,"
		+ CarProviderData.KEY_CARINFO_IMSI + " text,"
		+ CarProviderData.KEY_CARINFO_TOKEN + " text)");
	}
	
	private void setDefaultCarInfo(SQLiteDatabase db){
		ContentValues values = new ContentValues();
		values.put(CarProviderData.KEY_CARINFO_VIN, "LVTDB11B1TS000004");
		values.put(CarProviderData.KEY_CARINFO_ICCID, "8986031690021000151");
		values.put(CarProviderData.KEY_CARINFO_SN, "CL6230004TS0");
		values.put(CarProviderData.KEY_CARINFO_MEID, "A100001FA0D7DA");
		values.put(CarProviderData.KEY_CARINFO_TOKEN, "");
		db.insert(CarProviderData.CARINFO_TABLE, "debug", values);
	}
	
	private void createPhoneNum(SQLiteDatabase db) {
		db.execSQL("create table if not exists " + CarProviderData.PHONENUM_TABLE +"(_id integer primary key autoincrement, " 
		+ CarProviderData.KEY_PHONENUM_RECUTE_NUM + " text,"
		+ CarProviderData.KEY_PHONENUM_RECUTE_TIME + " integer,"
		+ CarProviderData.KEY_PHONENUM_NAVI_NUM + " text,"
		+ CarProviderData.KEY_PHONENUM_EMERGENCY_NUM1 + " text,"
		+ CarProviderData.KEY_PHONENUM_EMERGENCY_NUM2 + " text,"
		+ CarProviderData.KEY_PHONENUM_EMERGENCY_TIME + " integer,"
		+ CarProviderData.KEY_PHONENUM_KAIYI_NUM + " text)");
	}
	
	private void setDefaultPhoneNum(SQLiteDatabase db){
		
	}
	
	private void createPermission(SQLiteDatabase db) {
		db.execSQL("create table if not exists " + CarProviderData.PERMISSION_TABLE +"(_id integer primary key autoincrement, " 
		+ CarProviderData.KEY_PERMISSION_SID + " text,"
		+ CarProviderData.KEY_PERMISSION_PID + " text,"
		+ CarProviderData.KEY_PERMISSION_ABLE + " text)");
	}
	
	private void setDefaultPermission(SQLiteDatabase db){
		
	}
	
	private void createSids(SQLiteDatabase db) {
		db.execSQL("create table if not exists " + CarProviderData.SIDS_TABLE +"(_id integer primary key autoincrement, " 
		+ CarProviderData.KEY_SID + " text,"
		+ CarProviderData.KEY_SID_EXPIRED + " integer)");
	}
	
	private void createFlowInfo(SQLiteDatabase db) {
		db.execSQL("create table if not exists " + CarProviderData.FLOW_TABLE +"(_id integer primary key autoincrement, " 
		+ CarProviderData.KEY_FLOWINFO_REMINDVALUE + " text,"
		+ CarProviderData.KEY_FLOWINFO_PHONENUM + " text,"
		+ CarProviderData.KEY_FLOWINFO_REMIDE + " text,"
		+ CarProviderData.KEY_FLOWINFO_USEFLOW + " text,"
		+ CarProviderData.KEY_FLOWINFO_SURPLUSFLOW + " text,"
		+ CarProviderData.KEY_FLOWINFO_CURRFLOWTATAL + " text)");
	}

	private void createAccount(SQLiteDatabase db){
		db.execSQL("create table if not exists " + CarProviderData.ACCOUNT_TABLE +"(_id integer primary key autoincrement, " 
				+ CarProviderData.KEY_ACCOUNT_STATUS + " text,"
				+ CarProviderData.KEY_ACCOUNT_UID + " integer,"
				+ CarProviderData.KEY_ACCOUNT_AID + " integer,"
				+ CarProviderData.KEY_ACCOUNT_MOBILE + " text,"
				+ CarProviderData.KEY_ACCOUNT_IDNUMBER + " text)");
	}
}
