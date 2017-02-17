/**
 * 
 */
package com.yc.external;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.zhcar.apprecord.RecordManager;
import com.zhcar.data.AppUseRecord;
import com.zhcar.data.GlobalData;
import com.zhcar.provider.CarProviderData;
import com.zhcar.utils.GPRSManager;
import com.zhcar.utils.Utils;

/**
 * @author YC
 * @time 2016-7-15 下午3:12:41
 * TODO:处理音乐Post过来的信息
 */
public class PostFromZUI implements IPostFromClient {

	private final Uri carInfoUri = Uri.parse("content://cn.com.semisky.carProvider/carInfo");
	
	private static final String TAG = "PostFromZUI";
	private Context mContext;

	private static Map<String, AppUseRecord> appRecords;	
	
	@Override
	public boolean postInfo(int cmd, String val) {
		switch (cmd) {
		case Cmd.POST_RECORD_ENTER_APP:
			if (val != null){
				HashMap<String, String> map = new JsonHelper().json2Map(val);
				if (map != null){
					String appName = map.get(Cmd.POST_RECORD_KEY_APPNAME);
					String vin = queryVIN();
					if (vin != null){
						AppUseRecord appUseRecord = new AppUseRecord(vin, "SUCCESS", "HMI");
						appUseRecord.setAppName(appName);
						appUseRecord.setStartTime(getCurTime());
						appRecords.put(appName, appUseRecord);
					}
				}
			}
			return true;
		case Cmd.POST_RECORD_EXIT_APP:
			if (val != null){
				HashMap<String, String> map = new JsonHelper().json2Map(val);
				if (map != null){
					String appName = map.get(Cmd.POST_RECORD_KEY_APPNAME);
					AppUseRecord useRecord = appRecords.get(appName);
					if (useRecord != null){
						appRecords.remove(appName);
						useRecord.setEndTime(getCurTime());
						RecordManager.getInstance().HttpPostAppRecord(useRecord);
					}
				}
			}
			return true;
		case Cmd.POST_RECORD_APPPROGRAM:
			if (val != null){
				HashMap<String, String> map = new JsonHelper().json2Map(val);
				if (map != null){
					String appName = map.get(Cmd.POST_RECORD_KEY_APPNAME);
					String appProgram = map.get(Cmd.POST_RECORD_KEY_APPPROGRAM);
					if (Utils.isEmpty(appName) && Utils.isEmpty(appProgram)){
						AppUseRecord useRecord = appRecords.get(appName);
						if (useRecord != null){
							appRecords.remove(appName);
							useRecord.setAppProgram(appProgram);
							appRecords.put(appName, useRecord);
						}
					}
				}
			}
			return true;
			
		case Cmd.POST_RECORD_APPPROGRAMAUTH:
			if (val != null){
				HashMap<String, String> map = new JsonHelper().json2Map(val);
				if (map != null){
					String appName = map.get(Cmd.POST_RECORD_KEY_APPNAME);
					String appProgramAuth = map.get(Cmd.POST_RECORD_KEY_APPPROGRAMAUTH);
					if (Utils.isEmpty(appName) && Utils.isEmpty(appProgramAuth)){
						AppUseRecord useRecord = appRecords.get(appName);
						if (useRecord != null){
							appRecords.remove(appName);
							useRecord.setAppProgramAuth(appProgramAuth);
							appRecords.put(appName, useRecord);
						}
					}
				}
			}
			return true;
		case Cmd.POST_RECORD_APPSTATUS:
			if (val != null){
				HashMap<String, String> map = new JsonHelper().json2Map(val);
				if (map != null){
					String appName = map.get(Cmd.POST_RECORD_KEY_APPNAME);
					String appStatus = map.get(Cmd.POST_RECORD_KEY_APPSTATUS);
					if (Utils.isEmpty(appName) && Utils.isEmpty(appStatus)){
						AppUseRecord useRecord = appRecords.get(appName);
						if (useRecord != null){
							appRecords.remove(appName);
							useRecord.setAppStatus(appName);
							appRecords.put(appName, useRecord);
						}
					}
				}
			}
			return true;
		/*case Cmd.POST_CAN_OK:
			Log.i(TAG, "CAN is OK");
			GPRSManager gprsManager = new GPRSManager(mContext);
			if (!gprsManager.isEnable()){
				gprsManager.turnOn();
			}
			Utils.sendBroadcast(mContext, GlobalData.ACTION_3G, GlobalData.KEY_3G_CONTROL , "on");
			if (gprsManager.isNetWorkValuable()){
				Utils.sendBroadcast(mContext, GlobalData.ACTION_3G, GlobalData.KEY_3G_STATE, "on");
			}
			else{
				Utils.sendBroadcast(mContext, GlobalData.ACTION_3G, GlobalData.KEY_3G_STATE, "off");
			}
			return true;*/
		}
		return false;
	}
	
	public PostFromZUI(Context context){
		mContext = context;
		appRecords = new HashMap<String, AppUseRecord>();
		ConnManager.getInstance().register(this);
	}
	
	@Override
	protected void finalize() throws Throwable {
		ConnManager.getInstance().unregister(this);
		super.finalize();
	}
	
	
	private String getCurTime(){
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");       
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间       
		String str = formatter.format(curDate);
		return str;
	}
	
	private String queryVIN() {
		ContentResolver resolver = mContext.getContentResolver();
		Cursor cursor = resolver.query(carInfoUri, null, null, null, null);
		if(cursor != null && cursor.moveToNext()){
			String vin = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_VIN));
			cursor.close();
			cursor = null;
			return vin;
		}
		else{
			Log.i(TAG, "无VIN");
			if (cursor != null){
				cursor.close();
				cursor = null;
			}
			return null;
		}
	}
	
	private String queryToken() {
		ContentResolver resolver = mContext.getContentResolver();
		Cursor cursor = resolver.query(carInfoUri, null, null, null, null);
		if(cursor != null && cursor.moveToNext()){
			String token = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_CARINFO_TOKEN));
			cursor.close();
			cursor = null;
			return token;
		}
		else{
			Log.i(TAG, "无token");
			if (cursor != null){
				cursor.close();
				cursor = null;
			}
			return null;
		}
	}

}
