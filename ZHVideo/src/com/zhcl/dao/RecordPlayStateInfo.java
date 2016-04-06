/**
 * 
 */
package com.zhcl.dao;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.zh.uitls.L;

/**
 * 播放状态info
 * 
 * @author zhonghong.chenli
 * @date 2015-11-22 下午6:06:44
 */
public class RecordPlayStateInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final String tag = "RecordPlayStateInfo"; 

	/** 播放歌曲 */
	private VideoInfo videoInfo;
	/** 播放模式 */
	private int playMode;
	/** 当前播放时间 */
	private int currentPlayTime;
	/** 播放状态 */
	private boolean isPlay;

	public VideoInfo getVideoInfo() {
		return videoInfo;
	}

	public void setVideoInfo(VideoInfo videoInfo) {
		this.videoInfo = videoInfo;
	}

	public int getPlayMode() {
		return playMode;
	}

	public void setPlayMode(int playMode) {
		this.playMode = playMode;
	}

	public int getCurrentPlayTime() {
		return currentPlayTime;
	}

	public void setCurrentPlayTime(int currentPlayTime) {
		this.currentPlayTime = currentPlayTime;
	}

	public boolean isPlay() {
		return isPlay;
	}

	public void setPlay(boolean isPlay) {
		this.isPlay = isPlay;
	}

	@Override
	public boolean equals(Object o) {
		if(o == null || !(o instanceof RecordPlayStateInfo)){
			L.i(tag, "~instanceof");
			return false;
		}
		RecordPlayStateInfo o2 = (RecordPlayStateInfo)o;
		boolean isSame = true;
		
		do{	
			if(videoInfo == null && o2.getVideoInfo() != null || videoInfo != null && o2.getVideoInfo() == null){
				isSame = false;
				L.i(tag, "null ~null");
				break;
			}
			if(videoInfo != null && o2.getVideoInfo() != null && !videoInfo.getFileName().equals(o2.getVideoInfo().getFileName())){
				isSame = false;
				L.i(tag, "~path");
				break;
			}
			if(currentPlayTime != o2.getCurrentPlayTime()){
				isSame = false;
//				L.i(tag, "~currentPlayTime");
				break;
			}
			if(playMode != o2.getPlayMode()){
				isSame = false;
				L.i(tag, "~playMode");
				break;
			}
			if(isPlay != o2.isPlay()){
				isSame = false;
				L.i(tag, "~isPlay");
				break;
			}
		}while(false);
		return isSame;
	}
 
	public Object clone() {
		Object result = null;
		ByteArrayOutputStream bo = null;
		ObjectOutputStream oo = null;
		ByteArrayInputStream bi = null;
		ObjectInputStream oi = null;
		try {
			// 将对象写到流里
			bo = new ByteArrayOutputStream();
			oo = new ObjectOutputStream(bo);
			oo.writeObject(this);
			// 从流里读出来
			bi = new ByteArrayInputStream(bo.toByteArray());
			oi = new ObjectInputStream(bi);
			result = oi.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				bo.close();
				oo.close();
				bi.close();
				oi.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}

	
	
	
}
