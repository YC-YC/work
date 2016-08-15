/**
 * 
 */
package com.yc.external;

import java.util.HashMap;

import android.util.Log;

import com.zhonghong.data.GlobalData;
import com.zhonghong.utils.UpdateUiManager;

/**
 * @author YC
 * @time 2016-7-15 下午3:12:41
 * TODO:处理音乐Post过来的信息
 */
public class PostFromBtMusic implements IPostFromClient {

	private static final String TAG = "PostFromBtMusic";

	@Override
	public boolean postInfo(int cmd, String val) {
		switch (cmd) {
		case Cmd.POST_BTMUSIC_INFO:
			if (val != null){
				HashMap<String, String> map = new JsonHelper().json2Map(val);
				if (map != null){
					GlobalData.BtMusic.TITLE = map.get("title");
					UpdateUiManager.getInstances().callUpdate(UpdateUiManager.CMD_UPDATE_BTMUSIC_INFO);
				}
			}
			return true;
		}
		return false;
	}
	
	public PostFromBtMusic(){
		ConnManager.getInstance().register(this);
	}
	
	@Override
	protected void finalize() throws Throwable {
		ConnManager.getInstance().unregister(this);
		super.finalize();
	}

}
