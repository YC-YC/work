/**
 * 
 */
package com.zhcar.provider;

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
	private static final int DATABASE_VERSION = 2;// 数据库版本
	

	
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(TAG, "创建数据库");
//		"create table if not exists " + "person" +"(_id integer primary key autoincrement, vin text, sn text)"
		createCarInfo(db);
		createPhoneNum(db);
		createPermission(db);
	}

	private void createCarInfo(SQLiteDatabase db) {
		db.execSQL("create table if not exists " + CarProviderData.CARINFO_TABLE +"(_id integer primary key autoincrement, " 
		+ CarProviderData.KEY_VIN + " text,"
		+ CarProviderData.KEY_SN + " text,"
		+ CarProviderData.KEY_IMEI + " text,"
		+ CarProviderData.KEY_ICCID + " text,"
		+ CarProviderData.KEY_TOKEN + " text)");
	}
	
	private void createPhoneNum(SQLiteDatabase db) {
		db.execSQL("create table if not exists " + CarProviderData.PHONENUM_TABLE +"(_id integer primary key autoincrement, " 
		+ CarProviderData.KEY_SHOTCUT_RECUTE_NUM + " text,"
		+ CarProviderData.KEY_SHOTCUT_RECUTE_TIME + " integer,"
		+ CarProviderData.KEY_SHOTCUT_NAVI_NUM + " text,"
		+ CarProviderData.KEY_SHOTCUT_EMERGENCY_NUM1 + " text,"
		+ CarProviderData.KEY_SHOTCUT_EMERGENCY_NUM2 + " text,"
		+ CarProviderData.KEY_SHOTCUT_EMERGENCY_TIME + " integer,"
		+ CarProviderData.KEY_SHOTCUT_SERVICE_NUM + " text)");
	}
	
	private void createPermission(SQLiteDatabase db) {
		db.execSQL("create table if not exists " + CarProviderData.PERMISSION_TABLE +"(_id integer primary key autoincrement, " 
		+ CarProviderData.KEY_PERMISSION + " integer)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(TAG, "版本更新,直接删除");
		db.execSQL("DROP TABLE IF EXISTS " + CarProviderData.CARINFO_TABLE);
		createCarInfo(db);
		db.execSQL("DROP TABLE IF EXISTS " + CarProviderData.PHONENUM_TABLE);
		createPhoneNum(db);
		db.execSQL("DROP TABLE IF EXISTS " + CarProviderData.PERMISSION_TABLE);
		createPermission(db);
	}

}
