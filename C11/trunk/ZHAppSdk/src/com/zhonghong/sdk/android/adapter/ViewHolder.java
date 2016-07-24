/**
 * Copyright (c) 深圳市众鸿科技股份有限公司
 * @file_name ViewHolder.java
 * @class com.zhonghong.sdk.android.adapter.ViewHolder
 * @create 下午7:22:48
 */
package com.zhonghong.sdk.android.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * ViewHolder
 * <p>描述：用ViewHolder，主要是进行一些性能优化，减少一些不必要的重复操作</p>
*  下午7:22:48
 * 
 * @author ZH-SW-Lilc
 * @version 1.0.0
 */
public class ViewHolder {
    private SparseArray<View> mViews;
    private View mConvertView;
    private Context mContext;

    /**
     * 初始化ViewHolder
     *
     * @param context  上下文
     * @param parent   父视图
     * @param layoutId Item layout
     */
    public ViewHolder(Context context, ViewGroup parent, int layoutId) {
        this.mContext = context;
        this.mViews = new SparseArray<View>();
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        //设置Tag
        mConvertView.setTag(this);
    }

    /**
     * 获得到ViewHolder
     *
     * @param context     上下文
     * @param convertView convertView
     * @param parent      父视图
     * @param layoutId    Item layout
     * @return 返回ViewHolder对象
     */
    public static ViewHolder get(Context context, View convertView, ViewGroup parent, int layoutId) {
        if (convertView == null) {
            return new ViewHolder(context, parent, layoutId);
        } else {
            return (ViewHolder) convertView.getTag();
        }
    }

    /**
     * 获得到控件
     *
     * @param viewId item layout 中控件的id
     * @param <T>    范型
     * @return 范型View
     */

    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 获得到convertView
     *
     * @return convertView
     */
    public View getConvertView() {
        return mConvertView;
    }

    /**
     * 设置TextView的文本
     *
     * @param viewId item layout 中TextView的id
     * @param text   文本内容
     * @return ViewHolder
     */
    public ViewHolder setText(int viewId, String text) {
        TextView textView = getView(viewId);
        textView.setText(text);
        return this;
    }

    /**
     * 设置TextView的文本颜色
     *
     * @param viewId item layout 中TextView的id
     * @param color   颜色值
     * @return ViewHolder
     */
    public ViewHolder setTextColor(int viewId, int color) {
        TextView textView = getView(viewId);
        textView.setTextColor(color);
        return this;
    }
    
    /**
     * 通过url设置ImageView 的图片
     * 这里可以修改为自己的图片加载库
     *
     * @param viewId item layout 中ImageView的id
     * @param url    图片的url
     * @return ViewHolder
     */
    public ViewHolder setImageUrl(int viewId, String url) {
        ImageView imageView = getView(viewId);
        //这里可以修改为自己的图片加载库
        //Ion.with(mContext).load(url).intoImageView(imageView);
        if (TextUtils.isEmpty(url)) return this;
        //用fackbook图片加载库
        /*if (imageView instanceof SimpleDraweeView) {
            Uri uri = Uri.parse(url);
            ((SimpleDraweeView) imageView).setImageURI(uri);
        }*/
        return this;
    }

    /**
     * 通过ResourceId设置ImageView 的图片
     *
     * @param viewId     item layout 中ImageView的id
     * @param resourceId 图片资源文件的id
     * @return ViewHolder
     */
    public ViewHolder setImageResource(int viewId, int resourceId) {
        ImageView imageView = getView(viewId);
        imageView.setImageResource(resourceId);
        return this;
    }

    /**
     * 通过bitmap 设置ImageView 的图片
     *
     * @param viewId item layout 中ImageView的id
     * @param bitmap bitmap
     * @return ViewHolder
     */
    public ViewHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView imageView = getView(viewId);
        imageView.setImageBitmap(bitmap);
        return this;
    }
    
    
    /**
     * 通过BackgroundResource 设置ImageView 的图片
     *
     * @param viewId item layout 中ImageView的id
     * @param resourceId  图片资源文件的id
     * @return ViewHolder
     */
    public ViewHolder setBackgroundResource(int viewId, int resourceId) {
        ImageView imageView = getView(viewId);
        imageView.setBackgroundResource(resourceId);
        return this;
    }
    
    
    /**
     * 通过Drawable 设置ImageView 的图片
     *
     * @param viewId item layout 中ImageView的id
     * @param drawable drawable
     * @return ViewHolder
     */
    public ViewHolder setImageDrawable(int viewId, Drawable drawable) {
        ImageView imageView = getView(viewId);
        imageView.setImageDrawable(drawable);
        return this;
    }

    /**
     * 设置View隐藏Gone
     *
     * @param viewId
     * @return
     */
    public ViewHolder setViewGone(int viewId) {
        getView(viewId).setVisibility(View.GONE);
        return this;
    }

    /**
     * 设置View隐藏Invisible
     *
     * @param viewId
     * @return
     */
    public ViewHolder setViewInvisible(int viewId) {
        getView(viewId).setVisibility(View.INVISIBLE);
        return this;
    }

    /**
     * 设置View显示Visible
     *
     * @param viewId
     * @return
     */
    public ViewHolder setViewVisible(int viewId) {
        getView(viewId).setVisibility(View.VISIBLE);
        return this;
    }


    /**
     * ==============下边可以写自己的控件的实现，参考上边的ImageView================
     */


	/**
	 * 设置图片的动画效果
	 * 
	 * @param viewId item layout 中ImageView的id
	 * @param animation 动画效果
	 * @return ViewHolder
	 */
	public ViewHolder setImageAnimation(int viewId, Animation animation) {
		ImageView imageView = getView(viewId);
		imageView.setAnimation(animation);
		return this;
	}
	
	/**
	 * 取消图片动画效果 
	 * @param viewId item layout 中ImageView的id
	 * @return ViewHolder
	 */
	public ViewHolder clearAnimation(int viewId) {
		ImageView imageView = getView(viewId);
		imageView.clearAnimation();
		return this;
	}
}
