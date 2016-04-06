/**
 * 
 */
package com.zh.dao;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.zh.uitls.Utils;

/**
 * @author chenli
 * 专辑
 */
public class Album implements Comparable<Album> , Serializable{
	private String mAlbumName;
	private String pinyin;
	private Set<SongInfo> mSongSet = new HashSet<SongInfo>();
	public String getmAlbumName() {
		return mAlbumName;
	}
	public void setmAlbumName(String mAlbumName) {
		this.mAlbumName = mAlbumName;
		if(mAlbumName != null && mAlbumName.length() > 20){
			mAlbumName = mAlbumName.substring(0, 20);
		}
		pinyin = Utils.getInstance().getPinYin(mAlbumName);
	}
	
	public void addSong(SongInfo mSong){
		mSongSet.add(mSong);
	}
	
	public void deleteSong(SongInfo mSong){
		mSongSet.remove(mSong);
	}
	public Set<SongInfo> getmSongSet() {
		return mSongSet;
	}
	public void setmSongSet(HashSet<SongInfo> mSongSet) {
		this.mSongSet = mSongSet;
	}
	
	public String getPinYin(){
		return pinyin;
	}
	/**
	 * 回收
	 */
	public void recycle(){
		mSongSet.clear();
	}
	/**
	 * 按个数倒序
	 * 再按字母排序
	 */
	@Override
	public int compareTo(Album o) {
		int result = 0;
		if(mSongSet.size() > o.getmSongSet().size()){
			result = -1;
		}else if(mSongSet.size() < o.getmSongSet().size()){
			result = 1;
		}else{
			result = pinyin.compareTo(o.getPinYin());
		}
		return result;
	}
	
	/**
	 * 图片名字生成
	 */
	public String getImageCacheName(){
		return ("专辑图片" + this.getmAlbumName()).hashCode() + ".0";
	}
}
