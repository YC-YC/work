/**
 * 
 */
package com.zhonghong.rate;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhonghong.launcher.R;
import com.zhonghong.utils.DialogManager;

/**
 * @author YC
 * @time 2016-7-19 下午6:13:48
 * TODO:流量提醒对话框
 */
public class RateResultDialog extends Dialog implements View.OnClickListener{

	private Context mContext;
	private RateResultInfo mRateResultInfo;
	private ImageButton mBack;
	private TextView mRateAdverage, mRateMinValue, mRateMaxVal, mRateCheckTime;
	private ImageView mRateLeverHight, mRateLever80, mRateLever75, mRateLever70, mRateLever65, mRateLever60, mRateLeverLow;
	
	
	public RateResultDialog(Context context, RateResultInfo info) {
		super(context, /*android.R.style.Theme_Translucent_NoTitleBar*/R.style.Transparent);
		mContext = context;
		mRateResultInfo = info;
		setCusomView();
	}

	/**
	 * 自定义界面
	 */
	private void setCusomView() {
		View view = LayoutInflater.from(mContext).inflate(R.layout.layout_rate_checked_result, null);
		mBack = (ImageButton) view.findViewById(R.id.rate_result_back);
		mBack.setOnClickListener(this);
		mRateAdverage = (TextView) view.findViewById(R.id.rate_result_adverage);
		mRateAdverage.setText("" + mRateResultInfo.getAdverage());
		mRateMinValue = (TextView) view.findViewById(R.id.rate_result_minrateval);
		mRateMinValue.setText("" + mRateResultInfo.getMinRateValue());
		mRateMaxVal = (TextView) view.findViewById(R.id.rate_result_maxrateval);
		mRateMaxVal.setText("" + mRateResultInfo.getMaxRateValue());
		mRateCheckTime = (TextView) view.findViewById(R.id.rate_result_checktime);
		mRateCheckTime.setText("" + formatTime(mRateResultInfo.getCheckTime()));
		
		mRateLeverHight = (ImageView) view.findViewById(R.id.rate_result_score_lever_height);
		mRateLever80 = (ImageView) view.findViewById(R.id.rate_result_score_lever80);
		mRateLever75 = (ImageView) view.findViewById(R.id.rate_result_score_lever75);
		mRateLever70 = (ImageView) view.findViewById(R.id.rate_result_score_lever70);
		mRateLever65 = (ImageView) view.findViewById(R.id.rate_result_score_lever65);
		mRateLever60 = (ImageView) view.findViewById(R.id.rate_result_score_lever60);
		mRateLeverLow = (ImageView) view.findViewById(R.id.rate_result_score_lever_low);
		refreshRateLever();
		
		super.setContentView(view);
	}
	
	private void refreshRateLever(){
		int lever = getLever();
		mRateLeverHight.setSelected(false);
		mRateLever80.setSelected(false);
		mRateLever75.setSelected(false);
		mRateLever70.setSelected(false);
		mRateLever65.setSelected(false);
		mRateLever60.setSelected(false);
		mRateLeverLow.setSelected(false);
		switch (lever) {
		case 1:
			mRateLeverHight.setSelected(true);
			break;
		case 2:
			mRateLeverHight.setSelected(true);
			mRateLever80.setSelected(true);
			break;
		case 3:
			mRateLeverHight.setSelected(true);
			mRateLever80.setSelected(true);
			mRateLever75.setSelected(true);
			break;
		case 4:
			mRateLeverHight.setSelected(true);
			mRateLever80.setSelected(true);
			mRateLever75.setSelected(true);
			mRateLever70.setSelected(true);
			break;
		case 5:
			mRateLeverHight.setSelected(true);
			mRateLever80.setSelected(true);
			mRateLever75.setSelected(true);
			mRateLever70.setSelected(true);
			mRateLever65.setSelected(true);
			break;
		case 6:
			mRateLeverHight.setSelected(true);
			mRateLever80.setSelected(true);
			mRateLever75.setSelected(true);
			mRateLever70.setSelected(true);
			mRateLever65.setSelected(true);
			mRateLever60.setSelected(true);
			break;
		case 7:
			mRateLeverHight.setSelected(true);
			mRateLever80.setSelected(true);
			mRateLever75.setSelected(true);
			mRateLever70.setSelected(true);
			mRateLever65.setSelected(true);
			mRateLever60.setSelected(true);
			mRateLeverLow.setSelected(true);
			break;
		default:
			break;
		}
	}
	
	private int getLever(){
		int adverage = mRateResultInfo.getAdverage();
		if (adverage > 85){
			return 1;
		}
		if (adverage > 75){
			return 2;
		}
		if (adverage > 70){
			return 3;
		}
		if (adverage > 65){
			return 4;
		}
		if (adverage > 60){
			return 5;
		}
		if (adverage >= 60){
			return 6;
		}
		return 7;
		
	}
	
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
//		return;
	}
	
	@Override
	public void show() {
		super.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rate_result_back:
			DialogManager.getInstance().hideRateResultDialog();
			break;

		default:
			break;
		}
	}
	
	private String formatTime(long time){
//		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy:MM:dd-HH:mm:ss");       
		SimpleDateFormat formatter = new SimpleDateFormat ("mm:ss");       
		Date curDate = new Date(time);     
		String str = formatter.format(curDate);
		return str;
	}

}
