/**
 * 
 */
package com.zhcar.utils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import android.util.Log;

/**
 * @author YC
 * @time 2016-7-12 下午7:53:13
 * TODO:用于非View处更新Ui的中转，类似于EventBus功能
 */
public class UpdateUiManager {

	public static final int CMD_UPDATE_BTSTATE = 1;

	private static final String TAG = "UpdateUiManager";
	
	private static UpdateUiManager instances;
	private Set<UpdateViewCallback> mUpdateViewCallbacks;
	
	public interface UpdateViewCallback{
		void onUpdate(int cmd);
	}
	
	public void register(UpdateViewCallback callback){
		mUpdateViewCallbacks.add(callback);
		Log.i(TAG, "regUpdateViewCallback ++++ size = " + mUpdateViewCallbacks.size());
	}
	
	public void unregister(UpdateViewCallback callback){
		mUpdateViewCallbacks.remove(callback);
		Log.i(TAG, "unRegUpdateViewCallback ++++ size = " + mUpdateViewCallbacks.size());
		
	}
	
	public static UpdateUiManager getInstances(){
		if (instances == null){
			synchronized (UpdateUiManager.class) {
				if (instances == null){
					instances = new UpdateUiManager();
				}
			}
		}
		return instances;
	}
	
	public void callUpdate(int cmd){
		Iterator<UpdateViewCallback> iterator = mUpdateViewCallbacks.iterator();
		while (iterator.hasNext()){
			UpdateViewCallback callback = iterator.next();
			callback.onUpdate(cmd);
		}
	}
	
	private UpdateUiManager(){
		mUpdateViewCallbacks = new HashSet<UpdateViewCallback>();
	}
}
