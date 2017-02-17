package com.zhonghong.updatecan.view;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.zhonghong.updatecan.R;
import com.zhonghong.updatecan.UpdateCanStatus;
import com.zhonghong.updatecan.UpdateCanC40;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public void doClick(View view){
		switch (view.getId()) {
		case R.id.update:
			UpdateCanC40.getInstances().startUpdate("/mnt/USB/updCanMcu.bin", mUpdateStatus);
			break;

		default:
			break;
		}
	}
	
	private UpdateCanStatus mUpdateStatus = new UpdateCanStatus() {
		
		@Override
		public void onStatus(int cmd, int value) {
			Message msg = Message.obtain();
			msg.what = HANDLE_CMD_UPDATE_CALLBACK;
			msg.arg1 = cmd;
			msg.arg2 = value;
			mHandle.sendMessage(msg);
		}
	};
	
	private static final int HANDLE_CMD_UPDATE_CALLBACK = 0x100;
	private static final int HANDLE_CMD_SEARCH_FILE_FINISH = 0x101;
	private static final int HANDLE_CMD_REBOOT103 = 0x102;

	protected static final String TAG = "TestCanUpdate";
	private Handler mHandle = new Handler() {
		
		private int totalFileSize;
		
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLE_CMD_UPDATE_CALLBACK: {
				int cmd = msg.arg1;
				int value = msg.arg2;
				switch (cmd) {
				case UpdateCanStatus.STATUS_ERROR:
					Log.i(TAG, "升级出错...code：" + value);
					break;
				case UpdateCanStatus.STATUS_START:
					Log.i(TAG, "开始升级...包数为：" + value);
					break;
				case UpdateCanStatus.STATUS_UPDATEING:
					Log.i(TAG, "升级中...process：" + value);
					break;
				case UpdateCanStatus.STATUS_FINISH:
					Log.i(TAG, "升级完成...");
					break;
				default:
					break;
				}
			}break;

			default:
				break;
			}
		};
	};
	
}
