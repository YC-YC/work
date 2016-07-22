/**
 * 
 */
package com.zhcar.apprecord;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.zhcar.R;
import com.zhcar.carflow.GetFlowLoc;
import com.zhcar.data.AppUseRecord;
import com.zhcar.data.FlowInfoBean;
import com.zhcar.data.GlobalData;
import com.zhcar.dialog.DialogManager;
import com.zhcar.provider.CarProviderData;
import com.zhcar.utils.GPRSManager;
import com.zhcar.utils.Utils;
import com.zhcar.utils.http.HttpStatusCallback;

/**
 * @author YC
 * @time 2016-7-20 下午4:37:57
 * TODO:上传应用管理类
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
