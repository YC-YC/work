package com.zhonghong.mediascanner;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.zhonghong.mediascanerlib.ScannerConst;
import com.zhonghong.mediascanner.utils.FileUtil;

public class MainActivity extends Activity {

	private static final String TAG = "ScanManager";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	public void doClick(View view){
		switch (view.getId()) {
		case R.id.get_device_info:
//			testGetDeviceInfo();
			sendMsgToScannerService(this, "/mnt/USB", true);
			break;
		case R.id.test_db:
//			testDB();
			test();
			break;
		default:
			break;
		}
	}

	
	private void test(){
		File file = new File("/mnt/sdcard/zhmedia/123");
//		Log.i(TAG, "getTotalSpace = " + file.get);
	}
	
	private void sendMsgToScannerService(final Context context,
			final String device, boolean bMount) {
		if (!TextUtils.isEmpty(device)) {
			Intent it = new Intent(ScannerConst.SService_ACTION);
			if (bMount) {
				it.putExtra(ScannerConst.SService_KEY_CMD,
						ScannerConst.SService_CMD_MOUNT);
			} else {
				it.putExtra(ScannerConst.SService_KEY_CMD,
						ScannerConst.SService_CMD_UNMOUNT);
			}
			it.putExtra(ScannerConst.SService_KEY_PATH, device);
			context.startService(it);
		}
	}
	
	private void testDB(){
		/*SaverManager saverManager = new SaverManager();
		saverManager.updateDevicesInfo("uuid" + new Random().nextInt(10), SystemClock.elapsedRealtime());
		*/
		FileUtil.deleteDir("/mnt/sdcard/zhmedia/123", true);
	}
	
	private void testGetDeviceInfo(){
		StatFs statFs = new StatFs("/mnt/USB1/");
		statFs.getAvailableBlocksLong();
		
		StorageManager storageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
		StorageVolume[] storageVolumes = storageManager.getVolumeList();
		Log.i(TAG, "storageVolumes length = " + storageVolumes.length);
		for (int i = 0; i < storageVolumes.length; i++){
			StorageVolume storageVolume = storageVolumes[i];
			Log.i(TAG, "storageVolume info = " + storageVolume.toString());
			if (storageVolume.getUuid() != null && storageVolume.getState().equals(Environment.MEDIA_MOUNTED)){
				Log.i(TAG, "mounted device = " + storageVolume.getPath());
				Log.i(TAG, "storageVolumes uuid = " + storageVolumes[i].getUuid());
				File file = new File(storageVolume.getPath());
				Log.i(TAG, "lastModified = " + file.lastModified()
						+ "TotalSpace = " + file.getTotalSpace()
						+ "UsableSpace = " + file.getUsableSpace());
			}
		}
		
		Log.i(TAG, "get device uuid = " + FileUtil.getDeviceUuid(this, "/mnt/USB1"));
		Log.i(TAG, "get device device = " + FileUtil.getUUIDDevice(this, "3E37-BBC8"));
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

}
