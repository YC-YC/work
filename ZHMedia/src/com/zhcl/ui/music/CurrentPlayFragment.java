/**
 * 
 */
package com.zhcl.ui.music;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zh.dao.SongInfo;
import com.zh.uitls.L;
import com.zh.uitls.Utils;
import com.zhcl.media.CurrentPlayManager;
import com.zhcl.service.PlayerCode;
import com.zhcl.ui.widget.shader.CircularImageView;
import com.zhonghong.zhmedia.LocalFragmentManager;
import com.zhonghong.zhmedia.R;

/** 
 * 期望最后的UI把音乐变成一本书或一本日记
 * @author zhonghong.chenli
 * @date 2015-11-19 下午10:01:24 
 */
@SuppressLint("HandlerLeak")
public class CurrentPlayFragment extends BaseFragmentABS {
	private final String tag = CurrentPlayFragment.class.getSimpleName();
	private Context context;
	private View view;
	private ImageButton menu;
	private ImageButton playPause;
	private TextView songName;
	private TextView singer;
	private CircularImageView musicImage;
	
	private ImageButton search;
	private Button online;
	/**
	 * 当前播放歌单
	 */
	private CurrentSongListView mCurrentSongListView;
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		context = inflater.getContext();
		if(view != null){
			ViewGroup parent = (ViewGroup) view.getParent();
			if(parent != null){
				parent.removeView(view);
			}
			initDataShow();
			return view;
		}
		view = inflater.inflate(R.layout.current_play_fragment, container, false);
		initView(view);
		return view;
	} 
	
	private void initView(View view){ 
		menu = (ImageButton)view.findViewById(R.id.menu);
		playPause = (ImageButton)view.findViewById(R.id.playPause);
		songName = (TextView)view.findViewById(R.id.musicName);
		singer = (TextView)view.findViewById(R.id.singer);
		playPause.setOnClickListener(mOnClickListener);
		menu.setOnClickListener(mOnClickListener);
		view.setOnClickListener(mOnClickListener);
		mCurrentSongListView = new CurrentSongListView(context);
		musicImage = (CircularImageView)view.findViewById(R.id.musicImage);
		
		search = (ImageButton)view.findViewById(R.id.search);
		online = (Button)view.findViewById(R.id.online);
		search.setOnClickListener(mOnClickListener);
		online.setOnClickListener(mOnClickListener);
		initDataShow();
	}  
	
	/**
	 * 初始化数据显示
	 */
	private void initDataShow(){
		if(songName == null){
			return;
		}
		SongInfo mSongInfo = CurrentPlayManager.getInstance().getSongInfo();
		if(mSongInfo != null){
			songName.setText(mSongInfo.getTitle());
			singer.setText(mSongInfo.getSinger());
			updataPlayState();
			Bitmap bitmap = CurrentPlayManager.getInstance().getCurrentMusicImage();
			if(bitmap != null){
				musicImage.setImageBitmap(CurrentPlayManager.getInstance().getCurrentMusicImage());
			}else{
				musicImage.setImageResource(R.drawable.widget_qqmusic_default_album_small);
			} 
		}else{
			songName.setText("未选择歌曲");
			singer.setText("未选择歌曲");
			updataPlayState();
			musicImage.setImageResource(R.drawable.widget_qqmusic_default_album_small);
		}
	}
	
	
	/**
	 * 更新播放状态
	 */
	private void updataPlayState(){
		if(playPause == null){
			return;
		}
		if(CurrentPlayManager.getInstance().isPlay()){
			playPause.setImageResource(R.drawable.minibar_btn_pause_xml);
		}else{
			playPause.setImageResource(R.drawable.minibar_btn_play_xml);
		}
	}
	
	/**
	 * 播放监听
	 */
	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {   
			switch(v.getId()){
			case R.id.menu:
				CurrentPlayManager.getInstance().showCurrentMusicMenu(context);
				break;
			case R.id.playPause:
				CurrentPlayManager.getInstance().playPause();
				updataPlayState();
				break;
			case R.id.search:		//搜索键
				L.e(tag, "搜索");
//				SongManager.getIntance(context).querySong("a");
				LocalFragmentManager.getInstance().showSearchPage();
				break;
			case R.id.online:		//在线
				LocalFragmentManager.getInstance().showOnlinePage();
				break;
			}
			if(v.equals(view)){
				showCurrentPlayAllInfo();
			}
		} 
	};
	
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		L.e(tag, "onDestroyView");
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		L.e(tag, "onDestroy");
	}

	/** 当前完整的播放界面 */
	private CurrentPlayAllInfoFragment mCurrentPlayAllInfoFragment;
	
	/**
	 * show当前播放界面完整的界面
	 */
	public boolean showCurrentPlayAllInfo(){
		if(mCurrentPlayAllInfoFragment == null){
			mCurrentPlayAllInfoFragment = new CurrentPlayAllInfoFragment();
		}
		if(mCurrentPlayAllInfoFragment.isVisible()){
			return false;
		}
		FragmentManager mFragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		//从currentFragment替换成currenBody 原因是，会照成进入CurrentPlayAllInfoFragment慢，于是添加了隐藏和显示接口
		ft.replace(R.id.currenBody /*currentFragment*/, mCurrentPlayAllInfoFragment, mCurrentPlayAllInfoFragment.hashCode() + "");
		ft.addToBackStack(null);
		ft.commit();
		return true;
	}
	
	@Override
	public Object notifyInfo(int cmd, Object o) {
		switch(cmd){
		case HostCallBack.PLAY_LISTENER_PALY_NEW:
			initDataShow();
			break;
		case HostCallBack.PLAY_LISTENER_PALY_COMPLATE:
			break;
		case HostCallBack.PLAY_LISTENER_PALY_STATE_CH:
			updataPlayState();
			break;
		case HostCallBack.PLAY_LISTENER_READ_RECORD:
			initDataShow();
			break;
		case HostCallBack.REQUESY_SHOW_CURRENT_PLAYBAR:				//用作和当前播放界面相互切换
			if(view != null){
				L.i(tag, "申请显示currentBar");
				view.setVisibility(View.VISIBLE);
			}
			break;
		case HostCallBack.REQUESY_HIDE_CURRENT_PLAYBAR:
			if(view != null){
				L.i(tag, "申请隐藏currentBar");
				view.setVisibility(View.GONE);
			}
			break;
		case PlayerCode.NOTIFY_DEVICE_EJECT:
			initDataShow();
			break;
		case PlayerCode.NOTIFY_DEVICE_MOUNT:
			break;
		case HostCallBack.REQUESY_SHOW_CURRENT_PLAYPAGE:
			L.e(tag, "REQUESY_SHOW_CURRENT_PLAYPAGE");
			showCurrentPlayAllInfo();
			break;
		}
		return null;
	}
}
