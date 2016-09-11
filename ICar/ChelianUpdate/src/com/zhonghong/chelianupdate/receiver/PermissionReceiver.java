/**
 * 
 */
package com.zhonghong.chelianupdate.receiver;

import java.io.File;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.zhonghong.chelianupdate.base.AppConst;
import com.zhonghong.chelianupdate.base.GlobalData;
import com.zhonghong.chelianupdate.bean.GroupVersionVo;
import com.zhonghong.chelianupdate.bean.UpdateVo;
import com.zhonghong.chelianupdate.utils.DialogManager;
import com.zhonghong.chelianupdate.utils.FileUtil;
import com.zhonghong.chelianupdate.utils.InfoUtils;
import com.zhonghong.chelianupdate.utils.JSONParser;
import com.zhonghong.chelianupdate.utils.Saver;
import com.zhonghong.chelianupdate.utils.VersionUtils;

/**
 * @author YC
 * @time 2016-7-7 下午4:28:13
 * TODO:鉴权广播接收
 */
public class PermissionReceiver extends BroadcastReceiver {

	private static final String ACTION_PERMISSION = "com.tima.Permission";
	private static final String TAG = "update";
	private String result = "";
	@Override
	public void onReceive(Context context, Intent intent) {
		if (ACTION_PERMISSION.equals(intent.getAction())){
			String status = intent.getStringExtra("status");
			if ("succeed".equals(status)){
				GlobalData.bPermissionStatus = true;
				handlePermission(context);
			}
		}
	}

	/**
	 * 
	 */
	private void handlePermission(Context context) {
		String downloadState = Saver.getDownloadState();
		String installState = Saver.getInstallState();
		Log.i(TAG, "downloadState = " + downloadState + ", installState = " + installState);
		if (downloadState.equals(Saver.DOWNLOAD_STATE_FINISH)
				&& installState.equals(Saver.INSTALL_STATE_BEGIN)){
			checkIfUpgradeOk(context);
		}
		else if (downloadState.equals(Saver.DOWNLOAD_STATE_FINISH)
				&& installState.equals(Saver.INSTALL_STATE_DEFAULT)){
			if (!GlobalData.bCarRun){
				result = FileUtil.getGroupVersionVo(context);
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

	/**
	 * 检测是否升级成功
	 */
	private void checkIfUpgradeOk(Context context) {
		result = FileUtil.getGroupVersionVo(context);
		JSONParser parser = new JSONParser(result);
		if (parser.getStatus().isOk()) {
//			GroupVersionVo groupVersionVo = parser.getFullInfoCheckValid(context);
			GroupVersionVo groupVersionVo = parser.getFullInfo();
			List<UpdateVo> voList = groupVersionVo.getUpdateVoList();
			if (voList != null && voList.size() > 0) {
				boolean updateSuccess=true;
				Log.i(TAG, "voList.size " + voList.size());
				for(UpdateVo vo:voList) {
					if(vo.getAppId().equals("air")&&!VersionUtils.isAIRVersionValid(context, vo.getVersionCode())) {
						continue;
					}
					if(vo.getAppId().equals("can")&&!VersionUtils.isCANVersionValid(context, vo.getVersionCode())) {
						continue;
					}
					if(vo.getAppId().equals("8836")&&!VersionUtils.is8836VersionValid(context, vo.getVersionCode())) {
						continue;
					}
					if(vo.getAppId().equals("mcu")&&!VersionUtils.isMCUVersionValid(context, vo.getVersionCode())) {
						continue;
					}
					updateSuccess=false;
					break;
				}
				if(updateSuccess)
				{
					Log.i(TAG, "updateSuccess voList.size = " + voList.size());
					for(UpdateVo vo:voList) {
						pushSucceedStatus(vo.getAppId());
					}
					Saver.setInstallState(Saver.INSTALL_STATE_FINISH);
					deleteFilesIfUpdateFinished(context);
				}
			}
		}
	}
	
	private void deleteFilesIfUpdateFinished(Context context)
	{
		Log.i(TAG, "deleteFiles");
		JSONParser parser=new JSONParser(result);
		GroupVersionVo oldObj=parser.getFullInfo();
		List<UpdateVo> voList=oldObj.getUpdateVoList();
		for (UpdateVo vo : voList) {
			File file = new File(AppConst.DOWNLOAD_TARGET+ vo.getFileName());
			if (file.exists()) {
				file.delete();
			}
		}
	}
	
	private void pushSucceedStatus(String appId)
	{
		try {
			Log.i("Update","Success: "+appId);
			HttpUtils http = new HttpUtils();
			http.send(HttpRequest.HttpMethod.GET,InfoUtils.getUrlPart(AppConst.URL_HOST, AppConst.URL_REPORT_UPDATE_STATUS,appId,"SUCCEED"),
					new RequestCallBack<String>() {
						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							Log.i("Update","Push Cancel "+responseInfo.result);
						}
						@Override
						public void onFailure(HttpException error, String msg) {
						}
					});
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

}
