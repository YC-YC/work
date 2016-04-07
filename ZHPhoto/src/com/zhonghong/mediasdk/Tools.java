package com.zhonghong.mediasdk;import java.io.BufferedInputStream;import java.io.File;import java.io.FileInputStream;import java.io.IOException;import java.math.BigInteger;import android.content.Context;import android.content.Intent;import android.net.ConnectivityManager;import android.net.NetworkInfo;import android.os.Environment;import android.util.Log;/*import com.jrm.localmm.R;*//** *  * @author luoyong *  * @date 2011-07-27 */public class Tools {	private static final String FILE_SIZE_B = "B";	private static final String FILE_SIZE_KB = "KB";	private static final String FILE_SIZE_MB = "MB";	private static final String FILE_SIZE_GB = "GB";	private static final String FILE_SIZE_TB = "TB";	private static final String FILE_SIZE_NA = "N/A";	private static final String TAG = "Tools";			public static int DEVICE=Constants.SDCARD_DEVICE;	/**	 * 	 * @param path	 * @return 	 */	public static boolean isFileExist(String path) {		return isFileExist(new File(path));	}	/**	 * 	 * @param file	 *            {@link File}.	 */	public static boolean isFileExist(File file) {		if (file == null) {			return false;		}		return file.exists();	}	/**	 * @param duration	 * 	 * @return 	 */	public static String formatDuration(long duration) {		long time = duration / 1000;		if (time <= 0) {			return "--:--:--";		}		long min = time / 60 % 60;		long hour = time / 60 / 60;		long second = time % 60;		return String.format("%02d:%02d:%02d", hour, min, second);	}	/**	 * 	 * @param duration	 * 	 * @return 	 */	public static String formatDuration2(int duration) {		int time = duration / 1000;		if(time == 0 && duration > 0)		{		    time = 1;		}		int min = time / 60;		int second = time % 60;		return String.format("%02d:%02d", min, second);	}		/**	 * fromat second to druation	 */	public static String formatSecDuration2(int duration) {		int min = duration / 60;		int second = duration % 60;		return String.format("%02d:%02d", min, second);	}		/**	 * @param size	 * 	 * @return	 */	public static String formatSize(BigInteger size) {		if (size.compareTo(BigInteger.valueOf(1024)) == -1) {			return (size.toString() + FILE_SIZE_B);		} else if (size.compareTo(BigInteger.valueOf(1024 * 1024)) == -1) {			return (size.divide(BigInteger.valueOf(1024)).toString() + FILE_SIZE_KB);		} else if (size.compareTo(BigInteger.valueOf(1024 * 1024 * 1024)) == -1) {			return (size.divide(BigInteger.valueOf(1024 * 1024)).toString() + FILE_SIZE_MB);		} else if (size.compareTo(BigInteger				.valueOf(1024 * 1024 * 1024 * 1024L)) == -1) {			return (size.divide(BigInteger.valueOf(1024 * 1024 * 1024))					.toString() + FILE_SIZE_GB);		} else if (size.compareTo(BigInteger				.valueOf(1024 * 1024 * 1024 * 1024L).multiply(				BigInteger.valueOf(1024))) == -1) {			return (size.divide(BigInteger.valueOf(1024 * 1024 * 1024 * 1024L))					.toString() + FILE_SIZE_TB);		}		return FILE_SIZE_NA;	}	/**	 * @param type	 * 	 * @return	 */	public static boolean isMediaFile(final int type) {		if (Constants.FILE_TYPE_PICTURE == type				|| Constants.FILE_TYPE_SONG == type				|| Constants.FILE_TYPE_VIDEO == type) {			return true;		} else {			return false;		}	}	/**	 * 	 * @return	 */	public static boolean isNetWorkConnected(Context context) {		try {			ConnectivityManager cm = (ConnectivityManager) context			.getSystemService(Context.CONNECTIVITY_SERVICE);			if (cm != null) {				NetworkInfo ni = cm.getActiveNetworkInfo();				if (ni == null) {					return false;				}				boolean isConnected = ni.isConnected();				boolean isStateConnected = (ni.getState() == NetworkInfo.State.CONNECTED);				if (isConnected && isStateConnected) {					return true;				}			}		} catch (Exception e) {			Log.d("Tools", "network exception." + e.getMessage());		}		return false;	}	/**	 * 	 * @return	 */	public static String getUSBMountedPath() {		return Environment.getExternalStorageDirectory().getParent();	}	/**	 * 	 * The size of file is whether larger than the specified size.	 * 	 * 	 * 	 * @param path	 *            absolute path of file.	 * 	 * @param size	 *            the specified size of file.	 * 	 * @return true if the file is larger than size, otherwise false.	 */	public static boolean isLargeFile(final String path, final long size) {		File file = new File(path);		// file does not exist		if (!isFileExist(file)) {			Log.d("Tools", "file does not exist");			return true;		}		long length = file.length();		Log.d("Tools", "size of file : " + length);		// file bigger than size		if (length > size) {			return true;		}		return false;	}     // Subtitle Language Type    private static String[] subtitleLanguageType = null;        public static void setSubtitleLanguageType(String[] types){        subtitleLanguageType = types;    }        public static String[] getSubtitleLanguageTypes(){        return subtitleLanguageType;    }        public static String getSubtitleLanguage(int index){        if (subtitleLanguageType != null) {            if(index < subtitleLanguageType.length && index >=0){                return subtitleLanguageType[index];            }                   }        return null;    }        public static int getSubtitleLanguageIndex(String value){        if(subtitleLanguageType != null){            int size = subtitleLanguageType.length;            for (int i = 0; i < size; i++) {                if(subtitleLanguageType[i].equals(value)){                    return i;                }            }        }          return 0;    }    	public static String GetCharset(String path,byte[] buffer) {			String charset = "UTF-8";			int i = -1;					//			for(int j=0;j < buffer.length;j++){//				Log.d(TAG,"buffer"+j+" "+Integer.toHexString(buffer[j]&0xff));//			}							while ((buffer != null) && (++i < buffer.length)) {				if (buffer[i] <= (byte)0xA0) {					continue;				}else if((buffer[i] >= (byte)0xB0) && (buffer[i] <= (byte)0xF7)){					if(++i >=  buffer.length){break;}					if((buffer[i] >= (byte)0xA1) && (buffer[i] <= (byte)0xFE)){						charset = "GB2312";					}else{						charset = "UTF-8";						break;					}				}			}		Log.d(TAG,"GetCharset path:{"+path+"} charset:"+charset);		return charset;	}}