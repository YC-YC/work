/**
 * 
 */
package com.zhcl.ui.video;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zh.uitls.L;
import com.zhcl.ui.widget.PagerSlidingTabStrip;
import com.zhcl.video.VideoManager;
import com.zhcl.zhvideo.LocalFragmentManager;
import com.zhonghong.zhvideo.R;

/**
 * 所有音乐集合
 * @author ChenLi
 */
public class LocalVideoFragment extends Fragment implements ChildCallBack{
	/** 宿主回调对象 */
	protected HostCallBack mHostCallBack;
	/** 列表List Fragment */
	public static final int ALBUM_LIST_FRAGMENT = 2;
	/** 文件夹list Fragment*/
	public static final int DIR_LIST_FRAGMENT = 3;
	public interface StateI{
		/** 是否扫描完成 */
		public boolean isScanfEnd();
	}
	private static final String tag = "LocalVideoActivity";
	Context context;
	PagerSlidingTabStrip tabs;
	ViewPager pager;
	DisplayMetrics dm;
	/** 视频界面 */
	AllVideoFragment mAllVideoFragment;
	/** 视频文件夹界面 */
	AllVideoDirFragment mAllVideoDirFragment;
	
//	String[] titles = {"视频",  "文件夹" };
	String[] titles = {"视频"};
	View view;
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		context = inflater.getContext();
		if(view != null){
			ViewGroup parent = (ViewGroup) view.getParent();
			if(parent != null){
				parent.removeView(view);
			}
			return view;
		}
		view = inflater.inflate(R.layout.local_video_fragment, container, false);
		LocalFragmentManager.getInstance().init(context, this);
		initView(view);
		return view;
	}
	MyAdapter mMyAdapter;
	private void initView(View view){ 
		dm = getResources().getDisplayMetrics();
		pager = (ViewPager) view.findViewById(R.id.pager);
		tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
		
		if(mMyAdapter == null){
			mMyAdapter = new MyAdapter(((FragmentActivity)context).getSupportFragmentManager(), titles);
		}
		
		pager.setAdapter(mMyAdapter);
		tabs.setViewPager(pager);
	} 
	
	
	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		updataTitle();
	}

	/**
	 * 更新标题
	 */
	private void updataTitle(){
		if(tabs != null && tabs.getHandler() != null){
			tabs.getHandler().post(new Thread(){
				public void run(){
					tabs.updataTitile();
				}
			});
		}
	}
	/**
	 * @author ChenLi
	 */
	public class MyAdapter extends FragmentPagerAdapter {
		String[] _titles;

		public MyAdapter(FragmentManager fm, String[] titles) {
			super(fm);
			_titles = titles;
		}

		@Override
		public CharSequence getPageTitle(int position) { 
			String result = "";
			VideoManager mVideoManager = VideoManager.getIntance(context);
			switch(position){
			case 0:
				result = "视频/" + mVideoManager.getAllVideo().size();
				break;
			case 1:
				result = "文件夹/" + mVideoManager.getAllVideoDir().size();
				break;
			}
			return result;
//			return _titles[position];
		}

		@Override
		public int getCount() {
			return _titles.length;
		}

		@Override
		public Fragment getItem(int position) {
			Fragment mFragment = null;
			L.e(tag, "getItem position = " + position);
			switch (position) {
			case 0:  
				if (mAllVideoFragment == null) {
					L.e(tag, "new AllVideoFragment");
					mAllVideoFragment = new AllVideoFragment();
				}
				mFragment = mAllVideoFragment;
				break;
			case 1:
				if(mAllVideoDirFragment == null){
					L.e(tag, "new mAllVideoDirFragment");
					mAllVideoDirFragment = new AllVideoDirFragment();
				}
				mFragment = mAllVideoDirFragment;
				break;
			}
			return mFragment;
		}
	}


	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mHostCallBack = (HostCallBack)activity;	//得到宿主
		mHostCallBack.addChildCallBack(this);	//向宿主注册回调
	}
	
	@Override
	public Object notifyInfo(int cmd, Object o) {
		Object result = null;
		switch(cmd){
		case HostCallBack.STATE_SUCCESS:
			updataTitle();
			break;
		}
		
		return result;  
	}
	
}
