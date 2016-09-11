package com.zhonghong.chelianupdate.bean;

import java.io.File;

public class SimpleAppInfo {
	private String appName;
	private String version;
	private String url;
	private String filePath;
	private String appId;
	private long fileSize;
	private long downloaded;
	
	public SimpleAppInfo(UpdateVo vo,String target)
	{
		this.appName=vo.getAppName();
		this.version=vo.getVersionCode();
		this.url=vo.getFileUrl();
		this.filePath=target+vo.getFileName();
		this.fileSize=vo.getFileSize();
		this.setAppId(vo.getAppId());
		File file=new File(this.filePath);
		if(file.exists())
		{
			downloaded=file.length();
		}
		else
		{
			downloaded=0;
		}
	}
	
	public SimpleAppInfo(String appName,String version,String url,String fileName,String filePath,String appId,long fileSize)
	{
		this.appName=appName;
		this.fileSize=fileSize;
		this.version=version;
		this.url=url;
		this.filePath=filePath;
		this.setAppId(appId);
	}
	
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public long getDownloaded() {
		return downloaded;
	}

	public void setDownloaded(long downloaded) {
		this.downloaded = downloaded;
	}
	
	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public boolean isDownloadStarted()
	{
		File file=new File(filePath);
		return file.exists();
	}
	public boolean isDownloadFinished()
	{
		return downloaded==fileSize;
	}
}
