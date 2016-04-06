/**
 * 
 */
package com.zhcl.media;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.zh.dao.RecordPlayStateInfo;
import com.zh.dao.SongInfo;
import com.zh.uitls.Utils;
import com.zhcl.filescanner.FileScanner;
import com.zhcl.service.CustomApplication;
import com.zhcl.service.service.client.AudioPlayerClient;

/**
 * 断点记忆处理
 *  
 * @author zhonghong.chenli
 * @date 2015-11-22 下午6:03:06
 */ 
public class RecordManager {
	private static final String tag = "RecordManager";

	private RecordManager() {};

	public static RecordManager mRecordManager;

	public static RecordManager getInstance() { 
		if (mRecordManager == null) {
			mRecordManager = new RecordManager();
		}
		return mRecordManager;
	}

	private RecordPlayStateInfo mRecordPlayStateInfo;
	private RecordPlayStateInfo cloneRecordPlayStateInfo;
	private Vector<SongInfo> menuList;
	String recordPath = CustomApplication.recordPath;//"/mnt/sdcard/zhmusic/songInfoState.db";
	String recordMenuListPath = CustomApplication.recordMenuListPath;//"/mnt/sdcard/zhmusic/songMenuList.db";
	String recordBackup = recordPath + "backup";
	String recordMenuListBackupPath = recordMenuListPath + "backup";
	static boolean isRun;
	private Context context;

	/**
	 * 当前未调用
	 * @param context
	 */
	public void init(Context context){
		this.context = context;
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
						if(FileScanner.getInstance(null).isScanfEnd()){ // 扫描完才记忆
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
	 * 终止记录
	 */
	public void endRecord(){
		isRun = false;
	}

	/**
	 * 获取最新状态
	 */
	private void updataInfo() {
		mRecordPlayStateInfo.setSongInfo(CurrentPlayManager.getInstance().getSongInfo());
		mRecordPlayStateInfo.setPlayMode(CurrentPlayManager.getInstance().getPlayMode());
		mRecordPlayStateInfo.setPlay(AudioPlayerClient.getInstance().isPlaying());
		mRecordPlayStateInfo.setCurrentPlayTime(AudioPlayerClient.getInstance().getCurrentPosition());
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
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			writeObjectToPath(mRecordPlayStateInfo, recordBackup);	//备份
			
			writeMenuList();
//			L.e(tag, "记录！！！");
		}
	}

	
	/**
	 * 传入歌单
	 */
	public void setMenuList(Vector<SongInfo> menuList){
		this.menuList = menuList;
	}
	/**
	 * 写入歌单
	 */
	private void writeMenuList(){
		if(this.menuList != null){
			writeObjectToPath(menuList, recordMenuListPath);
			writeObjectToPath(menuList, recordMenuListBackupPath);	//备份
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
			oo.flush();
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
	private Object readObjectFromPath(String path) {
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
	public RecordPlayStateInfo getRecordPlayStateInfo(){
		RecordPlayStateInfo result = (RecordPlayStateInfo) readObjectFromPath(recordPath);
		 if(result == null){//读备份
			 result = (RecordPlayStateInfo) readObjectFromPath(recordBackup);
		 }
		 return result;
	}
	
	/**
	 * 读出歌单
	 */
	@SuppressWarnings("unchecked")
	public Vector<SongInfo> getMenuList(){
		Vector<SongInfo> result = (Vector<SongInfo>)readObjectFromPath(recordMenuListPath);
		if(result == null){//读备份
			result = (Vector<SongInfo>)readObjectFromPath(recordMenuListBackupPath);
		}
		return result;
	}
}
