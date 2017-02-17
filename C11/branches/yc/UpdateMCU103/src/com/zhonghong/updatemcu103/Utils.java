/**
 * 
 */
package com.zhonghong.updatemcu103;

import java.io.File;
import java.util.ArrayList;

import android.os.SystemClock;
import android.text.TextUtils;

/**
 * @author YC
 * @time 2016-10-14 下午3:31:25
 * TODO:
 */
public class Utils {

	public static boolean hasFileExits(String path){
		if (!TextUtils.isEmpty(path)){
			File file = new File(path);
			if (file.exists()){
				return true;
			}
		}
		return false;
	}
	
	private static ArrayList<String> listStr = new ArrayList<String>();
	
	public static void searchFile(String folder, String keywork){
		File[] files = new File(folder).listFiles();
		if (files != null){
			for(File file: files){
				if (file.toString().endsWith(keywork)){
					listStr.add(file.toString());
				}
			}
		}
	}
	
	public static void clearSearchList(){
		listStr.clear();
	}
	
	public static ArrayList<String> getSearchList(){
		return listStr;
	}
	
	
	
	
}
