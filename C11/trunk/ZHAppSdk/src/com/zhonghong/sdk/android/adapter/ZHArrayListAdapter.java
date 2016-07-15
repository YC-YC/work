/**
 * Copyright (c) 深圳市众鸿科技股份有限公司
 * @file_name BaseArrayListAdapter.java
 * @class com.zhonghong.sdk.android.adapter.BaseArrayListAdapter
 * @create 下午7:22:48
 */
package com.zhonghong.sdk.android.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

/**
 * 万能列表、网格视图适配器
 * <p>万能适配器（采用泛型）继承此类即可简单实现适配器</p>
 * 下午7:22:48
 * 
 * @author ZH-SW-Lilc
 * @version 1.0.0
 */
public abstract class ZHArrayListAdapter<T> extends BaseAdapter {
	private ArrayList<T> mList = new ArrayList<T>();
	protected Context mContext;
	protected ListView mListView;

	public ZHArrayListAdapter(Context context) {
		this.mContext = context;
	}

	/**
	 * 获取列表总长度
	 * @return 长度
	 */
	@Override
	public int getCount() {
		if (mList != null)
			return mList.size();
		else
			return 0;
	}

	/**
	 * 获取列表item对象
	 * @param 位置
	 * @return T对象
	 */
	@Override
	public T getItem(int position) {
		return mList == null ? null : (mList.size() == 0 ? null : mList
				.get(position));
	}

	/**
	 * 获取列表itemId
	 * @param 位置
	 * @return ID
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * 获得在数据集中的指定位置上显示数据的视图
	 * @param position 位置
	 * @param convertView 缓存视图
	 * @param parent 父类视图层
	 * @return 对应于指定位置的数据的视图
	 */
	@Override
	public abstract View getView(int position, View convertView,
			ViewGroup parent);

	/**
	 * 添加集合数据到列表
	 * @param list 集合数据
	 */
	public synchronized void setList(ArrayList<T> list) {
		this.mList = list;
		notifyDataSetChanged();
	}

	/**
	 * 添加对象数据到列表
	 * @param t 对象数据
	 */
	public synchronized void setT(T t) {
		if (this.mList != null) {
			this.mList.add(t);
			notifyDataSetChanged();
		}
	}

	/**
	 * 追加集合数据到列表
	 * @param list 集合数据
	 */
	public synchronized void addListT(ArrayList<T> list) {
		synchronized (mList) {
			if (mList != null) {
				mList.addAll(list);
				notifyDataSetChanged();
			}
		}
	}

	/**
	 * 清除列表中指定数据
	 * @param index 位置
	 */
	public synchronized void removeT(int index) {
		synchronized (mList) {
			if (this.mList != null) {
				this.mList.remove(index);
				notifyDataSetChanged();
			}
		}
	}

	/**
	 * 清除列表中所有数据
	 */
	public synchronized void removeT() {
		synchronized (mList) {
			if (this.mList != null) {
				this.mList.clear();
				notifyDataSetChanged();
			}
		}
	}

	/**
	 * 修改index位置的值为t
	 * 
	 * @param index
	 *            待设置的列表索引项
	 * @param t
	 *            项内容
	 */
	public synchronized void setT(int index, T t) {
		if (mList != null) {
			if (index < mList.size()) {
				this.mList.set(index, t);
				notifyDataSetChanged();
			}
		}
	}

	/**
	 * 添加index位置的值为t
	 * 
	 * @param index
	 *            待设置的列表索引项
	 * @param t
	 *            项内容
	 */
	public synchronized void addT(int index, T t) {
		synchronized (mList) {
			if (mList != null) {
				this.mList.add(index, t);
				notifyDataSetChanged();
			}
		}
	}

	/**
	 * 获取当前集合数据
	 * @return 集合数据
	 */
	public ArrayList<T> getList() {
		return mList;
	}

	/**
	 * 添加对象数组数据到列表
	 */
	public synchronized void setList(T[] list) {
		ArrayList<T> arrayList = new ArrayList<T>(list.length);
		for (T t : list) {
			arrayList.add(t);
		}
		setList(arrayList);
	}

	/**
	 * 获取当前ListView视图
	 * @return ListView视图
	 */
	public ListView getListView() {
		return mListView;
	}

	/**
	 * 放置listView视图
	 * @param listView对象
	 */
	public void setListView(ListView listView) {
		mListView = listView;
	}
}
