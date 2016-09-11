/**
 * 
 */
package com.zhcar.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author YC
 * @time 2016-7-7 上午11:03:04
 * TODO:
 * 版本1升到版本2，添加了配置信息
 */
public class DBHelper extends SQLiteOpenHelper {

	private static final String TAG = "DBHelper";
	/**数据库名称*/	
	private static final String DATABASE_NAME = "carinfo.db"; 
	/**版本号*/
	private static final int DATABASE_VERSION = 2;// 数据库版本
	

	
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(TAG, "onCreate");
		createCarInfo(db);
//		setDefaultCarInfo(db);
		createPhoneNum(db);
//		setDefaultPhoneInfo(db);
		createPermission(db);
		createSids(db);
		createFlowInfo(db);
		createAccount(db);
		createConfig(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(TAG, "版本更新");
//		ContentValues carInfoValues = null;
//		if (oldVersion == 1){
//			Log.i(TAG, "oldVersion = " + oldVersion);
//			carInfoValues = getCarInfoV1(db);
//		}
//		db.execSQL("DROP TABLE IF EXISTS " + CarProviderData.CARINFO_TABLE);
//		createCarInfo(db);
//		if (carInfoValues != null){
//			Log.i(TAG, "update newVersion = " + newVersion);
//			db.insert(CarProviderData.CARINFO_TABLE, "debug", carInfoValues);
//		}
//		db.execSQL("DROP TABLE IF EXISTS " + CarProviderData.PHONENUM_TABLE);
//		createPhoneNum(db);
//		db.execSQL("DROP TABLE IF EXISTS " + CarProviderData.PERMISSION_TABLE);
//		createPermission(db);
//		db.execSQL("DROP TABLE IF EXISTS " + CarProviderData.SIDS_TABLE);
//		createSids(db);
//		db.execSQL("DROP TABLE IF EXISTS " + CarProviderData.FLOW_TABLE);
//		createFlowInfo(db);
//		db.execSQL("DROP TABLE IF EXISTS " + CarProviderData.ACCOUNT_TABLE);
//		createAccount(db);
//		db.execSQL("DROP TABLE IF EXISTS " + CarProviderData.CONFIG_TABLE);
		if (newVersion == 2){
			createConfig(db);
		}
	}
	
	private ContentValues getCarInfoV1(SQLiteDatabase db) {
		Cursor cursor = db.query(CarProviderData.CARINFO_TABLE, null, null,
				null, null, null, null);
		ContentValues values = new ContentValues();
		if (cursor != null && cursor.moveToNext()) {
			values.put(CarProviderData.KEY_CARINFO_VIN, cursor.getString(cursor
					.getColumnIndex(CarProviderData.KEY_CARINFO_VIN)));
			
			values.put(CarProviderData.KEY_CARINFO_SN, cursor.getString(cursor
					.getColumnIndex(CarProviderData.KEY_CARINFO_SN)));
			
			values.put(CarProviderData.KEY_CARINFO_MEID, cursor.getString(cursor
					.getColumnIndex(CarProviderData.KEY_CARINFO_MEID)));
			
			values.put(CarProviderData.KEY_CARINFO_ICCID, cursor.getString(cursor
					.getColumnIndex(CarProviderData.KEY_CARINFO_ICCID)));
			
			values.put(CarProviderData.KEY_CARINFO_TOKEN, cursor.getString(cursor
					.getColumnIndex(CarProviderData.KEY_CARINFO_TOKEN)));
			
			values.put(CarProviderData.KEY_CARINFO_TSP_KEY, cursor.getString(cursor
					.getColumnIndex(CarProviderData.KEY_CARINFO_TSP_KEY)));
			
			values.put(CarProviderData.KEY_CARINFO_CLD_KEY, cursor.getString(cursor
					.getColumnIndex(CarProviderData.KEY_CARINFO_CLD_KEY)));
			
			values.put(CarProviderData.KEY_CARINFO_IMSI, cursor.getString(cursor
					.getColumnIndex(CarProviderData.KEY_CARINFO_IMSI)));
			
			values.put(CarProviderData.KEY_CARINFO_IMEI, cursor.getString(cursor
					.getColumnIndex(CarProviderData.KEY_CARINFO_IMEI)));
			
			values.put(CarProviderData.KEY_CARINFO_AKEY, cursor.getString(cursor
					.getColumnIndex(CarProviderData.KEY_CARINFO_AKEY)));
			
			values.put(CarProviderData.KEY_CARINFO_USER, cursor.getString(cursor
					.getColumnIndex(CarProviderData.KEY_CARINFO_USER)));

			values.put(CarProviderData.KEY_CARINFO_PASSWORD, cursor.getString(cursor
					.getColumnIndex(CarProviderData.KEY_CARINFO_PASSWORD)));
			
			values.put(CarProviderData.KEY_CARINFO_ESN, cursor.getString(cursor
					.getColumnIndex(CarProviderData.KEY_CARINFO_ESN)));
			
			values.put(CarProviderData.KEY_CARINFO_MDN, cursor.getString(cursor
					.getColumnIndex(CarProviderData.KEY_CARINFO_MDN)));
			
			values.put(CarProviderData.KEY_CARINFO_SKEY, cursor.getString(cursor
					.getColumnIndex(CarProviderData.KEY_CARINFO_SKEY)));
			
		}

		if (cursor != null) {
			cursor.close();
			cursor = null;
		}
		return values;
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
		+ CarProviderData.KEY_CARINFO_AKEY + " text,"
		+ CarProviderData.KEY_CARINFO_USER + " text,"
		+ CarProviderData.KEY_CARINFO_PASSWORD + " text,"
		+ CarProviderData.KEY_CARINFO_ESN + " text,"
		+ CarProviderData.KEY_CARINFO_MDN + " text,"
		+ CarProviderData.KEY_CARINFO_SKEY + " text,"
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
	
	private void setDefaultPhoneInfo(SQLiteDatabase db){
		ContentValues values = new ContentValues();
		values.put(CarProviderData.KEY_PHONENUM_KAIYI_NUM, "10086");
		values.put(CarProviderData.KEY_PHONENUM_NAVI_NUM, "10086");
		values.put(CarProviderData.KEY_PHONENUM_EMERGENCY_NUM1, "10086");
		values.put(CarProviderData.KEY_PHONENUM_EMERGENCY_NUM2, "10000");
		values.put(CarProviderData.KEY_PHONENUM_EMERGENCY_TIME, 20);
		values.put(CarProviderData.KEY_PHONENUM_RECUTE_NUM, "10086");
		values.put(CarProviderData.KEY_PHONENUM_RECUTE_TIME, 20);
		db.insert(CarProviderData.PHONENUM_TABLE, "debug", values);
	}
	
	private void createPermission(SQLiteDatabase db) {
		db.execSQL("create table if not exists " + CarProviderData.PERMISSION_TABLE +"(_id integer primary key autoincrement, " 
		+ CarProviderData.KEY_PERMISSION_SID + " text,"
		+ CarProviderData.KEY_PERMISSION_PID + " text,"
		+ CarProviderData.KEY_PERMISSION_ABLE + " text)");
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
	
	private void createConfig(SQLiteDatabase db){
		db.execSQL("create table if not exists " + CarProviderData.CONFIG_TABLE +"(_id integer primary key autoincrement, " 
				+ CarProviderData.KEY_CONFIG_ENVIRONMENTS + " integer)");
	}
}
