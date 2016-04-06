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
import android.graphics.BitmapFactory;
import android.os.Handler;
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
import com.zh.dao.Album;
import com.zhcl.media.ImageManager;
import com.zhcl.ui.widget.shader.RoundedImageView;
import com.zhonghong.zhmedia.R;

/**
 * 所有歌曲列表adapter
 * @author ChenLi
 */
public class AllAlbumListAdapter extends BaseAdapter {
	private static final String tag = "AllAlbumListAdapter";
	Vector<Album> allAlbum;
	Context context;
	Handler handler;
	/**
	 * 得到一个LayoutInfalter对象用来导入布局
	 */
	private LayoutInflater mInflater; 
	private DisplayImageOptions options;
	public AllAlbumListAdapter(Context context){
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		handler = new Handler();
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.default_album_small)
				.showImageForEmptyUri(R.drawable.default_album_small)
				.showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
	}
	
	public void setallAlbum(Vector<Album> allAlbum){
		this.allAlbum = allAlbum;
	}
	
	@Override
	public int getCount() {
		if(allAlbum != null){
			return allAlbum.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int index) {
		if(allAlbum != null){
			allAlbum.get(index);
		}
		return null;
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		if(allAlbum == null){
			return convertView;
		}
		  ViewHolder holder;
          if (convertView == null) {
              convertView = mInflater.inflate(R.layout.all_album_list_item, null);
              holder = new ViewHolder();
              /*得到各个控件的对象*/
              holder.image = (RoundedImageView)convertView.findViewById(R.id.image);
              holder.title = (TextView) convertView.findViewById(R.id.name);
              holder.info = (TextView) convertView.findViewById(R.id.info);
              convertView.setTag(holder); //绑定ViewHolder对象
          } else {
              holder = (ViewHolder) convertView.getTag(); //取出ViewHolder对象
          }
          
          Album album = allAlbum.get(position);
          /*设置TextView显示的内容，即我们存放在动态数组中的数据*/
          holder.title.setText(album.getmAlbumName());
          holder.info.setText(album.getmSongSet().size() + "首");
          String imagePath = ImageManager.getInstance().loadAlbumImage(album, holder.image, handler);
          if(new File(imagePath).exists()){
        	  ImageLoader.getInstance().displayImage("file:///" + imagePath, holder.image, options, animateFirstListener);
          }else{ 
        	  holder.image.setImageResource(R.drawable.default_album_small);
          } 
          return convertView;     
	} 

	 /**存放控件 的ViewHolder*/ 
    public final class ViewHolder { 
        public TextView title;
        public TextView info;
        public RoundedImageView image;
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
