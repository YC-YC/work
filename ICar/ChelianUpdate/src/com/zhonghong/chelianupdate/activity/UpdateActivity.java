package com.zhonghong.chelianupdate.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.zhonghong.chelianupdate.R;
import com.zhonghong.chelianupdate.adapter.UpdateOptionAdapter;
import com.zhonghong.chelianupdate.base.AppConst;
import com.zhonghong.chelianupdate.base.MyApp;
import com.zhonghong.chelianupdate.bean.DownloadInfo;
import com.zhonghong.chelianupdate.bean.GroupVersionVo;
import com.zhonghong.chelianupdate.bean.SimpleAppInfo;
import com.zhonghong.chelianupdate.bean.UpdateVo;
import com.zhonghong.chelianupdate.service.DownloadService;
import com.zhonghong.chelianupdate.utils.DialogManager;
import com.zhonghong.chelianupdate.utils.DownloadManager;
import com.zhonghong.chelianupdate.utils.FileUtil;
import com.zhonghong.chelianupdate.utils.InfoUtils;
import com.zhonghong.sdk.android.utils.AppManager;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class UpdateActivity extends Activity implements OnClickListener{
	private static final String TAG="UpdateActivity";
	private static final String URL_REPORT_UPDATE_STATUS="update-center/reportUpdateStatus";
	private static final String URL_HOST="http://cowinmguat.timasync.com/";
	
	private GroupVersionVo groupVersionVo;
	private ListView lvUpdateOptions;
	private List<SimpleAppInfo> appInfoList;
	private UpdateOptionAdapter adapter;
	private Button btnConfirm;
	private Button btnCancel;
	private Button btnUpdate;
	
	private Bundle extra;
	private DownloadManager downloadManager;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update);
		
		extra= getIntent().getExtras();
		if(extra==null)
		{
			finish();
		}
		groupVersionVo=(GroupVersionVo) extra.getSerializable("data");
		if(groupVersionVo==null)
		{
			finish();
		}
		downloadManager = DownloadService.getDownloadManager(getApplicationContext());
		String optionTime=extra.getString("time");
		if(optionTime!=null&&optionTime.equals("first"))			//当新的升级提醒第一次到来时，删除旧的升级文件
		{
			FileUtil.DeleteFile(new File(AppConst.DOWNLOAD_TARGET));
			cleanTasks();
		}
		
		//不知道是不是升级的服务器有问题，下载更新文件时经常无法断点续传，而且是有时候可以有时候不行，但是下载其它东西，例如下面的flash player就
		//完全没有这样的问题，无论是退出应用断网重连还是暴力重启各种姿势都可以继续下载，简直某该
		//如果要测试断点续传功能就把下面那段加上，当然因为没有判断为空所以升级项目要有空调才行....
		/*UpdateVo vo=groupVersionVo.getAir();
		vo.setFileSize(1196240);
		vo.setFileUrl("https://admdownload.adobe.com/bin/live/flashplayer22_ha_install.exe");*/
		init();
		
		try {
			downloadManager.stopAllDownload();
		} catch (DbException e) {
			e.printStackTrace();
		}
		AppManager.getAppManager().addActivity(this);
	}

	
	@Override
	protected void onResume() {
		super.onResume();
		//init();
	}

	
	/**
	 * 进行Activity的初始化操作，主要是各种组件的赋值以及从传过来的groupVersionVo中获取升级对象的列表
	 * */
	private void init()
	{
		lvUpdateOptions=(ListView) findViewById(R.id.lv_update_options);
		btnConfirm=(Button) findViewById(R.id.btn_download);
		btnCancel=(Button) findViewById(R.id.btn_cancel);
		btnUpdate=(Button) findViewById(R.id.btn_update);
		
		btnConfirm.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		btnUpdate.setOnClickListener(this);
		
		appInfoList=new ArrayList<SimpleAppInfo>();
		
		btnConfirm.setEnabled(true);
		btnUpdate.setEnabled(true);
		for(UpdateVo vo:groupVersionVo.getUpdateVoList())
		{
			SimpleAppInfo info=new SimpleAppInfo(vo,AppConst.DOWNLOAD_TARGET);
			appInfoList.add(info);
		}
		
		adapter=new UpdateOptionAdapter(getApplicationContext(),appInfoList);
		lvUpdateOptions.setAdapter(adapter);
		lvUpdateOptions.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int pos,
					long rawId) {
				resumeDownload(pos);
			}
		});
		btnUpdate.setEnabled(false);	
		for(SimpleAppInfo info:appInfoList)
		{
			if(info.isDownloadStarted())
			{
				btnConfirm.setEnabled(false);
				break;
			}
		}
		for(SimpleAppInfo info:appInfoList)
		{
			if(!info.isDownloadFinished())
			{
				return ;
			}
		}
		btnUpdate.setEnabled(true);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_download:
			Log.i(TAG, "resume0");
			btnConfirm.setText("下载");
			Log.i(TAG, "download");		
			if (checkNetworkStatus() == -1) {
				handleNoNetwork();
				return ;
			}
					 
			if (checkNetworkStatus() == 0) {
				handlerMobileNetwork();		
			}
			else
			{
				downloadFiles();
			}
			//下载列表中的所有项目
			break;
		case R.id.btn_cancel:
			handleExit();
			break;
		case R.id.btn_update:
			handleUpdate();
			break;
		}
		
	}
	
	/**
	 * @return 当网络为wifi时返回1，当网络为移动网络时返回0，否则返回-1
	 * */
	private int checkNetworkStatus()
	{
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if(networkInfo==null||!networkInfo.isConnected())
        {
        	return -1;
        }
        int type = networkInfo.getType();
        if(type==ConnectivityManager.TYPE_MOBILE)
        {
        	return 0;
        }
        else if(type==ConnectivityManager.TYPE_WIFI)
        {
        	return 1;
        }
        return -1;
	}
	
	/**
	 * 下载过程的监听器
	 * 主要作用就是更新任务进度以及处理下载成功和下载失败的事件，当下载失败时列表中对应项的图标应该有所改变以反映失败的状态，同时用户通过点击该项可以继续下载
	 * 不过现在还没有对应图标，所以这部分并没有得以体现 
	 * */
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
			adapter.notifyDataSetChanged();
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
				Settings.System.putString(getContentResolver(), "updatedownload", "finish");
				Settings.System.putString(getContentResolver(), "updateinstall", "begin");
				btnUpdate.setEnabled(true);	
				cleanTasks();
				handleUpdate();
			}
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
				downloadManager=DownloadService.getDownloadManager(getApplicationContext());
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
	
	private void reportSuccess(String appId)
	{
		HttpUtils http=new HttpUtils();		
		String url=InfoUtils.getUrlPart(URL_HOST, URL_REPORT_UPDATE_STATUS,appId,"DOWNLOAD_SUCCESS");
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
	
	private void handleUpdate()
	{
		Log.i(TAG,"update");
		cleanTasks();
		if(!isCarStopped())
		{
			AlertDialog.Builder builder=new AlertDialog.Builder(UpdateActivity.this);
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
			DialogManager.getInstance().showInstallDialog(getApplicationContext(), groupVersionVo);
			//AlertWindowUtils.showInstallAlertWindow(getApplicationContext(), groupVersionVo);
		}
	}
	
	/**
	 * 用来继续被中断的下载，参数为下载项在appInfoList中的对应位置
	 * */
	private void resumeDownload(final int pos)
	{
			if(checkNetworkStatus()==-1)
			{
				return ;
			}
			else if(checkNetworkStatus()==0)
			{
				AlertDialog.Builder builder=new AlertDialog.Builder(UpdateActivity.this);
				builder.setTitle("系统提示");
				builder.setMessage("当前网络为3G网络，确定要继续下载吗？");
				builder.setPositiveButton("确定",new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog,int which) 
					{	
						if (InfoUtils.querySurplusFlow() < (float) (groupVersionVo.getMaxSize())/ (1024 * 1024)) 
						{
								handleFlowInsufficient();
						}
						else
						{
							handleResumeDownload(pos);
						}
						dialog.dismiss();
					}});
				builder.setNegativeButton("取消",new DialogInterface.OnClickListener() 
					{
						@Override
						public void onClick(DialogInterface dialog,int which) 
							{
								btnConfirm.setEnabled(true);
								dialog.dismiss();
								return;
							}
					});
			}
			else if(checkNetworkStatus()==1)
			{
				handleResumeDownload(pos);
			}
	}
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_4)
		{
			handleExit();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	private void handleExit()
	{
		AlertDialog.Builder builder=new AlertDialog.Builder(UpdateActivity.this);
		builder.setTitle("系统提示");
		builder.setMessage("确认退出更新吗？您可以稍后再次选择进行更新。");
		builder.setPositiveButton("确定",new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				try {
					downloadManager.stopAllDownload();
				} catch (DbException e) {
					e.printStackTrace();
				}
				dialog.dismiss();
				finish();
			}
		});
		builder.setNegativeButton("取消",new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
			}
		});
		builder.show();
	}
	
	private boolean isCarStopped()
	{
		int speed=Settings.System.getInt(getContentResolver(), "carspeed",1);//默认值最好为1
		return speed==0;
	}
	
	/**
	 * 处理无网络连接的情况
	 * */
	private void handleNoNetwork()
	{
			AlertDialog.Builder builder=new AlertDialog.Builder(UpdateActivity.this);
			builder.setTitle("系统提示");
			builder.setMessage("您当前未连接网络，请连接网络后重试");
			builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,int which) {
					btnConfirm.setEnabled(true);
					dialog.dismiss();
					return;
				}
			});
			builder.setCancelable(false);
			builder.show();
	}
	
	/**
	 *处理网络为移动网络的情况 
	 * */
	private void handlerMobileNetwork()
	{
		AlertDialog.Builder builder=new AlertDialog.Builder(UpdateActivity.this);
		builder.setTitle("系统提示");
		builder.setMessage("当前网络为3G网络，确定要进行下载吗？");
		builder.setPositiveButton("确定",new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog,int which) 
			{	
				if (InfoUtils.querySurplusFlow() < (float) (groupVersionVo.getMaxSize())/ (1024 * 1024)) 
				{
						handleFlowInsufficient();
				}
				else
				{
					downloadFiles();
				}
				dialog.dismiss();
			}});
		builder.setNegativeButton("取消",new DialogInterface.OnClickListener() 
			{
				@Override
				public void onClick(DialogInterface dialog,int which) 
					{
						btnConfirm.setEnabled(true);
						dialog.dismiss();
						return;
					}
			});
		builder.setCancelable(false);
		builder.show();
	}
	
	/**
	 * 处理用户流量不足的情况
	 * */
	private void handleFlowInsufficient()
	{
		AlertDialog.Builder builder=new AlertDialog.Builder(UpdateActivity.this);
		builder.setTitle("系统提示");
		builder.setMessage("您当月流量不足以完成更新包下载，请购买更多流量后进行操作。");
		builder.setPositiveButton("确定",new DialogInterface.OnClickListener() 
			{
				@Override
				public void onClick(DialogInterface dialog,int which) 
				{
					btnConfirm.setEnabled(true);
					dialog.dismiss();
				}
			}).setCancelable(false).show();
						return;
	}
	
	//文件下载函数
	private void downloadFiles()
	{
		for (int i = 0; i < appInfoList.size(); i++) {
			SimpleAppInfo info = appInfoList.get(i);
			try {
				if(downloadManager.containsDownload(info.getUrl()))
				{
					downloadManager.resumeDownload(info.getUrl(),info.getFilePath(), new DownloadCallBack(i));
				}
				else
				{
					downloadManager.addNewDownload(info.getUrl(),info.getFilePath(),info.getFilePath(),true, false, new DownloadCallBack(i));
					Log.i(TAG,"Target: "+info.getFilePath());
				}
			} catch (DbException e) {
				e.printStackTrace();
			}
		}
		btnConfirm.setEnabled(false);
	}
	private void handleResumeDownload(int pos)
	{
		SimpleAppInfo info=appInfoList.get(pos);
		if(!info.isDownloadStarted()||info.isDownloadFinished())
		{
			return ;
		}
		downloadManager.stopDownload(info.getUrl());
		boolean resume;
		try {
			resume = downloadManager.resumeDownload(info.getUrl(),info.getFilePath(),new DownloadCallBack(pos));
			if(!resume)
			{
				downloadManager.addNewDownload(info.getUrl(), info.getFilePath(),info.getFilePath(), false, false, new DownloadCallBack(pos));
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
	}
}
