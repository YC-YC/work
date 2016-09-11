package com.zhonghong.chelianupdate.receiver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import com.zhonghong.chelianupdate.base.AppConst;
import com.zhonghong.chelianupdate.base.MyApp;
import com.zhonghong.chelianupdate.bean.GroupVersionVo;
import com.zhonghong.chelianupdate.bean.UpdateVo;
import com.zhonghong.chelianupdate.utils.DialogManager;
import com.zhonghong.chelianupdate.utils.FileUtil;
import com.zhonghong.chelianupdate.utils.JSONParser;
import com.zhonghong.chelianupdate.utils.Saver;
import com.zhonghong.chelianupdate.utils.VersionUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver{
	private static final String TAG="Update";
	private GroupVersionVo groupVersionVo;
	private Context mContext;
	private String result;
	@Override
	public void onReceive(Context context, Intent intent) {
		mContext=context;	
		Log.i("Update","BootReceiver");
		bootCheckUpdateState(context);
	}
	
	private void bootCheckUpdateState(Context context){
//		String downloadStatus=Settings.System.getString(mContext.getContentResolver(), "updatedownload");
		//如果获取不到下载状态信息，则说明从来没有进行过更新下载，直接返回
//		if(downloadStatus==null)
//		{
//			return ;
//		}
		String downloadState = Saver.getDownloadState();
		Log.i(TAG, "downloadState = " + downloadState);
		if (Saver.DOWNLOAD_STATE_DEFAULT.equals(downloadState)){
			return;
		}
		initGroupVersionVo();	//初始化更新信息对象
		if(groupVersionVo==null)
		{
			return;
		}
		//如果下载信息为未完成，则弹出对话框提示继续进行下载
		if(!downloadState.equals(Saver.DOWNLOAD_STATE_FINISH))
		{
			List<UpdateVo> list=groupVersionVo.getUpdateVoList();
			if(list!=null&&list.size()>0)
			{
				handleDownloadResume();
			}
			return ;
		}
		String installState = Saver.getInstallState();
		Log.i(TAG, "installState = " + installState);
		if (!Saver.INSTALL_STATE_BEGIN.equals(installState)){
			return;
		}
//		String updateInstallStatus=Settings.System.getString(context.getContentResolver(), "updateinstall"); 
//		if (updateInstallStatus != null){
//			Log.i(TAG, "updateInstallStatus = " + updateInstallStatus);
//		}
//		//如果升级状态为空或者升级状态为未完成也直接返回，不过按道理进行到这步时已经判断了下载已完成，updateInstallStatus至少应该是begin才对
//		if(updateInstallStatus==null||updateInstallStatus.equals("finish"))
//		{
//			return ;
//		}
		//当进行升级的目标文件的版本为不可用时说明当前版本大于或等于提供的升级文件的版本，即升级已经完成
		boolean updateSuccess=true;
		List<UpdateVo> voList = groupVersionVo.getUpdateVoList();
		for(UpdateVo vo:voList)
		{
			if(vo.getAppId().equals("air")&&!VersionUtils.isAIRVersionValid(context, vo.getVersionCode()))
			{
				continue;
			}
			if(vo.getAppId().equals("can")&&!VersionUtils.isCANVersionValid(context, vo.getVersionCode()))
			{
				continue;
			}
			if(vo.getAppId().equals("8836")&&!VersionUtils.is8836VersionValid(context, vo.getVersionCode()))
			{
				continue;
			}
			if(vo.getAppId().equals("mcu")&&!VersionUtils.isMCUVersionValid(context, vo.getVersionCode()))
			{
				continue;
			}
			updateSuccess=false;
			break;
		}
		if(updateSuccess)
		{
			Log.i(TAG, "updateSuccess");
//			Settings.System.putString(context.getContentResolver(), "updateinstall", "finish"); 
			Saver.setInstallState(Saver.INSTALL_STATE_FINISH);
			deleteFilesIfUpdateFinished(context);
		}
	}
	
	private void handleDownloadResume()
	{
		DialogManager.getInstance().showDownloadDialog(mContext, groupVersionVo,false);
	}
	
	private void deleteFilesIfUpdateFinished(Context context)
	{
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
	
	private void initGroupVersionVo()
	{
		result=FileUtil.getGroupVersionVo(mContext);
		JSONParser parser = new JSONParser(result);
		if(parser.getStatus().isOk())
		{
			groupVersionVo = parser.getFullInfoCheckValid(mContext);
		}
	}
}
