/**
 * 
 */
package com.zhonghong.base;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;

import com.zhonghong.utils.UpdateUiManager;
import com.zhonghong.utils.UpdateUiManager.UpdateViewCallback;

/**
 * @author YC
 * @time 2016-7-21 上午9:45:22 
 * TODO: 更新Ui基准Activity，只要继承即可回调
 */
public abstract class UpdateUiBaseActivity extends FragmentActivity {

	protected UpdateViewCallback mCallback;

	@Override
	protected void onResume() {
		super.onResume();
		mCallback = getUpdateViewCallback();
		if (mCallback != null){
			UpdateUiManager.getInstances().register(mCallback);
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mCallback != null){
			UpdateUiManager.getInstances().unregister(mCallback);
		}
	}
	
	/**
	 * @return 返回一个UpdateViewCallback实例，需要子类去实现
	 */
	protected abstract UpdateViewCallback getUpdateViewCallback();

}
