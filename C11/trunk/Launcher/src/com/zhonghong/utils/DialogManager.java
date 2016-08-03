/**
 * 
 */
package com.zhonghong.utils;

import com.zhonghong.rate.RateResultDialog;
import com.zhonghong.rate.RateResultInfo;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

/**
 * @author YC
 * @time 2016-7-19 下午8:27:39 TODO:管理对话框
 */
public class DialogManager {

	private static DialogManager instance;

	private RateResultDialog mRateResultDialog;

	public static DialogManager getInstance() {
		if (instance == null) {
			synchronized (DialogManager.class) {
				if (instance == null) {
					instance = new DialogManager();
				}
			}
		}
		return instance;
	}

	/**
	 * 显示对话框
	 */
	public void showRateResultDialog(final Context context, final RateResultInfo info) {
//		if (mCarFlowDialog == null) {
			mRateResultDialog = new RateResultDialog(context.getApplicationContext(),
					info);
			Window window = mRateResultDialog.getWindow();
			LayoutParams params = window.getAttributes();
			params.dimAmount = 0.8f;
			params.type = LayoutParams.TYPE_SYSTEM_ALERT;
			params.flags = LayoutParams.FLAG_ALT_FOCUSABLE_IM;
			params.gravity = Gravity.CENTER;
			window.addFlags(/*LayoutParams.FLAG_NOT_FOCUSABLE
					| */LayoutParams.FLAG_TRANSLUCENT_NAVIGATION	//与状态栏透明
					| LayoutParams.FLAG_TRANSLUCENT_STATUS
					| LayoutParams.FLAG_NOT_TOUCH_MODAL
					| LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
			window.setAttributes(params);
			mRateResultDialog.setCanceledOnTouchOutside(true);
//		}
		if (!mRateResultDialog.isShowing()) {
			mRateResultDialog.show();
		}
	}

	public void hideRateResultDialog() {
		if (mRateResultDialog != null && mRateResultDialog.isShowing()) {
			mRateResultDialog.dismiss();
		}
		mRateResultDialog = null;
	}


	private DialogManager() {

	}
}
