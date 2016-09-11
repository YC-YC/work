/**
 * 
 */
package com.zhcar.base;

import android.app.Application;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.util.Log;

import com.yc.external.PostFromZUI;
import com.zhcar.carflow.CarFlowManager;
import com.zhcar.data.GlobalData;
import com.zhcar.provider.CarProviderData;
import com.zhcar.utils.Saver;
import com.zhcar.utils.UpdateUiManager;

/**
 * @author YC
 * @time 2016-7-20 上午11:56:49
 * TODO:
 */
public class BaseApplication extends Application {

	private static final String TAG = "ZhCarApplication";
	
	private static BaseApplication instanse;
	private PostFromZUI mPostFromZUI;
	
	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate");
		super.onCreate();
		instanse = this;
		GlobalData.mContext = this;
		mPostFromZUI = new PostFromZUI(this);
		CarFlowManager.getInstance(this);
		GlobalData.environments = queryEnvironments();
		Saver.setEnvironment(GlobalData.environments);
		Log.i(TAG, "环境配置为：" + ((GlobalData.environments == GlobalData.ENV_PROCDUCT)?"生产环境":"测试环境"));
		getContentResolver().registerContentObserver(CarProviderData.URI_CONFIG, true, new ConfigObserver(new Handler(){}));
	}
	
	public static BaseApplication getInstanse(){
		return instanse;
	}
	
	
	private int queryEnvironments() {
		Cursor cursor = getContentResolver().query(CarProviderData.URI_CONFIG, null, null, null, null);
		int environments = GlobalData.ENV_TEST;
		if(cursor != null && cursor.moveToNext()){
			environments = cursor.getInt(cursor.getColumnIndex(CarProviderData.KEY_CONFIG_ENVIRONMENTS));
		}
		if (cursor != null){
			cursor.close();
			cursor = null;
		}
		return environments;
	}
	
	private class ConfigObserver extends ContentObserver{

		public ConfigObserver(Handler handler) {
			super(handler);
		}
		
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			Log.i(TAG, "环境配置信息改变");
			GlobalData.environments = queryEnvironments();
			Saver.setEnvironment(GlobalData.environments);
			UpdateUiManager.getInstances().callUpdate(UpdateUiManager.CMD_UPDATE_ENVIRONMENT, "");
		}
	};

}
