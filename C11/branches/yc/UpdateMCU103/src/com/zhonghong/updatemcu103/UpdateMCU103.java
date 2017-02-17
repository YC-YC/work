/**
 * 
 */
package com.zhonghong.updatemcu103;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.os.SystemClock;
import android.util.Log;
import android.zhonghong.mcuservice.CustomerProxy;
import android.zhonghong.mcuservice.RegistManager.ICustomerInfoChangedListener;
import android.zhonghong.mcuservice.SystemProxy;

/**
 * @author YC
 * @time 2016-10-17 下午8:01:08
 * TODO:升级103主要逻辑
 */
public class UpdateMCU103 {

	private static final String TAG = "UpdateMCU103";

	private static UpdateMCU103 instances;
	private UpdateStatus mUpdateStatus;
	/**文件内容*/
	private byte mFileContent[];
	/**一帧升级文件大小*/
	private static final int BLOCK_SIZE = 128;
	private int mFileLength;
	/**已经发送的数据长度*/
	private int mHadSendDataNum;
	/**将要发送数据长度*/
	private int mCurSendDataNum;
	/**将要发送数据内容*/
	private byte[] mSendDataBuf = new byte[2*BLOCK_SIZE];
	private byte[] mDataBuf = new byte[2*BLOCK_SIZE];
	private byte[] mSendCmdBuf = new byte[2];
	/**标识升级状态*/
	/**标识是否在升级*/
	private boolean bIsUpdateing = false;
	/**升级步骤*/
	private int mUpdateStep = 0;
	private CustomerProxy mCustomerProxy;
	private MCUDataListener mMCUDataListener = new MCUDataListener();
	
