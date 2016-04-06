/**
 * 
 */
package com.zhcl.ui.music;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
import android.webkit.WebView.FindListener;

import com.zh.uitls.L;
import com.zh.uitls.Utils;
import com.zhcl.media.SongManager;
import com.zhcl.service.PlayerCode;
import com.zhcl.ui.widget.PagerSlidingTabStrip;
import com.zhonghong.zhmedia.LocalFragmentManager;
import com.zhonghong.zhmedia.R;

/**
 * 所有音乐集合
 * @author ChenLi
 */
public class LocalMusicFragment extends Fragment implements ChildCallBack{
	/** 宿主回调对象 */
	protected HostCallBack mHostCallBack;
	/** 歌手列表 Fragment */
	public static final int SINGER_LIST_FRAGMENT = 1;
	/** 列表List Fragment */
	public static final int ALBUM_LIST_FRAGMENT = 2;
	/** 文件夹list Fragment*/
	public static final int DIR_LIST_FRAGMENT = 3;
	/** 背景 */
	public View bgView;
	public interface StateI{
		/** 是否扫描完成 */
		public boolean isScanfEnd();
	}
	private static final String tag = LocalMusicFragment.class.getSimpleName();
	Context context;
	PagerSlidingTabStrip tabs;
	ViewPager pager;
	DisplayMetrics dm;
	/** 歌曲界面 */
	AllMusicFragment mAllMusicFragment;
	/** 歌手界面 */
	AllSingerFragment mAllSingerFragment;
	/** 专辑界面 */
	AllAlbumFragment mAllAlbumFragment;
	/** 歌曲文件夹界面 */
	AllSongDirFragment mAllSongDirFragment;
	/** 收藏场fragment */
	CollectionFragment mCollectionFragment;
	
	/** 当前list Fragment */
	private CurrentListFragment mCurrentListFragment;
	
	/** 顶部预留view */
	View topBase;
	String[] titles = {"歌曲", "歌手", "专辑" , "文件夹" , "收藏"};
	View view;
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		L.e(tag, "onCreateView");
		context = inflater.getContext();
		if(view != null){
			L.e(tag, "view != null");
			ViewGroup parent = (ViewGroup) view.getParent();
			if(parent != null){
				parent.removeView(view);
			}
			return view;
		}
		view = inflater.inflate(R.layout.local_music_fragment, container, false);
		LocalFragmentManager.getInstance().init(context, this);
		initView(view);
		return view;
	}
	
	@SuppressLint("NewApi")
	private void initView(View view){ 
		dm = getResources().getDisplayMetrics();
		pager = (ViewPager) view.findViewById(R.id.pager);
		tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
		pager.setAdapter(new MyAdapter(((FragmentActivity)context).getSupportFragmentManager(), titles));
		tabs.setViewPager(pager);
		topBase = (View)view.findViewById(R.id.topBase);
		Utils.getInstance().updateViewHToStatusH(topBase);
	} 
	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		updataTitle();
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		L.e(tag, "onDestroy");
//	    mAllMusicFragment = null;
//		mAllSingerFragment = null;
//		mAllAlbumFragment = null;
//		mAllSongDirFragment = null;
//		view = null;
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		L.e(tag, "onDestroyView");
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
			SongManager mSongManager = SongManager.getInstance();
			switch(position){
			case 0:
				result = "歌曲/" + mSongManager.getAllSong().size();
				break;
			case 1:
				result = "歌手/" + mSongManager.getAllSinger().size();
				break;
			case 2:
				result = "专辑/" + mSongManager.getAllAlbum().size();
				break;
			case 3:
				result = "文件夹/" + mSongManager.getAllSongDir().size();
				break;
			case 4:
				result = "收藏/" + mSongManager.getCollectionSong().size();
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
				if (mAllMusicFragment == null) {
					L.e(tag, "new AllMusicFragment");
					mAllMusicFragment = new AllMusicFragment();
				}
				mFragment = mAllMusicFragment;
				break;
			case 1:
				if (mAllSingerFragment == null) {
					L.e(tag, "new AllSingerFragment");
					mAllSingerFragment = new AllSingerFragment();
				}
				mFragment = mAllSingerFragment;
				break;
			case 2:
				if (mAllAlbumFragment == null) {
					L.e(tag, "new AllAlbumFragment");
					mAllAlbumFragment = new AllAlbumFragment();
				} 
				mFragment = mAllAlbumFragment;
				break;
			case 3:
				if(mAllSongDirFragment == null){
					L.e(tag, "new mAllSongDirFragment");
					mAllSongDirFragment = new AllSongDirFragment();
				}
				mFragment = mAllSongDirFragment;
				break;
			case 4 :
				if(mCollectionFragment == null){
					L.e(tag, "new mCollectionFragment");
					mCollectionFragment = new CollectionFragment();
				}
				mFragment = mCollectionFragment;
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
		case PlayerCode.NOTIFY_ORIENTATION_LANDSCAPE:
			
			break;
		}
		
		return result;  
	}
	
}
//http://www.0531s.com/content-40-2251528-1.html