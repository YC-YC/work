package com.zhonghong.chelianupdate.bean;

public class UpdateStatusInfo{
	private boolean status;
	private String errorMessage;
	private String errorCode;
	
	/**
	 * 无参构造函数，自动设置参数:
	 * status--false
	 * errorMessage--null
	 * errorCode--null
	 * */
	public UpdateStatusInfo()
	{
		this.status=false;
		this.errorMessage=null;
		this.errorCode=null;
	}
	
	public UpdateStatusInfo(boolean status,String errorMessage,String errorCode)
	{
		this.status=status;
		this.errorMessage=errorMessage;
		this.errorCode=errorCode;
	}
	public boolean isOk() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public String getErrorMessage() {
		return errorMessage==null?"":errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getErrorCode() {
		return errorCode==null?"":errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
}
