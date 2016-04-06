/**
 * 
 */
package com.zhcl.ui.music;

import java.util.Vector;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

import com.zh.dao.Singer;
import com.zh.uitls.L;
import com.zh.uitls.Utils;
import com.zhcl.Adapter.AllSingerListAdapter;
import com.zhcl.media.CurrentPlayManager;
import com.zhcl.media.SongManager;
import com.zhonghong.zhmedia.LocalFragmentManager;
import com.zhonghong.zhmedia.R;

/**
 * 所有音乐集合
 * @author ChenLi
 */
public class AllSingerFragment extends AudioListFragmentABS {
	private final String tag = AllSingerFragment.class.getSimpleName();
	Context context;
	private LinearLayout bar;
	private ListView allSingerList;
	private RelativeLayout allSingerAll;
	private TextView info;
	private AllSingerListAdapter mAllSingerListAdapter;
	private Vector<Singer> singerList;
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
		view = inflater.inflate(R.layout.all_singer_fragment, container, false);
		mAllSingerListAdapter = new AllSingerListAdapter(context);
		initView(view);
		loadAllSong("go");
		allSingerList.setOnScrollListener(mOnScrollListener);
		return view;
	}
	
	private void initView(View view){ 
		bar = (LinearLayout)view.findViewById(R.id.bar);
		allSingerList = (ListView)view.findViewById(R.id.allSingerList);
		allSingerAll = (RelativeLayout)view.findViewById(R.id.allSingerAll);
		info = (TextView)view.findViewById(R.id.info);
	}
	
	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
		L.e(tag, "onLowMemory");
	}
	
	

	@Override
	protected String doInBackground(String... params) {
		singerList = SongManager.getInstance().getAllSinger();
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		if(allSingerList.getAdapter() != null){
			mAllSingerListAdapter.notifyDataSetChanged();
		}else{
			mAllSingerListAdapter.setallSinger(singerList);
			allSingerList.setAdapter(mAllSingerListAdapter);
			allSingerList.setSelectionFromTop(recordPosition[0], recordPosition[1]);
			allSingerList.setOnItemClickListener(mOnItemClickListener);
		}
		info.setText("歌手数目：" + singerList.size());
	}
	
	
	/**
	 * 滚动监听
	 */
	private OnScrollListener mOnScrollListener = new OnScrollListener() {
		
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// 不滚动时保存当前滚动到的位置  
	        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {  
	                recordPostion(allSingerList, recordPosition);
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
			Singer singer = singerList.get(arg2);
			L.i(tag, "准备show歌手列表：" + singer.getmSingerName());
			LocalFragmentManager.getInstance().showCurrentSingerList(singer);
		}
	};
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		L.i(tag, "onDestroy");
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		L.i(tag, "onDestroyView");
	}
	
	
}
