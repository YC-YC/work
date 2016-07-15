package com.zhcar.widget;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ProgressBar;

import com.zhcar.R;

/**
 * 
 * @author YC
 * @time 2016-7-11 下午4:33:24 TODO:健康指数
 */
@SuppressLint("DrawAllocation")
public class HealthProgressBar extends ProgressBar {

	private static final String TAG = "HealthProgressBar";

	/** 渐变颜色 */
	private int[] colors = new int[] { Color.GREEN, Color.YELLOW, Color.RED,
			Color.RED };
	/** 总角度 */
	private int mTotalAngle = 270;
	private int mStartAngle = 0;
	private int mUnReachBarColor = 0xFF0000;
	private int mUnReachBarWidth = dp2px(2);
	private int mReachBarWidth = dp2px(4);
	private int mTitleTextColor = 0xFF0000;
	private int mTitleTextSize = sp2px(10);
	private int mContentTextColor = 0xFF0000;
	private int mContentTextSize = sp2px(10);
	private int mTitleMarginTop = dp2px(100);
	private int mContentMarginTitle = dp2px(10);
	private String mTitle = "";
	private String mUnit = "";
	private int mRadiu = dp2px(100);
	private int mSmallCircleRadius = dp2px(80);
	private int mSmallCircleColor = 0x00FFFFFF;
	private int mSmallCircleWidth = dp2px(2);

	private Paint mTextPaint;

	private Paint mUnReachPaint;
	private Paint mReachPaint;

	private int mWidth;
	private int mHeight;
	// 直径
	private int diameter = 200;

	// 圆心
	private float centerX;
	private float centerY;

	private Paint allArcPaint;
	private Paint progressPaint;
	private Paint vTextPaint;
	private Paint hintPaint;
	private Paint degreePaint;
	private Paint curSpeedPaint;

	private RectF bgRect;

	private ValueAnimator progressAnimator;

	private float startAngle = 135;

	private float currentAngle = 0;
	private float lastAngle;
	private float maxValues = 60;
	private float curValues = 0;
	private float bgArcWidth = dipToPx(2);
	private float progressWidth = dipToPx(10);
	private float textSize = dipToPx(60);
	private float hintSize = dipToPx(15);
	private float curSpeedSize = dipToPx(13);
	private int aniSpeed = 1000;
	private float longdegree = dipToPx(13);
	private float shortdegree = dipToPx(5);
	private final int DEGREE_PROGRESS_DISTANCE = dipToPx(8);
	private String hintColor = "#676767";
	private String longDegreeColor = "#111111";
	private String shortDegreeColor = "#111111";
	private String bgArcColor = "#FFFF00";
	private boolean isShowCurrentSpeed = true;
	private String hintString = "Km/h";
	private boolean isNeedTitle;
	private boolean isNeedUnit;
	private boolean isNeedDial;
	private boolean isNeedContent;
	private String titleString;

	// sweepAngle / maxValues 的值
	private float k;

	public HealthProgressBar(Context context) {
		this(context, null);
	}

	public HealthProgressBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public HealthProgressBar(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initCofig(context, attrs);
		initView();
	}

	public void setReachBarColor(int[] colors){
		this.colors = colors;
		invalidate();
	}
	
	
	/**
	 * 初始化布局配置
	 * 
	 * @param context
	 * @param attrs
	 */
	private void initCofig(Context context, AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.HealthPorgressBar);

		mTotalAngle = a.getInteger(R.styleable.HealthPorgressBar_totalangle,
				mTotalAngle);
		mStartAngle = a.getInteger(R.styleable.HealthPorgressBar_startangle,
				mStartAngle);

		mUnReachBarColor = a.getColor(
				R.styleable.HealthPorgressBar_unreach_bar_color,
				mUnReachBarColor);
		Log.i(TAG, "mUnReachBarColor = " + mUnReachBarColor);
		
		mUnReachBarWidth = (int) a.getDimension(
				R.styleable.HealthPorgressBar_unreach_bar_width,
				mUnReachBarWidth);

		int color1 = a.getColor(R.styleable.HealthPorgressBar_reach_bar_color1,
				Color.GREEN);
		int color2 = a.getColor(R.styleable.HealthPorgressBar_reach_bar_color2,
				color1);
		int color3 = a.getColor(R.styleable.HealthPorgressBar_reach_bar_color3,
				color1);
		Log.i(TAG, "color1 = " + color1 + ", color2 = " + color2 + ", color3 = " + color3);
		colors = new int[] { color1, color2, color3, color3 };
		mReachBarWidth = (int) a.getDimension(
				R.styleable.HealthPorgressBar_reach_bar_width, mReachBarWidth);

