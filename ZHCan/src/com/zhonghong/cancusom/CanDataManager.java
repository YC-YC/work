package com.zhonghong.cancusom;

import java.util.HashSet;
import java.util.Set;

import android.util.Log;

import com.zhonghong.aidl.CanInfoParcel;
import com.zhonghong.base.BaseApplication;


public class CanDataManager {
	
	private static final String TAG = "CanDataManager";
	public static DasAutoXBS lastAirInfo = new DasAutoXBS();
	public static DasAutoXBS currentAirInfo = new DasAutoXBS();
	
	private CanInfoParcel mCanInfoParcel = new CanInfoParcel();
	
	private static CanDataManager instance;
	private Set<CanDataChangeCallback> sets;
	public static CanDataManager getInstance() {
		if (instance == null) {
			instance = new CanDataManager();
		}
		return instance;
	}
	
	private CanDataManager(){
		Log.i(TAG, "new CanDataManager");
		sets = new HashSet<CanDataChangeCallback>();
	}
	
	public void register(CanDataChangeCallback callback){
		sets.add(callback);
	}
	public void unRegister(CanDataChangeCallback callback){
		sets.remove(callback);
	}
	
	public CanInfoParcel getCanInfoParcel(){
		mCanInfoParcel.setMblowMode(currentAirInfo.blowMode);
		mCanInfoParcel.setMwindSpeed(currentAirInfo.windSpeed);
		mCanInfoParcel.setAirCircurlationMode(currentAirInfo.AirCircurlationMode);
		mCanInfoParcel.setLeftTemperature(currentAirInfo.leftTemperature);
		mCanInfoParcel.setAutoHighWind(currentAirInfo.bAutoHighWind);
		
		return mCanInfoParcel;
	}
	public void checkAirInfoUpdate()
	{
		if (!currentAirInfo.equals(lastAirInfo))
		{
			for (CanDataChangeCallback callback: sets){
				callback.callback();
			}
			try {
				lastAirInfo = (DasAutoXBS) currentAirInfo.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public static DasAutoXBS getCanInfo(){
		return currentAirInfo;
	}
	
	public static void writecmdCan(byte[] data){
		BaseApplication.getInstanse().getCanService().writecmdCan(data);
	}
	
	public interface CanDataChangeCallback{
		void callback();
	}
}
