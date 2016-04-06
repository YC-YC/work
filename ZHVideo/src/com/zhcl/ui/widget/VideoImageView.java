/**
 * 
 */
package com.zhcl.ui.widget;

import com.zh.uitls.L;
import com.zhonghong.zhvideo.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 当宽占全屏的时候，用来约束imageView的宽高比
 * @author zhonghong.chenli
 * @date 2015-12-20 下午3:41:22
 */
public class VideoImageView extends ImageView {

	float hScale = 1;
	
	public VideoImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.videoImage);
		hScale = a.getFloat(R.styleable.videoImage_hscale, hScale);
		a.recycle();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = (int) (width * hScale);
		setMeasuredDimension(width, height);

	}

}
