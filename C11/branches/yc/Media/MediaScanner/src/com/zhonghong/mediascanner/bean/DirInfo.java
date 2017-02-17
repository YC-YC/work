/**
 * 
 */
package com.zhonghong.mediascanner.bean;

import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import com.zhonghong.mediascanerlib.config.Config;

/**
 * @author YC
 * @time 2016-12-15 下午2:31:40
 * TODO:记录目录下的文件信息
 */
public class DirInfo{
	
	private static final String TAG = "DirInfo";
	/**文件夹的相对路径*/
	private String name;
	/**修改时间*/
	private long modifiled = 0L;
	/**目录下总文件大小*/
	private long size = 0L;
	/**文件的hashCode*/
	private long hashCode = 0L;

	
	private ArrayList<Integer> mAudioInfoPos = new ArrayList<Integer>();
	private ArrayList<Integer> mVideoInfoPos = new ArrayList<Integer>();
	private ArrayList<Integer> mImageInfoPos = new ArrayList<Integer>();
	private ArrayList<Integer> mOtherInfoPos = new ArrayList<Integer>();
	
	public DirInfo(String dirName, long modifiled, long size, long hashCode){
		this.name = dirName;
		this.modifiled = modifiled;
		this.size = size;
		this.hashCode = hashCode;
	}
	
	
	public String getName() {
		return name;
	}

	public long getModifiled() {
		return modifiled;
	}

	public long getSize() {
		return size;
	}

	public long getHashCode() {
		return hashCode;
	}


	public ArrayList<Integer> getAudioInfos() {
		return mAudioInfoPos;
	}
	
	public ArrayList<Integer> getVideoInfos() {
		return mVideoInfoPos;
	}

	public ArrayList<Integer> getImageInfos() {
		return mImageInfoPos;
	}

	public ArrayList<Integer> getOtherInfos() {
		return mOtherInfoPos;
	}
	
	public void putAudioInfo(Integer pos){
		mAudioInfoPos.add(pos);
	}
	
	public void putVideoInfo(Integer pos){
		mVideoInfoPos.add(pos);
	}
	
	public void putImageInfo(Integer pos){
		mImageInfoPos.add(pos);
	}
	
	public void putOtherInfo(Integer pos){
		mOtherInfoPos.add(pos);
	}

	
	
	public static String getDBTableName() {
		return Config.DIR_DB_TABLE;
	}
	public static String getSaveSQL(){
		return ("create table if not exists " +getDBTableName() +"(_id integer primary key autoincrement, " 
				+ "name" + " text,"
				+ "modifiled" + " long,"
				+ "size" + " long,"
				+ "hashCode" + " long)");
	}
	
	public static String getInsertSQL(){
		return ("insert into " + getDBTableName() +"(name,modifiled,size,hashCode) values(?,?,?,?)");
	}

	public void bindSQLiteStatement(SQLiteStatement state){
		state.bindString(1, getName());
		state.bindLong(2, getModifiled());
		state.bindLong(3, getSize());
		state.bindLong(4, getHashCode());
		state.executeInsert();
	}
	
	public static DirInfo getDirInfoFromDBCursor(Cursor cursor){
		String name = cursor.getString(cursor.getColumnIndex("name"));
		long modifiled = cursor.getLong(cursor.getColumnIndex("modifiled"));
		long size = cursor.getLong(cursor.getColumnIndex("size"));
		long hashCode = cursor.getLong(cursor.getColumnIndex("hashCode"));
		return new DirInfo(name, modifiled, size, hashCode);
		
	}

	@Override
	public String toString() {
		return "DirInfo [name=" + name + ", modifiled=" + modifiled + ", size="
				+ size + ", hashCode=" + hashCode + "]";
	}
	
}
