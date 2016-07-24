/**
 * 
 */
package com.yc.external;

import java.util.HashMap;

import com.zhonghong.data.GlobalData;
import com.zhonghong.utils.UpdateUiManager;

import android.util.Log;

/**
 * @author YC
 * @time 2016-7-15 下午3:12:41
 * TODO:处理音乐Post过来的信息
 */
public class PostFromMusic implements IPostFromClient {

	private static final String TAG = "PostFromMusic";

	@Override
	public boolean postInfo(int cmd, String val) {
		switch (cmd) {
		case Cmd.POST_MUSIC_INFO:
			Log.i(TAG, "PostFromMusic处理音乐信息：" + val);
			if (val != null){
				HashMap<String, String> map = new JsonHelper().json2Map(val);
				if (map != null){
					GlobalData.Music.TITLE = map.get("title");
					GlobalData.Music.ARTISE = map.get("artist");
					GlobalData.Music.CUR_PLAY_PATH = map.get("playpath");
					GlobalData.MediaWidgetType = GlobalData.MEDIA_WIDGET_TYPE_MUSIC;
					UpdateUiManager.getInstances().callUpdate(UpdateUiManager.CMD_UPDATE_MUSIC_INFO);
				}
			}
			return true;
		}
		return false;
	}
	
	public PostFromMusic(){
		ConnManager.getInstance().register(this);
	}
	
	@Override
	protected void finalize() throws Throwable {
		ConnManager.getInstance().unregister(this);
		super.finalize();
	}

}
