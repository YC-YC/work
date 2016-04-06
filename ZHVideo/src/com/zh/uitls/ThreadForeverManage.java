/**
 * 
 */
package com.zh.uitls;

import java.util.HashSet;

import android.util.Log;



/**
 * 注册的执行函数中不可有耗时操作 (暂时不用)
 * 一直存在的线程，如果需要执行某函数，请继承ThreadForever并向向此ThreadForeverManage对象注册即可
 * 线程10毫秒执行一次
 * @author zhong.chenli
 */
public class ThreadForeverManage {
	/**
	 * 接口（需要利用线程的函数继承此接口）
	 */
	public interface ThreadForever{
		/**
		 * 线程执行函数（10毫秒执行一次），一定不要在此线程中加入耗时操作
		 */
		public void doThread();
	}
	
	private static final String tag = "ThreadForeverManage";
	private static ThreadForeverManage mThreadForeverManage;
	public static ThreadForeverManage getInstance(){
		if(mThreadForeverManage == null){
			mThreadForeverManage = new ThreadForeverManage();
			Log.i(tag, "常驻线程开启");
		}
		return mThreadForeverManage;
	}
	
	/** ThreadForever接口实现类对象结集合 */
	private HashSet<ThreadForever> mThreadForeverSet = new HashSet<ThreadForever> ();
	
	/**
	 * 注册
	 */
	public void register(ThreadForever threadForever) {
		if (threadForever == null) {
			return;
		}
		Log.e(tag, "注册线程监听事件成功");
		synchronized (mThreadForeverSet) {
			mThreadForeverSet.add(threadForever); // 注册
		}
	}
	  
	/**
	 * 注销监听down事件
	 */
	public void cancel(ThreadForever threadForever) {
		synchronized (mThreadForeverSet) {
			if(mThreadForeverSet.remove(threadForever)){
				Log.e(tag, "注销线程监听事件成功");
			}
		}
	}
	
	/** 构造函数 并开启线程*/
	private ThreadForeverManage(){  
		new Thread(){
			public void run(){
				while(true){
					synchronized (mThreadForeverSet) {
						for(ThreadForever threadForever : mThreadForeverSet){
							threadForever.doThread();			//线程执行函数
						}
					}
					delay(10);			//是毫秒循环一次	
				}
			}  
		}.start();
	}
	
	/**
	 * 延时
	 */
	private void delay(int m){
		try {
			Thread.sleep(m);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
