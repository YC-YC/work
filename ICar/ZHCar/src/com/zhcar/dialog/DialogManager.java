/**
 * 
 */
package com.zhcar.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;

import com.zhcar.R;
import com.zhcar.utils.Utils;

/**
 * @author YC
 * @time 2016-7-19 下午8:27:39 TODO:管理对话框
 */
public class DialogManager {

	private static DialogManager instance;

	private CarFlowDialog mCarFlowDialog;

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
	 * 显示流量提醒对话框
	 */
	public void showCarFlowDialog(final Context context, final String tips) {
		if (mCarFlowDialog == null) {
			mCarFlowDialog = new CarFlowDialog(context.getApplicationContext(),
					tips);
			Window window = mCarFlowDialog.getWindow();
			LayoutParams params = window.getAttributes();
			params.dimAmount = 0.8f;
			params.type = LayoutParams.TYPE_SYSTEM_ALERT;
			params.flags = LayoutParams.FLAG_ALT_FOCUSABLE_IM;
			params.gravity = Gravity.CENTER;
			window.addFlags(LayoutParams.FLAG_NOT_FOCUSABLE
//					| LayoutParams.FLAG_TRANSLUCENT_NAVIGATION	//与状态栏透明
					| LayoutParams.FLAG_NOT_TOUCH_MODAL
					| LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
			window.setAttributes(params);
			mCarFlowDialog.setCanceledOnTouchOutside(true);
			Button btnConfirm = (Button) mCarFlowDialog.findViewById(R.id.carflow_confirm);
			btnConfirm.setOnClickListener(mOnClickListener);
			Button btnCancle = (Button) mCarFlowDialog.findViewById(R.id.carflow_cancle);
			btnCancle.setOnClickListener(mOnClickListener);
		}
		if (!mCarFlowDialog.isShowing()) {
			mCarFlowDialog.show();
		}
	}

	public void hideCarFlowDialog() {
		if (mCarFlowDialog != null && mCarFlowDialog.isShowing()) {
			mCarFlowDialog.dismiss();
		}
		mCarFlowDialog = null;
	}

	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.carflow_confirm:
				hideCarFlowDialog();
				Utils.startOtherActivity(v.getContext(), Utils.ZH_BUY_PKG, Utils.ZH_BUY_CLZ);
				break;
			case R.id.carflow_cancle:
				hideCarFlowDialog();
				break;
			}
		}
	};

	private DialogManager() {

	}
}
