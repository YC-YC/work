/**
 * 
 */
package com.zhonghong.mediascanner.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.zhonghong.mediascanerlib.ScannerConst;
import com.zhonghong.mediascanner.utils.FileUtil;
import com.zhonghong.mediascanner.utils.L;

/**
 * @author YC
 * @time 2016-12-7 下午2:49:11
 * TODO:
 */
public class ScannerReceiver extends BroadcastReceiver {


	private static final String TAG = "ScannerReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		L.i(TAG, "action = " + intent.getAction());
		if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
			if (FileUtil.isDeviceMounted("/mnt/USB")){
				sendMsgToScannerService(context, "/mnt/USB", true);
			}
			if (FileUtil.isDeviceMounted("/mnt/USB1")){
				sendMsgToScannerService(context, "/mnt/USB1", true);
			}
			if (FileUtil.isDeviceMounted("/mnt/USB2")){
				sendMsgToScannerService(context, "/mnt/USB2", true);
			}
		}
		else{
			if (Intent.ACTION_MEDIA_MOUNTED.equals(intent.getAction())){
				sendMsgToScannerService(context, getPath(intent), true);
			}
			else if (Intent.ACTION_MEDIA_UNMOUNTED.equals(intent.getAction())){
				sendMsgToScannerService(context, getPath(intent), false);
			}
		}
	}
	
	private String getPath(Intent intent){
		String path = null;
		final Uri uri = intent.getData();
		if (uri != null){
			path = uri.getPath();
		}
		return path;
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

}
