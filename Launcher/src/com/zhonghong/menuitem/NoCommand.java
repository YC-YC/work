/**
 * 
 */
package com.zhonghong.menuitem;

import android.content.Context;

/**
 * @author YC
 * @time 2016-4-11 上午9:57:49
 */
public class NoCommand implements Command {

	@Override
	public boolean execute(Context context) {
		return false;
	}

}
