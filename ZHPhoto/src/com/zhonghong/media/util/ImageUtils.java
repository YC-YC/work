package com.zhonghong.media.util;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class ImageUtils {
	private static final String TAG = "ImageUtils";
	
	public static final List<String> Favourtes = null;
	
	public static float ENLARGESCALE = 1.2f;
	public static float NARROWSCALE = 0.8f;
	public static String JPG = ".JPG";
	public static String JPEG = ".JPEG";
	public static String GIF = ".GIF";
	public static String PNG = ".PNG";
	public static String BMP = ".BMP";
	public static String WBMP = ".WBMP";
	
	public static Map<String,ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
	
	public static Bitmap zoomBitmap(Bitmap bitmap, float scale) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        return newbmp;
    }
	
	public static Bitmap decodeFile(String pathName,int containWidth,int containHeight) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(pathName, options);
		options.inJustDecodeBounds = false;
		
//		Log.i(TAG, "options.outWidth = "+options.outWidth);
//		Log.i(TAG, "options.outHeight = "+options.outHeight);
		
		options.inSampleSize = calculateSampleSize(options.outWidth,options.outHeight, containWidth, containHeight);
		Bitmap unscaledBitmap = BitmapFactory.decodeFile(pathName, options);
//		Log.i(TAG, "unscaledBitmap.getWidth()="+unscaledBitmap.getWidth());
//		Log.i(TAG, "unscaledBitmap.getHeight()="+unscaledBitmap.getHeight());
		return unscaledBitmap;
	}
	
	public static int calculateSampleSize(int srcWidth, int srcHeight,int dstWidth, int dstHeight) {
		
		final float srcAspect = (float) srcWidth / (float) srcHeight;
		final float dstAspect = (float) dstWidth / (float) dstHeight;
		if (srcAspect > dstAspect) {
			return srcWidth / dstWidth;
		} else {
			return srcHeight / dstHeight;
		}
	}
	
	public static Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }
//	private static final String ROOT_STRING = "/sdcard";
	private static final String ROOT_STRING = "/StorageCard";
	private static final String UDISK_DIR = "/UDisk";

	
	public static void loadPictures(String dir,List<String> list)
	{
		File file = new File(dir);
		if(!file.exists())
		{
			return ;
		}
		list.clear();
		getAllFilesInDir(file, list);
	}
	
	public static void loadFoldPictures()
	{
		File file = new File(ROOT_STRING);
		File udiskfile = new File(UDISK_DIR);
		List<File> files = new ArrayList<File>();
		if(file.exists())
		{
		    files.add(file);
		}
		if(udiskfile.exists())
		{
		    files.add(udiskfile);
		}
		map.clear();
		getAllFilesAsDirectory(files, map);
	}
	
	public static void getAllFiles(File root,List<String> list)
	{
		File files[] = root.listFiles();
		if(files != null)
		for(File f:files)
		{
			if(f.getName().startsWith("."))
			{
				
			}
			else if(f.isDirectory())
			{
				getAllFiles(f,list);
			}
			else
			{
				String filename = f.getName().toUpperCase();
				 if(filename.endsWith(JPG)
						 || filename.endsWith(JPEG)
						 || filename.endsWith(GIF)
						 || filename.endsWith(PNG)
					 	|| filename.endsWith(BMP)
					 	|| filename.endsWith(WBMP))
				list.add(f.toString());
			}
		}
    }
	
	public static void getAllFilesInDir(File root,List<String> list){
		
		File files[] = root.listFiles();
		for(File f:files)
		{
			String filename = f.getName().toUpperCase();
			if (filename.endsWith(JPG) || filename.endsWith(JPEG)
					|| filename.endsWith(GIF) || filename.endsWith(PNG)
					|| filename.endsWith(BMP) || filename.endsWith(WBMP))
				list.add(f.toString());
		}
    }

	public static void getAllFilesAsDirectory(List<File> root,Map<String, ArrayList<String>> map)
	{
		for(int i=0;i<root.size();i++)
		{
			ArrayList<File> tempDirList = new ArrayList<File>();
			File files[] = root.get(i).listFiles();
			if(files != null)
			{
				ArrayList<String> path = new ArrayList<String>();
				String directorStr=null;
				for(File f:files)
				{
					if(f.getName().startsWith("."))
					{
						
					}
					else if(f.isDirectory())
					{
						tempDirList.add(f);
					}
					else
					{
						String filename = f.getName().toUpperCase();
						if(filename.endsWith(JPG) 
								|| filename.endsWith(JPEG)
								|| filename.endsWith(GIF)
								|| filename.endsWith(PNG)
								|| filename.endsWith(BMP) 
								|| filename.endsWith(WBMP))
						{
							path.add(f.toString());
							if(directorStr == null)
							{
								directorStr = f.getParent();
							}
						}
					}
				}
				
				if(path.size()>0 && directorStr != null)
				{
					map.put(directorStr,path);
				}
			}
			getAllFilesAsDirectory(tempDirList, map);
		}
	}
}
