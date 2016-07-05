/**
 * 
 */
package com.zhonghong.leftitem;

import java.util.List;

import android.content.Context;

/**
 * @author YC
 * @time 2016-6-25 下午5:59:16
 * TODO:左边三个图标管理，主要任务是记录三个图标分别对应Menu的第几项
 */
public class LeftItemsCtrl {

	private IRecorder mRecorder;
	
	private List<Integer> mLeftItemInfo/* = new ArrayList<Integer>(){
		{
			clear();
			add(0);
			add(1);
			add(2);
		}
	}*/;
	
	public LeftItemsCtrl(Context mContext) {
		super();
		mRecorder = new FileRecorder(mContext);
		mLeftItemInfo = mRecorder.read();
	}

	public List<Integer> getLeftItemInfo(){
		return mLeftItemInfo;
	}
	
	/**
	 * @param item 左边三个的位置
	 * @param position Menu对应的位置
	 * @return
	 */
	public boolean setItemPosition(int item, int position){
		if (item < 0 || item >= mLeftItemInfo.size()){
			return false;
		}
		mLeftItemInfo.set(item, position);
		mRecorder.write(mLeftItemInfo);
		return true;
	}
}
