package com.zhonghong.mediascanner.utils;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.text.TextUtils;
import android.util.Log;

import com.zhonghong.mediascanerlib.config.Config;


/**
 * 
 * @author YC
 * @time 2016-12-8 下午5:38:36
 * TODO:
 */
public class FileUtil {

    private static final String TAG = "FileUtil";

	private FileUtil() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }
    
    /**
     * 生成数据库文件名
     * @param uuid
     * @return
     */
    public static String getDBFile(String uuid){
    	return (Config.DIR_PATH+Config.DB_FINE_START_TAG+uuid+".db");
    }
    
    public static boolean createOrExistsFile(String filePath) {
        return createOrExistsFile(new File(filePath));
    }

    /**
     * 判断文件是否存在，不存在则判断是否创建成功
     * @param file
     * @return
     */
    public static boolean createOrExistsFile(File file) {
        if (file == null) return false;
        // 如果存在，是文件则返回true，是目录则返回false
        if (file.exists()) return file.isFile();
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 创建文件夹
     * @param file
     * @return
     */
    public static boolean createOrExistsDir(String dirPath) {
        return createOrExistsDir(new File(dirPath));
    }
    
    /**
     * 创建文件夹
     * @param file
     * @return
     */
    public static boolean createOrExistsDir(File file) {
        // 如果存在，是目录则返回true，是文件则返回false，不存在则返回是否创建成功
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }


    /**
     * 删除文件
     * @param srcFilePath
     * @return
     */
    public static boolean deleteFile(String srcFilePath) {
        return deleteFile(new File(srcFilePath));
    }
    
    /**
     * 删除文件
     * @param file
     * @return
     */
    public static boolean deleteFile(File file) {
        return file != null && (!file.exists() || file.isFile() && file.delete());
    }
    
    /**
     * 删除目录
     * @param dirPath
     * @param deleteDir 是否删除当前文件夹
     * @return
     */
    public static boolean deleteDir(String dirPath, boolean deleteDir) {
        return deleteDir(new File(dirPath), deleteDir);
    }

    /**
     * 删除目录
     * @param dirPath
     * @param deleteDir 是否删除当前文件夹
     * @return
     */
    public static boolean deleteDir(File dir, boolean deleteDir) {
        if (dir == null) return false;
        // 目录不存在返回true
        if (!dir.exists()) return true;
        // 不是目录返回false
        if (!dir.isDirectory()) return false;
        // 现在文件存在且是文件夹
        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                if (file.isFile()) {
                    if (!deleteFile(file)) return false;
                } else if (file.isDirectory()) {
                    if (!deleteDir(file, true)) return false;
                }
            }
        }
        if (deleteDir){
        	return dir.delete();
        }
        return true;
    }
    
    /**
     *获取全路径中的文件名(带拓展名)
     */
    public static String getFileName(File file) {
        if (file == null) return null;
        return getFileName(file.getPath());
    }

    /**
     * 获取全路径中的文件名(带拓展名)
     */
    public static String getFileName(String filePath) {
        int lastSep = filePath.lastIndexOf(File.separator);
        return lastSep == -1 ? filePath : filePath.substring(lastSep + 1);
    }
    
    /**
     * 获取文件名（去除盘符）
     */
    public static String getFilePathNoRoot(String filePath) {
    	String rootPath = getRootPath(filePath);
    	if (!TextUtils.isEmpty(rootPath)){
    		return filePath.substring(rootPath.length());
    	}
    	return "";
    }
    
    /**
     * 获取文件夹
     * @param name
     * @return
     */
    public static String getFolderName(String name){
		if (name == null){
			return null;
		}
		String folder = File.separator;
		int lastIndex = name.lastIndexOf(File.separator);
		if (lastIndex > 0){
			folder = name.substring(0, lastIndex);
		}
		return folder;
	}

    /**
     * 获取全路径中的文件名(不带拓展名)
     */
    public static String getFileNameNoExtension(File file) {
        if (file == null) return null;
        return getFileNameNoExtension(file.getPath());
    }

    /**
     * 获取全路径中的文件名(不带拓展名)
     */
    public static String getFileNameNoExtension(String filePath) {
        int lastPoi = filePath.lastIndexOf('.');
        int lastSep = filePath.lastIndexOf(File.separator);
        if (lastSep == -1) {
            return (lastPoi == -1 ? filePath : filePath.substring(0, lastPoi));
        }
        if (lastPoi == -1 || lastSep > lastPoi) {
            return filePath.substring(lastSep + 1);
        }
        return filePath.substring(lastSep + 1, lastPoi);
    }

    /**
     * 获取全路径中的文件拓展名
     */
    public static String getFileExtension(File file) {
        if (file == null) return null;
        return getFileExtension(file.getPath());
    }

    /**
     * 获取全路径中的文件拓展名
     */
    public static String getFileExtension(String filePath) {
        int lastPoi = filePath.lastIndexOf('.');
        int lastSep = filePath.lastIndexOf(File.separator);
        if (lastPoi == -1 || lastSep >= lastPoi) return "";
        return filePath.substring(lastPoi + 1);
    }
    
    /**
     * 获取盘符
     * @param path
     * @return
     */
    public static String getRootPath(String path){
    	String[] folders = path.split(File.separator);
    	String root = "";
    	for (int i = 0; i < 2; i++){
    		root += (File.separator + folders[i+1]);
    	}
    	return root;
	}
    
    /**
     * 获取文件级数
     * @param file
     * @return
     */
    public static int getFolderNum(File file){
		String path = file.getPath();
		String[] folders = path.split(File.separator);
		if (folders != null){
			return folders.length-1;
		}
		return 0;
	}
	
	
	/**
	 * 获取设备的UUID
	 * @param context
	 * @param device 必需为根目录 {@link #getRootPath(String)}
	 * @return
	 */
	public static String getDeviceUuid(Context context, String device){
		if (TextUtils.isEmpty(device)){
			return "";
		}
		StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
		StorageVolume[] storageVolumes = storageManager.getVolumeList();
		for (int i = 0; i < storageVolumes.length; i++){
			StorageVolume storageVolume = storageVolumes[i];
			if (storageVolume.getPath().equals(device) /*&& storageVolume.getState().equals(Environment.MEDIA_MOUNTED)*/){
				String uuid = storageVolume.getUuid();
				if (TextUtils.isEmpty(uuid)){
					uuid = "FFFF-FFFF";
				}
				return uuid;
			}
		}
		return "";
	}
	
	/**
	 * 通过uuid查设备名称
	 * @param context
	 * @param uuid
	 * @return
	 */
	public static String getUUIDDevice(Context context, String uuid){
		if (TextUtils.isEmpty(uuid)){
			return "";
		}
		StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
		StorageVolume[] storageVolumes = storageManager.getVolumeList();
		for (int i = 0; i < storageVolumes.length; i++){
			StorageVolume storageVolume = storageVolumes[i];
			if (uuid.equals(storageVolume.getUuid())){
				return storageVolume.getPath();
			}
		}
		return "";
	}
	
	/**
	 * 设备是否挂载上
	 * @param device
	 * @return
	 */
	public static boolean isDeviceMounted(String device){
		File file = new File(device);
		if (file.exists() && file.canExecute()){
			Log.i(TAG, "isDeviceMounted " + device);
			return true;
		}
		Log.i(TAG, "isDevice not Mounted " + device);
		return false;
	}
}