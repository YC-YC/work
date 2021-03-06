/**
 * 
 */
package com.zhonghong.mediascanerlib.filebean;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import com.zhonghong.mediascanerlib.config.Config;

/**
 * @author YC
 * @time 2016-12-9 下午4:27:03
 * TODO:
 */
public class ImageInfo extends FileInfo {

	public ImageInfo(String path, String name, long modifiled, long size) {
		super(path, name, modifiled, size);
	}

	public static String getDBTableName() {
		return Config.IMAGE_DB_TABLE;
	}
	public static String getSaveSQL(){
		return ("create table if not exists " + getDBTableName() +"(_id integer primary key autoincrement, " 
				+ "path" + " text,"
				+ "name" + " text,"
				+ "modifiled" + " long,"
				+ "size" + " long)");
	}
	
	public static String getInsertSQL(){
		return ("insert into " + getDBTableName() +"(path,name,modifiled,size) values(?,?,?,?)");
	}

	public void bindSQLiteStatement(SQLiteStatement state){
		state.bindString(1, getPath());
		state.bindString(2, getName());
		state.bindLong(3, getModifiled());
		state.bindLong(4, getSize());
		state.executeInsert();
	}
	
	public static ImageInfo getImageInfoFromDBCursor(Cursor cursor){
		String path = cursor.getString(cursor.getColumnIndex("path"));
		String name = cursor.getString(cursor.getColumnIndex("name"));
		long modifiled = cursor.getLong(cursor.getColumnIndex("modifiled"));
		long size = cursor.getLong(cursor.getColumnIndex("size"));
		return new ImageInfo(path, name, modifiled, size);
	}

}
