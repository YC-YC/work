/**
 * 
 */
package com.zhcl.video;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import com.zh.uitls.L;
import com.zh.uitls.Utils;
import com.zhcl.dao.RecordPlayStateInfo;
import com.zhcl.dao.VideoInfo;
import com.zhcl.filescanner.FileScanner;
import com.zhcl.service.service.client.VideoPlayerClient;

/**
 * 断点记忆处理
 *  
 * @author zhonghong.chenli
 * @date 2015-11-22 下午6:03:06
 */ 
public class RecordManager {
	private static final String tag = "RecordManager";

	private RecordManager() {
	};

	public static RecordManager mRecordManager;

	public static RecordManager getInstance() {
		if (mRecordManager == null) {
			mRecordManager = new RecordManager();
		}
		return mRecordManager;
	}

	private RecordPlayStateInfo mRecordPlayStateInfo;
	private RecordPlayStateInfo cloneRecordPlayStateInfo;
	private Vector<VideoInfo> menuList;
	private static final String recordPath = "/mnt/sdcard/zhvideo/videoInfoState.db";
	String recordMenuListPath = "/mnt/sdcard/zhvideo/videoMenuList.db";
	static boolean isRun;

	/**
	 * 记录歌单信息
	 */
	public void recordPlayMenuList(final Vector<VideoInfo> videoList) {

	}

	// 断点记忆处·······························
	/**
	 * 每三秒检查有变化则记忆一次
	 */
	public void startRecord() {
		isRun = true;
		mRecordPlayStateInfo = new RecordPlayStateInfo();
		new Thread() {
			public void run() {
				try {
					Thread.sleep(1000);
					while (isRun) {
						if(FileScanner.getInstance(null).isScanfEnd() && CurrentPlayManager.getInstance().isAllowRecordVideoInfo()){ // 扫描完并且在视频播放界面才记忆
							Utils.getInstance().startTime("记录断点");
							runFunction();
							Utils.getInstance().endUseTime("记录断点");
						}
						Thread.sleep(3000);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	/**
	 * 强制记忆
	 */
	public void record(){
		L.e(tag, "强制记忆！");
		runFunction();
	}
	
	/**
	 * 终止记录
	 */
	public void endRecord(){
		isRun = false;
	}

	
	/**
	 * 获取最新状态
	 */
	private void updataInfo() {
		mRecordPlayStateInfo.setVideoInfo(CurrentPlayManager.getInstance().getVideoInfo());
		mRecordPlayStateInfo.setPlayMode(CurrentPlayManager.getInstance().getPlayMode());
		mRecordPlayStateInfo.setPlay(VideoPlayerClient.getInstance().isPlaying());
		L.e(tag, "记忆播放状态：" + VideoPlayerClient.getInstance().isPlaying());
		mRecordPlayStateInfo.setCurrentPlayTime(VideoPlayerClient.getInstance().getCurrentPosition());
	}

	/**
	 * 持续执行函数
	 */
	private void runFunction() {
		boolean isChange = false;
		// 获取最新状态
		updataInfo();
		isChange = !mRecordPlayStateInfo.equals(cloneRecordPlayStateInfo);

		if (isChange) { 
			// 克隆
			cloneRecordPlayStateInfo = (RecordPlayStateInfo) mRecordPlayStateInfo.clone();
			// 写入
			writeObjectToPath(mRecordPlayStateInfo, recordPath);
			writeMenuList();
//			L.e(tag, "记录！！！");
		}
	}

	
	/**
	 * 传入歌单
	 */
	public void setMenuList(Vector<VideoInfo> menuList){
		this.menuList = menuList;
	}
	/**
	 * 写入歌单
	 */
	private void writeMenuList(){
		if(this.menuList != null){
			writeObjectToPath(menuList, recordMenuListPath);
			menuList = null;
		}
	}
	/**
	 * 写入对象至文件
	 * 
	 * @param o
	 * @param path
	 * @return
	 */
	private boolean writeObjectToPath(Object o, String path) {
		ObjectOutputStream oo = null;
		try {
			oo = new ObjectOutputStream(new FileOutputStream(new File(path)));
			oo.writeObject(o);
		} catch (Exception e) { 
			e.printStackTrace();
			return false;
		} finally {
			try {
				oo.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return true;
	}

	/**
	 * 从文件中读出对象
	 * 
	 * @param path
	 * @return
	 */
	private static Object readObjectFromPath(String path) {
		Object result = null;
		ObjectInputStream oi = null;
		File file = new File(path);
		if(!file.exists()){
			return null;
		}
		try {
			oi = new ObjectInputStream(new FileInputStream(file));
			result = oi.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(oi != null){
					oi.close();
				}
			} catch (IOException e) { 
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 读取记忆的断点文件
	 * @return
	 */
	public static RecordPlayStateInfo getRecordPlayStateInfo(){
		return (RecordPlayStateInfo) readObjectFromPath(recordPath);
	}
	
	/**
	 * 读出歌单
	 */
	@SuppressWarnings("unchecked")
	public Vector<VideoInfo> getMenuList(){
		return (Vector<VideoInfo>)readObjectFromPath(recordMenuListPath);
	}
}
