/**
 * 
 */
package com.zh.broadcast;

import com.zh.uitls.L;
import com.zhcl.media.CurrentPlayManager;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.KeyEvent;

/**
 * @author zhonghong.chenli
 * date:2015-11-30上午10:28:44	<br/>
 */
public class MediaButtonBroadcast extends AbsBroadcast {
	private static final String tag = "MediaButtonBroadcast";
	
	public MediaButtonBroadcast(){
		 
	}
	
	Context context;
	public MediaButtonBroadcast(Context context){
		this.context = context;
	}
	@Override
	IntentFilter getIntentFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_MEDIA_BUTTON);
		return null;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		L.e(tag, "action :" + action);
		if(!Intent.ACTION_MEDIA_BUTTON.equals(action)){
			L.w(tag, "return");
			return;
		}
		KeyEvent event = (KeyEvent)intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
		int key = event.getKeyCode(); 
		int actionCount = event.getAction();
		L.i(tag, "key = " + key);
		L.i(tag, "actionCount = " + actionCount);
		if(actionCount != 1){
			L.w(tag, "非单击不处理");
			return;
		}
		if(!CurrentPlayManager.getInstance().isHaveAudioFocus()){	//是否存在焦点
			L.w(tag, "无音频焦点，不处理");
			return;
		}
		switch(key){
		case KeyEvent.KEYCODE_MEDIA_NEXT:
			CurrentPlayManager.getInstance().playNextMusic();
			break;
		case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
			CurrentPlayManager.getInstance().playPreMusic();
			break;
		case KeyEvent.KEYCODE_MEDIA_PLAY:
			CurrentPlayManager.getInstance().play();
			break;
		case KeyEvent.KEYCODE_MEDIA_PAUSE:
			CurrentPlayManager.getInstance().pause();
			break;
		case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
			CurrentPlayManager.getInstance().playPause();
			break;
		case KeyEvent.KEYCODE_MEDIA_STOP:
			CurrentPlayManager.getInstance().stop();
			break;
		}
	} 
	
	

}
