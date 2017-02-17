/**
 * 
 */
package com.zhcar.utils;

import com.zhcar.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * @author YC
 * @time 2016-8-10 下午4:58:31
 * TODO：通用对话框
 */
public class NormalDialog extends Dialog {

	private Context mContext;
	private int mId;

	public NormalDialog(Context context, int id) {
		super(context, /*android.R.style.Theme_Translucent_NoTitleBar*/R.style.Transparent);
		mContext = context;
		mId = id;
		setCusomView();
	}

	/**
	 * 自定义界面
	 */
	private void setCusomView() {
		View view = LayoutInflater.from(mContext).inflate(mId, null);
		super.setContentView(view);
	}
	
	@Override
	public void onBackPressed() {
//		super.onBackPressed();
		return;
	}

}
