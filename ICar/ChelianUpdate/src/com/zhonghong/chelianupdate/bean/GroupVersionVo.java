package com.zhonghong.chelianupdate.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class GroupVersionVo implements Serializable{

	private static final long serialVersionUID = -1404552029288571463L;
	
	private long id;
	private String type;
	private String versionCode;
	private String detail;
	private UpdateVo mcu;
	private UpdateVo air;
	private UpdateVo android;
	private UpdateVo can;
	private UpdateVo mo8836;
	private String createdTime;
	private String status;
	private long fileSize;
	private long maxSize;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getVersionCode() {
		return versionCode;
	}
	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public UpdateVo getMcu() {
		return mcu;
	}
	public void setMcu(UpdateVo mcu) {
		this.mcu = mcu;
	}
	public UpdateVo getAir() {
		return air;
	}
	public void setAir(UpdateVo air) {
		this.air = air;
	}
	public UpdateVo getAndroid() {
		return android;
	}
	public void setAndroid(UpdateVo android) {
		this.android = android;
	}
	public UpdateVo getCan() {
		return can;
	}
	public void setCan(UpdateVo can) {
		this.can = can;
	}
	public UpdateVo getMo8836() {
		return mo8836;
	}
	public void setMo8836(UpdateVo mo8836) {
		this.mo8836 = mo8836;
	}
	public String getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	public long getMaxSize() {
		return maxSize;
	}
	public void setMaxSize(long maxSize) {
		this.maxSize = maxSize;
	}
	public boolean containsUpdate(String voType)
	{
		if(voType=="android"&&type.contains("1"))
		{
			return true;
		}
		if(voType=="mcu"&&type.contains("2"))
		{
			return true;
		}
		if(voType=="air"&&type.contains("3"))
		{
			return true;
		}
		if(voType=="can"&&type.contains("4"))
		{
			return true;
		}
		if(voType=="8836"&&type.contains("5"))
		{
			return true;
		}
		return false;
	}
	/*public boolean isUpdateLegal(String appName,String version)
	{
		if(appName.equals("android")&&this.android!=null)
		{
			return compareVersion(this.android.getVersionCode(),version)>0;
		}
		if(appName.equals("mcu")&&this.mcu!=null)
		{
			return compareVersion(this.getMcu().getVersionCode(),version)>0;
		}
		if(appName.equals("air")&&this.air!=null)
		{
			return compareVersion(this.getAir().getVersionCode(),version)>0;
		}
		return true;
	}*/
	public List<UpdateVo> getUpdateVoList()
	{
		List<UpdateVo> list=new ArrayList<UpdateVo>();
		if(android!=null)
			list.add(android);
		if(air!=null)
			list.add(air);
		if(can!=null)
			list.add(can);
		if(mo8836!=null)
			list.add(mo8836);
		if(mcu!=null)
			list.add(mcu);
		return list;
	}
	
}
