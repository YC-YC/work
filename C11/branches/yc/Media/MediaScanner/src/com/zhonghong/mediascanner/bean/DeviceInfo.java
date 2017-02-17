/**
 * 
 */
package com.zhonghong.mediascanner.bean;

import java.util.ArrayList;
import java.util.List;

import com.zhonghong.mediascanerlib.filebean.AudioInfo;
import com.zhonghong.mediascanerlib.filebean.FileInfo;
import com.zhonghong.mediascanerlib.filebean.FileInfo.FileType;
import com.zhonghong.mediascanerlib.filebean.ImageInfo;
import com.zhonghong.mediascanerlib.filebean.OtherInfo;
import com.zhonghong.mediascanerlib.filebean.VideoInfo;
import com.zhonghong.mediascanner.utils.FileUtil;
import com.zhonghong.mediascanner.utils.L;

/**
 * @author YC
 * @time 2016-12-19 下午12:20:01
 * TODO:
 */
public class DeviceInfo {


	private static final String TAG = "DeviceInfo";
	private String uuid;
	private String rootPath;
	/**是否挂载上*/
	private boolean bMounted = false;
	private long audioID = 0L;
	private long videoID = 0L;
	private long imageID = 0L;
	private long otherID = 0L;
	private long dirID = 0L;
	
	private List<DirInfo> mDirInfos = new ArrayList<DirInfo>();
	private ArrayList<AudioInfo> mAudioInfos = new ArrayList<AudioInfo>();
	private ArrayList<VideoInfo> mVideoInfos = new ArrayList<VideoInfo>();
	private ArrayList<ImageInfo> mImageInfos = new ArrayList<ImageInfo>();
	private ArrayList<OtherInfo> mOtherInfos = new ArrayList<OtherInfo>();
	
	public DeviceInfo(String uuid, String rootPath){
		this.uuid = uuid;
		this.rootPath = rootPath;
	}
	
	public String getUuid(){
		return uuid;
	}
	
	public String getRootPath(){
		return rootPath;
	}
	
	public boolean getMounted(){
		return bMounted;
	}
	
	public void setMounted(boolean bMounted){
		this.bMounted = bMounted;
	}
	
	public void putDirInfo(DirInfo dirInfo){
//		L.i(TAG, "putDirInfo " + dirInfo.getName());
		mDirInfos.add(dirInfo);
		dirID += dirInfo.getModifiled();
	}
	
	/**
	 * 通过该方法更新文件信息会自动更新ID信息
	 * @param type
	 * @param info
	 */
	public void putFileInfo(FileType type, FileInfo info){
//		L.i(TAG, "putFileInfo path = " + info.getPath());
		DirInfo dirInfo = getDirInfo(FileUtil.getFolderName(info.getPath()));
		
		if (dirInfo != null){
			switch (type) {
			case TYPE_AUDIO:
				mAudioInfos.add((AudioInfo) info);
				audioID += info.getId();
				dirInfo.putAudioInfo(mAudioInfos.size()-1);
				break;
			case TYPE_VIDEO:
				mVideoInfos.add((VideoInfo) info);
				videoID += info.getId();
				dirInfo.putVideoInfo(mVideoInfos.size()-1);
				break;
			case TYPE_IMAGE:
				mImageInfos.add((ImageInfo) info);
				imageID += info.getId();
				dirInfo.putImageInfo(mImageInfos.size()-1);
				break;
			case TYPE_OTHER:
				mOtherInfos.add((OtherInfo) info);
				otherID += info.getId();
				dirInfo.putOtherInfo(mOtherInfos.size()-1);
				break;
			default:
				break;
			}
		}
		else{
			L.i(TAG, "null dir");
		}
	}
	
	/**
	 * 通过该方法更新文件信息会自动更新ID信息
	 * @param type
	 * @param info
	 */
	public void putFileInfo(DirInfo dirInfo, FileType type, FileInfo info){
//		L.i(TAG, "putFileInfo path = " + info.getPath());
		
		if (dirInfo != null){
			switch (type) {
			case TYPE_AUDIO:
				mAudioInfos.add((AudioInfo) info);
				audioID += info.getId();
				dirInfo.putAudioInfo(mAudioInfos.size()-1);
				break;
			case TYPE_VIDEO:
				mVideoInfos.add((VideoInfo) info);
				videoID += info.getId();
				dirInfo.putVideoInfo(mVideoInfos.size()-1);
				break;
			case TYPE_IMAGE:
				mImageInfos.add((ImageInfo) info);
				imageID += info.getId();
				dirInfo.putImageInfo(mImageInfos.size()-1);
				break;
			case TYPE_OTHER:
				mOtherInfos.add((OtherInfo) info);
				otherID += info.getId();
				dirInfo.putOtherInfo(mOtherInfos.size()-1);
				break;
			default:
				break;
			}
		}
		else{
			L.i(TAG, "null dir");
		}
	}
	
	
	public DirInfo getDirInfo(String dirName){
//		L.i(TAG, "getDirInfo dirName = " + dirName);
		DirInfo dirInfo = null;
		for (int i = 0; i < mDirInfos.size(); i++){
			if (mDirInfos.get(i).getName().equals(dirName)){
				dirInfo = mDirInfos.get(i);
				break;
			}
		}
		return dirInfo;
	}
	
	public List<DirInfo> getDirInfos(){
		return mDirInfos;
	}
	
	

	/**
	 * @return the mAudioInfos
	 */
	public ArrayList<AudioInfo> getAudioInfos() {
		return mAudioInfos;
	}
	
	public AudioInfo getAudioInfo(DirInfo dirInfo, String fileName) {
		ArrayList<Integer> audioInfos = dirInfo.getAudioInfos();
		for (int i = 0; i < audioInfos.size(); i++){
			if (fileName.equals(audioInfos.get(i))){
				return mAudioInfos.get(i);
			}
		}
		return null;
	}

	/**
	 * @return the mVideoInfos
	 */
	public ArrayList<VideoInfo> getVideoInfos() {
		return mVideoInfos;
	}

	/**
	 * @return the mImageInfos
	 */
	public ArrayList<ImageInfo> getImageInfos() {
		return mImageInfos;
	}

	/**
	 * @return the mOtherInfos
	 */
	public ArrayList<OtherInfo> getOtherInfos() {
		return mOtherInfos;
	}

	/**
	 * @return the audioID
	 */
	public long getAudioID() {
		return audioID;
	}

	/**
	 * @return the videoID
	 */
	public long getVideoID() {
		return videoID;
	}

	/**
	 * @return the imageID
	 */
	public long getImageID() {
		return imageID;
	}

	/**
	 * @return the otherID
	 */
	public long getOtherID() {
		return otherID;
	}

	/**
	 * @return the dirID
	 */
	public long getDirID() {
		return dirID;
	}

	

}
