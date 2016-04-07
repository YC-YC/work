/**
 * 
 */
package com.zhcl.ui.widget;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.zh.uitls.Utils;
import com.zhonghong.zhvideo.R;

/**
 * @author YC
 * @time 2016-4-1 上午9:36:07
 */
public class HorizontalList extends ViewGroup {

	private String TAG = getClass().getSimpleName();

	/**Item之间的间距*/
	private int mItemPadding = 0;
	/**显示的Item个数*/
	private int mShowItemNum = 5;
	/**中间一个放大的位数*/
	private final float mMidScale = 1.5f;
	/**适配器*/
	private BaseAdapter mAdapter;
	
	/**Item的宽高*/
	private int mItemWidth, mItemHeight;
	
	/**整个View的宽高*/
	private int mWidth, mHeight;
	
	/**中间放大Item的索引及其与布局中间的偏移*/
	private int mMidSelItemIndex = 0, mMidItemOffset = 0;
	
	
	/**保存item项与View的表*/
    private Map<View, Integer> mPosMap = new HashMap<View, Integer>();
	
	
	public HorizontalList(Context context) {
		this(context, null);
	}
	public HorizontalList(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	public HorizontalList(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		//无视xml中的padding的布局
		setPadding(0, 0, 0, 0);
	}
	
	public interface ItemOnClickListener{
		  void onItemClick(AdapterView<?> parent, View view, int position);
	}
	private ItemOnClickListener mOnItemClickListener;
	/** 设置Item的点击事件接口 */
	public void setItemOnClickListener(ItemOnClickListener onItemClickListener) {
		mOnItemClickListener = onItemClickListener;
	}
	
	public interface ItemPlayPauseOnClickListener{
		  void onItemPlayPause(View view, int position);
	}
	private ItemPlayPauseOnClickListener mItemPlayPauseOnClickListener;
	/** 设置Item的点击事件接口 */
	public void setItemPlayPauseOnClickListener(ItemPlayPauseOnClickListener itemPlayPauseOnClickListener) {
		mItemPlayPauseOnClickListener = itemPlayPauseOnClickListener;
	}
	
	public interface CurSelItemChangeCallback{
		void onItemChange(int oldItem, int curItem);
	}
	
	private CurSelItemChangeCallback mCurSelItemChangeCallback;
	public void setCurSelItemChangeCallback(CurSelItemChangeCallback callback)
	{
		mCurSelItemChangeCallback = callback;
	}
	
	public void setAdapter(BaseAdapter adapter) {
		//TODO setAdapter
		Log.i(TAG, "setAdapter size = " + adapter.getCount());
		mAdapter = adapter;
		addItems();
//		getItemWH();
		mMidSelItemIndex = getInitMideSelItemIndex();
		mMidItemOffset = 0;
		requestLayout();
	}
	
	/** 设置播放暂停状态按键的状态 */
	public void setPlayPauseState(int[] items, boolean[] states)
	{
		for (int i = 0; i < getChildCount(); i++)
		{
			boolean bSet = false;
			ImageView playpause = (ImageView) getChildAt(i).findViewById(R.id.itemplaypause);
			if (items != null && states != null)
			{
				for (int j = 0; j < items.length; j++)
				{
					if (i == items[j])
					{
						playpause.setVisibility(View.VISIBLE);
	//					playpause.setSelected(states[j]);
						if (states[j])
						{
							playpause.setImageResource(R.drawable.item_play_selector);
						}
						else
						{
							playpause.setImageResource(R.drawable.item_pause_selector);	
						}
						bSet = true;
						break;
					}
				}
			}
			
			if (!bSet)
			{
				playpause.setVisibility(View.GONE);
			}
		}
		Log.i(TAG, "setPlayPauseState");
	}
	
	/** 获取默认的中间显示Item的Index*/
	private int getInitMideSelItemIndex() {
		return mAdapter.getCount() < mShowItemNum/2 ? 0 : mShowItemNum/2;
	}
	
	/** 获取Item的宽高 */
	private void getItemWH() {
		if (mItemWidth == 0 || mItemHeight == 0)
		{
			/**因为Item为一样大小，所以只测量一个*/
			Log.i(TAG , "getChildCount = " + getChildCount());
			if (getChildCount() > 0)
			{
				View view = getChildAt(0);
				mItemWidth = view.getMeasuredWidth();
				mItemHeight = view.getMeasuredHeight();
				Log.i(TAG , "getItem w = " + mItemWidth + ", h = " + mItemHeight);
			}
		}
	}
	/** 添加Item */
	private void addItems() {
		removeAllViews();
		for (int i = 0; i < mAdapter.getCount(); i++)
		{
			final int j = i;
			View view = mAdapter.getView(i, null, null);
			addView(view);
			view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (mOnItemClickListener != null)
					{
						mOnItemClickListener.onItemClick(null, v, j);
					}
				}
			});
			
