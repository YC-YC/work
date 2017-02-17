package com.zhonghong.chelianupdate.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import android.content.Context;

import com.zhonghong.chelianupdate.base.AppConst;

public class FileUtil {
	
	public static String getGroupVersionVo(Context context)
	{
		String updateInfoSavePath = context.getFilesDir().getAbsolutePath() + AppConst.UPDATE_INFO_FILE_NAME;
		File file = new File(updateInfoSavePath);
		if (!file.exists()) {
			return null;
		}
		StringBuilder builder = new StringBuilder();
		FileReader fr = null;
		char[] buffer = new char[1024];
		try {
			fr = new FileReader(file);
			int len = 0;
			while ((len = fr.read(buffer)) > 0) {
				builder.append(buffer, 0, len);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		finally
		{
			try {
				if(fr!=null)
					fr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return builder.toString();
	}
	
	public static void DeleteFile(File file) { 
        if (file.exists() == false) { 
            return; 
        } else { 
            if (file.isFile()) { 
                file.delete(); 
                return; 
            } 
            if (file.isDirectory()) { 
                File[] childFile = file.listFiles(); 
                if (childFile == null || childFile.length == 0) { 
                    file.delete(); 
                    return; 
                } 
                for (File f : childFile) { 
                    DeleteFile(f); 
                } 
                file.delete(); 
            } 
        } 
    } 
}
