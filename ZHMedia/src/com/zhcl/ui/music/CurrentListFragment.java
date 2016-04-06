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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.zh.dao.SongInfo;
import com.zh.uitls.L;
import com.zh.uitls.Utils;
import com.zhcl.Adapter.AllMusicListAdapter;
import com.zhcl.media.CurrentPlayManager;
import com.zhonghong.zhmedia.LocalFragmentManager;
import com.zhonghong.zhmedia.R;

/** 
 * 当前歌曲列表（如：当前歌手，当前专辑，当前文件夹）
 * @author zhonghong.chenli
 * @date 2015-11-19 下午10:01:24 
 */
public class CurrentListFragment extends AudioListFragmentABS {
	private final String tag = CurrentListFragment.class.getSimpleName();
	Context context;
	View view;
	TextView info;
	ListView allMusicList;
	private AllMusicListAdapter mAllMusicListAdapter;
	Vector<SongInfo> allSongInfo;
	/** 顶部预留view */
	View topBase;
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		context = inflater.getContext();
		L.e(tag, "onCreateView");
		if(view != null){
			ViewGroup parent = (ViewGroup) view.getParent();
			if(parent != null){
				parent.removeView(view);
			}
			allMusicList.setVisibility(View.INVISIBLE);
			loadAllSong("go");
			return view;
		}
		view = inflater.inflate(R.layout.current_list_fragment, container, false);
		initView(view); 
		mAllMusicListAdapter = new AllMusicListAdapter(context);
		allMusicList.setOnItemClickListener(mOnItemClickListener);
		loadAllSong("go");
		return view;
	} 
	
	private void initView(View view){ 
		info = (TextView)view.findViewById(R.id.info);
		allMusicList = (ListView)view.findViewById(R.id.allSongList);
		topBase = (View)view.findViewById(R.id.topBase);
		Utils.getInstance().updateViewHToStatusH(topBase);
	}  
	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		L.e(tag, "onDestroyView");
		info.setText("");
	}

	//歌曲单击
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			CurrentPlayManager.getInstance().playFromSongMenu(allSongInfo.get(arg2), allSongInfo);
		}
	};
	
	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		L.e(tag, "doInBackground");
		LocalFragmentManager.getInstance().loadCurrentList();
		allSongInfo = LocalFragmentManager.getInstance().getCurrentListAllSong();
		return null;
	}

	@Override
	protected void onPostExecute(String result) { 
		// TODO Auto-generated method stub
		L.i(tag, "onPostExecute");
//		mAllMusicListAdapter.notifyDataSetChanged();
		mAllMusicListAdapter.setAllMusic(allSongInfo);
		allMusicList.setAdapter(mAllMusicListAdapter);
		info.setText(LocalFragmentManager.getInstance().getCurrentListTitle());
		allMusicList.setVisibility(View.VISIBLE);
		L.w(tag, "allSongInfo 个数：" + allSongInfo.size());
	}

	@Override
	public Object notifyInfo(int cmd, Object o) {
		super.notifyInfo(cmd, o);
		switch(cmd){
		case HostCallBack.PLAY_LISTENER_PALY_NEW:
			if(mAllMusicListAdapter != null){
				mAllMusicListAdapter.notifyDataSetChanged();	//通知更新
			}
			break;
		case HostCallBack.PLAY_LISTENER_PALY_COMPLATE:
			break;
		case HostCallBack.PLAY_LISTENER_PALY_STATE_CH:
			break;
		}
		return null;
	}
	
	
}
