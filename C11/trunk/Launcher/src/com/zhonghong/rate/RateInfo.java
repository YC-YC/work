/**
 * 
 */
package com.zhonghong.rate;

/**
 * @author YC
 * @time 2016-7-27 下午5:51:38
 * TODO:心率信息
 */
public class RateInfo {

	/**心率值*/
	private int rateVal;
	/**心率时间*/
	private long eventTime;
	
	
	
	/**
	 * @param rateVal
	 * @param eventTime
	 */
	public RateInfo(int rateVal, long eventTime) {
		super();
		this.rateVal = rateVal;
		this.eventTime = eventTime;
	}
	/**
	 * @return the rateVal
	 */
	public int getRateVal() {
		return rateVal;
	}
	/**
	 * @return the eventTime
	 */
	public long getEventTime() {
		return eventTime;
	}
	/**
	 * @param rateVal the rateVal to set
	 */
	public void setRateVal(int rateVal) {
		this.rateVal = rateVal;
	}
	/**
	 * @param eventTime the eventTime to set
	 */
	public void setEventTime(long eventTime) {
		this.eventTime = eventTime;
	}
	
	
}
