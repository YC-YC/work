package com.zhcl.ui.widget;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import com.zh.uitls.L;
import com.zh.uitls.Utils;
import com.zhcl.media.ImageManager;
import com.zhonghong.zhmedia.R;

/**
 * 显示lrc歌词控件
 * @author zhonghong.chenli
 * @date 2015-12-19 下午2:35:03
 */
@SuppressLint("DrawAllocation") 
public class LrcView extends View {
	private static final String tag = LrcView.class.getSimpleName();
	private static final int SCROLL_TIME = 400;
	private static final String DEFAULT_TEXT = "暂无歌词";
	
	private List<Lrc> mLrcList = new ArrayList<LrcView.Lrc>();
	private long mNextTime = 0l; // 保存下一句开始的时间

	private int mViewWidth; // view的宽度
	private int mLrcHeight; // lrc界面的高度
	private int mRows;      // 多少行
	private int mCurrentLine = 0; // 当前行
	private int mOffsetY;   // y上的偏移
	private int mMaxScroll; // 最大滑动距离=一行歌词高度+歌词间距

	private float mTextSize; // 字体
	private float mDividerHeight; // 行间距
	
	private Rect mTextBounds;

	private Paint mNormalPaint; // 常规的字体
	private Paint mCurrentPaint; // 当前歌词的大小

	private Bitmap mBackground;
	
	private Scroller mScroller;

	public LrcView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public LrcView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mScroller = new Scroller(context, new LinearInterpolator());
		inflateAttributes(attrs);
	}

	int currentTextColor;
	// 初始化操作
	private void inflateAttributes(AttributeSet attrs) {
		// <begin>
		// 解析自定义属性
		TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.Lrc);
		mTextSize = ta.getDimension(R.styleable.Lrc_textSize, 50.0f);
		mRows = ta.getInteger(R.styleable.Lrc_rows, 5);
		mDividerHeight = ta.getDimension(R.styleable.Lrc_dividerHeight, 0.0f);

		int normalTextColor = ta.getColor(R.styleable.Lrc_normalTextColor, 0xffffffff);
		currentTextColor = ta.getColor(R.styleable.Lrc_currentTextColor, 0xff00ffde);
		ta.recycle();
		// </end>

		// 计算lrc面板的高度
		mLrcHeight = (int) (mTextSize + mDividerHeight) * mRows + 5;
		Log.e("chenli", "mLrcHeight = " + mLrcHeight);
		mNormalPaint = new Paint();
		mCurrentPaint = new Paint();
		// 初始化paint
		mNormalPaint.setTextSize(mTextSize);
		mNormalPaint.setColor(normalTextColor);
		mNormalPaint.setAntiAlias(true);
		mNormalPaint.setShadowLayer(1, 1, 1, 0xFF000000);  
		
		mCurrentPaint.setTextSize(mTextSize);
		mCurrentPaint.setColor(currentTextColor);
		mCurrentPaint.setAntiAlias(true);
		mCurrentPaint.setShadowLayer(1, 1, 1, 0xFF000000);  
		
		mTextBounds = new Rect();
		mCurrentPaint.getTextBounds(DEFAULT_TEXT, 0, DEFAULT_TEXT.length(), mTextBounds);
		mMaxScroll = (int) (mTextBounds.height() + mDividerHeight);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// 重新设置view的高度