	private UpdateMCU103(){
		mCustomerProxy = new CustomerProxy();
	}
	public static UpdateMCU103 getInstances(){
		if (instances == null){
			synchronized (UpdateMCU103.class) {
				if (instances == null){
					instances = new UpdateMCU103();
				}
			}
		}
		return instances;
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
	public boolean startUpdate(String path, UpdateStatus updateStatus){
		Log.i(TAG, "startUpdate path = " + path);
		mUpdateStatus = updateStatus;
		if (bIsUpdateing){
			CallbackOnStatus(UpdateStatus.STATUS_ERROR, 2);
			return false;
		}
		if (!Utils.hasFileExits(path)){
			CallbackOnStatus(UpdateStatus.STATUS_ERROR, 0);
			return false;
		}
		
		if (!getFileContent(path) || !prepareFileContent()){
			CallbackOnStatus(UpdateStatus.STATUS_ERROR, 1);
			return false;
		}
		
		mHadSendDataNum = 0;
		bIsUpdateing = true;
		mUpdateStep = 0;
		CallbackOnStatus(UpdateStatus.STATUS_START, mFileLength);
		mCustomerProxy.registCustomerInfoChangedListener(mMCUDataListener);
		sendStartUpdate103ToMCU();
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
	 * 请求升级
	 */
	private void sendStartUpdate103ToMCU(){
		
		//进入boot
		byte[] data1 = {(byte) 0xFF, (byte) 0xAA, (byte) 0x9C, (byte) 0xB0, 0x00, 0x0A};
		mCustomerProxy.sendCustomerDataToMcu(data1);
		
		SystemClock.sleep(1*1000);
		
		//开始握手
		byte[] data2 = {(byte) 0xFF, (byte) 0xAA, (byte) 0x9C, (byte) 0xA0, 0x01, 0x0A};
//		mCustomerProxy.sendCustomerDataToMcu(data2);
		sendDataTo103(data2, 6);
		
	}
	
	/**
	 * 通过105转数据到103
	 * @param data
	 * @param len
	 */
	private void sendDataTo103(byte[] data, int len){
		mDataBuf = new byte[len+5];
		mDataBuf[0] = (byte) 0xFF;
		mDataBuf[1] = (byte) 0xAA;
		mDataBuf[2] = (byte) 0xC9;
		mDataBuf[3] = (byte) ((len+5)&0xFF);
		for(int i = 0; i < len; i++){
			mDataBuf[4 + i] = data[i];
		}
		mDataBuf[4+len] = (byte) 0x0A;
		mCustomerProxy.sendCustomerDataToMcu(mDataBuf);
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
	private boolean prepareFileContent() {
		if (mFileContent == null || mFileContent.length <= 10*1024 /*|| mFileContent.length >= 80*1024*/){
			return false;
		}
		return true;
	}
	
	/**
	 * 升级流程
	 */
	private void processUpdate(int cmd){
		Log.i(TAG, "processUpdate cmd = " + cmd + ", step = " + mUpdateStep);
		switch (mUpdateStep) {
		case 0:{
			if (cmd != 0x6000){
				CallbackOnStatus(UpdateStatus.STATUS_ERROR, 3);
				return;
			}
			sendCustomerID();
			mUpdateStep = 1;
		}break;
		case 1:{
			if (cmd == 0x6006){
				CallbackOnStatus(UpdateStatus.STATUS_ERROR, 3);
				return;
			}
			else if (cmd != 0x6005){
				CallbackOnStatus(UpdateStatus.STATUS_ERROR, 3);
				return;
			}
			sendStartSendFile();
			mUpdateStep = 2;
			
		}break;
		case 2:{
			if (cmd != 0x6000){
				CallbackOnStatus(UpdateStatus.STATUS_ERROR, 3);
				return;
			}
			sendFileHead();
			mUpdateStep = 3;
			
		}break;
		case 3:{
			if (cmd != 0x6000){
				CallbackOnStatus(UpdateStatus.STATUS_ERROR, 3);
				return;
			}
			sendFileContent();
			mUpdateStep = 4;
		}break;
		
		case 4:{
			if (cmd != 0x6002){	//重发上一帧
				mHadSendDataNum -= mCurSendDataNum;
				sendFileContent();
				break;
			}
			if (mHadSendDataNum >= mFileLength){
				sendEndSendFile();
				mUpdateStep = 5;
				break;
			}
			
			sendFileHead();
			mUpdateStep = 3;
			CallbackOnStatus(UpdateStatus.STATUS_UPDATEING, mHadSendDataNum);
			
		}break;
		
		case 5:{
			if (cmd != 0x6003){
				CallbackOnStatus(UpdateStatus.STATUS_ERROR, 3);
				return;
			}
			mCustomerProxy.unregistCustomerInfoChangeListener(mMCUDataListener);
			bIsUpdateing = false;
			CallbackOnStatus(UpdateStatus.STATUS_FINISH, 0);
		}break;

		default:
			break;
		}
	}
	
	/**
	 * 发送客户ID
	 */
	private void sendCustomerID() {
		mSendCmdBuf[0] = mFileContent[3072];
		mSendCmdBuf[1] = mFileContent[3073];
		sendDataTo103(mSendCmdBuf, 2);
	}
	
	/**
	 * 开始升级文件
	 */
	private void sendStartSendFile() {
		mSendCmdBuf[0] = 0x00;
		mSendCmdBuf[1] = (byte) 0xA0;
		sendDataTo103(mSendCmdBuf, 2);
	}
	
	/**
	 * 发送将要发送文件内容的标头（内容长度+checksun长度）
	 */
	private void sendFileHead() {
		int sendDataNum = mHadSendDataNum + BLOCK_SIZE;
		if (sendDataNum > mFileLength){
			mCurSendDataNum = BLOCK_SIZE - (sendDataNum - mFileLength);
		}
		else{
			mCurSendDataNum = BLOCK_SIZE;
		}
		mSendCmdBuf[0] = (byte) (mCurSendDataNum + 2);
		mSendCmdBuf[1] = (byte) 0xA1;
		sendDataTo103(mSendCmdBuf, 2);
	}
	
	
	/**
	 * 发送文件内容+checksun
	 */
	private void sendFileContent() {
		int checkSum = 0;
		for(int i = 0; i < mCurSendDataNum; i++){
			mSendDataBuf[i] = mFileContent[mHadSendDataNum + i];
			checkSum += (mFileContent[mHadSendDataNum + i] & 0x000000FF);
//			Log.i(TAG, "sendFileContent i = " + i + ", data[i] = " + (mFileContent[mHadSendDataNum + i] & 0x000000FF) + "checkSun = " + checkSum);
		}
//		Log.i(TAG, "end one frame,checksun = " + checkSum);
		mSendDataBuf[mCurSendDataNum] = (byte) ((checkSum >> 8) & 0xFF);
		mSendDataBuf[mCurSendDataNum+1] = (byte) (checkSum & 0xFF);
		sendDataTo103(mSendDataBuf, mCurSendDataNum+2);
		mHadSendDataNum += mCurSendDataNum;
	}
	
	/**
	 * 完成升级
	 */
	private void sendEndSendFile() {
		mSendCmdBuf[0] = 0x00;
		mSendCmdBuf[1] = (byte) 0xA2;
		sendDataTo103(mSendCmdBuf, 2);
	}
	
	private class MCUDataListener implements ICustomerInfoChangedListener{

		@Override
		public void notify(int[] arg0, short[] data) {
			Log.i(TAG, "MCUDataListener notify [" + data[0] + "],[" + data[1] + "],[" + data[2] + "],[" + data[3] + "]");
			if (bIsUpdateing){
				if (data[2] == 0x07 && data[3] == 0xE8){
					int cmd = data[4]*256+data[5];
					processUpdate(cmd);
				}
//				int cmd = arg0[0]*256+arg0[1];
//				processUpdate(cmd);
			}
		}
		
	}
}
