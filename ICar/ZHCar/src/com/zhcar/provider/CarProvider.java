/**
 * 
 */
package com.zhcar.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * @author YC
 * @time 2016-7-7 上午10:58:53
 * TODO:提供车信息
 */
public class CarProvider extends ContentProvider {

	private static final String TAG = "CarProvider";
	/**URI的指定，此处的字符串必须和声明的authorities一致*/
	public static final String AUTHORITIES = "cn.com.semisky.carProvider";
	private static final int CARINFO = 1;
	private static final int PHONEINFO = 2;
	private static final int PERMISSION = 3;
	
//	常量UriMatcher.NO_MATCH表示不匹配任何路径的返回码
	private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
	static{
		MATCHER.addURI(AUTHORITIES, "carInfo", CARINFO);
		MATCHER.addURI(AUTHORITIES, "phonenum", PHONEINFO);
		MATCHER.addURI(AUTHORITIES, "permission", PERMISSION);
	}
	
	private DBHelper mDbHelper;
	
	@Override
	public boolean onCreate() {
		DBContext context = new DBContext(getContext());
		mDbHelper = new DBHelper(context);
		
//		mDbHelper = new DBHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		switch (MATCHER.match(uri)) {
		case CARINFO:
			return db.query(CarProviderData.CARINFO_TABLE, projection, selection, selectionArgs, 
					null, null, sortOrder);
		case PHONEINFO:
			return db.query(CarProviderData.PHONENUM_TABLE, projection, selection, selectionArgs, 
					null, null, sortOrder);
		case PERMISSION:
			return db.query(CarProviderData.PERMISSION_TABLE, projection, selection, selectionArgs, 
					null, null, sortOrder);
			default:
				throw new IllegalArgumentException("Unknow Uri:" + uri.toString());
		}
	}

	@Override
	public String getType(Uri uri) {
		switch (MATCHER.match(uri)) {
		case CARINFO:
			return "vnd.android.cursor.item/carinfo"; 
		case PHONEINFO:
			return "vnd.android.cursor.item/phonenum"; 
		case PERMISSION:
			return "vnd.android.cursor.item/premission"; 
			default:
				throw new IllegalArgumentException("Unknow Uri:" + uri.toString());
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		long rowid = 0;
		Uri insertUri;
		switch (MATCHER.match(uri)) {
		case CARINFO:
			// 特别说一下第二个参数是当name字段为空时，将自动插入一个NULL。  
            rowid = db.insert(CarProviderData.CARINFO_TABLE, "debug", values); 
//            Log.i(TAG, "insert rowid = " + rowid);
            insertUri = ContentUris.withAppendedId(uri, rowid);// 得到代表新增记录的Uri  
            this.getContext().getContentResolver().notifyChange(uri, null);  
            return insertUri;
		case PHONEINFO:
			// 特别说一下第二个参数是当name字段为空时，将自动插入一个NULL。  
            rowid = db.insert(CarProviderData.PHONENUM_TABLE, "debug", values); 
//            Log.i(TAG, "insert rowid = " + rowid);
            insertUri = ContentUris.withAppendedId(uri, rowid);// 得到代表新增记录的Uri  
            this.getContext().getContentResolver().notifyChange(uri, null);  
            return insertUri;
		case PERMISSION:
			// 特别说一下第二个参数是当name字段为空时，将自动插入一个NULL。  
            rowid = db.insert(CarProviderData.PERMISSION_TABLE, "debug", values); 
//            Log.i(TAG, "insert rowid = " + rowid);
            insertUri = ContentUris.withAppendedId(uri, rowid);// 得到代表新增记录的Uri  
            this.getContext().getContentResolver().notifyChange(uri, null);  
            return insertUri;
			default:
				throw new IllegalArgumentException("Unknow Uri:" + uri.toString());
		}
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		int count = 0;
		switch (MATCHER.match(uri)) {  
        case CARINFO:  
            count = db.delete(CarProviderData.CARINFO_TABLE, selection, selectionArgs);  
            this.getContext().getContentResolver().notifyChange(uri, null);  
            return count; 
        case PHONEINFO:  
            count = db.delete(CarProviderData.PHONENUM_TABLE, selection, selectionArgs);  
            this.getContext().getContentResolver().notifyChange(uri, null);  
            return count;  
        case PERMISSION:  
            count = db.delete(CarProviderData.PERMISSION_TABLE, selection, selectionArgs);  
            this.getContext().getContentResolver().notifyChange(uri, null);  
            return count;  
        default:  
            throw new IllegalArgumentException("Unkwon Uri:" + uri.toString());  
        }  
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		int count = 0;
		switch (MATCHER.match(uri)) {  
		case CARINFO:  
            count = db.update(CarProviderData.CARINFO_TABLE, values, selection, selectionArgs);  
//            Log.i(TAG, "update count = " + count);
            this.getContext().getContentResolver().notifyChange(uri, null);  
            return count;  
		case PHONEINFO:  
            count = db.update(CarProviderData.PHONENUM_TABLE, values, selection, selectionArgs);  
//            Log.i(TAG, "update count = " + count);
            this.getContext().getContentResolver().notifyChange(uri, null);  
            return count; 
		case PERMISSION:  
            count = db.update(CarProviderData.PERMISSION_TABLE, values, selection, selectionArgs);  
//            Log.i(TAG, "update count = " + count);
            this.getContext().getContentResolver().notifyChange(uri, null);  
            return count; 
        default:  
            throw new IllegalArgumentException("Unkwon Uri:" + uri.toString());  
        }  
	}

}