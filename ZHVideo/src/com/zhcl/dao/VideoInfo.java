/**
 * 
 */
package com.zhcl.dao;

import java.io.File;
import java.io.Serializable;

/** 
 * 视频类
 * @author zhonghong.chenli
 * @date 2015-12-3 下午9:27:15 
 */
public class VideoInfo implements Comparable<VideoInfo> , Serializable{
	/** ID */
	private long id;
	/** 文件路径 */
	private String fileName;
	/** 标题 */
	private String title = "";
	/** 总时间 */
	private int duration;
	/** 文件大小 */
	private String size;
	/** 拼音缩写 */
	private String pinyin;
	/** 对应表的type（主键的一部分） */
	private int type;
	/** 主键 */
	private long key = -1;
	
	public VideoInfo(long id, int type) {
		this.id = id;
		this.type = type;
	}

	public static long makeKey(long id, int type) {
		return id + (type << 60);
	}

	public long getKey() {
		if (this.key == -1L) {
			return makeKey(this.id, this.type);
		}
		return this.key;
	}
	
	public static int makeId(String path){
		return path.toLowerCase().hashCode();
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) { 
		this.id = id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public void setKey(long key) {
		this.key = key;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
		this.title = new File(fileName).getName();
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getPinyin() {
		return pinyin;
	}
	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}
	public String getImageCacheName(){
		return ("歌手图片" + this.getFileName()).hashCode() + ".0";
	}
	
	@Override
	public int compareTo(VideoInfo s) {
		int result = 0;
		result = pinyin.compareTo(s.getPinyin());
		return result;
	}
}
