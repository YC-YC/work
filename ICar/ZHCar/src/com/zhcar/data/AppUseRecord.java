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
	/**车辆vin码*/
	private String vin;
	/**应用程序名称*/
	private String appName;
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

	/**
	 * 必填值
	 * @param vin
	 * @param appStatus
	 * @param type
	 */
	public AppUseRecord(String vin, String appStatus, String type) {
		super();
		this.vin = vin;
		this.appStatus = appStatus;
		this.type = type;
	}



	public AppUseRecord(String vin, String appName, String appStatus,
			String appProgram, String appProgramAuth, String startTime,
			String endTime, String type) {
		super();
		this.vin = vin;
		this.appName = appName;
		this.appStatus = appStatus;
		this.appProgram = appProgram;
		this.appProgramAuth = appProgramAuth;
		this.startTime = startTime;
		this.endTime = endTime;
		this.type = type;
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
		maps.put("vin", vin);
		maps.put("appName", appName);
		maps.put("appStatus", appStatus);
		maps.put("appProgram", appProgram);
		maps.put("appProgramAuth", appProgramAuth);
		maps.put("startTime", startTime);
		maps.put("endTime", endTime);
		maps.put("type", type);
		return maps;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((appName == null) ? 0 : appName.hashCode());
		result = prime * result
				+ ((appProgram == null) ? 0 : appProgram.hashCode());
		result = prime * result
				+ ((appProgramAuth == null) ? 0 : appProgramAuth.hashCode());
		result = prime * result
				+ ((appStatus == null) ? 0 : appStatus.hashCode());
		result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
		result = prime * result
				+ ((startTime == null) ? 0 : startTime.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((vin == null) ? 0 : vin.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AppUseRecord other = (AppUseRecord) obj;
		if (appName == null) {
			if (other.appName != null)
				return false;
		} else if (!appName.equals(other.appName))
			return false;
		if (appProgram == null) {
			if (other.appProgram != null)
				return false;
		} else if (!appProgram.equals(other.appProgram))
			return false;
		if (appProgramAuth == null) {
			if (other.appProgramAuth != null)
				return false;
		} else if (!appProgramAuth.equals(other.appProgramAuth))
			return false;
		if (appStatus == null) {
			if (other.appStatus != null)
				return false;
		} else if (!appStatus.equals(other.appStatus))
			return false;
		if (endTime == null) {
			if (other.endTime != null)
				return false;
		} else if (!endTime.equals(other.endTime))
			return false;
		if (startTime == null) {
			if (other.startTime != null)
				return false;
		} else if (!startTime.equals(other.startTime))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (vin == null) {
			if (other.vin != null)
				return false;
		} else if (!vin.equals(other.vin))
			return false;
		return true;
	}
	
	
	
	
	
	
}
