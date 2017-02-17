/**
 * 
 */
package com.zhonghong.mediascanner.saver;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.zhonghong.mediascanerlib.config.Config;
import com.zhonghong.mediascanerlib.filebean.AudioInfo;
import com.zhonghong.mediascanerlib.filebean.FileInfo;
import com.zhonghong.mediascanerlib.filebean.FileInfo.FileType;
import com.zhonghong.mediascanerlib.filebean.ImageInfo;
import com.zhonghong.mediascanerlib.filebean.OtherInfo;
import com.zhonghong.mediascanerlib.filebean.VideoInfo;
import com.zhonghong.mediascanner.bean.DeviceInfo;
import com.zhonghong.mediascanner.bean.DirInfo;
import com.zhonghong.mediascanner.utils.FileUtil;
import com.zhonghong.mediascanner.utils.L;

/**
 * @author YC
 * @time 2016-12-15 下午5:04:46 TODO:
 */
public class DBLoader {

	private static final String TAG = "DBLoader";
	private String uuid;
	private String rootPath;

	public DBLoader(String uuid, String rootPath) {
		this.uuid = uuid;
		this.rootPath = rootPath;
	}

	public DeviceInfo loadInfoFromDB() {

		String dbFile = FileUtil.getDBFile(uuid);
		if (new File(dbFile).exists()) {
			return loadInfo(dbFile);
		}
		return null;
	}

