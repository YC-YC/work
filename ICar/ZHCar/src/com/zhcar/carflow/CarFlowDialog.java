/**
 * 
 */
package com.zhcar.carflow;

import com.zhcar.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * @author YC
 * @time 2016-7-19 下午6:13:48
 * TODO:流量提醒对话框
 */
public class CarFlowDialog extends Dialog {

	private Context mContext;
	private String mMessage;

	public CarFlowDialog(Context context, String msg) {
		super(context, /*android.R.style.Theme_Translucent_NoTitleBar*/R.style.Transparent);
		mContext = context;
		mMessage = msg;
		setCusomView();
	}

	/**
	 * 自定义界面
	 */
	private void setCusomView() {
		View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_carflow, null);
		TextView content = (TextView) view.findViewById(R.id.carflow_tip_content);
		content.setText(mMessage);
		super.setContentView(view);
	}
	
	@Override
	public void onBackPressed() {
//		super.onBackPressed();
		return;
	}

}
