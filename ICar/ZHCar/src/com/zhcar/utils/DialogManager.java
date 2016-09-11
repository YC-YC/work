/**
 * 
 */
package com.zhcar.utils;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;

import com.zhcar.R;
import com.zhcar.carflow.CarFlowDialog;
import com.zhcar.ecall.ECallDialog;
import com.zhcar.ecall.ECallManager;

/**
 * @author YC
 * @time 2016-7-19 下午8:27:39 TODO:管理对话框
 */
public class DialogManager {

	private static final String TAG = "DialogManager";

	private static DialogManager instance;

	private CarFlowDialog mCarFlowDialog;
	private NormalDialog mNormalDialog;
	private ECallDialog mEmergencyCallDialog;

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
		Log.i(TAG, "showCarFlowDialog");
		if (mCarFlowDialog == null) {
			Log.i(TAG, "new CarFlowDialog");
			mCarFlowDialog = new CarFlowDialog(context.getApplicationContext(),
					tips);
			Log.i(TAG, "end new CarFlowDialog00000");
			Window window = mCarFlowDialog.getWindow();
//			Log.i(TAG, "end new CarFlowDialog111111");
			LayoutParams params = window.getAttributes();
			params.dimAmount = 0.8f;
			params.type = LayoutParams.TYPE_SYSTEM_ALERT;
			params.flags = LayoutParams.FLAG_ALT_FOCUSABLE_IM;
			params.gravity = Gravity.CENTER;
			window.addFlags(LayoutParams.FLAG_NOT_FOCUSABLE
//					|View.SYSTEM_UI_FLAG_LAYOUT_STABLE	//稳定位置
//					|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//					|View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION //视图延伸到导航栏
//					|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY	//沉浸式
//					| LayoutParams.FLAG_TRANSLUCENT_NAVIGATION	//与状态栏透明
					| LayoutParams.FLAG_NOT_TOUCH_MODAL
					| LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
			window.setAttributes(params);
//			Log.i(TAG, "end new CarFlowDialog22222");
			mCarFlowDialog.setCanceledOnTouchOutside(true);
			Button btnConfirm = (Button) mCarFlowDialog.findViewById(R.id.carflow_confirm);
			btnConfirm.setOnClickListener(mOnClickListener);
			Button btnCancle = (Button) mCarFlowDialog.findViewById(R.id.carflow_cancle);
			btnCancle.setOnClickListener(mOnClickListener);
//			Log.i(TAG, "end new CarFlowDialog");
		}
		if (!mCarFlowDialog.isShowing()) {
//			Log.i(TAG, "showCarFlowDialog show");
			mCarFlowDialog.show();
		}
		else{
//			Log.i(TAG, "showCarFlowDialog isShowing");
		}
	}

	public void hideCarFlowDialog() {
		Log.i(TAG, "hideCarFlowDialog");
		if (mCarFlowDialog != null && mCarFlowDialog.isShowing()) {
			mCarFlowDialog.dismiss();
		}
		mCarFlowDialog = null;
	}
	
	/**
	 * 显示流量提醒对话框
	 */
	public void showNormalDialog(final Context context, final int id) {
//		if (mNormalDialog == null) {
			mNormalDialog = new NormalDialog(context.getApplicationContext(), id);
			Window window = mNormalDialog.getWindow();
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
			mNormalDialog.setCanceledOnTouchOutside(true);
			Button btnCancle = (Button) mNormalDialog.findViewById(R.id.normal_dialog_cancle);
			btnCancle.setOnClickListener(mOnClickListener);
//		}
		if (!mNormalDialog.isShowing()) {
			mNormalDialog.show();
		}
	}

	public void hideNormalFlowDialog() {
		if (mNormalDialog != null && mNormalDialog.isShowing()) {
			mNormalDialog.dismiss();
		}
		mNormalDialog = null;
	}
	
	
	/**
	 * 显示流量提醒对话框
	 */
	public void showECallDialog(final Context context, final String tips) {
		if (mEmergencyCallDialog == null) {
			mEmergencyCallDialog = new ECallDialog(context.getApplicationContext(),
					tips);
			Window window = mEmergencyCallDialog.getWindow();
			LayoutParams params = window.getAttributes();
			params.dimAmount = 0.8f;
			params.type = LayoutParams.TYPE_SYSTEM_ALERT;
			params.flags = LayoutParams.FLAG_ALT_FOCUSABLE_IM;
			params.gravity = Gravity.CENTER;
			params.width = 560;
			params.height = 264;
			window.addFlags(LayoutParams.FLAG_NOT_FOCUSABLE
//					| LayoutParams.FLAG_TRANSLUCENT_NAVIGATION	//与状态栏透明
					| LayoutParams.FLAG_NOT_TOUCH_MODAL
					| LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
			window.setAttributes(params);
			mEmergencyCallDialog.setCanceledOnTouchOutside(true);
			mEmergencyCallDialog.findViewById(R.id.handup)
								.setOnClickListener(mOnClickListener);
		}
		if (!mEmergencyCallDialog.isShowing()) {
			mEmergencyCallDialog.show();
		}
	}

	public void updateEmergencyCallContent(String msg){
		if (mEmergencyCallDialog != null && mEmergencyCallDialog.isShowing()) {
			mEmergencyCallDialog.updateContent(msg);
		}
	}
	
	public void hideECallDialog() {
		if (mEmergencyCallDialog != null && mEmergencyCallDialog.isShowing()) {
			mEmergencyCallDialog.dismiss();
		}
		mEmergencyCallDialog = null;
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
			case R.id.handup:
				ECallManager.getInstance().endECall();
				break;
			case R.id.normal_dialog_cancle:
				hideNormalFlowDialog();
				break;
			}
		}
	};

	private DialogManager() {

	}
}
