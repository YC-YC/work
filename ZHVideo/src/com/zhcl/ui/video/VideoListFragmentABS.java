/**
 * 
 */
package com.zhcl.ui.video;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;

import com.zh.uitls.L;


/**
 * @author ChenLi
 *
 */
public abstract class VideoListFragmentABS extends Fragment implements ChildCallBack{
	private static final String tag = "VideoListFragmentABS";

	/**
	 * 异步加载对象
	 */
	protected LoadMusicAsyncTask mLoadMusicAsyncTask;
	
	/** 宿主回调对象 */
	protected HostCallBack mHostCallBack;
	/** 定位位置 */
	protected int[] recordPosition = new int[2];
	
	/**
	 * 加载所有歌曲
	 */
	protected void loadAllVideo(String cmd){
		mLoadMusicAsyncTask = new LoadMusicAsyncTask();
		mLoadMusicAsyncTask.execute(cmd);
	}
	
	/**
	 * 加载个数类
	 * @author ChenLi
	 * AsyncTask的3个泛型
	 * Param  传入数据类型
	 * Progress  更新UI数据类型
	 * Result  处理结果类型
	 */
	private class LoadMusicAsyncTask extends AsyncTask<String, Integer, String>{
		 //doInBackground方法内部执行后台任务,不可在此方法内修改UI  
		@Override
		protected String doInBackground(String... params) {
			return VideoListFragmentABS.this.doInBackground(params);
		} 

		 //onPostExecute方法用于在执行完后台任务后更新UI,显示结果  
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			VideoListFragmentABS.this.onPostExecute(result);
		}
	}
	
	/**
	 * 记录最后的位置
	 * position : top
	 */
	protected int[] recordPostion(ListView listView, int[] recordPosition){
		View view = listView.getChildAt(0);
		recordPosition[0] = listView.getFirstVisiblePosition();
		recordPosition[1] = (view == null) ? 0 : view.getTop();
		return recordPosition;
	} 
	
	/**
	 * 记录最后的位置
	 * position : top
	 */
	protected int[] recordPostion(GridView listView, int[] recordPosition){
		View view = listView.getChildAt(0);
		recordPosition[0] = listView.getFirstVisiblePosition();
		recordPosition[1] = (view == null) ? 0 : view.getTop();
		return recordPosition;
	} 
	
	
	/**
	 * 停止滚动
	 */
	protected void stopScroll(ListView listView){
		L.i(tag, "需添加停止滚动, 暂未找到方法，预留");
	}
	
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mHostCallBack = (HostCallBack)activity;	//得到宿主
		mHostCallBack.addChildCallBack(this);	//向宿主注册回调
	}
	
	@Override
	public Object notifyInfo(int cmd, Object o) {
		Object result = null;
		switch(cmd){
		case HostCallBack.STATE_SUCCESS:
//			L.e(tag, "host notify scanf OK !");
			loadAllVideo("go");
			break;
		}
		
		return result;  
	}
	
	/**
	 * 子线程中执行
	 * @param params
	 * @return
	 */
	abstract protected String doInBackground(String... params);
	/**
	 * 子线程执行完后执行
	 * @param result
	 */
	abstract protected void onPostExecute(String result);
}
