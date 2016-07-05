/**
 * 
 */
package com.zhonghong.sublauncher;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * @author YC
 * @time 2016-7-4 上午10:08:58
 * TODO:副屏Fragment适配器
 */
public class PageFragmentAdapter extends FragmentPagerAdapter {

	private List<Fragment> mFragments;

	public PageFragmentAdapter(FragmentManager fm, List<Fragment> fragments) {
		super(fm);
		mFragments = fragments;
	}

	@Override
	public Fragment getItem(int location) {
		return mFragments.get(location);
	}

	@Override
	public int getCount() {
		return mFragments.size();
	}

}
