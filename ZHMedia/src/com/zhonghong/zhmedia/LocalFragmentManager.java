/**
 * 
 */
package com.zhonghong.zhmedia;

import java.util.Collections;
import java.util.Set;
import java.util.Vector;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.zh.dao.Album;
import com.zh.dao.Singer;
import com.zh.dao.SongDir;
import com.zh.dao.SongInfo;
import com.zh.uitls.L;
import com.zhcl.media.SongManager;
import com.zhcl.ui.music.CurrentListFragment;
import com.zhcl.ui.music.LocalMusicFragment;
import com.zhcl.ui.music.LocalSearchFragment;
import com.zhcl.ui.music.OnlineMusicFragment;

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
	private LocalMusicFragment mLocalMusicFragment;
	/** 当前选中项列表 */
	private CurrentListFragment mCurrentListFragment;
	/** 本地搜索 */
	private LocalSearchFragment mLocalSearchFragment;
	String currentListTitle;
	Vector<SongInfo> currentListAllSong;
	Set<SongInfo> currentListAllSongSet;
	/** 歌手类型 */
	private final int TYPE_SINGER = 1;
	/** 专辑类型 */
	private final int TYPE_ALBUM = 2;
	/** 文件夹类型 */
	private final int TYPE_DIRTYPE = 3;
	/** 当前播放列表类型 */
	private int currentListType = 0;
	

	public void init(Context context, LocalMusicFragment mLocalMusicFragment) {
		if (context == null || mLocalMusicFragment == null) {
			throw new IllegalArgumentException("参数不可为空");
		}
		this.context = context;
		this.mLocalMusicFragment = mLocalMusicFragment;
	}

	private boolean isInit() {
		return this.context != null;
	}
	

	/**
	 * 显示搜索界面
	 */
	public boolean showSearchPage(){
		if (!isInit()) {
			L.w(tag, "LocalFragmentManager 未初始化，调用失败");
			return false;
		}
		if(mLocalSearchFragment == null){
			mLocalSearchFragment = new LocalSearchFragment();
		}
		if(!mLocalSearchFragment.isVisible()){
			FragmentManager mFragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
			FragmentTransaction ft = mFragmentManager.beginTransaction();
			ft.replace(R.id.currenBody, mLocalSearchFragment, mLocalSearchFragment.hashCode() + "");
			ft.addToBackStack(null);
			ft.commit();
		}
		
		return true;
	}
	
	
	/** 本地搜索 */
	private OnlineMusicFragment mOnlineMusicFragment;
	/**
	 * 显示搜索界面 测试
	 */
	public boolean showOnlinePage(){
		if (!isInit()) {
			L.w(tag, "LocalFragmentManager 未初始化，调用失败");
			return false;
		}
		if(mOnlineMusicFragment == null){
			mOnlineMusicFragment = new OnlineMusicFragment();
		}
		if(!mOnlineMusicFragment.isVisible()){
			FragmentManager mFragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
			FragmentTransaction ft = mFragmentManager.beginTransaction();
			ft.replace(R.id.currenBody, mOnlineMusicFragment, mOnlineMusicFragment.hashCode() + "");
			ft.addToBackStack(null);
			ft.commit();
		}
		return true;
	}
	
	/**
	 * showList
	 */
	private boolean showCurrentList() {
		if (!isInit()) {
			L.w(tag, "LocalFragmentManager 未初始化，调用失败");
			return false;
		}
		if(currentListAllSong == null){
			currentListAllSong = new Vector<SongInfo>();
		}
		if(mCurrentListFragment == null){
			mCurrentListFragment = new CurrentListFragment();
		}
		if(!mCurrentListFragment.isVisible()){
			FragmentManager mFragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
			FragmentTransaction ft = mFragmentManager.beginTransaction();
			ft.replace(R.id.currenBody, mCurrentListFragment, mCurrentListFragment.hashCode() + "");
			ft.addToBackStack(null);
			ft.commit();
		}
		return true;
	}
	
	/**
	 * 隐藏当前列表listFragment
	 * @return
	 */
	public boolean hideCurrentListFragment() {
		if(currentListAllSong != null){
			currentListAllSong.clear();
			currentListAllSong = null;
			currentListTitle = null;
		}
		FragmentManager mFragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		ft.remove(mCurrentListFragment);
		ft.commit();
		return true;
	}

	/**
	 * 显示当前歌手下所有歌曲
	 */
	public boolean showCurrentSingerList(Singer mSinger) {
		if (showCurrentList()) {
			currentListType = TYPE_SINGER;
			this.currentListTitle = mSinger.getmSingerName();
		}
		return true;
	}

	/**
	 * 显示当前专辑下所有歌曲
	 */
	public boolean showCurrentAlbumList(Album mAlbum) {
		if (showCurrentList()) {
			currentListType = TYPE_ALBUM;
			this.currentListTitle = mAlbum.getmAlbumName();
		}
		return true;
	}

	/**
	 * 显示当前文件夹下所有歌曲
	 */
	public boolean showCurrentDirList(SongDir mSongDir) {
		if (showCurrentList()) {
			currentListType = TYPE_DIRTYPE;
			this.currentListTitle = mSongDir.getSubName();
		}
		return true;
	}
	
	/**
	 * 根据类型和标题查找对应列表
	 * @param type
	 * @param title
	 */
	private void findAllSongFromTypeAndTitle(int type, String title){
		switch(type){
		case TYPE_SINGER:
			Vector<Singer> singerList = SongManager.getInstance().getAllSinger();
			for(Singer mSinger : singerList){
				if(mSinger.getmSingerName().equals(title)){
					currentListAllSongSet = mSinger.getmSongSet();
				}
			}
			break;
		case TYPE_ALBUM:
			Vector<Album> albumList = SongManager.getInstance().getAllAlbum();
			for(Album mAlbum : albumList){
				if(mAlbum.getmAlbumName().equals(title)){
					currentListAllSongSet = mAlbum.getmSongSet();
				}
			}
			break;
		case TYPE_DIRTYPE:
			Vector<SongDir> songDirList = SongManager.getInstance().getAllSongDir();
			synchronized (songDirList) {
				for(SongDir mSongDir : songDirList){
					if(mSongDir.getSubName().equals(title)){
						currentListAllSongSet = mSongDir.getSongDirSet();
					}
				}
			}
			break;
		}
	}
	
	/**
	 * 后台加载数据
	 */
	public void loadCurrentList(){
		if(currentListAllSong == null){
			currentListAllSong = new Vector<SongInfo>();
		}
		findAllSongFromTypeAndTitle(currentListType, currentListTitle);
		currentListAllSong.clear();
		currentListAllSong.addAll(currentListAllSongSet);
		Collections.sort(currentListAllSong);
	}
	
	/**
	 * 获得当前列表所有歌曲
	 * @return
	 */
	public Vector<SongInfo> getCurrentListAllSong(){
		return currentListAllSong;
	}
	 
	/**
	 * 获得当前列表 
	 * @return
	 */
	public String getCurrentListTitle(){
		return currentListTitle; 
	}
	
}
