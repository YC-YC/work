package com.zhonghong.chelianupdate.receiver;

import com.zhonghong.chelianupdate.base.AppConst;
import com.zhonghong.chelianupdate.utils.Saver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class UpdateReceiver extends BroadcastReceiver{
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("Update","receive");
		if(intent.getAction().equals("com.remoteupdate.tima2semisky_uporder"))
		{
			Log.i("Update","receive");
			/*//发送广播，表示已接受到发出的广播，通知发送方不需要再次进行发送
			Intent broadCastIntent=new Intent();
			broadCastIntent.setAction("com.remoteupdate.semisky2tima_reply");
			context.sendBroadcast(broadCastIntent);*/
			
			//启动升级服务
			Intent serviceIntent=new Intent();
			serviceIntent.setAction(AppConst.ACTION_CHELIAN_UPDATE);
			context.startService(serviceIntent);
			Saver.setDownloadState(Saver.DOWNLOAD_STATE_DEFAULT);
			Saver.setInstallState(Saver.INSTALL_STATE_DEFAULT);
		}
	}
}
