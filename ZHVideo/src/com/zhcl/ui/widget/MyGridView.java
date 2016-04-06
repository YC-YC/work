/**
 * 
 */
package com.zhcl.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/** 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author zhonghong.chenli
 * @date 2016-1-11 下午11:06:29 
 */
public class MyGridView extends GridView {
	
	private boolean isOnMeasure;
	public MyGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		isOnMeasure = true;
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		isOnMeasure = false;
		super.onLayout(changed, l, t, r, b);
	}

	public boolean isOnMeasure() {
		return isOnMeasure;
	}
	
	
	

}
