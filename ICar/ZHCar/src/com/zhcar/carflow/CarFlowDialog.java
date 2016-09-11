/**
 * 
 */
package com.zhcar.carflow;

import com.zhcar.R;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * @author YC
 * @time 2016-7-19 下午6:13:48
 * TODO:流量提醒对话框
 */
public class CarFlowDialog extends Dialog {

	private static final String TAG = "CarFlowDialog";
	private Context mContext;
	private String mMessage;

	public CarFlowDialog(Context context, String msg) {
		super(context, R.style.Transparent);
		Log.i(TAG, "setCusomView");
		mContext = context;
		mMessage = msg;
		setCusomView();
	}

	/**
	 * 自定义界面
	 */
	private void setCusomView() {
		Log.i(TAG, "setCusomView");
		View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_carflow, null);
		Log.i(TAG, "setCusomView 1111");
		TextView content = (TextView) view.findViewById(R.id.carflow_tip_content);
		content.setText(mMessage);
		super.setContentView(view);
	}
	
	@Override
	public void onBackPressed() {
//		super.onBackPressed();
		return;
	}
	
	@Override
	protected void finalize() throws Throwable {
		Log.i(TAG, "finalize");
		super.finalize();
	}

}
