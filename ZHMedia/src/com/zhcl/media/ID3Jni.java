/**
 * 
 */
package com.zhcl.media;

/**
 * @author guoxs
 */
public class ID3Jni { 
	public static boolean isUseJni = false;
	static { 
		try {  
			System.loadLibrary("ParseID3");
			isUseJni = true;
		} catch (Exception e) {
			e.printStackTrace(); 
			isUseJni = false;
		}
	}

	public static boolean isLoadId3JniOK() { 
		return isUseJni;
	}

	public static boolean isHaveID3Info(String filePath) {
		return ParseID3(filePath);
	}

	public static String getTitle() {
		return GetID3Title();
	}

	public static String getAlbum() {
		return GetID3Album(); 
	}

	public static String getArtist() {
		return GetID3Artist();
	}

	private static native boolean ParseID3(String filePath);

	private static native boolean ParseID3Pic(String filePath, String savePicPath);

	private static native String GetID3Title();

	private static native String GetID3Artist();

	private static native String GetID3Album();
	
}
