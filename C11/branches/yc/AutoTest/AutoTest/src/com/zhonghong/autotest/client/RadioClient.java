/**
 * 
 */
package com.zhonghong.autotest.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.zh.radio.autotest.ATRadio;
import com.zhonghong.autotest.bean.ATConst;
import com.zhonghong.autotest.bean.ATEvent;
import com.zhonghong.autotest.service.c11.ProcessManager;
import com.zhonghong.logic.autotest.ATInterface;

/**
 * @author YC
 * @time 2017-2-8 下午3:38:46
 * TODO:
 */
public class RadioClient extends ABSClient {

	private static final String TAG = "RadioClient";
	private ATRadio mATRadio = null;
	/**
	 * @param processManager
	 */
	public RadioClient(ProcessManager processManager) {
		super(processManager);
	}
	
	

	@Override
	public void onObjectCreate() {
		super.onObjectCreate();
		bindRadioService();
	}



	@Override
	public void onObjectDestory() {
		unbindRadioService();
		super.onObjectDestory();
	}

	private void bindRadioService(){
		Intent it = new Intent("com.zh.radio.autotest.ATRadioService");
		mProcessManager.getATService().bindService(it, conn, Context.BIND_AUTO_CREATE);
	}
	
	private void unbindRadioService(){
		try {
			mProcessManager.getATService().unbindService(conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	ServiceConnection conn = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mATRadio = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mATRadio = ATRadio.Stub.asInterface(service);
		}
	};

	@Override
	public boolean onDispatch(ATEvent event) {
		if (event.type == ATConst.Radio.TYPE_RADIO){
			return onProcess(event.cmd, event.info);
		}
		return false;
	}
	
	@Override
	protected void onResponse(int cmd, String info) {
		if (mProcessManager != null){
			ATEvent atEvent = new ATEvent(ATConst.Radio.TYPE_RADIO, cmd, info);
			mProcessManager.onResponseFromClient(atEvent);
		}
	}

	/**
	 * @param cmd
	 * @param info
	 * @return
	 */
	private boolean onProcess(int cmd, String info) {
		switch (cmd) {
		case ATConst.Radio.CMD_RADIO_SET_BAND:
			if (setBand(info)){
				onResponse(ATConst.Radio.CMD_RADIO_SET_BAND, "ok");
			}
			else{
				onResponse(ATConst.Radio.CMD_RADIO_SET_BAND, "notok");
			}
			break;
		case ATConst.Radio.CMD_RADIO_SET_FREQ:
			if (setFreq(info)){
				onResponse(ATConst.Radio.CMD_RADIO_SET_FREQ, "ok");
			}
			else{
				onResponse(ATConst.Radio.CMD_RADIO_SET_FREQ, "notok");
			}
			break;

		default:
			break;
		}
		return false;
	}
	
	
	/**
	 * @param band: am/fm
	 * @return
	 */
	private boolean setBand(String band){
		Log.i(TAG, "set band = " +  band);
		if (mATRadio != null){
			try {
				mATRadio.setBand(band);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	/**
	 * @param freq as 87.5
	 * @return
	 */
	private boolean setFreq(String freq){
		Log.i(TAG, "set setFreq = " +  freq);
		if (mATRadio != null){
			try {
				mATRadio.setFreq(freq);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

}
