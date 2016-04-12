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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zh.uitls.L;
import com.zh.uitls.Utils;
import com.zhcl.Adapter.HorizontalListAdapter;
import com.zhcl.dao.VideoInfo;
import com.zhcl.service.PlayerCode;
import com.zhcl.ui.widget.HorizontalList;
import com.zhcl.ui.widget.HorizontalList.ItemOnClickListener;
import com.zhcl.ui.widget.HorizontalList.ItemPlayPauseOnClickListener;
import com.zhcl.video.CurrentPlayManager;
import com.zhcl.video.VideoManager;
import com.zhonghong.zhvideo.R;

/**
 * 所有音乐集合
 * @author ChenLi
 */
public class AllVideoFragment extends VideoListFragmentABS{
	private final String tag = AllVideoFragment.class.getSimpleName();
	private final String TAG = getClass().getSimpleName();
	Context context;
	private LinearLayout bar;
//	private GridView allVideoList;
	private HorizontalList allVideoList;
	private RelativeLayout allVideoAll;
	private TextView info;
	private ProgressBar load;
	/**自定义水平列表*/
//	private MyHorizontalScollView mHorizontalScollView;
	
	private Vector<VideoInfo> allVideo;
	private HorizontalListAdapter mHorizontalListAdapter;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		L.i(tag, "onCreate");
	}
	View view ;
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		L.i(tag, "onCreateView");
		context = inflater.getContext();  
		if(view != null){
			ViewGroup parent = (ViewGroup) view.getParent();
			if(parent != null){
				parent.removeView(view);
			}
			loadAllVideo("go");
			return view;
		} 
		isHaveH = false;
		view = inflater.inflate(R.layout.all_video_fragment, container, false);
		initView(view);
		mHorizontalListAdapter = new HorizontalListAdapter(context); 
		loadAllVideo("go");
//		allVideoList.setOnScrollListener(mOnScrollListener);
//		allVideoList.setOnItemClickListener(mOnItemClickListener);
		return view;
	} 
	 
	
	
	/**
	 * 初始化view
	 * @param view
	 */
	private void initView(View view){
		bar = (LinearLayout)view.findViewById(R.id.bar);
		allVideoList = (HorizontalList)view.findViewById(R.id.allVideoList);
		allVideoAll = (RelativeLayout)view.findViewById(R.id.allVideoAll);
		info = (TextView)view.findViewById(R.id.info);
		load = (ProgressBar)view.findViewById(R.id.load);
//		mHorizontalScollView = (MyHorizontalScollView) view.findViewById(R.id.id_horizontalscollview);
	}

	@Override
	public void onResume() {  
		// TODO Auto-generated method stub
		super.onResume();
	} 

	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		L.i(tag, "onLowMemory");
	}
	
/*	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			CurrentPlayManager.getInstance().play(allVideo.get(arg2), allVideo, PlayerCode.VIDEO_PLAY_TYPE_FIRST);
		}
	};*/
	
	private ItemOnClickListener mItemOnClickListener = new ItemOnClickListener() {
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position) {
			CurrentPlayManager.getInstance().play(allVideo.get(position), allVideo, PlayerCode.VIDEO_PLAY_TYPE_FIRST);
		}
	};
	
	private ItemPlayPauseOnClickListener mItemPlayPauseOnClickListener = new ItemPlayPauseOnClickListener() {
		
		@Override
		public void onItemPlayPause(View view, int position) {
			
		}
	};
	
	/**
	 * 所有歌曲加载完成
	 */
	private void loadEnd(){
		allVideoAll.setVisibility(View.VISIBLE);
		load.setVisibility(View.GONE);
	}
	
	boolean isHaveH = false;
	
	/**
	 * 滚动监听
	 */
/*	private OnScrollListener mOnScrollListener = new OnScrollListener() {
		
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// 不滚动时保存当前滚动到的位置  
	        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {  
	                recordPostion(allVideoList, recordPosition);
	        }  
		}
		
		@Override
		public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
			// TODO Auto-generated method stub
			
		}
	};*/
	


	@Override
	protected String doInBackground(String... params) {
		Utils.getInstance().startTime("show歌曲列表");
		while(mHostCallBack.getState() != HostCallBack.STATE_SUCCESS){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		allVideo = VideoManager.getIntance(context).getAllVideo(); 
		Utils.getInstance().endUseTime("show歌曲列表");
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		L.e(tag, "onPostExecute allVideo.size = " + allVideo.size());
		/*if(allVideoList.getAdapter() != null){
			mHorizontalListAdapter.notifyDataSetChanged();
		}else*/
		{
			mHorizontalListAdapter.setAllVideo(allVideo);
			allVideoList.setAdapter(mHorizontalListAdapter);
//			allVideoList.setPlayPauseState(new int[]{2}, new boolean[]{true});
			allVideoList.setPlayPauseState(null, null);
			allVideoList.setItemOnClickListener(mItemOnClickListener);
			allVideoList.setItemPlayPauseOnClickListener(mItemPlayPauseOnClickListener);
		}
		
		/*if(mHorizontalScollView.getAdapter() != null){
			mAllVideoListAdapter.notifyDataSetChanged();
		}else{
			mAllVideoListAdapter.setAllVideo(allVideo);
			mHorizontalScollView.setAdapter(mAllVideoListAdapter);
		}*/
		
		
//		allVideoList.setSelection(recordPosition[0]);
//		allVideoList.setSelectionFromTop(recordPosition[0], recordPosition[1]);
		loadEnd();
	}
	
	/*private void setGridView() {
		L.e(tag, "setGridView");
		int size = mAllVideoListAdapter.getCount();
		Log.i(TAG, "view size = " + size);
		if (size <= 0 )
			return;
//		int length = mAllVideoListAdapter.getView(0, null, null).getWidth();;
		*//**这里的宽度和间距决定列表的布局*//*
		final int length = 150;
		final int space = 5;
		Log.i(TAG, "view length = " + length);
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
	*/
}
