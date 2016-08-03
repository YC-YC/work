/**
 * 
 */
package com.zhonghong.rate;

/**
 * @author YC
 * @time 2016-7-29 下午4:13:39
 * TODO:
 */
public class RateResultInfo {

	private int minRateValue;
	private int maxRateValue;
	private int adverage;
	private long checkTime;
	
	public RateResultInfo(){
		
	}

	/**
	 * @param minRateValue
	 * @param maxRateValue
	 * @param adverage
	 * @param checkTime
	 */
	public RateResultInfo(int minRateValue, int maxRateValue, int adverage,
			long checkTime) {
		super();
		this.minRateValue = minRateValue;
		this.maxRateValue = maxRateValue;
		this.adverage = adverage;
		this.checkTime = checkTime;
	}

	/**
	 * @return the minRateValue
	 */
	public int getMinRateValue() {
		return minRateValue;
	}

	/**
	 * @return the maxRateValue
	 */
	public int getMaxRateValue() {
		return maxRateValue;
	}

	/**
	 * @return the adverage
	 */
	public int getAdverage() {
		return adverage;
	}

	/**
	 * @return the checkTime
	 */
	public long getCheckTime() {
		return checkTime;
	}

	/**
	 * @param minRateValue the minRateValue to set
	 */
	public void setMinRateValue(int minRateValue) {
		this.minRateValue = minRateValue;
	}

	/**
	 * @param maxRateValue the maxRateValue to set
	 */
	public void setMaxRateValue(int maxRateValue) {
		this.maxRateValue = maxRateValue;
	}

	/**
	 * @param adverage the adverage to set
	 */
	public void setAdverage(int adverage) {
		this.adverage = adverage;
	}

	/**
	 * @param checkTime the checkTime to set
	 */
	public void setCheckTime(long checkTime) {
		this.checkTime = checkTime;
	}

	@Override
	public String toString() {
		return "RateResultInfo [minRateValue=" + minRateValue
				+ ", maxRateValue=" + maxRateValue + ", adverage=" + adverage
				+ ", checkTime=" + checkTime + "]";
	}
	
	
	
}
