package com.zh.uitls;

import android.os.SystemClock;

/**
 * 延时执行控制类
 * @author zhonghong.chenli
 * date:2015-12-17下午4:17:53	<br/>
 */
public class DelayExecute {

	public interface Execute{
		public void go();
	}
	/** 最后执行时间*/
	private long lastTime;
	private Execute mExecute;
	private int delay;
	public DelayExecute(int delay, Execute mExecute){
		if(delay < 0 || mExecute == null){
			throw new IllegalAccessError("DelayExecute  初始化异常， 参数不正确， delay = " + delay + " mExecute = " + mExecute);
		}
		this.mExecute  = mExecute;
		this.delay = delay;
	}
	
	/**
	 * 外部线程中调用执行
	 */
	public void execute(){
		long currentTime = SystemClock.uptimeMillis();
		if(currentTime - lastTime > this.delay){
			if(this.mExecute != null){
				this.mExecute.go();
			}
			lastTime = currentTime;
		}
	}
}
