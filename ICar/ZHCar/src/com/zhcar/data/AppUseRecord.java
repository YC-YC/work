/**
 * 
 */
package com.zhcar.data;

import java.util.HashMap;

/**
 * @author YC
 * @time 2016-7-21 下午5:36:24
 * TODO:App使用信息
 */
public class AppUseRecord {
	private String id;
	/**车辆vin码*/
	private String vin;
	/**应用程序名称*/
	private String appName;
	/**购车经销商代码*/
	private String dealerCode;
	/**购车经销商名称*/
	private String dealerName;
	/**应用程序使用状态（连接SP服务成功或者失败）*/
	private String appStatus;
	/**应用程序栏目（应用程序下的节目名称和音乐名称）*/
	private String appProgram;
	/**应用程序作者（应用程序下的节目主持人和音乐演唱者）*/
	private String appProgramAuth;
	/**应用程序启动时间*/
	private String startTime;
	/**应用程序退出时间或者车辆熄火时间*/
	private String endTime;
	/**使用类型*/
	private String type;

	public AppUseRecord(){
		
	}

	public AppUseRecord(String id, String vin, String appName,
			String dealerCode, String dealerName, String appStatus,
			String appProgram, String appProgramAuth, String startTime,
			String endTime, String type) {
		super();
		this.id = id;
		this.vin = vin;
		this.appName = appName;
		this.dealerCode = dealerCode;
		this.dealerName = dealerName;
		this.appStatus = appStatus;
		this.appProgram = appProgram;
		this.appProgramAuth = appProgramAuth;
		this.startTime = startTime;
		this.endTime = endTime;
		this.type = type;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the vin
	 */
	public String getVin() {
		return vin;
	}

	/**
	 * @return the appName
	 */
	public String getAppName() {
		return appName;
	}

	/**
	 * @return the dealerCode
	 */
	public String getDealerCode() {
		return dealerCode;
	}

	/**
	 * @return the dealerName
	 */
	public String getDealerName() {
		return dealerName;
	}

	/**
	 * @return the appStatus
	 */
	public String getAppStatus() {
		return appStatus;
	}

	/**
	 * @return the appProgram
	 */
	public String getAppProgram() {
		return appProgram;
	}

	/**
	 * @return the appProgramAuth
	 */
	public String getAppProgramAuth() {
		return appProgramAuth;
	}

	/**
	 * @return the startTime
	 */
	public String getStartTime() {
		return startTime;
	}

	/**
	 * @return the endTime
	 */
	public String getEndTime() {
		return endTime;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @param vin the vin to set
	 */
	public void setVin(String vin) {
		this.vin = vin;
	}

	/**
	 * @param appName the appName to set
	 */
	public void setAppName(String appName) {
		this.appName = appName;
	}

	/**
	 * @param dealerCode the dealerCode to set
	 */
	public void setDealerCode(String dealerCode) {
		this.dealerCode = dealerCode;
	}

	/**
	 * @param dealerName the dealerName to set
	 */
	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}

	/**
	 * @param appStatus the appStatus to set
	 */
	public void setAppStatus(String appStatus) {
		this.appStatus = appStatus;
	}

	/**
	 * @param appProgram the appProgram to set
	 */
	public void setAppProgram(String appProgram) {
		this.appProgram = appProgram;
	}

	/**
	 * @param appProgramAuth the appProgramAuth to set
	 */
	public void setAppProgramAuth(String appProgramAuth) {
		this.appProgramAuth = appProgramAuth;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	public HashMap<String, String> toMap() {
		HashMap<String, String> maps = new HashMap<String, String>();
		maps.put("id", id);
		maps.put("vin", vin);
		maps.put("appName", appName);
		maps.put("dealerCode", dealerCode);
		maps.put("dealerName", dealerName);
		maps.put("appStatus", appStatus);
		maps.put("appProgram", appProgram);
		maps.put("appProgramAuth", appProgramAuth);
		maps.put("startTime", startTime);
		maps.put("endTime", endTime);
		maps.put("type", type);
		return maps;
	}
	
	
	
	
}
