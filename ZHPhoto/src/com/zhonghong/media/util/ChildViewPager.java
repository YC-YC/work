
package com.zhonghong.media.util;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;

public class ChildViewPager extends ViewPager {

    private ViewPager parentViewPager;

    public ViewPager getParentViewPager() {
        return parentViewPager;
    }

    public void setParentViewPager(ViewPager parentViewPager) {
        this.parentViewPager = parentViewPager;
    }

    public ChildViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public ChildViewPager(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public int getChildPosition()
    {
        return getParentViewPager().getCurrentItem();
    }

    public boolean isOnEnd()
    {
        return getChildPosition() == getParentViewPager().getChildCount() - 1;
    }

    private int abc = 1;
    private float mLastMotionX;
    String TAG = "@";

    private float firstDownX;
    private float firstDownY;
    private boolean flag = false;

    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        final float x = ev.getX();
        final float y = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParentViewPager().requestDisallowInterceptTouchEvent(true);
                abc = 1;
                mLastMotionX = x;
                break;
            case MotionEvent.ACTION_MOVE:
        	
                if (abc == 1) {
                    if (x - mLastMotionX > 5 && getCurrentItem() == 0) {
                        abc = 0;
                        getParentViewPager().requestDisallowInterceptTouchEvent(false);
                    }

                    if (x - mLastMotionX < -5 && getCurrentItem() == getAdapter().getCount() - 1) {
                        abc = 0;
                        getParentViewPager().requestDisallowInterceptTouchEvent(false);
                    }
                }
                break;
                
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParentViewPager().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
