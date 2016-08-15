/**
 * 
 */
package com.zhcar.apprecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import android.util.Log;

import com.zhcar.data.AppUseRecord;
import com.zhcar.utils.http.HttpStatusCallback;

/**
 * @author YC
 * @time 2016-7-20 下午4:37:57
 * TODO:http上传应用信息管理类
 */
public class RecordManager {

	private static final String TAG = "AppRecord";
	private IPostAppRecord mPostAppRecord;
	
	private static RecordManager instance;
	public static RecordManager getInstance(){
		if (instance == null){
			synchronized (RecordManager.class) {
				if (instance == null){
					instance = new RecordManager();
				}
			}
		}
		return instance;
	}
	
	public void HttpPostAppRecord(AppUseRecord recordInfo){
		if (mPostAppRecord == null){
			mPostAppRecord = new PostAppRecord();
		}
		mPostAppRecord.SetInfo(recordInfo);
		mPostAppRecord.SetHttpStatusCallback(new HttpStatusCallback() {
			
			@Override
			public void onStatus(int status) {
				Log.i(TAG, "get Http status = " + status);
				if (status == HttpStatusCallback.RESULT_SUCCESS){
				}
			}
		});
		mPostAppRecord.Refresh();
	}
	
	
	private RecordManager(){
		
	}
	
}
