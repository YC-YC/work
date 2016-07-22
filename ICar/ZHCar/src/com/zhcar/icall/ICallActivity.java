/**
 * 
 */
package com.zhcar.icall;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.zhcar.R;
import com.zhcar.base.UpdateUiBaseActivity;
import com.zhcar.data.GlobalData;
import com.zhcar.provider.CarProviderData;
import com.zhcar.utils.BtUtils;
import com.zhcar.utils.UpdateUiManager;
import com.zhcar.utils.Utils;
import com.zhcar.utils.UpdateUiManager.UpdateViewCallback;

/**
 * @author YC
 * @time 2016-7-12 上午11:58:50
 * TODO: 呼叫凯翼
 */
public class ICallActivity extends UpdateUiBaseActivity implements OnClickListener{

	private static final String TAG = "ICall";
	private Button mCallservice;
	
	
	private ContentResolver resolver;
	private final Uri uri = Uri.parse("content://cn.com.semisky.carProvider/phonenum");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_call);
		resolver = getContentResolver();
		initViews();
	}

	private void initViews() {
		mCallservice = (Button) findViewById(R.id.callservice);
		mCallservice.setOnClickListener(this);
	}


	@Override
	protected void onResume() {
		super.onResume();
		refreshDialKey();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
	}
	

	
	private UpdateViewCallback mUpdateViewCallback = new UpdateViewCallback() {
		
		@Override
		public void onUpdate(int cmd) {
			switch (cmd) {
			case UpdateUiManager.CMD_UPDATE_BTSTATE:
				refreshDialKey();
				break;
			}
		}
	};
	
	/**刷新拨号按键*/
	private void refreshDialKey() {
		if (mCallservice == null)
			return;
		if (!BtUtils.isBtConnected()/* || BtUtils.isBtCalling()*/){
			mCallservice.setEnabled(false);
		}
		else{
			mCallservice.setEnabled(true);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.callservice:
			doCallService();
			break;
		}
	}

	private void doCallService() {
		if (BtUtils.isBtConnected() && !BtUtils.isBtCalling()){
			HashMap<String, String> extras = new HashMap<String, String>();
			extras.clear();
			extras.put(BtUtils.BT_CALL_PHONE_KEY, getKaiYiNum());
			Log.i(TAG, "拨打客服电话");
			Utils.sendBroadcast(this, BtUtils.BT_CONTROL_ACTION, extras);
		}
	}
	
	private String getKaiYiNum() {
		Cursor cursor = resolver.query(uri, null, null, null, null);
		if(cursor != null && cursor.moveToNext()){
			String kaiyiNum = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_PHONENUM_KAIYI_NUM));
			Log.i(TAG, "查询结果为：service_num = " + kaiyiNum);
			return kaiyiNum;
			}
		else{
			Log.i(TAG, "无查询结果");
			return null;
		}
	}

	@Override
	protected UpdateViewCallback getUpdateViewCallback() {
		return mUpdateViewCallback;
	}

	
	
	
}
