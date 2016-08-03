/**
 * 
 */
package com.zhcar.provider;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.os.Environment;
import android.util.Log;

/**
 * @author YC
 * @time 2016-7-7 下午9:54:53
 * TODO:用于修改DB文件路径
 * SQLiteOpenHelper通过调用openOrCreateDatabase获取路径，重写该方法即可修改路径
 */
public class DBContext extends ContextWrapper {

	private static final String TAG = "DBContext";
	private static final String DIR_PATH = "/ResidentFlash/zhdatabase";

	/**
	 * @param base
	 */
	public DBContext(Context base) {
		super(base);
	}
	
	/**
	 * 主要重写该方法获取指定路径
	 */
	@Override
	public File getDatabasePath(String name) {
//		boolean sdExit = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
//		if (!sdExit){
//			return null;
//		}
		
//		String dbDir = Environment.getExternalStorageDirectory().toString();
//		dbDir += (File.separator + "zhdatabase");\
		
		String dbPath = DIR_PATH + (File.separator + name);
//		Log.i(TAG, "new dbPath = " + dbPath);
		File dirFile = new File(DIR_PATH);
		if (!dirFile.exists()){
			dirFile.mkdirs();
		}
		
		File dbFile = new File(dbPath);
		boolean isFileExit = false;
		if (!dbFile.exists()){
			try {
				if (dbFile.createNewFile()){
					isFileExit = true;
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else{
			isFileExit = true;
		}
		
		if (isFileExit){
			return dbFile;
		}
		else{
			return null;
		}
	}
	
	/**
	 * android 2.3及以下会调用这个方法
	 */
	@Override
	public SQLiteDatabase openOrCreateDatabase(String name, int mode,
			CursorFactory factory) {
		return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
	}
	
	/**
	 *  Android 4.0会调用此方法获取数据库
	 */
	@Override
	public SQLiteDatabase openOrCreateDatabase(String name, int mode,
			CursorFactory factory, DatabaseErrorHandler errorHandler) {
		return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
	}
	

}
