/**
 * 
 */
package com.zhcl.Adapter;

import java.util.Vector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zh.dao.SongDir;
import com.zhonghong.zhmedia.R;

/**
 * 所有歌曲文件夹列表adapter
 * @author ChenLi
 */
public class AllSongDirListAdapter extends BaseAdapter {
	private static final String tag = "AllSongDirListAdapter";
	Vector<SongDir> allSongDir;
	Context context;
	/**
	 * 得到一个LayoutInfalter对象用来导入布局
	 */
	private LayoutInflater mInflater; 

	public AllSongDirListAdapter(Context context){
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
	}
	
	public void setallSongDir(Vector<SongDir> allSongDir){
		this.allSongDir = allSongDir;
	}
	
	@Override
	public int getCount() {
		if(allSongDir != null){
			return allSongDir.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int index) {
		if(allSongDir != null){
			allSongDir.get(index);
		}
		return null;
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		if(allSongDir == null){
			return convertView;
		}
		  ViewHolder holder;
          if (convertView == null) {
              convertView = mInflater.inflate(R.layout.all_music_list_item, null);
              holder = new ViewHolder();
              /*得到各个控件的对象*/
              holder.title = (TextView) convertView.findViewById(R.id.name);
              holder.info = (TextView) convertView.findViewById(R.id.info);
              convertView.setTag(holder); //绑定ViewHolder对象
          } else {
              holder = (ViewHolder) convertView.getTag(); //取出ViewHolder对象
          }
          SongDir songdir = allSongDir.get(position);
          /*设置TextView显示的内容，即我们存放在动态数组中的数据*/
          holder.title.setText(songdir.getSubName());
          holder.info.setText(songdir.getSongDirSet().size() + "首" + ", " + songdir.getdirPath());
          return convertView;
	}

	
	 /*存放控件 的ViewHolder*/
    public final class ViewHolder {
        public TextView title;
        public TextView info;
    }

}
