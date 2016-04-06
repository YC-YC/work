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
import com.zhcl.Adapter.AllMusicListAdapter;
import com.zhcl.media.CurrentPlayManager;
import com.zhcl.service.PlayerCode;
import com.zhonghong.zhmedia.R;

/**
 * 当前歌单
 * @author zhonghong.chenli
 * date:2015-12-18下午2:05:44	<br/>
 */
public class Fragment3 extends AudioListFragmentABS{
	private final String tag = Fragment3.class.getSimpleName();
	Context context;
	View view;
	ListView allMusicList;
	private AllMusicListAdapter mAllMusicListAdapter;
	Vector<SongInfo> currentPlayMusicList;
	TextView info;
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(view != null){
			ViewGroup parent = (ViewGroup) view.getParent();
			if(parent != null){
				parent.removeView(view);
			}
			loadAllSong("go");
			return view;
		}
		loadAllSong("go");
		view = inflater.inflate(R.layout.layout3, container, false);
		context = inflater.getContext();
		initView(view);
		mAllMusicListAdapter = new AllMusicListAdapter(context);
		allMusicList.setOnItemClickListener(mOnItemClickListener);
		return view;
	}
	
	
	private void initView(View view) {
		// TODO Auto-generated method stub
		info = (TextView)view.findViewById(R.id.info);
		allMusicList = (ListView)view.findViewById(R.id.list);
		
	}
	
	
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			CurrentPlayManager.getInstance().playFromSongMenu(currentPlayMusicList.get(arg2), currentPlayMusicList);
		}
	};
	
	/**
	 * 跳到当前播放的歌曲
	 */
	private void toPlayIndex(){
		if(currentPlayMusicList == null){
			return;
		}
		int index = currentPlayMusicList.indexOf(CurrentPlayManager.getInstance().getSongInfo());
		allMusicList.setSelectionFromTop(index, 0);
	}
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		L.e(tag, "setUserVisibleHint：" + isVisibleToUser);
		if(isVisibleToUser){
			toPlayIndex(); 
		}else{
			toPlayIndex(); 
		}
	}
		
	/**
	 * 子线程中执行
	 * @param params
	 * @return
	 */
	 protected String doInBackground(String... params){
		currentPlayMusicList = CurrentPlayManager.getInstance().getSongMenu();
		L.e(tag, "doInBackground  currentPlayMusicList : " + currentPlayMusicList.size());
	    return null;
	 }
	/**
	 * 子线程执行完后执行
	 * @param result
	 */
	protected void onPostExecute(String result) {
		if(allMusicList.getAdapter() != null){
			mAllMusicListAdapter.notifyDataSetChanged();
		}else{
			mAllMusicListAdapter.setAllMusic(currentPlayMusicList);
			allMusicList.setAdapter(mAllMusicListAdapter);
		}
		toPlayIndex(); 
		info.setText("当前歌单 - " + currentPlayMusicList.size() + "首");
	}

	@Override
	public Object notifyInfo(int cmd, Object o) {
		super.notifyInfo(cmd, o);
		switch(cmd){
		case HostCallBack.PLAY_LISTENER_PALY_NEW:
			if(mAllMusicListAdapter != null){
				mAllMusicListAdapter.notifyDataSetChanged();	//通知更新
				info.setText("当前歌单 - " + currentPlayMusicList.size() + "首");
			}
			break;
		case HostCallBack.PLAY_LISTENER_PALY_COMPLATE:
			break;
		case HostCallBack.PLAY_LISTENER_PALY_STATE_CH:
			break;
			
//加这个是因为当父viewpager所在的fragment销毁view时本fragment监听不到
		case HostCallBack.REQUESY_SHOW_CURRENT_PLAYBAR:				//用作和当前播放界面相互切换
			
			break;
		case HostCallBack.REQUESY_HIDE_CURRENT_PLAYBAR:
			toPlayIndex(); 
			break;
		}
		return null;
	}
}
