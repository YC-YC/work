/**
 * 
 */
package com.zhcl.ui.music;

import java.util.Vector;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

import com.zh.dao.Album;
import com.zh.dao.Singer;
import com.zh.uitls.L;
import com.zh.uitls.Utils;
import com.zhcl.Adapter.AllAlbumListAdapter;
import com.zhcl.media.SongManager;
import com.zhonghong.zhmedia.LocalFragmentManager;
import com.zhonghong.zhmedia.R;

/**
 * 所有音乐集合
 * @author ChenLi
 */
public class AllAlbumFragment extends AudioListFragmentABS{
	private final String tag = AllAlbumFragment.class.getSimpleName();
	Context context;
	private LinearLayout bar;
	private ListView allAlbumList;
	private RelativeLayout allAlbumAll;
	private TextView info;
	private AllAlbumListAdapter mAllAlbumListAdapter;
	private Vector<Album> albumList;
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
		view = inflater.inflate(R.layout.all_album_fragment, container, false);
		mAllAlbumListAdapter = new AllAlbumListAdapter(context);
		initView(view);
		loadAllSong("go");
		allAlbumList.setOnScrollListener(mOnScrollListener);
		allAlbumList.setOnItemClickListener(mOnItemClickListener);
		return view;
	}
	
	private void initView(View view){ 
		bar = (LinearLayout)view.findViewById(R.id.bar);
		allAlbumList = (ListView)view.findViewById(R.id.allAlbumList);
		allAlbumAll = (RelativeLayout)view.findViewById(R.id.allAlbumAll);
		info = (TextView)view.findViewById(R.id.info);
	} 

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		albumList = SongManager.getInstance().getAllAlbum();
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		if(allAlbumList == null){
			L.e(tag, "allAlbumList == null");
			return;
		}
		if(allAlbumList.getAdapter() != null){
			mAllAlbumListAdapter.notifyDataSetChanged();
		}else{
			mAllAlbumListAdapter.setallAlbum(albumList);
			allAlbumList.setAdapter(mAllAlbumListAdapter);
			allAlbumList.setSelectionFromTop(recordPosition[0], recordPosition[1]);
		}
		info.setText("专辑数目:" + albumList.size());
	}
	
	/**
	 * 滚动监听
	 */
	private OnScrollListener mOnScrollListener = new OnScrollListener() {
		
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// 不滚动时保存当前滚动到的位置  
	        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {  
	                recordPostion(allAlbumList, recordPosition);
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
			Album mAlbum = albumList.get(arg2);
			LocalFragmentManager.getInstance().showCurrentAlbumList(mAlbum);
		}
	};
	
	
}
