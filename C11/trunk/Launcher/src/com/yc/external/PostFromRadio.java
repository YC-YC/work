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
 * TODO:处理收音机Post过来的信息
 */
public class PostFromRadio implements IPostFromClient {

	private static final String TAG = "PostFromRadio";

	@Override
	public boolean postInfo(int cmd, String val) {
		switch (cmd) {
		case Cmd.POST_RADIO_INFO:
			Log.i(TAG, "ExternalRadio处理收音机信息：" + val);
			if (val != null){
				HashMap<String, String> map = new JsonHelper().json2Map(val);
				if (map != null){
					GlobalData.Radio.TITLE = map.get("title");
					GlobalData.Radio.CUR_FREQ = map.get("curfreq");
					UpdateUiManager.getInstances().callUpdate(UpdateUiManager.CMD_UPDATE_RADIO_INFO);
				}
			}
			return true;
		}
		return false;
	}
	
	public PostFromRadio(){
		ConnManager.getInstance().register(this);
	}
	
	@Override
	protected void finalize() throws Throwable {
		ConnManager.getInstance().unregister(this);
		super.finalize();
	}

}
