/**
 * 
 */
package com.zhonghong.menuitem;

import android.content.Context;

import com.zhonghong.utils.Utils;

/**
 * @author YC
 * @time 2016-4-11 上午9:57:49
 */
public class AppstoreCommand implements Command {

	@Override
	public boolean execute(Context context) {
		return Utils.startOtherActivity(context, Utils.ZH_APPSTORE_PKG, Utils.ZH_APPSTORE_CLZ);
	}

}
