package com.zhonghong.chelianupdate.dialog;

import java.io.File;
import java.security.spec.MGF1ParameterSpec;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.zhonghong.chelianupdate.R;
import com.zhonghong.chelianupdate.activity.UpdateActivity;
import com.zhonghong.chelianupdate.activity.UpdateActivity.DownloadCallBack;
import com.zhonghong.chelianupdate.base.AppConst;
import com.zhonghong.chelianupdate.base.MyApp;
import com.zhonghong.chelianupdate.bean.DownloadInfo;
import com.zhonghong.chelianupdate.bean.GroupVersionVo;
import com.zhonghong.chelianupdate.bean.SimpleAppInfo;
import com.zhonghong.chelianupdate.bean.UpdateVo;
import com.zhonghong.chelianupdate.service.DownloadService;
import com.zhonghong.chelianupdate.utils.DialogManager;
import com.zhonghong.chelianupdate.utils.DownloadManager;
import com.zhonghong.chelianupdate.utils.InfoUtils;
import com.zhonghong.chelianupdate.utils.Saver;

/**
 * 
 * 第一次接收到升级广播时的提示对话框
 ***/
public class NewUpdateDialog extends Dialog{
	private final String TAG="";
	private TextView txvUpdateInfo;
    private Button btnPositive;
    private Button btnNegative;
	private List<TextView> appNameTextViewList;
	private List<TextView> appVersionTextViewList;
	private GroupVersionVo groupVersionVo;
	private Context mContext;
	private DownloadManager downloadManager;
	private List<SimpleAppInfo> appInfoList;
	public NewUpdateDialog(Context context,GroupVersionVo groupVersionVo) {
		super(context,android.R.style.Theme_Translucent_NoTitleBar);
		mContext=context;
		this.groupVersionVo=groupVersionVo;
		init();
	}
	
	private void init() {
		View contentView = LayoutInflater.from(mContext).inflate(R.layout.update_popupwindow, null);
		initTextViews(contentView);
		txvUpdateInfo=(TextView) contentView.findViewById(R.id.txv_update_info);
	    btnPositive=(Button) contentView.findViewById(R.id.btn_positive);
	    btnNegative=(Button) contentView.findViewById(R.id.btn_negative);
	    StringBuilder sb=new StringBuilder("发布说明:\n");
	    List<UpdateVo> voList=groupVersionVo.getUpdateVoList();
	    for(UpdateVo vo:groupVersionVo.getUpdateVoList())
		{
			SimpleAppInfo info=new SimpleAppInfo(vo,AppConst.DOWNLOAD_TARGET);
			appInfoList.add(info);
		}
	    for(int i=0;i<voList.size();i++)
	    {
	    	appNameTextViewList.get(i).setText(voList.get(i).getAppName());
	    	appVersionTextViewList.get(i).setText(voList.get(i).getVersionCode());
	    }
	    txvUpdateInfo.setText(sb.toString());
	    btnPositive.setOnClickListener(mPositiveButtonOnClickListener);
	    btnNegative.setOnClickListener(mNegativeButtonOnClickListener);
		super.setContentView(contentView);
	}
	
	private void initTextViews(View contentView)
	{
		appNameTextViewList=new ArrayList<TextView>();
		appVersionTextViewList=new ArrayList<TextView>();
		appNameTextViewList.add((TextView) contentView.findViewById(R.id.txv_update_info_app_name01));
		appNameTextViewList.add((TextView) contentView.findViewById(R.id.txv_update_info_app_name02));
		appNameTextViewList.add((TextView) contentView.findViewById(R.id.txv_update_info_app_name03)); 
		appNameTextViewList.add((TextView) contentView.findViewById(R.id.txv_update_info_app_name04)); 
		appNameTextViewList.add((TextView) contentView.findViewById(R.id.txv_update_info_app_name05)); 
		
		
		appVersionTextViewList.add((TextView) contentView.findViewById(R.id.txv_update_info_app_version01));
		appVersionTextViewList.add((TextView) contentView.findViewById(R.id.txv_update_info_app_version02));
		appVersionTextViewList.add((TextView) contentView.findViewById(R.id.txv_update_info_app_version03));
		appVersionTextViewList.add((TextView) contentView.findViewById(R.id.txv_update_info_app_version04));
		appVersionTextViewList.add((TextView) contentView.findViewById(R.id.txv_update_info_app_version05));
	}
	
	@Override
	public void onBackPressed() {
		return;
	}
	
