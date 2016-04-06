/**
 * 
 */
package com.zh.uitls;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StatFs;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.zh.dao.SongInfo;
import com.zhcl.media.ImageManager;

/**
 * @author zhonghong.chenli date:2015-6-12上午10:07:45 <br/>
 */
public class Utils {
	private static final String tag = "Utils";
	private Utils() {
	}

	private static Utils mUtils;
	HashMap<String, Long> timeRecord;

	public static Utils getInstance() {
		if (mUtils == null) {
			mUtils = new Utils();
		}
		return mUtils;
	}

	public void startTime(String paramString) {
		if (this.timeRecord == null){
			this.timeRecord = new HashMap<String, Long>();
		}
		this.timeRecord.put(paramString, Long.valueOf(SystemClock.uptimeMillis()));
	}

	public boolean endUseTime(String paramString) {
		if ((this.timeRecord == null) || (!this.timeRecord.containsKey(paramString))) {
			L.e("Utils", paramString + " 错误 ： 未设置起始时间！");
			return false;
		}
		long l = SystemClock.uptimeMillis() - ((Long) this.timeRecord.get(paramString)).longValue();
		this.timeRecord.remove(paramString);
		L.i("Utils", paramString + " 消耗时间 ： " + l);
		return true;
	}
	
	
	/***
	 * 格式化时间
	 */
	@SuppressLint("SimpleDateFormat")
	public String formatTime(long time){
		 SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//等价于
		 return sdf.format(time);
	}
	
	
	/**
	 * 格式化大小
	 * @param paramLong
	 * @return
	 */
	public static String formatFileSize(long paramLong) {
		DecimalFormat localDecimalFormat = new DecimalFormat("#.00");
		if (paramLong == 0L)
			return "0B";
		if (paramLong < 1024L)
			return "0 KB";
		if (paramLong < 1048576L)
			return localDecimalFormat.format(paramLong / 1024.0D) + "KB";
		if (paramLong < 1073741824L)
			return localDecimalFormat.format(paramLong / 1048576.0D) + "MB";
		return localDecimalFormat.format(paramLong / 1073741824.0D) + "GB";
	}
	
	/**
	 * 取文件路径最后一截,不含“/”
	 */
	public String getFilePathLastSub(String filePath){
		return filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
	}
	
	/**
	 * 取文件所在文件夹
	 */
	public String getDirPathFromfile(String filePath){
		return filePath.substring(0, filePath.lastIndexOf("/"));
	}
	
	/** 
	 * 汉字返回拼音，字母原样返回，都转换为小写首字母
	 * @param input
	 * @return
	 */
	public String getPinYin(String input) {
		String result = cn2FirstSpell(input);
		if(result == null || "".equals(result.trim())){
			if(input != null && input.length() > 0){
				result =  input.substring(0, 1);
			} 
		} 
		if(result.length() == 0){
			return "";
		}
		char firstA = result.charAt(0);
		if(!(firstA >= 'A' && firstA <= 'Z') && !(firstA >= 'a'  && firstA <= 'z')){
			return "~" + result;
		}
		return result.toUpperCase();
	}
	
