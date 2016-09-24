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
	/** 生产环境 */
	private static final int ENVIRONMENT_PRODUCT = 0;
	/** 测试环境 */
	  private static final int ENVIRONMENT_TEST = 1;

	  public static boolean isEnvironmentProduct()
	  {
	    int i = Settings.System.getInt(BaseApplication.getInstanse().getContentResolver(), KEY_ENVIRONMENT, ENVIRONMENT_PRODUCT);
	    boolean bIsProduct = false;
	    if (i == ENVIRONMENT_PRODUCT)
	    	bIsProduct = true;
	    return bIsProduct;
	  }

	  public static void setEnvironment(boolean bIsTest)
	  {
	    if (!bIsTest)
	    {
	      Settings.System.putInt(BaseApplication.getInstanse().getContentResolver(), KEY_ENVIRONMENT, ENVIRONMENT_PRODUCT);
	      return;
	    }
	    Settings.System.putInt(BaseApplication.getInstanse().getContentResolver(), KEY_ENVIRONMENT, ENVIRONMENT_TEST);
	  }
}
