/**
 * 
 */
package com.zhcl.video;

import android.annotation.SuppressLint;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import com.zh.uitls.L;
import com.zh.uitls.Utils;
import com.zhcl.dao.VideoInfo;

/**
 * @author chenli
 *
 */
public class VideoInfoFactory extends ObjectFactory{
	private static String tag = "VideoInfoFactory";
	/**
	 * 根据id和type构造videoInfo对象
	 * @param id
	 * @param type
	 */
	public static VideoInfo CreateVideoInfo(long id, int type){
		return new VideoInfo(id, type);
	}
	
	
	/**
	 * 创建videoInfo，自行解析好id3
	 * @param path
	 * @return
	 */
	public static VideoInfo CreateVideoInfo(String path){
		int id = VideoInfo.makeId(path);
		int type = 6;
		VideoInfo videoInfo = CreateVideoInfo(id, type);
		videoInfo.setFileName(path);  
		Utils.getInstance().startTime("解析时间");
		videoInfo.setDuration(getVideoDuration(path));
		Utils.getInstance().endUseTime("解析时间");
		videoInfo.setPinyin(Utils.getInstance().getPinYin(videoInfo.getTitle()));
		return videoInfo;
	}
	 
	public static MediaMetadataRetriever retriever = null;
	@SuppressLint("NewApi")
	public static int getVideoDuration(String filePath){
		int result = 0;
		if(retriever == null){
			retriever = new MediaMetadataRetriever();
		}
		try{
			retriever.setDataSource(filePath);
			result = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
		}catch(Exception e){
			Log.w(tag, "读取时长异常 ： " + filePath); 
			retriever.release();
			retriever = null;
		}
		L.e(tag, "总时长：" + result);
		return result;
	}
}
