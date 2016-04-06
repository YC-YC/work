/**
 * 
 */
package com.zhcl.ui.music;

import java.util.Vector;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.zh.dao.SongInfo;
import com.zh.uitls.L;
import com.zh.uitls.Utils;
import com.zhcl.Adapter.AllMusicListAdapter;
import com.zhcl.media.CurrentPlayManager;
import com.zhcl.media.SongManager;
import com.zhonghong.zhmedia.R;

/** 
 * 当前歌曲列表（如：当前歌手，当前专辑，当前文件夹）
 * @author zhonghong.chenli
 * @date 2015-11-19 下午10:01:24 
 */
public class LocalSearchFragment extends AudioListFragmentABS {
	private final String tag = "LocalSearchFragment";
	Context context;
	View view;
	EditText info;
	ListView allMusicList;
	private AllMusicListAdapter mAllMusicListAdapter;
	Vector<SongInfo> allSongInfo;
	ImageButton back;
	/** 顶部预留view */
	View topBase;
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		L.e(tag, "onCreateView");
		context = inflater.getContext();
		if(view != null){
			ViewGroup parent = (ViewGroup) view.getParent();
			if(parent != null){
				parent.removeView(view);
			}
			into();
			return view;
		}
		view = inflater.inflate(R.layout.local_search_fragment, container, false);
		initView(view); 
		mAllMusicListAdapter = new AllMusicListAdapter(context);
		allMusicList.setOnItemClickListener(mOnItemClickListener);
		into();
		return view;
	} 
	
	/**
	 * 进入界面执行操作
	 */
	private void into(){
		if(info == null){
			return;
		}
		loadAllSong("go");
		info.requestFocus();
		//打开键盘
	}
	
	private void exit(){
		if(info == null){
			return;
		}
		//关闭键盘
		info.setText("");
	}
	
	private void showKeyboard(View view){
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);  
		imm.showSoftInput(view,InputMethodManager.SHOW_FORCED);  
	}
	
	
	private void initView(View view){ 
		info = (EditText)view.findViewById(R.id.info);
		allMusicList = (ListView)view.findViewById(R.id.allSongList);
		back = (ImageButton)view.findViewById(R.id.back);
		
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager mFragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
				mFragmentManager.popBackStack();
			}
		});
		
		info.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				L.e(tag, "onTextChanged count= " + count);
				allSongInfo = SongManager.getInstance().querySong(info.getText().toString());
				mAllMusicListAdapter.setAllMusic(allSongInfo);
				allMusicList.setAdapter(mAllMusicListAdapter);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
//				L.e(tag, "beforeTextChanged");
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
//				L.e(tag, "afterTextChanged");
			}
		});
		topBase = (View)view.findViewById(R.id.topBase);
		Utils.getInstance().updateViewHToStatusH(topBase);
	}  
	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		exit();
		super.onDestroyView();
		L.e(tag, "onDestroyView");
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
		return null;
	}

	@Override
	protected void onPostExecute(String result) { 
		// TODO Auto-generated method stub
		L.i(tag, "onPostExecute");
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
		}
		return null;
	}
}
