/**
 * 
 */
package com.zhonghong.mediascanner.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.util.Log;

/**
 * @author YC
 * @time 2016-12-20 上午9:14:44
 * TODO:次数统计管理类
 */
public class CountManager {

	
	private static final String TAG = "CountManager";
	private HashMap<String , Integer> mCounts = new HashMap<String , Integer>();
	
	public synchronized void push(String name){
		int count = 1;
		Iterator<Entry<String, Integer>> iterator = mCounts.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, Integer> next = iterator.next();
			if (next.getKey().equals(name)){
				count = next.getValue() + 1;
				break;
			}
		}
		Log.i(TAG, String.format("push name[%s] has count = [%d]", name, count));
		mCounts.put(name, count);
	}
	
	public synchronized void pop(String name){
		int count = 0;
		Iterator<Entry<String, Integer>> iterator = mCounts.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, Integer> next = iterator.next();
			if (next.getKey().equals(name)){
				count = next.getValue() - 1;
				break;
			}
		}
		if (count >= 0){
			mCounts.put(name, count);
		}
		Log.i(TAG, String.format("pop name[%s] has count = [%d]", name, count));
		if (count <= 0){
			mCounts.remove(name);
		}
	}
	
	public synchronized boolean isValid(String name){
		Iterator<Entry<String, Integer>> iterator = mCounts.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, Integer> next = iterator.next();
			if (next.getKey().equals(name)){
				Log.i(TAG, String.format("isValid name[%s] has count = [%d]", name, next.getValue()));
				if (next.getValue() > 0){
					return true;
				}
				else{
					return false;
				}
			}
		}
		return false;
	}

}
