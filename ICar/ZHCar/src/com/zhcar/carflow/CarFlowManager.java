/**
 * 
 */
package com.zhcar.carflow;

import java.util.HashMap;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.zhcar.R;
import com.zhcar.data.FlowInfoBean;
import com.zhcar.data.GlobalData;
import com.zhcar.provider.CarProviderData;
import com.zhcar.utils.DialogManager;
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
	
	private FlowInfoBean mFlowInfo;
	
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
			public void onStatus(int status, int appUseRecordHashCode) {
				Log.i(TAG, "get Http status = " + status);
				if (status == HttpStatusCallback.RESULT_SUCCESS){
					FlowInfoBean flowInfo = mGetFlowLoc.GetFlowInfo();
//					Log.i(TAG, "getFlowInfo = " + flowInfo);
					GlobalData.flowInfo.setUseFlow(flowInfo.getUseFlow());
					GlobalData.flowInfo.setCurrFlowTotal(flowInfo.getCurrFlowTotal());
					GlobalData.flowInfo.setSurplusFlow(flowInfo.getSurplusFlow());
					updateFlowInfoProvider();
					Log.i(TAG, "getFlowInfo OK");
					new Handler().post(new Runnable() {
						
						@Override
						public void run() {
							Log.i(TAG, "getFlowInfo postDelayed");
							checkCarFlow();
						}
					});
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
			String remindStr = cursor.getString(cursor.getColumnIndex(CarProviderData.KEY_FLOWINFO_REMIDE));
			
			mFlowInfo = new FlowInfoBean(remindValStr, remindStr, usedStr, surplusStr, totalStr);
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
//		if (GlobalData.bPermissionStatus)
		{
			if (queryFlowInfo()){
				if (!mFlowInfo.getRemindVal()){
					Log.i(TAG, "not reminded");
					return;
				}
//				Log.i(TAG, "totalVal = " + totalVal + ", remindValue = " + remindValue + ", surplusVal = " + surplusVal);
				float totalVal = getFloatStrVal(mFlowInfo.getCurrFlowTotal());
				float remindValue = getFloatStrVal(mFlowInfo.getRemindValue());
				if (remindValue == 0.0f){
					//不设置则为10%
					remindValue = 0.1f * totalVal;
				}
				float surplusVal = getFloatStrVal(mFlowInfo.getSurplusFlow());
				Log.i(TAG, "totalVal = " + totalVal + ", remindValue = " + remindValue + ", surplusVal = " + surplusVal);
				if (surplusVal < 1.0f){
					//关闭3G
					GPRSManager gprsManager = new GPRSManager(mContext);
					if (gprsManager.isEnable()){
						gprsManager.turnOff();
					}
					Utils.sendBroadcast(mContext, GlobalData.ACTION_3G, GlobalData.KEY_3G_CONTROL, "off");
					DialogManager.getInstance().showNormalDialog(mContext, R.layout.dialog_noflow);
				}
				else{
					if (surplusVal < remindValue){
						DialogManager.getInstance().showCarFlowDialog(mContext, getTipStr(surplusVal));
					}
				}
			}
		}
	}
	
	private String getTipStr(float surplusVal){
		return mContext.getResources().getString(R.string.carflow_tip_content1) + surplusVal + mContext.getResources().getString(R.string.carflow_tip_content2);
	}
	
	
	private float getFloatStrVal(String floatStr){
		float result = 0.0f;
		if (!TextUtils.isEmpty(floatStr) && !"null".equals(floatStr)){
			try {
				result = Float.valueOf(floatStr);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}
