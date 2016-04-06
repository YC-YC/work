/**
 * 
 */
package com.zhcl.Adapter;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.zh.dao.Singer;
import com.zh.uitls.L;
import com.zhcl.media.ImageManager;
import com.zhcl.ui.widget.shader.CircularImageView;
import com.zhonghong.zhmedia.R;

/**
 * 所有歌曲列表adapter
 * @author ChenLi
 */
public class AllSingerListAdapter extends BaseAdapter {
	private static final String tag = "allSingerListAdapter";
	Vector<Singer> allSinger;
	Context context;
	Handler handler;
	/**
	 * 得到一个LayoutInfalter对象用来导入布局
	 */
	private LayoutInflater mInflater; 
	private DisplayImageOptions options;

	public AllSingerListAdapter(Context context){
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		handler = new Handler(Looper.getMainLooper());
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.default_avatar) 			//加载中图片
				.showImageForEmptyUri(R.drawable.default_avatar)	   //加载不到图片时
				.showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
	}
	
	public void setallSinger(Vector<Singer> allSinger){
		this.allSinger = allSinger;
	}
	
	@Override
	public int getCount() {
		if(allSinger != null){
			return allSinger.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int index) {
		if(allSinger != null){
			allSinger.get(index);
		}
		return null;
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
//		L.e(tag, "getView = " + position);
		if(allSinger == null){
			return convertView;
		}
		  ViewHolder holder;
          if (convertView == null) {
        	  //L.e(tag, "convertView == null getView = " + position);
              convertView = mInflater.inflate(R.layout.all_singer_list_item, null);
              holder = new ViewHolder();
              /*得到各个控件的对象*/
              holder.image = (CircularImageView)convertView.findViewById(R.id.image);
              holder.title = (TextView) convertView.findViewById(R.id.name);
              holder.info = (TextView) convertView.findViewById(R.id.info);
              convertView.setTag(holder); //绑定ViewHolder对象
          } else {
        	 // L.e(tag, "convertView != null getView = " + position);
              holder = (ViewHolder) convertView.getTag(); //取出ViewHolder对象
          }
          
          Singer singer = allSinger.get(position);
          /*设置TextView显示的内容，即我们存放在动态数组中的数据*/
          holder.title.setText(singer.getmSingerName());
          holder.info.setText(singer.getmSongSet().size() + "首");
          String imagePath = ImageManager.getInstance().loadSingerImage(singer, holder.image, handler);
          if(new File(imagePath).exists()){
        	  ImageLoader.getInstance().displayImage("file:///" + imagePath, holder.image, options, animateFirstListener);
          }else{
        	  holder.image.setImageResource(R.drawable.default_avatar);
          }
          return convertView;
	}

	 /**存放控件 的ViewHolder*/
    public final class ViewHolder {
        public TextView title;
        public TextView info;
        public CircularImageView image;
    }
    
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    
    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}
    
}
