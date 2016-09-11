package com.zhonghong.chelianupdate.service;

import java.io.File;
import java.util.List;

import com.zhonghong.chelianupdate.base.AppConst;
import com.zhonghong.chelianupdate.base.MyApp;
import com.zhonghong.chelianupdate.bean.UpdateVo;
import com.zhonghong.sdk.android.utils.ZToast;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

public class InstallUpdateService extends Service{

	private final String TAG="Update";
	
	private List<UpdateVo> voList;
	private final String APP_NAME_AIR="air";
	private final String APP_NAME_CAN="can";
	private final String APP_NAME_8836="8836";
	private final String APP_NAME_MCU="mcu";
	
	private final String SO_CANUPGRADE = "libCanUpgrade.so";
	private final String DATA_CANUPGRADE = "ZH_PUBLIC_APPCANUPGRADE_10";
	
	private final String SO_AIRUPGRADE = "libAirUpgrade.so";
	private final String DATA_AIRUPGRADE = "ZH_PUBLIC_APPAIRUPGRADE_10";
	
	private final String SO_8836UPGRADE = "libAppUpdate8836.so";
	private final String DATA_8836UPGRADE = "ZH_PUBLIC_APPCANUPGRADE_10";
	
	private final String SO_MCUUPGRADE = "libAppUpdateMcu.so";
	private final String DATA_MCUUPGRADE = "ZH_PUBLIC_APPUPDATEMCU_10";
	private UpdateInfoReceiver receiver;
	
	@SuppressWarnings("unchecked")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Bundle bundle=intent.getExtras();
		voList=(List<UpdateVo>) bundle.getSerializable("vo_list");
//		ZToast.showLong(getApplicationContext(),""+voList.size());
		receiver=new UpdateInfoReceiver();
		IntentFilter filter=new IntentFilter();
		filter.addAction("com.zhonghong.zuiserver.BROADCAST");
		registerReceiver(receiver, filter);
		Log.i(TAG,""+voList.size());
		for(UpdateVo vo:voList)
		{
			Log.i(TAG,vo.getFileName());
		}
		if(voList!=null&&voList.size()>0)
		{
			update(voList.get(0).getAppId(),AppConst.DOWNLOAD_TARGET+voList.get(0).getFileName());
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		if(receiver!=null)
		{
			unregisterReceiver(receiver);
		}
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	/**
	 * 发送升级命令
	 * @param appId 需要升级的App的Id，如"air"、"mcu"
	 * @param filePath 需要升级的App的升级文件的完整文件路径
	 * */
	private void update(String appId,String filePath)
	{
		if(filePath==null)
			return;
		File file=new File(filePath);
		Log.i(TAG,"Updateaaaqaaa"+"wwww"+filePath);
		if(file.exists())
		{
			Log.i(TAG,"Updatebbbbbb"+"wwww"+appId);
			if(appId.equals("air"))
			{
				if(findNext(appId)!=-1)
				{
					openZuiApp(this, SO_AIRUPGRADE, DATA_AIRUPGRADE, "A:"+filePath);
				}
				else
				{
					openZuiApp(this, SO_AIRUPGRADE, DATA_AIRUPGRADE, filePath);
				}
					
			}
			else if(appId.equals("can"))
			{
				if(findNext(appId)!=-1)
				{
					openZuiApp(this, SO_CANUPGRADE, DATA_CANUPGRADE, "A:"+filePath);
				}
				else
				{
					openZuiApp(this, SO_CANUPGRADE, DATA_CANUPGRADE, filePath);
				}	
			}
			else if(appId.equals("8836"))
			{
				if(findNext(appId)!=-1)
				{
					openZuiApp(this, SO_8836UPGRADE, DATA_8836UPGRADE, "A:"+filePath);
				}
				else
				{
					openZuiApp(this, SO_8836UPGRADE, DATA_8836UPGRADE, filePath);
				}
			}
			else if(appId.equals("mcu"))
			{
				openZuiApp(this, SO_MCUUPGRADE, DATA_MCUUPGRADE,filePath);
			}
		}
		else
		{
			handleFail(appId);
		}
	}
	
	private void handleFail(String appId)
	{
		
	}
	public class UpdateInfoReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context cxt, Intent intent) {
			
			
			String updateAir=intent.getStringExtra("upgrade_air");
			String updateCan=intent.getStringExtra("upgrade_can");
			String update8836=intent.getStringExtra("upgrade_8836");
			String updateMcu=intent.getStringExtra("upgrade_mcu");
			if(intent.getStringExtra("app_id")!=null)
			{
				return ;
			}
			
			
			if(updateAir!=null)
			{
				if(updateAir.equals("failed"))
				{
					handleFail(APP_NAME_AIR);
				}
				else
				{
					int next=findNext(APP_NAME_AIR);
					if(next==-1)
						finishInstall(APP_NAME_AIR);
					else
						update(voList.get(next).getAppId(),AppConst.DOWNLOAD_TARGET+voList.get(next).getFileName());
					return;
				}
			}
			if(updateCan!=null)
			{
				Log.i(TAG,"can");
				if(updateCan.equals("failed"))
				{
					handleFail(APP_NAME_CAN);
				}
				else
				{
					int next=findNext(APP_NAME_CAN);
					if(next==-1)
						finishInstall(APP_NAME_CAN);
					else
					{
						update(voList.get(next).getAppId(),AppConst.DOWNLOAD_TARGET+voList.get(next).getFileName());
					}
					return;
				}
			}
			if(update8836!=null)
			{
				Log.i(TAG,"8836");
				if(update8836.equals("failed"))
				{
					handleFail(APP_NAME_8836);
				}
				else
				{
					int next=findNext(APP_NAME_8836);
					if(next==-1)
						finishInstall(APP_NAME_8836);
					else
						update(voList.get(next).getAppId(),AppConst.DOWNLOAD_TARGET+voList.get(next).getFileName());
					return;
				}
			}
			if(updateMcu!=null)
			{
				Log.i(TAG,"mcu");
				if(updateMcu.equals("failed"))
				{
					Log.i(TAG,"mcu fail");
					handleFail(APP_NAME_MCU);
				}
			}
			finishInstall(APP_NAME_MCU);
		}	
	}

	/**
	 * 获取升级列表中下一个对象在列表中的位置
	 * @param appId 当前对象的appId
	 * @return 当存在下一个对象时返回下一个对象在列表中的位置(可通过list.get()获得对象)，当下一个对象不存在时返回-1
	 * */
	private int findNext(String appId)
	{
		for(int i=0;i<voList.size();i++)
		{
			if(voList.get(i).getAppId().equals(appId))
			{
				if(i!=voList.size()-1)
				{
					return i+1;
				}
				break;
			}
		}
		return -1;
	}
	
	private void finishInstall(String step)
	{
		if(step.equals(APP_NAME_AIR)||step.equals(APP_NAME_CAN))
		{
			//resetSystem();
		}
	}
	
	private void resetSystem()
	{
		PowerManager powerManager=(PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
		powerManager.reboot("c ctr javr reset system");
	}
	
	private void openZuiApp(Context context, String soName, String data,String cmd) {
		Log.i(TAG,soName+" "+data+" "+cmd);
		Intent intent = new Intent("widget_entrance");
		intent.setComponent(new ComponentName("com.example.zuiserver", "com.example.zuiserver.StartActivity"));
		intent.putExtra("et1", data);
		intent.putExtra("et2", soName);
		intent.putExtra("et3", cmd);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		context.startActivity(intent);
	}

}
