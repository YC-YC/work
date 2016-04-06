/**
 * 
 */
package com.zhcl.media;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import android.content.Context;
import android.database.Cursor;
import android.os.Looper;
import android.util.Log;

import com.zh.dao.Album;
import com.zh.dao.Singer;
import com.zh.dao.SongDir;
import com.zh.dao.SongInfo;
import com.zh.uitls.L;
import com.zh.uitls.Utils;
import com.zhcl.filescanner.LocalFileCacheManager;

/**
 * @author chenli
 *
 */
public class SongManager {
	private static final String tag = "SongManager";
	private static SongManager mSongManager;
	private static Context context;
	private SongDBHelp mSongDBHelp;
	/** 歌曲集合 */
	private Vector<SongInfo> songList;
	/** 歌手集合 */
	private Vector<Singer> singerList;
	/** 专辑集合 */
	private Vector<Album> albumList;
	/** 文件夹集合 */
	private Vector<SongDir> songDirList;
	/** 歌曲字母表 */
	private HashMap<String, Integer> songABCIndex;
	/** 收藏列表集合 */
	private Vector<SongInfo> collectionList;
	/** 收藏中存在的列表 */
	private Vector<SongInfo> collectionExistsList;
	/** 收藏中不存在的列表 */
	private Vector<SongInfo> collectionNotExistsList;
	/** 新加的歌曲数 */
	private int newAddSongNum;
	/** 取掉的歌曲数 */
	private int newDelSongNum;
	private SongManager(){}
	/** 音乐是否加载完成 */
	private boolean isLoadok;
	
	public void init(Context context, String path){
		this.context = context;
		mSongDBHelp = new SongDBHelp(context, path);
		
		if(songList == null){
			songList = new Vector<SongInfo>();
		}
		if(albumList == null){
			albumList = new Vector<Album>();
		}
		if(singerList == null){
			singerList = new Vector<Singer>();
		}
		if(songDirList == null){
			songDirList = new Vector<SongDir>();
		}
		if(songABCIndex == null){
			songABCIndex = new HashMap<String, Integer>();
		}
		if(collectionList == null){
			collectionList = new Vector<SongInfo>();
		}
		
		if(collectionExistsList == null){
			collectionExistsList = new Vector<SongInfo>();
		}
		
		if(collectionNotExistsList == null){
			collectionNotExistsList = new Vector<SongInfo>();
		}
	}
	
