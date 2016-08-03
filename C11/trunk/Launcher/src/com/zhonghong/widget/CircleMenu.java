/**
 * 
 */
package com.zhonghong.widget;

import java.util.List;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhonghong.launcher.R;
import com.zhonghong.menuitem.ItemInfoBean;
import com.zhonghong.utils.FontsUtils;


/**
 * @author YC
 * @time 2016-3-21 上午11:19:11
 */
public class CircleMenu extends ViewGroup {

	private static final String TAG = "CircleMenuLayout";
	private static final String TAG1 = "Drag";
	private static final int PER_PAGE_NUM = 5;	//每页个数
	private Context mContext;
	/**半径 */
	private int mRadius;
	/** 根据menu item的个数，计算角度 */
	private	float mPerItemAngle;
	/** 该容器内child item的默认尺寸(无用) */
	private static final float RADIO_DEFAULT_CHILD_DIMENSION = 1 / 4f;
	/** 菜单的中心child的默认尺寸 */
	private float RADIO_DEFAULT_CENTERITEM_DIMENSION = 1 / 3f;
	/** 移动角度 */
	private double mMoveAngle = 0;
	/**开始显示在UI上的第一个Item*/
	private int mFirstShowItem = 0;
	/**上一次显示页第一个Item*/
	private int mLastFirstShowItem = 0;
	/**圆心X,Y*/
	private int mCircleX, mCircleY;
	/** 设置显示的开始角度 */
	private double mStartAngleIndex = 0;
	/** 总显示角度 */
	private double mTotalAngle = 0;
	/** 菜单的个数 */
	private int mMenuItemCount;
	/**菜单信息*/
	private List<ItemInfoBean> mItemInfos;

	/** child的w,h */
	private int mChildWidth,mChildHeight;
	
