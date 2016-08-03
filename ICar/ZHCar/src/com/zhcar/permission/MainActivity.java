package com.zhcar.permission;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.zhcar.R;
import com.zhcar.apprecord.RecordManager;
import com.zhcar.carflow.CarFlowManager;
import com.zhcar.carflow.GetFlowLoc;
import com.zhcar.data.AppUseRecord;
import com.zhcar.data.GlobalData;
import com.zhcar.dialog.DialogManager;
import com.zhcar.provider.CarProviderData;
import com.zhcar.utils.GPRSManager;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";

	private TextView mContent;
	
	private ContentResolver resolver;
	private final Uri carInfoUri = Uri.parse("content://cn.com.semisky.carProvider/carInfo");
	private final Uri phoneInfoUri = Uri.parse("content://cn.com.semisky.carProvider/phonenum");
	private final Uri permissionUri = Uri.parse("content://cn.com.semisky.carProvider/permission");
	private final Uri flowUri = Uri.parse("content://cn.com.semisky.carProvider/flowInfo");

	private String VIN;
	private String SN;
	private String IMEI;
	private String ICCID;
	private String TOKEN;
	
	private GPRSManager mGprsManager;
	
	private GetFlowLoc getFlowLoc = new GetFlowLoc();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContent = (TextView) findViewById(R.id.content);
		mGprsManager = new GPRSManager(this);
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
		case R.id.delete:
			deleteCarinfoProvider();
			break;
		case R.id.showdialog:
			DialogManager.getInstance().showCarFlowDialog(this, "您的当月可用流量还剩400M，可点击T服务购买按钮购买相关套餐增加流量。*如流量超限造成断网，请连接WIFI或登录车主网站购买。");
			break;
		case R.id.postrecord:
//			if (ICCID != null && !ICCID.isEmpty() && TOKEN != null && !TOKEN.isEmpty())
			{
				SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");       
				Date curDate = new Date(System.currentTimeMillis());//获取当前时间       
				String str = formatter.format(curDate);
				RecordManager.getInstance().HttpPostAppRecord(new AppUseRecord("1", "2", "3", "4", "5","2016-07-22 10:00:00", str, "6"));
			}
			break;
		case R.id.switch_gprs:
			if (mGprsManager.isEnable()){
				mGprsManager.turnOff();
			}
			else
			{
				mGprsManager.turnOn();
			}
			break;
		case R.id.getflow:
