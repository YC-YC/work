/**
 * 
 */
package com.zhonghong.lock;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;

import com.zhonghong.launcher.R;
import com.zhonghong.sdk.android.utils.PreferenceUtils;
import com.zhonghong.utils.GlobalData;
import com.zhonghong.widget.CircleMenu.OnMenuItemClickListener;

/**
 * 
 * @author YC
 * @time 2016-6-29 下午12:03:42
 * TODO 加锁悬浮窗口管理
 */
public class LockWndHelper {
	
	protected static final String TAG = "LockWndHelper";

	private static final int PER_SCREEN_WIDTH = 1280;
	private static final int LOCKVIEW_HEIGHT = 650;
	private static final int LOCKVIEW_WIDTH = 10;
	private static final int LOCKVIEW_EXTEND_WIDTH = 250;
	
	
	private static LockWndHelper instances;
	public static LockWndHelper getInstaces(){
		if (instances == null){
			synchronized (LockWndHelper.class) {
				if (instances == null){
					instances = new LockWndHelper();
				}
			}
		}
		return instances;
	}
	
	WindowManager mWindowManager;
	
	private View mDrawView;
	private DrawerLayout mDrawerLayout;
	private Button mLock;
	private WindowManager.LayoutParams  mDrawViewParams;
	
	/**是否松开*/
	private boolean mReleaseTouch = false;
	
	private OnLockClickListener mOnLockClickListener;
	public interface OnLockClickListener{
		public void onClick(View v);
	}
	public void setOnLockClickListener(OnLockClickListener listener) {
		this.mOnLockClickListener = listener;
	}
	
	
	public boolean createDrawerLayoutView(Context context){
		Log.i(TAG, "createDrawerLayoutView");
		getWindowManager(context);
		if (mDrawView == null){
			mDrawView = LayoutInflater.from(context.getApplicationContext())
					.inflate(R.layout.layout_lock, null);
			
			if (mDrawViewParams == null){
				mDrawViewParams = new WindowManager.LayoutParams();
				
				mDrawViewParams.type = LayoutParams.TYPE_PHONE;
//				mBigViewParams.type = LayoutParams.TYPE_TOAST;	//添加这个类型不需要打开系统悬浮权限
				mDrawViewParams.format = PixelFormat.RGBA_8888;
				//设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）  
				mDrawViewParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE
						|LayoutParams.FLAG_TRANSLUCENT_STATUS
						|LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;	//忽略状态栏
				//调整悬浮窗显示的停靠位置为左侧置顶  
				mDrawViewParams.gravity = Gravity.LEFT|Gravity.CENTER; 
				
				// 以屏幕左上角为原点，设置x、y初始值，相对于gravity  
				mDrawViewParams.x = PER_SCREEN_WIDTH - LOCKVIEW_WIDTH;  
				mDrawViewParams.y = 0;  
		  
		        //设置悬浮窗口长宽数据    
				mDrawViewParams.width = LOCKVIEW_WIDTH;  
				mDrawViewParams.height = LOCKVIEW_HEIGHT;

			}
			mWindowManager.addView(mDrawView, mDrawViewParams);  
			Log.i(TAG, "addView");
			mDrawerLayout = (DrawerLayout) mDrawView.findViewById(R.id.drawer_layout);
//			openLeft();
			mLock = (Button)mDrawerLayout.findViewById(R.id.lock);
			setLockState(PreferenceUtils.getBoolean(GlobalData.KEY_LOCK, false));
			mLock.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Log.i(TAG, "点击了加锁");
					if (mOnLockClickListener != null){
						mOnLockClickListener.onClick(v);
					}
					setLockState(PreferenceUtils.getBoolean(GlobalData.KEY_LOCK, false));
//					close();
				}
			});
			mDrawerLayout.setScrimColor(Color.TRANSPARENT);	//去除阴影
//			mDrawerLayout.setDrawerShadow(R.drawable.item_default, GravityCompat.START);
			mDrawerLayout.setDrawerListener(new DrawerListener() {
				
				@Override
				public void onDrawerStateChanged(int state) {
					// TODO Auto-generated method stub
//					Log.i(TAG, "onDrawerStateChanged state = " + state);
					if (mReleaseTouch && state == 0){
						if (!mDrawerLayout.isDrawerOpen(Gravity.RIGHT)){
							makeDrawerLayoutInVisible();
						}
					}
				}
				
				@Override
				public void onDrawerSlide(View drawerView, float slideOffset) {
//					Log.i(TAG, "onDrawerSlide tag = " + drawerView.getTag() + ", slideOffset = " + slideOffset);
				}
				
				
				@Override
				public void onDrawerOpened(View drawerView) {
//					Log.i(TAG, "onDrawerOpened tag = " + drawerView.getTag());
				/*	if ("LEFT".equals(drawerView.getTag())){
						makeDrawerLayoutInVisible();
//						openRight();
					}*/
				}
				
				@Override
				public void onDrawerClosed(View drawerView) {
//					Log.i(TAG, "onDrawerClosed tag = " + drawerView.getTag());
					if ("RIGHT".equals(drawerView.getTag())){
						makeDrawerLayoutInVisible();
					}
				}
			});
			mDrawerLayout.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
//					Log.i(TAG, "onDrawer onTouch action = " + event.getAction());
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						mReleaseTouch = false;
						//将事件设置到右边的边距上
						event.setLocation(event.getX()+LOCKVIEW_EXTEND_WIDTH, event.getY());
						makeDrawerLayoutVisible();
						break;
					case MotionEvent.ACTION_UP:
						mReleaseTouch = true;
						break;
					default:
						break;
					}
					return false;
				}
			});
			return true;
		}
		
		return false;
	}
	
	private void openLeft(){
		mDrawerLayout.openDrawer(Gravity.LEFT);
//		Log.i(TAG, "openLeft Drawer");
		
	}
	
	private void openRight(){
//		mDrawerLayout.openDrawer(Gravity.RIGHT);
		mDrawerLayout.openDrawer(mDrawView.findViewById(R.id.right));
//		Log.i(TAG, "openRight Drawer");
	}
	
	private void close(){
		mDrawerLayout.closeDrawers();
	}
	
	private void makeDrawerLayoutVisible(){
//		Log.i(TAG, "makeDrawerLayoutVisible");
		mDrawViewParams.width = LOCKVIEW_EXTEND_WIDTH;
		mDrawViewParams.x = PER_SCREEN_WIDTH-LOCKVIEW_EXTEND_WIDTH;
		mWindowManager.updateViewLayout(mDrawView, mDrawViewParams);
		
		setLockState(PreferenceUtils.getBoolean(GlobalData.KEY_LOCK, false));
	}
	private void makeDrawerLayoutInVisible(){
		Log.i(TAG, "makeDrawerLayoutInVisible");
		mDrawViewParams.width = LOCKVIEW_WIDTH;
		mDrawViewParams.x = PER_SCREEN_WIDTH - LOCKVIEW_WIDTH;
		mWindowManager.updateViewLayout(mDrawView, mDrawViewParams);
	}
	
	public void removeDrawerLayoutView(Context context){
		 if(mDrawView != null){  
	            getWindowManager(context);  
	            mWindowManager.removeView(mDrawView);  
	            mDrawView = null;  
		 }  
	}
	
	private void setLockState(boolean lockState){
		if (mLock != null){
			mLock.setSelected(lockState);
		}
	}
	
	private LockWndHelper(){
		
	}
	
	private WindowManager getWindowManager(Context context){
		if (mWindowManager == null){
			 mWindowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);  
		}
		return mWindowManager;
	}
	
}
