/**
 * 
 */
package com.simcom.updateversion;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import android.text.TextUtils;
import android.util.Log;

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
	
	public static String process(String[] args){
		String result = "";
//		String args[] = {"stop", "ril-daemon"};
		ProcessBuilder processBuilder = new ProcessBuilder(args);
		Process process = null;
		InputStream errIs = null;
		InputStream inIs = null;
		try {
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int read = -1;
			process = processBuilder.start();
			errIs = process.getErrorStream();
			
			while( (read = errIs.read()) != -1){
				baos.write(read);
			}
			baos.write('\n');
			
			inIs = process.getInputStream();
			while((read = inIs.read()) != -1){
				baos.write(read);
			}
			byte[] data = baos.toByteArray();
			result = new String(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			try{
				if (errIs != null){
					errIs.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			
			try{
				if (inIs != null){
					inIs.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			
			if (process != null){
				process.destroy();
			}
		}
		
		return result;
	}
}
