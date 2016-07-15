/**
 * 
 */
package com.zhonghong.carflow;

/**
 * @author YC
 * @time 2016-7-6 上午11:51:21
 * TODO:Http返回结果
 */
public class FlowInfoBean {

	String name;
	int id;
	int remindValue;
	String vin;
	String mobile;
	String phoneNum;
	boolean remind;
	/**已用流量*/
	float useFlow;
	/**剩余流量*/
	float surplusFlow;
	/**总流量*/
	float currFlowTotal;

	public FlowInfoBean() {
		super();
	}

	public FlowInfoBean(String name, int id, int remindValue, String vin,
			String mobile, String phoneNum, boolean remind, int useFlow,
			int surplusFlow, int currFlowTotal) {
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

	public void setName(String name) {
		this.name = name;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setRemindValue(int remindValue) {
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

	public void setRemind(boolean remind) {
		this.remind = remind;
	}

	/**已用流量*/
	public void setUseFlow(float useFlow) {
		this.useFlow = useFlow;
	}

	/**剩余流量*/
	public void setSurplusFlow(float surplusFlow) {
		this.surplusFlow = surplusFlow;
	}
	
	/**总流量*/
	public void setCurrFlowTotal(float currFlowTotal) {
		this.currFlowTotal = currFlowTotal;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public int getRemindValue() {
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

	public boolean isRemind() {
		return remind;
	}

	/**已用流量*/
	public float getUseFlow() {
		return useFlow;
	}

	/**剩余流量*/
	public float getSurplusFlow() {
		return surplusFlow;
	}

	/**总流量*/
	public float getCurrFlowTotal() {
		return currFlowTotal;
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
