/**
 * 
 */
package com.zhonghong.mediascanner.scan;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.text.TextUtils;

import com.zhonghong.mediascanner.BaseApplication;
import com.zhonghong.mediascanner.IDataMode;
import com.zhonghong.mediascanner.bean.DeviceInfo;
import com.zhonghong.mediascanner.bean.ScanCallback;
import com.zhonghong.mediascanner.saver.DevicePool;
import com.zhonghong.mediascanner.saver.IRecordDevice;
import com.zhonghong.mediascanner.saver.RecordDeviceFactory;
import com.zhonghong.mediascanner.utils.FileUtil;
import com.zhonghong.mediascanner.utils.ILifeCycle;
import com.zhonghong.mediascanner.utils.L;
import com.zhonghong.mediascanner.utils.Utils;

/**
 * @author YC
 * @time 2016-12-19 下午7:20:59
 * TODO:扫描池管理,用于扫描处理
 */
public class ScanPool implements ILifeCycle, ScanCallback{

	private static final String TAG = "ScanPool";

	private HashMap<String, ScanClient> mScanPool = new HashMap<String, ScanClient>();
	
//	private DevicePool mDevicePool;
	private IDataMode mDateMode;
	private IRecordDevice mRecordDevice;
	
	public ScanPool(IDataMode dataMode){
//		mDevicePool = devicePool;
		mDateMode = dataMode;
		mRecordDevice = RecordDeviceFactory.getRecordDevice();
	}
	
	public synchronized void startScanDevice(String device){
		String uuid = FileUtil.getDeviceUuid(BaseApplication.getApplication(), device);
		if (TextUtils.isEmpty(uuid)){
			L.i(TAG, "null uuid");
			return;
		}
		
		ScanClient scanClient = mScanPool.get(uuid);
		if (scanClient == null){
			scanClient = new ScanClient(device, uuid, mDateMode);
			mScanPool.put(uuid, scanClient);
		}
		scanClient.startScanDevice(this);
	}
	
	
	/**
	 * 设备是否在扫描中
	 * @param device
	 * @return
	 */
	public boolean isDeviceScanning(String device){
		String uuid = FileUtil.getDeviceUuid(BaseApplication.getApplication(), device);
		ScanClient scanClient = mScanPool.get(uuid);
		return (scanClient == null) ? false: true;
	}
	
	public DeviceInfo getScanDeviceInfo(String device){
		String uuid = FileUtil.getDeviceUuid(BaseApplication.getApplication(), device);
		ScanClient scanClient = mScanPool.get(uuid);
		if (scanClient != null){
			return scanClient.getScanDevice();
		}
		return null;
	}
	
	@Override
	public void onObjectCreate() {
		Iterator<Entry<String, ScanClient>> iterator = mScanPool.entrySet().iterator();
		while(iterator.hasNext()){
			iterator.next().getValue().onObjectCreate();
		}
	}

	@Override
	public void onObjectDestory() {
		Iterator<Entry<String, ScanClient>> iterator = mScanPool.entrySet().iterator();
		while(iterator.hasNext()){
			iterator.next().getValue().onObjectDestory();
		}
	}
	
	@Override
	public void onStatus(String device, int status, String info) {
		switch (status) {
		case ScanCallback.STATUS_START_SCAN:
			Utils.sendStartScanBroadcast(device);
			L.startTime("扫描设备整个过程" + device);
			break;
		case ScanCallback.STATUS_IN_SCANNING:
			break;
		case ScanCallback.STATUS_FINISH_SCAN:{
			String uuid = FileUtil.getDeviceUuid(BaseApplication.getApplication(), device);
			mRecordDevice.updateDevice(uuid);
			mScanPool.remove(uuid);
			Utils.sendFinishScanBroadcast(device);
			L.endUseTime("扫描设备整个过程" + device);
		}break;
		case ScanCallback.STATUS_BREAK_SCAN:{
			String uuid = FileUtil.getDeviceUuid(BaseApplication.getApplication(), device);
			mScanPool.remove(uuid);
		}break;
		case ScanCallback.STATUS_START_DB_LOAD:
			break;
		case ScanCallback.STATUS_FINISH_DB_LOAD:
			break;
		case ScanCallback.STATUS_START_DB_SAVE:
			break;
		case ScanCallback.STATUS_FINISH_DB_SAVE:{
//			String uuid = FileUtil.getDeviceUuid(BaseApplication.getApplication(), device);
//			mScanPool.remove(uuid);
		}
			break;
		default:
			L.i(TAG, "unknown status = " + status);
			break;
		}
	}
}
