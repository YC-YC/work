/**
 * 
 */
package com.zhcl.ui.music;

import java.util.HashMap;
import java.util.Vector;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zh.dao.SongInfo;
import com.zh.uitls.L;
import com.zh.uitls.Utils;
import com.zhcl.Adapter.AllMusicListAdapter;
import com.zhcl.Adapter.AllMusicListAdapter.ViewHolder;
import com.zhcl.media.CurrentPlayManager;
import com.zhcl.media.SongManager;
import com.zhcl.service.PlayerCode;
import com.zhcl.service.service.client.AudioPlayerClient;
import com.zhonghong.zhmedia.R;

/**
 * 所有音乐集合
 * @author ChenLi
 */
public class AllMusicFragment extends AudioListFragmentABS{
	private final String tag = AllMusicFragment.class.getSimpleName();
	Context context;
	private LinearLayout bar;
	private ListView allMusicList;
	private RelativeLayout allMusicAll;
	private TextView info;
	private ProgressBar load;
	
	private Vector<SongInfo> allSong;
	private HashMap<String, Integer> abcIndex;
	private AllMusicListAdapter mAllMusicListAdapter;
	private LinearLayout abcIndexLayout;
	private String[] indexStr = { "A", "B", "C", "D", "E", "F", "G", "H",
			"I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
			"V", "W", "X", "Y", "Z", "#" };
	private int adbH;
	private TextView indexABCTip;
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
			return view;
		}  
		L.e(tag, "onCreateView true");
		isHaveH = false;
		view = inflater.inflate(R.layout.all_music_fragment, container, false);
		initView(view);
		mAllMusicListAdapter = new AllMusicListAdapter(context); 
		loadAllSong("go");
		allMusicAll.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener); //监听背景大小改变字母表
		allMusicList.setOnScrollListener(mOnScrollListener);
		allMusicList.setOnItemClickListener(mOnItemClickListener);
		return view;
	} 
	 
	/**
	 * 初始化view
	 * @param view
	 */
	private void initView(View view){
		bar = (LinearLayout)view.findViewById(R.id.bar);
		allMusicList = (ListView)view.findViewById(R.id.allMusicList);
		allMusicAll = (RelativeLayout)view.findViewById(R.id.allMusicAll);
		info = (TextView)view.findViewById(R.id.info);
		abcIndexLayout = (LinearLayout)view.findViewById(R.id.abcIndex);
		load = (ProgressBar)view.findViewById(R.id.load);
		indexABCTip = (TextView)view.findViewById(R.id.indexABCTip);
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
	
	
	
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			CurrentPlayManager.getInstance().playFromSongMenu(allSong.get(arg2), allSong);
		}
	};
	  
	/**
	 * 所有歌曲加载完成
	 */
	private void loadEnd(){
		allMusicAll.setVisibility(View.VISIBLE);
		load.setVisibility(View.GONE);
	}
	
	boolean isHaveH = false;
	/**
	 * 监听Viewlayout，用于取得索引表大小
	 */
	private OnGlobalLayoutListener mOnGlobalLayoutListener = new OnGlobalLayoutListener() {
		
		@Override
		public void onGlobalLayout() { 
			if(abcIndexLayout.getHeight() > 0){
				if(!isHaveH){
					adbH = abcIndexLayout.getHeight() / indexStr.length;
					isHaveH  = true;
					getIndexView();
				}
			}
		} 
	};
	
	
	/**
	 * 绘制索引列表
	 */
	private void getIndexView() {
		LinearLayout.LayoutParams params = new LayoutParams(
				LayoutParams.WRAP_CONTENT, adbH);
		for (int i = 0; i < indexStr.length; i++) {
			final TextView tv = new TextView(context);
			tv.setLayoutParams(params);
			tv.setText(indexStr[i]);
			tv.setPadding(10, 0, 10, 0);
			tv.setTextColor(getResources().getColor(R.color.indexabc)); 
			abcIndexLayout.addView(tv);
		}
		abcIndexLayout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				float y = event.getY();
				int index = (int) (y / adbH);
				if (index > -1 && index < indexStr.length) {// 防止越界
					String key = indexStr[index];
					if (abcIndex.containsKey(key)) {
						int pos = abcIndex.get(key);
						if (allMusicList.getHeaderViewsCount() > 0) {// 防止ListView有标题栏，本例中没有。
							allMusicList.setSelectionFromTop(pos + allMusicList.getHeaderViewsCount(), 0);
						} else { 
							allMusicList.setSelectionFromTop(pos, 0);// 滑动到第一项
						} 
//						tv_show.setVisibility(View.VISIBLE);
//						tv_show.setText(indexStr[index]);
					}
					indexABCTip.setText(key);
				}
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
//					layoutIndex.setBackgroundColor(Color.parseColor("#606060"));
					stopScroll(allMusicList);
					indexABCTip.setVisibility(View.VISIBLE);
					break;

				case MotionEvent.ACTION_MOVE:
					
					break;
				case MotionEvent.ACTION_UP:
//					layoutIndex.setBackgroundColor(Color.parseColor("#00ffffff"));
//					tv_show.setVisibility(View.GONE);
					hideABCTip();
					recordPostion(allMusicList, recordPosition);
					break;
				}
				return true;
			}
		});
	}
	
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		L.e(tag, "setUserVisibleHint：" + isVisibleToUser);
		if(!isVisibleToUser){
			hideABCTip();
		}
	}
	
	/**
	 * 隐藏字母提示
	 */
	private void hideABCTip(){
		if(indexABCTip != null){
			indexABCTip.setVisibility(View.GONE);
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
			updataIndexAbcLight();
		}
	};
	
	private void updataIndexAbcLight(){
		if(abcIndexLayout != null){
			for(int i = 0; i < abcIndexLayout.getChildCount(); i++){
				((TextView)abcIndexLayout.getChildAt(i)).setTextColor(getResources().getColor(R.color.indexabc));
			}
		}else{
			return;
		}
		if(allSong == null || allSong.isEmpty()){
			return ;
		}
		int index = allMusicList.getFirstVisiblePosition();
		//取首字母
		if(allSong.size() <= index){
			L.w(tag, "插拔过程可能出现数组越界");
			return;
		}
		SongInfo songInfo = allSong.get(index);
		if(songInfo == null){
			return ;
		}
		char key = songInfo.getPinyin().charAt(0);
//		Log.e(tag, "key = " + (int)key);
		TextView indexT = ((TextView)abcIndexLayout.getChildAt(key - 65));
		if(indexT != null){
			indexT.setTextColor(getResources().getColor(R.color.holo_green_light_new));
		}
		
	}

	

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		Utils.getInstance().startTime("show歌曲列表");
		while(mHostCallBack != null && mHostCallBack.getState() != HostCallBack.STATE_SUCCESS){
//			L.e(tag, "doInBackground ,mHostCallBack = " + mHostCallBack  + " mHostCallBack.getState() = " + mHostCallBack.getState());
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block 
				e.printStackTrace();
			}
		} 
		allSong = SongManager.getInstance().getAllSong(); 
		abcIndex = SongManager.getInstance().getAllSongABCIndex();
		Utils.getInstance().endUseTime("show歌曲列表");
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		L.e(tag, "onPostExecute");
		if(allMusicList.getAdapter() != null){
			L.e(tag, "allMusicList.getAdapter()");
			mAllMusicListAdapter.notifyDataSetChanged();
		}else{
			L.e(tag, "allMusicList.setAllMusic()");
			mAllMusicListAdapter.setAllMusic(allSong);
			allMusicList.setAdapter(mAllMusicListAdapter);
			allMusicList.setSelectionFromTop(recordPosition[0], recordPosition[1]);
		}
		loadEnd();
		info.setText("歌曲数目：" + allSong.size() + "，新加数目：" + SongManager.getInstance().getNewAddSongNum()
				+ ", 删除数目：" + SongManager.getInstance().getNewDelSongNum()
				);
		updataIndexAbcLight();
	}
	
	
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
		case PlayerCode.NOTIFY_ORIENTATION_LANDSCAPE:
			break;
		}
		return null;
	}
	
}
