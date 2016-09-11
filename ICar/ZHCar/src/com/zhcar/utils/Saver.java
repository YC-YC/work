/**
 * 
 */
package com.zhcar.utils;

import com.zhcar.base.BaseApplication;
import com.zhcar.data.GlobalData;

import android.provider.Settings;

/**
 * @author YC
 * @time 2016-9-11 上午9:18:27 TODO:全局数据保存
 */
public class Saver {

	/** 环境状态 */
	public static final String KEY_ENVIRONMENT = "environment";
	/** 测试环境 */
	public static final String ENVIRONMENT_TEST = "environment_test";
	/** 生产环境 */
	public static final String ENVIRONMENT_PRODUCT = "environment_product";

	
	public static void setEnvironment(String environment) {
		Settings.System.putString(BaseApplication.getInstanse()
				.getContentResolver(), KEY_ENVIRONMENT, environment);
	}
	
	public static void setEnvironment(int environment) {
		String env = ENVIRONMENT_TEST;
		if (environment == GlobalData.ENV_PROCDUCT){
			env = ENVIRONMENT_PRODUCT;
		}
		Settings.System.putString(BaseApplication.getInstanse()
				.getContentResolver(), KEY_ENVIRONMENT, env);
	}

	public static String getEnvironMent() {
		return Settings.System.getString(BaseApplication.getInstanse()
				.getContentResolver(), KEY_ENVIRONMENT);
	}
	
	public static boolean isEnvironmentProduct(){
		String environMent = getEnvironMent();
		return (environMent == null | ENVIRONMENT_PRODUCT.equals(environMent));
	}
}