		mTitleTextColor = a
				.getColor(R.styleable.HealthPorgressBar_title_text_color,
						mTitleTextColor);
		mTitleTextSize = (int) a.getDimension(
				R.styleable.HealthPorgressBar_title_text_size, mTitleTextSize);

		mContentTextColor = a.getColor(
				R.styleable.HealthPorgressBar_content_text_color,
				mContentTextColor);
		mContentTextSize = (int) a.getDimension(
				R.styleable.HealthPorgressBar_content_text_size,
				mContentTextSize);

		mTitleMarginTop = (int) a.getDimension(
				R.styleable.HealthPorgressBar_title_margintop,
				mTitleMarginTop);

		mContentMarginTitle = (int) a.getDimension(
				R.styleable.HealthPorgressBar_content_margintitle,
				mContentMarginTitle);
		mTitle = a.getString(R.styleable.HealthPorgressBar_title);
		mUnit = a.getString(R.styleable.HealthPorgressBar_unit);
		mRadiu = (int) a.getDimension(R.styleable.HealthPorgressBar_radiu,
				mRadiu);
		mSmallCircleRadius = (int) a.getDimension(R.styleable.HealthPorgressBar_small_circle_radius, mSmallCircleRadius);
		mSmallCircleColor = a.getColor(R.styleable.HealthPorgressBar_small_circle_color,
				mSmallCircleColor);
		mSmallCircleWidth = (int) a.getDimension(R.styleable.HealthPorgressBar_small_circle_width, mSmallCircleWidth);
		
