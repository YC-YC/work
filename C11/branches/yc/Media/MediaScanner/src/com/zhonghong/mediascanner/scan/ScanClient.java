/**
 * 
 */
package com.zhonghong.mediascanner.scan;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android.os.Process;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.zhonghong.mediascanerlib.config.Config;
import com.zhonghong.mediascanerlib.filebean.AudioInfo;
import com.zhonghong.mediascanerlib.filebean.FileInfo.FileType;
import com.zhonghong.mediascanerlib.filebean.ImageInfo;
import com.zhonghong.mediascanerlib.filebean.OtherInfo;
import com.zhonghong.mediascanerlib.filebean.VideoInfo;
import com.zhonghong.mediascanner.IDataMode;
import com.zhonghong.mediascanner.bean.DeviceInfo;
import com.zhonghong.mediascanner.bean.DirInfo;
import com.zhonghong.mediascanner.bean.ScanCallback;
import com.zhonghong.mediascanner.saver.DBLoader;
import com.zhonghong.mediascanner.saver.DBLoader.SaveType;
import com.zhonghong.mediascanner.utils.FileUtil;
import com.zhonghong.mediascanner.utils.ILifeCycle;
import com.zhonghong.mediascanner.utils.L;
import com.zhonghong.utils.ID3Jni;

/**
 * @author YC
 * @time 2016-12-9 下午12:05:07
 * TODO:
 */
public class ScanClient implements ILifeCycle{

	private static final String TAG = "ScanClient";
	private String mScanDevice = null;
	private String mScanDeviceUuid = null;
	private ScanCallback mScanCallback = null;
	private IDataMode mDataMode;

	DeviceInfo scanDeviceInfo = null;
	
	public ScanClient(String device, String uuid, IDataMode dateMode){
		this.mDataMode = dateMode;
		this.mScanDevice = device;
		this.mScanDeviceUuid = uuid;
		scanDeviceInfo = new DeviceInfo(mScanDeviceUuid, mScanDevice);
		L.i(TAG, "new ScanClient");
	}
	
	/**
	 * 正在扫描的盘符
	 * @return
	 */
	public DeviceInfo getScanDevice(){
		return scanDeviceInfo;
	}

	/**
	 * 开始扫描盘符
	 * @param root
	 * @param callback
	 */
	public synchronized void startScanDevice(ScanCallback callback){
		L.i(TAG, "start scan " + mScanDevice);
		if (callback == null){
			throw new IllegalArgumentException("ScanCallback can't be null");
		}
		mScanCallback = callback;
		new Thread(new ScanRunnable(), "scan_thread").start();
	}
	
	private class ScanRunnable implements Runnable{

		DBLoader dbLoader = new DBLoader(mScanDeviceUuid, mScanDevice);
		DeviceInfo dbDeviceInfo = null;
		
		
//		long LTime = 0;
//		long CPTime = 0;
		
		@Override
		public void run() {
			Process.setThreadPriority(Process.THREAD_PRIORITY_FOREGROUND);
			onDisPatchStatus(ScanCallback.STATUS_START_DB_LOAD, "");
			L.startTime("加载数据库");
			loadDBDeviceInfo();
			L.endUseTime("加载数据库");
			onDisPatchStatus(ScanCallback.STATUS_FINISH_DB_LOAD, "");
			
			onDisPatchStatus(ScanCallback.STATUS_START_SCAN, "");
//			L.startTime("扫描设备:"+mScanDeviceUuid);
//			LTime = 0;
//			CPTime = 0;
//			boolean scanDir = scanDir(mScanDevice);
			boolean scanDir = scanDir2(mScanDevice);
//			L.i("L", String.format("LTime=[%d], CPTime=[%d], total=[%d]",  LTime, CPTime, LTime+CPTime));
//			L.endUseTime("扫描设备:"+mScanDeviceUuid);
			if (scanDir){
				L.i("L", String.format("scan end, has audio=[%d], video=[%d], image=[%d]", 
						scanDeviceInfo.getAudioInfos().size(), 
						scanDeviceInfo.getVideoInfos().size(),
						scanDeviceInfo.getImageInfos().size()));
				mDataMode.setLoadedDeviceInfo(scanDeviceInfo);
				onDisPatchStatus(ScanCallback.STATUS_FINISH_SCAN, "");
				
				/*while(mDataMode.isReqDeviceInfo(mScanDevice)){
					L.i(TAG, "wait req finish");
					SystemClock.sleep(100);
				}*/
				onDisPatchStatus(ScanCallback.STATUS_START_DB_SAVE, "");
				L.startTime("保存数据库");
				saveDBDeviceInfo();
				L.endUseTime("保存数据库");
				onDisPatchStatus(ScanCallback.STATUS_FINISH_DB_SAVE, "");
			}
			else{
				onDisPatchStatus(ScanCallback.STATUS_BREAK_SCAN, "");
			}
			
		}
		
