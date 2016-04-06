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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zh.dao.SongInfo;
import com.zh.uitls.L;
import com.zhcl.Adapter.AllMusicListAdapter;
import com.zhcl.media.CurrentPlayManager;
import com.zhcl.media.SongManager;
import com.zhonghong.zhmedia.R;

/**
 * 所有音乐集合
 * 
 * @author ChenLi
 */
public class CollectionFragment extends AudioListFragmentABS {
	private final String tag = CollectionFragment.class.getSimpleName();
	Vector<SongInfo> collectionList;
	TextView info;
	ListView allMusicList;
	Context context;
	private AllMusicListAdapter mAllMusicListAdapter;
	View view;
	private LinearLayout bar;
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = inflater.getContext();
		if (view != null) {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null) {
				parent.removeView(view);
			}
			return view;
		}
		view = inflater.inflate(R.layout.collcetion_music_fragment, container, false);
		mAllMusicListAdapter = new AllMusicListAdapter(context);
		initView(view);
		loadAllSong("go");
		allMusicList.setOnScrollListener(mOnScrollListener);
		allMusicList.setOnItemClickListener(mOnItemClickListener);
		return view;
	}

	private void initView(View view) {
		bar = (LinearLayout)view.findViewById(R.id.bar);
		allMusicList = (ListView)view.findViewById(R.id.allMusicList);
		info = (TextView)view.findViewById(R.id.info);
	}

	/**
	 * 子线程中执行
	 * 
	 * @param params
	 * @return
	 */
	protected String doInBackground(String... params) {
		collectionList = SongManager.getInstance().getCollectionSong();
		L.e(tag, "doInBackground  collectionList : "
				+ collectionList.size());
		return null;
	}

	/**
	 * 子线程执行完后执行
	 * 
	 * @param result
	 */
	protected void onPostExecute(String result) {
		if(mAllMusicListAdapter == null || collectionList == null){
			L.w(tag, "mAllMusicListAdapter == null || collectionList == null");
			return;
		}
		if(allMusicList.getAdapter() != null){
			mAllMusicListAdapter.notifyDataSetChanged();
		}else{
			mAllMusicListAdapter.setAllMusic(collectionList);
			allMusicList.setAdapter(mAllMusicListAdapter);
//			info.setText("收藏歌曲(" + collectionList.size() + "首)");
			allMusicList.setSelectionFromTop(recordPosition[0], recordPosition[1]);
		}
		
	}

	/**
	 * 滚动监听
	 */
	private OnScrollListener mOnScrollListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// 不滚动时保存当前滚动到的位置
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
				recordPostion(allMusicList, recordPosition);
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
			CurrentPlayManager.getInstance().playFromSongMenu(collectionList.get(arg2), collectionList);
		}
	};

	@Override
	public Object notifyInfo(int cmd, Object o) {
		super.notifyInfo(cmd, o);
		switch(cmd){
		case HostCallBack.PLAY_LISTENER_PALY_NEW:
			mAllMusicListAdapter.notifyDataSetChanged();	//通知更新
			break;
		case HostCallBack.PLAY_LISTENER_PALY_COMPLATE:
			break;
		case HostCallBack.PLAY_LISTENER_PALY_STATE_CH:
			break;
		}
		return null;
	}
}
