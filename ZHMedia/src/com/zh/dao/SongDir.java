/**
 * 
 */
package com.zh.dao;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.zh.uitls.L;
import com.zh.uitls.Utils;

/**
 * @author chenli
 * 歌曲目录
 */
public class SongDir implements Comparable<SongDir> , Serializable{
	private String dirPath;
	private String pinyin;
	private String subName;
	private Set<SongInfo> mSongDirSet = new HashSet<SongInfo>();
	public String getdirPath() {
		return dirPath;
	}
	
	public void setDirPath(String dirPath) {
		this.dirPath = dirPath;
		pinyin = Utils.getInstance().getPinYin(dirPath);
		subName = Utils.getInstance().getFilePathLastSub(dirPath);
		L.e("setDirPath", "subName = " + subName);
	}
	
	public void addSong(SongInfo mSong){
		mSongDirSet.add(mSong);
	}
	
	public void deleteSong(SongInfo mSong){
		mSongDirSet.remove(mSong);
	}
	
	
	public Set<SongInfo> getSongDirSet() {
		return mSongDirSet;
	}
	
	public void setSongDirSet(HashSet<SongInfo> mSongDirSet) {
		this.mSongDirSet = mSongDirSet;
	}
	
	public String getPinYin(){
		return pinyin;
	}
	
	public String getSubName(){
		return subName;
	}
	
	/**
	 * 回收
	 */
	public void recycle(){
		mSongDirSet.clear();
	}
	
	/**
	 * 按个数倒序
	 * 再按字母排序
	 */
	@Override
	public int compareTo(SongDir o) {
		int result = 0;
		if(mSongDirSet.size() > o.getSongDirSet().size()){
			result = -1;
		}else if(mSongDirSet.size() < o.getSongDirSet().size()){
			result = 1;
		}else{
			result = pinyin.compareTo(o.getPinYin());
		}
		return result;
	}
}
