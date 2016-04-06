/**
 * 
 */
package com.zhcl.ui.music;

import java.util.Vector;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zh.dao.SongInfo;
import com.zh.uitls.L;
import com.zhcl.Adapter.AllMusicListAdapter;
import com.zhcl.media.CurrentPlayManager;
import com.zhcl.media.SongManager;
import com.zhonghong.zhmedia.R;

/** 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author zhonghong.chenli
 * @date 2015-11-21 下午9:09:06 
 */
public class CollectionSongListView extends RelativeLayout{
	private static final String tag = CollectionSongListView.class.getSimpleName();
	Vector<SongInfo> currentPlayMusicList;
	TextView info;
	ListView allMusicList;
	Context context;
	private AllMusicListAdapter mAllMusicListAdapter;
	public CollectionSongListView(Context context) {
		super(context);
		this.context = context;
		init(context);
	}
	
	
	private void init(Context context){
		 LayoutInflater.from(context).inflate(R.layout.current_play_list, this);
		 initView();
		 mAllMusicListAdapter = new AllMusicListAdapter(context);
	}
	
	private void initView(){
		allMusicList = (ListView)findViewById(R.id.allSongList);
		info = (TextView)findViewById(R.id.info);
		this.setBackgroundColor(Color.GRAY);
		allMusicList.setOnItemClickListener(mOnItemClickListener);
	}
	
	/**
	 * 加载当前信息
	 */
	public void load(){
		 LoadMusicAsyncTask mLoadMusicAsyncTask = new LoadMusicAsyncTask();
		 mLoadMusicAsyncTask.execute("go");
	}
	
	/**
	 * 跳到当前播放的歌曲
	 */
	private void toPlayIndex(){
		int index = currentPlayMusicList.indexOf(CurrentPlayManager.getInstance().getSongInfo());
		allMusicList.setSelectionFromTop(index, 0);
	}
	
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			//播放当前存在的歌曲列表
			SongInfo songInfo = currentPlayMusicList.get(arg2);
			if(songInfo.exists()){
				CurrentPlayManager.getInstance().playFromSongMenu(songInfo, SongManager.getInstance().getCollectionExistsSong()/*currentPlayMusicList*/);
			}else{
				Toast.makeText(context, "本地未找到" + songInfo.getTitle(), Toast.LENGTH_SHORT).show();
			}
			
		}
	};
	
	/**
	 * 加载个数类
	 * AsyncTask的3个泛型
	 * Param  传入数据类型
	 * Progress  更新UI数据类型
	 * Result  处理结果类型
	 */
	private class LoadMusicAsyncTask extends AsyncTask<String, Integer, String>{
		 //doInBackground方法内部执行后台任务,不可在此方法内修改UI  
		@Override
		protected String doInBackground(String... params) {
			return CollectionSongListView.this.doInBackground(params);
		} 

		 //onPostExecute方法用于在执行完后台任务后更新UI,显示结果  
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			CollectionSongListView.this.onPostExecute(result);
		}
	}
	
	/**
	 * 子线程中执行
	 * @param params
	 * @return
	 */
	 protected String doInBackground(String... params){
		currentPlayMusicList = SongManager.getInstance().getCollectionSong();
		L.e(tag, "doInBackground  currentPlayMusicList : " + currentPlayMusicList.size());
	    return null;
	 }
	/**
	 * 子线程执行完后执行
	 * @param result
	 */
	protected void onPostExecute(String result) {
		mAllMusicListAdapter.setAllMusic(currentPlayMusicList);
		allMusicList.setAdapter(mAllMusicListAdapter);
		toPlayIndex(); 
		info.setText("收藏歌曲(" + currentPlayMusicList.size() + "首)");
	}
	
	
}
