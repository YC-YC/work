/**
 * 
 */
package com.zhonghong.rate;

import android.os.SystemClock;

/**
 * @author YC
 * @time 2016-7-27 下午7:22:23
 * TODO:预备状态，3S后开始检测
 */
public class ReadyStatus extends BaseStatus{

	private final int STATUS_TIME = 3*1000;
	@Override
	protected boolean needEnterNextStatus() {
		if (mRateInfos.size() >= 2){
			if (mRateInfos.get(mRateInfos.size()-1).getEventTime() - mRateInfos.get(0).getEventTime() > STATUS_TIME){
				return true;
			}
		}
		return false;
	}

	@Override
	protected void enterNextStatus() {
		RateManager.getRateManager().setCurStatus(RateManager.getRateManager().getCheckingStatus());
	}
	
	/**
	 * 
	 * @return 本状态剩余时间
	 */
	public int getStatusRestTime(){
		int restTime = STATUS_TIME;
		if (mRateInfos.size() > 0){
			restTime = (int) (STATUS_TIME - (SystemClock.elapsedRealtime() - mRateInfos.get(0).getEventTime()));
		}
		return restTime;
	}


}
