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
import android.text.TextUtils;

import com.zhonghong.autotest.bean.ATConst;
import com.zhonghong.autotest.bean.ATEvent;
import com.zhonghong.autotest.service.c11.ProcessManager;
import com.zhonghong.logic.autotest.ATInterface;

/**
 * @author YC
 * @time 2017-2-6 下午6:17:00
 * TODO:
 */
public class LogicClient extends ABSClient{
	
	/**
	 * @param processManager
	 */
	public LogicClient(ProcessManager processManager) {
		super(processManager);
	}

	private static final int ID_SCREEN_MAIN = 1;
	private static final int ID_SCREEN_VICE = 2;
	
	private ATInterface mATInterface = null;
	
	@Override
	public void onObjectCreate() {
		super.onObjectCreate();
		bindLogicService();
	}
	
	@Override
	public void onObjectDestory() {
		super.onObjectDestory();
		unbindLogicService();
	}
	
	private void bindLogicService(){
		Intent it = new Intent("com.zhonghong.logic.autotest.ATService");
		
		mProcessManager.getATService().bindService(it, conn, Context.BIND_AUTO_CREATE);
		
	}
	
	private void unbindLogicService(){
		try {
			mProcessManager.getATService().unbindService(conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	ServiceConnection conn = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mATInterface = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mATInterface = ATInterface.Stub.asInterface(service);
		}
	};
	
	@Override
	public boolean onDispatch(ATEvent event) {
		if (event.type == ATConst.Logic.TYPE_LOGIC){
			return onProcess(event.cmd, event.info);
		}
		return false;
	}
	
	@Override
	protected void onResponse(int cmd, String info){
		if (mProcessManager != null){
			ATEvent atEvent = new ATEvent(ATConst.Logic.TYPE_LOGIC, cmd, info);
			mProcessManager.onResponseFromClient(atEvent);
		}
	}

	private boolean onProcess(int cmd, String info) {
		switch (cmd) {
		case ATConst.Logic.CMD_LOGIC_SET_VOL:
			if (!TextUtils.isEmpty(info)){
				if (info.startsWith("main::")){
					String volume = info.substring("main::".length());
					if (!TextUtils.isEmpty(volume)){
						if (volume.equals("mute")){
							setMute(ID_SCREEN_MAIN);
						}
						else{
							try {
								int val = Integer.parseInt(volume);
								setVolume(ID_SCREEN_MAIN, val);
							} catch (Exception e) {
							}
						}
					}
				}
				else if (info.startsWith("vice::")){
					String volume = info.substring("vice::".length());
					if (!TextUtils.isEmpty(volume)){
						if (volume.equals("mute")){
							setMute(ID_SCREEN_VICE);
						}
						else{
							try {
								int val = Integer.parseInt(volume);
								setVolume(ID_SCREEN_VICE, val);
							} catch (Exception e) {
							}
						}
					}
				}
				onResponse(ATConst.Logic.CMD_LOGIC_SET_VOL, "ok");
				return true;
			}
			onResponse(ATConst.Logic.CMD_LOGIC_SET_VOL, "notok");
			break;

		default:
			break;
		}
		return false;
	}


	
	/**
	 * 
	 * @param screenID
	 * @param val
	 */
	private void setVolume(int screenID, int val){
		if (mATInterface != null){
			try {
				mATInterface.setVolume(screenID, val);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * @param screenID
	 */
	private void setMute(int screenID){
		if (mATInterface != null){
			try {
				mATInterface.setMute(screenID);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
}