		private void loadDBDeviceInfo(){
			dbDeviceInfo = mDataMode.getLoadedDeviceInfo(mScanDeviceUuid);
			if (dbDeviceInfo == null){
				dbDeviceInfo = dbLoader.loadInfoFromDB();
				
				if (dbDeviceInfo != null){
					mDataMode.setLoadedDeviceInfo(dbDeviceInfo);
				}
			}
		}
		
		private void saveDBDeviceInfo(){
			
			boolean bSaveAudioInfo = false;
			boolean bSaveVideoInfo = false;
			boolean bSaveImageInfo = false;
			boolean bSaveOtherInfo = false;
			boolean bSaveDirInfo = false;
			
			if (dbDeviceInfo == null
					|| dbDeviceInfo.getDirID() != scanDeviceInfo.getDirID()) {
				bSaveDirInfo = true;
			}
			if (dbDeviceInfo == null
					|| dbDeviceInfo.getAudioID() != scanDeviceInfo.getAudioID()) {
				bSaveAudioInfo = true;
			}
			if (dbDeviceInfo == null
					|| dbDeviceInfo.getVideoID() != scanDeviceInfo.getVideoID()) {
				bSaveVideoInfo = true;
			}
			if (dbDeviceInfo == null
					|| dbDeviceInfo.getImageID() != scanDeviceInfo.getImageID()) {
				bSaveImageInfo = true;
			}
			if (Config.bGetOtherInfo
					&& (dbDeviceInfo == null || dbDeviceInfo.getOtherID() != scanDeviceInfo
							.getOtherID())) {
//				L.i(TAG, "dbDeviceInfo.getAllOtherID() = " + dbDeviceInfo.getAllOtherID() + 
//						", scanDeviceInfo.getAllOtherID() = " + scanDeviceInfo.getAllOtherID());
				bSaveOtherInfo = true;
			}
			SaveType saveType = dbLoader.new SaveType(bSaveAudioInfo,
					bSaveVideoInfo, bSaveImageInfo, bSaveOtherInfo, bSaveDirInfo);
			dbLoader.saveDeviceInfoToDB(scanDeviceInfo, saveType);
		}
		
