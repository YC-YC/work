/**
 * 
 */
package com.zhcar.check;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhcar.R;
import com.zhcar.widget.HealthProgressBar;

/**
 * @author YC
 * @time 2016-7-14 下午12:23:06
 * TODO:自动检测
 */
public class CheckActivity extends Activity {

	private LinearLayout mLayoutChecking, mLayoutChecked;
	private ImageView mCarType;
	private HealthProgressBar mHealthProgressBar;
	private TextView mCheckedTips;
	
	
	private static final Map<Integer, Integer> carTypeMaps = new HashMap<Integer, Integer>(){
		{
			put(1, R.drawable.cartype_1);
			put(2, R.drawable.cartype_2);
			put(3, R.drawable.cartype_3);
			put(4, R.drawable.cartype_4);
			put(5, R.drawable.cartype_5);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View decorView = getWindow().getDecorView(); //获取顶层窗口
		int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
		decorView.setSystemUiVisibility(uiOptions);
		setContentView(R.layout.activity_check);
		
		initViews();
		/*new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				refreshLayout(false);
			}
		}, 10*1000);*/
	}

	@Override
	protected void onResume() {
		super.onResume();
		refreshLayout(true);
		refreshCarType(3);
		refreshCheckedTips();
	}
	
	private void initViews() {
		mLayoutChecking = (LinearLayout) findViewById(R.id.layout_checking);
		mLayoutChecked = (LinearLayout) findViewById(R.id.layout_checked);
		mCarType = (ImageView) findViewById(R.id.cartype);
		mHealthProgressBar = (HealthProgressBar) findViewById(R.id.healthprogressbar);
//		mHealthProgressBar.setProgress(70);
//		mHealthProgressBar.setReachBarColor(new int[]{Color.YELLOW, Color.YELLOW});
		
		mCheckedTips = (TextView) findViewById(R.id.checked_tips);
	}
	
	
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
		}
	}
	
	/**
	 * 更新车类型图片
	 * @param type 车类型
	 */
	private void refreshCarType(int type){
		if (mCarType == null)
			return;
		int imgId = carTypeMaps.get(type);
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
	
}
