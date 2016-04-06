/**
 * 
 */
package com.zhcl.dao;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.zh.uitls.L;
import com.zh.uitls.Utils;

/**
 * @author chenli
 * 歌曲目录
 */
public class VideoDir implements Comparable<VideoDir> , Serializable{
	private String dirPath;
	private String pinyin;
	private String subName;
	private Set<VideoInfo> mVideoDirSet = new HashSet<VideoInfo>();
	public String getdirPath() {
		return dirPath;
	}
	
	public void setDirPath(String dirPath) {
		this.dirPath = dirPath;
		pinyin = Utils.getInstance().getPinYin(dirPath);
		subName = Utils.getInstance().getFilePathLastSub(dirPath);
		L.e("setDirPath", "subName = " + subName);
	}
	
	public void addVideo(VideoInfo mVideo){
		mVideoDirSet.add(mVideo);
	}
	
	public void deleteVideo(VideoInfo mVideo){
		mVideoDirSet.remove(mVideo);
	}
	
	
	public Set<VideoInfo> getVideoDirSet() {
		return mVideoDirSet;
	}
	
	public void setVideoDirSet(HashSet<VideoInfo> mVideoDirSet) {
		this.mVideoDirSet = mVideoDirSet;
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
		mVideoDirSet.clear();
	}
	
	/**
	 * 按个数倒序
	 * 再按字母排序
	 */
	@Override
	public int compareTo(VideoDir o) {
		int result = 0;
		if(mVideoDirSet.size() > o.getVideoDirSet().size()){
			result = -1;
		}else if(mVideoDirSet.size() < o.getVideoDirSet().size()){
			result = 1;
		}else{
			result = pinyin.compareTo(o.getPinYin());
		}
		return result;
	}
}
