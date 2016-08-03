/**
 * 
 */
package com.zhcar.carflow;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.zhcar.R;
import com.zhcar.data.FlowInfoBean;
import com.zhcar.data.GlobalData;
import com.zhcar.dialog.DialogManager;
import com.zhcar.provider.CarProviderData;
import com.zhcar.utils.GPRSManager;
import com.zhcar.utils.Utils;
import com.zhcar.utils.http.HttpStatusCallback;

/**
 * @author YC
 * @time 2016-7-20 下午4:37:57
 * TODO:查询流量管理类
 */
public class CarFlowManager {

	private static final String TAG = "CarFlow";
	private GetFlowLoc mGetFlowLoc;
	
	private Context mContext;
	private ContentResolver mResolver;
	private static CarFlowManager instance;
	public static CarFlowManager getInstance(Context context){
		if (instance == null){
			synchronized (CarFlowManager.class) {
				if (instance == null){
					instance = new CarFlowManager(context);
				}
			}
		}
		return instance;
	}
	
	
	/**
	 * 网络请求流量信息
	 * @param iccid
	 * @param token
	 */
	public void HttpRequestCarFlow(String iccid, String token){
		if (mGetFlowLoc == null){
			mGetFlowLoc = new GetFlowLoc();
		}
		mGetFlowLoc.SetInfo(iccid, token);
		mGetFlowLoc.SetHttpStatusCallback(new HttpStatusCallback() {
			
			@Override
			public void onStatus(int status) {
				Log.i(TAG, "get Http status = " + status);
				if (status == HttpStatusCallback.RESULT_SUCCESS){
					FlowInfoBean flowInfo = mGetFlowLoc.GetFlowInfo();
					Log.i(TAG, "getFlowInfo = " + flowInfo);
					GlobalData.flowInfo.setSurplusFlow(flowInfo.getSurplusFlow());
					GlobalData.flowInfo.setUseFlow(flowInfo.getUseFlow());
					GlobalData.flowInfo.setCurrFlowTotal(flowInfo.getCurrFlowTotal());
					updateFlowInfoProvider();
				}
			}
		});
		mGetFlowLoc.Refresh();
	}
	
	/**
	 * 更新流量信息到provider
	 */
	private void updateFlowInfoProvider() {
		ContentValues values = new ContentValues();
		values.put(CarProviderData.KEY_FLOWINFO_CURRFLOWTATAL, GlobalData.flowInfo.getCurrFlowTotal());
		values.put(CarProviderData.KEY_FLOWINFO_USEFLOW, GlobalData.flowInfo.getUseFlow());
		values.put(CarProviderData.KEY_FLOWINFO_SURPLUSFLOW, GlobalData.flowInfo.getSurplusFlow());
		Cursor cursor = mResolver.query(CarProviderData.URI_FLOWINFO, null, null, null, null);
		if(cursor != null && cursor.moveToNext()){
			int row = mResolver.update(CarProviderData.URI_FLOWINFO, values, null, null);
		}
		else{
			mResolver.insert(CarProviderData.URI_FLOWINFO, values);
		}
		if (cursor != null){
			cursor.close();
			cursor = null;
		}
	}
	
	
	/**
	 * 从provider获取流量信息
	 * @return
	 */
	private boolean queryFlowInfo() {
		Cursor cursor = mResolver.query(CarProviderData.URI_FLOWINFO, null, null, null, null);
		if(cursor != null && cursor.moveToNext()){
			String totalStr = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_FLOWINFO_CURRFLOWTATAL));
			String usedStr = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_FLOWINFO_USEFLOW));
			String surplusStr = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_FLOWINFO_SURPLUSFLOW));
			String remindValStr = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_FLOWINFO_REMINDVALUE));
			
			Log.i(TAG, "查询流量结果为：totalStr = " + totalStr + ", usedStr = " + usedStr 
					+ ", surplusStr = " + surplusStr + ", remindValStr = " + remindValStr);
			cursor.close();
			cursor = null;
			return true;
		}
		else{
			if (cursor != null){
				cursor.close();
				cursor = null;
			}
			Log.i(TAG, "无查询结果");
			return false;
		}
	}
	
	private CarFlowManager(Context context){
		mContext = context.getApplicationContext();
		mResolver = mContext.getContentResolver();
		mResolver.registerContentObserver(CarProviderData.URI_FLOWINFO, true, new FlowObserver(new Handler(){}));
		
	}
	
	private class FlowObserver extends ContentObserver{

		public FlowObserver(Handler handler) {
			super(handler);
		}
		
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			Log.i(TAG, "FlowInfo数据变化");
			checkCarFlow();
		}
	};
	
	/**
	 * 提示流量信息
	 */
	public void checkCarFlow(){
		if (GlobalData.bPermissionStatus){
			if (queryFlowInfo()){
				String flowTotalStr = GlobalData.flowInfo.getCurrFlowTotal();
				String remindValueStr = GlobalData.flowInfo.getRemindValue();
				String surplusFlowStr = GlobalData.flowInfo.getSurplusFlow();
				float totalVal = 0.0f;
				float remindValue = 0.0f;
				float surplusVal = 0.0f;
				try {
					totalVal = Float.valueOf(flowTotalStr);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (Utils.isEmpty(remindValueStr) || "null".equals(remindValueStr)){
					remindValue = 0.1f * totalVal;
				}
				else{
					try {
						remindValue = Float.valueOf(remindValueStr);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				try {
					surplusVal = Float.valueOf(surplusFlowStr);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Log.i(TAG, "totalVal = " + totalVal + ", remindValue = " + remindValue + ", surplusVal = " + surplusVal);
				if (remindValue < 1.0f){
					//关闭3G
					GPRSManager gprsManager = new GPRSManager(mContext);
					if (gprsManager.isEnable()){
						gprsManager.turnOff();
					}
					DialogManager.getInstance().showCarFlowDialog(mContext, getTipStr(surplusVal));
				}
				else{
//					if (surplusVal < remindValue){
						DialogManager.getInstance().showCarFlowDialog(mContext, getTipStr(surplusVal));
//					}
				}
			}
		}
	}
	
	private String getTipStr(float surplusVal){
		return mContext.getResources().getString(R.string.carflow_tip_content1) + surplusVal + mContext.getResources().getString(R.string.carflow_tip_content2);
	}
}
