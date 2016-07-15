package com.zhcar.permission;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.zhcar.R;
import com.zhcar.provider.CarProviderData;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";

	private TextView mContent;
	
	private ContentResolver resolver;
	private final Uri carInfoUri = Uri.parse("content://cn.com.semisky.carProvider/carInfo");
	private final Uri phoneInfoUri = Uri.parse("content://cn.com.semisky.carProvider/phonenum");
	private final Uri permissionUri = Uri.parse("content://cn.com.semisky.carProvider/permission");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContent = (TextView) findViewById(R.id.content);
		
		resolver = getContentResolver();
		resolver.registerContentObserver(carInfoUri, true, new CarInfoObserver(new Handler()) {});
		resolver.registerContentObserver(phoneInfoUri, true, new PhoneInfoObserver(new Handler()) {});
	}
	
	private class CarInfoObserver extends ContentObserver{

		public CarInfoObserver(Handler handler) {
			super(handler);
		}
		
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			Log.i(TAG, "CarInfoProvider 数据变化");
			queryCarinfoProvider();
		}
	};
	private class PhoneInfoObserver extends ContentObserver{

		public PhoneInfoObserver(Handler handler) {
			super(handler);
		}
		
		
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			Log.i(TAG, "PhoneInfoProvider 数据变化");
			queryNuminfoProvider();
		}
	};

	public void doClick(View view) {
		switch (view.getId()) {
		case R.id.query:
	    	queryCarinfoProvider();
	    	queryNuminfoProvider();
	    	queryPermissionProvider();
			break;
		case R.id.insert:
			insertCarinfoProvider();
			insertNuminfoProvider();
			insertPermissionProvider();
			break;
		case R.id.update:
			updateCarinfoProvider();
			updateNuminfoProvider();
			updatePermissionProvider();
			break;
		default:
			break;
		}
	}

	private void insertCarinfoProvider() {
		Cursor cursor = resolver.query(carInfoUri, null, null, null, null);
		if(cursor != null && cursor.moveToNext()){
			Log.i(TAG, "已经存在，请调用update方法");
			return;
		}
		ContentValues values = new ContentValues();
		values.put(CarProviderData.KEY_VIN, "test_vin");
		values.put(CarProviderData.KEY_ICCID, "test_iccid");
		values.put(CarProviderData.KEY_SN, "test_sn");
		values.put(CarProviderData.KEY_IMEI, "test_imei");
		values.put(CarProviderData.KEY_TOKEN, "test_token");
		Uri insertUri = resolver.insert(carInfoUri, values);
	}

	private void queryCarinfoProvider() {
		Cursor cursor = resolver.query(carInfoUri, null, null, null, null);
		if(cursor != null && cursor.moveToNext()){
			String VIN = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_VIN));
			String SN = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_SN));
			String IMEI = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_IMEI));
			String ICCID = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_ICCID));
			String TOKEN = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_TOKEN));
			
			Log.i(TAG, "查询carinfo结果为：vin = " + VIN + ", sn = " + SN 
					+ ", imei = " + IMEI + ", iccid = " + ICCID + ", token = " + TOKEN);
			}
		else{
			if (cursor == null){
				Log.i(TAG, "无查询结果为null");
			}
			Log.i(TAG, "无查询结果");
		}
	}
	
	private void updateCarinfoProvider() {
		ContentValues values = new ContentValues();
		values.put(CarProviderData.KEY_VIN, "test_update_vin" + Math.random());
		values.put(CarProviderData.KEY_ICCID, "test_update_iccid" + Math.random());
//		values.put(CarProviderData.KEY_SN, "test_update_sn");
//		values.put(CarProviderData.KEY_IMEI, "test_update_imei");
		int row = resolver.update(carInfoUri, values, null, null);
	}
	
	private void insertNuminfoProvider() {
		Cursor cursor = resolver.query(phoneInfoUri, null, null, null, null);
		if(cursor != null && cursor.moveToNext()){
			Log.i(TAG, "已经存在，请调用update方法");
			return;
		}
		ContentValues values = new ContentValues();
		values.put(CarProviderData.KEY_SHOTCUT_RECUTE_NUM, "4008001000");
		values.put(CarProviderData.KEY_SHOTCUT_RECUTE_TIME, "20");
		values.put(CarProviderData.KEY_SHOTCUT_NAVI_NUM, "4008001001");
		values.put(CarProviderData.KEY_SHOTCUT_EMERGENCY_NUM1, "4008001002");
		values.put(CarProviderData.KEY_SHOTCUT_EMERGENCY_NUM2, "4008001003");
		values.put(CarProviderData.KEY_SHOTCUT_EMERGENCY_TIME, "30");
		values.put(CarProviderData.KEY_SHOTCUT_SERVICE_NUM, "4008001004");
		Uri insertUri = resolver.insert(phoneInfoUri, values);
	}

	private void queryNuminfoProvider() {
		Cursor cursor = resolver.query(phoneInfoUri, null, null, null, null);
		if(cursor != null && cursor.moveToNext()){
			String recuteNum = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_SHOTCUT_RECUTE_NUM));
			int recuteTime = cursor.getInt(cursor.getColumnIndex(CarProviderData.KEY_SHOTCUT_RECUTE_TIME));
			String NaviNum = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_SHOTCUT_NAVI_NUM));
			String emergencyNum1 = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_SHOTCUT_EMERGENCY_NUM1));
			String emergencyNum2 = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_SHOTCUT_EMERGENCY_NUM2));
			int emergencyTime = cursor.getInt(cursor.getColumnIndex(CarProviderData.KEY_SHOTCUT_EMERGENCY_TIME));
			String serviceNum = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_SHOTCUT_SERVICE_NUM));
			
			Log.i(TAG, "查询numinfo结果为：recuteNum = " + recuteNum + ", recuteTime = " + recuteTime 
					+ ", NaviNum = " + NaviNum + ", emergencyNum1 = " + emergencyNum1 + ", emergencyNum2 = " + emergencyNum2
					+ ", emergencyTime = " + emergencyTime + ", serviceNum = " + serviceNum);
			}
		else{
			Log.i(TAG, "无查询结果");
		}
	}
	
	private void updateNuminfoProvider() {
		ContentValues values = new ContentValues();
		values.put(CarProviderData.KEY_SHOTCUT_EMERGENCY_NUM1, "10086");
		values.put(CarProviderData.KEY_SHOTCUT_EMERGENCY_TIME, 35);
//		values.put(CarProviderData.KEY_SN, "test_update_sn");
//		values.put(CarProviderData.KEY_IMEI, "test_update_imei");
		values.put(CarProviderData.KEY_SHOTCUT_SERVICE_NUM, "10086");
		int row = resolver.update(phoneInfoUri, values, null, null);
	}
	
	private void insertPermissionProvider() {
		Cursor cursor = resolver.query(permissionUri, null, null, null, null);
		if(cursor != null && cursor.moveToNext()){
			Log.i(TAG, "已经存在，请调用update方法");
			return;
		}
		ContentValues values = new ContentValues();
		values.put(CarProviderData.KEY_PERMISSION, 0);
		Uri insertUri = resolver.insert(permissionUri, values);
	}

	private void queryPermissionProvider() {
		Cursor cursor = resolver.query(permissionUri, null, null, null, null);
		if(cursor != null && cursor.moveToNext()){
			int permission = cursor.getInt(cursor.getColumnIndex(CarProviderData.KEY_PERMISSION));
			
			Log.i(TAG, "查询permission结果为：permission = " + permission);
			}
		else{
			Log.i(TAG, "无查询结果");
		}
	}
	
	private void updatePermissionProvider() {
		ContentValues values = new ContentValues();
		values.put(CarProviderData.KEY_PERMISSION, 1);
		int row = resolver.update(permissionUri, values, null, null);
	}
}
