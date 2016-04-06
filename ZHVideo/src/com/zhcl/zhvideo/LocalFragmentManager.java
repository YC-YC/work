/**
 * 
 */
package com.zhcl.zhvideo;

import java.util.Collections;
import java.util.Set;
import java.util.Vector;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.zh.uitls.L;
import com.zhcl.dao.VideoDir;
import com.zhcl.dao.VideoInfo;
import com.zhcl.ui.video.CurrentListFragment;
import com.zhcl.ui.video.LocalVideoFragment;
import com.zhcl.video.VideoManager;
import com.zhonghong.zhvideo.R;

/**
 * 做一个代理类，不让其他fragment直接操作父ragment
 * 
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author zhonghong.chenli
 * @date 2015-11-21 下午4:42:35
 */
public class LocalFragmentManager {
	private static final String tag = "LocalFragmentManager";

	private LocalFragmentManager() {
	}

	private static LocalFragmentManager mLocalFragmentManager;

	public static LocalFragmentManager getInstance() {
		if (mLocalFragmentManager == null) {
			mLocalFragmentManager = new LocalFragmentManager();
		}
		return mLocalFragmentManager;
	}

	private Context context;
	private LocalVideoFragment mLocalVideoFragment;
	/** 当前选中项列表 */
	private CurrentListFragment mCurrentListFragment;
	String currentListTitle;
	Vector<VideoInfo> currentListAllVideo;
	Set<VideoInfo> currentListAllVideoSet;
	/** 歌手类型 */
	private final int TYPE_SINGER = 1;
	/** 专辑类型 */
	private final int TYPE_ALBUM = 2;
	/** 文件夹类型 */
	private final int TYPE_DIRTYPE = 3;
	/** 当前播放列表类型 */
	private int currentListType = 0;
	

	public void init(Context context, LocalVideoFragment mLocalVideoFragment) {
		if (context == null || mLocalVideoFragment == null) {
			throw new IllegalArgumentException("参数不可为空");
		}
		this.context = context;
		this.mLocalVideoFragment = mLocalVideoFragment;
	}

	private boolean isInit() {
		return this.context != null;
	}
	

	
	
	/**
	 * showList
	 */
	private boolean showCurrentList() {
		if (!isInit()) {
			L.w(tag, "LocalFragmentManager 未初始化，调用失败");
			return false;
		}
		if(currentListAllVideo == null){
			currentListAllVideo = new Vector<VideoInfo>();
		}
		if(mCurrentListFragment == null){
			mCurrentListFragment = new CurrentListFragment();
		}
		FragmentManager mFragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		ft.replace(R.id.currenBody, mCurrentListFragment, mCurrentListFragment.hashCode() + "");
		ft.addToBackStack(null);
		ft.commit();
		return true;
	}
	
	/**
	 * 隐藏当前列表listFragment
	 * @return
	 */
	public boolean hideCurrentListFragment() {
		if(currentListAllVideo != null){
			currentListAllVideo.clear();
			currentListAllVideo = null;
			currentListTitle = null;
		}
		FragmentManager mFragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		ft.remove(mCurrentListFragment);
		ft.commit();
		return true;
	}


	/**
	 * 显示当前文件夹下所有歌曲
	 */
	public boolean showCurrentDirList(VideoDir mVideoDir) {
		if (showCurrentList()) {
			currentListType = TYPE_DIRTYPE;
			this.currentListTitle = mVideoDir.getSubName();
		}
		return true;
	}
	
	/**
	 * 根据类型和标题查找对应列表
	 * @param type
	 * @param title
	 */
	private void findAllVideoFromTypeAndTitle(int type, String title){
		switch(type){
		case TYPE_DIRTYPE:
			Vector<VideoDir> videoDirList = VideoManager.getIntance(null).getAllVideoDir();
			for(VideoDir mVideoDir : videoDirList){
				if(mVideoDir.getSubName().equals(title)){
					currentListAllVideoSet = mVideoDir.getVideoDirSet();
				}
			}
			break;
		}
	}
	
	/**
	 * 后台加载数据
	 */
	public void loadCurrentList(){
		findAllVideoFromTypeAndTitle(currentListType, currentListTitle);
		currentListAllVideo.clear();
		currentListAllVideo.addAll(currentListAllVideoSet);
		Collections.sort(currentListAllVideo);
	}
	
	/**
	 * 获得当前列表所有歌曲
	 * @return
	 */
	public Vector<VideoInfo> getCurrentListAllVideo(){
		return currentListAllVideo;
	}
	 
	/**
	 * 获得当前列表 
	 * @return
	 */
	public String getCurrentListTitle(){
		return currentListTitle; 
	}
	
}
