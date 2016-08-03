/**
 * 
 */
package com.zhonghong.rate;


/**
 * @author YC
 * @time 2016-7-27 下午7:22:23
 * TODO:准备状态，连续5s后进入ReadyStatus
 */
public class PrepareStatus extends BaseStatus {

	private final int STATUS_TIME = 5*1000;

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
		RateManager.getRateManager().setCurStatus(RateManager.getRateManager().getReadyStatus());
	}

}
