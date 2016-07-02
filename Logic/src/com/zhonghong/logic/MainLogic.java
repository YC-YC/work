/**
 * 
 */
package com.zhonghong.logic;

import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.zhonghong.mcuservice.IDataChangedListener;
import android.zhonghong.mcuservice.McuHardKeyInfo;
import android.zhonghong.mcuservice.McuHardKeyProxy;
import android.zhonghong.mcuservice.McuManagerService;
import android.zhonghong.mcuservice.RegistManager.IMcuHardKeyChangedListener;
import android.zhonghong.mcuservice.SettingsProxy;

import com.zhonghong.logic.data.GlobalData;
import com.zhonghong.recordinfo.RecordAppManager;
import com.zhonghong.recordinfo.RecordInfoBean;
import com.zhonghong.utils.AppUtils;
import com.zhonghong.utils.L;
import com.zhonghong.utils.SettingsUtils;

/**
 * @author YC
 * @time 2016-7-2 上午10:19:54
 * TODO:
 */
public class MainLogic implements IMain{

	private static final String TAG = "MainLogic";
	
	private Context mContext;
	private Handler mHandler = new Handler();
	
	private SettingsProxy mSettingsProxy;
	private McuHardKeyProxy mMcuHardKeyProxy;
	
	private VolumeReceiver mVolumeReceiver;
	
	private McuHardKeyChangedListener mHardKeyChangedListener;
	

	public MainLogic(Context context) {
		super();
		this.mContext = context;
		L.startTime("new MainLogic 花费时间");
		
		L.endUseTime("new MainLogic 花费时间");
	}
	
	@Override
	public void onCreate() {
		L.startTime("MainLogic onCreate 花费时间");
		init();
		L.endUseTime("MainLogic onCreate 花费时间");
		sendInitOkBroadcast();
	}

	@Override
	public void onDestroy() {
		if (mVolumeReceiver != null)
			mContext.unregisterReceiver(mVolumeReceiver);
		if (mMcuHardKeyProxy != null && mHardKeyChangedListener != null)
			mMcuHardKeyProxy.unregistKeyInfoChangedListener(mHardKeyChangedListener);
	}
	
	/**Logic初始化完成*/
	private void sendInitOkBroadcast() {
		Intent it = new Intent();
		it.setAction(GlobalData.Logic.INIT_OK);
		mContext.sendBroadcast(it);
	}
	
	private void init(){
		initRecordApp();
		initMCUService();
		registerVolChangeReceiver();
	}
	
	
	/**MCU按键*/
	private void initMCUService() {
		mSettingsProxy = new SettingsProxy();
		mMcuHardKeyProxy = new McuHardKeyProxy();
		mHardKeyChangedListener = new McuHardKeyChangedListener();
		mMcuHardKeyProxy.registKeyInfoChangedListener(mHardKeyChangedListener);
	}

	/**系统声音变化*/
	private void registerVolChangeReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.media.VOLUME_CHANGED_ACTION");
		mVolumeReceiver = new VolumeReceiver();
		mContext.registerReceiver(mVolumeReceiver, filter);
	}


	/**初始化记忆*/
	private void initRecordApp(){
		RecordInfoBean appCellInfo = RecordAppManager.getInstaces(mContext).getRecordInfo();
		RunningTaskInfo topAppInfo = AppUtils.getTopAppInfo(mContext);
		if (topAppInfo.topActivity.getPackageName() != appCellInfo.getLastPkgName() ||
				topAppInfo.topActivity.getClassName() != appCellInfo.getLastClassName())
		AppUtils.startOtherActivity(mContext, appCellInfo.getLastPkgName(), appCellInfo.getLastClassName());
	}
	
	/**声音变化广播*/
	private class VolumeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction()
					.equals("android.media.VOLUME_CHANGED_ACTION")) {
				AudioManager audioManager = (AudioManager) mContext
						.getSystemService(Context.AUDIO_SERVICE);
				int currVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);// 当前的媒体音量
				if (mSettingsProxy != null)
					mSettingsProxy.setVol(currVolume);
			}
		}

	}
	
	/**MCU按键监听*/
	private class McuHardKeyChangedListener implements IMcuHardKeyChangedListener{

		@Override
		public void notify(int[] changeCMDs, final McuHardKeyInfo hardkey) {
			mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					int keycode = hardkey.getKeyCode();
					Log.i(TAG, "mcu info key = " + keycode);
					switch (keycode) {
					case McuHardKeyInfo.KEYCODE_BACK:
						SettingsUtils.sendSyskey(KeyEvent.KEYCODE_BACK);
						break;
					case McuHardKeyInfo.KEYCODE_HOME:
						SettingsUtils.sendSyskey(KeyEvent.KEYCODE_HOME);
						break;
					case McuHardKeyInfo.KEYCODE_MODE:
						break;
					case McuHardKeyInfo.KEYCODE_NAVI:
						break;
					case McuHardKeyInfo.KEYCODE_NEXT:
						SettingsUtils.sendSyskey(KeyEvent.KEYCODE_MEDIA_NEXT);
						break;
					case McuHardKeyInfo.KEYCODE_PREV:
						SettingsUtils.sendSyskey(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
						break;
					case McuHardKeyInfo.KEYCODE_PLAY:
						SettingsUtils.sendSyskey(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
						break;
					case McuHardKeyInfo.KEYCODE_POWER:
						SettingsUtils.sendSyskey(KeyEvent.KEYCODE_POWER);
						break;
					case McuHardKeyInfo.KEYCODE_TFTOFF:
						break;
					case McuHardKeyInfo.KEYCODE_VOL_INC:
					/*{
						int vol = mSettingsProxy.getSettingsInfo().getVol();
						if (vol > 0)
							mSettingsProxy.setVol(vol-1);
					}*/
						SettingsUtils.sendSyskey(KeyEvent.KEYCODE_VOLUME_UP);
						break;
					case McuHardKeyInfo.KEYCODE_VOL_DEC:
					/*{
						int vol = mSettingsProxy.getSettingsInfo().getVol();
						if (vol < GlobalData.SettingsConfig.MAX_VOL)
							mSettingsProxy.setVol(vol+1);
					}*/
						SettingsUtils.sendSyskey(KeyEvent.KEYCODE_VOLUME_DOWN);
						break;
					default:
						break;
					}
				}
			});
		}
		
	}

}
