/**
 * 
 */
package com.zhcl.media;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.provider.MediaStore;
import android.util.Log;

import com.zh.dao.SongInfo;
import com.zh.uitls.L;
import com.zh.uitls.Utils;
import com.zhcl.filescanner.FileInfo;

/**
 * Song_table
 * @author chenli
 *
 */
public class SongDBHelp extends SQLiteOpenHelper{
	private static final String tag = "SongDBHelp";
	  public static String c = "time DESC";
	  Context context;
	  
	public SongDBHelp(Context context) {
		super(context, "recogniz.db", null, 6);
		L.i(tag, "create SongDBHelp instance");
		this.context = context; 
	}
//--------------------------------- 
	private SQLiteDatabase db;
	String dirDBPath ;
	public SongDBHelp(Context context, String dbPath){
		super(context, "recogniz.db", null, 6);
		this.context = context;
		dirDBPath = dbPath;//"/mnt/sdcard/zhmusic" + File.separator + "zhclmusic.db";
		findDB(); 
	}
	
	/**
	 * 查找数据库
	 */
	private void findDB() {
		L.w(tag, "initDirDatabase");
		boolean bool = isDbExist(false);
		L.i(tag, "isDbExist: " + bool);
		if (!bool) {
			createParentFile(new File(dirDBPath).getParent());
			if (this.db != null) {		
				this.db.close();
				this.db = null;
			}
		}
		openDB();
	}

	/**
	 * 清除是否存在
	 * @param isDelete 是否清除旧有数据库
	 * @return
	 */
	private boolean isDbExist(boolean isDelete) {
		File file = new File(this.dirDBPath);
		L.d(tag, "mDatabasePath : " + this.dirDBPath);
		if (!file.exists()){
			return false;
		}
		if (isDelete) {
			file.delete();
			return false;
		}
		return true;
	}
	
