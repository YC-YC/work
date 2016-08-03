/**
 * 
 */
package com.zhonghong.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * @author YC
 * @time 2016-6-27 下午8:50:31
 * TODO:叠加布局，实现两个图片叠加，要求background的大小比src的大
 */
public class OverlapLayout extends ImageView{

	private Context mContext;
	
	public OverlapLayout(Context context) {
		this(context, null);
	}
	
	public OverlapLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public OverlapLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	}
	
	public void setImageResource(int resId){
		Drawable src = mContext.getResources().getDrawable(resId);
		int width = src.getIntrinsicWidth();
		int height = src.getIntrinsicHeight();
		Drawable background = getBackground();
		if (background != null)
		{
			int intrinsicWidth = background.getIntrinsicWidth();
			int intrinsicHeight = background.getIntrinsicHeight();
			int leftpadding = (intrinsicWidth - width)/2;
			int toppadding = (intrinsicHeight - height)/2;
			if (leftpadding >= 0 && toppadding >= 0){
				setPadding(leftpadding, toppadding, leftpadding, toppadding);
			}
		}
		super.setImageDrawable(src);
	}


}