	public CircleMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		//无视xml中的padding的布局
		setPadding(0, 0, 0, 0);
	}

	/**
	 * 设置菜单属性
	 * 设置菜单条目的图标和文本
	 * @param items Item信息
	 * @param startAngle 开始角度（理论值为（-90到90），但越接近90效果越不好）
	 * @param radio	半径 需要根据startAngle调整,startAngle越小，半径越小
	 */
	public void setMenuAttr(List<ItemInfoBean> items, float startAngle, int radio) {
		mItemInfos = items;
		mRadius = radio;
		mStartAngleIndex = startAngle;
		mMoveAngle = 0;
		mTotalAngle = 180 - 2*startAngle;
		// 参数检查
		if (items == null) {
			throw new IllegalArgumentException("菜单项文本和图片至少设置其一");
		}
		// 初始化mMenuCount
		mMenuItemCount = items.size();

		mPerItemAngle = (float) (mTotalAngle/(PER_PAGE_NUM-1));
		addMenuItems();

	}
	
	/**添加菜单项*/
	private void addMenuItems() {
		LayoutInflater mInflater = LayoutInflater.from(getContext());

		//根据用户设置的参数，初始化view
		for (int i = 0; i < mMenuItemCount; i++) {
			final int j = i;
			View view = mInflater.inflate(R.layout.circle_menu_item, this,
					false);
			ImageView iv = (ImageView) view
					.findViewById(R.id.id_circle_menu_item_image);
			TextView tv = (TextView) view
					.findViewById(R.id.id_circle_menu_item_text);

			if (iv != null) {
				iv.setImageResource(mItemInfos.get(i).getItemImgId());
				if (mItemInfos.get(i).getItemText() != ""){
					iv.setClickable(true);
					iv.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
	
							if (mOnMenuItemClickListener != null) {
								mOnMenuItemClickListener.itemClick(v, j);
							}
						}
					});
					iv.setOnLongClickListener(new OnLongClickListener() {
						
						@Override
						public boolean onLongClick(View v) {
							ClipData.Item item = new ClipData.Item("debug");
							String[] mimiType = {ClipDescription.MIMETYPE_TEXT_PLAIN};
							ClipData clipData = new ClipData("debug", mimiType, item);
							View.DragShadowBuilder shadowBuilder = new DragShadowBuilder(v);
							v.startDrag(clipData, shadowBuilder, null, 0);
							//startDrag后，每个setOnDragListener都可以监听到
							v.setOnDragListener(new OnDragListener() {
								
								@Override
								public boolean onDrag(View v, DragEvent event) {
									switch (event.getAction()) {
									case DragEvent.ACTION_DRAG_ENDED:
										procUp(0);
										return false;
									}
									return true;
								}
							});
							if (mOnMenuItemLongClickListener != null){
								mOnMenuItemLongClickListener.onItemLongClick(v, j);
							}
							return true;
						}
					});
				}
				else{
					iv.setClickable(false);
				}
			}
			if (tv != null) {
				tv.setText(mItemInfos.get(i).getItemText());
				tv.setTypeface(FontsUtils.getRuiZiBiGerTypeface(mContext));
			}

			// 添加view到容器中
			addView(view);
		}
	}

	/**MenuItem的点击事件接口*/
	private OnMenuItemClickListener mOnMenuItemClickListener;
	public interface OnMenuItemClickListener {
		void itemClick(View view, int pos);
	}
	public void setOnMenuItemClickListener(OnMenuItemClickListener listener) {
		this.mOnMenuItemClickListener = listener;
	}
	
	/**MenuItem的长点击事件接口*/
	private OnMenuItemLongClickListener mOnMenuItemLongClickListener;
	public interface OnMenuItemLongClickListener {
		void onItemLongClick(View view, int pos);
	}
	public void setOnMenuItemLongClickListener(OnMenuItemLongClickListener listener) {
		this.mOnMenuItemLongClickListener = listener;
	}
	
	/**Menu页面监听*/
	private OnPageChangeListener mPageChangeListener;
	public interface OnPageChangeListener {
		void onPageSelected(int page);
	}
	public void setOnPageChangeListener(OnPageChangeListener listener) {
		this.mPageChangeListener = listener;
		this.mPageChangeListener.onPageSelected(getPageFirstItem(mFirstShowItem)/PER_PAGE_NUM);
	}
	

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// menu item数量
		final int count = getChildCount();
		// menu item尺寸
		int childSize = (int) (mRadius * RADIO_DEFAULT_CHILD_DIMENSION);
		// menu item测量模式
		int childMode = MeasureSpec.EXACTLY;
		// 迭代测量
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);

			if (child.getVisibility() == GONE) {
				continue;
			}
			// 计算menu item的尺寸；以及和设置好的模式，去对item进行测量
			int makeMeasureSpec = -1;

			if (child.getId() == R.id.id_circle_menu_item_center){
				makeMeasureSpec = MeasureSpec.makeMeasureSpec(
						(int) (mRadius * RADIO_DEFAULT_CENTERITEM_DIMENSION),
						childMode);
			} else{
				makeMeasureSpec = MeasureSpec.makeMeasureSpec(childSize,
						childMode);
			}
			child.measure(0, 0);
			mChildWidth = child.getMeasuredWidth();
			mChildHeight = child.getMeasuredHeight();
		}
		
		int resWidth = 0;
		int resHeight = 0;
		//根据传入的参数，分别获取测量模式和测量值
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);

		int height = MeasureSpec.getSize(heightMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);

//		Log.i(tag, "onMeasure width = " +width +", widthMode = " +widthMode + ",height = " + height + ", heightMode = " + heightMode);
		//如果宽或者高的测量模式非精确值
		if (widthMode != MeasureSpec.EXACTLY
				|| heightMode != MeasureSpec.EXACTLY) 
		{
			int childWidth = (int) (mRadius*RADIO_DEFAULT_CHILD_DIMENSION);
			if (mStartAngleIndex < 0)
			{
				resWidth = 2*mRadius;
			}
			else
			{
				resWidth = (int) (mRadius*Math.cos(Math.toRadians(mStartAngleIndex - mPerItemAngle))*2);
			}
			//宽度左右各进行半个childview补偿
			resWidth += mChildWidth;
			resHeight = (int) (mRadius - mRadius*Math.sin(Math.toRadians(mStartAngleIndex - mPerItemAngle)));
			//上下高度各进行childview补偿
			resHeight += mChildHeight;
		} 
		else 
		{
			resWidth = width;
			resHeight = height;
		}
