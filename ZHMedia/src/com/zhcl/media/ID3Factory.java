/**
 * 
 */
package com.zhcl.media;

import java.io.File;

import android.util.Log;

import com.zh.uitls.L;

/**
 * @author chenli 
 *
 */
public class ID3Factory {
	private static final String tag = "ID3Factory";
	/**
	 * 获取id3对象
	 * @param filePath
	 * @return
	 */
	public static ID3 getID3(String filePath){
		ID3 id3 = new ID3();
		if(filePath == null || !new File(filePath).exists() || !isHaveID3Info(filePath)){
			Log.w(tag, "getID3 异常！ path : " + filePath);
			return id3; 
		}
		id3.setAlbum(getAlbum());
		id3.setTitle(getTitle());
		id3.setArtist(getArtist());
//		L.i(tag, "getTitle() = " + getAlbum());
//		L.i(tag, "getAlbum() = " + getTitle());
//		L.i(tag, "getArtist() = " + getArtist());
		return id3;
	}
	
	
	/**
	 * 判断是否存在ID3信息
	 * @return
	 */
	private static boolean isHaveID3Info(String filePath){
		if(ID3Jni.isLoadId3JniOK()){
			return ID3Jni.isHaveID3Info(filePath);
		}
		return ID3Java.isHaveID3Info(filePath);
	}
	
	private static String getTitle() {
		if(ID3Jni.isLoadId3JniOK()){
			return ID3Jni.getTitle();
		}
		return ID3Java.getTitle();
	}

	private static String getAlbum() {
		if(ID3Jni.isLoadId3JniOK()){
			return ID3Jni.getAlbum();
		}
		return ID3Java.getAlbum();
	}

	private static String getArtist() {
		if(ID3Jni.isLoadId3JniOK()){
			return ID3Jni.getArtist();
		}
		return ID3Java.getArtist();
	}
	
	
	
	void id3Test(String filePath){
		ID3 id3 = ID3Factory.getID3(filePath);
		Log.e(tag, "filePath = " + filePath);
		Log.e(tag, id3.toString());
	}
}