//		int measuredHeightSpec = MeasureSpec.makeMeasureSpec(mLrcHeight, MeasureSpec.AT_MOST);
//		super.onMeasure(widthMeasureSpec, measuredHeightSpec);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// 获取view宽度
		mViewWidth = getMeasuredWidth();
	}

	int temp = -1;
	@Override
	protected void onDraw(Canvas canvas) {
		//背景
		if (mBackground != null) {
			canvas.drawBitmap(Bitmap.createScaledBitmap(mBackground, mViewWidth, mLrcHeight, true),
					new Matrix(), null);
		}
		
		// float centerY = (getMeasuredHeight() + mTextBounds.height() - mDividerHeight) / 2;
		float centerY = (getMeasuredHeight() + mTextBounds.height()) / 2;	//y居中
		if (mLrcList.isEmpty()) {	
			float x = (mViewWidth - mCurrentPaint.measureText(DEFAULT_TEXT)) / 2;
//			Log.e("chenli", "x = " + x + " y = " + centerY);
			canvas.drawText(DEFAULT_TEXT,  x, centerY, mCurrentPaint);
			return;
		}
		drawLrc(canvas);
	}
	
	//当前非正解，后续重写歌词显示
	/** 当前项 */
	int index;
	/** 能画多少行 */
	int canDrawCount;
	int tempScrollIndex;
	public void loadTime(int time){
		if(!hasLrc()){
			return;
		}
		int temp = getCurrentStrIndex(time);
		if(temp < tempScrollIndex || temp > tempScrollIndex + 1){
			tempScrollIndex = temp;
			index = temp;
		}
		if(temp != tempScrollIndex){
			tempScrollIndex = temp;  
//			index = temp;
//			mOffsetY = 0;
//			if(index + 1 < mTimes.size()){
				mScroller.abortAnimation();
				mScroller.startScroll(0, 0, 0, (int)(2 * mDividerHeight + mTextBounds.height()), SCROLL_TIME);
//			}
		}
		postInvalidate();
	} 
	
	
	/**
	 * 获得当前歌词index
	 */
	private int getCurrentStrIndex(int time){
		for(int i = 0; i < mLrcList.size(); i++){
			//第一行
			if(time < mLrcList.get(0).time){
				return 0;
			}
			//最后一行
			if(time > mLrcList.get(mLrcList.size() - 1).time){
				return mLrcList.size() - 1;
			}
			//中间行
			if(time > mLrcList.get(i).time  && time < mLrcList.get(i + 1).time){
				return i;
			}
		}
		return 0;
	}
	 
	/**
	 * 能画多少航
	 */
	private int canDrawCountLine(){
		canDrawCount = (int) (getMeasuredHeight() / (2 * mDividerHeight + mTextBounds.height()));
		Log.e("chenli", "能画多少行：" + canDrawCount);
		return canDrawCount;
	}
	
	/**
	 * 画歌词
	 * @param canvas
	 */
	private void drawLrc(Canvas canvas){
		if(canDrawCount == 0){
			canDrawCountLine(); 
		}
		
		float centerY = (getMeasuredHeight() + mTextBounds.height()) / 2;	//y居中
		//画当前行的之前行
		int beforeCount = index - (canDrawCount / 2);
		beforeCount = beforeCount < 0 ? 0 : beforeCount;
		for(int i = index -1; i >= beforeCount; i--){
			String beforeLine = mLrcList.get(i).str;
			float currentX = (mViewWidth - mCurrentPaint.measureText(beforeLine)) / 2;
			int before = index - i;
			canvas.drawText(beforeLine, currentX, centerY - mOffsetY - before * (2 * mDividerHeight + mTextBounds.height()), mNormalPaint);
		}
		
		//画当前行
		String currentLine = mLrcList.get(index).str;
		float currentX = (mViewWidth - mCurrentPaint.measureText(currentLine)) / 2;
		canvas.drawText(currentLine, currentX, centerY - mOffsetY, mCurrentPaint);
		//画后面行
		int afterCount = index + (canDrawCount / 2) + 2;
		afterCount = afterCount >= mLrcList.size() ? mLrcList.size(): afterCount;
		for(int i = index + 1; i < afterCount; i++){
			String afterLine = mLrcList.get(i).str;
			currentX = (mViewWidth - mCurrentPaint.measureText(afterLine)) / 2;
			int afterIndex = i - index;
			canvas.drawText(afterLine, currentX, centerY - mOffsetY + afterIndex * (2 * mDividerHeight + mTextBounds.height()), mNormalPaint);
		}
		
	}
	
	@Override
	public void computeScroll() {
		if(mScroller.computeScrollOffset()) {
			mOffsetY = mScroller.getCurrY();
			if(mScroller.isFinished()) {
				int cur = mScroller.getCurrX();
				mCurrentLine = cur <= 1 ? 0 : cur - 1;
				mOffsetY = 0;
				index = tempScrollIndex;
			}
			
			postInvalidate();
		}
	}

	// 解析时间
	private Long parseTime(String time) {
		// 03:02.12
		String[] min = time.split(":");
		if(min.length < 2){
			return 0l;
		}
		String[] sec = min[1].split("\\.");
		
		long minInt = Long.parseLong(min[0].replaceAll("\\D+", "")
				.replaceAll("\r", "").replaceAll("\n", "").trim());
		long secInt = Long.parseLong(sec[0].replaceAll("\\D+", "")
				.replaceAll("\r", "").replaceAll("\n", "").trim());
		long milInt = Long.parseLong(sec[1].replaceAll("\\D+", "")
				.replaceAll("\r", "").replaceAll("\n", "").trim());
		
		return minInt * 60 * 1000 + secInt * 1000 + milInt * 10;
	}

	// 解析每行
	private String[] parseLine(String line) {
		Matcher matcher = Pattern.compile("\\[\\d.+\\].+").matcher(line);
		// 如果形如：[xxx]后面啥也没有的，则return空
		if (!matcher.matches()) {
			L.w(tag, "throws " + line);
			return null;
		}
		line = line.replaceAll("\\[", "");
		String[] result = line.split("\\]");
		return result;
	}

	// 外部提供方法
	// 传入当前播放时间
	public synchronized void changeCurrent(long time) {
		// 如果当前时间小于下一句开始的时间
		// 直接return
		if (mNextTime > time) {
			return;
		}
		
		// 每次进来都遍历存放的时间
		int timeSize = mLrcList.size();
		for (int i = 0; i < timeSize; i++) {
			
			// 解决最后一行歌词不能高亮的问题
			if(mNextTime == mLrcList.get(timeSize - 1).time) {
				mNextTime += 60 * 1000;
				mScroller.abortAnimation();
				mScroller.startScroll(timeSize, 0, 0, mMaxScroll, SCROLL_TIME);
//				mNextTime = mTimes.get(i);
//				mCurrentLine = i <= 1 ? 0 : i - 1;
				postInvalidate();
				return;
			}
			
			// 发现这个时间大于传进来的时间
			// 那么现在就应该显示这个时间前面的对应的那一行
			// 每次都重新显示，是不是要判断：现在正在显示就不刷新了
			if (mLrcList.get(i).time > time) {
				mNextTime = mLrcList.get(i).time;
				mScroller.abortAnimation();
				mScroller.startScroll(i, 0, 0, mMaxScroll, SCROLL_TIME);
//				mNextTime = mTimes.get(i);
//				mCurrentLine = i <= 1 ? 0 : i - 1;
				postInvalidate();
				return;
			}
		}
	}
	
	// 外部提供方法
	// 拖动进度条时
	public void onDrag(int progress) {
		for(int i=0;i<mLrcList.size();i++) {
			if(mLrcList.get(i).time > progress) {
				mNextTime = i == 0 ? 0 : mLrcList.get(i-1).time;
				return;
			}
		}
	}

	/**
	 * 歌词解析
	 * 	[03:34.03][03:02.61][01:25.17]读完了依赖
	 *	[03:36.96][03:05.41][01:27.85]我很快就离开
	 * @param path
	 * @return
	 */
	public boolean setLrcPath(String path) {
		L.e(tag, "setLrcPath :" + path);
		reset();
		File file = new File(path);
		if (!file.exists()) {
			postInvalidate();
			return false;
		}

		BufferedReader reader = null;
		String codeString = "utf-8";
		if(!path.contains(ImageManager.CACHEDIR_LRC)){
			codeString = Utils.getInstance().codeString(path);
		}
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),codeString));

			String line = "";
			String[] arr;
			while (null != (line = reader.readLine())) {
				arr = parseLine(line);
				saveLrc(arr);
			}
			Collections.sort(mLrcList);	//时间排序
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if(reader != null) {
				try {
					reader.close();		
				} catch (IOException e) { 
					e.printStackTrace();
				}
			}
		}
		if(!hasLrc()){
			postInvalidate();
			return false;
		}
		Log.e("chenli", "加载完成");
		return true;
	}

	private void reset() {
		mLrcList.clear();
		mCurrentLine = 0;
		mNextTime = 0l;
		index = 0;
		canDrawCount = 0;
		tempScrollIndex = 0;
	}
	
	// 是否设置歌词
	public boolean hasLrc() {
		return mLrcList != null && !mLrcList.isEmpty();
	}

	// 外部提供方法
	// 设置背景图片
	public void setBackground(Bitmap bmp) {
		mBackground = bmp;
	}
	
	
	private void saveLrc(String[] lrcLines){
		if(lrcLines == null || lrcLines.length == 1){
			return;
		}
		String lrcStr = lrcLines[lrcLines.length - 1];
		for(int i = 0; i < lrcLines.length - 1; i++){
			Log.e("chenli", " parseTime(lrcLines[i] = " + parseTime(lrcLines[i]) + " str4 = " + lrcStr);
			mLrcList.add(new Lrc(parseTime(lrcLines[i]), lrcStr));
		}
	}
	
	/**
	 * 歌词类
	 */
	class Lrc implements Comparable<Lrc>{
		/** 播放时间 */
		public long time;
		/** 歌词 */
		public String str;
		public Lrc(long time, String str){
			this.time = time;
			this.str = str;
		}
		
		/**
		 * 排序
		 */
		@Override
		public int compareTo(Lrc s) {
			return ((Long)time).compareTo(s.time);
		}
	}
}
