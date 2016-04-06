/**
 * 
 */
package com.zhcl.video;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import android.content.Context;
import android.database.Cursor;
import android.os.Looper;
import android.util.Log;

import com.zh.uitls.L;
import com.zh.uitls.Utils;
import com.zhcl.dao.VideoDir;
import com.zhcl.dao.VideoInfo;
import com.zhcl.filescanner.LocalFileCacheManager;

/**
 * @author chenli
 *
 */
public class VideoManager {
	private static final String tag = "VideoManager";
	private static VideoManager mVideoManager;
	private static Context context;
	private VideoDBHelp mVideoDBHelp;
	/** 歌曲集合 */
	private Vector<VideoInfo> videoList;
	/** 文件夹集合 */
	private Vector<VideoDir> videoDirList;
	/** 歌曲字母表 */
	private HashMap<String, Integer> videoABCIndex;
	/** 新加的歌曲数 */
	private int newAddVideoNum;
	/** 取掉的歌曲数 */
	private int newDelVideoNum;
	private VideoManager(Context context){
		this.context = context;
		mVideoDBHelp = new VideoDBHelp(context, "/mnt/sdcard/recogniz.db");
		init();
	}
	
	private void init(){
		if(videoList == null){
			videoList = new Vector<VideoInfo>();
		}
		if(videoDirList == null){
			videoDirList = new Vector<VideoDir>();
		}
		if(videoABCIndex == null){
			videoABCIndex = new HashMap<String, Integer>();
		}
	}
	
	public static VideoManager getIntance(Context context){
		if(mVideoManager == null){
			mVideoManager = new VideoManager(context);
		}
		return mVideoManager;
	}
	
	
	
	/**
	 * 更新所有视频信息
	 * @return
	 */
	public boolean updataAllVideoInfo(){
		//所有歌曲，最后需被删除的
		ArrayList<Integer> needDelVideo = new ArrayList<Integer>();
		//所有文件，最后需被添加的
		ArrayList<String> needAddFile = new ArrayList<String>();
		//最后需要添加新数据库的videoinfo
		ArrayList<VideoInfo> needAddVideoInfo = new ArrayList<VideoInfo>();
		//搜所有文件
		Cursor fileCursor = LocalFileCacheManager.getInstance().queryAllFiles();
		//搜所有video
		Cursor allVideoCursor = queryAllVideo();
		this.index = 0;
		this.total = LocalFileCacheManager.getInstance().queryAllFileCount();
//		Cursor systemCursor = this.mVideoDBHelp.querySystemMedia();
		//
		if(allVideoCursor != null){				
			while(allVideoCursor.moveToNext()){				//获得搜有video信息
				needDelVideo.add(allVideoCursor.getInt(0));
			}
			allVideoCursor.moveToFirst();
			allVideoCursor.close();
		}
		//剔除存在的list
		if (fileCursor != null) {
			VideoInfo videoInfo = null;
			Utils.getInstance().startTime("剔除videotable中存在的信息");
			while (fileCursor.moveToNext()) {
				String filePath = fileCursor.getString(0); 
				int id = VideoInfo.makeId(filePath);
				
				boolean isRemove =  isRemove(filePath);
				
				if(needDelVideo.contains((Integer)id) && !isRemove){
//					Log.e(tag, "！！！！存在：" + filePath);
					updateProgress(filePath, ++this.index);		//同步进度
					needDelVideo.remove((Integer)id);
					continue;
				}
				if(!isRemove){
					needAddFile.add(filePath);
				}
			}
			fileCursor.close();
			Utils.getInstance().endUseTime("剔除videotable中存在的信息");
			Log.w(tag, "需添加id3信息的歌曲数：" + needAddFile.size());
		} else {
			L.w(tag, "文件夹查询异常！！updataAllVideoInfo 结果为空");
		}
		int systemCount = 0;
//		//解析id3,先查找系统数据库
//		if(systemCursor != null){
//			while(systemCursor.moveToNext()){
//				String filePath = systemCursor.getString(9);
//				if(needAddFile.contains(filePath)){				//如果系统中存在此文件的信息
//					needAddFile.remove(filePath);		//删除需要添加的
//					//创建videoInfo
//					VideoInfo videoInfo = VideoInfoFactory.CreateVideoInfo(VideoInfo.makeId(filePath), 6);
//					videoInfo.setFileName(filePath);
//					videoInfo.setPinyin(Utils.getInstance().getPinYin(videoInfo.getTitle()));
//					needAddVideoInfo.add(videoInfo);
//					systemCount++; 
//				}
//			}   
//		}
		Log.w(tag, "系统数据库中搜索到个数为：" + systemCount);
		int formatCount = 0;
		//如果数据库中没有则自行解析,比较耗时
		for(String filePath : needAddFile){
			updateProgress(filePath, ++this.index);		//同步进度
			VideoInfo videoInfo = VideoInfoFactory.CreateVideoInfo(filePath);
			needAddVideoInfo.add(videoInfo);
			formatCount++;
		}
		Log.w(tag, "自行解析的个数：" + formatCount);
		//删除该删除的
		mVideoDBHelp.deleteVideoInfoFromList(needDelVideo, 0);
		//添加改添加的
		mVideoDBHelp.insertVideoTable(needAddVideoInfo);
		L.w(tag, "删除歌曲数目：" + needDelVideo.size());
		L.w(tag, "新加歌曲数目：" + needAddVideoInfo.size());
		newDelVideoNum = needDelVideo.size();
		newAddVideoNum = needAddVideoInfo.size();
		return true;
	}
	
