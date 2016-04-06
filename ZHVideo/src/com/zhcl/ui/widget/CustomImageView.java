/**
 * 
 */
package com.zhcl.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View.MeasureSpec;
import android.widget.ImageView;

import com.zhonghong.zhvideo.R;

/**
 * @author YC
 * @time 2016-3-29 下午7:29:57
 */
public class CustomImageView extends ImageView {

	private final String TAG = getClass().getSimpleName();

	private static final int TYPE_CIRCLE = 0;
	private static final int TYPE_ROUND = 1;

	/** 类型 */
	private int mType;

	private int mWidth;
	private int mHeight;

	/**圆角半径*/
	private int mRadius;

	private Bitmap mSrc;
	
	/**宽高比例*/
	private float whScale = 1;

	public CustomImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CustomImageView(Context context) {
		this(context, null);
	}

	public CustomImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.CustomImageView, defStyle, 0);

		int count = a.getIndexCount();
		for (int i = 0; i < count; i++) {
			int attr = a.getIndex(i);
			switch (attr) {
			case R.styleable.CustomImageView_boredRadius:
				mRadius = a
						.getDimensionPixelSize(
								attr,
								getStandarSize(context,
										TypedValue.COMPLEX_UNIT_PX, 10));
				break;
			case R.styleable.CustomImageView_type:
				mType = a.getInt(attr, 0);
				break;
			case R.styleable.CustomImageView_whscale:
				whScale = a.getFloat(attr, 1);
				break;
			case R.styleable.CustomImageView_src:
				mSrc = BitmapFactory.decodeResource(getResources(),
						a.getResourceId(attr, 0));

				break;
			}
		}
		a.recycle();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		/*int mode = MeasureSpec.getMode(widthMeasureSpec);
		int size = MeasureSpec.getSize(widthMeasureSpec);
		Log.i(TAG, "onMeasure width size = " + size);
		if (mode == MeasureSpec.EXACTLY) {
			mWidth = size;
		} else {
			// 由图片决定的宽
			int desireByImg = getPaddingLeft() + getPaddingRight()
					+ mSrc.getWidth();
			if (mode == MeasureSpec.AT_MOST) {
				mWidth = Math.min(size, desireByImg);
			} else {
				mWidth = desireByImg;
			}
		}
		mode = MeasureSpec.getMode(heightMeasureSpec);
		size = MeasureSpec.getSize(heightMeasureSpec);
		
		Log.i(TAG, "onMeasure heigh size = " + size);
		
		if (mode == MeasureSpec.EXACTLY) {
			mHeight = size;
		} else {
			// 由图片决定的宽
			int desireByImg = getPaddingTop() + getPaddingBottom()
					+ mSrc.getHeight();
			if (mode == MeasureSpec.AT_MOST) {
				mHeight = Math.min(size, desireByImg);
			} else {
				mHeight = desireByImg;
			}
		}
		Log.i(TAG, "onMeasure width = " + mWidth + ", height = " + mHeight);*/
		int width = MeasureSpec.getSize(widthMeasureSpec);
		Log.i(TAG, "onMeasure getSize width = " + width);
		int height = (int) (width * whScale);
		mWidth = width;
		mHeight = height;
		Log.i(TAG, "onMeasure width = " + mWidth + ", height = " + mHeight);
		setMeasuredDimension(mWidth, mHeight);
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		switch (mType) {
		case TYPE_CIRCLE:
			int min = Math.min(mWidth, mHeight);
			int max = Math.max(mWidth, mHeight);
			mSrc = Bitmap.createScaledBitmap(mSrc, min, min, false);
			canvas.drawBitmap(createCircleImage(mSrc, min), (max-min)/2, 0, null);
			break;
		case TYPE_ROUND:
			canvas.drawBitmap(getRoundedCornerBitmap(mSrc, mRadius), 0, 0, null);
			break;

		default:
			break;
		}
		Log.i(TAG, "onDraw");
	}

	
	@Override
	public void setImageBitmap(Bitmap bm) {
		// TODO Auto-generated method stub
		super.setImageBitmap(bm);
		mSrc = bm;
		requestLayout();
		Log.i(TAG, "setImageBitmap");
	}
	
	
	/**
	 * 转成标准尺寸
	 * 
	 * @param context
	 * @param unit
	 *            {@code TypedValue.COMPLEX_UNIT_PX...}
	 *            COMPLEX_UNIT_SP是单位，20是数值，也就是20sp。
	 * @param value
	 * @return
	 */
	public static int getStandarSize(Context context, int unit, int value) {
		return (int) TypedValue.applyDimension(unit, value, context
				.getResources().getDisplayMetrics());
	}

	/**
	 * 绘制圆图
	 * 
	 * @param source
	 * @param min
	 * @return
	 */
	public static Bitmap createCircleImage(Bitmap source, int min) {
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		Bitmap target = Bitmap.createBitmap(min, min, Config.ARGB_8888);
		/** 产生一个同样大小的画布 */
		Canvas canvas = new Canvas(target);
		/** 首先绘制圆形 */
		canvas.drawCircle(min / 2, min / 2, min / 2, paint);
		/** 使用SRC_IN，参考上面的说明 */
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		/** 绘制图片 */
		int width = source.getWidth();
		int left = (min - width)/2;
		canvas.drawBitmap(source, left, 0, paint);
		return target;
	}

	/**
	 * 获得圆角图片
	 * 
	 * @param bitmap
	 * @param roundPx
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

}
