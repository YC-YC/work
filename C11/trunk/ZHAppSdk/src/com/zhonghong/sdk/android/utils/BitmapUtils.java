/**
 * Copyright (c) 深圳市众鸿科技股份有限公司
 * @file_name BitmapUtils.java
 * @class com.zhonghong.sdk.android.utils.BitmapUtils
 * @create 上午11:47:53
 */
package com.zhonghong.sdk.android.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * 图片工具类
 * <p></p>
 * 上午11:47:53
 *
 * @author ZH-SW-Lilc
 * @version 1.0.0
 */
public class BitmapUtils {
	
	/**
	 * 获得圆角图片的方法
	 * 
	 * @param bitmap 原始图片
	 * @return 处理后的圆角图片
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		int srcW = bitmap.getWidth();
		int srcH = bitmap.getHeight();
		if(srcW > srcH){
			srcW = srcH;
		}else {
			srcH = srcW;
		}

		Bitmap output = Bitmap.createBitmap(srcW,srcH, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, srcW, srcH);
		final RectF rectF = new RectF(rect);
		final float roundPx = bitmap.getWidth() / 2;

		paint.setAntiAlias(true);
		//canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}
}
