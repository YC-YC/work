/**
 * 
 */
package com.zhonghong.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ProgressBar;

import com.zhonghong.launcher.R;

/**
 * @author YC
 * @time 2016-5-22 下午4:14:18
 */
public class RoundProgressbar extends ProgressBar {

	private static final int DEFAULT_REACH_PROGRESS_BAR_HEIGHT = 2;
	private static final int DEFAULT_REACH_PROGRESS_BAR_COLOR = 0xFFD3D6DA;
	private static final String TAG = "RoundProgressbar";
	/**半径*/
	private int mRadius = dp2px(30);
	private int mReachProgressbarHeight = dp2px(DEFAULT_REACH_PROGRESS_BAR_HEIGHT);
	private int mReachProgressBarColor = DEFAULT_REACH_PROGRESS_BAR_COLOR;
	private int mUnReachProgressbarHeight = dp2px(DEFAULT_REACH_PROGRESS_BAR_HEIGHT);
	private int mUnReachProgressBarColor = DEFAULT_REACH_PROGRESS_BAR_COLOR;
	private int mProgressbarTextSize = dp2px(DEFAULT_REACH_PROGRESS_BAR_HEIGHT);
	private int mProgressBarTextColor = DEFAULT_REACH_PROGRESS_BAR_COLOR;
	
	private String mText;
	
	protected Paint mPaint = new Paint();
	
	public RoundProgressbar(Context context) {
		this(context, null);
	}

	public RoundProgressbar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public RoundProgressbar(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		
		getStyleAttributes(attrs);
		
//		mPaint.setStyle(Paint.Style.STROKE);  
//        mPaint.setAntiAlias(true);  
//        mPaint.setDither(true);  
//        mPaint.setStrokeCap(Cap.SQUARE);
	}

	/**
	 * 获取自定义属性
	 */
	private void getStyleAttributes(AttributeSet attrs) {
		final TypedArray ta = getContext().obtainStyledAttributes(attrs, 
				R.styleable.RoundProgress);
		
		mRadius = (int) ta.getDimension(R.styleable.RoundProgress_roundprogress_radius, 
				mRadius);
		
		mReachProgressBarColor = ta.getColor(R.styleable.RoundProgress_roundprogress_reach_color, 
				mReachProgressBarColor);
		mReachProgressbarHeight = (int) ta.getDimension(R.styleable.RoundProgress_roundprogress_reach_bar_height, 
				mReachProgressbarHeight);
		mUnReachProgressBarColor = ta.getColor(R.styleable.RoundProgress_roundprogress_unreach_color, 
				mUnReachProgressBarColor);
		mUnReachProgressbarHeight = (int) ta.getDimension(R.styleable.RoundProgress_roundprogress_unreach_bar_height, 
				mUnReachProgressbarHeight);
		mProgressBarTextColor = ta.getColor(R.styleable.RoundProgress_roundprogress_text_color, 
				mProgressBarTextColor);
		mProgressbarTextSize = (int) ta.getDimension(R.styleable.RoundProgress_roundprogress_text_size, 
				mProgressbarTextSize);
		
		ta.recycle();
	}
	
	@Override
	protected synchronized void onMeasure(int widthMeasureSpec,
			int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);

		final int paintWidth = mReachProgressbarHeight;

		if (widthMode != MeasureSpec.EXACTLY) {
			final int exceptWidth = getPaddingLeft() + getPaddingRight() + 2
					* mRadius + 2 * paintWidth;
			widthMeasureSpec = MeasureSpec.makeMeasureSpec(exceptWidth,
					MeasureSpec.EXACTLY);
			Log.i(TAG, "getPaddingLeft = " + getPaddingLeft() + ", getPaddingRight = " + getPaddingRight() + ", exceptWidth = " + exceptWidth);
		}
		
		if (heightMode != MeasureSpec.EXACTLY) {
			final int exceptHeight = getPaddingTop() + getPaddingBottom() + 2
					* mRadius + 2 * paintWidth;
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(exceptHeight,
					MeasureSpec.EXACTLY);
			Log.i(TAG, "getPaddingTop = " + getPaddingTop() + ", getPaddingBottom = " + getPaddingBottom() + ", exceptHeight = " + exceptHeight);
			
		}

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	@SuppressLint("DrawAllocation")
	@Override
	protected synchronized void onDraw(Canvas canvas) {

		
		canvas.save();
		//画布移动到左边
//		canvas.translate(getPaddingLeft(), getPaddingTop());
		int centerX = getMeasuredWidth()/2;
		int centerY = getMeasuredHeight()/2;
		mPaint.setStyle(Paint.Style.STROKE);  
        mPaint.setAntiAlias(true);  
        mPaint.setDither(true); 
		//未到达进度
		mPaint.setColor(mUnReachProgressBarColor);
		mPaint.setStrokeWidth(mUnReachProgressbarHeight);
		canvas.drawCircle(centerX, centerY, mRadius, mPaint);
		
		//已走进度
		mPaint.setColor(mReachProgressBarColor);
		mPaint.setStrokeWidth(mReachProgressbarHeight);
		final float sweepAngle = getProgress()*1.0f/getMax()*360;
//		Log.i(TAG, "sweepAngle = " + sweepAngle);
		RectF ovlf = new RectF(
				centerX - mRadius - mReachProgressbarHeight/2, 
				centerY - mRadius - mReachProgressbarHeight/2, 
				centerX + mRadius + mReachProgressbarHeight/2, 
				centerY + mRadius + mReachProgressbarHeight/2);
		
		canvas.drawArc(ovlf, -90, sweepAngle, false, mPaint);
		
		//进度文本
		//文字的宽高
		if (mText != null){
			mPaint.setStyle(Style.FILL);
			mPaint.setTextSize(mProgressbarTextSize);
			mPaint.setColor(mProgressBarTextColor);
			final int textWidth = (int) mPaint.measureText(mText);
			final int textHeight = (int) (mPaint.ascent() + mPaint.descent())/2;
			canvas.drawText(mText, centerX - textWidth/2, centerY - textHeight, mPaint);
		}			
		canvas.restore();
	}
	
	public void setText(String text){
		mText = text;
		invalidate();
	}
	
	private int dp2px(int dpVal) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
				dpVal, 
				getResources().getDisplayMetrics());
	}
	
	private int sp2px(int spVal) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 
				spVal, 
				getResources().getDisplayMetrics());
	}
	
}
