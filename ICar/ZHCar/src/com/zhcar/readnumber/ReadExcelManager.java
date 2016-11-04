/**
 * 
 */
package com.zhcar.readnumber;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.zhcar.R;
import com.zhcar.base.BaseApplication;
import com.zhcar.data.GlobalData;
import com.zhcar.provider.CarProviderData;
import com.zhcar.utils.Utils;


/**
 * @author YC
 * @time 2016-8-1 下午4:33:18
 * TODO:读取5码
 */
public class ReadExcelManager implements IUSBStateChange{

	private static final String TAG = "ReadExcelManager";
	
	private static final String FILE_NAME = "CodeList.xls";
	/**测试环境*/
//	private static final String TEST_MEID = "A100001FA0D7DA";
//	private static final String TEST_VIN = "LVTDB11B1TS000004";
	
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
			if (start >= 0){
				MEID = MEID.substring(start);
			}
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
//							Log.i(TAG, "get Cell 0 = " + MEID);
							if (meid.equals(MEID)){
								String AKEY = sheet.getCell(1, i).getContents().trim();
								String IMSI = sheet.getCell(2, i).getContents().trim();
								String USER = sheet.getCell(3, i).getContents().trim();
								String PASSWORD = sheet.getCell(4, i).getContents().trim();
								String ESN = sheet.getCell(5, i).getContents().trim();
								String MDN = sheet.getCell(6, i).getContents().trim();
								String ICCID = sheet.getCell(7, i).getContents().trim();
//								String PRLNAME = sheet.getCell(8, i).getContents();
								String SN = sheet.getCell(9, i).getContents().trim();
								String SKEY = sheet.getCell(10, i).getContents().trim();
								
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
//								Log.i(TAG, "read ok, update");
								Utils.ToastThread(Utils.getResourceString(R.string.read_OK));
								Utils.sendBroadcast(BaseApplication.getInstanse(), GlobalData.ACTION_ZHCAR_TO_ZUI, GlobalData.KEY_UPDATE_FIVE_NUMBER, "true");
								File file = new File(path);
								updateXls(file.getParent(), SN, MEID);
								
								break;
							}
						}
					}
					book.close();
					is.close();
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
	
private boolean updateXls(String root, String sn, String meid){
		
		File file = new File(root, getCurDate()+".xls");
		Workbook workbook = null;
		WritableWorkbook book = null;
		try {
			if (!file.exists()){
				book = Workbook.createWorkbook(file);
			}
			else{
				workbook = Workbook.getWorkbook(file);
				book = Workbook.createWorkbook(file, workbook);
			}
			WritableSheet sheet = null;
			try {
				sheet = book.getSheet(0);
				Log.i(TAG, "getSheet");
			} catch (IndexOutOfBoundsException e) {
				Log.i(TAG, "createSheet");
				sheet = book.createSheet("MEID-SN表", 0);
			}
			int Rows = sheet.getRows();
			Log.i(TAG, "Sheet Row = " + Rows);
			boolean bExist = false;
			for (int i = 0; i < Rows; i++){
				String MEID = sheet.getCell(0, i).getContents();
				if (meid.equals(MEID)){
					Log.i(TAG, "exit meid");
					Utils.ToastThread("已经存在于Excle表");
					bExist = true;
				}
			}
			if (!bExist){
				Log.i(TAG, "write sn = " + sn + ", meid = " + meid);
				sheet.addCell(new Label(0, Rows, meid));
				sheet.addCell(new Label(1, Rows, sn));
				sheet.addCell(new Label(2, Rows, getCurTime()));
				Log.i(TAG, "write ok");
				Utils.ToastThread("更新到Excle表");
			}
			book.write();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			if (workbook != null){
				workbook.close();
			}
			if (book != null){
				try {
					book.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	private String getCurTime(){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String str = formatter.format(curDate);
		return str;
	}

	private String getCurDate(){
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String str = formatter.format(curDate);
		return str;
	}
}
