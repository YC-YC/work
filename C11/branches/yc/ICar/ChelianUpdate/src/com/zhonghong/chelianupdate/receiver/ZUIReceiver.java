package com.zhonghong.chelianupdate.receiver;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

import com.zhonghong.chelianupdate.R;
import com.zhonghong.chelianupdate.base.GlobalData;
import com.zhonghong.chelianupdate.base.MyApp;
import com.zhonghong.chelianupdate.bean.GroupVersionVo;
import com.zhonghong.chelianupdate.bean.UpdateVo;
import com.zhonghong.chelianupdate.utils.DialogManager;
import com.zhonghong.chelianupdate.utils.FileUtil;
import com.zhonghong.chelianupdate.utils.JSONParser;
import com.zhonghong.chelianupdate.utils.Saver;
import com.zhonghong.chelianupdate.utils.Utils;

public class ZUIReceiver extends BroadcastReceiver{

	private static final String TAG = "ZUIReceiver";
	private Context mContext;
	private GroupVersionVo groupVersionVo;
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals("com.zhonghong.zuiserver.BROADCAST"))
		{
			mContext=context;
			String extraString=intent.getStringExtra("car_speed");
			if(extraString!=null){
				handleCarRun(context, extraString);
			}
			extraString = intent.getStringExtra("remote_upgrade_enter");
			if (extraString != null){
				handleEnterUpgrade(context);
			}
		}
	}
	
	/**
	 * 打开升级界面
	 */
	private void handleEnterUpgrade(Context context){
		if (!GlobalData.bPermissionStatus){
//			Utils.ToastThread(Utils.getResourceString(R.string.retry_later));
			return;
		}
		String result=FileUtil.getGroupVersionVo(context);
		JSONParser parser = new JSONParser(result);
		if(parser.getStatus().isOk())
		{
			groupVersionVo = parser.getFullInfoCheckValid(mContext);
			List<UpdateVo> voList = groupVersionVo.getUpdateVoList();
			if (voList != null) {
				for (UpdateVo vo : voList) {
					Log.i(TAG, vo.getAppName());
				}
				DialogManager manager=DialogManager.getInstance();
				if (!manager.isDownloadDialogShowing()){
					manager.showDownloadDialog(context, groupVersionVo,false);
				}
			}
		}
	}
	
	
	/**
	 * 根据 车速判断提示升级
	 * @param context
	 * @param speedStr
	 */
	private void handleCarRun(Context context, String speedStr) {
		Log.i(TAG, "receiver carrun = " + speedStr);
		int speed = 0;
		try {
			speed = Integer.parseInt(speedStr);
		} catch (Exception e) {
		}
		if (speed > 0){
			GlobalData.bCarRun = true;
		}
		else{
			GlobalData.bCarRun = false;
		}
		if (GlobalData.bCarRun){
			DialogManager.getInstance().hideInstallDialog();
			return;
		}
		if (!GlobalData.bPermissionStatus){
			return;
		}
		String downloadState = Saver.getDownloadState();
		String installState = Saver.getInstallState();
		Log.i(TAG, "downloadState = " + downloadState + ", installState = " + installState);
		if (downloadState.equals(Saver.DOWNLOAD_STATE_FINISH) && installState.equals(Saver.INSTALL_STATE_DEFAULT)){
			if (!DialogManager.getInstance().isInstallDialogShowing()){
				String result = FileUtil.getGroupVersionVo(context);
				JSONParser parser = new JSONParser(result);
				if (parser.getStatus().isOk()) {
					GroupVersionVo groupVersionVo = parser.getFullInfoCheckValid(context);
					List<UpdateVo> voList = groupVersionVo.getUpdateVoList();
					if (voList != null) {
						DialogManager.getInstance().showInstallDialog(context, groupVersionVo);
					}
				}
			}
		}
	}
}
