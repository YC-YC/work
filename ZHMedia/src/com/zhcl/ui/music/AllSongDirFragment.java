/**
 * 
 */
package com.zhcl.ui.music;

import java.util.Vector;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zh.dao.Album;
import com.zh.dao.SongDir;
import com.zh.uitls.L;
import com.zhcl.Adapter.AllSongDirListAdapter;
import com.zhcl.Adapter.AllSongDirListAdapter;
import com.zhcl.media.SongManager;
import com.zhonghong.zhmedia.LocalFragmentManager;
import com.zhonghong.zhmedia.R;

/**
 * 所有音乐集合
 * @author ChenLi
 */
public class AllSongDirFragment extends AudioListFragmentABS{
	private final String tag = AllSongDirFragment.class.getSimpleName();
	Context context;
	private LinearLayout bar;
	private ListView allSongDirList;
	private RelativeLayout allSongDirAll;
	private TextView info;
	private AllSongDirListAdapter mAllSongDirListAdapter;
	private Vector<SongDir> songDirList;
	View view;
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		context = inflater.getContext();
		if(view != null){
			ViewGroup parent = (ViewGroup) view.getParent();
			if(parent != null){
				parent.removeView(view);
			}
			return view;
		}
		view = inflater.inflate(R.layout.all_songdir_fragment, container, false);
		mAllSongDirListAdapter = new AllSongDirListAdapter(context);
		initView(view);
		loadAllSong("go");
		allSongDirList.setOnScrollListener(mOnScrollListener);
		allSongDirList.setOnItemClickListener(mOnItemClickListener);
		return view;
	}
	
	private void initView(View view){ 
		bar = (LinearLayout)view.findViewById(R.id.bar);
		allSongDirList = (ListView)view.findViewById(R.id.allSongDirList);
		allSongDirAll = (RelativeLayout)view.findViewById(R.id.allSongDirAll);
		info = (TextView)view.findViewById(R.id.info);

	} 

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		songDirList = SongManager.getInstance().getAllSongDir();
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		if(allSongDirList == null){
			L.w(tag, "allSongDirList == null");
			return;
		}
		if(allSongDirList.getAdapter() != null){
			mAllSongDirListAdapter.notifyDataSetChanged();
		}else{
			mAllSongDirListAdapter.setallSongDir(songDirList);
			allSongDirList.setAdapter(mAllSongDirListAdapter);
			allSongDirList.setSelectionFromTop(recordPosition[0], recordPosition[1]);
		}
		info.setText("歌曲文件夹数目:" + songDirList.size());
	}
	
	/**
	 * 滚动监听
	 */
	private OnScrollListener mOnScrollListener = new OnScrollListener() {
		
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// 不滚动时保存当前滚动到的位置  
	        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {  
	                recordPostion(allSongDirList, recordPosition);
	        }  
		}
		
		@Override
		public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
			// TODO Auto-generated method stub
			
		}
	};
	
	/**
	 * 列表监听
	 */
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			SongDir mSongDir = songDirList.get(arg2);
			LocalFragmentManager.getInstance().showCurrentDirList(mSongDir);
		}
	};
	
}
