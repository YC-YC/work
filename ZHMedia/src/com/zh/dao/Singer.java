package com.zh.dao;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.zh.uitls.Utils;
/**
 * 歌手封面需缩小并缓存至文件
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author zhonghong.chenli
 * @date 2015-11-24 下午11:08:52
 */
public class Singer implements Comparable<Singer> , Serializable{
	private String mSingerName;
	private String pinyin;
	private Set<SongInfo> mSongSet = new HashSet<SongInfo>();
	public String getmSingerName() {
		return mSingerName;
	}
	public void setmSingerName(String mSingerName) {
		this.mSingerName = mSingerName;
		pinyin = Utils.getInstance().getPinYin(mSingerName);
	}
	public Set<SongInfo> getmSongSet() {
		return mSongSet;
	}
	public void setmSongSet(HashSet<SongInfo> mSongSet) {
		this.mSongSet = mSongSet;
	}
	
	public void addSong(SongInfo mSong){
		mSongSet.add(mSong);
	}
	
	public void deleteSong(SongInfo mSong){
		mSongSet.remove(mSong);
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
	public int compareTo(Singer s) {
		int result = 0;
		if(mSongSet.size() > s.getmSongSet().size()){
			result = -1;
		}else if(mSongSet.size() < s.getmSongSet().size()){
			result = 1;
		}else{
			result = pinyin.compareTo(s.getPinYin());
		}
		return result;
	}
	
	/**
	 * 歌手图片名字生成
	 */
	public String getImageCacheName(){
		return ("歌手图片" + this.getmSingerName()).hashCode() + ".0";
	}
	
}
