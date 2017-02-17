/**
 * 
 */
package com.example.tt;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.widget.TextView;

/**
 * @author YC
 * @time 2016-8-16 下午4:59:19
 * TODO:
 */
public class MarQueeTextView extends TextView {

	public MarQueeTextView(Context context) {
		super(context);
	}

	public MarQueeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MarQueeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	@ExportedProperty(category = "focus")
	public boolean isFocused() {
		return true;
	}

}