	public static SongManager getInstance(){
		if(mSongManager == null){
			mSongManager = new SongManager();
		}
		return mSongManager;
	}
	
	
	/**
	 * 取指定模块中的歌曲
	 */
	public boolean queryAllSongFromModule(String modulePath){
		Cursor allSongCursor = this.mSongDBHelp.queryAllSongFromModule(modulePath);
		if(allSongCursor != null){
			int idIndex = allSongCursor.getColumnIndex("id");
			int typeIndex = allSongCursor.getColumnIndex("type");
			int fidIndex  = allSongCursor.getColumnIndex("fid");
			int pinyinIndex = allSongCursor.getColumnIndex("pinyin");
			int pathIndex = allSongCursor.getColumnIndex("path");
			int nameIndex = allSongCursor.getColumnIndex("name");
			int singernameIndex = allSongCursor.getColumnIndex("singername");
			int albumnameIndex = allSongCursor.getColumnIndex("albumname");
			while(allSongCursor.moveToNext()){
				long id = allSongCursor.getLong(idIndex);
				int type = allSongCursor.getInt(typeIndex);
				long fid = allSongCursor.getLong(fidIndex);
				String pinyin = allSongCursor.getString(pinyinIndex);
				String filePath = allSongCursor.getString(pathIndex);
				String title = allSongCursor.getString(nameIndex);
				String aristr = allSongCursor.getString(singernameIndex);
				String album = allSongCursor.getString(albumnameIndex);
				L.e(tag, "指定模块的音乐：" + title + " path : " + filePath);
			}
			allSongCursor.close();
		}else{
			L.e(tag, "queryAllSongFromModule == null");
		}
		
		return true;
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
	
	/**
	 * 是否已经移除
	 */
	public boolean isRemove(String filePath){
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
	
	
	/**
	 * 更新所有歌曲信息，后面需要修改，将id3作为songInfo的成员
	 * @return
	 */
	public boolean updataAllSongInfo(){
		//所有歌曲，最后需被删除的
		ArrayList<Integer> needDelSong = new ArrayList<Integer>();
		//所有文件，最后需被添加的
		ArrayList<String> needAddFile = new ArrayList<String>();
		//最后需要添加新数据库的songinfo
		ArrayList<SongInfo> needAddSongInfo = new ArrayList<SongInfo>();
		//搜所有文件
		Cursor fileCursor = LocalFileCacheManager.getInstance().queryAllFiles();
		//搜所有song
		Cursor allSongCursor = queryAllSong();
		Cursor systemCursor = this.mSongDBHelp.querySystemMedia();
		this.index = 0;
		this.total = LocalFileCacheManager.getInstance().queryAllFileCount();
		//
		int songCount = 0;  
		if(allSongCursor != null){				
			while(allSongCursor.moveToNext()){				//获得搜有song信息
				songCount++;
				needDelSong.add(allSongCursor.getInt(0));
			}
			L.e(tag, "数据库中存的歌曲数：" + songCount);
//			allSongCursor.moveToFirst(); 
			allSongCursor.close();
		}
		songCount = 0;
		//剔除存在的list
		if (fileCursor != null) {
			SongInfo songInfo = null;
			Utils.getInstance().startTime("剔除songtable中存在的信息");
			while (fileCursor.moveToNext()) {
				songCount++;
				String filePath = fileCursor.getString(0); 
				int id = SongInfo.makeId(filePath);
//				if(needDelSong.contains((Integer)id)){
				boolean isRemove =  isRemove(filePath);
				
				if(needDelSong.contains((Integer)id) && !isRemove){
					updateProgress(filePath, ++this.index);		//同步进度
					needDelSong.remove((Integer)id);
					continue;
				}
				if(!isRemove){
					needAddFile.add(filePath);
				}
				
			}
			L.e(tag, "数据库中存的文件数：" + songCount); 
			fileCursor.close();
			Utils.getInstance().endUseTime("剔除songtable中存在的信息");
			Log.w(tag, "需添加id3信息的歌曲数：" + needAddFile.size());
		} else {
			L.w(tag, "文件夹查询异常！！updataAllSongInfo 结果为空");
		}
		int systemCount = 0;
//		//解析id3,先查找系统数据库
//		if(systemCursor != null){
//			while(systemCursor.moveToNext()){
//				String filePath = systemCursor.getString(9);
//				if(needAddFile.contains(filePath)){				//如果系统中存在此文件的信息
//					needAddFile.remove(filePath);		//删除需要添加的
//					//创建songInfo
//					SongInfo songInfo = SongInfoFactory.CreateSongInfo(SongInfo.makeId(filePath), 6);
//					ID3 id3 = new ID3();
//					id3.setTitle(systemCursor.getString(2));
//					id3.setArtist(systemCursor.getString(4));
//					id3.setAlbum(systemCursor.getString(5));
//					songInfo.setAlbum(id3.getAlbum());
//					songInfo.setTitle(id3.getTitle());
//					songInfo.setSinger(id3.getArtist());
//					songInfo.setFileName(filePath);
//					songInfo.setPinyin(Utils.getInstance().getPinYin(songInfo.getTitle()));
//					needAddSongInfo.add(songInfo);
//					systemCount++; 
//					updateProgress(filePath, ++this.index);		//同步进度
//				}
//			}   
//		}
		systemCursor.close();
		Log.w(tag, "系统数据库中搜索到个数为：" + systemCount);
		int formatCount = 0;
		//如果数据库中没有则自行解析,比较耗时
		for(String filePath : needAddFile){
			updateProgress(filePath, ++this.index);		//同步进度
			SongInfo songInfo = SongInfoFactory.CreateSongInfo(filePath);
			needAddSongInfo.add(songInfo);
			formatCount++;
		}
		Log.w(tag, "自行解析的个数：" + formatCount);
		//删除该删除的
		mSongDBHelp.deleteSongInfoFromList(needDelSong, 0);
		//添加改添加的
		mSongDBHelp.insertSongTable(needAddSongInfo);
		L.w(tag, "删除歌曲数目：" + needDelSong.size());
		L.w(tag, "新加歌曲数目：" + needAddSongInfo.size());
		newDelSongNum = needDelSong.size();
		newAddSongNum = needAddSongInfo.size();
		return true;
	}
	
	/**
	 * 获取新增歌曲数
	 * @return
	 */
	public int getNewAddSongNum() {
		return newAddSongNum;
	}

	/**
	 * 获取删除歌曲数
	 * @return
	 */
	public int getNewDelSongNum() {
		return newDelSongNum;
	}

	/**
	 * 查询所有歌曲个数
	 */
	public long queryAllSongNum(){
		return mSongDBHelp.queryAllSongNum();
	}
	
	/**
	 * 查询所有歌手个数
	 */
	public long queryAllSongerNum(){
		return mSongDBHelp.queryAllSongerNum();
	}
	
	/**
	 * 查询所有专辑个数
	 */
	public long queryAllAlbumrNum(){
		return mSongDBHelp.queryAllAlbumrNum();
	}
	
	
	/**
	 * 获取所有歌曲的cursor对象
	 * @return
	 */
	public Cursor queryAllSong(){
		return mSongDBHelp.queryAllSong();
	}
	
	/**
	 * 查询所有歌手
	 */
	public Cursor queryAllSinger(){
		return mSongDBHelp.queryAllSinger();
	}
	
	/**
	 * 查询所有专辑信息
	 */
	public Cursor queryAllAlbumr(){
		return mSongDBHelp.queryAllAlbumr();
	}
	

	/**
	 * 查询专辑下所有歌曲
	 */
	public Cursor querySongFromAlbumer(String albumer){
		return this.mSongDBHelp.querySongFromAlbumer(albumer);
	}
	
	/**
	 * 查询某歌手下的歌曲
	 * 可能有重复的歌曲，但是文件不重复
	 */
	public Cursor querySongFromSinger(String singer){
		return this.mSongDBHelp.querySongFromSinger(singer);
	}
	
	
	/**
	 * 获取歌手歌的数目
	 * @param singer 歌手名
	 * @return
	 */
	public long querySongNumFromSinger(String singer){
		return this.mSongDBHelp.querySongNumFromSinger(singer);
	}
	
	/**
	 * 获取专辑下歌曲数目
	 * @param albumr
	 * @return
	 */
	public long queryAlbumrNumFromSinger(String albumr){
		return this.mSongDBHelp.queryAlbumrNumFromSinger(albumr);
	}
	
	
	
	/**
	 * 取所有歌曲信息以及字母排序map
	 * 只允许在子线程中加载
	 */
	public int loadAllMusicInfo(){
		if(Thread.currentThread().getId() == Looper.getMainLooper().getThread().getId()){	//如果
			throw new IllegalArgumentException("请在子线程中加载");
		}
		synchronized (songList) {
			synchronized (albumList) {
				synchronized (singerList) {
					synchronized (songDirList) {
						setLoadok(false);
						resetSingerAndAlbum();	 
						Cursor allSongCursor = SongManager.getInstance().queryAllSong();
						int index = 0;
						/** 歌手集合 */
						HashMap<String, Singer> singerMap = new HashMap<String, Singer>();
						/** 专辑集合 */
						HashMap<String, Album> albumMap = new HashMap<String, Album>();
						/** 文件夹集合 */
						HashMap<String, SongDir> songDirMap = new HashMap<String, SongDir>();
						if(allSongCursor != null){
							int idIndex = allSongCursor.getColumnIndex("id");
							int typeIndex = allSongCursor.getColumnIndex("type");
							int fidIndex  = allSongCursor.getColumnIndex("fid");
							int pinyinIndex = allSongCursor.getColumnIndex("pinyin");
							int pathIndex = allSongCursor.getColumnIndex("path");
							int nameIndex = allSongCursor.getColumnIndex("name");
							int singernameIndex = allSongCursor.getColumnIndex("singername");
							int albumnameIndex = allSongCursor.getColumnIndex("albumname");
							while(allSongCursor.moveToNext()){
				//				(id long not null, type integer not null, fid long not null, path text not null, pinyin text not null, name text not null, singername text, albumname text, PRIMARY KEY (id,type))
								long id = allSongCursor.getLong(idIndex);
								int type = allSongCursor.getInt(typeIndex);
								long fid = allSongCursor.getLong(fidIndex);
								String pinyin = allSongCursor.getString(pinyinIndex);
								String filePath = allSongCursor.getString(pathIndex);
								String title = allSongCursor.getString(nameIndex);
								String aristr = allSongCursor.getString(singernameIndex);
								String album = allSongCursor.getString(albumnameIndex);
								SongInfo songInfo = SongInfoFactory.CreateSongInfo(id, type);
								songInfo.setAlbum(album);
								songInfo.setTitle(title);
								songInfo.setSinger(aristr);
								songInfo.setFileName(filePath);
								songInfo.setPinyin(pinyin);
								songList.add(songInfo);
								String firstABC = pinyin.substring(0, 1);
				//				L.e(tag, "title = " + title + " pinyin =" + pinyin);
								if(songABCIndex.get(firstABC) == null){
									songABCIndex.put(firstABC, index);
//									L.i(tag, "abc = " + firstABC + " index = " + index);
								} 
								addSingerInfo(singerMap, aristr, songInfo);
								addAlbumInfo(albumMap, album, songInfo);
								addSongDirInfo(songDirMap, filePath, songInfo);
								index++;
							}   
							singerList.addAll(singerMap.values());
							albumList.addAll(albumMap.values());
							songDirList.addAll(songDirMap.values());
							Collections.sort(singerList);
							Collections.sort(albumList);
							Collections.sort(songDirList);
							allSongCursor.close();
						}
					}
				}
			}
		}  
		setLoadok(true);
		loadAllMusicInfoFromCollection();	//搜收藏列表
		return 1;
	}
	
	
	/**
	 * 强迫症清除
	 */
	private void recycleAll(){
		L.i(tag, "albumList 个数：" + albumList.size());
		L.i(tag, "singerList 个数：" + singerList.size());
		L.i(tag, "songDirList 个数：" + songDirList.size());
		for(Album mAlbum : albumList){
			mAlbum.recycle();
		}
		
		for(Singer mSinger : singerList){
			mSinger.recycle();
		}
		
		for(SongDir mSongDir : songDirList){
			mSongDir.recycle();
		}
	}
	
	/**
	 * 初始化歌手信息以及专辑信息
	 */
	private void resetSingerAndAlbum(){
		Utils.getInstance().startTime("recycleAll");
		recycleAll();
		Utils.getInstance().endUseTime("recycleAll");
		songList.clear();
		albumList.clear();
		singerList.clear();
		songDirList.clear();
		songABCIndex.clear();
		
	}
	
	/**
	 * 初始化收藏列表信息
	 */
	private void resetCollection(){
		collectionList.clear();
		collectionExistsList.clear();
		collectionNotExistsList.clear();
	}
	
	/**
	 * 歌手数据收集
	 */
	private void addSingerInfo(HashMap<String, Singer> singerMap, String singer, SongInfo songInfo){
		Singer mSinger = singerMap.get(singer);
		if(mSinger == null){
			mSinger = new Singer();
			singerMap.put(singer, mSinger);
			mSinger.setmSingerName(singer);
		}
		mSinger.addSong(songInfo);
	}
	
	/**
	 * 专辑数据收集
	 */
	private void addAlbumInfo(HashMap<String, Album> albumMap, String album, SongInfo songInfo){
		Album mAlbum = albumMap.get(album);
		if(mAlbum == null){   
			mAlbum = new Album();
			albumMap.put(album, mAlbum);
			mAlbum.setmAlbumName(album);
		}
		mAlbum.addSong(songInfo);
	}
	
	/**
	 * 歌曲文件夹数据收集
	 */
	private void addSongDirInfo(HashMap<String, SongDir> songDirMap, String filePath, SongInfo songInfo) {
		filePath = Utils.getInstance().getDirPathFromfile(filePath);
		SongDir mSongDir = songDirMap.get(filePath);
		if(mSongDir == null){
			mSongDir = new SongDir();
			songDirMap.put(filePath, mSongDir);
			mSongDir.setDirPath(filePath);
		}
		mSongDir.addSong(songInfo);
	}
	
	/**
	 * 获取歌曲info
	 * @return
	 */
	public Vector<SongInfo> getAllSong() {
		return songList;
	}

	/**
	 * 获取歌手info
	 * @return
	 */
	public Vector<Singer> getAllSinger() {
		return singerList;
	}

	/**
	 * 获取专辑info
	 * @return
	 */
	public Vector<Album> getAllAlbum() {
		return albumList;
	}
	
	/**
	 * 获取文件夹info
	 */
	public Vector<SongDir> getAllSongDir(){
		return songDirList;
	}
	
	/**
	 * 获取歌曲字母对照表
	 * @return
	 */
	public HashMap<String, Integer> getAllSongABCIndex(){
		return songABCIndex;
	}
	
	Vector<SongInfo> searchSongInfoList = new Vector<SongInfo>();
	/**
	 * 根据关键字查询歌曲
	 * 先实现在缓存中搜索，避免重新从数据库中搜索构造songinfo对象，如果效率上有问题再改为数据库搜索
	 */
	public Vector<SongInfo> querySong(String key){
		L.i(tag, "querySong key : " + key);
		Utils.getInstance().startTime("querySong");
		searchSongInfoList.clear();
		if(songList == null || key == null || "".equals(key.trim())){
			return searchSongInfoList;
		}
		key = key.trim();
		for(SongInfo songInfo : songList){
			//比较歌曲名 包括拼音
			if(songInfo.getTitle().contains(key) || songInfo.getPinyin().contains(key.toUpperCase())){
				searchSongInfoList.add(songInfo);
				L.i(tag, "歌名：" + songInfo.getTitle());
			}else if(songInfo.getSinger().contains(key)){	//比较歌手名
				searchSongInfoList.add(songInfo);
				L.i(tag, "歌手名：" + songInfo.getSinger());
			}else if(songInfo.getAlbum().contains(key)){	//比较专辑名
				searchSongInfoList.add(songInfo);
				L.i(tag, "专辑名：" + songInfo.getAlbum());
			}
		}
		L.i(tag, "找到歌曲数目：" + searchSongInfoList.size());
		Utils.getInstance().endUseTime("querySong");
		return searchSongInfoList;
	}
	
	/**
	 * 根据路径查找歌曲
	 * @param path
	 * @return
	 */
	public SongInfo querySongFromPath(String path){
		SongInfo result = null;
		Utils.getInstance().startTime("querySongFromPath");
		for(SongInfo songInfo : songList){
			//比较歌曲名 包括拼音
			if(songInfo.getFileName().equals(path)){
				result = songInfo;
				break;
			}
		}
		Utils.getInstance().endUseTime("querySongFromPath");
		return result;
	}
	
	/**
	 * 根据路径查找歌曲
	 * 不做安全检查
	 */
	public SongInfo querySongFromList(String path, Vector<SongInfo> songInfos){
		Utils.getInstance().startTime("querySongFromList");
		for(SongInfo songInfo : songInfos){
			//比较歌曲名 包括拼音
			if(songInfo.getFileName().equals(path)){
				return songInfo;
			}
		}
		Utils.getInstance().endUseTime("querySongFromList");
		return null;
	}
	
	/**
	 * 查询所有收藏
	 */
	public Cursor queryAllCollection(){
		return mSongDBHelp.queryAllSongFromCollection();
	}
	
	/**
	 * 缓存列表
	 */
	private ArrayList<SongInfo> tempSongList = new ArrayList<SongInfo>();
	
	/**
	 * 遍历收藏列表，需在所有列表查询完后再执行
	 */
	public int loadAllMusicInfoFromCollection(){
		if(Thread.currentThread().getId() == Looper.getMainLooper().getThread().getId()){	//如果
			throw new IllegalArgumentException("请在子线程中加载");
		}
		synchronized (collectionList) {
			resetCollection();
			Cursor allSongCursor = SongManager.getInstance().queryAllCollection();
			if(allSongCursor != null){
				int idIndex = allSongCursor.getColumnIndex("id");
				int typeIndex = allSongCursor.getColumnIndex("type");
				int fidIndex  = allSongCursor.getColumnIndex("fid");
				int pinyinIndex = allSongCursor.getColumnIndex("pinyin");
				int pathIndex = allSongCursor.getColumnIndex("path");
				int nameIndex = allSongCursor.getColumnIndex("name");
				int singernameIndex = allSongCursor.getColumnIndex("singername");
				int albumnameIndex = allSongCursor.getColumnIndex("albumname");
				while(allSongCursor.moveToNext()){
	//				(id long not null, type integer not null, fid long not null, path text not null, pinyin text not null, name text not null, singername text, albumname text, PRIMARY KEY (id,type))
					long id = allSongCursor.getLong(idIndex);
					int type = allSongCursor.getInt(typeIndex);
					long fid = allSongCursor.getLong(fidIndex);
					String pinyin = allSongCursor.getString(pinyinIndex);
					String filePath = allSongCursor.getString(pathIndex);
					String title = allSongCursor.getString(nameIndex);
					String aristr = allSongCursor.getString(singernameIndex);
					String album = allSongCursor.getString(albumnameIndex);
					
					if(!new File(filePath).exists() || isRemove(filePath)){
						continue;
					}
					
					
					SongInfo songInfo = SongInfoFactory.CreateSongInfo(id, type);
					songInfo.setAlbum(album);
					songInfo.setTitle(title);
					songInfo.setSinger(aristr);
					songInfo.setFileName(filePath);
					songInfo.setPinyin(pinyin);
					if(songList.contains(songInfo)){
						int listIndex = songList.indexOf(songInfo);
						songList.remove(listIndex);
						songList.add(listIndex, songInfo);
					}
					collectionList.add(songInfo);
					if(new File(songInfo.getFileName()).exists()){
						collectionExistsList.add(songInfo);
					}else{
						collectionNotExistsList.add(songInfo);
					}
				}   
				allSongCursor.close();
				//打印测试
				for(SongInfo song : collectionList){
					L.e(tag, "收藏列表：" + song.toString());
				}
			}
		}  
		return 1;
	}
	
	/**
	 * 获取收藏列表
	 */
	public Vector<SongInfo> getCollectionSong(){
		return this.collectionList;
	}
	
	/**
	 * 获取收藏列表中当前本机存在的歌
	 */
	public Vector<SongInfo> getCollectionExistsSong(){
		return this.collectionExistsList;
	}
	
	/**
	 * 获取收藏列表中当前本机不存在的歌
	 */
	public Vector<SongInfo> getCollectionNotExistsSong(){
		return this.collectionNotExistsList;
	}
	
	/**
	 * 收藏回调，如果收藏耗时则通过回调返回状态,返回状态需要放在主线程中完成
	 */
	public interface CollectionCallBack{
		public void ctrlCollectionOK();
	}
	
	/**
	 * 添加收藏
	 */
	public synchronized boolean addCollection(SongInfo songInfo, CollectionCallBack mCollectionCallBack) {
		boolean result = false;
		do{
			if(collectionList.contains(songInfo)){
				L.w(tag, "已经收藏过！");
				result = true;
				break;
			}
			//存入数据库
			tempSongList.clear();
			tempSongList.add(songInfo);
			if(this.mSongDBHelp.insertCollectionTable(tempSongList)){
				//删除当前列表
				collectionList.add(songInfo);
				Utils.getInstance().startTime("收藏排序");
				Collections.sort(collectionList);	//排序
				Utils.getInstance().endUseTime("收藏排序");
				result = true;
			}else{
				L.w(tag, "收藏异常！" + songInfo.toString());
				result = false;
			}
			tempSongList.clear();
		}while(false);
		mCollectionCallBack.ctrlCollectionOK();
		return result;
	}

	/**
	 * 删除收藏
	 */
	public synchronized boolean delCollection(SongInfo songInfo, CollectionCallBack mCollectionCallBack) {
		boolean result = false;
		do{
			if(!collectionList.contains(songInfo)){
				L.w(tag, "歌曲并非收藏！");
				result = true;
				break;
			}
			//删除数据库
			tempSongList.clear();
			tempSongList.add(songInfo);
			if(this.mSongDBHelp.deleteSongInfoFromCollection(tempSongList)){
				//删除当前列表
				collectionList.remove(songInfo);	
				collectionExistsList.remove(songInfo);
				collectionNotExistsList.remove(songInfo);
				result = true;
			}else{
				L.w(tag, "删除收藏异常！" + songInfo.toString());
				result = false;
			}
			tempSongList.clear();
		}while(false);
		mCollectionCallBack.ctrlCollectionOK();
		return result;
	}

	/**
	 * 歌曲是否被收藏
	 * @param songInfo
	 * @return
	 */
	public boolean isConllection(SongInfo songInfo) {
		return this.collectionList == null? false : collectionList.contains(songInfo) ? true : false;
	}

	
	
	
	public boolean isLoadok() {
		return isLoadok;
	}

	public void setLoadok(boolean isLoadok) {
		this.isLoadok = isLoadok;
	}

	/**
	 * 根据目录路径获得目录下所有歌曲
	 */
	public Vector<SongInfo> getSonginfosFromDirPath(String dirPath){
		if(dirPath == null || "".equals(dirPath.trim())){
			return null;
		}
		File file = new File(dirPath);
		if(!file.isDirectory()){
			return null;
		}
		if(isLoadok()){	//如果已经扫描完成,从扫描目录中取  如果未扫描完，则不返回
			for(SongDir mSongDir : songDirList){
				if(mSongDir.getdirPath().equals(dirPath)){
					Vector<SongInfo> result = new Vector<SongInfo>();
					result.addAll(mSongDir.getSongDirSet());
					Collections.sort(result);
					return result;
				}
			}
		}
		return null;
	}
	
	/**
	 * 进度管理-----------
	 * 文件夹扫描	30%
	 * 文件扫描	20%
	 * 歌曲查询	5%
	 * id3解析	40%
	 * 入库		5%
	 * 呈现
	 */
	
}	
