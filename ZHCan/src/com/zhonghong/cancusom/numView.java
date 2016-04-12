package com.zhonghong.cancusom;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.zhonghong.zhcan.R;

public class numView extends ImageView{
	int numpress[] = { R.drawable.num0press, R.drawable.num1dispress,
			R.drawable.num2press, R.drawable.num3press, R.drawable.num4press,
			R.drawable.num5press, R.drawable.num6press, R.drawable.num7press,
			R.drawable.num8press, R.drawable.num9press };
	int numdispress[] = { R.drawable.num0dispress, R.drawable.num1dispress,
			R.drawable.num2dispress, R.drawable.num3dispress,
			R.drawable.num4dispress, R.drawable.num5dispress,
			R.drawable.num6dispress, R.drawable.num7dispress,
			R.drawable.num8dispress, R.drawable.num9dispress };
	public numView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	private void setimage(int d1,int d2){
		Resources res = getResources(); 
		Bitmap bmp=mergeBitmap(BitmapFactory.decodeResource(res, d1), BitmapFactory.decodeResource(res, d2));
		setBackground(bitmap2Drawable(bmp));
	}
	private void setimage(int d1){
		setBackgroundResource(d1);
	}
	public void setnum(int num){
		if(num>=100){
			return ;
		}
		if(num<10){
			setimage(numpress[num]);
		}else{
			setimage(numpress[num/10],numpress[num%10]);
		}
	}
    private Drawable bitmap2Drawable(Bitmap bitmap) {  
        return new BitmapDrawable(bitmap);  
    } 
	private Bitmap mergeBitmap(Bitmap firstBitmap, Bitmap secondBitmap) {
        Bitmap bitmap = Bitmap.createBitmap(firstBitmap.getWidth()+secondBitmap.getWidth(), firstBitmap.getHeight(),
                firstBitmap.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(firstBitmap, 0,0, null);
        canvas.drawBitmap(secondBitmap, firstBitmap.getWidth(), 0, null);
        return bitmap;
    }
}