		private FileFilter dirFilter = new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				if (pathname.isDirectory()){
					return true;
				}
				return false;
			}
		};
		
		/**
		 * 扫描目录文件(废弃方法)
		 * @param dirPath
		 * @return 文件不存在时返回false（认为是拨出U盘）
		 */
		private boolean scanDir(String dirPath){
			
			Log.i("L", "scanDir = " + dirPath);
			String dirName = FileUtil.getFilePathNoRoot(dirPath);
			if (TextUtils.isEmpty(dirName)){
				//根目录
				dirName = File.separator;
			}
			
			File dir = new File(dirPath);		
			if(!dir.exists()){
				return false;
			}
			if (Config.LIMITED_SCAN_FOLDER_NUM){
				if (FileUtil.getFolderNum(dir) >= Config.MAX_SCAN_FOLDER_NUM){
					L.i("L", "file to long, ignore");
					return true;
				}
			}
			
			/**当前文件夹是否需要扫描文件*/
			boolean bReScanFile = true;
			
			DirInfo dbDirInfo = null;
			if (dbDeviceInfo != null){
				dbDirInfo = dbDeviceInfo.getDirInfo(dirName);
			}
			
			DirInfo scanDirInfo = null;
			if (dirName.equals(File.separator)){
				//根目录
				scanDirInfo = new DirInfo(dirName, 0, 0, 0);
			}
			else{
				scanDirInfo = new DirInfo(dirName, dir.lastModified(), 0, 0);
				if (dbDirInfo != null 
						&& dbDirInfo.getModifiled() == dir.lastModified()){
					//时间相同，不用扫描该目录下的文件，但仍需要遍历文件夹
					bReScanFile = false;
				}
			}
			scanDeviceInfo.putDirInfo(scanDirInfo);
//			L.i(TAG, "dir bReScanFile = " + bReScanFile);
			if (!bReScanFile){
				//将数据库加载的数据全部复制过来
//				L.startTime("复制文件信息");
//				long copyTime = SystemClock.elapsedRealtime();
				ArrayList<Integer> audioInfos = dbDirInfo.getAudioInfos();
				for (int i = 0; i < audioInfos.size(); i++){
					scanDeviceInfo.putFileInfo(scanDirInfo, FileType.TYPE_AUDIO, 
							dbDeviceInfo.getAudioInfos().get(audioInfos.get(i)));
				}
				L.i("L", "cp audioInfos size=" + audioInfos.size());
				ArrayList<Integer> videoInfos = dbDirInfo.getVideoInfos();
				for (int i = 0; i < videoInfos.size(); i++){
					scanDeviceInfo.putFileInfo(scanDirInfo, FileType.TYPE_VIDEO, 
							dbDeviceInfo.getVideoInfos().get(videoInfos.get(i)));
				}
				L.i("L", "cp videoInfos size=" + videoInfos.size());
				ArrayList<Integer> imageInfos = dbDirInfo.getImageInfos();
				for (int i = 0; i < imageInfos.size(); i++){
					scanDeviceInfo.putFileInfo(scanDirInfo, FileType.TYPE_IMAGE, 
							dbDeviceInfo.getImageInfos().get(imageInfos.get(i)));
				}
				L.i("L", "cp imageInfos size=" + imageInfos.size());
				ArrayList<Integer> otherInfos = dbDirInfo.getOtherInfos();
				for (int i = 0; i < otherInfos.size(); i++){
					scanDeviceInfo.putFileInfo(scanDirInfo, FileType.TYPE_OTHER, 
							dbDeviceInfo.getOtherInfos().get(otherInfos.get(i)));
				}
				L.i("L", "cp other size=" + otherInfos.size());
//				CPTime += (SystemClock.elapsedRealtime() - copyTime);
//				L.i("L", "复制文件总耗时：" + CPTime);
//				L.endUseTime("复制文件信息");
				
			}
			
//			L.startTime("列出文件信息");
//			long listTime = SystemClock.elapsedRealtime();
			File[] subFiles = null;
			if (bReScanFile){
				L.i("L", "listFiles");
				subFiles = dir.listFiles();
			}
			else{
				L.i("L", "listFolders");
				subFiles = dir.listFiles(dirFilter);
			}
//			LTime += (SystemClock.elapsedRealtime() - listTime);
//			L.i("L", "列出文件总耗时：" + LTime);
//			L.endUseTime("列出文件信息");
			if (subFiles == null){
				return false;
			}
			L.i("L", "proc subFiles.size = " + subFiles.length);
			for(File file: subFiles){
				if (!file.exists()){
					return false;
				}
				L.i("L", "proc file = " + file);
				if (!file.isHidden()){
					
					//文件夹都需要扫描
					if (file.isDirectory()){
						if (!isIgnoreFolder(file)){
//							SystemClock.sleep(5);
							if (!scanDir(file.getPath())){
								return false;
							}
						}
					}
					else{
						if (bReScanFile){
							
							if (file.isFile()){
								String path = file.getPath();
								
								String fileName = FileUtil.getFilePathNoRoot(path);
								String extension = FileUtil.getFileExtension(path).toLowerCase(Locale.ENGLISH);
								String name = file.getName();
								long modifiled = file.lastModified();
								long size = file.length();
								if (Config.AUDIO_EXTENSION.contains(extension)){
									String title = null;
									String artist = null;
									String album = null;
									
									AudioInfo audioInfo = null;
									if (dbDirInfo != null){
										audioInfo = dbDeviceInfo.getAudioInfo(dbDirInfo, fileName);
									}
									if (audioInfo != null && audioInfo.getModifiled() == modifiled){
										title = audioInfo.getTitle();
										artist = audioInfo.getArtist();
										album = audioInfo.getAlbum();
									}
									else{
										if (ID3Jni.isHaveID3Info(path)){
											title = ID3Jni.getTitle();
											artist = ID3Jni.getArtist();
											album = ID3Jni.getAlbum();
										}
										title = TextUtils.isEmpty(title) ? "unknown": title;
										artist = TextUtils.isEmpty(artist) ? "unknown": artist;
										album = TextUtils.isEmpty(album) ? "unknown": album;
									}
									AudioInfo audioInfo2 = new AudioInfo(fileName, name, modifiled, size, title, artist, album);
									scanDeviceInfo.putFileInfo(scanDirInfo, FileType.TYPE_AUDIO, audioInfo2);
//									scanDirInfo.putAudioInfo(audioInfo2);
								}
								else if (Config.VIDEO_EXTENSION.contains(extension)){
									VideoInfo videoInfo = new VideoInfo(fileName, name, modifiled, size);
									scanDeviceInfo.putFileInfo(scanDirInfo, FileType.TYPE_VIDEO, videoInfo);
//									scanDirInfo.putVideoInfo(videoInfo);
								}
								else if (Config.IMAGE_EXTENSION.contains(extension)){
									ImageInfo imageInfo = new ImageInfo(fileName, name, modifiled, size);
									scanDeviceInfo.putFileInfo(scanDirInfo, FileType.TYPE_IMAGE, imageInfo);
//									scanDirInfo.putImageInfo(imageInfo);
								}
								else{
									if (Config.bGetOtherInfo){
										OtherInfo otherInfo = new OtherInfo(fileName, name, modifiled, size);
										scanDeviceInfo.putFileInfo(scanDirInfo, FileType.TYPE_OTHER, otherInfo);
//										scanDirInfo.putOtherInfo(otherInfo);
									}
								}
							}
						}
					}
				}
			}
			return true;
		}
		
		/**
		 * 扫描目录文件（这才是正宫）
		 * @param dirPath
		 * @return 文件不存在时返回false（认为是拨出U盘）
		 */
		private boolean scanDir2(String dirPath){
			
			Log.i("L", "scanDir = " + dirPath);
			String dirName = FileUtil.getFilePathNoRoot(dirPath);
			if (TextUtils.isEmpty(dirName)){
				//根目录
				dirName = File.separator;
			}
			
			File dir = new File(dirPath);		
			if(!dir.exists()){
				return false;
			}
			if (Config.LIMITED_SCAN_FOLDER_NUM){
				if (FileUtil.getFolderNum(dir) >= Config.MAX_SCAN_FOLDER_NUM){
					L.i("L", "file to long, ignore");
					return true;
				}
			}
			
			
			File[] subFiles = null;
			List<File> fileLists = new ArrayList<File>();
			long folderSize = 0L;
			long folderHashCode = 0L;
			List<File> folderLists = new ArrayList<File>();
			subFiles = dir.listFiles();
			if (subFiles != null){
				for(File file: subFiles){
					if (file.isDirectory()){
						folderLists.add(file);
					}
					else if (file.isFile()){
						fileLists.add(file);
						folderSize += file.length();
						folderHashCode += file.hashCode();
					}
				}
			}
			
			/**当前文件夹是否需要扫描文件*/
			boolean bReScanFile = true;
			
			DirInfo dbDirInfo = null;
			if (dbDeviceInfo != null){
				dbDirInfo = dbDeviceInfo.getDirInfo(dirName);
			}
			

			DirInfo scanDirInfo = null;
			if (dirName.equals(File.separator)){
				//根目录
				scanDirInfo = new DirInfo(dirName, 0, 0, 0);
			}
			else{
				scanDirInfo = new DirInfo(dirName, dir.lastModified(), folderSize, folderHashCode);
				if (dbDirInfo != null 
						&& dbDirInfo.getModifiled() == scanDirInfo.getModifiled()
						&& dbDirInfo.getSize() == scanDirInfo.getSize()
						&& dbDirInfo.getHashCode() == scanDirInfo.getHashCode()){
					//时间、大小、hashCode相同，不用扫描该目录下的文件，但仍需要遍历文件夹
					bReScanFile = false;
				}
			}
			scanDeviceInfo.putDirInfo(scanDirInfo);
//			L.i(TAG, "dir bReScanFile = " + bReScanFile);
			//遍历文件
			if (!bReScanFile){
				//将数据库加载的数据全部复制过来
//				L.startTime("复制文件信息");
//				long copyTime = SystemClock.elapsedRealtime();
				ArrayList<Integer> audioInfos = dbDirInfo.getAudioInfos();
				for (int i = 0; i < audioInfos.size(); i++){
					scanDeviceInfo.putFileInfo(scanDirInfo, FileType.TYPE_AUDIO, 
							dbDeviceInfo.getAudioInfos().get(audioInfos.get(i)));
				}
//				L.i("L", "cp audioInfos size=" + audioInfos.size());
				ArrayList<Integer> videoInfos = dbDirInfo.getVideoInfos();
				for (int i = 0; i < videoInfos.size(); i++){
					scanDeviceInfo.putFileInfo(scanDirInfo, FileType.TYPE_VIDEO, 
							dbDeviceInfo.getVideoInfos().get(videoInfos.get(i)));
				}
//				L.i("L", "cp videoInfos size=" + videoInfos.size());
				ArrayList<Integer> imageInfos = dbDirInfo.getImageInfos();
				for (int i = 0; i < imageInfos.size(); i++){
					scanDeviceInfo.putFileInfo(scanDirInfo, FileType.TYPE_IMAGE, 
							dbDeviceInfo.getImageInfos().get(imageInfos.get(i)));
				}
//				L.i("L", "cp imageInfos size=" + imageInfos.size());
				ArrayList<Integer> otherInfos = dbDirInfo.getOtherInfos();
				for (int i = 0; i < otherInfos.size(); i++){
					scanDeviceInfo.putFileInfo(scanDirInfo, FileType.TYPE_OTHER, 
							dbDeviceInfo.getOtherInfos().get(otherInfos.get(i)));
				}
//				L.i("L", "cp other size=" + otherInfos.size());
//				CPTime += (SystemClock.elapsedRealtime() - copyTime);
//				L.i("L", "复制文件总耗时：" + CPTime);
//				L.endUseTime("复制文件信息");
				
			}
			else{
				for (File file: fileLists){
					if (!file.exists()){
						return false;
					}
//					L.i("L", "proc file = " + file);
					if (!file.isHidden()){

						String path = file.getPath();
						
						String fileName = FileUtil.getFilePathNoRoot(path);
						String extension = FileUtil.getFileExtension(path).toLowerCase(Locale.ENGLISH);
						String name = file.getName();
						long modifiled = file.lastModified();
						long size = file.length();
						if (Config.AUDIO_EXTENSION.contains(extension)){
							String title = null;
							String artist = null;
							String album = null;
							
							AudioInfo audioInfo = null;
							if (dbDirInfo != null){
								audioInfo = dbDeviceInfo.getAudioInfo(dbDirInfo, fileName);
							}
							if (audioInfo != null && audioInfo.getModifiled() == modifiled){
								title = audioInfo.getTitle();
								artist = audioInfo.getArtist();
								album = audioInfo.getAlbum();
							}
							else{
								if (ID3Jni.isHaveID3Info(path)){
									title = ID3Jni.getTitle();
									artist = ID3Jni.getArtist();
									album = ID3Jni.getAlbum();
								}
								title = TextUtils.isEmpty(title) ? "unknown": title;
								artist = TextUtils.isEmpty(artist) ? "unknown": artist;
								album = TextUtils.isEmpty(album) ? "unknown": album;
							}
							AudioInfo audioInfo2 = new AudioInfo(fileName, name, modifiled, size, title, artist, album);
							scanDeviceInfo.putFileInfo(scanDirInfo, FileType.TYPE_AUDIO, audioInfo2);
//							scanDirInfo.putAudioInfo(audioInfo2);
						}
						else if (Config.VIDEO_EXTENSION.contains(extension)){
							VideoInfo videoInfo = new VideoInfo(fileName, name, modifiled, size);
							scanDeviceInfo.putFileInfo(scanDirInfo, FileType.TYPE_VIDEO, videoInfo);
//							scanDirInfo.putVideoInfo(videoInfo);
						}
						else if (Config.IMAGE_EXTENSION.contains(extension)){
							ImageInfo imageInfo = new ImageInfo(fileName, name, modifiled, size);
							scanDeviceInfo.putFileInfo(scanDirInfo, FileType.TYPE_IMAGE, imageInfo);
//							scanDirInfo.putImageInfo(imageInfo);
						}
						else{
							if (Config.bGetOtherInfo){
								OtherInfo otherInfo = new OtherInfo(fileName, name, modifiled, size);
								scanDeviceInfo.putFileInfo(scanDirInfo, FileType.TYPE_OTHER, otherInfo);
//								scanDirInfo.putOtherInfo(otherInfo);
							}
						}
					
					}
				}
			}
			
			//遍历文件夹
			for (File folder: folderLists){
				if (!folder.exists()){
					return false;
				}
//				L.i("L", "proc file = " + folder);
				if (!folder.isHidden()){
					if (!isIgnoreFolder(folder)){
//						SystemClock.sleep(5);
						if (!scanDir2(folder.getPath())){
							return false;
						}
					}
				}
			}
			
//			L.startTime("列出文件信息");
//			long listTime = SystemClock.elapsedRealtime();
			/*File[] subFiles = null;
			if (bReScanFile){
				L.i("L", "listFiles");
				subFiles = dir.listFiles();
			}
			else{
				L.i("L", "listFolders");
				subFiles = dir.listFiles(dirFilter);
			}*/
//			LTime += (SystemClock.elapsedRealtime() - listTime);
//			L.i("L", "列出文件总耗时：" + LTime);
//			L.endUseTime("列出文件信息");
			
			return true;
		}
		
		
		private boolean isIgnoreFolder(File dir){
			String path = dir.getPath();
			String rootPath = FileUtil.getRootPath(path);
			Iterator<String> iterator = Config.FOLDER_BLOCKLIST.iterator();
			while(iterator.hasNext()){
				String ignorFolder = rootPath + iterator.next();
				if (path.equalsIgnoreCase(ignorFolder)){
					L.i(TAG, "ignorFolder = " + ignorFolder);
					return true;
				}
			}
			return false;
		}
		
	}
	
	
	@Override
	public void onObjectCreate() {
		
	}

	@Override
	public void onObjectDestory() {
		
	}

	private void onDisPatchStatus(int status, String info) {
//		L.i(TAG, "onStatus status = " + status + ", info = " + info);
		if (mScanCallback != null){
			mScanCallback.onStatus(mScanDevice, status, info);
		}
		else{
			L.i(TAG, "null callback");
		}
	}
	
}
