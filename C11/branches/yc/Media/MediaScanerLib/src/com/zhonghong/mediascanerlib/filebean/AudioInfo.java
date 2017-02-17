/**
 * 
 */
package com.zhonghong.mediascanerlib.filebean;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import com.zhonghong.mediascanerlib.config.Config;

/**
 * @author YC
 * @time 2016-12-9 下午3:08:45
 * TODO:
 */
public class AudioInfo extends FileInfo {
	
	private String title;
	private String artist;
	private String album;

	public AudioInfo(String path, String name, long modifiled, long size,
			String title, String artist, String album) {
		super(path, name, modifiled, size);
		this.title = title;
		this.artist = artist;
		this.album = album;
	}
	

	public String getTitle() {
		return title;
	}

	public String getArtist() {
		return artist;
	}

	public String getAlbum() {
		return album;
	}

	public static String getDBTableName() {
		return Config.MUSIC_DB_TABLE;
	}
	public static String getSaveSQL(){
		return ("create table if not exists " + Config.MUSIC_DB_TABLE +"(_id integer primary key autoincrement, " 
				+ "path" + " text,"
				+ "name" + " text,"
				+ "modifiled" + " long,"
				+ "size" + " long,"
				+ "title" + " text,"
				+ "artist" + " text,"
				+ "album" + " text)");
	}
	
	public static String getInsertSQL(){
		return ("insert into " + Config.MUSIC_DB_TABLE +"(path,name,modifiled,size,title,artist,album) values(?,?,?,?,?,?,?)");
	}

	public void bindSQLiteStatement(SQLiteStatement state){
		state.bindString(1, getPath());
		state.bindString(2, getName());
		state.bindLong(3, getModifiled());
		state.bindLong(4, getSize());
		state.bindString(5, getTitle());
		state.bindString(6, getArtist());
		state.bindString(7, getAlbum());
		state.executeInsert();
	}
	
	public static AudioInfo getAudioInfoFromDBCursor(Cursor cursor){
		String path = cursor.getString(cursor.getColumnIndex("path"));
		String name = cursor.getString(cursor.getColumnIndex("name"));
		long modifiled = cursor.getLong(cursor.getColumnIndex("modifiled"));
		long size = cursor.getLong(cursor.getColumnIndex("size"));
		String title = cursor.getString(cursor.getColumnIndex("title"));
		String artist = cursor.getString(cursor.getColumnIndex("artist"));
		String album = cursor.getString(cursor.getColumnIndex("album"));
		return new AudioInfo(path, name, modifiled, size, title, artist, album);
		
	}
	
	@Override
	public String toString() {
		return super.toString() + "title=" + title + ", artist=" + artist + ", album="
				+ album + "]";
	}

	
}
