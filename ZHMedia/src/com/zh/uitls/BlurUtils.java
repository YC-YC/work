/**
 * 
 */
package com.zh.uitls;

import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Looper;

import com.zhonghong.blur.BlurJNI;

/** 
 * 高斯模糊工具类
 * @author 陈立 
 * @date 2015-10-18 上午12:22:37 
 *  
 */
public class BlurUtils {
	Context context;
	private static final String tag = "BlurUtils";
	private static BlurUtils mBlurUtils;
	private BlurUtils(){}
	private BlurListener mBlurListener;
	Handler handler;
	public static final BlurUtils getInstance(){
		if(mBlurUtils == null){
			mBlurUtils = new BlurUtils();
		}
		return mBlurUtils;
	}
	
	/**
	 * 监听接收完成高斯模糊的bitmap
	 * @author 陈立
	 */
	public interface BlurListener{
		public void blurComplete(Bitmap bitmap);
	}
	/**
	 * 初始化
	 * @param context
	 */
	public void init(Context context){
		this.context = context;
		handler = new Handler(Looper.getMainLooper());
	}
	
	/**
	 * 设置监听
	 * @param mBlurListener
	 */
	public void setBlurListener(BlurListener mBlurListener){
		this.mBlurListener = mBlurListener;
	}
	
	/**
	 * 开始异步取高斯模糊图片
	 * 获得高斯处理后的bitmap，用完请释放,一张图片只许取一张，效果请使用alpha调节
	 * @param quality		bitmap转字节数组的质量 建议范围 80 ~ 100
	 * @param photoScale	图片缩小倍数			建议范围 30 ~5
	 * @param radius		圆半径，模糊程度		建议范围 2 ~ 20
	 * @author ChenLi
	 */
	public void startGlurPhoto(final Bitmap bitmap, /*final int quality, */final int photoScale, final int radius){
		new Thread(){
			public void run(){
				handler.post(new Thread(){
					public void run(){
						Bitmap result = getBlurPhoto(bitmap,/* quality,*/ photoScale, radius);
						if(mBlurListener != null){
							mBlurListener.blurComplete(result);
						}
					}
				});
			}
		}.start();
	}
	
	
	/**
	 * 获得高斯处理后的bitmap，用完请释放,一张图片只许取一张，效果请使用alpha调节
	 * @param quality		bitmap转字节数组的质量 建议范围 80 ~ 100
	 * @param photoScale	图片缩小倍数			建议范围 30 ~5
	 * @param radius		圆半径，模糊程度		建议范围 2 ~ 20
	 * @author ChenLi
	 */

	public synchronized Bitmap getBlurPhoto(Bitmap bitmap/*, int quality*/, int photoScale, int radius){
//		byte[] data = bitmapToByte(bitmap, quality);
//		Bitmap scalePhoto = decodeBitmap(data, photoScale);
		Bitmap scalePhoto = small(bitmap, photoScale);
		return doBlurJniBitMap(scalePhoto, (int) radius, true);
	}
	
	
	/**
	 * 缩小图片
	 * @return
	 */
	private Bitmap decodeBitmap(byte[] bitdata, int blurScale) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = blurScale;
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeByteArray(bitdata, 0, bitdata.length, options);
	}
	
	
	 private static Bitmap small(Bitmap bitmap, int blurScale) {
		  Matrix matrix = new Matrix(); 
		  matrix.postScale(1.0f / blurScale, 1.0f / blurScale); //长和宽放大缩小的比例
		  Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
		  return resizeBmp;
		 }


	/**
	 * bitmap 转成字节数组
	 * @param bmp
	 * @return
	 */
	private byte[] bitmapToByte(Bitmap bmp, int quality) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();// 初始化一个流对象
		bmp.compress(CompressFormat.JPEG, quality, output);// 把bitmap100%高质量压缩 到  output对象里
		bmp.recycle();// 自由选择是否进行回收
		byte[] result = output.toByteArray();// 转换成功了
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	/**
	 * 利用jni高斯处理
	 */
   private Bitmap doBlurJniBitMap(Bitmap sentBitmap, int radius, boolean canReuseInBitmap) {
        Bitmap bitmap;
        if (canReuseInBitmap) {
            bitmap = sentBitmap;
        } else {
            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        }
        if (radius < 1) {
            return (null);
        }
        //Jni BitMap
        BlurJNI.blurBitMap(bitmap, radius);

        return (bitmap);
    }

}
