/**
 * 
 */
package com.zh.dao;

import android.annotation.SuppressLint;

import java.io.File;
import java.io.Serializable;


/**
 * @author chenli
 * 
 */
public class SongInfo implements Comparable<SongInfo> , Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String fileName;
	private String title;
	private int duration;
	private String singer;
	private String album;
	private String year;
	private String size;
	private String fileUrl;
	/** 拼音缩写 */
	private String pinyin;

	/** 对应表的id（主键的一部分）只在构造的时候分配,暂定为路径的hashcode */
	private long id;
	/** 对应表的type（主键的一部分） */
	private int type;
	/** 主键 */
	private long key = -1;

	public SongInfo(long id, int type) {
		this.id = id;
		setType(type);
	}

	public SongInfo(long id) {
		this.id = id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setKey(long key) {
		this.key = key;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getId() {
		return id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
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

	public String getSinger() {
		return singer;
	}

	public void setSinger(String singer) {
		this.singer = singer;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public SongInfo() {
		super();
	}
	
	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public SongInfo(String fileName, String title, int duration, String singer,
			String album, String year, String size, String fileUrl) {
		super();
		this.fileName = fileName;
		this.title = title;
		this.duration = duration;
		this.singer = singer;
		this.album = album;
		this.year = year;
		this.size = size;
		this.fileUrl = fileUrl;
	}

	@Override
	public String toString() {
		return "Song [fileName=" + fileName + ", id=" + id + ", type=" + type
				+ ", title=" + title + ", duration=" + duration + ", singer="
				+ singer + ", album=" + album + ", year=" + year + ", size="
				+ size + ", fileUrl=" + fileUrl + ", pinyin = " + pinyin + "]";
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

	@Override
	public int compareTo(SongInfo s) {
		int result = 0;
		result = pinyin.compareTo(s.getPinyin());
		return result;
	}
	
	
	@SuppressLint("DefaultLocale")
	@Override
	public int hashCode() {
		if(this.fileName == null){
			return super.hashCode();
		}
		return this.fileName.toLowerCase().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if(o == null || !(o instanceof SongInfo)){
			return false;
		}
		
		if(((SongInfo)o).getFileName().equals(this.fileName)){
			return true;
		}
		return false;
	}
	
	/**
	 * 本机是否存在
	 */
	public boolean exists(){
		return new File(this.fileName).exists();
	}

	/**
	 * 回收
	 */
	public void recycle(){
		
	}
	
	/**
	 * lrc名字生成
	 */
	public String getLrcCacheName(){
		return ("LRC" + this.getFileName()).hashCode() + ".0";
	}
	
	
	/**
	 * 图片名字生成,当歌曲被删除时需检查干掉缓存
	 */
	public String getImageCacheName(){
		return ("缓存图片" + this.getFileName()).hashCode() + ".0";
	}
}