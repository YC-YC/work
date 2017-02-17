/**
 * 
 */
package com.zhonghong.mediascanner.saver;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;

import com.zhonghong.mediascanerlib.config.Config;
import com.zhonghong.mediascanner.BaseApplication;
import com.zhonghong.mediascanner.bean.DeviceInfo;
import com.zhonghong.mediascanner.utils.FileUtil;
import com.zhonghong.mediascanner.utils.L;

/**
 * @author YC
 * @time 2016-12-10 上午11:30:58
 * TODO:设备池，用于保存完成扫描的设备数据
 */
public class DevicePool{

	private static final String TAG = "DevicePool";
	
	
	/**保存从数据库加载的数据*/
	private List<DeviceInfo> mLoadedDeviceInfos = new ArrayList<DeviceInfo>();

	
	public DevicePool(){
	}
	
	/**获取加载到的数据*/
	public synchronized DeviceInfo getLoadedDeviceInfo(String uuid){
		for (int i = 0; i < mLoadedDeviceInfos.size(); i++){
			DeviceInfo deviceInfo = mLoadedDeviceInfos.get(i);
			if (deviceInfo.getUuid().equals(uuid)){
				return deviceInfo;
			}
		}
		return null;
	}
	
	/**更新数据到加载到的数据中*/
	public synchronized void setLoadedDeviceInfo(DeviceInfo info){
		for (int i = 0; i < mLoadedDeviceInfos.size(); i++){
			DeviceInfo deviceInfo = mLoadedDeviceInfos.get(i);
			if (info.getUuid().equals(deviceInfo.getUuid())){
				mLoadedDeviceInfos.remove(i);
				break;
			}
		}
		info.setMounted(true);
		mLoadedDeviceInfos.add(info);
		L.i(TAG, "dbDeviceInfo size = " + mLoadedDeviceInfos.size());
	}
	
	/**
	 * 清除一些数据
	 * @param device
	 */
	public synchronized void unMountDevice(String device){
		String uuid = FileUtil.getDeviceUuid(BaseApplication.getApplication(), device);
		if (TextUtils.isEmpty(uuid)){
			L.i(TAG, "null uuid");
			return;
		}
		for (int i = 0; i < mLoadedDeviceInfos.size(); i++){
			DeviceInfo deviceInfo = mLoadedDeviceInfos.get(i);
			if (uuid.equals(deviceInfo.getUuid())){
				deviceInfo.setMounted(false);
			}
		}
		
		if (Config.LIMITED_SAVE_DEVICEINFO_NUM){
			if (mLoadedDeviceInfos.size() > Config.SAVE_DEVICEINFO_NUM){
				for (int i = 0; i < (mLoadedDeviceInfos.size() - Config.SAVE_DEVICEINFO_NUM); i++){
					DeviceInfo deviceInfo = mLoadedDeviceInfos.get(i);
					if (!deviceInfo.getMounted()){
						L.i(TAG, "remove data of " + mLoadedDeviceInfos.get(i).getUuid());
						mLoadedDeviceInfos.remove(i);
//						deviceInfo = null;
						//垃圾回收，但主动调用作用不大
//						System.gc();
						break;
					}
				}
			}
		}
	}
	
}
