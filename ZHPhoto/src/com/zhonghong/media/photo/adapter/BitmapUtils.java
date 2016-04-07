package com.zhonghong.media.photo.adapter;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

public class BitmapUtils {

	/**
	 * 生成圆形图片
	 * @param bitmap
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		int srcW = bitmap.getWidth();
		int srcH = bitmap.getHeight();
		if(srcW > srcH){
			srcW = srcH;
		}else{
			srcH = srcW;
		}
		Bitmap output = Bitmap.createBitmap(srcW, srcH/*bitmap.getWidth(), bitmap.getHeight()*/, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242; //0xffffffff  0xff424242
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, srcW, srcH/*bitmap.getWidth(), bitmap.getHeight()*/);
		final RectF rectF = new RectF(rect);
		final float roundPx = bitmap.getWidth() / 2;

		paint.setAntiAlias(true);
		//canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, null, rect, paint);
		//bitmap.recycle();
		return output;
	}
	
}
