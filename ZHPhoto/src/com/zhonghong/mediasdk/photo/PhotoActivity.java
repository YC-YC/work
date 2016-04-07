package com.zhonghong.mediasdk.photo;

import com.zhonghong.mediasdk.Constants;
import com.zhonghong.mediasdk.photo.PhotoServiceHelper;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;


public class PhotoActivity extends Activity {
	private static final String TAG = "PhotoActivity";
	protected void onCreate(Bundle savedInstanceState,Handler handler) {
		super.onCreate(savedInstanceState);
		PhotoServiceHelper.HelperCreate(this, handler);
	}

	@Override
	protected void onPause(){	
		super.onPause();
	}
	
	@Override
	protected void onStop(){
		PhotoServiceHelper.HelperStop();
		super.onStop();
	}
	
	@Override
	protected void onResume(){
		PhotoServiceHelper.HelperResume();
		super.onResume();
	}
	
	@Override
	protected void onDestroy(){
		PhotoServiceHelper.HelperDestory();
		super.onDestroy();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
	
		return false;
	}
}
