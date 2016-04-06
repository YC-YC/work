/**
 * 
 */
package com.zh.broadcast;

import java.util.HashSet;

import android.content.Context;

/**
 * @author zhonghong.chenli date:2015-6-12上午9:35:15 <br/>
 */

public class ZuiBroadcast { 
	static final String tag = "ZuiBroadcast";
	HashSet<AbsBroadcast> absBroadcastSet;
	Context context;
	private static ZuiBroadcast mZuiBroadcast;
	MountBroadcast mMountBroadcast;
	MediaButtonBroadcast mMediaButtonBroadcast;

	public static ZuiBroadcast getInstance() {
		return mZuiBroadcast;
	}

	public ZuiBroadcast(Context context) {
		mZuiBroadcast = this;
		this.context = context;
		this.absBroadcastSet = new HashSet<AbsBroadcast>();
		initBroadcastChild(context);
	}

	private void initBroadcastChild(Context context) {
		if(mMountBroadcast == null){
			mMountBroadcast = new MountBroadcast(context);
		}
//		if(mMediaButtonBroadcast == null){
//			mMediaButtonBroadcast = new MediaButtonBroadcast(context);
//		}
	}

	public void concelLogicListner(AbsBroadcast paramAbsBroadcast) {
		synchronized (absBroadcastSet) {
			this.absBroadcastSet.remove(paramAbsBroadcast);
		}
		
	}

	public void registLogicListner(AbsBroadcast paramAbsBroadcast) {
		synchronized (absBroadcastSet) {
			this.absBroadcastSet.add(paramAbsBroadcast);
		}
	}

	public void registerReceiver() {
		synchronized (absBroadcastSet) {
			for (AbsBroadcast bcc : absBroadcastSet) {
				context.registerReceiver(bcc, bcc.getIntentFilter());
			}
		}
	}

	public void unregisterReceiver() {
		synchronized (absBroadcastSet) {
			for (AbsBroadcast bcc : absBroadcastSet) {
				context.unregisterReceiver(bcc);
			}
		}
	}
}