//		Log.i(tag, "onMeasure resWidth = " +resWidth +", resHeight = " +resHeight);

		// 设置view的w,h
		setMeasuredDimension(resWidth, resHeight);
		
		mCircleX = getMeasuredWidth()/2;
		mCircleY = getMeasuredHeight() - (mRadius+mChildHeight/2);
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		//TODO 布局每个View的位置

		//移动时根据mMoveAngle，更新mFirstShowItem
		if (Math.abs(mMoveAngle) > mPerItemAngle){
			int num = (int) (mMoveAngle/mPerItemAngle);
			mFirstShowItem -= num;
			mMoveAngle %= mPerItemAngle;
		}
		mFirstShowItem = checkItem(mFirstShowItem);
		
		layoutChildView();
		
		if (getPageFirstItem(mLastFirstShowItem) != getPageFirstItem(mFirstShowItem)){
			if (mPageChangeListener != null){
				mPageChangeListener.onPageSelected(getPageFirstItem(mFirstShowItem)/PER_PAGE_NUM);
			}
		}
		mLastFirstShowItem = mFirstShowItem;
	}

	/**Laying out the child views*/
	private void layoutChildView(){
		int left, top;
		for (int i = 0; i < mMenuItemCount; i++){
			final View child = getChildAt(i);
			if (itemNeedtoShow(i)){
				final double angle = getItemAngle(i);
				left = (int) (mCircleX - mRadius*Math.cos(Math.toRadians(angle)) - child.getMeasuredWidth()/2);
				top = (int) (mCircleY + (mRadius*Math.sin(Math.toRadians(angle)) - child.getMeasuredHeight()/2));
				child.layout(left, top, left + child.getMeasuredWidth(), top + child.getMeasuredHeight());
			} else {
				child.layout(0, 0, 0, 0);
			}
		}
		checkPreOrNextItem();
	}
	
	private double getItemAngle(int item){
		
		if (item >= mFirstShowItem){
			return mStartAngleIndex + (item-mFirstShowItem)*mPerItemAngle + mMoveAngle;
		}else{
			return mStartAngleIndex + (mMenuItemCount + item - mFirstShowItem)*mPerItemAngle + mMoveAngle;
		}
	}
	
	/**item是否为显示项*/
	private boolean itemNeedtoShow(int item){
		boolean result = false;
		if (mFirstShowItem >= 0 && mFirstShowItem < mMenuItemCount - PER_PAGE_NUM){
			if (item >= mFirstShowItem && item < mFirstShowItem + PER_PAGE_NUM){
				result = true;
			}
		}else{
			if (item >= mFirstShowItem || item < (mFirstShowItem+PER_PAGE_NUM)%mMenuItemCount){
				result = true;
			}
				
		}
		return result;
	}
	
	/**检测前一个或后一个Item*/
	private void checkPreOrNextItem() {
		int left, top;
		if (mMoveAngle > 0){	//前一个
			View child = getChildAt(checkItem(mFirstShowItem - 1));
			final double angle = mStartAngleIndex - mPerItemAngle + mMoveAngle;
			left = (int) (mCircleX - mRadius*Math.cos(Math.toRadians(angle)) - child.getMeasuredWidth()/2);
			top = (int) (mCircleY + (mRadius*Math.sin(Math.toRadians(angle)) - child.getMeasuredHeight()/2));
			child.layout(left, top, left + child.getMeasuredWidth(), top + child.getMeasuredHeight());
		} else if (mMoveAngle < 0){	//后一个
			View child = getChildAt(checkItem(mFirstShowItem + PER_PAGE_NUM));
			final double angle = mStartAngleIndex + PER_PAGE_NUM*mPerItemAngle + mMoveAngle;
			left = (int) (mCircleX - mRadius*Math.cos(Math.toRadians(angle)) - child.getMeasuredWidth()/2);
			top = (int) (mCircleY + (mRadius*Math.sin(Math.toRadians(angle)) - child.getMeasuredHeight()/2));
			child.layout(left, top, left + child.getMeasuredWidth(), top + child.getMeasuredHeight());
		}
	}

	/**校验item*/
	private int checkItem(int item){
		if (item >= 0 && item < mMenuItemCount){
			return item;
		}
		int result = 0;
		if (item < 0){
			result = mMenuItemCount + item%mMenuItemCount;
		}else{
			result = item%mMenuItemCount;
		}
		Log.i(TAG, "checkItem item = " + item + ", newItem = " + result);
		return result;
	}
	
	/** 当每秒移动角度达到该值时，认为是快速移动 */
	private static final int FLINGABLE_VALUE = 10;
	/** 如果移动角度达到该值，则屏蔽点击Item */
	private static final int NOCLICK_VALUE = 100;
	/** 移动的最大位移 */
	private static final int MOVE_MIN_DIFF = 20;
	/** 检测按下到抬起时旋转的角度 */
	private float mTmpAngle;
	/**按下时的开始项*/
	private int mDownStartItem;
	/** 检测按下到抬起时使用的时间 */
	private long mLastMoveTime;
	/** 记录上一次的x，y坐标 */
	private float mLastX,mLastY;
	/** 记录按下的x，y坐标 */
	private float mDownX;
	/** 判断是否正在自动滚动 */
	private boolean isFling;
	/** 自动滚动的Runnable */
	private AutoFlingRunnable mFlingRunnable;

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		//TODO dispatchTouchEvent

		float x = ev.getX();
		float y = ev.getY();
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN://按下
			mLastX = x;
			mLastY = y;
			mDownX = x;
			mLastMoveTime = SystemClock.elapsedRealtime();
			mTmpAngle = 0;
			mDownStartItem = mFirstShowItem;
			if (isFling) // 如果当前已经在滚动  
			{
				removeCallbacks(mFlingRunnable);
				isFling = false;
				return true;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			float angle = (float) ((x - mLastX)*mTotalAngle/getMeasuredWidth());
			Log.i(TAG, "ACTION_MOVE angle = " + angle);
			mLastX = x;  
			mLastY = y;
			mLastMoveTime = SystemClock.elapsedRealtime();
			if (Math.abs(mDownX - x) <= MOVE_MIN_DIFF){
				Log.i(TAG, "ACTION_MOVE break");
				break;
			}
			procMove(angle); 
            mTmpAngle = angle; 
			break;
		case MotionEvent.ACTION_UP:
			 // 计算，每秒移动的角度  
           float speed = mTmpAngle * 1000/(SystemClock.elapsedRealtime() - mLastMoveTime);  
            Log.i(TAG, "11111anglePerSecond up" + speed);
            procUp(speed);
            // 如果当前旋转角度超过NOCLICK_VALUE屏蔽点击  
            if (Math.abs(speed) > NOCLICK_VALUE)  
            {  
                return true;  
            }  
            break;
		default:
			break;
		}
		super.dispatchTouchEvent(ev);//执行一下，不然子View获取不到点击事件
		return true;
	}
	
	/**移动角度*/
	private void procMove(float angle){
		mMoveAngle += angle;  
		if (Math.abs(mMoveAngle) > mPerItemAngle){
			int num = (int) (mMoveAngle/mPerItemAngle);
			mFirstShowItem -= num;
			mMoveAngle %= mPerItemAngle;
		}
		checkNotCycle();
		
		mFirstShowItem = checkItem(mFirstShowItem);
		Log.i(TAG, "procMove mFirstShowItem = " + mFirstShowItem);
		int tmpFirstItem = NextOrPrePageFirstItem(isClockWise(angle), mDownStartItem);
		if (!isClockWise(angle)){	//逆时针
			//若开始页为本页开始页，最多可切到前一页
			if (mDownStartItem == getPageFirstItem(mDownStartItem)){
				if (tmpFirstItem == mFirstShowItem){
					mMoveAngle = 0.0f;
				}
			}
			//否则只能切到本页的开始页
			else{
				if (getPageFirstItem(mDownStartItem) == mFirstShowItem){
					mMoveAngle = 0.0f;
				}
			}
		}else{
			if (tmpFirstItem == mFirstShowItem){
				mMoveAngle = 0.0f;
			}
		}
        Log.i(TAG, "procMove mMoveAngle = " + mMoveAngle);
        requestLayout();  
	}

	/**不循环旋转调用此方法*/
	private void checkNotCycle() {
		if (mFirstShowItem > getPageFirstItem(mMenuItemCount-1) ||
				((mFirstShowItem == getPageFirstItem(mMenuItemCount-1)) && mMoveAngle < 0.0f)){
			mFirstShowItem = getPageFirstItem(mMenuItemCount-1);
			mMoveAngle = 0.0f;
			Log.i(TAG, "To End Page");
		}
		else if (mFirstShowItem < 0 || 
				((mFirstShowItem == 0) && mMoveAngle > 0.0f)){
			mFirstShowItem = 0;
			mMoveAngle = 0.0f;
			Log.i(TAG, "To First Page");
		}
	}
	
	/**松开时的处理*/
	private void procUp(float speed){
		 Log.i(TAG, "procUp mFirstShowItem = " + mFirstShowItem + ", mMoveAngle = " + mMoveAngle);
         // 如果达到该值认为是快速移动,根据移动方向转完本页
         if (Math.abs(speed) > FLINGABLE_VALUE && !isFling)  
         {  
         	//顺时针
         	if (isClockWise(speed)){
         		//若没到最后一页，转到后一页
         		if (mFirstShowItem < getPageFirstItem(mMenuItemCount-1)){
         			moveToNextOrPrePage(true);
         		}else{
         			moveToCurPageFirst();
         		}
         	}
         	//若逆时针，则转到本页开始项
         	else{
         		if ((mFirstShowItem == getPageFirstItem(mFirstShowItem)) && mMoveAngle > 0){
         			moveToNextOrPrePage(false);
         		}
         		else{
         			moveToCurPageFirst();
         		}
         	}
//          return true;  
         }
         //速度过小时，应该做回退
         else{
        	 int firstItem = getPageFirstItem(mFirstShowItem);
        	 Log.i(TAG, "procUp mFirstShowItem = " + mFirstShowItem + ", firstItem = " + firstItem);
        	 //若当前显示页是本页开始页，则不处理
        	if (firstItem == mFirstShowItem){
        		mMoveAngle = 0.0f;
        		requestLayout();
         	}
        	//根据显示项转到开始页
        	else{
        		//若显示页超过一半，则顺时针转到下一页
        		if (mFirstShowItem - firstItem >= (PER_PAGE_NUM+1)/2){
//        			moveToNextPage(true);
        			//若没到最后一页，转到后一页
             		if (mFirstShowItem < getPageFirstItem(mMenuItemCount-1)){
             			moveToNextOrPrePage(true);
             		}else{
             			moveToCurPageFirst();
             		}
        		}
        		//否则显示本页开始页
        		else{
        			moveToCurPageFirst();
        		}
        	}
         }
	}
	
	/**滚动到下一页或前一页*/
	private void moveToNextOrPrePage(boolean bNext){
		int endItem = NextOrPrePageFirstItem(bNext, mFirstShowItem);
 		double stopAngle = getBetweenAngle(mFirstShowItem, endItem, bNext) - mMoveAngle;
 		post(mFlingRunnable = new AutoFlingRunnable(stopAngle, (bNext ? -1:1)*getSpeed(stopAngle), endItem));
	}
	
	/**滚动到本页开始*/
	private void moveToCurPageFirst(){
		int endItem = getPageFirstItem(mFirstShowItem);
 		double stopAngle = getBetweenAngle(mFirstShowItem, endItem, false) - mMoveAngle;
 		post(mFlingRunnable = new AutoFlingRunnable(stopAngle, getSpeed(stopAngle), endItem));
	
	}
	
	/**自动转动的速度*/
	private float getSpeed(double angle){
		angle = Math.abs(angle);
		if (angle > 50){
			return 4.5f;
		}
		else if(angle > 30){
			return 2.5f;
		}else if (angle > 15){
			return 1.5f;
		}else{
			return 1;
		}
	}
	
	/**速度是否是顺时针*/
	private boolean isClockWise(float speed){
		return speed < 0 ? true: false;
	}
	
	/**获取列表项的第一项*/
	private int getPageFirstItem(int item){
		return item/PER_PAGE_NUM*PER_PAGE_NUM;
	}
	
	/**根据方向获取当前项前一页或后一页的开始项*/
	private int NextOrPrePageFirstItem(boolean bClockwise, int curItem){
		curItem = curItem/PER_PAGE_NUM * PER_PAGE_NUM;
		int newItem = 0;
		//若顺时针则下一页
		if (bClockwise){	
			newItem = curItem + PER_PAGE_NUM;
			if (newItem >= mMenuItemCount){
//				newItem = (newItem-mMenuItemCount)/PER_PAGE_NUM * PER_PAGE_NUM;
				newItem = getPageFirstItem(mMenuItemCount - 1);
			}
			Log.i(TAG, "NextOrPrePageItem curItem = " + curItem + ", next page item = " + newItem);
		//若逆时针则上一页
		}else{	
			newItem = curItem - PER_PAGE_NUM;
			if (newItem < 0){
//				newItem = (mMenuItemCount-1)/PER_PAGE_NUM * PER_PAGE_NUM;
				newItem = 0;
			}
			Log.i(TAG, "NextOrPrePageItem curItem = " + curItem + ", pre page item = " + newItem);
		}
		return newItem;
	}
	
	/**从from项到to项之间的角度*/
	private double getBetweenAngle(int from, int to, boolean bClockwise){	
		return getBetweenItem(from, to, bClockwise)*mPerItemAngle;
	}
	
	/**根据方向，计算两个Item之间的项个数*/
	private int getBetweenItem(int from, int to, boolean bClockwise){	
		int count = 0;
		if (bClockwise){	
			//顺时针，from要小于to,如果from大于to,说明是转了一圈
			if (from > to){
				to += mMenuItemCount;
			}
			count = from - to;
		} else { 
			//逆时针，from要大于to,如果from小于to,说明是转了一圈
			if (from < to){
				from += mMenuItemCount;
			}
			count = from - to;
		}
		Log.i(TAG, "getBetweenItem [ " + from + ", " + to + " ] = " + count);
		return count;
	}
	
	private class AutoFlingRunnable implements Runnable {
		
		private double mEndAngle;
		private double mHadRunAngle;
		private float mSpeed;
		private int mEndItem;
		public AutoFlingRunnable(double endAngle, float speed, int endItem){
			mEndAngle = endAngle;
			mEndItem = endItem;
			mSpeed = speed;
			mHadRunAngle = 0.0f;
			Log.i(TAG, "AutoFling mEndAngle = " + mEndAngle + ", mSpeed = " + mSpeed + ", endItem = " + mEndItem);
		}
		
		public void run() {
			if (Math.abs(mEndAngle - mHadRunAngle) < Math.abs(mSpeed))
			{
				mMoveAngle = 0.0f;
				mFirstShowItem = mEndItem;
				requestLayout();
				isFling = false;
				Log.i(TAG, "AutoFling Stop");
				return;
			}
			isFling = true;
			mMoveAngle += mSpeed;
			mHadRunAngle += mSpeed;
			Log.i(TAG, "AutoFlingRunnable mHadRunAngle = " + mHadRunAngle);
			postDelayed(this, 30);
			// 重新布局
			requestLayout();
		}
	};

}
