/**
 * 
 */
package com.zhonghong.updatecan;

import java.io.File;
import java.io.FileInputStream;

import android.util.Log;

import com.zhonghong.datamode.DataFactory;
import com.zhonghong.datamode.IDataMode;
import com.zhonghong.datamode.IDataMode.DataListener;

/**
 * @author YC
 * @time 2016-10-17 下午8:01:08
 * TODO:升级主要逻辑
 */
public class UpdateCanC40 {

	private static final String TAG = "UpdateCan";

	private static UpdateCanC40 sInstances;
	private UpdateCanStatus mUpdateStatus;
	/**文件内容*/
	private byte mFileContent[];
	/**一帧升级文件大小*/
	private static final int BLOCK_SIZE = 128;
	private int mFileLength;
	/**标识是否在升级*/
	private boolean bIsUpdateing = false;
	
	private IDataMode mDataMode;
	private DataListener mDataListener;
	
	private UpdateCanC40(){
		mDataMode = DataFactory.getRecordDevice();
	}
	public static UpdateCanC40 getInstances(){
		if (sInstances == null){
			synchronized (UpdateCanC40.class) {
				if (sInstances == null){
					sInstances = new UpdateCanC40();
				}
			}
		}
		return sInstances;
	}
	
	/**
	 * 是否在升级
	 * @return
	 */
	public boolean isUpdateing(){
		return bIsUpdateing;
	}
	
	/**
	 * 开始升级
	 * @param path
	 * @param updateStatus
	 * @return
	 */
	public synchronized boolean startUpdate(String path, UpdateCanStatus updateStatus){
		Log.i(TAG, "startUpdate path = " + path);
		mUpdateStatus = updateStatus;
		if (bIsUpdateing){
			CallbackOnStatus(UpdateCanStatus.STATUS_ERROR, 2);
			return false;
		}
		if (!Utils.hasFileExits(path)){
			CallbackOnStatus(UpdateCanStatus.STATUS_ERROR, 0);
			return false;
		}
		
		if (!getFileContent(path) || !checkFileContent()){
			CallbackOnStatus(UpdateCanStatus.STATUS_ERROR, 1);
			return false;
		}
		
		bIsUpdateing = true;
//		if (mDataListener == null){
			mDataListener = new MyDataListener();
//		}
		mDataMode.registerDataListener(mDataListener);
		sendStartUpdate();
		//开始升级
		return true;
	}
	
	private void CallbackOnStatus(int cmd, int value){
		if (mUpdateStatus != null){
			mUpdateStatus.onStatus(cmd, value);
			Log.i(TAG, "onStatus cmd = " + cmd + ", value = " + value);
		}
	}
	
	/**
	 * 获取文件文件内容
	 * @param path
	 * @return
	 */
	private boolean getFileContent(String path) {
		File file = new File(path);
		try {
			FileInputStream inputStream = new FileInputStream(file);
			mFileLength = (int) file.length();
			Log.i(TAG, "getFileSize = " + mFileLength);
			mFileContent = new byte[mFileLength];
			inputStream.read(mFileContent);
			inputStream.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 判断文件是否符合升级要求，后续可加入校验
	 * @return
	 */
	private boolean checkFileContent() {
		if (mFileContent == null || mFileContent.length%BLOCK_SIZE != 0){
			return false;
		}
		return true;
	}
	
	
	/**
	 * @param data
	 */
	private void processUpdate(byte[] data) {
		int type = data[0]&0xFF;
		switch (type) {
		// 状态
		case 0xFC: {
			int state = data[2]&0xFF;
			switch (state) {
			// 请求重新计算包大小
			case 0x00: {
				sendPacketSize();
			}break;
			// 升级就绪
			case 0x01: {
				CallbackOnStatus(UpdateCanStatus.STATUS_START, mFileLength);
			}break;
			// 升级完成
			case 0x02: {
				bIsUpdateing = false;
				mDataMode.unregisterDataListener(mDataListener);
				CallbackOnStatus(UpdateCanStatus.STATUS_FINISH, 0);
			}break;
			default:
				break;
			}
		}break;
			//请求包数据
		case 0xFD: {
			int packetNum = ((data[2]&0xFF) << 8) + (data[3]&0xFF);
			Log.i(TAG, String.format("Req [%d] packet", packetNum));
			sendPacketData(packetNum);
			CallbackOnStatus(UpdateCanStatus.STATUS_UPDATEING, (packetNum+1)*BLOCK_SIZE);
		}break;

		default:
			break;
		}
	}
	
	/**
	 * 开始升级
	 */
	private void sendStartUpdate(){
		sendPacketSize();
	}
	
	/**
	 * 发送文件包总数
	 */
	private void sendPacketSize(){
		int size = mFileLength/BLOCK_SIZE;
		byte[] data = {0x2E, (byte) 0xFA, 0x02, 0x00, 0x00, 0x00};
		data[3] = (byte) ((size >> 8)&0xFF);
		data[4] = (byte) (size&0xFF);
		data[5] = getCheckSun(data, 1, 4);
		mDataMode.sendData(data);
	}
	
	/**
	 * 发送第N包数据
	 * @param packetNum
	 */
	private void sendPacketData(int packetNum){
		byte[] canData = getPacketData(packetNum);
		mDataMode.sendData(canData);
	}
	
	/**
	 * 获取包数据
	 * @param packetNum
	 * @return
	 */
	private byte[] getPacketData(int packetNum){
		int len = BLOCK_SIZE;
		byte[] canData = new byte[len+6];
		canData[0] = 0x2E;
		canData[1] = (byte) 0xFB;
		canData[2] = (byte) (len+2);
		canData[3] = (byte) (packetNum>>8);
		canData[4] = (byte) (packetNum&0xFF);
		
		for(int i = 0; i < len; i++){
			int index = packetNum*BLOCK_SIZE + i;
			if (index < mFileLength){
				canData[5+i] = mFileContent[index];
			}
			else{
				canData[5+i] = 0x00;
			}
		}
		canData[len+5] = getCheckSun(canData, 1, len+4);
		return canData;
	}
	
	/**
	 * 计算checksun
	 * @param data
	 * @param start
	 * @param end
	 * @return
	 */
	private byte getCheckSun(byte[] data, int start, int end){
		int checkSun = 0;
		for (int i = start; i <= end; i++){
			checkSun += (data[i]&0xFF);
		}
		return (byte) (checkSun^0xFF);
	}
	
	private class MyDataListener implements DataListener{
		
		@Override
		public void onDataIn(byte[] data) {
			processUpdate(data);
		}

	}

}