	/**
	 * 获取新增歌曲数
	 * @return
	 */
	public int getNewAddVideoNum() {
		return newAddVideoNum;
	}

	/**
	 * 获取删除歌曲数
	 * @return
	 */
	public int getNewDelVideoNum() {
		return newDelVideoNum;
	}

	/**
	 * 查询所有歌曲个数
	 */
	public long queryAllVideoNum(){
		return mVideoDBHelp.queryAllVideoNum();
	}
	
	/**
	 * 查询所有歌手个数
	 */
	public long queryAllVideoerNum(){
		return mVideoDBHelp.queryAllVideoerNum();
	}
	
	/**
	 * 查询所有专辑个数
	 */
	public long queryAllAlbumrNum(){
		return mVideoDBHelp.queryAllAlbumrNum();
	}
	
	
	/**
	 * 获取所有歌曲的cursor对象
	 * @return
	 */
	public Cursor queryAllVideo(){
		return mVideoDBHelp.queryAllVideo();
	}
	
	/**
	 * 查询所有歌手
	 */
	public Cursor queryAllSinger(){
		return mVideoDBHelp.queryAllSinger();
	}
	
	/**
	 * 查询所有专辑信息
	 */
	public Cursor queryAllAlbumr(){
		return mVideoDBHelp.queryAllAlbumr();
	}
	

	/**
	 * 查询专辑下所有歌曲
	 */
	public Cursor queryVideoFromAlbumer(String albumer){
		return this.mVideoDBHelp.queryVideoFromAlbumer(albumer);
	}
	
	/**
	 * 查询某歌手下的歌曲
	 * 可能有重复的歌曲，但是文件不重复
	 */
	public Cursor queryVideoFromSinger(String singer){
		return this.mVideoDBHelp.queryVideoFromSinger(singer);
	}
	
	
	/**
	 * 获取歌手歌的数目
	 * @param singer 歌手名
	 * @return
	 */
	public long queryVideoNumFromSinger(String singer){
		return this.mVideoDBHelp.queryVideoNumFromSinger(singer);
	}
	
	/**
	 * 获取专辑下歌曲数目
	 * @param albumr
	 * @return
	 */
	public long queryAlbumrNumFromSinger(String albumr){
		return this.mVideoDBHelp.queryAlbumrNumFromSinger(albumr);
	}
	