	 /** 
     * 获取汉字串拼音首字母，英文字符不变 
     * 
     * @param chinese 汉字串 
     * @return 汉语拼音首字母 
     */ 
    public static String cn2FirstSpell(String chinese) { 
            StringBuffer pybf = new StringBuffer(); 
            char[] arr = chinese.toCharArray(); 
            HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat(); 
            defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE); 
            defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE); 
            for (int i = 0; i < arr.length; i++) { 
                    if (arr[i] > 128) { 
                            try { 
                                    String[] _t = PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat); 
                                    if (_t != null) { 
                                            pybf.append(_t[0].charAt(0)); 
                                    } 
                            } catch (BadHanyuPinyinOutputFormatCombination e) { 
                                    e.printStackTrace(); 
                            } 
                    } else { 
                            pybf.append(arr[i]); 
                    } 
            } 
            return pybf.toString().replaceAll("\\W", "").trim(); 
    } 

    /** 
     * 获取汉字串拼音，英文字符不变 
     * 
     * @param chinese 汉字串 
     * @return 汉语拼音 
     */ 
    public static String cn2Spell(String chinese) { 
            StringBuffer pybf = new StringBuffer(); 
            char[] arr = chinese.toCharArray(); 
            HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat(); 
            defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE); 
            defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE); 
            for (int i = 0; i < arr.length; i++) { 
                    if (arr[i] > 128) { 
                            try { 
                                    pybf.append(PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat)[0]); 
                            } catch (BadHanyuPinyinOutputFormatCombination e) { 
                                    e.printStackTrace(); 
                            } 
                    } else { 
                            pybf.append(arr[i]); 
                    }  
            } 
            return pybf.toString(); 
    }   

    
    /**
	 * 得到 全拼
	 * 
	 * @param src
	 * @return
	 */
	public String getPinYinAll(String src) {
		char[] t1 = null;
		t1 = src.toCharArray();
		String[] t2 = new String[t1.length];
		HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
		t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		t3.setVCharType(HanyuPinyinVCharType.WITH_V);
		String t4 = "";
		int t0 = t1.length;
		try {
			for (int i = 0; i < t0; i++) {
				// 判断是否为汉字字符
				if (java.lang.Character.toString(t1[i]).matches(
						"[\\u4E00-\\u9FA5]+")) {
					t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);
					t4 += t2[0];
				} else {
					t4 += java.lang.Character.toString(t1[i]);
				}
			}
			return t4;
		} catch (BadHanyuPinyinOutputFormatCombination e1) {
			e1.printStackTrace();
		}
		return t4;
	}
	
	/**
	 * 毫秒格式化为00:00
	 * @param l
	 * @return
	 */
	public String formatLongToTimeStr(int l) {
        int hour = 0;
        int minute = 0;
        int second = 0;
 
        second = l / 1000;
 
        if (second > 60) {
            minute = second / 60;
            second = second % 60;
        }
        if (minute > 60) {
            hour = minute / 60;
            minute = minute % 60;
        }
        return /*(getTwoLength(hour) + ":" + */(getTwoLength(minute)  + ":"  + getTwoLength(second));
    }
    
    private String getTwoLength(final int data) {
        if(data < 10) {
            return "0" + data;
        } else {
            return "" + data;
        }
    } 
    
    /**
     * 根据歌曲路径返回歌词路径
     */
	public String serchLrc(SongInfo songInfo) { 
        String lrc = ImageManager.getInstance().getLrcCachePath(songInfo);
        if(new File(lrc).exists()){
        	return lrc;
        }
        lrc = songInfo.getFileName();
        return lrc.substring(0, lrc.lastIndexOf(".")).trim() + ".lrc".trim(); 
    }
    
    
    /**
	 * 判断是否有足够的空间供下载
	 * 
	 * @param downloadSize
	 * @return
	 */
	@SuppressLint("NewApi")
	public boolean isEnoughForDownload(String dirPath, long downloadSize) {
		StatFs statFs = new StatFs(dirPath);
		Log.e(tag, "可用：" + (statFs.getFreeBytes() / 1024 / 1024) + "M");
		// sd卡分区数
		int blockCounts = statFs.getBlockCount();
		// Log.e("ray", "blockCounts" + blockCounts);
		// sd卡可用分区数
		int avCounts = statFs.getAvailableBlocks();
		// Log.e("ray", "avCounts" + avCounts);
		// 一个分区数的大小
		long blockSize = statFs.getBlockSize();
		// Log.e("ray", "blockSize" + blockSize);
		// sd卡可用空间
		long spaceLeft = avCounts * blockSize;
		Log.e("ray", "spaceLeft" + spaceLeft);
		Log.e("ray", "downloadSize" + downloadSize);
		if (spaceLeft < downloadSize) {
			return false;
		}
		return true;
	}
	
	
	/**
	 * @Description 获取专辑封面
	 * 使用开源库比android 提供的慢
	 * android花20ms 开源库花70hs
	 * @param filePath 文件路径，like XXX/XXX/XX.mp3
	 * @return 专辑封面bitmap
	 */
	public Bitmap createAlbumArt(final String filePath) {
		Utils.getInstance().startTime("获取专辑封面");
//		L.e(tag, "获取专辑封面：" + filePath);
		//---- 使用开源库取
		/*Bitmap bitmap = null;
		Mp3File mp3file;
		try {
			mp3file = new Mp3File(filePath);
			if (mp3file.hasId3v2Tag()) {
				ID3v2 id3v2Tag = mp3file.getId3v2Tag();
				byte[] imageData = id3v2Tag.getAlbumImage();
				if (imageData != null) {
					// String mimeType = id3v2Tag.getAlbumImageMimeType();
					// // Write image to file - can determine appropriate file
					// extension from the mime type
					// RandomAccessFile file = new
					// RandomAccessFile("album-artwork", "rw");
					// file.write(data);
					// file.close();
					bitmap = BitmapFactory.decodeByteArray(imageData, 0,
							imageData.length);
					L.e(tag, "解出专辑图片");
				}
				L.e(tag, "没有专辑图片");
			} else {
				L.e(tag, "不存在Id3v2Tag");
			}
		
		//使用android库解析
		} catch (Exception e) {
			// TODO Auto-generated catch block
			L.e(tag, "解析专辑图片异常");
			e.printStackTrace();
		}*/
		//----
		
	    Bitmap bitmap = null;
	    //能够获取多媒体文件元数据的类
	    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
	    try {
	        retriever.setDataSource(filePath); //设置数据源
	        byte[] embedPic = retriever.getEmbeddedPicture(); //得到字节型数据
	        if(embedPic == null){
	        	return null;
	        }
	        bitmap = BitmapFactory.decodeByteArray(embedPic, 0, embedPic.length); //转换为图片
	    } catch (Exception e) {
	        e.printStackTrace(); 
	    	L.e(tag, "缩略图解析异常：" + filePath);
	    } finally {
	        try {
	            retriever.release();
	        } catch (Exception e2) {
	        	L.e(tag, "缩略图解析异常 释放失败：" + filePath);
	            e2.printStackTrace();
	        }
	    }
	    Utils.getInstance().endUseTime("获取专辑封面");
	    return bitmap;
	}
   
	/**
	 * Drawable → Bitmap
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {

		Bitmap bitmap = Bitmap.createBitmap(

		drawable.getIntrinsicWidth(),

		drawable.getIntrinsicHeight(),

		drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);

		Canvas canvas = new Canvas(bitmap);

		// canvas.setBitmap(bitmap);

		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());

		drawable.draw(canvas);

		return bitmap;

	}
	
	/**
	 * 获得高斯处理后的bitmap，用完请释放,一张图片只许取一张，效果请使用alpha调节
	 * @param photoScale	图片缩小倍数			建议范围 30 ~5
	 * @param radius		圆半径，模糊程度		建议范围 2 ~ 20
	 */
	public Bitmap blurBitmap(Bitmap bit, int photoScale, int radius){
		return BlurUtils.getInstance().getBlurPhoto(bit, radius, radius);
	}
	
	public Bitmap small(Bitmap bitmap, int blurScale) {
		Matrix matrix = new Matrix();
		matrix.postScale(1.0f / blurScale, 1.0f / blurScale); // 长和宽放大缩小的比例
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		return resizeBmp;
	}
	
	/**
	 * 图片灰色处理
	 */
	public Bitmap getGrayBitmap(Bitmap mBitmap) {
		Bitmap mGrayBitmap = Bitmap.createBitmap(mBitmap.getWidth(),
				mBitmap.getHeight(), Config.ARGB_8888);
		Canvas mCanvas = new Canvas(mGrayBitmap);
		Paint mPaint = new Paint();

		// 创建颜色变换矩阵
		ColorMatrix mColorMatrix = new ColorMatrix();
		// 设置灰度影响范围
		mColorMatrix.setSaturation(0);
		// 创建颜色过滤矩阵
		ColorMatrixColorFilter mColorFilter = new ColorMatrixColorFilter(
				mColorMatrix);
		// 设置画笔的颜色过滤矩阵
		mPaint.setColorFilter(mColorFilter);
		// 使用处理后的画笔绘制图像
		mCanvas.drawBitmap(mBitmap, 0, 0, mPaint);
		return mGrayBitmap;
	}
	
	/**
	 * 获取状态栏高度
	 * @return
	 */
	public int getStatusH(){
		return 30;
	}
	
	/**
	 * 设置组件高度为状态栏高度
	 */
	public void updateViewHToStatusH(View view){
		
		ViewGroup.LayoutParams linearParams = (ViewGroup.LayoutParams) view.getLayoutParams(); // 取控件mGrid当前的布局参数
		linearParams.height = Utils.getInstance().getStatusH();// 当控件的高强制设成75象素
		view.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件mGrid2
	}
	
	/**
	 * 检测当的网络（WLAN、3G/2G）状态
	 * @param context Context
	 * @return true 表示网络可用
	 */
	public boolean isEnableNetwork(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (info != null && info.isConnected()) 
			{
				// 当前网络是连接的
				if (info.getState() == NetworkInfo.State.CONNECTED) 
				{
					// 当前所连接的网络可用
					return true;
				}
			}
		}
		return false;
	}
	
	public String codeString(String path) {
		String result = "utf-8";
		File file = new File(path);
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		String text = "";
		try {
			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);
			bis.mark(4);
			byte[] first3bytes = new byte[3];
			// System.out.println("");
			// 找到文档的前三个字节并自动判断文档类型。
			bis.read(first3bytes);
			bis.reset();
			if (first3bytes[0] == (byte) 0xEF && first3bytes[1] == (byte) 0xBB && first3bytes[2] == (byte) 0xBF) {// utf-8
				result = "utf-8";
			} else if (first3bytes[0] == (byte) 0xFF && first3bytes[1] == (byte) 0xFE) {
				result = "unicode";
			} else if (first3bytes[0] == (byte) 0xFE && first3bytes[1] == (byte) 0xFF) {
				result = "utf-16be";
			} else if (first3bytes[0] == (byte) 0xFF && first3bytes[1] == (byte) 0xFF) {
				result = "utf-16le";
			} else {
				result = "GBK";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(bis != null){
				try {
					bis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(bis != null){
				try {
					bis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		L.e(tag, "字符编码  = " + result);
		return result;
	}

	/**
	 * 生成圆形图片
	 * @param bitmap
	 * @return
	 */
	public Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		int srcW = bitmap.getWidth();
		int srcH = bitmap.getHeight();
		if(srcW > srcH){
			srcW = srcH;
		}else{
			srcH = srcW;
		}
		Bitmap output = Bitmap.createBitmap(srcW, srcH/*bitmap.getWidth(), bitmap.getHeight()*/, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, srcW, srcH/*bitmap.getWidth(), bitmap.getHeight()*/);
		final RectF rectF = new RectF(rect);
		final float roundPx = bitmap.getWidth() / 2;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}
	
}
