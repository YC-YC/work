/**
 * 
 */
package com.zhonghong.autotestclient;

import android.os.RemoteException;
import android.text.TextUtils;

import com.zhonghong.autotestlib.bean.ABSManager;
import com.zhonghong.autotestlib.bean.ATConst;

/**
 * @author YC
 * @time 2017-2-6 下午6:17:00
 * TODO:
 */
public class LogicManager extends ABSManager{
	
	private static final int ID_SCREEN_MAIN = 1;
	private static final int ID_SCREEN_VICE = 2;

	@Override
	public void onObjectCreate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onObjectDestory() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean onProcess(int cmd, String info) {
		switch (cmd) {
		case ATConst.Logic.CMD_LOGIC_SET_VOL:
			if (!TextUtils.isEmpty(info)){
				if (info.startsWith("main::")){
					String volume = info.substring("main::".length());
					if (!TextUtils.isEmpty(volume)){
						if (volume.equals("mute")){
							setMute(LogicManager.ID_SCREEN_MAIN);
						}
						else{
							try {
								int val = Integer.parseInt(volume);
								setVolume(LogicManager.ID_SCREEN_MAIN, val);
							} catch (Exception e) {
							}
						}
					}
				}
				else if (info.startsWith("vice::")){
					String volume = info.substring("vice::".length());
					if (!TextUtils.isEmpty(volume)){
						if (volume.equals("mute")){
							setMute(LogicManager.ID_SCREEN_VICE);
						}
						else{
							try {
								int val = Integer.parseInt(volume);
								setVolume(LogicManager.ID_SCREEN_VICE, val);
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

	private void onResponse(int cmd, String info){
		if (mATConnService != null){
			try {
				mATConnService.onResponse(ATConst.Logic.TYPE_LOGIC, 
						cmd, info);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void setVolume(int screenID, int val){
		
	}
	
	private void setMute(int screenID){
		
	}

	

	
}
