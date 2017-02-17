package com.zhonghong.mediascanerlib.aidl;

interface ScannerCallback{
	/**
	 * 开始
	 * @param device
	 * @param type 信息类型（1：Audio,2:Video, 3:Image）
	 */
	void onStart(String device, int type);
	
	/**
	 * 结束
	 * @param device
	 * @param type 信息类型（1：Audio,2:Video, 3:Image）
	 * @param code
	 */
	void onEnd(String device, int type, int code);
	/**
	 * 
	 * @param device
	 * @param type 信息类型（1：Audio,2:Video, 3:Image）
	 * @param fileInfo 相应的信息，通过gson转成AudioInfo，VideoInfo, ImageInfo
	 */
	void onGetFileInfo(String device, int type, String fileInfo);
}