package com.zhonghong.chelianupdate.service;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.zhonghong.chelianupdate.R;
import com.zhonghong.chelianupdate.activity.UpdateActivity;
import com.zhonghong.chelianupdate.base.AppConst;
import com.zhonghong.chelianupdate.base.GlobalData;
import com.zhonghong.chelianupdate.bean.CarInfo;
import com.zhonghong.chelianupdate.bean.GroupVersionVo;
import com.zhonghong.chelianupdate.bean.UpdateStatusInfo;
import com.zhonghong.chelianupdate.bean.UpdateVo;
import com.zhonghong.chelianupdate.utils.DialogManager;
import com.zhonghong.chelianupdate.utils.FileUtil;
import com.zhonghong.chelianupdate.utils.InfoUtils;
import com.zhonghong.chelianupdate.utils.JSONParser;
import com.zhonghong.chelianupdate.utils.Saver;
import com.zhonghong.chelianupdate.utils.SignatureGenerator;
import com.zhonghong.sdk.android.utils.ZToast;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateService extends Service {

	private static final String TAG = "Update";
	private static final String URL_SOURCE_PART="update-center/getUpdateInfoByVin";
	private static final String URL_REPORT_UPDATE_STATUS="update-center/reportUpdateStatus";
	private static final String URL_HOST="http://cowinmguat.timasync.com/";
	
	private GroupVersionVo groupVersionVo;
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG,"ServiceStart");
		String checkUrl=InfoUtils.getUrlPart(URL_HOST, URL_SOURCE_PART);
		if(checkUrl!=null)
		{
			updateCheck(checkUrl);
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	private void updateCheck(String url) {
		Log.i(TAG, url);
		HttpUtils http = new HttpUtils();
		http.configDefaultHttpCacheExpiry(1000);

		try {
			http.send(HttpRequest.HttpMethod.GET, url,
					new RequestCallBack<String>() {
						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							String result=responseInfo.result;
							//测试，修改了返回的result中的版本号信息
							//result=result.replaceAll("\"1[a-c]\"", "\"HE-2016-08-11-V1.05.2.1\"");
							/*JSONParser parser = new JSONParser(responseInfo.result);*/
							JSONParser parser = new JSONParser(result);
							UpdateStatusInfo status = parser.getStatus();
							if (!status.isOk()) { // 如果返回的信息不是有效的升级信息就进行错误处理
								handleCheckFail(status);
							} else {
								handleCheckSuccess(parser);
							}
						}
						@Override
						public void onFailure(HttpException error, String msg) {
							groupVersionVo=null;
//							Toast.makeText(getApplicationContext(),
//									"Update encounter error",
//									Toast.LENGTH_LONG).show();
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.stopSelf();
		}
	}
	
	
	 @Override
	public void onDestroy() {
		 Log.i(TAG,"Service destroy");
		super.onDestroy();
	}

	private void handleCheckFail(UpdateStatusInfo status)
	{
		groupVersionVo=null;
//		Toast.makeText(
//				getApplicationContext(),
//				status.getErrorMessage()
//						+ " ErrorCode:"
//						+ status.getErrorCode(),
//				Toast.LENGTH_LONG).show();
	}
	
	private void handleCheckSuccess(JSONParser parser)
	{
		String updateInfoSavePath=getApplicationContext().getFilesDir().getAbsolutePath()+AppConst.UPDATE_INFO_FILE_NAME;
		File file=new File(updateInfoSavePath);
		FileWriter fw=null;
		try {
			fw=new FileWriter(file,false);
			fw.write(parser.getJsonString());
			fw.flush();
			fw.close();
			Log.i(TAG,"Writer to file");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		FileUtil.DeleteFile(new File(AppConst.DOWNLOAD_TARGET));		//删除在升级文件夹内的原有升级文件
		// 获取升级信息
		Log.i(TAG,"test");
		groupVersionVo = parser.getFullInfoCheckValid(getApplicationContext());
		List<UpdateVo> voList = groupVersionVo.getUpdateVoList();
		if (voList != null&&voList.size()>0) {
//			Settings.System.putString(getContentResolver(), "updatedownload", "begin"); 
			Saver.setDownloadState(Saver.DOWNLOAD_STATE_BEGIN);
			if (DialogManager.getInstance().isInstallDialogShowing()){
				DialogManager.getInstance().hideInstallDialog();
			}
			DialogManager.getInstance().showDownloadDialog(getApplicationContext(), groupVersionVo,true);	
		}
		
		GroupVersionVo full=parser.getFullInfo();
		List<UpdateVo> fullList=full.getUpdateVoList();
		for(UpdateVo vo:fullList)
		{
			sendPushed(vo.getAppId());
		}
		//这里有一点，如果服务器发过来的版本比本地版本等级低，那么可能就不会有推送的报告返回去,所以就改成了所有的都会报告推送
		//如果需要只报告有效版本的信息，那么就把上面的推送逻辑挪到if (voList != null&&!voList.isEmpty())里面去，fullList换成voList就行
		
		//发送广播，表示已接受到发出的广播，通知发送方不需要再次进行发送
		Intent broadCastIntent=new Intent();
		broadCastIntent.setAction("com.remoteupdate.semisky2tima_reply");
		sendBroadcast(broadCastIntent);
	}
	
	private void sendPushed(String appId)
	{
		Log.i("Update","send pushed: "+appId);
		HttpUtils http = new HttpUtils();
		try {
			http.send(HttpRequest.HttpMethod.GET,InfoUtils.getUrlPart(URL_HOST, URL_REPORT_UPDATE_STATUS,appId,"PUSHSUCCEED"),
					new RequestCallBack<String>() {
						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							Log.i(TAG,responseInfo.result);
						}
						@Override
						public void onFailure(HttpException error, String msg) {
							Log.i(TAG,msg);
						}
					});
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
