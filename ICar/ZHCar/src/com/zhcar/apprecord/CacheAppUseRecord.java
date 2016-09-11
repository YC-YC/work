/**
 * 
 */
package com.zhcar.apprecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.zhcar.carflow.CarFlowManager;
import com.zhcar.data.AppUseRecord;

/**
 * @author Administrator
 * @time 2016-8-9 下午3:35:05
 * TODO:缓存记录信息
 */
public class CacheAppUseRecord {
	
private static final String TAG = "CacheAppUseRecord";

//	private List<AppUseRecord> mSendList = new ArrayList<AppUseRecord>();
	private List<AppUseRecord> mCacheList = new ArrayList<AppUseRecord>();

//	private static CacheAppUseRecord instance;
//	public static CacheAppUseRecord getInstance(){
//		if (instance == null){
//			synchronized (CacheAppUseRecord.class) {
//				if (instance == null){
//					instance = new CacheAppUseRecord();
//				}
//			}
//		}
//		return instance;
//	}
	
	public CacheAppUseRecord(){
		
	}
	/**
	 * 发送前填入数据
	 * @param info
	 */
	public void push(AppUseRecord info){
		if (info == null){
			return;
		}
		mCacheList.add(info);
	}
	
	/**
	 * 发送结果反馈
	 * @param bOk
	 */
	public void sendState(boolean bOk, int hashCode){
		if (bOk){
			for (AppUseRecord info: mCacheList){
				if (info.hashCode() == hashCode){
					mCacheList.remove(info);
					Log.i(TAG, "send Ok, remove, have cache num = " + mCacheList.size());
					break;
				}
			}
		}
		Log.i(TAG, "have cache num = " + mCacheList.size());
	}
	
	/**
	 * 是否有没被发送的内容
	 * @return
	 */
	public boolean hasCacheInfo(){
		return mCacheList.size() > 0 ? true: false;
	}
	
	public AppUseRecord getNextCacheInfo(){
		if (hasCacheInfo()){
			AppUseRecord record = mCacheList.get(0);
			mCacheList.remove(0);
			return record;
		}
		return null;
	}
}
