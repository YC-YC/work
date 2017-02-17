package com.zhonghong.chelianupdate.utils;

import java.util.List;

import com.zhonghong.chelianupdate.bean.GroupVersionVo;
import com.zhonghong.chelianupdate.bean.UpdateVo;
import com.zhonghong.chelianupdate.dialog.DownloadDialog;
import com.zhonghong.chelianupdate.dialog.InstallDialog;
import com.zhonghong.chelianupdate.dialog.NewUpdateDialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

@SuppressLint("InlinedApi") 
public class DialogManager {

	private static DialogManager instance;

	private NewUpdateDialog mNewUpdateDialog;
	private DownloadDialog mDownloadDialog;
	private InstallDialog mInstallDialog;
	
	
	public static DialogManager getInstance() {
		if (instance == null) {
			synchronized (DialogManager.class) {
				if (instance == null) {
					instance = new DialogManager();
				}
			}
		}
		return instance;
	}

	/**
	 * 显示下载信息对话框，用于这次的升级信息第一次出现时(即接收到有新升级广播时用于提示，如果不是第一次进行提示则用showDownloadDialog()进行提示)，
	 * 主要是因为第一次进行提示时用户如果选择了取消升级，则需要向服务器推送取消信息，如果没有这一需求，可以直接两个对话框都用这种方式或者都用showDownloadDialog
	 * */
	public void showNewUpdateDialog(final Context context,GroupVersionVo groupVersionVo)
	{
		if(mNewUpdateDialog==null)
		{
			mNewUpdateDialog=new NewUpdateDialog(context,groupVersionVo);
			Window window = mNewUpdateDialog.getWindow();
			LayoutParams params = window.getAttributes();
			params.dimAmount = 0.8f;
			params.type = LayoutParams.TYPE_SYSTEM_ALERT;
			params.flags = LayoutParams.FLAG_ALT_FOCUSABLE_IM;
			params.gravity = Gravity.CENTER;
			params.width=720;
			params.height=540;
			int flags=LayoutParams.FLAG_NOT_FOCUSABLE
					| LayoutParams.FLAG_NOT_TOUCH_MODAL
					| LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
			if(Build.VERSION.SDK_INT>=19)
			{
				flags |= LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
			}
			window.addFlags(flags);
			window.setAttributes(params);
			mNewUpdateDialog.setCanceledOnTouchOutside(true);	
		}
		if(!mNewUpdateDialog.isShowing())
		{
			mNewUpdateDialog.show();
		}
	}
	
	/**
	 * 显示下载信息对话框，用于这次的升级信息再次出现时(开机提醒等)
	 * */
	public void showDownloadDialog(final Context context,GroupVersionVo groupVersionVo,boolean isFirstTime) {
		if (mDownloadDialog == null) {
			mDownloadDialog = new DownloadDialog(context.getApplicationContext(),groupVersionVo,isFirstTime);
			Window window = mDownloadDialog.getWindow();
			
			LayoutParams params = window.getAttributes();
			params.dimAmount = 0.8f;
			params.type = LayoutParams.TYPE_SYSTEM_ALERT;
			params.flags = LayoutParams.FLAG_ALT_FOCUSABLE_IM;
			params.gravity = Gravity.CENTER;
			params.width=720;
			params.height=540;
			int flags=LayoutParams.FLAG_NOT_FOCUSABLE
					| LayoutParams.FLAG_NOT_TOUCH_MODAL
					| LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
			if(Build.VERSION.SDK_INT>=19)
			{
				flags |= LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
			}
			window.addFlags(flags);
			window.setAttributes(params);
			mDownloadDialog.setCanceledOnTouchOutside(true);	
		}
		if (!mDownloadDialog.isShowing()) {
			mDownloadDialog.show();
		}
	}
	
	/**
	 * 显示下载完成可以安装升级的对话框
	 * */
	public void showInstallDialog(final Context context,GroupVersionVo groupVersionVo) {	
		if (mInstallDialog == null) {
			mInstallDialog = new InstallDialog(context.getApplicationContext(),groupVersionVo);
			Window window = mInstallDialog.getWindow();
			
			LayoutParams params = window.getAttributes();
			params.dimAmount = 0.8f;
			params.type = LayoutParams.TYPE_SYSTEM_ALERT;
			params.flags = LayoutParams.FLAG_ALT_FOCUSABLE_IM;
			params.gravity = Gravity.CENTER;
			params.width=720;
			params.height=540;
			int flags=LayoutParams.FLAG_NOT_FOCUSABLE
					| LayoutParams.FLAG_NOT_TOUCH_MODAL
					| LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
			if(Build.VERSION.SDK_INT>=19)
			{
				flags |= LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
			}
			window.addFlags(flags);
			window.setAttributes(params);
			mInstallDialog.setCanceledOnTouchOutside(true);	
		}
		if (!mInstallDialog.isShowing()) {
			mInstallDialog.show();
		}
	}
	
	public boolean isNewUpdateDialogShowing()
	{
		return mNewUpdateDialog!=null&&mNewUpdateDialog.isShowing();
	}
	
	public boolean isDownloadDialogShowing()
	{
		return mInstallDialog!=null&&mDownloadDialog.isShowing();
	}
	
	public boolean isInstallDialogShowing()
	{
		return mInstallDialog!=null&&mInstallDialog.isShowing();
	}
	
	public void hideNewUpdateDialog()
	{
		if(mNewUpdateDialog!=null&&mNewUpdateDialog.isShowing())
		{
			mNewUpdateDialog.dismiss();
		}
		mNewUpdateDialog = null;
	}
	
	public void hideDownloadDialog()
	{
		if(mDownloadDialog!=null&&mDownloadDialog.isShowing())
		{
			mDownloadDialog.dismiss();
		}
		mDownloadDialog = null;
	}
	
	public void hideInstallDialog()
	{
		if(mInstallDialog!=null&&mInstallDialog.isShowing())
		{
			mInstallDialog.dismiss();
		}
		mInstallDialog = null;
	}
}
