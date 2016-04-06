/**
 * 
 */
package com.zhcl.ui.music;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zh.uitls.L;
import com.zhcl.media.CurrentPlayManager;
import com.zhcl.media.MyVisualizer;
import com.zhcl.media.MyVisualizer.IResultListener;
import com.zhcl.service.PlayerCode;
import com.zhcl.ui.widget.PlayFlashView;
import com.zhonghong.zhmedia.R;

/**
 * 频谱
 * @author zhonghong.chenli
 * date:2015-12-18下午2:05:10	<br/>
 */

public class Fragment2 extends BaseFragmentABS {
	private final String tag = Fragment2.class.getSimpleName();;
	Context context;
	View view;
	MyVisualizer visualizer;
	/** 频谱UI */
	PlayFlashView mPlayFlashView;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		L.e(tag, "onCreateView");
		if(visualizer != null){
			visualizer.Start();
		}
		if(view != null){
			ViewGroup parent = (ViewGroup) view.getParent();
			if(parent != null){
				parent.removeView(view);
			}
			return view;
		}
		view = inflater.inflate(R.layout.layout2, container, false);
		context = inflater.getContext();
		initView(view);
		return view;
	}
	
	private void initView(View view){
		mPlayFlashView = (PlayFlashView)view.findViewById(R.id.playFlashView);
		visualizer = new MyVisualizer();
		visualizer.setIResultListener(mIResultListener);
		resetAudioSeesion(CurrentPlayManager.getInstance().getAudioSessionID());
	}
	
	/**
	 * 重置频谱
	 * @param audioSessionID
	 */
	private void resetAudioSeesion(int audioSessionID){
		L.e(tag, "resetAudioSeesion!!!!!!!!!!!!!!");
		visualizer.reset(audioSessionID, false, true, false);
		visualizer.SetCaptureInfo(1024, 32);
		visualizer.Start();
	}
	

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		L.e(tag, "setUserVisibleHint：" + isVisibleToUser);
		if(visualizer != null){
			if(isVisibleToUser){
//				resetAudioSeesion(CurrentPlayManager.getInstance().getAudioSessionID());
//				visualizer.Start();
			}else{
//				visualizer.Stop();
			}
		}
		this.isVisibleToUser = isVisibleToUser;
	}
	
	private boolean isVisibleToUser = false;
	/**
	 * 是否允许实行频谱
	 */
	private boolean isAllowVisualizer(){
		
		return isInLayout() || isVisibleToUser;
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		L.e(tag, "onDestroyView");
		super.onDestroyView();
		visualizer.Stop();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		L.e(tag, "onPause");
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		L.e(tag, "onResume");
	}

	IResultListener mIResultListener = new IResultListener() {
		@Override
		public void Result(int num, String data) {
			String[] strs = data.split(" ");
			mPlayFlashView.updata(strs);
//			Log.e("chenli", "strs = " + strs.length);
//			for (int i = 0; i < strs.length; i++) {
//				Log.e("chenli", "strs = " + strs[i]);
//			}
//			if (strs.length >= 16) {
//				for (int i = 0; i < seekBars.length; i++) {
//					Log.e("chenli", "str = " + strs[i + 1]);
////					seekBars[i].setProgress(Integer.parseInt(strs[i + 1]));
//					L.e(tag, "" + Integer.parseInt(strs[i + 1]));
//				}
//			}
		}
	};

	@Override
	public Object notifyInfo(int cmd, Object o) {
		switch(cmd){
		case HostCallBack.PLAY_LISTENER_PALY_NEW:
			break;
		case HostCallBack.PLAY_LISTENER_PALY_COMPLATE:
			break;
		case HostCallBack.PLAY_LISTENER_PALY_STATE_CH:
			break;
		case PlayerCode.CALL_BACK_PLAY_SESSIONID:
			L.e(tag, "播放sessionID ："  + (String)o);
			if(isAllowVisualizer()){
				L.e(tag, "启动频谱！");
				resetAudioSeesion(Integer.parseInt((String)o));
			}
			break;
			//加这个是因为当父viewpager所在的fragment销毁view时本fragment监听不到
		case HostCallBack.REQUESY_SHOW_CURRENT_PLAYBAR:				//用作和当前播放界面相互切换
			if(visualizer != null){
				visualizer.Stop();
			}
			break;
		case HostCallBack.REQUESY_HIDE_CURRENT_PLAYBAR:
			if(visualizer != null){
				visualizer.Start();
			}
			break;
		case PlayerCode.NOTIFY_DEVICE_EJECT:
			if(visualizer != null && CurrentPlayManager.getInstance().getSongInfo() == null){
				visualizer.Stop();
			}
			break;
		}
		return null;
	}
}
