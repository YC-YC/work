package com.zhonghong.media.util;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

public class MarqueeTextView extends TextView {

	public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }
    
    public MarqueeTextView(Context context) {
        // TODO Auto-generated constructor stub
        super(context);
    }
    
    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public boolean isFocused() {
        // TODO Auto-generated method stub
        return true;
    }
    
    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        // TODO Auto-generated method stub
    }

}
