package com.zhonghong.logic;

import com.zhonghong.logic.data.GlobalData;
import com.zhonghong.logic.record.RecordAppManager;
import com.zhonghong.logic.record.RecordInfoBean;
import com.zhonghong.logic.utils.AppUtils;

import android.app.Activity;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	public void doTest(View view){
		switch (view.getId()) {
		case R.id.button1:
			if (!AppUtils.isServiceRunning(this, GlobalData.LOGIC_SERVICE_CLASS)){
				startService(new Intent(GlobalData.LOGIC_SERVICE_CLASS));
				Log.i(TAG , "check open Logicservice");
			}
			break;
		case R.id.button2:
			RunningTaskInfo topAppInfo = AppUtils.getTopAppInfo(this);
			RecordAppManager.getInstaces(this).setRecordInfo(
					new RecordInfoBean(topAppInfo.topActivity.getPackageName(), 
							topAppInfo.topActivity.getClassName()));
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
