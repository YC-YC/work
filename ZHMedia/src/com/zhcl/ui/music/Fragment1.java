/**
 * 
 */
package com.zhcl.ui.music;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zh.dao.SongInfo;
import com.zh.uitls.DelayExecute;
import com.zh.uitls.DelayExecute.Execute;
import com.zh.uitls.L;
import com.zh.uitls.ThreadForeverManage;
import com.zh.uitls.ThreadForeverManage.ThreadForever;
import com.zh.uitls.Utils;
import com.zhcl.media.CurrentPlayManager;
import com.zhcl.service.PlayerCode;
import com.zhcl.service.service.client.AudioPlayerClient;
import com.zhcl.ui.widget.LrcView;
import com.zhonghong.zhmedia.R;

/**
 * 歌词
 * @author zhonghong.chenli
 * date:2015-12-18下午2:05:25	<br/>
 */
public class Fragment1 extends BaseFragmentABS implements ThreadForever{
	private final String tag = Fragment1.class.getSimpleName();
	Context context;
	View view;
	private LrcView mLrcView;
	/** 多长时间执行一次 */
	private final int DELAY = 500;
	/** 指定延时时间执行 */
	private DelayExecute mDelayExecute;
	/** 更新时间 */
	private final int UI_UPDATA_TIME = 1;
	UIHandler hander;
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ThreadForeverManage.getInstance().register(this);	//注册自己
		if(view != null){
			ViewGroup parent = (ViewGroup) view.getParent();
			if(parent != null){
				parent.removeView(view);
			}
			return view;
		}
		hander = new UIHandler();
		view = inflater.inflate(R.layout.layout1, container, false);
		context = inflater.getContext();
		initView(view);
		return view;
	}
	
	
	
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ThreadForeverManage.getInstance().cancel(this);
	}




	private void initView(View view){
		mLrcView = (LrcView)view.findViewById(R.id.lrc);
		loadLrc(CurrentPlayManager.getInstance().getSongInfo());
		mDelayExecute = new DelayExecute(DELAY, mExecute);
	}
	
	
	/**
	 * 加载歌词
	 */
	private boolean loadLrc(SongInfo mSongInfo){
		String lrcPath = null;
		try {
			lrcPath = Utils.getInstance().serchLrc(mSongInfo);
			return mLrcView.setLrcPath(lrcPath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			L.e(tag, "lrcPath : " + lrcPath);
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 刷新同步歌词
	 */
	private void updataLrc(int currentTime){
		mLrcView.loadTime(currentTime);
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
					updataLrc(time);
				}
				break;
			}
		}
	};
	
	
	@Override
	public Object notifyInfo(int cmd, Object o) {
		switch(cmd){
		case HostCallBack.PLAY_LISTENER_PALY_NEW:
			loadLrc(CurrentPlayManager.getInstance().getSongInfo());
			break;
		case HostCallBack.PLAY_LISTENER_PALY_COMPLATE:
			break;
		case HostCallBack.PLAY_LISTENER_PALY_STATE_CH:
			break;
		case PlayerCode.CLL_BACK_LRC_COMPLETE:		//歌词下载完成
			SongInfo songInfo = CurrentPlayManager.getInstance().getSongInfo();
			if(songInfo != null && o != null){
				String newLrc = (String)o;
				if(newLrc.equals(Utils.getInstance().serchLrc(songInfo))){
					loadLrc(songInfo);
				}
			}
			
			break;
		}
		return null;
	}

	
	/**
	 * DELAY时间执行实体
	 */
	private Execute mExecute = new Execute() {
		@Override
		public void go(){
			hander.sendEmptyMessage(UI_UPDATA_TIME);
		}
	};
	
	@Override
	public void doThread() {
		if(mDelayExecute != null){
			mDelayExecute.execute();
		}
	}
}
