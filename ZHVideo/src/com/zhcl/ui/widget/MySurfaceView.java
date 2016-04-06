/**
 * 
 */
package com.zhcl.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.zh.uitls.L;

/**
 * @author zhonghong.chenli
 * date:2015-12-22下午12:05:17	<br/>
 */
public class MySurfaceView extends SurfaceView {
	private static final String tag = MySurfaceView.class.getSimpleName();
	public MySurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * 构造初始化
	 * @param context
	 */
	private void init(Context context){
		initHolder(getHolder());
	}
	
	/**
	 * 初始化配置
	 * @param holder
	 */
	private void initHolder(SurfaceHolder holder){
		holder.addCallback(mCallback);
		holder.setKeepScreenOn(true);
	}

	/**
	 * 视频生命周期
	 */
	private Callback mCallback = new Callback() {
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			//记录断点
			L.i(tag, "surfaceDestroyed");
		}
		
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			L.e(tag, "surfaceCreated");
//			switch(currentState){
//			case PlayerCode.VIDEO_PLAY_TYPE_FIRST:
//				CurrentPlayManager.getInstance().playRecordCache();
//				break;
//			case PlayerCode.VIDEO_PLAY_TYPE_RECORD:
//				CurrentPlayManager.getInstance().palyRecord();
//				break;
//			case PlayerCode.VIDEO_PLAY_TYPE_RESUME:
//				CurrentPlayManager.getInstance().palyRecord();
//				break;
//			}
//			initSeekBar();
//			currentState = PlayerCode.VIDEO_PLAY_TYPE_RESUME;
		} 
		
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			
		}
	};
}
