/**
 * 
 */
package com.zhonghong.logic.record;

import android.content.Context;
import android.util.Log;


/**
 * @author YC
 * @time 2016-6-22 下午3:26:48
 * TODO:记录应用，外部只调用该类的方法
 */
public class RecordAppManager {

	private static final String TAG = "RecordAppManager";

	private static RecordAppManager instances;
	
	private RecordInfoBean mRecordInfo;
	
	private Context mContext;
	
	private IRecorder mRecorder;
	
	public static RecordAppManager getInstaces(Context context){
		
		if (instances == null){
			synchronized (RecordAppManager.class) {
				if (instances == null){
					instances = new RecordAppManager(context);
				}
			}
		}
		return instances;
	}
	
	public RecordInfoBean getRecordInfo(){
		Log.i(TAG, "getLastAppCellInfo cellInfo = " + mRecordInfo.toString());
		return mRecordInfo;
	}
	
	public boolean setRecordInfo(RecordInfoBean cellInfo){
		if (mRecordInfo.equals(cellInfo)){
			return false;
		}
		mRecordInfo = cellInfo;
		Log.i(TAG, "setLastAppCellInfo cellInfo = " + mRecordInfo.toString());
		mRecorder.write(mRecordInfo);
		return true;
	}
	
	
	private RecordAppManager(Context context){
		mContext = context;
		mRecorder = new FileRecorder(mContext);
		mRecordInfo = mRecorder.read();
	}
	
}
