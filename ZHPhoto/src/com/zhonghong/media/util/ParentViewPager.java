package com.zhonghong.media.util;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;

public class ParentViewPager extends ViewPager{

    private ViewPager childViewPager;
    
    public ViewPager getChildViewPager() {
        return childViewPager;
    }

    public void setChildViewPager(ViewPager childViewPager) {
        this.childViewPager = childViewPager;
    }

    public ParentViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }
    
    public ParentViewPager(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }
    
    public int getChildPosition()
    {
        return getChildViewPager().getCurrentItem();
    }
    
    public boolean isChildOnEnd()
    {
        return getChildPosition() == getChildViewPager().getChildCount()-1; 
    }
    
    float mLastMotionX;
    float mLastMotionY;
    
    int TOUCH_STATE_NULL = -1;
    int TOUCH_STATE_REST = 0;
    int TOUCH_STATE_PARENT_SCOLLING = 1;
    int mTouchState = TOUCH_STATE_NULL;
    
    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        // TODO Auto-generated method stub
        
        final int action = arg0.getAction();
        final float x = arg0.getX();
        final float y = arg0.getY();
        
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = x;
                break;

            case MotionEvent.ACTION_MOVE:
                
                //child               
            	if(isChildOnEnd() && mLastMotionX > x)
                {
                    mTouchState = TOUCH_STATE_PARENT_SCOLLING;
                }
                mLastMotionX = x;
                break;

            case MotionEvent.ACTION_UP://
//                mTouchState = TOUCH_STATE_REST;
                break;

            case MotionEvent.ACTION_CANCEL:
//                mTouchState = TOUCH_STATE_REST;
                break;
        }
        return true;
    }
}
