/**
 * 
 */
package com.zhcar.ecall;

import com.zhcar.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * @author YC
 * @time 2016-8-4 上午9:17:05
 * TODO:紧急联系人
 */

public class ECallDialog extends Dialog {

	private Context mContext;
	private String mMessage;
	private TextView mContent;

	public ECallDialog(Context context, String msg) {
		super(context, /*android.R.style.Theme_Translucent_NoTitleBar*/R.style.Transparent);
		mContext = context;
		mMessage = msg;
		setCusomView();
	}

	/**
	 * 自定义界面
	 */
	private void setCusomView() {
		View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_emergency_call, null);
		mContent = (TextView) view.findViewById(R.id.emergency_call_tips);
		mContent.setText(mMessage);
		super.setContentView(view);
	}
	
	/**
	 * 更新提示状态信息
	 * @param msg
	 */
	public void updateContent(String msg){
		mContent.setText(msg);
	}
	
	
	@Override
	public void onBackPressed() {
//		super.onBackPressed();
		return;
	}

}
