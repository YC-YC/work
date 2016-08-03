/**
 * 
 */
package com.zhonghong.rate;

import java.util.ArrayList;

/**
 * @author YC
 * @time 2016-7-27 下午9:35:38
 * TODO:
 */
public abstract class BaseStatus implements IStatus {

	protected final String TAG = getClass().getSimpleName();

	protected ArrayList<RateInfo> mRateInfos = new ArrayList<RateInfo>();
	
	protected abstract boolean needEnterNextStatus();
	protected abstract void enterNextStatus();
	
	@Override
	public void inputRate(RateInfo info) {

		mRateInfos.add(info);
		if (needEnterNextStatus()){
			enterNextStatus();
			reset();
		}
	}

	@Override
	public void reset() {
		mRateInfos.clear();
	}

}
