package com.zhonghong.mediascanerlib.aidl;

import com.zhonghong.mediascanerlib.aidl.ScannerCallback;
interface ScannerInterface{

	/**
	 * 获取文件信息
	 * @param device 设备路径
	 * @param type	信息类型（1：Audio,2:Video, 3:Image）
	 * @param callback 具体信息回调
	 */
	void getFileInfo(String device, int type, ScannerCallback callback);
}