/**
 * 
 */
package com.zhonghong.rate;

import android.os.SystemClock;

/**
 * @author YC
 * @time 2016-7-27 下午7:22:23
 * TODO:正在检测状态
 */
public class CheckingStatus extends BaseStatus {
	
	private final int STATUS_TIME = 60*1000;
	
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
		RateManager.getRateManager().getCheckedStatus().setCheckResult(mRateInfos);
		RateManager.getRateManager().setCurStatus(RateManager.getRateManager().getCheckedStatus());
//		Log.i(TAG, "getAverage = " + getAverage());
	}

	/*@Override
	public int getMin() {
		int min = 0;
		if (mRateInfos.size() > 0){
			min = mRateInfos.get(0).getRateVal();
			for (int i = 1; i < mRateInfos.size(); i++){
				if (mRateInfos.get(i).getRateVal() < min){
					min = mRateInfos.get(i).getRateVal();
				}
			}
		}
		return min;
	}

	@Override
	public int getMax() {
		int max = 0;
		if (mRateInfos.size() > 0){
			max = mRateInfos.get(0).getRateVal();
			for (int i = 1; i < mRateInfos.size(); i++){
				if (mRateInfos.get(i).getRateVal() > max){
					max = mRateInfos.get(i).getRateVal();
				}
			}
		}
		return max;
	}

	@Override
	public int getAverage() {
		int average = 0;
		if (mRateInfos.size() > 0){
			int total = 0;
			Iterator<RateInfo> iterator = mRateInfos.iterator();
			while (iterator.hasNext()){
				total += iterator.next().getRateVal();
			}
			average = total/mRateInfos.size();
		}
		return average;
	}*/
	
	public int getLastRateValue(){
		int value = 0;
		if (mRateInfos.size() > 0){
			value = mRateInfos.get(mRateInfos.size()-1).getRateVal();
		}
		return value;
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
