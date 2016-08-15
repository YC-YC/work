/**
 * 
 */
package com.zhcar.utils;

import java.lang.reflect.Method;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author YC
 * @time 2016-7-21 下午3:50:22 TODO:移动数据网络
 */
public class GPRSManager implements ISwitch {

	private Context mContext;
	private ConnectivityManager mConnectivityManager;

	public GPRSManager(Context context) {
		super();
		this.mContext = context;
		mConnectivityManager = (ConnectivityManager) this.mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	private void setGPRSEnable(boolean bEnable) {
		Class ownerClass = mConnectivityManager.getClass();

		Class[] argsClass = new Class[1];
		argsClass[0] = boolean.class;

		Method method;
		try {
			method = ownerClass.getMethod("setMobileDataEnabled", argsClass);
			method.invoke(mConnectivityManager, bEnable);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private boolean getGPRSEnable() {
		
		Class ownerClass = mConnectivityManager.getClass();

		Class[] argsClass = null;
		Object[] argObject = null;
		Boolean isOpen = false;
		Method method;
		try {
			method = ownerClass.getMethod("getMobileDataEnabled", argsClass);
			isOpen = (Boolean) method.invoke(mConnectivityManager,argObject);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isOpen;
	}

	@Override
	public void turnOn() {
		setGPRSEnable(true);
	}

	@Override
	public void turnOff() {
		setGPRSEnable(false);
	}

	@Override
	public boolean isEnable() {
		return getGPRSEnable();
	}
	
	public boolean isNetWorkValilable(){
		NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
		return networkInfo == null? false: networkInfo.isAvailable();
	}

}
