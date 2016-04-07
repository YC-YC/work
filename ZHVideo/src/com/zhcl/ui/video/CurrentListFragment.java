/**
 * 
 */
package com.zhcl.ui.video;

import java.util.Vector;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.zh.uitls.L;
import com.zhcl.Adapter.AllVideoListAdapter;
import com.zhcl.dao.VideoInfo;
import com.zhcl.service.PlayerCode;
import com.zhcl.video.CurrentPlayManager;
import com.zhcl.zhvideo.LocalFragmentManager;
import com.zhonghong.zhvideo.R;

/** 
 * 当前歌曲列表（如：当前歌手，当前专辑，当前文件夹）
 * @author zhonghong.chenli
 * @date 2015-11-19 下午10:01:24 
 */
public class CurrentListFragment extends VideoListFragmentABS {
	private final String tag = "CurrentListFragment";
	Context context;
	View view;
	TextView info;
	GridView allVideoList;
	private AllVideoListAdapter mAllVideoListAdapter;
	Vector<VideoInfo> allVideoInfo;
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		L.e(tag, "onCreateView");
		context = inflater.getContext();
		if(view != null){
			ViewGroup parent = (ViewGroup) view.getParent();
			if(parent != null){
				parent.removeView(view);
			}
			loadAllVideo("go");
			return view;
		}
		view = inflater.inflate(R.layout.current_list_fragment, container, false);
		initView(view); 
		mAllVideoListAdapter = new AllVideoListAdapter(context);
		allVideoList.setOnItemClickListener(mOnItemClickListener);
		loadAllVideo("go");
		return view;
	} 
	
	private void initView(View view){ 
		info = (TextView)view.findViewById(R.id.info);
		allVideoList = (GridView)view.findViewById(R.id.allVideoList);
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
			CurrentPlayManager.getInstance().play(allVideoInfo.get(arg2), allVideoInfo, PlayerCode.VIDEO_PLAY_TYPE_FIRST);
		}
	};
	
	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		L.e(tag, "doInBackground");
		LocalFragmentManager.getInstance().loadCurrentList();
		allVideoInfo = LocalFragmentManager.getInstance().getCurrentListAllVideo();
		return null;
	}

	@Override
	protected void onPostExecute(String result) { 
		// TODO Auto-generated method stub
		L.i(tag, "onPostExecute");
		if(allVideoList.getAdapter() != null){
			mAllVideoListAdapter.notifyDataSetChanged();
		}else{
			L.w(tag, "allVideoInfo 个数：" + allVideoInfo.size());
			mAllVideoListAdapter.setAllVideo(allVideoInfo);
			allVideoList.setAdapter(mAllVideoListAdapter);
		}
		info.setText(LocalFragmentManager.getInstance().getCurrentListTitle());
	}
}