	private View.OnClickListener mPositiveButtonOnClickListener=new View.OnClickListener()
	{
		@Override
		public void onClick(View view) {
			downloadManager=DownloadService.getDownloadManager(mContext);
			List<UpdateVo> voList=groupVersionVo.getUpdateVoList();
			for (int i = 0; i < voList.size(); i++) {
				UpdateVo vo = voList.get(i);
				try {
					if(downloadManager.containsDownload(vo.getFileUrl()))
					{
						downloadManager.resumeDownload(vo.getFileUrl(),AppConst.DOWNLOAD_TARGET+vo.getFileName(), new DownloadCallBack(i));
					}
					else
					{
						downloadManager.addNewDownload(vo.getFileUrl(),vo.getFileName(),AppConst.DOWNLOAD_TARGET+vo.getFileName(),true, false, new DownloadCallBack(i));
					}
				} catch (DbException e) {
					e.printStackTrace();
				}
			}
		    dismiss();
		}
	};
	
	public class DownloadCallBack extends RequestCallBack<File> {
		private int pos;
		public DownloadCallBack(int pos)
		{
			this.pos=pos;
		}
		@Override
		public void onFailure(HttpException arg0, String arg1) {
		}
		@Override
		public void onLoading(long total, long current, boolean isUploading) {
			super.onLoading(total, current, isUploading);
			Log.i(TAG,"...");
			appInfoList.get(pos).setDownloaded(current);
		}
		@Override
		public void onSuccess(ResponseInfo<File> arg0) {
			try {
				reportSuccess(appInfoList.get(pos).getAppId());
			} catch (Exception e) {
				e.printStackTrace();
			}		
			//如果所有的升级文件都完成了下载，则将系统数据库中的下载状态设置为已完成
			boolean finished=true;
			for(SimpleAppInfo info:appInfoList)
			{
				if(info.getDownloaded()<info.getFileSize())
				{
					finished=false;//return;
					break;
				}
			}
			if(finished)
			{
				Settings.System.putString(mContext.getContentResolver(), "updatedownload", "finish");
				Settings.System.putString(mContext.getContentResolver(), "updateinstall", "begin");
				cleanTasks();
				handleUpdate();
			}
		}
	}
	
	private void reportSuccess(String appId)
	{
		HttpUtils http=new HttpUtils();		
		String url=InfoUtils.getUrlPart(Saver.getHostUrl(), AppConst.URL_REPORT_UPDATE_STATUS,appId,"DOWNLOAD_SUCCESS");
		http.send(HttpRequest.HttpMethod.GET, url,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						Log.i(TAG,"finish");
						Log.i(TAG,responseInfo.result);			
					}
					@Override
					public void onFailure(HttpException error, String msg) {
						Log.i(TAG,msg);
					}
				});
	}
	
	private View.OnClickListener mNegativeButtonOnClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View view) {
			for(int i=0;i<groupVersionVo.getUpdateVoList().size();i++)
			{
				pushCancelStatus(groupVersionVo.getUpdateVoList().get(i).getAppId());
			}
			dismiss();
		}
	};
	
	private void pushCancelStatus(String appId)
	{
		try {
			Log.i("Update","Cancel: "+appId);
			HttpUtils http = new HttpUtils();
			http.send(HttpRequest.HttpMethod.GET,InfoUtils.getUrlPart(Saver.getHostUrl(), AppConst.URL_REPORT_UPDATE_STATUS,appId,"CANCEL"),
					new RequestCallBack<String>() {
						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							Log.i("Update",responseInfo.result);
						}
						@Override
						public void onFailure(HttpException error, String msg) {
						}
					});
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 清除XUtils内置的下载信息列表
	 * */
	private void cleanTasks()
	{
		try {
			if(downloadManager==null)
			{
				downloadManager=DownloadService.getDownloadManager(mContext);
			}
			downloadManager.stopAllDownload();
			for (int i = downloadManager.getDownloadInfoListCount()-1; i >=0; i--) {
				DownloadInfo info = downloadManager.getDownloadInfo(i);
				downloadManager.removeDownload(info);
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
	}
	
	private void handleUpdate()
	{
		Log.i(TAG,"update");
		cleanTasks();
		if(!isCarStopped())
		{
			AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
			builder.setTitle("系统提示");
			builder.setMessage("行车时无法进行升级，请妥当停车后在设置面板中选择升级。");
			builder.setPositiveButton("确定",new DialogInterface.OnClickListener() 
			{
				@Override
				public void onClick(DialogInterface dialog,int which) 
				{
					dialog.dismiss();
				}
			});
			builder.show();
		}
		else
		{
			DialogManager.getInstance().showInstallDialog(mContext, groupVersionVo);
			//AlertWindowUtils.showInstallAlertWindow(getApplicationContext(), groupVersionVo);
		}
	}
	
	private boolean isCarStopped()
	{
		int speed=Settings.System.getInt(mContext.getContentResolver(), "carspeed",1);//默认值最好为1
		return speed==0;
	}
}
