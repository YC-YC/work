package com.zhcl.ui.widget;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.zhcl.Adapter.AllVideoListAdapter;

/**
 * @author YC
 * @time 2016-3-29 上午11:23:52
 */
public class MyHorizontalScollView extends HorizontalScrollView 
implements OnClickListener {

	private AllVideoListAdapter mAdapter;
    private OnItemClickListener mOnClickListener;
    private CurrentImageChangeListener mListener;
    /**HorizontalListView中的LinearLayout*/
    private LinearLayout mContainer;
    
    /**屏幕宽度*/
    private int mScreenWidth;
    
    /**item的宽度*/
    private int mChildWidth;
    /**item的高度*/
    private int mChildHeight;
    
    /**每屏item个数*/
    private int mCountOneScreen;
    
    /**第一张显示item的下标*/
    private int mFirstItemIndex;
    
    /**最后一张加载的item的下标*/
    private int mLastItemIndex;
  
    
    /**保存item项与View的表*/
    private Map<View, Integer> mPosMap = new HashMap<View, Integer>();
	
    public interface OnItemClickListener  
    {  
    	void onClick(View view, int pos);  
    }  
    
    /** 图片滚动时的回调接口 */  
    public interface CurrentImageChangeListener  
    {  
        void onCurrentImgChanged(int position, View viewIndicator);  
    }
    
	public MyHorizontalScollView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	public MyHorizontalScollView(Context context) {
		this(context, null);
	}
	public MyHorizontalScollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mContainer = (LinearLayout) getChildAt(0);
	}
	
	public void setAdapter(AllVideoListAdapter adapter){
		mAdapter = adapter;
		if (mAdapter.getCount() <= 0)
		{
			return;
		}
		mContainer = (LinearLayout) getChildAt(0);
		
		//添加第一个View
		final View view  = adapter.getView(0, null, mContainer);
		mContainer.addView(view);
		
		if (mChildWidth == 0 && mChildHeight == 0)
		{
			int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
			int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
			
			view.measure(w, h);
			
			mChildWidth = view.getMeasuredWidth();
			mChildHeight = view.getMeasuredHeight();
			
			//加载多加两个
			mCountOneScreen = mScreenWidth/mChildWidth + 2;
		}
		
		initFirstScreenChildern(mCountOneScreen);
	}
	
	/**
	 * 初始化第一屏幕的元素
	 * @param mCountOneScreen2
	 */
	private void initFirstScreenChildern(int countOneScreen) {
		mContainer = (LinearLayout) getChildAt(0);  
        mContainer.removeAllViews();  
        
        mPosMap.clear();
        
        for (int i = 0; i < countOneScreen && i < mAdapter.getCount(); i++)
        {
        	View view = mAdapter.getView(i, null, mContainer);
        	
        /*	if ( i == countOneScreen/2)
        	{
        		int width = view.getWidth();
        		int height = view.getHeight();
        		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)(width*1.5f), (int)(height*1.5f));
        		view.setLayoutParams(params);
        	}*/
        	
        	view.setOnClickListener(this);
        	mContainer.addView(view);
        	mPosMap.put(view, i);
        	mLastItemIndex = i;
        }
        
        //回调  
        if (mListener != null)  
        {  
            notifyCurrentImgChanged();  
        }  
        
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		switch (ev.getAction()) {
		case MotionEvent.ACTION_MOVE:
			int scrollX = getScrollX();
			if (scrollX >= mChildWidth)  
            {  
                loadNextImg();  
            }  
            // 如果当前scrollX = 0， 往前设置一张，移除最后一张  
            if (scrollX == 0)  
            {  
                loadPreImg();  
            }  
			break;

		}
		return super.onTouchEvent(ev);
	}
	
	
	
	
	/**
	 * 加载下一张图片 
	 */
	private void loadNextImg() {
		if (mLastItemIndex == mAdapter.getCount() - 1)
		{
			return;
		}
		
		//移除第一张图片，且将水平滚动位置置0  
		
		scrollTo(0, 0);
		mPosMap.remove(mContainer.getChildAt(0));
		mContainer.removeViewAt(0);
		
		//获取下一张图片，并且设置onclick事件，且加入容器中  
        View view = mAdapter.getView(++mLastItemIndex, null, mContainer);  
        view.setOnClickListener(this);  
        mContainer.addView(view);  
        mPosMap.put(view, mLastItemIndex);
        
      //当前第一张图片小标  
        mFirstItemIndex++; 
        
      //如果设置了滚动监听则触发  
        if (mListener != null)  
        {  
            notifyCurrentImgChanged();  
        } 
	}
	/**
	 * 加载前一张图片
	 */
	private void loadPreImg() {
		if (mFirstItemIndex == 0)
		{
			return;
		}
		
		//获得当前应该显示的第一张图片的下标
		int index = mLastItemIndex - mCountOneScreen;
		if (index >= 0)
		{
			// 移除最后一张
			int oldPos = mContainer.getChildCount() - 1;
			mPosMap.remove(mContainer.getChildAt(oldPos));
			mContainer.removeViewAt(oldPos);

			// 将此View放入第一个位置
			View view = mAdapter.getView(index, null, mContainer);
			mPosMap.put(view, index);
			mContainer.addView(view, 0);
			view.setOnClickListener(this);
			// 水平滚动位置向左移动view的宽度个像素
			scrollTo(mChildWidth, 0);
			// 当前位置--，当前第一个显示的下标--
			mLastItemIndex--;
			mFirstItemIndex--;
			
			 //回调  
            if (mListener != null)  
            {  
                notifyCurrentImgChanged();  
            }  
		}
		
	}
	
	/** 
     * 滑动时的回调 
     */  
    @SuppressLint("NewApi")
	public void notifyCurrentImgChanged()  
    {  
        //先清除所有的背景色，点击时会设置为蓝色  
        for (int i = 0; i < mContainer.getChildCount(); i++)  
        {  
            mContainer.getChildAt(i).setBackgroundColor(Color.BLACK);
            mContainer.getChildAt(i).setScaleX(1.0f);
            mContainer.getChildAt(i).setScaleY(1.0f);
        }  
        mListener.onCurrentImgChanged(mFirstItemIndex+2, mContainer.getChildAt(0+2));  
  
    }
	
	public void setOnItemClickListener(OnItemClickListener mOnClickListener)  
    {  
        this.mOnClickListener = mOnClickListener;  
    }
	
	public void setCurrentImageChangeListener(CurrentImageChangeListener mListener)  
    {  
        this.mListener = mListener;  
    }  
	
	@Override
	public void onClick(View v) {
		if (mOnClickListener != null)
		{
			for (int i = 0; i < mContainer.getChildCount(); i++)
			{
				mContainer.getChildAt(i).setBackgroundColor(Color.WHITE);
			}
			mOnClickListener.onClick(v, mPosMap.get(v));
		}
	}  
	

}
