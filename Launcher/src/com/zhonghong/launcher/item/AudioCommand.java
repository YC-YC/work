/**
 * 
 */
package com.zhonghong.launcher.item;

import android.content.Context;

import com.zhonghong.utils.Utils;

/**
 * @author YC
 * @time 2016-4-11 上午9:57:49
 */
public class AudioCommand implements Command {

	private boolean mFlag = false;
	
	@Override
	public boolean execute(Context context) {
//		if (mFlag)
		{
			mFlag = false;
			return Utils.startOtherActivity(context, Utils.ZH_AUDIO_PKG, Utils.ZH_AUDIO_CLZ);
		}
//		else
//		{
//			mFlag = true;
//			return Utils.startOtherActivity(context, Utils.ZH_AUDIO2_PKG, Utils.ZH_AUDIO2_CLZ);
//		}
	}

}