		a.recycle();

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);

		final int paintWidth = Math.max(mReachBarWidth, mUnReachBarWidth);

		if (widthMode != MeasureSpec.EXACTLY) {
			final int exceptWidth = getPaddingLeft() + getPaddingRight() + 2
					* mRadiu + 1 * paintWidth;
			widthMeasureSpec = MeasureSpec.makeMeasureSpec(exceptWidth,
					MeasureSpec.EXACTLY);
			Log.i(TAG, "onMeasure exceptWidth = " + exceptWidth);

		}

		if (heightMode != MeasureSpec.EXACTLY) {
			final int exceptHeight = getPaddingTop() + getPaddingBottom() + 2
					* mRadiu + 1 * paintWidth;
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(exceptHeight,
					MeasureSpec.EXACTLY);
			Log.i(TAG, "onMeasure exceptHeight = " + exceptHeight);
		}
		Log.i(TAG, "onMeasure widthMeasureSpec = " + widthMeasureSpec
				+ ", heightMeasureSpec = " + heightMeasureSpec);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// 圆心
		centerX = getMeasuredWidth() / 2;
		centerY = getMeasuredHeight() / 2;
	}

	private void initView() {


		mTextPaint = new Paint();
		mTextPaint.setStyle(Style.STROKE);
		mTextPaint.setAntiAlias(true);
		mTextPaint.setDither(true);
		mTextPaint.setStrokeCap(Cap.ROUND);
		mTextPaint.setTextAlign(Paint.Align.LEFT);

		mUnReachPaint = new Paint();
		mUnReachPaint.setAntiAlias(true);
		mUnReachPaint.setStyle(Paint.Style.STROKE);
		mUnReachPaint.setStrokeWidth(mUnReachBarWidth);
		mUnReachPaint.setColor(mUnReachBarColor);
//		mUnReachPaint.setStrokeCap(Paint.Cap.ROUND);

		mReachPaint = new Paint();
		mReachPaint.setAntiAlias(true);
		mReachPaint.setStyle(Paint.Style.STROKE);
		mReachPaint.setStrokeCap(Paint.Cap.ROUND);
		mReachPaint.setStrokeWidth(mReachBarWidth);
		mReachPaint.setColor(Color.GREEN);


	}

	@Override
	protected void onDraw(Canvas canvas) {

		final String contentText = getProgress()
				+ ((mUnit == null) ? "" : mUnit);
		Rect rect = new Rect();


		canvas.save();

		int left = (int) (centerX - mRadiu);
		int top = (int) (centerX - mRadiu);

		// 未到达进度
		canvas.drawArc(new RectF(left, top, 2 * mRadiu, 2 * mRadiu),
				startAngle, mTotalAngle, false, mUnReachPaint);

		// 已走进度
		// 设置渐变色
		final float sweepAngle = getProgress() * 1.0f / getMax() * mTotalAngle;
		getReachBarColor();
			
		SweepGradient sweepGradient = new SweepGradient(centerX, centerY,
				colors, null);
		Matrix matrix = new Matrix();
		matrix.setRotate(startAngle, centerX, centerY);
		sweepGradient.setLocalMatrix(matrix);
		mReachPaint.setShader(sweepGradient);
//		canvas.drawArc(new RectF(left, left, 2 * mRadiu, 2 * mRadiu),
//				startAngle, sweepAngle, true, mReachPaint);
		canvas.drawArc(new RectF(left, top, 2 * mRadiu, 2 * mRadiu),
				startAngle, sweepAngle, false, mReachPaint);

		
		//小圆
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
//		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(mSmallCircleWidth);
		paint.setColor(mSmallCircleColor);
		left = (int) (centerX - mSmallCircleRadius)*2;
//		Log.i(TAG, "small circle mSmallCircleRadius = " + mSmallCircleRadius + ", left = " + left);
		canvas.drawArc(new RectF(left, left, 2 * mSmallCircleRadius, 2 * mSmallCircleRadius),
				startAngle, mTotalAngle, false, paint);
		// 进度文本
		mTextPaint.setStyle(Style.FILL);
		mTextPaint.setColor(mTitleTextColor);
		mTextPaint.setTextSize(mTitleTextSize);
		mTextPaint.getTextBounds(contentText, 0, 1, rect);
		final int titleWidth = (int) mTextPaint.measureText(mTitle);
		final int titleHeight = rect.height();


		int y = mTitleMarginTop + titleHeight;
		canvas.drawText(mTitle, (getMeasuredWidth() - titleWidth) / 2, y,
				mTextPaint);

		mTextPaint.setColor(mContentTextColor);
		mTextPaint.setTextSize(mContentTextSize);
		mTextPaint.getTextBounds(mTitle, 0, 1, rect);
		final int contentWidth = (int) mTextPaint.measureText(contentText);
		final int contentHeight = rect.height();

		y += (mContentMarginTitle + contentHeight);
		canvas.drawText(contentText, (getMeasuredWidth() - contentWidth) / 2,
				y, mTextPaint);

		canvas.restore();

	}

	/**
	 * 
	 */
	private void getReachBarColor() {
		float progress = (float)getProgress()/getMax();
		if (progress < 0.6){
			colors = new int[]{Color.RED, Color.RED};
		}
		else if (progress < 0.8){
			colors = new int[]{Color.YELLOW, Color.YELLOW};
		}
		else{
			colors = new int[]{Color.GREEN, Color.GREEN};
		}
	}


	/**
	 * 设置当前值
	 * 
	 * @param currentValues
	 */
	private void setCurrentValues(float currentValues) {
		if (currentValues > maxValues) {
			currentValues = maxValues;
		}
		if (currentValues < 0) {
			currentValues = 0;
		}
		this.curValues = currentValues;
		lastAngle = currentAngle;
		setAnimation(lastAngle, currentValues * k, aniSpeed);
	}



	/**
	 * 为进度设置动画
	 * 
	 * @param last
	 * @param current
	 */
	private void setAnimation(float last, float current, int length) {
		Log.i(TAG, "Animation last = " + last + ", current = " + current
				+ ", currentAngle = " + currentAngle);
		progressAnimator = ValueAnimator.ofFloat(last, current);
		progressAnimator.setDuration(length);
		progressAnimator.setTarget(currentAngle);
		progressAnimator
				.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

					@Override
					public void onAnimationUpdate(ValueAnimator animation) {
						currentAngle = (Float) animation.getAnimatedValue();
						curValues = currentAngle / k;
						Log.i(TAG, "Animation currentAngle = " + currentAngle
								+ ", curValues = " + curValues);
					}
				});
		progressAnimator.start();
	}

	/**
	 * dip 转换成px
	 * 
	 * @param dip
	 * @return
	 */
	private int dipToPx(float dip) {
		float density = getContext().getResources().getDisplayMetrics().density;
		return (int) (dip * density + 0.5f * (dip >= 0 ? 1 : -1));
	}


	protected int dp2px(int dpVal) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				dpVal, getResources().getDisplayMetrics());
	}

	protected int sp2px(int spVal) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
				spVal, getResources().getDisplayMetrics());
	}

}
