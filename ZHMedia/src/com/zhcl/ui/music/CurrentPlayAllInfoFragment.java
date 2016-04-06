/**
 * 
 */
package com.zhcl.ui.music;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.zh.dao.SongInfo;
import com.zh.uitls.DelayExecute;
import com.zh.uitls.DelayExecute.Execute;
import com.zh.uitls.L;
import com.zh.uitls.ThreadForeverManage;
import com.zh.uitls.ThreadForeverManage.ThreadForever;
import com.zh.uitls.Utils;
import com.zhcl.media.CurrentPlayManager;
import com.zhcl.media.SongManager.CollectionCallBack;
import com.zhcl.service.PlayerCode;
import com.zhcl.service.service.client.AudioPlayerClient;
import com.zhcl.ui.widget.shader.CircularImageView;
import com.zhonghong.zhmedia.R;

/** 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author zhonghong.chenli
 * @date 2015-11-19 下午10:01:24 
 */
@SuppressLint("HandlerLeak")
public class CurrentPlayAllInfoFragment extends BaseFragmentABS implements ThreadForever{
	private final String tag = CurrentPlayAllInfoFragment.class.getSimpleName();
	private Context context;
	private View view;
	private AlertDialog dialog;
	/** 后台线程是否正在执行 */
	private boolean isBackThreadRun;
	private Handler hander;
	/** 更新时间 */
	private final int UI_UPDATA_TIME = 1;
	/** 当前播放歌单 */
	private CurrentSongListView mCurrentSongListView;
	/** 是否允许改变当前时间 */
	private boolean isAllowChangeTime = true;
	private CircularImageView musicPhoto;
	private TextView musicName;
	private TextView musicSinger;
	private ImageButton preBut;
	private ImageButton playPauseBut;
	private ImageButton nextBut;
	private SeekBar seekbar;
	private TextView currentTime;
	private TextView allTime;
	private ImageButton collection;
	private ImageButton mode;
	private ImageButton menuList;
	/** 当前播放歌曲 */
	private SongInfo mSongInfo;
	private View imageLayout;
	/** 对应的viewPager */
	private ViewPager viewPager;  
	private DelayExecute mDelayExecute;
	/** 多长时间执行一次 */
	private final int DELAY = 1000;
	/** 页面标示view */
	private View page1, page2, page3;
	private View[] pageIndexArray;
	private View shadow;
	/** 顶部预留view */
	View topBase;
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mHostCallBack.connString(HostCallBack.REQUESY_HIDE_CURRENT_PLAYBAR, null);
		ThreadForeverManage.getInstance().register(this);	//注册自己
		context = inflater.getContext();
		if(view != null){
			ViewGroup parent = (ViewGroup) view.getParent();
			if(parent != null){
				parent.removeView(view);
			}
			initDataShow();
			return view;
		}
		hander = new UIHandler();
		view = inflater.inflate(R.layout.current_play_allinfo_fragment, container, false);
		initView(view);
		return view;
	} 
	
	private void initView(View view){ 
		shadow = view.findViewById(R.id.shadow);
		imageLayout = (View)view.findViewById(R.id.imageLayout);
		musicPhoto = (CircularImageView)view.findViewById(R.id.musicPhoto);
		musicName = (TextView)view.findViewById(R.id.musicName);
		musicSinger = (TextView)view.findViewById(R.id.musicSinger);
		preBut = (ImageButton)view.findViewById(R.id.preBut);
		playPauseBut = (ImageButton)view.findViewById(R.id.playPauseBut);
		nextBut = (ImageButton)view.findViewById(R.id.nextBut);
		preBut.setOnClickListener(mOnClickListener);
		playPauseBut.setOnClickListener(mOnClickListener);
		nextBut.setOnClickListener(mOnClickListener);
		seekbar = (SeekBar)view.findViewById(R.id.seekbar);
		currentTime = (TextView)view.findViewById(R.id.currentTime);
		allTime = (TextView)view.findViewById(R.id.allTime);
		seekbar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
		collection = (ImageButton)view.findViewById(R.id.collection);	//收藏
		mode = (ImageButton)view.findViewById(R.id.mode);				
		menuList = (ImageButton)view.findViewById(R.id.menuList);
		menuList.setOnClickListener(mOnClickListener);
		mode.setOnClickListener(mOnClickListener);
		collection.setOnClickListener(mOnClickListener);
		collection.setOnLongClickListener(mOnLongClickListener);
		viewPager = (ViewPager)view.findViewById(R.id.viewpager);
		viewPager.setAdapter(new MyAdapter(((FragmentActivity)context).getSupportFragmentManager(), null));
		viewPager.setOnPageChangeListener(mOnPageChangeListener);
		mDelayExecute = new DelayExecute(DELAY, mExecute);
		topBase = (View)view.findViewById(R.id.topBase);
		
		page1 = (View)view.findViewById(R.id.page1);
		page2 = (View)view.findViewById(R.id.page2);
		page3 = (View)view.findViewById(R.id.page3);
		pageIndexArray = new View[]{page1, page2, page3};
		setCurrenPageIndex(0);
		Utils.getInstance().updateViewHToStatusH(topBase);
		initDataShow();
	}  
	
	/**选中过滤*/
	public final float[] BT_SELECTED = new float[] {
			1, 0, 0, 0, -50, 0, 1, 0, 0, -50, 0, 0, 1, 0, -50, 0, 0, 0, 1, 0
	};
	/**
	 * 初始化显示
	 */
	@SuppressLint("NewApi")
	private void initDataShow(){
		mSongInfo = CurrentPlayManager.getInstance().getSongInfo();
		if(musicName == null){
			return;
		}
		if(mSongInfo != null){
			musicName.setText(mSongInfo.getTitle());
			musicSinger.setText(mSongInfo.getSinger());
			initSeekBar();
			loadModeInfo();
			//配置歌词
			Bitmap bitmap = CurrentPlayManager.getInstance().getCurrentMusicImage();
			L.e(tag, "getCurrentMusicImage");
			boolean isDefaultPhoto = false;
			if(bitmap == null){
				//取默认图片
				isDefaultPhoto = true;
			}
			if(bitmap != null){
				Utils.getInstance().startTime("高斯模糊");
				bitmap = Utils.getInstance().blurBitmap(bitmap , 30, 6/*9*/);
				Utils.getInstance().endUseTime("高斯模糊");
//				bitmap = Utils.getInstance().getGrayBitmap(bitmap);
			}
			view.setBackground(new BitmapDrawable(bitmap));
			musicPhoto.clearAnimation();
			/*if(bitmap == null){
				imageLayout.setVisibility(View.GONE);
			}else*/{
				/*imageLayout.setVisibility(View.VISIBLE);*/
				if(isDefaultPhoto){
					shadow.setVisibility(View.GONE);
					musicPhoto.setImageResource(R.drawable.widget_qqmusic_default_album_large);
				}else{
					shadow.setVisibility(View.VISIBLE);
					musicPhoto.setImageBitmap(CurrentPlayManager.getInstance().getCurrentMusicImage());
				}
				startPhotoAnimation();
			}
		}else{
			musicName.setText("未选择歌曲");
			musicSinger.setText("未选择歌曲");
			allTime.setText("00:00");
			currentTime.setText("00:00");
			seekbar.setProgress(0); 
			view.setBackground(null);
			updataPlayState();
			musicPhoto.setImageResource(R.drawable.widget_qqmusic_default_album_large);
		}
	} 
	
	
	Animation operatingAnim;  
	/**
	 * 开始动画
	 */
	private void startPhotoAnimation(){
//		Bitmap bitmap = CurrentPlayManager.getInstance().getCurrentMusicImage();
//		if(musicPhoto != null && bitmap != null){
//			if(operatingAnim == null){
//				operatingAnim = AnimationUtils.loadAnimation(context, R.anim.tip); 
//				LinearInterpolator lin = new LinearInterpolator();   
//				operatingAnim.setInterpolator(lin); 
//				operatingAnim.setFillAfter(true);
//			}
//			musicPhoto.startAnimation(operatingAnim);
//		}
	}
	
	/**
	 * 暂停动画
	 */
	private void pausePhotoAnimation(){
//		musicPhoto.clearAnimation();
	}
	
	/**
	 * 结束动画
	 */
	private void stopPhotoAnimation(){
//		operatingAnim.cancel();
	}
	
	
	/**
	 * 初始化seekbar
	 */
	private void initSeekBar(){
		int duration = AudioPlayerClient.getInstance().getDuration();
		allTime.setText(Utils.getInstance().formatLongToTimeStr(duration));
		seekbar.setMax(duration);
		seekbar.setProgress(AudioPlayerClient.getInstance().getCurrentPosition());
	}
	
	/**
	 * 加载菜单项
	 */
	private void loadModeInfo(){
		int palyMode = CurrentPlayManager.getInstance().getPlayMode();
		switch(palyMode){
		case PlayerCode.PLAY_MODE_ALL_CYCLE:
			mode.setImageResource(R.drawable.player_btn_repeat_xml);
			break;
		case PlayerCode.PLAY_MODE_RANDOM:
			mode.setImageResource(R.drawable.player_btn_random_xml);
			break;
		case PlayerCode.PLAY_MODE_SINGLE_CYCLE:
			mode.setImageResource(R.drawable.player_btn_repeatone_xml);
			break;
		}
		updataPlayState();
	}
	
	/**
	 * 更新播放状态
	 */
	private void updataPlayState(){
		if(playPauseBut == null){
			return;
		}
		if(CurrentPlayManager.getInstance().isPlay()){
			playPauseBut.setImageResource(R.drawable.landscape_player_btn_pause_xml);
			startPhotoAnimation();
		}else{
			playPauseBut.setImageResource(R.drawable.landscape_player_btn_play_xml);
			pausePhotoAnimation();
		}
		
		if(CurrentPlayManager.getInstance().isCollection()){
			collection.setImageResource(R.drawable.add_favor_already_xml);
		}else{
			collection.setImageResource(R.drawable.add_favor_not_already_xml);
		}
	}
	
	
	/**
	 * 播放监听
	 */
	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {   
			switch(v.getId()){
			case R.id.preBut:
				CurrentPlayManager.getInstance().playPreMusic();
				break;
			case R.id.playPauseBut:
				CurrentPlayManager.getInstance().playPause();
				updataPlayState();
				break;
			case R.id.nextBut:
				CurrentPlayManager.getInstance().playNextMusic();
				break;
			case R.id.mode:
				CurrentPlayManager.getInstance().changePlayMode();
				loadModeInfo();
				break;
			case R.id.menuList:
				CurrentPlayManager.getInstance().showCurrentMusicMenu(context);
				break; 
			case R.id.collection:	
				L.e(tag, "收藏！");
				CurrentPlayManager.getInstance().ctrlCollectionCurrentSong(new CollectionCallBack() {
					@Override
					public void ctrlCollectionOK() {
						L.e(tag, "收藏处理执行完，可以更新状态");
						updataPlayState();
					}
				});
				break;
			}
			
		} 
	};
	
	
	private OnLongClickListener mOnLongClickListener = new OnLongClickListener() {
		
		@Override
		public boolean onLongClick(View v) {
			boolean result = false;
			switch(v.getId()){
			case R.id.collection:
				result = true;
				CurrentPlayManager.getInstance().showCollectionMusicMenu(context);
				break;
			}
			return result;
		}
	};
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		L.i(tag, "onDestroyView");
		musicPhoto.clearAnimation();
		ThreadForeverManage.getInstance().cancel(this);
	}
	
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mHostCallBack.connString(HostCallBack.REQUESY_SHOW_CURRENT_PLAYBAR, null);
	}

	/**
	 * DELAY时间执行实体
	 */
	private Execute mExecute = new Execute() {
		@Override
		public void go() {
			hander.sendEmptyMessage(UI_UPDATA_TIME);
		}
	};
	
	
	@Override
	public void doThread() {
		if(mDelayExecute != null){
			mDelayExecute.execute();
		}
	}
	
	/**
	 * ui控制
	 */
	private class UIHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case UI_UPDATA_TIME:
				if(AudioPlayerClient.getInstance().isPlaying()){
					int time = AudioPlayerClient.getInstance().getCurrentPosition();
					currentTime.setText(Utils.getInstance().formatLongToTimeStr(time));
					if(isAllowSetCurrentTime()){
						seekbar.setProgress(time);
					}
				}
				break;
			}
		}
	};
	
    /**
     * 是否允许改变当前时间
     * @return
     */
    private boolean isAllowSetCurrentTime(){
    	return isAllowChangeTime;
    }
    
    /**
     * 设置是否允许改变时间
     * @param isAllowChange
     */
    private void setAllowChangeCurrentTime(boolean isAllowChange){
    	isAllowChangeTime = isAllowChange;
    }
    
    
    /**
     * seekbar进度条监听
     */
    private OnSeekBarChangeListener mOnSeekBarChangeListener = new OnSeekBarChangeListener() {
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			L.i(tag, "onStopTrackingTouch" + seekBar.getProgress());
			setAllowChangeCurrentTime(true);
			AudioPlayerClient.getInstance().seekTo(seekBar.getProgress());
			if(!CurrentPlayManager.getInstance().isPlay()){
				CurrentPlayManager.getInstance().play();
			}
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			L.i(tag, "onStartTrackingTouch");
			setAllowChangeCurrentTime(false);
		}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//			L.i(tag, "onProgressChanged");
		}
	};

	@Override
	public Object notifyInfo(int cmd, Object o) {
		if(this.isHidden()){
			return null;
		}
		switch(cmd){
		case HostCallBack.PLAY_LISTENER_PALY_NEW:
			initDataShow();
			break;
		case HostCallBack.PLAY_LISTENER_PALY_COMPLATE:
			break;
		case HostCallBack.PLAY_LISTENER_PALY_STATE_CH:
			updataPlayState();
			break;
		case PlayerCode.NOTIFY_DEVICE_EJECT:
			initDataShow();
			break;
		case PlayerCode.NOTIFY_DEVICE_MOUNT:
			break;
		case HostCallBack.REQUESY_SHOW_CURRENT_PLAYBAR:
			L.e(tag, "REQUESY_SHOW_CURRENT_PLAYBAR");
			break;
		} 
		return null;
	}
	
	/**
	 * 当前是否用户可见
	 */
	private boolean isVisibleToUser;
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		L.e(tag, "setUserVisibleHint：" + isVisibleToUser);
		this.isVisibleToUser = isVisibleToUser;
	}
	
