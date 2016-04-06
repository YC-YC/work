/**
 * 
 */
package com.zhcl.video;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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

import com.zh.uitls.L;
import com.zhcl.dao.VideoInfo;

/**
 * Video_table
 * @author chenli
 *
 */
public class VideoDBHelp extends SQLiteOpenHelper{
	private static final String tag = "VideoDBHelp";
	  public static String c = "time DESC";
	  Context context;
	  
	public VideoDBHelp(Context context) {
		super(context, "recogniz.db", null, 6);
		L.i(tag, "create VideoDBHelp instance");
		this.context = context; 
	}
//--------------------------------- 
	private SQLiteDatabase db;
	String dirDBPath ;
	public VideoDBHelp(Context context, String dbPath){
		super(context, "recogniz.db", null, 6);
		this.context = context;
		dirDBPath = "/mnt/sdcard/zhvideo" + File.separator + "zhclvideo.db";
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
//			this.db.execSQL("CREATE TABLE IF NOT EXISTS history ( _id INTEGER PRIMARY KEY AUTOINCREMENT , time INTEGER , videoid INTEGER , type INTEGER , singerid INTEGER , albumid INTEGER , duration INTEGER , size1 INTEGER , size2 INTEGER , soso INTEGER , exint1 INTEGER , exint2 INTEGER , exint3 INTEGER , exint4 INTEGER , name TEXT NOT NULL , singer TEXT NOT NULL , album TEXT NOT NULL , albumurl TEXT NOT NULL , wap24 TEXT NOT NULL , wap48 TEXT NOT NULL , wap96 TEXT NOT NULL , wap128 TEXT NOT NULL , wifiurl TEXT NOT NULL , extext1 TEXT NOT NULL , extext2 TEXT NOT NULL , extext3 TEXT NOT NULL , extext4 TEXT NOT NULL,switch integer,pay_month integer,pay_price integer,pay_album integer,pay_album_price integer,try_size integer,try_begin integer,try_end integer,alert integer,quality integer,pay_play integer,pay_download integer,pay_status integer );");
//			this.db.execSQL("create table if not exists radio_table(_id INTEGER PRIMARY KEY AUTOINCREMENT, id INTEGER NOT NULL, type INTEGER, name TEXT, sub_num INTEGER, num_str TEXT, is_vip INTEGER, not_del INTEGER, img_url TEXT, small_url TEXT, jump_url TEXT, group_id INTEGER, uin LONG NOT NULL, duration LONG, date INTEGER, time LONG);");
			this.db.execSQL("create table if not exists Video_table (id long not null, type integer not null, fid long not null, path text not null, pinyin text not null, name text not null, duration integer not null, PRIMARY KEY (id,type));");
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
	public boolean insertVideoTable(ArrayList<VideoInfo> videoInfoList){
		if ((videoInfoList == null) || (videoInfoList.size() == 0) || !isDBEnable()){
			return false;
		}
		L.i(tag, "添加videoInfo个数：" + videoInfoList.size());
		String currentPath = null;
		try 
	    {
	      this.db.beginTransaction();
	      ContentValues localContentValues = new ContentValues();
	      for(VideoInfo videoInfo : videoInfoList){ 
	    	  localContentValues.clear(); 
	          localContentValues.put("id", videoInfo.getId());
	          localContentValues.put("type", videoInfo.getType());
	          localContentValues.put("fid", VideoInfo.makeKey(videoInfo.getId(), videoInfo.getType()));
	          localContentValues.put("path", videoInfo.getFileName());
	          localContentValues.put("name", videoInfo.getTitle());
	          localContentValues.put("pinyin", videoInfo.getPinyin());
	          localContentValues.put("duration", videoInfo.getDuration());
	          currentPath = videoInfo.getFileName();
	          this.db.insert("Video_table", null, localContentValues);
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
	 * 查询所有video
	 * @return
	 */
	public Cursor queryAllVideo(){
		Cursor resultCursor = null;
		if (!isDBEnable()) {
			return resultCursor;
		}
		try {
			resultCursor = this.db.query("Video_table", null, null, null, null, null, "pinyin asc"); //升序asc 降序 desc
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
			resultCursor = this.db.query("Video_table", new String[]{"distinct singername"}, null, null, null, null, null);
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
			resultCursor = this.db.query("Video_table", new String[]{"distinct albumname"}, null, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultCursor;
	}
	
	
	/**
	 * 查询所有歌曲个数
	 */
	public long queryAllVideoNum(){
		long count = -1L;
		if(!isDBEnable()){
			L.w(tag, "queryAllVideoNum exception");
			return count;
		}
		try {
			SQLiteStatement localSQLiteStatement = this.db.compileStatement("select count(distinct id) from Video_table");
			if (localSQLiteStatement != null) {
				count = localSQLiteStatement.simpleQueryForLong();
				L.i(tag, "queryAllVideoNum: " + count);
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
	public long queryAllVideoerNum(){
		long count = -1L;
		if(!isDBEnable()){
			L.w(tag, "queryAllVideoerNum exception");
			return count;
		}
		try {
			SQLiteStatement localSQLiteStatement = this.db.compileStatement("select count(distinct singername) from Video_table");
			if (localSQLiteStatement != null) {
				count = localSQLiteStatement.simpleQueryForLong();
				L.i(tag, "queryAllVideoerNum: " + count);
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
			SQLiteStatement localSQLiteStatement = this.db.compileStatement("select count(distinct albumname) from Video_table");
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
	public Cursor queryVideoFromAlbumer(String albumer){
		Cursor resultCursor = null;
		if (!isDBEnable()) {
			return resultCursor;
		}
		try {
			resultCursor = this.db.query("Video_table", null, "albumname = ?", new String[]{albumer}, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultCursor; 
	}
	
	/**
	 * 查询某歌手下的歌曲
	 * 可能有重复的歌曲，但是文件不重复
	 */
	public Cursor queryVideoFromSinger(String singer){
		Cursor resultCursor = null;
		if (!isDBEnable()) {
			return resultCursor;
		}
		try {
			resultCursor = this.db.query("Video_table", null, "singername = ?", new String[]{singer}, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultCursor; 
	}
	
	
	
	/**
	 * 查询某歌手下的歌曲
	 * 可能有重复的歌曲，但是文件不重复
	 */
	public long queryVideoNumFromSinger(String singer){
		long count = -1L;
		if(!isDBEnable()){
			L.w(tag, "queryVideoNumFromSinger exception");
			return count;
		}
		try {
			SQLiteStatement localSQLiteStatement = this.db.compileStatement("select count(distinct id) from Video_table where singername = \"" + singer + "\"");
			if (localSQLiteStatement != null) {
				count = localSQLiteStatement.simpleQueryForLong();
				L.i(tag, "queryVideoNumFromSinger: " + count);
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
			SQLiteStatement localSQLiteStatement = this.db.compileStatement("select count(distinct id) from Video_table where albumname = \"" + albumr + "\"");
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
	
	public boolean deleteVideoInfoFromList(ArrayList<Integer> videoIDList, int type){
		if ((videoIDList == null) || (videoIDList.size() == 0) || !isDBEnable()){
			L.w(tag, "deleteVideoInfoFromList exception");
			return false;
		}
		L.i(tag, "删除的歌曲信息：" + videoIDList);
	    try
	    { 
	      this.db.beginTransaction();
	      for(Integer id : videoIDList){
	          String[] arrayOfString = new String[1];
	          arrayOfString[0] = id + "";
	          if(this.db.delete("Video_table", "id=?", arrayOfString) <= 0){
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
	public boolean deleteVideoInfoFromList(ArrayList<VideoInfo> videoList){
		if ((videoList == null) || (videoList.size() == 0) || !isDBEnable()){
			L.w(tag, "deleteVideoInfoFromList exception");
			return false;
		}
		L.i(tag, "删除的歌曲信息：" + videoList);
	    try
	    { 
	      this.db.beginTransaction();
	      for(VideoInfo videoInfo : videoList){
	          String[] arrayOfString = new String[1];
	          arrayOfString[0] = videoInfo.getId() + "";
	          if(this.db.delete("Video_table", "id=?", arrayOfString) <= 0){
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
	public Cursor queryAllVideoFromModule(String modulePath){
		Cursor resultCursor = null;
		if (!isDBEnable()) {
			return resultCursor;
		}
		try {
			resultCursor = this.db.query("Video_table", null, "path like ?", new String[]{modulePath + "%"}, null, null, "pinyin asc");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultCursor; 
	}
	
	
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


	public void onCreate(SQLiteDatabase paramSQLiteDatabase) {
		paramSQLiteDatabase
				.execSQL("CREATE TABLE history ( _id INTEGER PRIMARY KEY AUTOINCREMENT , time INTEGER , videoid INTEGER , type INTEGER , singerid INTEGER , albumid INTEGER , duration INTEGER , size1 INTEGER , size2 INTEGER , soso INTEGER , exint1 INTEGER , exint2 INTEGER , exint3 INTEGER , exint4 INTEGER , name TEXT NOT NULL , singer TEXT NOT NULL , album TEXT NOT NULL , albumurl TEXT NOT NULL , wap24 TEXT NOT NULL , wap48 TEXT NOT NULL , wap96 TEXT NOT NULL , wap128 TEXT NOT NULL , wifiurl TEXT NOT NULL , extext1 TEXT NOT NULL , extext2 TEXT NOT NULL , extext3 TEXT NOT NULL , extext4 TEXT NOT NULL,switch integer,pay_month integer,pay_price integer,pay_album integer,pay_album_price integer,try_size integer,try_begin integer,try_end integer,alert integer,quality integer,pay_play integer,pay_download integer,pay_status integer );");
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
