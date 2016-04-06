/**
 * 
 */
package com.zhcl.ui.music;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/** 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author zhonghong.chenli
 * @date 2015-12-15 下午11:05:29 
 */
public abstract class BaseFragmentABS extends Fragment implements ChildCallBack {

	/** 宿主回调对象 */
	protected HostCallBack mHostCallBack;
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mHostCallBack = (HostCallBack)activity;	//得到宿主
		mHostCallBack.addChildCallBack(this);	//向宿主注册回调
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		mHostCallBack.removeChildCallBack(this);
		mHostCallBack = null;
	}
}
