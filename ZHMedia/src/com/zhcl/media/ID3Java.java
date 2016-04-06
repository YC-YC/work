/**
 * 
 */
package com.zhcl.media;

import android.media.MediaMetadataRetriever;
import android.util.Log;

/**
 * 用java解析ID3
 * @author chenli
 */
public class ID3Java {
	private static final String tag = "ID3Java";
	private static String title;
	private static String album;
	private static String artist;
	public static MediaMetadataRetriever retriever;
	public static boolean isHaveID3Info(String filePath){
		if(retriever == null){
			retriever = new MediaMetadataRetriever();
		}
		try{
			retriever.setDataSource(filePath);
			title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
			artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
			album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
		}catch(Exception e){
			Log.w(tag, "id3 异常");
			retriever.release();
//			e.printStackTrace(); 
			retriever = null;
			return false;
		}
		return true;
	}

	/**
	 * 获取歌曲名
	 */
	public static String getTitle(){
		return title;
	}

	/**
	 * 获取专辑
	 */
	public static String getAlbum(){
		return album;
		
	}

	/**
	 * 获取歌手
	 */
	public static String getArtist(){
		return artist;
	}

}
