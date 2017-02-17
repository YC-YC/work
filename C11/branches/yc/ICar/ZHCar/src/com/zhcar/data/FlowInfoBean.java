/**
 * 
 */
package com.zhcar.data;

/**
 * @author YC
 * @time 2016-7-6 上午11:51:21
 * TODO:Http返回结果
 */
public class FlowInfoBean {

	String name;
	String id;
	String remindValue;
	String vin;
	String mobile;
	String phoneNum;
	String remind;
	/**已用流量*/
	String useFlow;
	/**剩余流量*/
	String surplusFlow;
	/**总流量*/
	String currFlowTotal;

	public FlowInfoBean() {
		super();
	}

	public FlowInfoBean(String name, String id, String remindValue, String vin,
			String mobile, String phoneNum, String remind, String useFlow,
			String surplusFlow, String currFlowTotal) {
		super();
		this.name = name;
		this.id = id;
		this.remindValue = remindValue;
		this.vin = vin;
		this.mobile = mobile;
		this.phoneNum = phoneNum;
		this.remind = remind;
		this.useFlow = useFlow;
		this.surplusFlow = surplusFlow;
		this.currFlowTotal = currFlowTotal;
	}
	

	/**
	 * @param remindValue
	 * @param remind
	 * @param useFlow
	 * @param surplusFlow
	 * @param currFlowTotal
	 */
	public FlowInfoBean(String remindValue, String remind, String useFlow,
			String surplusFlow, String currFlowTotal) {
		super();
		this.remindValue = remindValue;
		this.remind = remind;
		this.useFlow = useFlow;
		this.surplusFlow = surplusFlow;
		this.currFlowTotal = currFlowTotal;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setRemindValue(String remindValue) {
		this.remindValue = remindValue;
	}

	public void setVin(String vin) {
		this.vin = vin;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public void setRemind(String remind) {
		this.remind = remind;
	}

	/**已用流量*/
	public void setUseFlow(String useFlow) {
		this.useFlow = useFlow;
	}

	/**剩余流量*/
	public void setSurplusFlow(String surplusFlow) {
		this.surplusFlow = surplusFlow;
	}
	
	/**总流量*/
	public void setCurrFlowTotal(String currFlowTotal) {
		this.currFlowTotal = currFlowTotal;
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	public String getRemindValue() {
		return remindValue;
	}

	public String getVin() {
		return vin;
	}

	public String getMobile() {
		return mobile;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public String getRemind() {
		return remind;
	}

	/**已用流量*/
	public String getUseFlow() {
		return useFlow;
	}

	/**剩余流量*/
	public String getSurplusFlow() {
		return surplusFlow;
	}

	/**总流量*/
	public String getCurrFlowTotal() {
		return currFlowTotal;
	}
	
	public boolean getRemindVal() {
		return (remind == null || "1".equals(remind) || "true".equals(remind))? true: false;
	}




	@Override
	public String toString() {
		return "FlowInfoBean [name=" + name + ", id=" + id + ", remindValue="
				+ remindValue + ", vin=" + vin + ", mobile=" + mobile
				+ ", phoneNum=" + phoneNum + ", remind=" + remind
				+ ", useFlow=" + useFlow + ", surplusFlow=" + surplusFlow
				+ ", currFlowTotal=" + currFlowTotal + "]";
	}
	
	
	
}
