/**
 * 
 */
package com.zhcar.apprecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zhcar.data.AppUseRecord;

/**
 * @author Administrator
 * @time 2016-8-9 下午3:35:05
 * TODO:缓存记录信息
 */
public class CacheAppUseRecord {
	
	private List<AppUseRecord> mSendList = new ArrayList<AppUseRecord>();
	private List<AppUseRecord> mCacheList = new ArrayList<AppUseRecord>();

	/**
	 * 发送前填入数据
	 * @param info
	 */
	public void push(AppUseRecord info){
		if (info == null){
			return;
		}
		mSendList.add(info);
	}
	
	/**
	 * 发送结果反馈
	 * @param bOk
	 */
	public void sendState(boolean bOk){
		if (mSendList.size() <= 0){
			return;
		}
		if (!bOk){
			mCacheList.add(mSendList.get(0));
		}
		mSendList.remove(0);
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
