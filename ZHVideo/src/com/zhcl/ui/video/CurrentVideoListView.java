/**
 * 
 */
package com.zhcl.ui.video;

import java.util.Vector;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zh.uitls.L;
import com.zhcl.Adapter.AllVideoListAdapter;
import com.zhcl.dao.VideoInfo;
import com.zhcl.service.PlayerCode;
import com.zhcl.video.CurrentPlayManager;
import com.zhonghong.zhvideo.R;

/** 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author zhonghong.chenli
 * @date 2015-11-21 下午9:09:06 
 */
public class CurrentVideoListView extends RelativeLayout{
	private static final String tag = "CurrentVideoListView";
	Vector<VideoInfo> currentPlayVideoList;
	TextView info;
	GridView allVideoList;
	Context context;
	private AllVideoListAdapter mAllVideoListAdapter;
	public CurrentVideoListView(Context context) {
		super(context);
		this.context = context;
		init(context);
	}
	
	
	private void init(Context context){
		 LayoutInflater.from(context).inflate(R.layout.current_play_list, this);
		 initView();
		 mAllVideoListAdapter = new AllVideoListAdapter(context);
	}
	
	private void initView(){
		allVideoList = (GridView)findViewById(R.id.allVideoList);
		info = (TextView)findViewById(R.id.info);
		this.setBackgroundColor(Color.GRAY);
		allVideoList.setOnItemClickListener(mOnItemClickListener);
	}
	
	/**
	 * 加载当前信息
	 */
	public void load(){
		 LoadVideoAsyncTask mLoadVideoAsyncTask = new LoadVideoAsyncTask();
		 mLoadVideoAsyncTask.execute("go");
	}
	
	/**
	 * 跳到当前播放的歌曲
	 */
	private void toPlayIndex(){
		int index = currentPlayVideoList.indexOf(CurrentPlayManager.getInstance().getVideoInfo());
//		allVideoList.setSelectionFromTop(index, 0);
	}
	
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			CurrentPlayManager.getInstance().play(currentPlayVideoList.get(arg2), currentPlayVideoList, PlayerCode.VIDEO_PLAY_TYPE_FIRST);
		}
	};
	
	/**
	 * 加载个数类
	 * AsyncTask的3个泛型
	 * Param  传入数据类型
	 * Progress  更新UI数据类型
	 * Result  处理结果类型
	 */
	private class LoadVideoAsyncTask extends AsyncTask<String, Integer, String>{
		 //doInBackground方法内部执行后台任务,不可在此方法内修改UI  
		@Override
		protected String doInBackground(String... params) {
			return CurrentVideoListView.this.doInBackground(params);
		} 

		 //onPostExecute方法用于在执行完后台任务后更新UI,显示结果  
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			CurrentVideoListView.this.onPostExecute(result);
		}
	}
	
	/**
	 * 子线程中执行
	 * @param params
	 * @return
	 */
	 protected String doInBackground(String... params){
		currentPlayVideoList = CurrentPlayManager.getInstance().getVideoMenu();
		L.e(tag, "doInBackground  currentPlayVideoList : " + currentPlayVideoList.size());
	    return null;
	 }
	/**
	 * 子线程执行完后执行
	 * @param result
	 */
	protected void onPostExecute(String result) {
		if(allVideoList.getAdapter() != null){
			mAllVideoListAdapter.notifyDataSetChanged();
		}else{
			mAllVideoListAdapter.setAllVideo(currentPlayVideoList);
			allVideoList.setAdapter(mAllVideoListAdapter);
			setGridView();
			toPlayIndex(); 
			info.setText("当前播放列表(" + currentPlayVideoList.size() + "个)"); 
		}
	}
	
	private void setGridView() {
		L.e(tag, "setGridView");
		int size = mAllVideoListAdapter.getCount();
		Log.i(tag, "view size = " + size);
		if (size <= 0 )
			return;
//		int length = mAllVideoListAdapter.getView(0, null, null).getWidth();;
		/**这里的宽度和间距决定列表的布局*/
		final int length = 100;
		final int space = 5;
		Log.i(tag, "view length = " + length);
		float density = context.getResources().getDisplayMetrics().density;
		int gridviewWidth = (int) ((size * length + (size-1)*space) * density);
		int itemWidth = (int) (length * density);
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				gridviewWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
		
		allVideoList.setLayoutParams(params); // 重点
		allVideoList.setColumnWidth(itemWidth); // 重点
		allVideoList.setHorizontalSpacing(space); // 间距
		allVideoList.setStretchMode(GridView.NO_STRETCH);
		allVideoList.setNumColumns(size); // 重点
		
	}
	
	
}
