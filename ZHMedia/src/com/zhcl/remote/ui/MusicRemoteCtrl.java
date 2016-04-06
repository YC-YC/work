/**
 * 
 */
package com.zhcl.remote.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.zh.uitls.DelayExecute;
import com.zh.uitls.L;
import com.zh.uitls.ThreadForeverManage;
import com.zh.uitls.DelayExecute.Execute;
import com.zh.uitls.ThreadForeverManage.ThreadForever;
import com.zhcl.media.CurrentPlayManager;
import com.zhonghong.zhmedia.LocalMusicActivity;

/**
 * 小部件控制对象
 * @author chen
 */
public class MusicRemoteCtrl implements IRemote {
	private static final String tag = MusicRemoteCtrl.class.getSimpleName();
	private static MusicRemoteCtrl mMusicRemoteCtrl;
	private MusicRemoteCtrl(){}
	public static MusicRemoteCtrl getInstance(){
		if(mMusicRemoteCtrl == null){
			mMusicRemoteCtrl = new MusicRemoteCtrl();
		}
		return mMusicRemoteCtrl;
	}
	
	Context context;
	/** 主线程handler */
	private Handler handler;
	/** 后台线程 */
	private BackThread mBackThread = new BackThread();
	/** 线程刷新频率控制对象 */
	private DelayExecute mDelayExecute;
	/** 更新频率 */
	private static final int DELAY = 1000;
	/** 更新时间消息 */
	private static final int HANDLER_UI_UPDATA_TIME = 1;
	/**
	 * 初始化
	 * @param context
	 */
	public void init(Context context){
		if(this.context != null){
			L.w(tag, "已经初始化");
			return;
		}
		this.context = context;
		mDelayExecute = new DelayExecute(DELAY, mExecute);
		
	}
	
	/** 当前是否存在小部件可用 */
	private boolean isHasWidgetEnable = false;
	
	@Override
	public void requestRemoteCtrl(int cmd) {
		switch(cmd){
		case IRemote.MUSIC_PLAYER_PRE:
			remotePre();
			break;
		case IRemote.MUSIC_PLAYER_NEXT:
			remoteNext();
			break;
		case IRemote.MUSIC_PLAYER_PLAY_PAUSE:
			remotePlayPause();
			break;
		case IRemote.MUSIC_WIDGET_ENABLE:		//有小部件可用
			setWidgetEnable();
			break;
		case IRemote.MUSIC_WIDGET_DISENABLE:	//所有小部件均不可用
			setWidgetDisEnable();
			break;
		case IRemote.PAGE_ENTER_DEFAULT_PAGE:	//回到音乐
			enterPage("main");
			break;
		case IRemote.PAGE_ENTER_LIST_PAGE:		//进入列表界面
			enterPage("list");
			break;
		}
	}
	
	/**
	 * 进入界面
	 */
	private void enterPage(String page){
		Intent intent = new Intent(context, LocalMusicActivity.class);
        intent.putExtra("page", page);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        context.startActivity(intent);
	}
	
	/**
	 * 操作前处理
	 */
	private boolean before(){
		return true;
	}

	/**
	 * 远程控制播放/暂停
	 */
	private void remotePlayPause(){
		before();
		CurrentPlayManager.getInstance().playPause();
	}
	
	/**
	 * 远程下一曲
	 */
	private void remoteNext(){
		before();
		CurrentPlayManager.getInstance().playNextMusic();
	}
	
	/**
	 * 远程上一曲
	 */
	private void remotePre(){
		before();
		CurrentPlayManager.getInstance().playPreMusic();
	}
	
	@Override
	public void requestUpdateRemoteUI(Context context){
//		L.e(tag, "requestUpdateRemoteUI");
		Intent updateIntent = new Intent(MusicWidget.UPDATE_MUSIC);		//如果数值有变化则发送更新广播
		context.sendBroadcast(updateIntent);
	}
	
	
	
	
	
	
//-------------------请求更新时间控制	
	/**
	 * 获得主线程handler
	 * @return
	 */
	private Handler getHandler(){
		if(handler == null){
			handler = new Handler(Looper.getMainLooper()){
				@Override
				public void handleMessage(Message msg) {
					switch(msg.what){
					case HANDLER_UI_UPDATA_TIME:
						//请求更新UI
						requestUpdateRemoteUI(context);
						break;
					}
				}
			};
		}
		return handler;
	}
	
	/**
	 * 小部件可用
	 */
	private void setWidgetEnable(){
		L.e(tag, "widget可用！");
		if(isHasWidgetEnable){
			return;
		}
		isHasWidgetEnable = true;
		ThreadForeverManage.getInstance().register(mBackThread);
	}
	
	/**
	 * 小部件不可用
	 */
	private void setWidgetDisEnable(){
		L.e(tag, "widget不可用！");
		isHasWidgetEnable = false;
		ThreadForeverManage.getInstance().cancel(mBackThread);
	}
	
	/**
	 * 请求更新当前播放时间
	 */
	
	class BackThread implements ThreadForever{
		@Override
		public void doThread() {
			if(mDelayExecute != null){
				mDelayExecute.execute();
			}
		}
	}
	
	/**
	 * DELAY时间执行实体
	 */
	private Execute mExecute = new Execute() {
		@Override
		public void go() {
			getHandler().sendEmptyMessage(HANDLER_UI_UPDATA_TIME);
		}
	};
}
