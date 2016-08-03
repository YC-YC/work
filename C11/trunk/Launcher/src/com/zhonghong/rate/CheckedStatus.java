/**
 * 
 */
package com.zhonghong.rate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import android.util.Log;


/**
 * @author YC
 * @time 2016-7-27 下午7:22:23
 * TODO:检测完成状态
 */
public class CheckedStatus extends BaseStatus{

	@Override
	protected boolean needEnterNextStatus() {
		return false;
	}

	@Override
	protected void enterNextStatus() {
		
	}
	
	private ArrayList<RateInfo> mCheckRateInfos = new ArrayList<RateInfo>();
	private long mCheckedTime;
	/**
	 * 将检测的结果传入
	 * @param rateInfos
	 */
	public void setCheckResult(ArrayList<RateInfo> rateInfos){
		mCheckRateInfos.clear();
		Iterator<RateInfo> iterator = rateInfos.iterator();
		while (iterator.hasNext()){
			mCheckRateInfos.add(iterator.next());
		}
//		Log.i(TAG, "setCheckResult srclist = " + rateInfos.hashCode() + ", desList = " + mCheckRateInfos.hashCode());
		Log.i(TAG, "setCheckResult size = " + rateInfos.size() + ", first = " + rateInfos.get(0).getRateVal() + ", last = " + rateInfos.get(rateInfos.size()-1).getRateVal());
		mCheckedTime = System.currentTimeMillis();
	}
	
	private int getMin() {
		int min = 0;
		if (mCheckRateInfos.size() > 0){
			min = mCheckRateInfos.get(0).getRateVal();
			for (int i = 1; i < mCheckRateInfos.size(); i++){
				if (mCheckRateInfos.get(i).getRateVal() < min){
					min = mCheckRateInfos.get(i).getRateVal();
				}
			}
		}
		return min;
	}

	private int getMax() {
		int max = 0;
		if (mCheckRateInfos.size() > 0){
			max = mCheckRateInfos.get(0).getRateVal();
			for (int i = 1; i < mCheckRateInfos.size(); i++){
				if (mCheckRateInfos.get(i).getRateVal() > max){
					max = mCheckRateInfos.get(i).getRateVal();
				}
			}
		}
		return max;
	}

	private int getAverage() {
		int average = 0;
		if (mCheckRateInfos.size() > 0){
			int total = 0;
			Iterator<RateInfo> iterator = mCheckRateInfos.iterator();
			while (iterator.hasNext()){
				total += iterator.next().getRateVal();
			}
			average = total/mCheckRateInfos.size();
		}
		return average;
	}
	
	private long getCheckedTime(){
		return mCheckedTime;
	}
	
	public RateResultInfo getResultInfo(){
		return new RateResultInfo(getMin(), getMax(), getAverage(), getCheckedTime());
	
	}

}