	/**
	 * 取所有歌曲信息以及字母排序map
	 * 只允许在子线程中加载
	 */
	public int loadAllVideoInfo(){
		if(Thread.currentThread().getId() == Looper.getMainLooper().getThread().getId()){	//如果
			throw new IllegalArgumentException("请在子线程中加载");
		}
		synchronized (videoList) {
			synchronized (videoDirList) {
				resetSingerAndAlbum();	 
				Cursor allVideoCursor = VideoManager.getIntance(context).queryAllVideo();
				int index = 0;
				/** 文件夹集合 */
				HashMap<String, VideoDir> videoDirMap = new HashMap<String, VideoDir>();
				if(allVideoCursor != null){
					int idIndex = allVideoCursor.getColumnIndex("id");
					int typeIndex = allVideoCursor.getColumnIndex("type");
					int fidIndex  = allVideoCursor.getColumnIndex("fid");
					int pinyinIndex = allVideoCursor.getColumnIndex("pinyin");
					int pathIndex = allVideoCursor.getColumnIndex("path");
					int nameIndex = allVideoCursor.getColumnIndex("name");
					int durationIndex = allVideoCursor.getColumnIndex("duration");
					while(allVideoCursor.moveToNext()){
		//				(id long not null, type integer not null, fid long not null, path text not null, pinyin text not null, name text not null, singername text, albumname text, PRIMARY KEY (id,type))
						long id = allVideoCursor.getLong(idIndex);
						int type = allVideoCursor.getInt(typeIndex);
						long fid = allVideoCursor.getLong(fidIndex);
						String pinyin = allVideoCursor.getString(pinyinIndex);
						String filePath = allVideoCursor.getString(pathIndex);
						String title = allVideoCursor.getString(nameIndex);
						int duration = allVideoCursor.getInt(durationIndex);
						
						if(!new File(filePath).exists() || isRemove(filePath)){
							continue;
						}
						
						VideoInfo videoInfo = VideoInfoFactory.CreateVideoInfo(id, type);
						videoInfo.setTitle(title);
						videoInfo.setFileName(filePath);
						videoInfo.setPinyin(pinyin);
						videoInfo.setDuration(duration);
						videoList.add(videoInfo);
						String firstABC = pinyin.substring(0, 1);
		//				L.e(tag, "title = " + title + " pinyin =" + pinyin);
						if(videoABCIndex.get(firstABC) == null){
							videoABCIndex.put(firstABC, index);
							L.i(tag, "abc = " + firstABC + " index = " + index);
						} 
						addVideoDirInfo(videoDirMap, filePath, videoInfo);
						index++;
					}   
					videoDirList.addAll(videoDirMap.values());
					Collections.sort(videoDirList);
					allVideoCursor.close();
				}
			}
		}  
		return 1;
	}
	
	
	/**
	 * 强迫症清除
	 */
	private void recycleAll(){
		L.i(tag, "videoDirList 个数：" + videoDirList.size());
		for(VideoDir mVideoDir : videoDirList){
			mVideoDir.recycle();
		}
	}
	
	/**
	 * 初始化歌手信息以及专辑信息
	 */
	private void resetSingerAndAlbum(){
		Utils.getInstance().startTime("recycleAll");
		recycleAll();
		Utils.getInstance().endUseTime("recycleAll");
		videoList.clear();
		videoDirList.clear();
		videoABCIndex.clear();
	}
	
	
	/**
	 * 歌曲文件夹数据收集
	 */
	private void addVideoDirInfo(HashMap<String, VideoDir> videoDirMap, String filePath, VideoInfo videoInfo) {
		filePath = Utils.getInstance().getDirPathFromfile(filePath);
		VideoDir mVideoDir = videoDirMap.get(filePath);
		if(mVideoDir == null){
			mVideoDir = new VideoDir();
			videoDirMap.put(filePath, mVideoDir);
			mVideoDir.setDirPath(filePath);
		}
		mVideoDir.addVideo(videoInfo);
	}
	
	/**
	 * 获取歌曲info
	 * @return
	 */
	public Vector<VideoInfo> getAllVideo() {
		return videoList;
	}

	/**
	 * 获取文件夹info
	 */
	public Vector<VideoDir> getAllVideoDir(){
		return videoDirList;
	}
	
	/**
	 * 获取歌曲字母对照表
	 * @return
	 */
	public HashMap<String, Integer> getAllVideoABCIndex(){
		return videoABCIndex;
	}
	
	
	/**
	 * 是否已经移除
	 */
	private boolean isRemove(String filePath){
		int filePathLength = filePath.length();
		for(String delDir : removePath){
			int dirLength = delDir.length();
			if(filePathLength < dirLength){
				return false;
			}
			if(delDir.equals(filePath.substring(0, dirLength))){
				return true;
			}
		}
		return false;
	}
	
	HashSet<String> removePath = new HashSet<String>();
	public void addRemovePath(String path){
		L.e(tag, "添加移除的路径path:" + path);
		synchronized (removePath) {
			removePath.add(path);
		}
	}
	
	public void delRemovePath(String path){
		synchronized (removePath) {
			removePath.remove(path);
		}
	}
	
	private String progressPath = "";
	private int index;
	private long total;
	private void updateProgress(String path, int currentIndex){
		progressPath = path;
		index = currentIndex;
//		L.e(tag, "total + " + total + " index = " + index);
	}
	/**
	 * 获取当前处理的歌曲（ID3）
	 */
	public String getCurrentSongPath(){
		return progressPath;
	} 
	
	/**
	 * 获得百分比
	 * @return
	 */
	public int getCurrentPercent(){
		if (this.total > 0){
			return (int) (100 * this.index / this.total);
		}
		return 0;
	}
}	
