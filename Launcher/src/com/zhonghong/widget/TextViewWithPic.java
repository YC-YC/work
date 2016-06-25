/**
 * 
 */
package com.zhonghong.widget;

import java.util.Map;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author YC
 * @time 2016-3-24 下午7:30:52
 */
public class TextViewWithPic extends View {

	private Map<String, Integer> mCharMap;
	
	public TextViewWithPic(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setText(String text, Map<String, Integer> charMap)
	{
		mCharMap = charMap;
	}

}
