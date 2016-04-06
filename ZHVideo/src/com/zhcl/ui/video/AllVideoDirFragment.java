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
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zh.uitls.L;
import com.zhcl.Adapter.AllVideoDirListAdapter;
import com.zhcl.dao.VideoDir;
import com.zhcl.video.VideoManager;
import com.zhcl.zhvideo.LocalFragmentManager;
import com.zhonghong.zhvideo.R;

/**
 * 所有音乐集合
 * @author ChenLi
 */
public class AllVideoDirFragment extends VideoListFragmentABS{
	private final String tag = "AllDirFragment";
	Context context;
	private LinearLayout bar;
	private ListView allVideoDirList;
	private RelativeLayout allVideoDirAll;
	private TextView info;
	private AllVideoDirListAdapter mAllVideoDirListAdapter;
	private Vector<VideoDir> videoDirList;
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
		view = inflater.inflate(R.layout.all_videodir_fragment, container, false);
		mAllVideoDirListAdapter = new AllVideoDirListAdapter(context);
		initView(view);
		loadAllVideo("go");
		allVideoDirList.setOnScrollListener(mOnScrollListener);
		allVideoDirList.setOnItemClickListener(mOnItemClickListener);
		return view;
	}
	
	private void initView(View view){ 
		bar = (LinearLayout)view.findViewById(R.id.bar);
		allVideoDirList = (ListView)view.findViewById(R.id.allVideoDirList);
		allVideoDirAll = (RelativeLayout)view.findViewById(R.id.allVideoDirAll);
		info = (TextView)view.findViewById(R.id.info);

	} 

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		videoDirList = VideoManager.getIntance(context).getAllVideoDir();
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		if(allVideoDirList.getAdapter() != null){
			mAllVideoDirListAdapter.notifyDataSetChanged();
		}else{
			mAllVideoDirListAdapter.setallVideoDir(videoDirList);
			allVideoDirList.setAdapter(mAllVideoDirListAdapter);
			allVideoDirList.setSelectionFromTop(recordPosition[0], recordPosition[1]);
		}
		info.setText("歌曲文件夹数目:" + videoDirList.size());
	}
	
	/**
	 * 滚动监听
	 */
	private OnScrollListener mOnScrollListener = new OnScrollListener() {
		
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// 不滚动时保存当前滚动到的位置  
	        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {  
	                recordPostion(allVideoDirList, recordPosition);
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
			VideoDir mVideoDir = videoDirList.get(arg2);
			LocalFragmentManager.getInstance().showCurrentDirList(mVideoDir);
			L.e(tag, "列表中个数：" + mVideoDir.getVideoDirSet().size());
		}
	};
}
