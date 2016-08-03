/**
 * 
 */
package com.zhcar.readnumber;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import jxl.Sheet;
import jxl.Workbook;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.zhcar.base.BaseApplication;
import com.zhcar.provider.CarProviderData;


/**
 * @author YC
 * @time 2016-8-1 下午4:33:18
 * TODO:读取5码
 */
public class ReadExcelManager implements IUSBStateChange{

	private static final String TAG = "ReadExcelManager";
	
	private static final String FILE_NAME = "CodeList.xls";

	private static ReadExcelManager instance;

	/**是否在读取中*/
	private boolean isReading;
	/**正在读取的文件路径*/
	private String readPath;
	/**请求退出读*/
	private boolean bRequestExit;

	public static ReadExcelManager getInstance() {
		if (instance == null) {
			synchronized (ReadExcelManager.class) {
				if (instance == null) {
					instance = new ReadExcelManager();
				}
			}
		}
		return instance;
	}
	
	private ReadExcelManager(){
		
	}
	
	@Override
	public void onUnmounted(String devPath) {
		if (isReading){
			if (readPath.contains(devPath)){
				bRequestExit = true;
				Log.i(TAG, "request exit read");
			}
		}
	}

	@Override
	public void onMounted(String devPath) {
		if (isReading){
			Log.i(TAG, "isReading");
			return;
		}
		
		String filePath = devPath + FILE_NAME;
		if (new File(filePath).exists()){
			readExcel(filePath);
		}
	}
	
	/**
	 * 获取3G模块的IMEI
	 */
	private String getMEID(){
		String MEID = ((TelephonyManager) BaseApplication.getInstanse().getSystemService(Context.TELEPHONY_SERVICE))
				.getDeviceId();
		Log.i(TAG, "get raw meid = " + MEID);
		if (!TextUtils.isEmpty(MEID)){
			int start  = MEID.indexOf("A");
			MEID = MEID.substring(start);
		}
		return MEID;
	}
	
	private void readExcel(final String path){
		final String meid = getMEID();
		Log.i(TAG, "get meid = " + meid);
		if (meid == null){
			Log.e(TAG, "invalued meid");
			return;
		}
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				isReading = true;
				readPath = path;
				bRequestExit = false;
				
				try {
					InputStream is = new FileInputStream(path);// "mnt/sdcard/CodeList.xls"
					Workbook book = Workbook.getWorkbook(is);
					int num = book.getNumberOfSheets();
					// 获得第一个工作表对象
					Sheet sheet = book.getSheet(0);
					// 行数 排
					int Rows = sheet.getRows();
					for (int i = 0; i < Rows; i++) {
						if (bRequestExit){
							Log.i(TAG, "exit read");
							break;
						}
						
						if ("".equals(sheet.getCell(0, i).getContents())) {
							Log.i(TAG, "read end, no match");
							break;
						} else {
							
							String MEID = sheet.getCell(0, i).getContents();
							if (meid.equals(MEID)){
								String AKEY = sheet.getCell(1, i).getContents();
								String IMSI = sheet.getCell(2, i).getContents();
								String USER = sheet.getCell(3, i).getContents();
								String PASSWORD = sheet.getCell(4, i).getContents();
								String ESN = sheet.getCell(5, i).getContents();
								String MDN = sheet.getCell(6, i).getContents();
								String ICCID = sheet.getCell(7, i).getContents();
								String SN = sheet.getCell(8, i).getContents();
								String SKEY = sheet.getCell(9, i).getContents();
								
								ContentValues values = new ContentValues();
								values.put(CarProviderData.KEY_CARINFO_MEID, MEID);
								values.put(CarProviderData.KEY_CARINFO_AKEY, AKEY);
								values.put(CarProviderData.KEY_CARINFO_IMSI, IMSI);
								values.put(CarProviderData.KEY_CARINFO_USER, USER);
								values.put(CarProviderData.KEY_CARINFO_PASSWORD, PASSWORD);
								values.put(CarProviderData.KEY_CARINFO_ESN, ESN);
								values.put(CarProviderData.KEY_CARINFO_MDN, MDN);
								values.put(CarProviderData.KEY_CARINFO_ICCID, ICCID);
								values.put(CarProviderData.KEY_CARINFO_SN, SN);
								values.put(CarProviderData.KEY_CARINFO_SKEY, SKEY);
								Log.i(TAG, "read ok, values = " + values.toString());
								updateCarInfo(values);
								Log.i(TAG, "read ok, update");
								break;
							}
						}
					}
					book.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				isReading = false;
			}
		}).start();
	}
	
	private void updateCarInfo(ContentValues values) {

		ContentResolver resolver = BaseApplication.getInstanse().getContentResolver();
		// 可以只选其中一个
		Cursor cursor = resolver.query(CarProviderData.URI_CARINFO, null, null,
				null, null);
		if (cursor != null && cursor.moveToNext()) {
			int row = resolver.update(CarProviderData.URI_CARINFO, values,
					null, null);
			Log.d(TAG, "更新信息");
		} else {
			resolver.insert(CarProviderData.URI_CARINFO, values);
			Log.d(TAG, "信息添加完毕");
		}
		if (cursor != null){
			cursor.close();
			cursor = null;
		}
	}
}
