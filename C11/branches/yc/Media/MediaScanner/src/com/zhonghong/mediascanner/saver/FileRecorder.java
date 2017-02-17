/**
 * 
 */
package com.zhonghong.mediascanner.saver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.os.FileUtils;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.zhonghong.mediascanner.utils.FileUtil;
import com.zhonghong.mediascanner.utils.InitThread;
import com.zhonghong.mediascanner.utils.L;


/**
 * @author YC
 * @time 2017-1-14 上午11:16:08
 * TODO:文件记录Map内容
 */
public class FileRecorder{
	
	private static final String START_TAG = "<";
	private static final String END_TAG = ">";
	private static final String ITEM_SPLIT = "::";
	
//	private static final String RECORD_INFO_FILE = Config.DIR_PATH+"record_info.cfg";
	private static final String TAG = "FileRecorder";
	private boolean bInit = false;
	private String mFilePath;
	
	/**内容信息*/
	private Map<String, String> mContent = new HashMap<String, String>();
	
	public FileRecorder(String filePath){
		mFilePath = filePath;
		new InitThread() {
			@Override
			protected void loadData() {
				L.startTime("加载文件信息");
				init();
				bInit = true;
				L.endUseTime("加载文件信息");
			}

			private void init() {
				if (FileUtil.createOrExistsFile(mFilePath)){
					readFileInfo();
				}
			}
		}.start();
	}
	
	
	private void waitForInit(){
		while(!bInit){
			synchronized (this) {
				SystemClock.sleep(5);
				Log.i(TAG, "wait for init");
			}
		}
	}
	
	private void readFileInfo(){
		if (FileUtil.createOrExistsFile(mFilePath)){
			File file = new File(mFilePath);
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
								String item = line.substring(START_TAG.length(), endIndex);
								if (!TextUtils.isEmpty(item)){
									L.i(TAG, "get device = " +  item);
									String[] split = item.split(ITEM_SPLIT);
									if (split != null && split.length == 2){
										mContent.put(split[0], split[1]);
									}
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
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
	
	/**
	 * 保存数据到文件
	 * @param content
	 * @return
	 */
	private void saveFileInfo(){
		Map<String, String> tmpContent  = new HashMap<String, String>(mContent);
		StringBuilder builder = new StringBuilder();
		Iterator<Entry<String, String>> iterator = tmpContent.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, String> next = iterator.next();
			builder.append(START_TAG + next.getKey() + ITEM_SPLIT + next.getValue() + END_TAG + "\n");
		}
		
		if (builder.length() > 0 && FileUtil.createOrExistsFile(mFilePath)){
			File file = new File(mFilePath);
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
	
	
	public synchronized String getValue(String key, String defaultStr){
		waitForInit();
		if (mContent.containsKey(key)){
			return mContent.get(key);
		}
		else{
			return defaultStr;
		}
	}
	
	public synchronized void putValue(String key, String value){
		if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)){
			throw new IllegalArgumentException("key或value不能为空");
		}
		waitForInit();
		String oldValue = null;
		if (mContent.containsKey(key)){
			oldValue = mContent.get(key);
		}
		if (!value.equals(oldValue)){
			mContent.put(key, value);
			saveFileInfo();
		}
	}

}
