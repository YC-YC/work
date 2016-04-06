/**
 * 
 */
package com.zh.broadcast;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;

/**
 * 广播抽象类，所有广播继承此类即可
 * @author zhonghong.chenli
 * date:2015-6-12上午9:25:39	<br/>
 */
abstract class AbsBroadcast extends BroadcastReceiver {
	public AbsBroadcast(){super();}
	abstract IntentFilter getIntentFilter();
}
