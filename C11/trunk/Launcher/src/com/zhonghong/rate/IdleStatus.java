/**
 * 
 */
package com.zhonghong.rate;


/**
 * @author YC
 * @time 2016-7-27 下午7:22:23
 * TODO:空闲状态
 */
public class IdleStatus extends BaseStatus {

	@Override
	protected boolean needEnterNextStatus() {
		return true;
	}

	@Override
	protected void enterNextStatus() {
		RateManager.getRateManager().setCurStatus(RateManager.getRateManager().getPrepareStatus());
	}

}