	public boolean saveDeviceInfoToDB(DeviceInfo info, SaveType saveType) {

		FileUtil.createOrExistsDir(Config.DIR_PATH);

		String dbFile = FileUtil.getDBFile(uuid);
		SQLiteDatabase db = null;
		try {
			db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (db == null) {
			return false;
		}
		boolean bResult = true;
		try {
			if (saveType.isbSaveDirInfo()) {
				saveDirInfos(db, info);
			}
			if (saveType.isbSaveAudioInfo()) {
				saveAudioInfos(db, info.getAudioInfos());
			}
			if (saveType.isbSaveVideoInfo()) {
				saveVideoInfos(db, info.getVideoInfos());
			}
			if (saveType.isbSaveImageInfo()) {
				saveImageInfos(db, info.getImageInfos());
			}
			if (saveType.isbSaveOtherInfo()) {
				saveOtherInfos(db, info.getOtherInfos());
			}
		} catch (Exception e) {
			e.printStackTrace();
			bResult = false;
		}
		try {
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bResult;
	}

	private void saveDirInfos(SQLiteDatabase db, DeviceInfo info) {
		if (info != null) {
			db.execSQL("DROP TABLE IF EXISTS " + DirInfo.getDBTableName());
			List<DirInfo> dirInfos = info.getDirInfos();
			if (dirInfos.size() > 0) {
				L.startTime("插入Dir:" + dirInfos.size());
				db.execSQL(DirInfo.getSaveSQL());
				SQLiteStatement state = db.compileStatement(DirInfo
						.getInsertSQL());
				db.beginTransaction();
				Iterator<DirInfo> iterator = dirInfos.iterator();
				while(iterator.hasNext()){
					iterator.next().bindSQLiteStatement(state);
				}
				db.setTransactionSuccessful();
				db.endTransaction();
				L.endUseTime("插入Dir:" + dirInfos.size());
			}
		}
	}

	
	private void saveAudioInfos(SQLiteDatabase db, List<AudioInfo> infos){
		if (infos != null){
			L.startTime("插入Audio:" + infos.size());
			db.execSQL("DROP TABLE IF EXISTS " + AudioInfo.getDBTableName());
			if (infos.size() > 0){
				db.execSQL(AudioInfo.getSaveSQL());
				SQLiteStatement state = db.compileStatement(AudioInfo.getInsertSQL());
				db.beginTransaction();
				Iterator<AudioInfo> iterator = infos.iterator();
				while(iterator.hasNext()){
					iterator.next().bindSQLiteStatement(state);
				}
				db.setTransactionSuccessful();
				db.endTransaction();
			}
			L.endUseTime("插入Audio:" + infos.size());
		}
	}
	
	private void saveVideoInfos(SQLiteDatabase db, List<VideoInfo> infos){
		if (infos != null){
			L.startTime("插入Vidio:" + infos.size());
			db.execSQL("DROP TABLE IF EXISTS " + VideoInfo.getDBTableName());
			if (infos.size() > 0){
				db.execSQL(VideoInfo.getSaveSQL());
				SQLiteStatement state = db.compileStatement(VideoInfo.getInsertSQL());
				db.beginTransaction();
				Iterator<VideoInfo> iterator = infos.iterator();
				while(iterator.hasNext()){
					iterator.next().bindSQLiteStatement(state);
				}
				db.setTransactionSuccessful();
				db.endTransaction();
			}
			L.endUseTime("插入Vidio:" + infos.size());
		}
	}
	
	private void saveImageInfos(SQLiteDatabase db, List<ImageInfo> infos){
		if (infos != null){
			L.startTime("插入Image:" + infos.size());
			db.execSQL("DROP TABLE IF EXISTS " + ImageInfo.getDBTableName());
			if (infos.size() > 0){
				db.execSQL(ImageInfo.getSaveSQL());
				SQLiteStatement state = db.compileStatement(ImageInfo.getInsertSQL());
				db.beginTransaction();
				Iterator<ImageInfo> iterator = infos.iterator();
				while(iterator.hasNext()){
					iterator.next().bindSQLiteStatement(state);
				}
				db.setTransactionSuccessful();
				db.endTransaction();
			}
			L.endUseTime("插入Image:" + infos.size());
		}
	}
	
	private void saveOtherInfos(SQLiteDatabase db, List<OtherInfo> infos){
		if (infos != null){
			L.startTime("插入数据库");
			if (infos.size() > 0){
				db.execSQL("DROP TABLE IF EXISTS " + OtherInfo.getDBTableName());
				db.execSQL(OtherInfo.getSaveSQL());
				SQLiteStatement state = db.compileStatement(OtherInfo.getInsertSQL());
				db.beginTransaction();
				Iterator<OtherInfo> iterator = infos.iterator();
				while(iterator.hasNext()){
					iterator.next().bindSQLiteStatement(state);
				}
				db.setTransactionSuccessful();
				db.endTransaction();
			}
			L.i(TAG, "插入Other数据数为" + infos.size());
			L.endUseTime("插入数据库");
		}
	}
	

	private DeviceInfo loadInfo(String path) {

		DeviceInfo deviceInfo = new DeviceInfo(uuid, rootPath);
		SQLiteDatabase db = null;
		try {
			db = SQLiteDatabase.openOrCreateDatabase(path, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (db == null) {
			return null;
		}
		Cursor cursor = null;

		try {

			cursor = db.query(DirInfo.getDBTableName(), null, null, null, null,
					null, "_id desc");
			if (cursor != null) {
				while (cursor.moveToNext()) {
					DirInfo dirInfo = DirInfo.getDirInfoFromDBCursor(cursor);
					deviceInfo.putDirInfo(dirInfo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			cursor = db.query(AudioInfo.getDBTableName(), null, null, null,
					null, null, "_id desc"); // 升序asc 降序 desc
			if (cursor != null) {
				while (cursor.moveToNext()) {
					AudioInfo audioInfo = AudioInfo
							.getAudioInfoFromDBCursor(cursor);
					deviceInfo.putFileInfo(FileType.TYPE_AUDIO, audioInfo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			cursor = db.query(VideoInfo.getDBTableName(), null, null, null,
					null, null, "_id desc"); // 升序asc 降序 desc
			if (cursor != null) {
				while (cursor.moveToNext()) {
					VideoInfo info = VideoInfo.getVideoInfoFromDBCursor(cursor);
					deviceInfo.putFileInfo(FileInfo.FileType.TYPE_VIDEO, info);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			cursor = db.query(ImageInfo.getDBTableName(), null, null, null,
					null, null, "_id desc"); // 升序asc 降序 desc
			if (cursor != null) {
				while (cursor.moveToNext()) {
					ImageInfo info = ImageInfo.getImageInfoFromDBCursor(cursor);
					deviceInfo.putFileInfo(FileInfo.FileType.TYPE_IMAGE, info);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			if (Config.bGetOtherInfo) {
				cursor = db.query(OtherInfo.getDBTableName(), null, null, null,
						null, null, "_id desc"); // 升序asc 降序 desc
				if (cursor != null) {
					while (cursor.moveToNext()) {
						OtherInfo info = OtherInfo
								.getOtherInfoFromDBCursor(cursor);
						deviceInfo.putFileInfo(FileInfo.FileType.TYPE_OTHER,
								info);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return deviceInfo;
	}

	public class SaveType {
		private boolean bSaveAudioInfo = false;
		private boolean bSaveVideoInfo = false;
		private boolean bSaveImageInfo = false;
		private boolean bSaveOtherInfo = false;
		private boolean bSaveDirInfo = false;

		private static final int BIT0 = 0x01 << 0;
		private static final int BIT1 = 0x01 << 1;
		private static final int BIT2 = 0x01 << 2;
		private static final int BIT3 = 0x01 << 3;
		private static final int BIT4 = 0x01 << 4;

		private int saveFlag;

		public SaveType(boolean bSaveAudioInfo, boolean bSaveVideoInfo,
				boolean bSaveImageInfo, boolean bSaveOtherInfo,
				boolean bSaveDirInfo) {
			super();
			this.bSaveAudioInfo = bSaveAudioInfo;
			this.bSaveVideoInfo = bSaveVideoInfo;
			this.bSaveImageInfo = bSaveImageInfo;
			this.bSaveOtherInfo = bSaveOtherInfo;
			this.bSaveDirInfo = bSaveDirInfo;
			saveFlag |= (bSaveAudioInfo ? BIT0 : 0);
			saveFlag |= (bSaveVideoInfo ? BIT1 : 0);
			saveFlag |= (bSaveImageInfo ? BIT2 : 0);
			saveFlag |= (bSaveOtherInfo ? BIT3 : 0);
			saveFlag |= (bSaveDirInfo ? BIT4 : 0);

		}

		public SaveType(int flag) {
			saveFlag = flag;
			this.bSaveAudioInfo = ((saveFlag & BIT0) > 0) ? true : false;
			this.bSaveVideoInfo = ((saveFlag & BIT1) > 0) ? true : false;
			this.bSaveImageInfo = ((saveFlag & BIT2) > 0) ? true : false;
			this.bSaveOtherInfo = ((saveFlag & BIT3) > 0) ? true : false;
			this.bSaveDirInfo = ((saveFlag & BIT4) > 0) ? true : false;
		}

		public boolean isbSaveAudioInfo() {
			return bSaveAudioInfo;
		}

		public boolean isbSaveVideoInfo() {
			return bSaveVideoInfo;
		}

		public boolean isbSaveImageInfo() {
			return bSaveImageInfo;
		}

		public boolean isbSaveOtherInfo() {
			return bSaveOtherInfo;
		}

		public boolean isbSaveDirInfo() {
			return bSaveDirInfo;
		}

		public int getSaveFlag() {
			return saveFlag;
		}
	}
}
