/**
 * Copyright (c) 深圳市众鸿科技股份有限公司
 * @file_name SettingsTestActivity.java
 * @class com.nforetek.bt.demo.ui.SettingsTestActivity
 * @create 下午4:48:20
 */
package com.nforetek.bt.demo.ui;

import java.util.List;

import com.zh.bt.entry.BtDevice;
import com.zh.bt.service.BtSettingsService;
import com.zh.bt.service.BtSettingsService.LocalBinder;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

/**
 * 
 * <p></p>
 * 下午4:48:20
 *
 * @author ZH-SW-Lilc
 * @version 1.0.0
 */
public class SettingsTestActivity extends Activity{
	public static String TAG = SettingsTestActivity.class.getSimpleName();
	BtSettingsService mBtSettingsService;
	LocalBinder mBinder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_test);
		
		bindService( new Intent( "com.zh.bt.service.BtSettingsService" ), mConnection, BIND_AUTO_CREATE);
	}
	
	private ServiceConnection mConnection = new ServiceConnection() {
		
		@Override
		public void onServiceConnected(ComponentName arg0, IBinder localBinder) {
			Log.i("llc", "-----onServiceConnected---");
			mBinder = (LocalBinder) localBinder;
			mBtSettingsService = mBinder.getService();
		}
		
		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			Log.i("llc", "-----onServiceDisconnected---");
		}
		
	};

	
	public void back(View view) {
		finish();
	}


	public void state(View view) {
		if(mBtSettingsService != null){
			boolean isEnabled = mBtSettingsService.isBtEnabled();
			Log.i(TAG, "蓝牙连接状态==="+isEnabled);
		}
	}


	public void open(View view) {
		if(mBtSettingsService != null){
			Log.i(TAG, "-----打开-----");
			mBtSettingsService.setBtEnable();
		}
	}


	public void close(View view) {
		if(mBtSettingsService != null){
			Log.i(TAG, "-----关闭蓝牙-----");
			mBtSettingsService.setBtDisable();
		}
	}

	public void discoveryDevice(View view) {
		if(mBtSettingsService != null){
			Log.i(TAG, "-----扫描设备-----");
			mBtSettingsService.requestDiscoveryDevice();
		}
	}

	
	public void deviceList(View view) {
		if(mBtSettingsService != null){
			List<BtDevice> btDevices =  mBtSettingsService.getFoundedList();
			Log.i(TAG, "扫描设备到的设备数"+btDevices.size());
		}
	}

	public void connectDevice(View view) {
		if(mBtSettingsService != null){
			List<BtDevice> btDevices =  mBtSettingsService.getFoundedList();
			mBtSettingsService.requestToConectDevice(btDevices.get(0).getAddress());
			Log.i(TAG, "-----连接"+btDevices.get(0).getAddress()+"设备-----");
		}
	}
	
	public void pairedDeviceList(View view) {
		if(mBtSettingsService != null){
			List<BtDevice> btDevices =  mBtSettingsService.getPairedDeviceList();
			Log.i(TAG, "已配对设备数"+btDevices.size());
		}
	}
	
	

}
