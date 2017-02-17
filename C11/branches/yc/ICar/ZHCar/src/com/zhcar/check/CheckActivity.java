/**
 * 
 */
package com.zhcar.check;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhcar.R;
import com.zhcar.base.UpdateUiBaseActivity;
import com.zhcar.utils.UpdateUiManager;
import com.zhcar.utils.UpdateUiManager.UpdateViewCallback;
import com.zhcar.widget.HealthProgressBar;

/**
 * @author YC
 * @time 2016-7-14 下午12:23:06
 * TODO:自动检测
 */
public class CheckActivity extends UpdateUiBaseActivity {

	private static final String TAG = "CheckActivity";
	private LinearLayout mLayoutChecking, mLayoutChecked, mLayoutWhole;
	private ImageView mCarType;
	private HealthProgressBar mHealthProgressBar;
	private TextView mCheckedTips;
	private String mCarTypeName;
	private int mScore = -1;
	private Handler mHandler;
	
	private static final String CARTYPE_MC22 = "mc22";
	private static final String CARTYPE_XC51 = "xc51";
	
	
	private static final Map<String, Integer> carTypeMaps = new HashMap<String, Integer>(){
		{
			put(CARTYPE_MC22, R.drawable.cartype_mc22);
			put(CARTYPE_XC51, R.drawable.cartype_xc51);
		}
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		View decorView = getWindow().getDecorView(); //获取顶层窗口
		int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
		decorView.setSystemUiVisibility(uiOptions);
		setContentView(R.layout.activity_check);
		mHandler = new Handler();
		mScore = -1;
		initViews();
		
	}


	@Override
	protected void onNewIntent(Intent intent) {
		Log.i(TAG, "onNewIntent");
		super.onNewIntent(intent);
		mScore = -1;
	}
	
	@Override
	protected void onResume() {
		Log.i(TAG, "onResume");
		mCarTypeName = Settings.System.getString(getContentResolver(), "cartype");
		if (mCarTypeName == null){
			mCarTypeName = CARTYPE_MC22;
		}
		super.onResume();
		if (mScore < 0){
			refreshLayout(true);
			refreshCarType(mCarTypeName);
		}
		else{
			refreshLayout(false);
			mHealthProgressBar.setProgress(mScore);
			refreshCheckedTips();
		}
	}
	
	@Override
	protected void onPause() {
		Log.i(TAG, "onPause");
		super.onPause();
		mHandler.removeCallbacks(mFinishRunnable);
	}
	@Override
	protected void onDestroy() {
		Log.i(TAG, "onDestroy");
		super.onDestroy();
	}
	
	private void initViews() {
		mLayoutChecking = (LinearLayout) findViewById(R.id.layout_checking);
		mLayoutChecked = (LinearLayout) findViewById(R.id.layout_checked);
		mLayoutWhole = (LinearLayout) findViewById(R.id.layout_whole);
		mLayoutChecked.setOnTouchListener(mTouchListener);
		mCarType = (ImageView) findViewById(R.id.cartype);
		mHealthProgressBar = (HealthProgressBar) findViewById(R.id.healthprogressbar);
		mCheckedTips = (TextView) findViewById(R.id.checked_tips);
	}
	
	private OnTouchListener mTouchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			mHandler.removeCallbacks(mFinishRunnable);
			mHandler.postDelayed(mFinishRunnable, 15*1000);
			return true;
		}
	};
	
	private Runnable mFinishRunnable = new Runnable() {
		
		@Override
		public void run() {
			finish();
		}
	};
	
	/**
	 * 更新布局
	 * @param bChecking true:检测中 false:检测完成
	 */
	private void refreshLayout(boolean bChecking){
		if (mLayoutChecking == null || mLayoutChecked == null)
			return;
		if (bChecking){
			mLayoutChecking.setVisibility(View.VISIBLE);
			mLayoutChecked.setVisibility(View.GONE);
		}
		else{
			mLayoutChecking.setVisibility(View.GONE);
			mLayoutChecked.setVisibility(View.VISIBLE);
			mHandler.postDelayed(mFinishRunnable, 15*1000);
		}
	}
	
	/**
	 * 更新车类型图片
	 * @param type 车类型
	 */
	private void refreshCarType(String carTypeName){
		if (mCarType == null)
			return;
		int imgId = carTypeMaps.get(carTypeName);
		if (imgId > 0){
			mCarType.setBackgroundResource(imgId);
		}
	}
	
	/**
	 * 更新检查结果提示信息
	 */
	private void refreshCheckedTips() {
		if (mCheckedTips == null || mHealthProgressBar == null)
			return;
		String tips = null;
		float progress = (float) mHealthProgressBar.getProgress()
				/ mHealthProgressBar.getMax();
		if (progress < 0.6) {
			tips = getResources().getString(R.string.checked_tips_warn);
		} else if (progress < 0.8) {
			tips = getResources().getString(R.string.checked_tips_normal);
		} else {
			tips = getResources().getString(R.string.checked_tips_well);
		}
		mCheckedTips.setText(tips);
	}
	
	
	/**刷新拨号按键*/
	private void updateCheckView(final String val) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					mScore = Integer.valueOf(val)/100;
					if (mScore > 100){
						mScore = 100;
					}
					if (mScore < 0){
						mScore = 0;
					}
					refreshLayout(false);
					mHealthProgressBar.setProgress(mScore);
					refreshCheckedTips();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private UpdateViewCallback mUpdateViewCallback = new UpdateViewCallback() {
		
		@Override
		public void onUpdate(int cmd, String val) {
			switch (cmd) {
			case UpdateUiManager.CMD_UPDATE_CHECK_SCORE:
				Log.i(TAG, "score = " + val);
				updateCheckView(val);
				break;
			}
		}
	};

	@Override
	protected UpdateViewCallback getUpdateViewCallback() {
		return mUpdateViewCallback;
	}
	
}