			view.findViewById(R.id.itemplaypause).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (mItemPlayPauseOnClickListener != null)
					{
						mItemPlayPauseOnClickListener.onItemPlayPause(v, j);
					}
				}
			});
		}
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//TODO onMeasure
		Log.i(TAG, "onMeasure");
//		Utils.getInstance().startTime("onMeasure");
		measureChild();
		getItemWH();
		
		mWidth = 0;
		mHeight = 0;
		
		mWidth = (int) (mItemWidth * (mShowItemNum - 1) + mItemWidth*mMidScale + mItemPadding * (mShowItemNum+1));
		mHeight = (int) (mItemHeight * mMidScale);
		Log.i(TAG, "onMeasure mWidth = " + mWidth + ", mHeight = " + mHeight);
		setMeasuredDimension(mWidth, mHeight);
		
	}
	
	/**必需测量子View，否则获取不到wh*/
	private void measureChild() {
		for (int i = 0 ; i < getChildCount() ; i++){
			View childView = getChildAt(i);
			childView.measure(0, 0);
		}
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		//TODO onLayout
//		Log.i(TAG, "onLayout mMidItemOffset = " + mMidItemOffset);
		if (getChildCount() <= 0)
		{
			return;
		}
//		Utils.getInstance().startTime("onLayout");
		final float midItemScale = getScaleByOffset(mMidItemOffset);
		/**左或右元素放大倍数*/
		final float leftRightScale = 1.0f + mMidScale - midItemScale;	
//		Log.i(TAG, "onLayout midItemScale = " + midItemScale + ", leftRightScale = " + leftRightScale);
		
		int left = 0;
		final int top = mHeight/2 - mItemHeight/2;
		/**布局选中项*/
		left = mWidth/2 + mMidItemOffset;
		left -= mItemWidth/2;
		
//		Log.i(TAG, "onLayout mMidSelItemIndex = " + mMidSelItemIndex + ", mMidItemOffset = "  + mMidItemOffset);
		View view = getChildAt(mMidSelItemIndex);
//		Log.i(TAG, "onLayout MidItem left = " + left);
//		Log.i(TAG , "onLayout mItemWidth = " + mItemWidth + ", mItemHeight = " + mItemHeight);
		
		view.layout(left, top, left+mItemWidth, top+mItemHeight);
		view.setScaleX(midItemScale);
		view.setScaleY(midItemScale);
		
		/**布局左边项*/
		int index = mMidSelItemIndex-1;
		left = (int) (left - mItemWidth*(midItemScale - 1)/2);
		if (mMidItemOffset <= 0)
		{
			/**布局左边全部正常显示的Item*/
			for (int i = 0; i < mShowItemNum/2  && index >= 0; i++,index--)
			{
				left -= (mItemWidth + mItemPadding);
				view = getChildAt(index);
				view.layout(left, top, left+mItemWidth, top+mItemHeight);
				view.setScaleX(1.0f);
				view.setScaleY(1.0f);
//				Log.i(TAG, "onLayout LeftItem " + i + " left = " + left);
			}
		}
		else
		{
			
			if (index >= 0)
			{
				/**布局左边第一个放大的Item*/
				left -= (mItemPadding + mItemWidth*leftRightScale - mItemWidth*(leftRightScale - 1.0f)/2);
				view = getChildAt(index);
				view.layout(left, top, left+mItemWidth, top+mItemHeight);
				view.setScaleX(leftRightScale);
				view.setScaleY(leftRightScale);
//				Log.i(TAG, "onLayout LeftScale 1 left = " + left);
				index--;
				left -= (mItemWidth*(leftRightScale - 1.0f)/2);
				/**布局左边第二个开始的Item*/
				for (int i = 0; i < mShowItemNum/2 && index >= 0; i++,index--)
				{
					left -= (mItemWidth + mItemPadding);
					view = getChildAt(index);
					view.layout(left, top, left+mItemWidth, top+mItemHeight);
					view.setScaleX(1.0f);
					view.setScaleY(1.0f);
//					Log.i(TAG, "onLayout LeftItem " + i + " left = " + left);
				}
			}
		}
		
		/**布局右边项*/
		index = mMidSelItemIndex + 1;
		left = mWidth/2 + mMidItemOffset;
		left += (mItemWidth*midItemScale)/2;
		if (mMidItemOffset >= 0)
		{
			/**布局右边全部正常显示的Item*/
			for (int i = 0; i < mShowItemNum/2  && index < getChildCount(); i++,index++)
			{
				left += mItemPadding;
				view = getChildAt(index);
				view.layout(left, top, left+mItemWidth, top+mItemHeight);
				view.setScaleX(1.0f);
				view.setScaleY(1.0f);
				left += mItemWidth;
			}
		}
		else
		{
			if (index < getChildCount())
			{
				/**布局右边第一个放大的Item*/
				left += mItemPadding + mItemWidth*(leftRightScale-1.0f)/2;
				view = getChildAt(index);
				view.layout(left, top, left+mItemWidth, top+mItemHeight);
				view.setScaleX(leftRightScale);
				view.setScaleY(leftRightScale);
				index++;
				left += mItemWidth + mItemWidth*(leftRightScale - 1.0f)/2;
				/**布局右边第二个开始的Item*/
				for (int i = 0; i < mShowItemNum/2  && index < getChildCount(); i++,index++)
				{
					left += mItemPadding;
					view = getChildAt(index);
					view.layout(left, top, left+mItemWidth, top+mItemHeight);
					view.setScaleX(1.0f);
					view.setScaleY(1.0f);
					left += mItemWidth;
				}
			}
		}
		
		showView();
		
		setItemEvent();
		
//		Utils.getInstance().endUseTime("onLayout");
//		Utils.getInstance().endUseTime("onMeasure");
	}
	
	/**设置Item事件*/
	private void setItemEvent() {
		for (int i = 0; i < getChildCount(); i++)
		{
			View view = getChildAt(i);
			if (i == mMidSelItemIndex)
			{
				view.setClickable(true);
				view.setSelected(true);
			}
			else
			{
				view.setClickable(false);
				view.setSelected(false);
			}
		}
	}
	/** 显示和隐藏Item项 */
	private void showView() {
		int startShowIndex = 0;
		int endShowIndex = 0;
		
		if (mMidItemOffset >= 0)
		{
			startShowIndex = mMidSelItemIndex - (mShowItemNum/2 + 1);
			if (startShowIndex < 0)
			{
				startShowIndex = 0;
			}
			
			endShowIndex = mMidSelItemIndex + mShowItemNum/2;
			if (endShowIndex > getChildCount() - 1)
			{
				endShowIndex = getChildCount() - 1;
			}
		}
		else
		{
			startShowIndex = mMidSelItemIndex - mShowItemNum/2;
			if (startShowIndex < 0)
			{
				startShowIndex = 0;
			}
			
			endShowIndex = mMidSelItemIndex + (mShowItemNum/2 + 1);
			if (endShowIndex > getChildCount() - 1)
			{
				endShowIndex = getChildCount() - 1;
			}
		}
//		Log.i(TAG, "showView start = " + startShowIndex + ", end = " + endShowIndex);
		for (int i = 0; i < getChildCount(); i++)
		{
			if (i >= startShowIndex && i <= endShowIndex)
			{
				getChildAt(i).setVisibility(View.VISIBLE);
			}
			else
			{
				getChildAt(i).setVisibility(View.GONE);
			}
		}
	}
	/**根据偏移量计算放大值*/
	private float getScaleByOffset(int offset) {
		float scale = 1.0f;
		int totalOffset = (int) (mItemWidth + (mMidScale - 1.0f)*mItemWidth + mItemPadding);
		scale = 1.5f - 0.5f * Math.abs(offset)/totalOffset;
		if (scale > 1.5f)
		{
			scale = 1.5f;
		}
		else if (scale < 1.0f)
		{
			scale = 1.0f;
		}
		return scale;
		
	}
	
	private int mLastX = 0;
	/**	检测按下到抬起时使用的时间*/
	private long mDownTime = 0;
	private int mDownX = 0;
	/**当每秒移动角度达到该值时，认为是快速移动 */
	private final int FLINGABLE_VALUE = 100;
	/**是否在滚动*/
	private boolean mIsFling;
	/**自动滚动的Runnable*/
	private AutoFlingRunnable mFlingRunnable;
	
	/** 后退处理，防止停止时滚动太快 */
	private boolean isMoveBack;
	private MoveBackRunnable mMoveBackRunnable;
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		//TODO dispatchTouchEvent
		Log.i(TAG, "dispatchTouchEvent action = " + event.getAction() + ", x = " + event.getX());
		switch (event.getAction()) {
		
		case MotionEvent.ACTION_DOWN:
			mLastX = (int) event.getX();
			mDownX = (int) event.getX();
			mDownTime = SystemClock.elapsedRealtime();
			if (isMoveBack)// 如果当前已经在回退  
			{
				removeCallbacks(mMoveBackRunnable);
				isMoveBack = false;
			}
			if (mIsFling)
			{
				mIsFling = false;
				removeCallbacks(mFlingRunnable);
				return true;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			int x = (int) event.getX();
			mMidItemOffset += (x - mLastX);
			checkOffset();
			checkBorder();
			mLastX = x;
//			Log.i(TAG, "move mOffset = " + mOffset);
			requestLayout();
			break;
		case MotionEvent.ACTION_UP:
			float speed = (event.getX() - mDownX)*1000/(SystemClock.elapsedRealtime() - mDownTime);
			Log.i(TAG, "onTouchEvent speed = " + speed);
			if (Math.abs(speed) > FLINGABLE_VALUE && !mIsFling)
			{
				 post(mFlingRunnable = new AutoFlingRunnable(speed));  
	             return true;  
			}
			else
			{
				if (Math.abs(mMidItemOffset) > 0)
				{
					isMoveBack = true;
					post(mMoveBackRunnable = new MoveBackRunnable(getStopOffset(mMidItemOffset)));
				}
			}
			break;
		}
		super.dispatchTouchEvent(event);
		return true;
	}
	
	/**校验偏移值,每次修改mMidItemOffset时需要调用*/
	private void checkOffset() {
		/**正负标识*/
		final int flag = mMidItemOffset > 0 ? 1: -1;
		final int itemOffset = (int) (mItemWidth + (mMidScale - 1.0f)*mItemWidth/2 + mItemPadding);
//		Log.i(TAG, "checkOffset ++++++++++ mMidItemOffset = " + mMidItemOffset);
		while ( Math.abs(mMidItemOffset) > itemOffset )
		{
			mMidItemOffset = flag * (Math.abs(mMidItemOffset) - itemOffset);
			final int oldItem = mMidSelItemIndex;
			if (flag > 0)
			{
				//TODO LoadPre
				mMidSelItemIndex--;
				if (mMidSelItemIndex < 0)
				{
					mMidSelItemIndex = 0;
				}
			}
			else
			{
				//TODO LoadNext
				mMidSelItemIndex++;
				if (mMidSelItemIndex > getChildCount() - 1)
				{
					mMidSelItemIndex = getChildCount() - 1;
				}
			}
			if (mCurSelItemChangeCallback != null)
			{
				mCurSelItemChangeCallback.onItemChange(oldItem, mMidSelItemIndex);
			}
		}
//		Log.i(TAG, "checkOffset ---------- mMidItemOffset = " + mMidItemOffset);
	}
	
	/**
	 * 检测边界，处理第一个和最后一个
	 */
	private boolean checkBorder()
	{
		if ((mMidSelItemIndex == getChildCount() - 1) && mMidItemOffset < 0)
		{
			mMidItemOffset = 0;
			return true;
		}
		
		if ((mMidSelItemIndex == 0) && mMidItemOffset > 0)
		{
			mMidItemOffset = 0;
			return true;
		}
		return false;
	}

	private class AutoFlingRunnable implements Runnable
	{

		private final int STOP_SPEED = 20;
		private float mSpeed;
		

		public AutoFlingRunnable(float speed)
		{
			this.mSpeed = speed;
		}

		public void run()
		{
			if (Math.abs(mSpeed) < STOP_SPEED)
			{
				mIsFling = false;
				isMoveBack = true;
				post(mMoveBackRunnable = new MoveBackRunnable(getStopOffset(mMidItemOffset)));
				return;
			}
			mIsFling = true;
			// 不断改变mMidItemOffset，让其滚动，/30为了避免滚动太快
			mMidItemOffset += (mSpeed / 30);
			// 逐渐减小这个值
			mSpeed /= 1.0666F;
			checkOffset();
			if (checkBorder())
			{
				mIsFling = false;
			}
			else
			{
				postDelayed(this, 30);
			}
			// 重新布局
			requestLayout();
		}
	}
	
	/** 获取停止时需要调整的偏移 */
	private int getStopOffset(int offset) {
		final int ITEM_OFFSET = (int) (mItemWidth + mItemPadding + (mMidScale - 1.0f)*mItemWidth/2);
		
		int result = 0;
		if (Math.abs(offset) > ITEM_OFFSET/2)
		{
			result = (ITEM_OFFSET - Math.abs(offset)%ITEM_OFFSET)*(offset > 0? 1: -1);	
		}
		else
		{
			result = -1*offset;
		}
		return result;
	}
	
	/**
	 * 回退任务
	 * @author YC
	 * @time 2016-4-5  14:01:36
	 */
	private class MoveBackRunnable implements Runnable {
		private static final int SPEED_DIFF = 2;
		private int mOffset;
		
		/** @param offset 需要偏移的值 */
		public MoveBackRunnable(int offset){
			mOffset = offset;
		}
		public void run() {
//			Log.i(tag, "222222 run mStartAngle = " + mStartAngle + ", mEndAngle = " + mEndAngle + ", mOffsetAngle = " + mOffsetAngle);
			if (Math.abs(mOffset) < SPEED_DIFF)
			{
				upsetToNormalItem();
				isMoveBack = false;
				return;
			}
			
			if (mOffset > 0)
			{
				mMidItemOffset += SPEED_DIFF;
				mOffset -= SPEED_DIFF;
			}
			else
			{
				mMidItemOffset -= SPEED_DIFF;
				mOffset += SPEED_DIFF;
			}
			postDelayed(this, 1);
			checkOffset();
			// 重新布局
			requestLayout();
		}
	}

	/**恢复到正常显示状态 */
	public void upsetToNormalItem() {
		final int itemOffset = (int) (mItemWidth + (mMidScale - 1.0f)*mItemWidth/2 + mItemPadding);
		final int DIFF = 10;
		final int oldItem = mMidSelItemIndex;
		if ( mMidItemOffset < -1 * itemOffset + DIFF)
		{
			mMidItemOffset = 0;
			//TODO LoadNext
			mMidSelItemIndex++;
			if (mMidSelItemIndex > getChildCount() - 1)
			{
				mMidSelItemIndex = getChildCount() - 1;
			}
			
			if (mCurSelItemChangeCallback != null)
			{
				mCurSelItemChangeCallback.onItemChange(oldItem, mMidSelItemIndex);
			}
		}
		else if ( mMidItemOffset > itemOffset - DIFF)
		{
			mMidItemOffset = 0;
			//TODO LoadPre
			mMidSelItemIndex--;
			if (mMidSelItemIndex < 0)
			{
				mMidSelItemIndex = 0;
			}
			
			if (mCurSelItemChangeCallback != null)
			{
				mCurSelItemChangeCallback.onItemChange(oldItem, mMidSelItemIndex);
			}
		}
		else
		{
			mMidItemOffset = 0;
		}
		requestLayout();
	}
	
	
}
