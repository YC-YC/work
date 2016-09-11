package com.zhonghong.chelianupdate.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.IBinder;

import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.util.LogUtils;
import com.zhonghong.chelianupdate.base.AppConst;
import com.zhonghong.chelianupdate.bean.DownloadInfo;
import com.zhonghong.chelianupdate.utils.DownloadManager;
import com.zhonghong.sdk.android.utils.NetworkUtils;

import java.util.List;

/**
 * Author: wyouflf
 * Date: 13-11-10
 */
public class DownloadService extends Service {

    private static DownloadManager DOWNLOAD_MANAGER;
    private static NetworkChangedReceiver receiver;
    
    public static DownloadManager getDownloadManager(Context appContext) {
        if (!DownloadService.isServiceRunning(appContext)) {
            Intent downloadSvr = new Intent(AppConst.ACTION_CHELIAN_DOWNLOAD);
            downloadSvr.setPackage(appContext.getPackageName());
            appContext.startService(downloadSvr);
        	receiver=new NetworkChangedReceiver();
        	IntentFilter filter=new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            appContext.registerReceiver(receiver,filter);
        }
        if (DownloadService.DOWNLOAD_MANAGER == null) {
            DownloadService.DOWNLOAD_MANAGER = new DownloadManager(appContext);
            try {
				DOWNLOAD_MANAGER.stopAllDownload();
			} catch (DbException e) {
				e.printStackTrace();
			}
        }
        return DOWNLOAD_MANAGER;
    }

    public DownloadService() {
        super();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	/*@Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }*/

    @Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	@Override
    public void onDestroy() {
        if (DOWNLOAD_MANAGER != null) {
            try {
                DOWNLOAD_MANAGER.stopAllDownload();
                DOWNLOAD_MANAGER.backupDownloadInfoList();
                if(receiver!=null)
                {
                	unregisterReceiver(receiver);
                	receiver=null;
                }
            } catch (DbException e) {
                LogUtils.e(e.getMessage(), e);
            }
        }
        super.onDestroy();
    }
    
    @Override
	public void onTaskRemoved(Intent rootIntent) {
    	try {
    		DOWNLOAD_MANAGER.stopAllDownload();
			DOWNLOAD_MANAGER.backupDownloadInfoList();
		} catch (DbException e) {
			e.printStackTrace();
		}
		super.onTaskRemoved(rootIntent);
	}

	@Override
	public void onTrimMemory(int level) {
		if(level!=ComponentCallbacks2.TRIM_MEMORY_COMPLETE)
			return ;
    	try {
    		DOWNLOAD_MANAGER.stopAllDownload();
			DOWNLOAD_MANAGER.backupDownloadInfoList();
		} catch (DbException e) {
			e.printStackTrace();
		}
		super.onTrimMemory(level);
	}

	public static boolean isServiceRunning(Context context) {
        boolean isRunning = false;

        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList
                = activityManager.getRunningServices(Integer.MAX_VALUE);

        if (serviceList == null || serviceList.size() == 0) {
            return false;
        }

        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(DownloadService.class.getName())) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }
    
	private static class NetworkChangedReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {	
			String action = intent.getAction();
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				if (NetworkUtils.isNetworkAvailable()) {
					for (int i = 0; i < DOWNLOAD_MANAGER
							.getDownloadInfoListCount(); i++) {
						try {
							DownloadInfo info = DOWNLOAD_MANAGER
									.getDownloadInfo(i);
							if (info.getState().equals(
									HttpHandler.State.FAILURE)) {
								DOWNLOAD_MANAGER.resumeDownload(
										DOWNLOAD_MANAGER.getDownloadInfo(i),
										null);
							}
						} catch (DbException e) {
							e.printStackTrace();
						}
					}
					/*Activity activity = AppManager.getAppManager()
							.currentActivity();*/
					
				}

			}
		}

	}
}