//			GetFlowLoc getFlowLoc = new GetFlowLoc();
			if (ICCID != null && !ICCID.isEmpty() && TOKEN != null && !TOKEN.isEmpty()){
				/*getFlowLoc.SetInfo(ICCID, TOKEN);
				getFlowLoc.SetHttpStatusCallback(new HttpStatusCallback() {
					
					@Override
					public void onStatus(int status) {
						Log.i(TAG, "get Http status = " + status);
						if (status == HttpStatusCallback.RESULT_SUCCESS){
							FlowInfoBean flowInfo = getFlowLoc.GetFlowInfo();
							Log.i(TAG, "getFlowInfo = " + flowInfo);
							GlobalData.flowInfo.setSurplusFlow(flowInfo.getSurplusFlow());
							GlobalData.flowInfo.setUseFlow(flowInfo.getUseFlow());
							GlobalData.flowInfo.setCurrFlowTotal(flowInfo.getCurrFlowTotal());
							updateFlowInfoProvider();
						}
					}
				});
				getFlowLoc.Refresh();*/
				CarFlowManager.getInstance(this).HttpRequestCarFlow(ICCID, TOKEN);
			}
			break;
		case R.id.getimei:
			getIMEI();
			break;
		default:
			break;
		}
	}
	
	/**
	 * 获取3G模块的IMEI
	 */
	private void getIMEI(){
		String Imei = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE))
				.getDeviceId();
		Log.i(TAG, "getIMEI imei = " + Imei);
	}

	private void insertCarinfoProvider() {
		Cursor cursor = resolver.query(carInfoUri, null, null, null, null);
		if(cursor != null && cursor.moveToNext()){
			Log.i(TAG, "已经存在，请调用update方法");
			return;
		}
		ContentValues values = new ContentValues();
		values.put(CarProviderData.KEY_CARINFO_VIN, "test_vin");
		values.put(CarProviderData.KEY_CARINFO_ICCID, "test_iccid");
		values.put(CarProviderData.KEY_CARINFO_SN, "test_sn");
		values.put(CarProviderData.KEY_CARINFO_MEID, "test_imei");
		values.put(CarProviderData.KEY_CARINFO_TOKEN, "test_token");
		Uri insertUri = resolver.insert(carInfoUri, values);
	}

	private void queryCarinfoProvider() {
		Cursor cursor = resolver.query(carInfoUri, null, null, null, null);
		if(cursor != null && cursor.moveToNext()){
			VIN = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_VIN));
			SN = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_SN));
			IMEI = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_MEID));
			ICCID = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_ICCID));
			TOKEN = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_TOKEN));
			
			Log.i(TAG, "查询carinfo结果为：vin = " + VIN + ", sn = " + SN 
					+ ", imei = " + IMEI + ", iccid = " + ICCID + ", token = " + TOKEN);
			}

		if (cursor != null){
			cursor.close();
			cursor = null;
			Log.i(TAG, "无查询结果");
		}
		else{
			Log.i(TAG, "无查询结果为null");
		}
	}
	
	private void updateCarinfoProvider() {
		ContentValues values = new ContentValues();
		values.put(CarProviderData.KEY_CARINFO_VIN, "test_update_vin" + Math.random());
		values.put(CarProviderData.KEY_CARINFO_ICCID, "test_update_iccid" + Math.random());
		int row = resolver.update(carInfoUri, values, null, null);
	}
	
	private void deleteCarinfoProvider() {
		resolver.delete(carInfoUri, null, null);
	}
	
	private void insertNuminfoProvider() {
		Cursor cursor = resolver.query(phoneInfoUri, null, null, null, null);
		if(cursor != null && cursor.moveToNext()){
			Log.i(TAG, "已经存在，请调用update方法");
			return;
		}
		ContentValues values = new ContentValues();
		values.put(CarProviderData.KEY_PHONENUM_RECUTE_NUM, "4008001000");
		values.put(CarProviderData.KEY_PHONENUM_RECUTE_TIME, "20");
		values.put(CarProviderData.KEY_PHONENUM_NAVI_NUM, "4008001001");
		values.put(CarProviderData.KEY_PHONENUM_EMERGENCY_NUM1, "4008001002");
		values.put(CarProviderData.KEY_PHONENUM_EMERGENCY_NUM2, "4008001003");
		values.put(CarProviderData.KEY_PHONENUM_EMERGENCY_TIME, "30");
		values.put(CarProviderData.KEY_PHONENUM_KAIYI_NUM, "4008001004");
		Uri insertUri = resolver.insert(phoneInfoUri, values);
	}

	private void queryNuminfoProvider() {
		Cursor cursor = resolver.query(phoneInfoUri, null, null, null, null);
		if(cursor != null && cursor.moveToNext()){
			String recuteNum = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_PHONENUM_RECUTE_NUM));
			int recuteTime = cursor.getInt(cursor.getColumnIndex(CarProviderData.KEY_PHONENUM_RECUTE_TIME));
			String NaviNum = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_PHONENUM_NAVI_NUM));
			String emergencyNum1 = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_PHONENUM_EMERGENCY_NUM1));
			String emergencyNum2 = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_PHONENUM_EMERGENCY_NUM2));
			int emergencyTime = cursor.getInt(cursor.getColumnIndex(CarProviderData.KEY_PHONENUM_EMERGENCY_TIME));
			String serviceNum = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_PHONENUM_KAIYI_NUM));
			
			Log.i(TAG, "查询numinfo结果为：recuteNum = " + recuteNum + ", recuteTime = " + recuteTime 
					+ ", NaviNum = " + NaviNum + ", emergencyNum1 = " + emergencyNum1 + ", emergencyNum2 = " + emergencyNum2
					+ ", emergencyTime = " + emergencyTime + ", serviceNum = " + serviceNum);
			}
		if (cursor != null){
			cursor.close();
			cursor = null;
			Log.i(TAG, "无查询结果");
		}
		else{
			Log.i(TAG, "无查询结果为null");
		}
	}
	
	private void updateNuminfoProvider() {
		ContentValues values = new ContentValues();
		values.put(CarProviderData.KEY_PHONENUM_EMERGENCY_NUM1, "10086");
		values.put(CarProviderData.KEY_PHONENUM_EMERGENCY_TIME, 35);
//		values.put(CarProviderData.KEY_SN, "test_update_sn");
//		values.put(CarProviderData.KEY_IMEI, "test_update_imei");
		values.put(CarProviderData.KEY_PHONENUM_KAIYI_NUM, "10086");
		int row = resolver.update(phoneInfoUri, values, null, null);
	}
	
	private boolean insertPermissionProvider() {
		Cursor cursor = resolver.query(permissionUri, null, null, null, null);
		if(cursor != null && cursor.moveToNext()){
			Log.i(TAG, "已经存在，请调用update方法");
			return false;
		}
		ContentValues values = new ContentValues();
		values.put(CarProviderData.KEY_PERMISSION_ABLE, 0);
		Uri insertUri = resolver.insert(permissionUri, values);
		return true;
	}

	private void queryPermissionProvider() {
		/*Cursor cursor = resolver.query(permissionUri, null, null, null, null);
		if(cursor != null && cursor.moveToNext()){
			int permission = cursor.getInt(cursor.getColumnIndex(CarProviderData.KEY_PERMISSION_ABLE));
			
			Log.i(TAG, "查询permission结果为：permission = " + permission);
			}
		else{
			Log.i(TAG, "无查询结果");
		}*/
	}
	
	private void updatePermissionProvider() {
		ContentValues values = new ContentValues();
		values.put(CarProviderData.KEY_PERMISSION_ABLE, 1);
		int row = resolver.update(permissionUri, values, null, null);
	}
	
	private void updateFlowInfoProvider() {
		ContentValues values = new ContentValues();
		values.put(CarProviderData.KEY_FLOWINFO_CURRFLOWTATAL, GlobalData.flowInfo.getCurrFlowTotal());
		values.put(CarProviderData.KEY_FLOWINFO_USEFLOW, GlobalData.flowInfo.getUseFlow());
		values.put(CarProviderData.KEY_FLOWINFO_SURPLUSFLOW, GlobalData.flowInfo.getSurplusFlow());
		Cursor cursor = resolver.query(flowUri, null, null, null, null);
		if(cursor != null && cursor.moveToNext()){
			int row = resolver.update(flowUri, values, null, null);
		}
		else{
			resolver.insert(flowUri, values);
		}
		if (cursor != null){
			cursor.close();
			cursor = null;
			Log.i(TAG, "无查询结果");
		}
		else{
			Log.i(TAG, "无查询结果为null");
		}
	}
	
}