	/**
	 * 创建数据库所在文件夹
	 * @param paramString
	 * @return
	 */
	public static boolean createParentFile(String paramString) {
		File parent = new File(paramString);
		if (parent.exists()) {
			return true;
		}
		parent.mkdirs();
		File nomediaFile = new File(paramString + ".nomedia"); // 告知没有媒体
		try {
			nomediaFile.createNewFile();
			return true;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return true;
	}
	
	
	/**
	 * 打开数据库
	 * 创建表
	 */
	private boolean openDB() {
		L.w(tag, "initializeDB");
		if (this.db == null) {
			try {
				this.db = SQLiteDatabase.openOrCreateDatabase(this.dirDBPath, null);
				if (this.db == null) {
					L.e(tag, "数据库打开失败：" + this.dirDBPath);
					return false;
				}
			} catch (SQLiteException localSQLiteException1) {
				L.e(tag, "initializeDB openOrCreateDatabase ERROR :" + this.dirDBPath);
			}
		}
		try {
			int Vierson = this.db.getVersion();
			if ((Vierson != 0) && (Vierson != 10)) {
				L.e(tag, "数据库版本不对，清除表");
				//清除表格
				this.db.execSQL("DROP TABLE IF EXISTS history");
				this.db.execSQL("DROP TABLE IF EXISTS radio_table");
			}
			this.db.setVersion(10);
			//不存在则创建
//			this.db.execSQL("CREATE TABLE IF NOT EXISTS history ( _id INTEGER PRIMARY KEY AUTOINCREMENT , time INTEGER , songid INTEGER , type INTEGER , singerid INTEGER , albumid INTEGER , duration INTEGER , size1 INTEGER , size2 INTEGER , soso INTEGER , exint1 INTEGER , exint2 INTEGER , exint3 INTEGER , exint4 INTEGER , name TEXT NOT NULL , singer TEXT NOT NULL , album TEXT NOT NULL , albumurl TEXT NOT NULL , wap24 TEXT NOT NULL , wap48 TEXT NOT NULL , wap96 TEXT NOT NULL , wap128 TEXT NOT NULL , wifiurl TEXT NOT NULL , extext1 TEXT NOT NULL , extext2 TEXT NOT NULL , extext3 TEXT NOT NULL , extext4 TEXT NOT NULL,switch integer,pay_month integer,pay_price integer,pay_album integer,pay_album_price integer,try_size integer,try_begin integer,try_end integer,alert integer,quality integer,pay_play integer,pay_download integer,pay_status integer );");
//			this.db.execSQL("create table if not exists radio_table(_id INTEGER PRIMARY KEY AUTOINCREMENT, id INTEGER NOT NULL, type INTEGER, name TEXT, sub_num INTEGER, num_str TEXT, is_vip INTEGER, not_del INTEGER, img_url TEXT, small_url TEXT, jump_url TEXT, group_id INTEGER, uin LONG NOT NULL, duration LONG, date INTEGER, time LONG);");
			//两张表暂时做成一样
			this.db.execSQL("create table if not exists Song_table (id long not null, type integer not null, fid long not null, path text not null, pinyin text not null, name text not null, singername text, albumname text, PRIMARY KEY (id,type));");
			this.db.execSQL("create table if not exists Collection_table (id long not null, type integer not null, fid long not null, path text not null, pinyin text not null, name text not null, singername text, albumname text, PRIMARY KEY (id,type));");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 数据库是否可用
	 */
	private boolean isDBEnable() {
		return this.db != null;
	}
	
	/**
	 * 插入歌曲信息
	 * @param paramCursor
	 * @return
	 */
	public boolean insertSongTable(ArrayList<SongInfo> songInfoList){
		if ((songInfoList == null) || (songInfoList.size() == 0) || !isDBEnable()){
			return false;
		}
		L.i(tag, "添加songInfo个数：" + songInfoList.size());
		String currentPath = null;
		try 
	    {
	      this.db.beginTransaction();
	      ContentValues localContentValues = new ContentValues();
	      for(SongInfo songInfo : songInfoList){ 
	    	  localContentValues.clear(); 
	          localContentValues.put("id", songInfo.getId());
	          localContentValues.put("type", songInfo.getType());
	          localContentValues.put("fid", SongInfo.makeKey(songInfo.getId(), songInfo.getType()));
	          localContentValues.put("path", songInfo.getFileName());
	          localContentValues.put("name", songInfo.getTitle());
	          localContentValues.put("singername", songInfo.getSinger());
	          localContentValues.put("albumname", songInfo.getAlbum());
	          localContentValues.put("pinyin", songInfo.getPinyin());
	          currentPath = songInfo.getFileName();
	          this.db.insert("Song_table", null, localContentValues);
	      } 
	      this.db.setTransactionSuccessful();
	    }catch(Exception e){
	    	L.e(tag, "error currentPath = " + currentPath);
	    	e.printStackTrace();
			return false;
	    }  
	    finally 
	    {
	    	try{
	    	 this.db.endTransaction();
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
	    }
		return true;
	}
	
	/**
	 * 查询所有song
	 * @return
	 */
	public Cursor queryAllSong(){
		Cursor resultCursor = null;
		if (!isDBEnable()) {
			return resultCursor;
		}
		try {
			resultCursor = this.db.query("Song_table", null, null, null, null, null, "pinyin asc"); //升序asc 降序 desc
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultCursor;
	}
	
	
	/**
	 * 查询所有歌手
	 */
	public Cursor queryAllSinger(){
		Cursor resultCursor = null;
		if (!isDBEnable()) {
			return resultCursor;
		}
		try {
			resultCursor = this.db.query("Song_table", new String[]{"distinct singername"}, null, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultCursor;
	}
	
	
	/**
	 * 查询所有专辑信息
	 */
	public Cursor queryAllAlbumr(){
		Cursor resultCursor = null;
		if (!isDBEnable()) {
			return resultCursor;
		}
		try {
			resultCursor = this.db.query("Song_table", new String[]{"distinct albumname"}, null, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultCursor;
	}
	
	
	/**
	 * 查询所有歌曲个数
	 */
	public long queryAllSongNum(){
		long count = -1L;
		if(!isDBEnable()){
			L.w(tag, "queryAllSongNum exception");
			return count;
		}
		try {
			SQLiteStatement localSQLiteStatement = this.db.compileStatement("select count(distinct id) from Song_table");
			if (localSQLiteStatement != null) {
				count = localSQLiteStatement.simpleQueryForLong();
				L.i(tag, "queryAllSongNum: " + count);
			}
			return count;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	
	
	/**
	 * 查询所有歌手个数
	 */
	public long queryAllSongerNum(){
		long count = -1L;
		if(!isDBEnable()){
			L.w(tag, "queryAllSongerNum exception");
			return count;
		}
		try {
			SQLiteStatement localSQLiteStatement = this.db.compileStatement("select count(distinct singername) from Song_table");
			if (localSQLiteStatement != null) {
				count = localSQLiteStatement.simpleQueryForLong();
				L.i(tag, "queryAllSongerNum: " + count);
			}
			return count;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	
	
	
	
	/**
	 * 查询所有专辑个数
	 */
	public long queryAllAlbumrNum(){
		long count = -1L;
		if(!isDBEnable()){
			L.w(tag, "queryAllAlbumrNum exception");
			return count;
		}
		try {
			SQLiteStatement localSQLiteStatement = this.db.compileStatement("select count(distinct albumname) from Song_table");
			if (localSQLiteStatement != null) {
				count = localSQLiteStatement.simpleQueryForLong();
				L.i(tag, "queryAllAlbumrNum: " + count);
			}
			return count;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	
	
	
	
	
	/**
	 * 查询专辑下的歌曲
	 * 可能有重复的歌曲，但是文件不重复
	 */
	public Cursor querySongFromAlbumer(String albumer){
		Cursor resultCursor = null;
		if (!isDBEnable()) {
			return resultCursor;
		}
		try {
			resultCursor = this.db.query("Song_table", null, "albumname = ?", new String[]{albumer}, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultCursor; 
	}
	
	/**
	 * 查询某歌手下的歌曲
	 * 可能有重复的歌曲，但是文件不重复
	 */
	public Cursor querySongFromSinger(String singer){
		Cursor resultCursor = null;
		if (!isDBEnable()) {
			return resultCursor;
		}
		try {
			resultCursor = this.db.query("Song_table", null, "singername = ?", new String[]{singer}, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultCursor; 
	}
	
	
	
	/**
	 * 查询某歌手下的歌曲
	 * 可能有重复的歌曲，但是文件不重复
	 */
	public long querySongNumFromSinger(String singer){
		long count = -1L;
		if(!isDBEnable()){
			L.w(tag, "querySongNumFromSinger exception");
			return count;
		}
		try {
			SQLiteStatement localSQLiteStatement = this.db.compileStatement("select count(distinct id) from Song_table where singername = \"" + singer + "\"");
			if (localSQLiteStatement != null) {
				count = localSQLiteStatement.simpleQueryForLong();
				L.i(tag, "querySongNumFromSinger: " + count);
			}
			return count;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	
	
	/**
	 * 查询某专辑下的歌曲数
	 * 可能有重复的歌曲，但是文件不重复
	 */
	public long queryAlbumrNumFromSinger(String albumr){
		long count = -1L;
		if(!isDBEnable()){
			L.w(tag, "queryAlbumrNumFromSinger exception");
			return count;
		}
		try {
			SQLiteStatement localSQLiteStatement = this.db.compileStatement("select count(distinct id) from Song_table where albumname = \"" + albumr + "\"");
			if (localSQLiteStatement != null) {
				count = localSQLiteStatement.simpleQueryForLong();
				L.i(tag, "queryAlbumrNumFromSinger: " + count);
			}
			return count;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	
	/**
	 * 返回系统媒体库corsor
	 * @param paramCursor
	 * @return
	 */
	public Cursor querySystemMedia(){
		Cursor cursor = context.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Audio.Media._ID,
						MediaStore.Audio.Media.DISPLAY_NAME,
						MediaStore.Audio.Media.TITLE,
						MediaStore.Audio.Media.DURATION,
						MediaStore.Audio.Media.ARTIST,
						MediaStore.Audio.Media.ALBUM,
						MediaStore.Audio.Media.YEAR,
						MediaStore.Audio.Media.MIME_TYPE,
						MediaStore.Audio.Media.SIZE,
						MediaStore.Audio.Media.DATA },
				MediaStore.Audio.Media.MIME_TYPE + "=? or "
						+ MediaStore.Audio.Media.MIME_TYPE + "=?",
				new String[] { "audio/mpeg", "audio/x-ms-wma" }, null);
		return cursor;
	}
	
	public boolean deleteSongInfoFromList(ArrayList<Integer> songIDList, int type){
		if ((songIDList == null) || (songIDList.size() == 0) || !isDBEnable()){
			L.w(tag, "deleteSongInfoFromList exception");
			return false;
		}
		L.i(tag, "删除的歌曲信息：" + songIDList);
	    try
	    { 
	      this.db.beginTransaction();
	      for(Integer id : songIDList){
	          String[] arrayOfString = new String[1];
	          arrayOfString[0] = id + "";
	          if(this.db.delete("Song_table", "id=?", arrayOfString) <= 0){
	        	  L.e(tag, "删除歌曲信息失败！");
	          }
	      }
	      this.db.setTransactionSuccessful();
	      L.i(tag, "批量删除歌曲信息成功！！");
	    }
	    catch (Throwable localThrowable2)
	    {
	    	localThrowable2.printStackTrace();
			return false;
	    }
	    finally
	    {
	    	 this.db.endTransaction();
	    }
		return true;
	}
	
	/**
	 * 删除信息
	 * @param paramCursor
	 * @return
	 */
	public boolean deleteSongInfoFromList(ArrayList<SongInfo> songList){
		if ((songList == null) || (songList.size() == 0) || !isDBEnable()){
			L.w(tag, "deleteSongInfoFromList exception");
			return false;
		}
		L.i(tag, "删除的歌曲信息：" + songList);
	    try
	    { 
	      this.db.beginTransaction();
	      for(SongInfo songInfo : songList){
	          String[] arrayOfString = new String[1];
	          arrayOfString[0] = songInfo.getId() + "";
	          if(this.db.delete("Song_table", "id=?", arrayOfString) <= 0){
	        	  L.e(tag, "删除歌曲信息失败！");
	          }
	      }
	      this.db.setTransactionSuccessful();
	      L.i(tag, "批量删除歌曲信息成功！！");
	    }
	    catch (Throwable localThrowable2)
	    {
	    	localThrowable2.printStackTrace();
			return false;
	    }
	    finally
	    {
	    	 this.db.endTransaction();
	    }
		return true;
	}
	
	/**
	 * 取指定路径下的歌曲
	 * 如 sd 和 usb下的进行分类
	 */
	public Cursor queryAllSongFromModule(String modulePath){
		Cursor resultCursor = null;
		if (!isDBEnable()) {
			return resultCursor;
		}
		try {
			resultCursor = this.db.query("Song_table", null, "path like ?", new String[]{modulePath + "%"}, null, null, "pinyin asc");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultCursor; 
	}
	
	
	
	/**
	 * 插入收藏歌曲信息
	 * @param paramCursor
	 * @return
	 */
	public boolean insertCollectionTable(ArrayList<SongInfo> songInfoList){
		if ((songInfoList == null) || (songInfoList.size() == 0) || !isDBEnable()){
			L.e(tag, "insertCollectionTable " + songInfoList);
			return false;
		}
		L.i(tag, "Collection 添加songInfo个数：" + songInfoList.size());
		String currentPath = null;
		try 
	    {
	      this.db.beginTransaction();
	      ContentValues localContentValues = new ContentValues();
	      for(SongInfo songInfo : songInfoList){ 
	    	  localContentValues.clear(); 
	          localContentValues.put("id", songInfo.getId());
	          localContentValues.put("type", songInfo.getType());
	          localContentValues.put("fid", SongInfo.makeKey(songInfo.getId(), songInfo.getType()));
	          localContentValues.put("path", songInfo.getFileName());
	          localContentValues.put("name", songInfo.getTitle());
	          localContentValues.put("singername", songInfo.getSinger());
	          localContentValues.put("albumname", songInfo.getAlbum());
	          localContentValues.put("pinyin", songInfo.getPinyin());
	          currentPath = songInfo.getFileName();
	          this.db.insert("Collection_table", null, localContentValues);
	      } 
	      this.db.setTransactionSuccessful();
	    }catch(Exception e){
	    	L.e(tag, "error currentPath = " + currentPath);
	    	e.printStackTrace();
			return false;
	    }  
	    finally 
	    {
	    	try{
	    	 this.db.endTransaction();
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
	    }
		return true;
	}
	
	/**
	 * 删除收藏
	 */
	public boolean deleteSongInfoFromCollection(ArrayList<SongInfo> songList){
		if ((songList == null) || (songList.size() == 0) || !isDBEnable()){
			L.w(tag, "deleteSongInfoFromCollection exception");
			return false;
		}
		L.i(tag, "删除的歌曲信息：" + songList);
	    try
	    { 
	      this.db.beginTransaction();
	      for(SongInfo songInfo : songList){
	          String[] arrayOfString = new String[1];
	          arrayOfString[0] = songInfo.getId() + "";
	          if(this.db.delete("Collection_table", "id=?", arrayOfString) <= 0){
	        	  L.e(tag, "删除歌曲信息失败！");
	          }
	      }
	      this.db.setTransactionSuccessful();
	      L.i(tag, "批量删除歌曲信息成功！！");
	    }
	    catch (Throwable localThrowable2)
	    {
	    	localThrowable2.printStackTrace();
			return false;
	    }
	    finally
	    {
	    	 this.db.endTransaction();
	    }
		return true;
	}
	
	/**
	 * 查询所有song
	 * @return
	 */
	public Cursor queryAllSongFromCollection(){
		Cursor resultCursor = null;
		if (!isDBEnable()) {
			return resultCursor;
		}
		try {
			resultCursor = this.db.query("Collection_table", null, null, null, null, null, "pinyin asc"); //升序asc 降序 desc
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultCursor;
	}
	
	
//	/**
//	 * 歌曲查询，根据关键字，按歌曲拼音排序
//	 */
//	public Cursor querySongFromKey(String key){
//		Cursor resultCursor = null;
//		if (!isDBEnable()) {
//			return resultCursor;
//		}
//		try {
//			resultCursor = this.db.query("Song_table", null, "path like ?", new String[]{modulePath + "%"}, null, null, "pinyin asc");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return resultCursor;
//	}
	
//--------------------------------------------

	public static String a(Cursor paramCursor) {
		if (paramCursor == null)
			return null;
		return paramCursor.getString(paramCursor.getColumnIndex("extext1"));
	}

	  private void a(SQLiteDatabase paramSQLiteDatabase)
	  {
	    Log.d("RecognizerDB", "updateVersion5To6");
	    paramSQLiteDatabase.execSQL("alter table history add column switch INTEGER");
	    paramSQLiteDatabase.execSQL("alter table history add column pay_month INTEGER");
	    paramSQLiteDatabase.execSQL("alter table history add column pay_price ");
	    paramSQLiteDatabase.execSQL("alter table history add column pay_album INTEGER");
	    paramSQLiteDatabase.execSQL("alter table history add column pay_album_price INTEGER");
	    paramSQLiteDatabase.execSQL("alter table history add column try_size INTEGER");
	    paramSQLiteDatabase.execSQL("alter table history add column try_begin INTEGER");
	    paramSQLiteDatabase.execSQL("alter table history add column try_end INTEGER");
	    paramSQLiteDatabase.execSQL("alter table history add column alert INTEGER");
	    paramSQLiteDatabase.execSQL("alter table history add column quality INTEGER DEFAULT -1");
	    paramSQLiteDatabase.execSQL("alter table history add column pay_play INTEGER");
	    paramSQLiteDatabase.execSQL("alter table history add column pay_download INTEGER");
	    paramSQLiteDatabase.execSQL("alter table history add column pay_status INTEGER");
	  }

	  public static boolean a(long paramLong)
	  {
//	    int i = 1;
//	    SQLiteDatabase localSQLiteDatabase = null;
//	    try
//	    {
//	      localSQLiteDatabase = com.tencent.qqmusic.common.b.e.e();
//	      localSQLiteDatabase.beginTransaction();
//	      String[] arrayOfString = new String[1];
//	      arrayOfString[0] = String.valueOf(paramLong);
//	      if (localSQLiteDatabase.delete("history", "songid=?", arrayOfString) > 0);
//	      while (true)
//	      {
//	        localSQLiteDatabase.setTransactionSuccessful();
//	        return i;
//	        i = 0;
//	      }
//	    }
//	    catch (Exception localException)
//	    {
//	      localException.printStackTrace();
//	      Log.e("RecognizerDB", "Exception on deleteSong: " + localException.getMessage());
//	      return false;
//	    }
//	    finally
//	    {
//	      if (localSQLiteDatabase != null)
//	        localSQLiteDatabase.endTransaction();
//	    }
//	    throw localObject;
		  
		  return false;
	  }

//	  public static boolean a(j paramj)
//	  {
//	    SQLiteDatabase localSQLiteDatabase = null;
//	    Log.d("RecognizerDB", "addRecognizerInfo:info:" + paramj.a);
//	    if ((paramj != null) && (!b(paramj)))
//	      try
//	      {
//	        localSQLiteDatabase = com.tencent.qqmusic.common.b.e.e();
//	        localSQLiteDatabase.beginTransaction();
//	        ContentValues localContentValues = new ContentValues();
//	        localContentValues.put("time", Long.valueOf(System.currentTimeMillis()));
//	        localContentValues.put("songid", Long.valueOf(paramj.a));
//	        localContentValues.put("type", Integer.valueOf(2));
//	        localContentValues.put("singerid", Integer.valueOf(0));
//	        localContentValues.put("albumid", Integer.valueOf(0));
//	        localContentValues.put("duration", Integer.valueOf(0));
//	        localContentValues.put("size1", Integer.valueOf(0));
//	        localContentValues.put("size2", Integer.valueOf(0));
//	        localContentValues.put("name", paramj.d);
//	        localContentValues.put("singer", paramj.e);
//	        localContentValues.put("album", "");
//	        localContentValues.put("albumurl", "");
//	        localContentValues.put("wap24", Integer.valueOf(0));
//	        localContentValues.put("wap48", paramj.k);
//	        localContentValues.put("wap96", "");
//	        localContentValues.put("wap128", "");
//	        localContentValues.put("wifiurl", "");
//	        localContentValues.put("exint1", Integer.valueOf(0));
//	        localContentValues.put("exint2", Integer.valueOf(0));
//	        localContentValues.put("extext3", "");
//	        localContentValues.put("soso", Integer.valueOf(0));
//	        localContentValues.put("exint3", Integer.valueOf(0));
//	        localContentValues.put("exint4", Integer.valueOf(0));
//	        localContentValues.put("extext1", paramj.i);
//	        localContentValues.put("extext2", paramj.j);
//	        localContentValues.put("extext4", "");
//	        if (localSQLiteDatabase.insert("history", null, localContentValues) > 0L);
//	        for (int i = 1; ; i = 0)
//	        {
//	          localSQLiteDatabase.setTransactionSuccessful();
//	          return i;
//	        }
//	      }
//	      catch (Exception localException)
//	      {
//	        localException.printStackTrace();
//	        Log.e("RecognizerDB", "Exception on addRecognizerInfo: " + localException.getMessage());
//	        return false;
//	      }
//	      finally
//	      {
//	        if (localSQLiteDatabase != null)
//	          localSQLiteDatabase.endTransaction();
//	      }
//	    return false;
//	  }

	  public static boolean a(SongInfo paramSongInfo)
	  {
//	    int i = 1;
//	    Log.d("RecognizerDB", "addSongInfo:info:" + paramSongInfo.getId());
//	    if (paramSongInfo != null)
//	    {
//	      SQLiteDatabase localSQLiteDatabase = null;
//	      try
//	      {
//	        localSQLiteDatabase = com.tencent.qqmusic.common.b.e.e();
//	        localSQLiteDatabase.beginTransaction();
//	        ContentValues localContentValues = new ContentValues();
//	        localContentValues.put("type", Integer.valueOf(paramSongInfo.getType()));
//	        localContentValues.put("singerid", Long.valueOf(paramSongInfo.getSingerId()));
//	        localContentValues.put("albumid", Long.valueOf(paramSongInfo.getAlbumId()));
//	        localContentValues.put("duration", Long.valueOf(1000L * c(paramSongInfo.getDuration())));
//	        localContentValues.put("size1", Long.valueOf(paramSongInfo.getSize128()));
//	        localContentValues.put("size2", Long.valueOf(paramSongInfo.getHQSize()));
//	        localContentValues.put("exint3", Long.valueOf(paramSongInfo.getFlacSize()));
//	        localContentValues.put("name", paramSongInfo.getName());
//	        localContentValues.put("singer", paramSongInfo.getSinger());
//	        localContentValues.put("album", paramSongInfo.getAlbum());
//	        int j;
//	        int k;
//	        if (paramSongInfo.isDujia())
//	        {
//	          j = i;
//	          localContentValues.put("wap24", Integer.valueOf(j));
//	          if (paramSongInfo.isSosoMusic())
//	            localContentValues.put("wifiurl", x.e(paramSongInfo.get128KMP3Url(true)));
//	          localContentValues.put("exint1", Integer.valueOf(0));
//	          localContentValues.put("exint2", Integer.valueOf(0));
//	          localContentValues.put("extext3", paramSongInfo.getMid());
//	          localContentValues.put("soso", Integer.valueOf(0));
//	          localContentValues.put("exint4", Long.valueOf(paramSongInfo.getProtectTime()));
//	          localContentValues.put("extext4", paramSongInfo.getMVId());
//	          localContentValues.put("wap48", paramSongInfo.getKmid());
//	          localContentValues.put("wap96", paramSongInfo.getAlbumMid());
//	          localContentValues.put("switch", Integer.valueOf(paramSongInfo.getSwitch()));
//	          localContentValues.put("pay_month", Integer.valueOf(paramSongInfo.getPayTrackMonth()));
//	          localContentValues.put("pay_price", Integer.valueOf(paramSongInfo.getPayTrackPrice()));
//	          localContentValues.put("pay_album", Integer.valueOf(paramSongInfo.getPayAlbum()));
//	          localContentValues.put("pay_album_price", Integer.valueOf(paramSongInfo.getPayAlbumPrice()));
//	          localContentValues.put("try_size", Integer.valueOf(paramSongInfo.getTrySize()));
//	          localContentValues.put("try_begin", Integer.valueOf(paramSongInfo.getTryBegin()));
//	          localContentValues.put("try_end", Integer.valueOf(paramSongInfo.getTryEnd()));
//	          localContentValues.put("alert", Integer.valueOf(paramSongInfo.getAlert()));
//	          localContentValues.put("quality", Integer.valueOf(paramSongInfo.getQuality()));
//	          localContentValues.put("pay_play", Integer.valueOf(paramSongInfo.getPayPlay()));
//	          localContentValues.put("pay_download", Integer.valueOf(paramSongInfo.getPayDownload()));
//	          if (!paramSongInfo.hasPaid())
//	            break label568;
//	          k = i;
//	          label504: localContentValues.put("pay_status", Integer.valueOf(k));
//	          String[] arrayOfString = new String[1];
//	          arrayOfString[0] = String.valueOf(paramSongInfo.getId());
//	          if (localSQLiteDatabase.update("history", localContentValues, "songid=?", arrayOfString) <= 0)
//	            break label574;
//	        }
//	        while (true)
//	        {
//	          localSQLiteDatabase.setTransactionSuccessful();
//	          return i;
//	          j = 0;
//	          break;
//	          label568: k = 0;
//	          break label504;
//	          label574: i = 0;
//	        }
//	      }
//	      catch (Exception localException)
//	      {
//	        localException.printStackTrace();
//	        Log.e("RecognizerDB", "Exception on addSongInfo: " + localException.getMessage());
//	        return false;
//	      }
//	      finally
//	      {
//	        if (localSQLiteDatabase != null)
//	          localSQLiteDatabase.endTransaction();
//	      }
//	    }
	    return false;
	  }

	  // ERROR //
	  public static SongInfo[] a() 
	  {
	    // Byte code:
	    //   0: ldc 122
	    //   2: ldc_w 399
	    //   5: invokestatic 130	com/tencent/qqmusiccommon/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)V
	    //   8: new 401	java/util/ArrayList
	    //   11: dup
	    //   12: invokespecial 402	java/util/ArrayList:<init>	()V
	    //   15: astore_0
	    //   16: invokestatic 404	com/tencent/qqmusic/common/b/e:d	()Landroid/database/sqlite/SQLiteDatabase;
	    //   19: ldc 180
	    //   21: getstatic 97	com/tencent/qqmusic/business/ad/h:b	[Ljava/lang/String;
	    //   24: aconst_null
	    //   25: aconst_null
	    //   26: aconst_null
	    //   27: aconst_null
	    //   28: getstatic 101	com/tencent/qqmusic/business/ad/h:c	Ljava/lang/String;
	    //   31: invokevirtual 408	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
	    //   34: astore 4
	    //   36: aload 4
	    //   38: astore_2
	    //   39: aload_2
	    //   40: invokeinterface 411 1 0
	    //   45: ifeq +68 -> 113
	    //   48: aload_0
	    //   49: aload_2
	    //   50: invokestatic 414	com/tencent/qqmusic/business/ad/h:c	(Landroid/database/Cursor;)Lcom/tencent/qqmusicplayerprocess/songinfo/SongInfo;
	    //   53: invokeinterface 420 2 0
	    //   58: pop
	    //   59: goto -20 -> 39
	    //   62: astore_1
	    //   63: ldc 122
	    //   65: aload_1
	    //   66: invokestatic 423	com/tencent/qqmusiccommon/util/Log:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
	    //   69: aload_2
	    //   70: ifnull +9 -> 79
	    //   73: aload_2
	    //   74: invokeinterface 426 1 0
	    //   79: aload_0
	    //   80: aload_0
	    //   81: invokeinterface 429 1 0
	    //   86: anewarray 281	com/tencent/qqmusicplayerprocess/songinfo/SongInfo
	    //   89: invokeinterface 433 2 0
	    //   94: checkcast 435	[Lcom/tencent/qqmusicplayerprocess/songinfo/SongInfo;
	    //   97: areturn
	    //   98: astore_3
	    //   99: aconst_null
	    //   100: astore_2
	    //   101: aload_2
	    //   102: ifnull +9 -> 111
	    //   105: aload_2
	    //   106: invokeinterface 426 1 0
	    //   111: aload_3
	    //   112: athrow
	    //   113: aload_2
	    //   114: ifnull -35 -> 79
	    //   117: goto -44 -> 73
	    //   120: astore_3
	    //   121: goto -20 -> 101
	    //   124: astore_1
	    //   125: aconst_null
	    //   126: astore_2
	    //   127: goto -64 -> 63
	    //
	    // Exception table:
	    //   from	to	target	type
	    //   39	59	62	java/lang/Exception
	    //   16	36	98	finally
	    //   39	59	120	finally
	    //   63	69	120	finally
	    //   16	36	124	java/lang/Exception
		  return null;
	  }

	  // ERROR //
	  public static SongInfo b(long paramLong)
	  {
	    // Byte code:
	    //   0: ldc 122
	    //   2: new 197	java/lang/StringBuilder
	    //   5: dup
	    //   6: invokespecial 199	java/lang/StringBuilder:<init>	()V
	    //   9: ldc_w 438
	    //   12: invokevirtual 205	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
	    //   15: lload_0
	    //   16: invokevirtual 225	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
	    //   19: invokevirtual 212	java/lang/StringBuilder:toString	()Ljava/lang/String;
	    //   22: invokestatic 130	com/tencent/qqmusiccommon/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)V
	    //   25: lload_0
	    //   26: lconst_0
	    //   27: lcmp
	    //   28: ifle +162 -> 190
	    //   31: invokestatic 404	com/tencent/qqmusic/common/b/e:d	()Landroid/database/sqlite/SQLiteDatabase;
	    //   34: astore 5
	    //   36: iconst_1
	    //   37: anewarray 13	java/lang/String
	    //   40: astore 6
	    //   42: aload 6
	    //   44: iconst_0
	    //   45: lload_0
	    //   46: invokestatic 178	java/lang/String:valueOf	(J)Ljava/lang/String;
	    //   49: aastore
	    //   50: aload 5
	    //   52: ldc 180
	    //   54: getstatic 97	com/tencent/qqmusic/business/ad/h:b	[Ljava/lang/String;
	    //   57: ldc 182
	    //   59: aload 6
	    //   61: aconst_null
	    //   62: aconst_null
	    //   63: getstatic 101	com/tencent/qqmusic/business/ad/h:c	Ljava/lang/String;
	    //   66: invokevirtual 408	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
	    //   69: astore 7
	    //   71: aload 7
	    //   73: astore_3
	    //   74: aload_3
	    //   75: ifnull +109 -> 184
	    //   78: aload_3
	    //   79: invokeinterface 441 1 0
	    //   84: ifeq +100 -> 184
	    //   87: aload_3
	    //   88: invokestatic 414	com/tencent/qqmusic/business/ad/h:c	(Landroid/database/Cursor;)Lcom/tencent/qqmusicplayerprocess/songinfo/SongInfo;
	    //   91: astore 9
	    //   93: aload 9
	    //   95: astore 8
	    //   97: aload_3
	    //   98: ifnull +9 -> 107
	    //   101: aload_3
	    //   102: invokeinterface 426 1 0
	    //   107: aload 8
	    //   109: areturn
	    //   110: astore 4
	    //   112: aconst_null
	    //   113: astore_3
	    //   114: aload 4
	    //   116: invokevirtual 195	java/lang/Exception:printStackTrace	()V
	    //   119: ldc 122
	    //   121: new 197	java/lang/StringBuilder
	    //   124: dup
	    //   125: invokespecial 199	java/lang/StringBuilder:<init>	()V
	    //   128: ldc_w 443
	    //   131: invokevirtual 205	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
	    //   134: aload 4
	    //   136: invokevirtual 209	java/lang/Exception:getMessage	()Ljava/lang/String;
	    //   139: invokevirtual 205	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
	    //   142: invokevirtual 212	java/lang/StringBuilder:toString	()Ljava/lang/String;
	    //   145: invokestatic 214	com/tencent/qqmusiccommon/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
	    //   148: aload_3
	    //   149: ifnull +9 -> 158
	    //   152: aload_3
	    //   153: invokeinterface 426 1 0
	    //   158: aconst_null
	    //   159: areturn
	    //   160: astore_2
	    //   161: aconst_null
	    //   162: astore_3
	    //   163: aload_3
	    //   164: ifnull +9 -> 173
	    //   167: aload_3
	    //   168: invokeinterface 426 1 0
	    //   173: aload_2
	    //   174: athrow
	    //   175: astore_2
	    //   176: goto -13 -> 163
	    //   179: astore 4
	    //   181: goto -67 -> 114
	    //   184: aconst_null
	    //   185: astore 8
	    //   187: goto -90 -> 97
	    //   190: aconst_null
	    //   191: areturn
	    //
	    // Exception table:
	    //   from	to	target	type
	    //   31	71	110	java/lang/Exception
	    //   31	71	160	finally
	    //   78	93	175	finally
	    //   114	148	175	finally
	    //   78	93	179	java/lang/Exception
		  return null;
	  }

	  // ERROR //
	  public static String b()
	  {
		  return null;
	  }

	  public static String b(Cursor paramCursor)
	  {
	    if (paramCursor == null)
	      return null;
	    return paramCursor.getString(paramCursor.getColumnIndex("extext2"));
	  }

//	  private static boolean b(j paramj)
//	  {
//	    int i = 1;
//	    if (paramj != null)
//	    {
//	      SQLiteDatabase localSQLiteDatabase = null;
//	      try
//	      {
//	        localSQLiteDatabase = com.tencent.qqmusic.common.b.e.e();
//	        localSQLiteDatabase.beginTransaction();
//	        ContentValues localContentValues = new ContentValues();
//	        localContentValues.put("time", Long.valueOf(System.currentTimeMillis()));
//	        localContentValues.put("extext1", paramj.i);
//	        localContentValues.put("extext2", paramj.j);
//	        String[] arrayOfString = new String[1];
//	        arrayOfString[0] = String.valueOf(paramj.a);
//	        if (localSQLiteDatabase.update("history", localContentValues, "songid=?", arrayOfString) > 0);
//	        while (true)
//	        {
//	          localSQLiteDatabase.setTransactionSuccessful();
//	          return i;
//	          i = 0;
//	        }
//	      }
//	      catch (Exception localException)
//	      {
//	        localException.printStackTrace();
//	        Log.e("RecognizerDB", "Exception on updateWhenExistEx: " + localException.getMessage());
//	        return false;
//	      }
//	      finally
//	      {
//	        if (localSQLiteDatabase != null)
//	          localSQLiteDatabase.endTransaction();
//	      }
//	    }
//	    return false;
//	  }

//	  private static long c(long paramLong)
//	  {
//	    if (paramLong > 1000000000L)
//	      paramLong /= 1000000000L;
//	    do
//	    {
//	      return paramLong;
//	      if (paramLong > 1000000L)
//	        return paramLong / 1000000L;
//	    }
//	    while (paramLong <= 1000L);
//	    return paramLong / 1000L;
//	  }

	  public static SongInfo c(Cursor paramCursor)
	  {
//	    if (paramCursor == null)
//	      return null;
//	    while (true)
//	    {
//	      try
//	      {
//	        long l = paramCursor.getLong(paramCursor.getColumnIndex("songid"));
//	        int i = paramCursor.getInt(paramCursor.getColumnIndex("type"));
//	        if ((l <= 0L) || (i <= 0))
//	          break;
//	        SongInfo localSongInfo = ((c)com.tencent.qqmusic.e.getInstance(53)).a(l, i);
//	        localSongInfo.setSingerId(paramCursor.getLong(paramCursor.getColumnIndex("singerid")));
//	        localSongInfo.setAlbumId(paramCursor.getLong(paramCursor.getColumnIndex("albumid")));
//	        localSongInfo.setDuration(1000L * c(paramCursor.getLong(paramCursor.getColumnIndex("duration"))));
//	        localSongInfo.setSize128(paramCursor.getLong(paramCursor.getColumnIndex("size1")));
//	        localSongInfo.setHQSize(paramCursor.getLong(paramCursor.getColumnIndex("size2")));
//	        localSongInfo.setCreateDate(paramCursor.getLong(paramCursor.getColumnIndex("time")));
//	        localSongInfo.setName(paramCursor.getString(paramCursor.getColumnIndex("name")));
//	        localSongInfo.setSinger(paramCursor.getString(paramCursor.getColumnIndex("singer")));
//	        localSongInfo.setAlbum(paramCursor.getString(paramCursor.getColumnIndex("album")));
//	        localSongInfo.setFlacSize(paramCursor.getLong(paramCursor.getColumnIndex("exint3")));
//	        localSongInfo.setProtectTime(paramCursor.getLong(paramCursor.getColumnIndex("exint4")));
//	        if (i != 4)
//	          continue;
//	        localSongInfo.set128KMP3Url(x.f(paramCursor.getString(paramCursor.getColumnIndex("wifiurl"))));
//	        localSongInfo.setMid(paramCursor.getString(paramCursor.getColumnIndex("extext3")));
//	        localSongInfo.setAction(paramCursor.getInt(paramCursor.getColumnIndex("exint1")));
//	        localSongInfo.setEQ(paramCursor.getInt(paramCursor.getColumnIndex("exint2")));
//	        localSongInfo.setMVId(paramCursor.getString(paramCursor.getColumnIndex("extext4")));
//	        if (paramCursor.getInt(paramCursor.getColumnIndex("wap24")) == 1)
//	        {
//	          bool = true;
//	          localSongInfo.setDujia(bool);
//	          localSongInfo.setKmid(paramCursor.getString(paramCursor.getColumnIndex("wap48")));
//	          localSongInfo.setAlbumMid(paramCursor.getString(paramCursor.getColumnIndex("wap96")));
//	          if (paramCursor.getColumnIndex("switch") == -1)
//	            continue;
//	          localSongInfo.setSwitch(paramCursor.getInt(paramCursor.getColumnIndex("switch")));
//	          if (paramCursor.getColumnIndex("pay_month") == -1)
//	            continue;
//	          localSongInfo.setPayTrackMonth(paramCursor.getInt(paramCursor.getColumnIndex("pay_month")));
//	          if (paramCursor.getColumnIndex("pay_price") == -1)
//	            continue;
//	          localSongInfo.setPayTrackPrice(paramCursor.getInt(paramCursor.getColumnIndex("pay_price")));
//	          if (paramCursor.getColumnIndex("pay_album") == -1)
//	            continue;
//	          localSongInfo.setPayAlbum(paramCursor.getInt(paramCursor.getColumnIndex("pay_album")));
//	          if (paramCursor.getColumnIndex("pay_album_price") == -1)
//	            continue;
//	          localSongInfo.setPayAlbumPrice(paramCursor.getInt(paramCursor.getColumnIndex("pay_album_price")));
//	          if (paramCursor.getColumnIndex("try_size") == -1)
//	            continue;
//	          localSongInfo.setTrySize(paramCursor.getInt(paramCursor.getColumnIndex("try_size")));
//	          if (paramCursor.getColumnIndex("try_begin") == -1)
//	            continue;
//	          localSongInfo.setTryBegin(paramCursor.getInt(paramCursor.getColumnIndex("try_begin")));
//	          if (paramCursor.getColumnIndex("try_end") == -1)
//	            continue;
//	          localSongInfo.setTryEnd(paramCursor.getInt(paramCursor.getColumnIndex("try_end")));
//	          if (paramCursor.getColumnIndex("alert") == -1)
//	            continue;
//	          localSongInfo.setAlert(paramCursor.getInt(paramCursor.getColumnIndex("alert")));
//	          if (paramCursor.getColumnIndex("quality") == -1)
//	            continue; 
//	          localSongInfo.setQuality(paramCursor.getInt(paramCursor.getColumnIndex("quality")));
//	          if (paramCursor.getColumnIndex("pay_play") == -1)
//	            continue;
//	          localSongInfo.setPayPlay(paramCursor.getInt(paramCursor.getColumnIndex("pay_play")));
//	          if (paramCursor.getColumnIndex("pay_download") == -1)
//	            continue;
//	          localSongInfo.setPayDownload(paramCursor.getInt(paramCursor.getColumnIndex("pay_download")));
//	          if (paramCursor.getColumnIndex("pay_status") == -1)
//	            continue;
//	          localSongInfo.setPayStatus(paramCursor.getInt(paramCursor.getColumnIndex("pay_status")));
//	          localSongInfo.setPlayNeedVkey(false);
//	          return localSongInfo;
//	        }
//	      }
//	      catch (Exception localException)
//	      {
//	        localException.printStackTrace();
//	        return null;
//	      }
//	      boolean bool = false;
//	    }
		  return null;
	  }

	public void onCreate(SQLiteDatabase paramSQLiteDatabase) {
		paramSQLiteDatabase
				.execSQL("CREATE TABLE history ( _id INTEGER PRIMARY KEY AUTOINCREMENT , time INTEGER , songid INTEGER , type INTEGER , singerid INTEGER , albumid INTEGER , duration INTEGER , size1 INTEGER , size2 INTEGER , soso INTEGER , exint1 INTEGER , exint2 INTEGER , exint3 INTEGER , exint4 INTEGER , name TEXT NOT NULL , singer TEXT NOT NULL , album TEXT NOT NULL , albumurl TEXT NOT NULL , wap24 TEXT NOT NULL , wap48 TEXT NOT NULL , wap96 TEXT NOT NULL , wap128 TEXT NOT NULL , wifiurl TEXT NOT NULL , extext1 TEXT NOT NULL , extext2 TEXT NOT NULL , extext3 TEXT NOT NULL , extext4 TEXT NOT NULL,switch integer,pay_month integer,pay_price integer,pay_album integer,pay_album_price integer,try_size integer,try_begin integer,try_end integer,alert integer,quality integer,pay_play integer,pay_download integer,pay_status integer );");
		paramSQLiteDatabase
				.execSQL("create table if not exists radio_table(_id INTEGER PRIMARY KEY AUTOINCREMENT, id INTEGER NOT NULL, type INTEGER, name TEXT, sub_num INTEGER, num_str TEXT, is_vip INTEGER, not_del INTEGER, img_url TEXT, small_url TEXT, jump_url TEXT, group_id INTEGER, uin LONG NOT NULL, duration LONG, date INTEGER, time LONG);");
		L.e(tag, "onCreate");
	}
	
	public void test(){
		File dataDB = context.getDatabasePath("recogniz.db");
		File sdDB = new File("/mnt/sdcard/recogniz.db");
		L.e(tag, "test :" + dataDB.getAbsolutePath());
		try {
			copyFile(dataDB, sdDB);
			L.e(tag, "文件：" + dataDB.getAbsolutePath() + " 拷贝到：" + sdDB.getAbsolutePath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1,
			int paramInt2) {
		if ((paramInt1 <= 4) && (paramInt2 >= 5))
			;
		try {
			paramSQLiteDatabase .execSQL("create table if not exists radio_table(_id INTEGER PRIMARY KEY AUTOINCREMENT, id INTEGER NOT NULL, type INTEGER, name TEXT, sub_num INTEGER, num_str TEXT, is_vip INTEGER, not_del INTEGER, img_url TEXT, small_url TEXT, jump_url TEXT, group_id INTEGER, uin LONG NOT NULL, duration LONG, date INTEGER, time LONG);");
			if (paramInt1 < 6)
				a(paramSQLiteDatabase);
			return;
		} catch (SQLException localSQLException) {
			localSQLException.printStackTrace();
		}
		L.e(tag, "onUpgrade");
	}
	
	
	
	// 复制文件
    public static void copyFile(File sourceFile, File targetFile) throws IOException {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        } finally {
            // 关闭流
            if (inBuff != null)
                inBuff.close();
            if (outBuff != null)
                outBuff.close();
        }
    }
}
