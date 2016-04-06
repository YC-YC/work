/**
 * 
 */
package com.zhcl.Adapter;

import java.util.Vector;

import android.content.Context;
import android.opengl.Visibility;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zh.dao.SongInfo;
import com.zhcl.media.CurrentPlayManager;
import com.zhonghong.zhmedia.R;

/**
 * 所有歌曲列表adapter
 * @author ChenLi
 */
public class AllMusicListAdapter extends BaseAdapter {
	private static final String tag = "AllMusicListAdapter";
	Vector<SongInfo> allMusic;
	Context context;
	/**
	 * 得到一个LayoutInfalter对象用来导入布局
	 */
	private LayoutInflater mInflater; 

	public AllMusicListAdapter(Context context){
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
	}
	
	public void setAllMusic(Vector<SongInfo> allMusic){
		this.allMusic = allMusic;
	}
	
	@Override
	public int getCount() {
		if(allMusic != null){
			return allMusic.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int index) {
		if(allMusic != null){
			allMusic.get(index);
		}
		return null;
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		if(allMusic == null){
			return convertView;
		}
		  ViewHolder holder;
//          Log.v("BaseAdapterTest", "getView " + position + " " + convertView);
          
          if (convertView == null) {
              convertView = mInflater.inflate(R.layout.all_music_list_item, null);
              holder = new ViewHolder();
              /*得到各个控件的对象*/
              holder.title = (TextView) convertView.findViewById(R.id.name);
              holder.singer = (TextView) convertView.findViewById(R.id.info);
              holder.mImageView = (ImageView) convertView.findViewById(R.id.current);
              holder.position = position;
              convertView.setTag(holder); //绑定ViewHolder对象
          } else {
              holder = (ViewHolder) convertView.getTag(); //取出ViewHolder对象
          }
          
          if(allMusic.size() <= position){
        	  return convertView;
          }
          SongInfo songInfo = allMusic.get(position);
          /*设置TextView显示的内容，即我们存放在动态数组中的数据*/
          holder.title.setText(songInfo.getTitle());
          holder.singer.setText(songInfo.getSinger() + "·" + songInfo.getAlbum());
          SongInfo currentPlaySong = CurrentPlayManager.getInstance().getSongInfo();
          if(currentPlaySong != null && currentPlaySong.equals(songInfo)){
        	  holder.mImageView.setVisibility(View.VISIBLE);
          }else{
        	  holder.mImageView.setVisibility(View.INVISIBLE);
          }
          return convertView;
	}

	
	 /*存放控件 的ViewHolder*/
    public final class ViewHolder {
    	int position;
        public TextView title;
        public TextView singer;
        public ImageView mImageView;
    }

    
    public void changeSong(){
    	
    }
}
