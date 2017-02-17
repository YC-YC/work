/**
 * 
 */
package com.zhonghong.autotest.client;

import android.bluetooth.BluetoothAdapter;

import com.zhonghong.autotest.bean.ATConst;
import com.zhonghong.autotest.bean.ATEvent;
import com.zhonghong.autotest.service.c11.ProcessManager;

/**
 * @author YC
 * @time 2017-2-8 下午3:38:46
 * TODO:
 */
public class BTClient extends ABSClient {

	/**
	 * @param processManager
	 */
	public BTClient(ProcessManager processManager) {
		super(processManager);
	}
	
	@Override
	public void onObjectCreate() {
		// TODO Auto-generated method stub
		super.onObjectCreate();
	}



	@Override
	public void onObjectDestory() {
		// TODO Auto-generated method stub
		super.onObjectDestory();
	}

	@Override
	public boolean onDispatch(ATEvent event) {
		if (event.type == ATConst.BT.TYPE_BT){
			return onProcess(event.cmd, event.info);
		}
		return false;
	}
	
	@Override
	protected void onResponse(int cmd, String info) {
		if (mProcessManager != null){
			ATEvent atEvent = new ATEvent(ATConst.BT.TYPE_BT, cmd, info);
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
		case ATConst.BT.CMD_BT_SET_ON_OFF:{
			boolean bOn = false;
			if ("on".equals(info)){
				bOn = true;
			}
			if (setBtOnOff(bOn)){
				onResponse(ATConst.BT.CMD_BT_SET_ON_OFF, "ok");
			}
			else{
				onResponse(ATConst.BT.CMD_BT_SET_ON_OFF, "notok");
			}
		}
			return true;
		case ATConst.BT.CMD_BT_SET_AUTO_CONN:
			/*if (setFreq(info)){
				onResponse(ATConst.Radio.CMD_RADIO_SET_FREQ, "ok");
			}
			else{
				onResponse(ATConst.Radio.CMD_RADIO_SET_FREQ, "notok");
			}*/
			break;
		case ATConst.BT.CMD_BT_GET_BT_ADDR:
			onResponse(ATConst.BT.CMD_BT_GET_BT_ADDR, getBtAddr());
			return true;

		default:
			break;
		}
		return false;
	}
	
	
	private boolean setBtOnOff(boolean bOn){
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bOn){
			if (bluetoothAdapter.isEnabled()){
				return false;
			}
			else{
				bluetoothAdapter.enable();
				return true;
			}
		}
		else{
			if (!bluetoothAdapter.isEnabled()){
				return false;
			}
			else{
				bluetoothAdapter.disable();
				return true;
			}
		}
	}
	
	private String getBtAddr(){
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		return bluetoothAdapter.getAddress();
	}

	
	
}