//当前列表、频谱、歌词界面
	
	private Fragment1 f1;
	private Fragment2 f2;
	private Fragment3 f3;
	    
	public class MyAdapter extends FragmentPagerAdapter {
		String[] _titles;

		public MyAdapter(FragmentManager fm, String[] titles) {
			super(fm);
			_titles = titles;
		}

		@Override
		public CharSequence getPageTitle(int position) { 
			String result = "";
			switch(position){
			case 0:
				result = " ";
				break;
			case 1:
				result = " ";
				break;
			case 2:
				result = " ";
				break;
			}
			return result;
		}

		@Override
		public int getCount() {
			return 3/*_titles.length*/;
		}

		@Override
		public Fragment getItem(int position) {
			Fragment mFragment = null;
			switch (position) {
			case 0:
				if (f3 == null) {
					 Log.e("chenli", "new fragment3");
					 f3 = new Fragment3();
				 }
				 mFragment = f3;
				break;
			case 1:
				if (f2 == null) {
					 Log.e("chenli", "new fragment2");
					 f2 = new Fragment2();
				 }
				 mFragment = f2;
				break; 
			case 2:
				 if (f1 == null) {
					 Log.e("chenli", "new fragment1");
				 	f1 = new Fragment1();
				 }
				 mFragment = f1;
				break;
			}
			return mFragment;
		}
	}
	
	/**
	 * 页面监听
	 */
	private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int page) { 
			//当前选中页面 
			L.e(tag, "onPageSelected : " + page);
			setCurrenPageIndex(page);
		} 
		
		@Override
		public void onPageScrolled(int page, float arg1, int arg2) {
			//页面、 比例、 x位置
//			L.e(tag, "onPageScrolled : " + page + " " + arg1 + " " + arg2);
		}
		
		@Override
		public void onPageScrollStateChanged(int page) {
			//当前改变的页面
//			L.e(tag, "onPageScrollStateChanged : " + page);
		}
	};
	
	/**
	 * 设置当前页面标示
	 */
	private void setCurrenPageIndex(int index){
		if(pageIndexArray == null){
			L.w(tag, "pageIndexArray == null");
			return;
		}
		for(int i = 0; i < pageIndexArray.length; i++){
			//还原
			pageIndexArray[i].setBackgroundColor(Color.GRAY);
		}
		//显示
		pageIndexArray[index].setBackgroundColor(Color.WHITE);
	}
	
}
