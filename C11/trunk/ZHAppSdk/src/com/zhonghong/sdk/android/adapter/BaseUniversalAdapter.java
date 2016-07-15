/**
 * Copyright (c) 深圳市众鸿科技股份有限公司
 * @file_name BaseUniversalAdapter.java
 * @class com.zhonghong.sdk.android.adapter.BaseUniversalAdapter
 * @create 下午7:22:48
 */
package com.zhonghong.sdk.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import java.util.ArrayList;
import java.util.List;

/**
 * 万能适配器（采用泛型）<br/>
 * <p>
 * (更加厉害)  抽取出来的模板适配器，任何适配器继承该适配器，就很容易实现适配器功能。
 * 下午7:22:48
 * 
 * @author ZH-SW-Lilc
 * @version 1.0.0
 */
public abstract class BaseUniversalAdapter<T> extends BaseAdapter {
    protected Context context;
    protected List<T> mList;
    protected LayoutInflater mInflater;
    protected int layoutId;

    /**
     * 通用的Adapter
     *
     * @param context 上下文
     */
    public BaseUniversalAdapter(Context context) {
        this.context = context;
    }

    /**
     * 通用的Adapter
     *
     * @param context  上下文
     * @param layoutId item  布局视图
     */
    public BaseUniversalAdapter(Context context, int layoutId) {
        this.context = context;
        this.layoutId = layoutId;
        mInflater = LayoutInflater.from(context);
    }

    /**
     * 通用的Adapter
     *
     * @param context  上下文
     * @param mList    数据集
     * @param layoutId item  布局视图
     */
    public BaseUniversalAdapter(Context context, List<T> mList, int layoutId) {
        this.context = context;
        this.mList = mList;
        this.layoutId = layoutId;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public T getItem(int position) {
        return mList == null ? null : (mList.size() == 0 ? null : mList
                .get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(context, convertView, parent, layoutId);
        convert(holder, getItem(position), position);
        return holder.getConvertView();
    }

    /**
     * 给适配器设置数据集合
     *
     * @param list
     */
    public synchronized void setListData(List<T> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    /**
     * 获取适配器数据
     *
     * @return
     */
    public synchronized List<T> getListData() {
        return mList;
    }


    /**
     * 移除指定索引index处的数据
     *
     * @param index
     */
    public synchronized void removeData(int index) {
        if (this.mList != null) {
            synchronized (mList) {
                if (index < mList.size()) {
                    this.mList.remove(index);
                    notifyDataSetChanged();
                }
            }
        }
    }

    /**
     * 清空适配器中所有数据
     */
    public synchronized void removeData() {
        if (this.mList != null) {
            synchronized (mList) {
                this.mList.clear();
                notifyDataSetChanged();
            }
        }
    }

    /**
     * 替换指定位置的数据
     *
     * @param index 待设置的列表索引项
     * @param t     项内容
     */
    public synchronized void setData(int index, T t) {
        if (mList != null) {
            if (index < mList.size()) {
                this.mList.set(index, t);
                notifyDataSetChanged();
            }
        }
    }

    /**
     * 在指定位置插入一条数据
     *
     * @param index
     * @param t
     */
    public synchronized void addData(int index, T t) {
        if (mList != null) {
            synchronized (mList) {
                this.mList.add(index, t);
                notifyDataSetChanged();
            }
        }
    }

    /**
     * 在适配器中添加一条数据
     *
     * @param t
     */
    public synchronized void addData(T t) {
        if (this.mList != null) {
            this.mList.add(t);
            notifyDataSetChanged();
        }
    }

    /**
     * 向适配器增加数组对象数据
     *
     * @param list
     */
    public synchronized void addListData(T[] list) {
        List<T> arrayList = new ArrayList<T>(list.length);
        for (T t : list) {
            arrayList.add(t);
        }
        setListData(arrayList);
    }

    /**
     * 向适配器增加集合对象数据
     *
     * @param list
     */
    public synchronized void addListData(List<T> list) {
        if (mList != null) {
            synchronized (mList) {
                mList.addAll(list);
                notifyDataSetChanged();
            }
        }
    }


    public abstract void convert(ViewHolder holder, T t, int position);
}
