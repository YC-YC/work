/**
 * 
 */
package com.zhonghong.mediascanner.saver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.os.FileUtils;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.zhonghong.mediascanerlib.config.Config;
import com.zhonghong.mediascanner.utils.FileUtil;
import com.zhonghong.mediascanner.utils.InitThread;
import com.zhonghong.mediascanner.utils.L;

/**
 * @author YC
 * @time 2016-12-21 上午10:04:48
 * TODO:文件存储方式
 */
public class FileRecordDevice implements IRecordDevice{

	private static final String START_TAG = "<";
	private static final String END_TAG = ">";
	private static final String DEVICES_INFO_FILE = "devices_info.cfg";
	private static final String TAG = "FileRecordDevice";
	private boolean bInit = false;
	
	/**存储设备信息*/
	private List<String> mDevices = new ArrayList<String>();
	
	public FileRecordDevice(){
		new InitThread() {
			@Override
			protected void loadData() {
				L.startTime("加载设备信息");
				init();
				bInit = true;
				L.endUseTime("加载设备信息");
			}
		}.start();
	}
	
	@Override
	public synchronized void updateDevice(String uuid) {
		L.i(TAG, "updateDevice uuid = " + uuid);
		waitForInit();
		if (TextUtils.isEmpty(uuid)){
			L.i(TAG, "null uuid, ignore");
			return;
		}

		if (mDevices.contains(uuid)){
			if (uuid.equals(mDevices.get(mDevices.size()-1))){
				L.i(TAG, "same uuid");
				return;
			}
			mDevices.remove(uuid);
		}
		mDevices.add(uuid);
		if (Config.LIMITED_SAVE_DEVICE_NUM){
			while(mDevices.size() > Config.SAVER_DEVICE_NUM){
				try {
					FileUtil.deleteFile(FileUtil.getDBFile(mDevices.get(0)));
					FileUtil.deleteFile(FileUtil.getDBFile(mDevices.get(0))+"-journal");
				} catch (Exception e) {
					e.printStackTrace();
				}
				mDevices.remove(0);
			}
		}

		writeDevicesInfo();
	}
	
	private void waitForInit(){
		while(!bInit){
			synchronized (this) {
				SystemClock.sleep(5);
				Log.i(TAG, "wait for init");
			}
		}
	}
	
	/**
	 * 初始化存储文件数据
	 */
	private void init() {
		if (FileUtil.createOrExistsFile(Config.DIR_PATH+DEVICES_INFO_FILE)){
			readDevicesInfo();
		}
	}
	
	/**
	 * 从文件读取信息
	 */
	private void readDevicesInfo(){
		File file = new File(Config.DIR_PATH+DEVICES_INFO_FILE);
		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader reader = new BufferedReader(fileReader);
			String line = "";
			try {
				while((line = reader.readLine()) != null){
//					L.i(TAG, "read line = " + line);
					if (line.startsWith(START_TAG)){
						int endIndex = line.indexOf(END_TAG);
						if (endIndex > 0){
							String subString = line.substring(START_TAG.length(), endIndex);
							if (!TextUtils.isEmpty(subString)){
								L.i(TAG, "get device = " +  subString);
								mDevices.add(subString);
							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			finally{
				try {
					reader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					fileReader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * 将信息写到文件
	 */
	private void writeDevicesInfo(){
		StringBuilder builder = new StringBuilder();
		Iterator<String> iterator = mDevices.iterator();
		while(iterator.hasNext()){
			String next = iterator.next();
			builder.append(START_TAG + next + END_TAG + "\n");
		}
		
		if (builder.length() > 0){
		File file = new File(Config.DIR_PATH+DEVICES_INFO_FILE);
		try {
			FileOutputStream outputStream = new FileOutputStream(file);
			outputStream.write(builder.toString().getBytes());
			FileUtils.sync(outputStream);
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		}
	}
	
}
