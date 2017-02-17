package com.zhcar.permission;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.CLTX.outer.newinterface.PhoneUtil;
import com.zhcar.R;
import com.zhcar.apprecord.RecordManager;
import com.zhcar.carflow.CarFlowManager;
import com.zhcar.carflow.GetFlowLoc;
import com.zhcar.data.AppUseRecord;
import com.zhcar.data.GlobalData;
import com.zhcar.ecall.ECallManager;
import com.zhcar.provider.CarProviderData;
import com.zhcar.readnumber.VersionActivity;
import com.zhcar.utils.DialogManager;
import com.zhcar.utils.GPRSManager;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";

	private TextView mContent;
	private EditText mInputNum;
	private EditText mInputDialDtmfNum;
	private Button mPermission;
	
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
		mInputNum = (EditText) findViewById(R.id.inputnum);
		mInputDialDtmfNum = (EditText) findViewById(R.id.inputdialdtmfnum);
		mPermission = (Button) findViewById(R.id.getpermission);
		mGprsManager = new GPRSManager(this);
		resolver = getContentResolver();
		resolver.registerContentObserver(carInfoUri, true, new CarInfoObserver(new Handler()) {});
		resolver.registerContentObserver(phoneInfoUri, true, new PhoneInfoObserver(new Handler()) {});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mPermission.setText(GlobalData.bPermissionStatus?"鉴权成功":"鉴权不成功");
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

	private boolean bCarRun = false;
	public void doClick(View view) {
		switch (view.getId()) {
		case R.id.query:
	    	queryCarinfoProvider();
	    	queryNuminfoProvider();
	    	queryPermissionProvider();
			break;
		case R.id.insert:
//			insertCarinfoProvider();
//			insertNuminfoProvider();
//			insertPermissionProvider();
			break;
		case R.id.update:
//			updateCarinfoProvider();
			updateNuminfoProvider();
//			updatePermissionProvider();
			break;
		case R.id.delete:
			deleteCarinfoProvider();
			break;
		case R.id.showdialog:
			DialogManager.getInstance().showCarFlowDialog(this, "您的当月可用流量还剩400M，可点击T服务购买按钮购买相关套餐增加流量。*如流量超限造成断网，请连接WIFI或登录车主网站购买。");
			break;
		case R.id.shownoflowdialog:
			DialogManager.getInstance().showNormalDialog(this, R.layout.dialog_noflow);
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
		case R.id.callemergency:
			ECallManager.getInstance().startECall();
			break;
		case R.id.openversion:
			startActivity(new Intent(this, VersionActivity.class));
			break;
		case R.id.getnetwork:
			boolean valilable = new GPRSManager(this).isNetWorkValuable();
			Log.i(TAG, "is network valilable = " + valilable);
			Toast.makeText(this, valilable ? "网络可用":"网络不可用", 100).show();
			break;
		case R.id.dial:
			{
				String num = mInputNum.getText().toString().trim();
				if (num != null){
					PhoneUtil.getInstance().dial(num);
				}
				else{
					Toast.makeText(this, "号码为空", Toast.LENGTH_LONG).show();
				}
			}
			break;
		case R.id.hangup:
			PhoneUtil.getInstance().hangUp();
			break;
		case R.id.dialdtmf:
		{
			String num = mInputDialDtmfNum.getText().toString().trim();
			PhoneUtil.getInstance().dialDtmf(num);
//			PhoneUtil.getInstance().playDtmfTone('1', false);
		}
		break;
		case R.id.sendpermissionbroadcast:
			sendBroadcast("com.tima.Permission", "status", "succeed");
			break;
		case R.id.sendcarrunbroadcast:
			bCarRun = !bCarRun;
			sendBroadcast("com.zhonghong.zuiserver.BROADCAST", "CARRUN_INFO", bCarRun ? "1":"0");
			Toast.makeText(this, bCarRun? "行车中":"行车停止", Toast.LENGTH_LONG).show();
			break;
		case R.id.getnetworktype:
		TelephonyManager i = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//		i.listen(new PhoneStateListener(), events)
		Toast.makeText(this, "网络类型为：" + i.getNetworkType() + " -- > " + getNetworkClass(i.getNetworkType()), Toast.LENGTH_LONG).show();
		break;
		default:
			break;
		}
	}
	
	/** Unknown network class. {@hide} */
	private static final int NETWORK_CLASS_UNKNOWN = 0;
	/** Class of broadly defined "2G" networks. {@hide} */
	private static final int NETWORK_CLASS_2_G = 1;
	/** Class of broadly defined "3G" networks. {@hide} */
	private static final int NETWORK_CLASS_3_G = 2;
	/** Class of broadly defined "4G" networks. {@hide} */
	private static final int NETWORK_CLASS_4_G = 3;
	
	private static int getNetworkClass(int networkType) {
	    switch (networkType) {
	        case TelephonyManager.NETWORK_TYPE_GPRS:
	        case TelephonyManager.NETWORK_TYPE_EDGE:
	        case TelephonyManager.NETWORK_TYPE_CDMA:
	        case TelephonyManager.NETWORK_TYPE_1xRTT:
	        case TelephonyManager.NETWORK_TYPE_IDEN:
	    return NETWORK_CLASS_2_G;
	        case TelephonyManager.NETWORK_TYPE_UMTS:
	        case TelephonyManager.NETWORK_TYPE_EVDO_0:
	        case TelephonyManager.NETWORK_TYPE_EVDO_A:
	        case TelephonyManager.NETWORK_TYPE_HSDPA:
	        case TelephonyManager.NETWORK_TYPE_HSUPA:
	        case TelephonyManager.NETWORK_TYPE_HSPA:
	        case TelephonyManager.NETWORK_TYPE_EVDO_B:
	        case TelephonyManager.NETWORK_TYPE_EHRPD:
	        case TelephonyManager.NETWORK_TYPE_HSPAP:
	    return NETWORK_CLASS_3_G;
	        case TelephonyManager.NETWORK_TYPE_LTE:
	    return NETWORK_CLASS_4_G;
	        default:
	    return NETWORK_CLASS_UNKNOWN;
	    }
	}
	
	private void sendBroadcast(String action, String key, String val){
		Intent intent = new Intent();
		intent.setAction(action);
		intent.putExtra(key, val);
		sendBroadcast(intent);
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
		values.put(CarProviderData.KEY_PHONENUM_RECUTE_NUM, "10000");
		values.put(CarProviderData.KEY_PHONENUM_RECUTE_TIME, 20);
		values.put(CarProviderData.KEY_PHONENUM_NAVI_NUM, "10000");
		values.put(CarProviderData.KEY_PHONENUM_EMERGENCY_NUM1, "15820201347");
		values.put(CarProviderData.KEY_PHONENUM_EMERGENCY_NUM2, "10000");
		values.put(CarProviderData.KEY_PHONENUM_EMERGENCY_TIME, 30);
		values.put(CarProviderData.KEY_PHONENUM_KAIYI_NUM, "10086");
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
		values.put(CarProviderData.KEY_PHONENUM_EMERGENCY_NUM1, "15820201347");
		values.put(CarProviderData.KEY_PHONENUM_EMERGENCY_TIME, 20);
		values.put(CarProviderData.KEY_PHONENUM_EMERGENCY_NUM2, "10000");
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
