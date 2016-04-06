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
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
import com.zh.uitls.L;
import com.zh.uitls.Utils;
import com.zhcl.dao.VideoInfo;
import com.zhcl.ui.widget.MyGridView;
import com.zhcl.video.ImageManager;
import com.zhonghong.zhvideo.R;

/**
 * 所有歌曲列表adapter
 * @author ChenLi
 */
public class AllVideoListAdapter extends BaseAdapter {
	private static final String tag = "AllVideoListAdapter";
	private final String TAG = getClass().getSimpleName();
	Vector<VideoInfo> allVideo;
	Context context;
	
	/**列表项的布局*/
	private int mListItemId;
	
	/**
	 * 得到一个LayoutInfalter对象用来导入布局
	 */
	private LayoutInflater mInflater; 
	private DisplayImageOptions options;
	Handler handler;
	/**
	 * @param context
	 * @param listItemId 需要要有一个ImageView和两个text
	 */
	public AllVideoListAdapter(Context context, int listItemId){
		this.context = context;
		this.mListItemId = listItemId;
		this.mInflater = LayoutInflater.from(context);
		handler = new Handler(Looper.getMainLooper());
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.danmu_bubble_itembg) 			//加载中图片
		.showImageForEmptyUri(R.drawable.danmu_bubble_itembg)	   //加载不到图片时
		.showImageOnFail(R.drawable.ic_error).cacheInMemory(true)/*.delayBeforeLoading(100)*/
		.cacheOnDisk(true).considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565).build();
	}
	
	public void setAllVideo(Vector<VideoInfo> allVideo){
		this.allVideo = allVideo;
	}
	
	@Override
	public int getCount() {
		if(allVideo != null){
			return allVideo.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int index) {
		if(allVideo != null){
			allVideo.get(index);
		}
		return null;
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		if(allVideo == null){
			Log.i(TAG, "getView allVideo is null");
			return convertView;
		}
		  ViewHolder holder;
          if (convertView == null) {
//              convertView = mInflater.inflate(R.layout.all_video_list_item, null);
              convertView = mInflater.inflate(this.mListItemId, null);
        	  holder = new ViewHolder();
              /*得到各个控件的对象*/
              holder.title = (TextView) convertView.findViewById(R.id.name);
              holder.info = (TextView) convertView.findViewById(R.id.info);
              
              holder.image = (ImageView) convertView.findViewById(R.id.image);
              convertView.setTag(holder); //绑定ViewHolder对象
          } else {
              holder = (ViewHolder) convertView.getTag(); //取出ViewHolder对象
          }
          
          if(viewGroup instanceof MyGridView){
        	  MyGridView mMyGridView = (MyGridView)viewGroup;
        	  if(mMyGridView.isOnMeasure()){
        		  Log.i(TAG, "MyGridView OnMeasure");
        		  return convertView;
        	  }
          }
        	  
    	  VideoInfo videoInfo = allVideo.get(position);
          /*设置TextView显示的内容，即我们存放在动态数组中的数据*/
          holder.title.setText(Utils.getInstance().getSingleLineStr(holder.title, videoInfo.getTitle()));
          //          L.e(tag, "singleLine :" + Utils.getInstance().getSingleLineStr(holder.title, videoInfo.getTitle()));
          //图片可以异步读取
          holder.info.setText(Utils.getInstance().formatLongToTimeStr(videoInfo.getDuration()));
          String imagePath = ImageManager.getInstance().loadVideoInfoImage(videoInfo, holder.image, handler);
//              L.e(tag, "path:" +  "file:///" + imagePath);
          L.e(tag, "getTitle : " + videoInfo.getTitle() + " index :" + position);
          if(new File(imagePath).exists()){
        	  ImageLoader.getInstance().displayImage("file:///" + imagePath, holder.image, options, animateFirstListener);
          }else{
        	  holder.image.setImageResource(R.drawable.danmu_bubble_itembg);
          }
//          Log.i(TAG, "getView convertView width = " + convertView.getWidth());
          return convertView;
	}

	
	 /*存放控件 的ViewHolder*/
    public final class ViewHolder {
        public TextView title;
        public TextView info;
        public ImageView image;
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
